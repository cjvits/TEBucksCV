package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

public class JdbcAccountDaoTests extends BaseDaoTests {
    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void test_get_account_by_user_Id_happy_path() {
        Account retrievedAccount1 =  sut.getAccountByUserId(ACCOUNT_1.getUserId());
        Account retrievedAccount2 =  sut.getAccountByUserId(ACCOUNT_2.getUserId());
        Account retrievedAccount3 =  sut.getAccountByUserId(ACCOUNT_3.getUserId());
        Assert.assertEquals(ACCOUNT_1, retrievedAccount1);
        Assert.assertEquals(ACCOUNT_2, retrievedAccount2);
        Assert.assertEquals(ACCOUNT_3, retrievedAccount3);
    }

    @Test
    public void getAccountByUserId_returns_null_with_invalid_id() {
        Account retrievedAccount4 = sut.getAccountByUserId(4);
        Assert.assertNull(retrievedAccount4);
    }

    @Test
    public void createNewAccount_creates_account_with_correct_starting_balance() {
        Account retrievedAccount = sut.createNewAccount(4);
        Assert.assertEquals(CREATED_ACCOUNT_4, retrievedAccount);
    }

    @Test(expected = ResponseStatusException.class)
    public void createNewAccount_throwsResponseStatusException_with_invalid_id() {
        sut.createNewAccount(5);
    }

    @Test
    public void isValidTransfer_returns_true_for_valid_transfer() {
        Assert.assertTrue(sut.isValidTransfer(TRANSFER_1));
    }

    @Test(expected = ResponseStatusException.class)
    public void isValidTransfer_throws_exception_if_either_user_cant_be_found() {
        sut.isValidTransfer(INVALID_USER_TRANSFER_5);
    }

    @Test(expected = ResponseStatusException.class)
    public void isValidTransfer_throws_exception_if_overdraft_will_occur() {
        sut.isValidTransfer(OVERDRAFT_TRANSFER_6);
    }

    @Test
    public void isValidTransfer_returns_true_if_account_balance_is_emptied_out() {
        Assert.assertTrue(sut.isValidTransfer(TRANSFER_TOTAL_BALANCE));
    }

    @Test
    public void executeTransfer_works_given_send_transfer() {
        sut.executeValidatedTransfer(TRANSFER_1);
        double expectedBalanceForUser1 = ACCOUNT_1.getBalance() + TRANSFER_1.getAmount();
        double expectedBalanceForUser2 = ACCOUNT_2.getBalance() - TRANSFER_1.getAmount();
        Assert.assertEquals(expectedBalanceForUser1, sut.getAccountById(1).getBalance(), 0.01);
        Assert.assertEquals(expectedBalanceForUser2, sut.getAccountById(2).getBalance(), 0.01);
    }

    @Test
    public void executeTransfer_works_given_request_transfer() {
        sut.executeValidatedTransfer(TRANSFER_3);
        double expectedBalanceForUser1 = ACCOUNT_1.getBalance() - TRANSFER_3.getAmount();
        double expectedBalanceForUser3 = ACCOUNT_3.getBalance() + TRANSFER_3.getAmount();
        Assert.assertEquals(expectedBalanceForUser1, sut.getAccountById(1).getBalance(), 0.01);
        Assert.assertEquals(expectedBalanceForUser3, sut.getAccountById(3).getBalance(), 0.01);
    }


}
