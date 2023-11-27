package com.techelevator.tebucks.controllers;

import com.techelevator.tebucks.dao.JdbcAccountDao;
import com.techelevator.tebucks.dao.JdbcTransferDao;
import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class TeBucksController {
    private final UserDao userDao;
    private final JdbcAccountDao accountDao;
    private final JdbcTransferDao transferDao;

    public TeBucksController(UserDao userDao, JdbcAccountDao jdbcAccountDao, JdbcTransferDao jdbcTransferDao) {
        this.userDao = userDao;
        this.accountDao = jdbcAccountDao;
        this.transferDao = jdbcTransferDao;
    }

    @GetMapping("/account/balance")
    public Account getBalance(Principal principal) {
        User loggedInUser = userDao.getUserByUsername(principal.getName());
        Account loggedInAccount = accountDao.getAccountByUserId(loggedInUser.getId());
        if (loggedInAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The logged in user can't be found.");
        }
        return loggedInAccount;
    }

    @GetMapping("/account/transfers")
    public List<Transfer> listTransfers(Principal principal) {
        User loggedInUser = userDao.getUserByUsername(principal.getName());
        List<Transfer> loggedInUserTransfers = transferDao.getTransfersByUserId(loggedInUser.getId());
        if (loggedInUserTransfers == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The logged in user can't be found.");
        }
        return loggedInUserTransfers;
    }

    @GetMapping("/transfers/{id}")
    public Transfer getTransfer(@PathVariable int id, Principal principal) {
        Transfer transfer = transferDao.getTransferById(id);
        User loggedInUser = userDao.getUserByUsername(principal.getName());
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This transfer does not exist.");
        }
        if (transfer.getUserTo().getId() != loggedInUser.getId() &&
                transfer.getUserFrom().getId() != loggedInUser.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot see a transfer that you aren't involved in.");
        }
        return transfer;
    }

    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping("/transfers")
    public Transfer createTransfer(@Valid @RequestBody NewTransferDto newTransferDto,
                                   Principal principal) {
        Transfer newTransfer = createTransferFromDto(newTransferDto);
        User loggedInUser = userDao.getUserByUsername(principal.getName());
        if (loggedInUser.getId() != newTransferDto.getUserFromId() &&
                loggedInUser.getId() != newTransferDto.getUserToId() ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to create a transfer that you aren't involved in.");
        }
        if (newTransferDto.getTransferType().equals(TransferTypeEnum.REQUEST.getValue())) {
            return transferDao.createTransfer(newTransferDto);
        }
        if (accountDao.isValidTransfer(newTransfer)) {
            Transfer createdTransfer = transferDao.createTransfer(newTransferDto);
            if (createdTransfer == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create transfer.");
            }
            accountDao.executeValidatedTransfer(createdTransfer);
            return createdTransfer;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transfers/{id}/status")
    public Transfer updateTransferStatus(@Valid @RequestBody TransferStatusUpdateDto transferStatusUpdateDto,
                                         @PathVariable int id, Principal principal) {
        Transfer transferToUpdate = transferDao.getTransferById(id);
        User loggedInUser = userDao.getUserByUsername(principal.getName());
        if (loggedInUser.getId() != transferToUpdate.getUserFrom().getId() &&
                loggedInUser.getId() != transferToUpdate.getUserTo().getId() ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update a transfer that you aren't involved in.");
        }
        if (transferStatusUpdateDto.getTransferStatus().equals(TransferStatusEnum.REJECTED.getValue())) {
            return transferDao.updateTransferStatus(id, transferStatusUpdateDto);
        }
        if (accountDao.isValidTransfer(transferToUpdate)) {
            Transfer updatedTransfer = transferDao.updateTransferStatus(id, transferStatusUpdateDto);
            accountDao.executeValidatedTransfer(updatedTransfer);
            return updatedTransfer;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users")
    public List<User> listUsers(Principal principal) {
        List<User> users = userDao.getUsers();
        User loggedInUser = userDao.getUserByUsername(principal.getName());
        users.remove(loggedInUser);
        return users;
    }

    private Transfer createTransferFromDto(NewTransferDto newTransferDto) {
        Transfer transfer = new Transfer();
        transfer.setTransferStatus(TransferStatusEnum.PENDING.getValue());
        if (newTransferDto.getTransferType().equals(TransferTypeEnum.SEND.getValue())) {
            transfer.setTransferStatus(TransferStatusEnum.APPROVED.getValue());
        }
        transfer.setUserFrom(userDao.getUserById(newTransferDto.getUserFromId()));
        transfer.setUserTo(userDao.getUserById(newTransferDto.getUserToId()));
        transfer.setAmount(newTransferDto.getAmount());
        transfer.setTransferType(newTransferDto.getTransferType());
        return transfer;
    }
}