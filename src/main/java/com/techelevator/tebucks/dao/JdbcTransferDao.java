package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.security.dao.UserDao;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

    public List<Transfer> getTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfer " +
                "where user_to = ? or user_from = ? " +
                "order by transfer_id asc;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (rowSet.next()) {
                transfers.add(mapRowToTransfer(rowSet));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    public Transfer getTransferById(int id) {
        String sql = "select * from transfer where transfer_id = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
            if (rowSet.next()) {
                return mapRowToTransfer(rowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    public Transfer createTransfer(NewTransferDto newTransferDto) {
        String transferStatus = TransferStatusEnum.PENDING.getValue();
        if (newTransferDto.getTransferType().equals(TransferTypeEnum.SEND.getValue())) {
            transferStatus = TransferStatusEnum.APPROVED.getValue();
        }
        String sql = "insert into transfer(transfer_type, user_from, user_to, amount, transfer_status) " +
                "values (?, ?, ?, ?, ?) returning transfer_id";
        try {
            Integer newTransferId = jdbcTemplate.queryForObject(sql, int.class,
                    newTransferDto.getTransferType(),
                    newTransferDto.getUserFromId(),
                    newTransferDto.getUserToId(),
                    newTransferDto.getAmount(),
                    transferStatus);
            return getTransferById(newTransferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    public Transfer updateTransferStatus(int id, TransferStatusUpdateDto transferStatusUpdateDto) {
        String sql = "update transfer set transfer_status = ? where transfer_id = ?;";
        try {
            jdbcTemplate.update(sql, transferStatusUpdateDto.getTransferStatus(), id);
            return getTransferById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("The status message sent is invalid", e);
        }

    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferStatus(rs.getString("transfer_status"));
        transfer.setTransferType(rs.getString("transfer_type"));
        transfer.setAmount(rs.getDouble("amount"));
        transfer.setUserTo(userDao.getUserById(rs.getInt("user_to")));
        transfer.setUserFrom(userDao.getUserById(rs.getInt("user_from")));
        return transfer;
    }
}