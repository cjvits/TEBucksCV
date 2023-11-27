package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TxLogDto;
import com.techelevator.tebucks.services.TEARSService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JdbcAccountDao {
    public static final double STARTING_BALANCE = 1_000;
    private final JdbcTemplate jdbcTemplate;

    private final TEARSService tearsService;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        tearsService = new TEARSService();
    }

    public Account createNewAccount(int userId) {
        String sql = "INSERT INTO account (user_id, balance) VALUES (?, ?) RETURNING account_id";
        try {
            Integer newAccountId = jdbcTemplate.queryForObject(sql, int.class, userId, STARTING_BALANCE);
            if (newAccountId == null) {
                throw new DaoException("Failed to create new account");
            }
            return getAccountById(newAccountId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The userId or balance provided were invalid");
        }
    }

    public Account getAccountByUserId(int userId) {
        String sql = "select * from account where user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                return mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    public Account getAccountById(int id) {
        String sql = "select * from account where account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                return mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    public boolean isValidTransfer(Transfer transfer) {
        int userSendingId = transfer.getUserFrom().getId();
        int userReceivingId = transfer.getUserTo().getId();
        Account sendingAccount = getAccountByUserId(userSendingId);
        Account receivingAccount = getAccountByUserId(userReceivingId);
        if (sendingAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The userTo does not exist.");
        }
        if (receivingAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The userFrom does not exist.");
        }
        double userSendingBalance = sendingAccount.getBalance();
        userSendingBalance -= transfer.getAmount();
        if (userSendingBalance < 0) {
            TxLogDto newTxLogDto = new TxLogDto("Overdraft " + transfer.getTransferType() + " transfer attempted",
                    transfer.getUserFrom().getUsername(), transfer.getUserTo().getUsername(), transfer.getAmount());

            tearsService.addToLog(newTxLogDto, tearsService.getToken());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    public void executeValidatedTransfer(Transfer transfer) {
        if (transfer.getAmount() >= 1_000) {

            TxLogDto newTxLogDto = new TxLogDto(transfer.getTransferType() + " transfer executed",
                    transfer.getUserFrom().getUsername(), transfer.getUserTo().getUsername(), transfer.getAmount());

            tearsService.addToLog(newTxLogDto, tearsService.getToken());
        }
        int userSendingId = transfer.getUserFrom().getId();
        int userReceivingId = transfer.getUserTo().getId();
        double userSendingBalance = getAccountByUserId(userSendingId).getBalance();
        double userReceivingBalance = getAccountByUserId(userReceivingId).getBalance();
        userSendingBalance -= transfer.getAmount();
        userReceivingBalance += transfer.getAmount();
        String sql = "update account set balance = ? where user_id = ?";
        jdbcTemplate.update(sql, userSendingBalance, userSendingId);
        jdbcTemplate.update(sql, userReceivingBalance, userReceivingId);
    }

    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getDouble("balance"));
        return account;
    }

}
