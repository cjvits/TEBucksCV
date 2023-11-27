package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.security.model.User;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestingDatabaseConfig.class)
public abstract class BaseDaoTests {
    public static final User USER_1 = new User(1, "user1", "user1", "ROLE_USER", true);
    public static final User USER_2 = new User(2, "user2", "user2", "ROLE_USER", true);
    public static final User USER_3 = new User(3, "user3", "user3", "ROLE_USER", true);
    public static final User INVALID_USER_100 = new User(100, "user4", "user4", "ROLE_USER", true);
    public static final User INVALID_USER_101 = new User(101, "user4", "user4", "ROLE_USER", true);
    public static final Account ACCOUNT_1 = new Account(1,1,1000);
    public static final Account ACCOUNT_2 = new Account(2,2,2000);
    public static final Account ACCOUNT_3 = new Account(3,3,3000);
    public static final Account CREATED_ACCOUNT_4 = new Account(4,4, JdbcAccountDao.STARTING_BALANCE);
    public static final Transfer TRANSFER_1 = new Transfer(1, TransferTypeEnum.SEND.getValue(), TransferStatusEnum.APPROVED.getValue(), USER_1, USER_2, 200);
    public static final Transfer TRANSFER_2 = new Transfer(2, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.PENDING.getValue(), USER_2, USER_3, 400);
    public static final Transfer UPDATED_TRANSFER_2 = new Transfer(2, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.REJECTED.getValue(), USER_2, USER_3, 400);
    public static final Transfer TRANSFER_3 = new Transfer(3, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.APPROVED.getValue(), USER_3, USER_1, 600);
    public static final Transfer CREATED_TRANSFER_4 = new Transfer(4, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.PENDING.getValue(), USER_2, USER_1, 400);
    public static final Transfer INVALID_USER_TRANSFER_5 = new Transfer(5, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.APPROVED.getValue(), INVALID_USER_100, INVALID_USER_101, 600);
    public static final Transfer OVERDRAFT_TRANSFER_6 = new Transfer(6, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.APPROVED.getValue(), USER_1, USER_2, ACCOUNT_2.getBalance() + 1);
    public static final Transfer TRANSFER_TOTAL_BALANCE = new Transfer(6, TransferTypeEnum.REQUEST.getValue(), TransferStatusEnum.APPROVED.getValue(), USER_1, USER_2, ACCOUNT_2.getBalance());
    public static final NewTransferDto NEW_TRANSFER_DTO_1 = new NewTransferDto(1, 2, 400, TransferTypeEnum.REQUEST.getValue()); // 4
    public static final NewTransferDto NEW_TRANSFER_DTO_2 = new NewTransferDto(2, 3, 400, TransferTypeEnum.SEND.getValue()); // 4
    public static final TransferStatusUpdateDto TRANSFER_STATUS_UPDATE_DTO_REJECTED = new TransferStatusUpdateDto(TransferStatusEnum.REJECTED.getValue());

    @Autowired
    protected DataSource dataSource;

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.queryForRowSet("select setval('account_account_id_seq', (select max(account_id) from account))");
    }

}
