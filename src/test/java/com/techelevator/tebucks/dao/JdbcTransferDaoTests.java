package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {
    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcAccountDao jdbcAccountDao = new JdbcAccountDao(jdbcTemplate);
        UserDao userDao = new JdbcUserDao(jdbcTemplate, jdbcAccountDao);
        sut = new JdbcTransferDao(jdbcTemplate, userDao);
    }
    @Test
    public void getTransferById_works_with_existing_id() {
        Transfer retrievedTransfer1 =  sut.getTransferById(1);
        Transfer retrievedTransfer2 =  sut.getTransferById(2);
        Transfer retrievedTransfer3 =  sut.getTransferById(3);

        Assert.assertEquals(TRANSFER_1, retrievedTransfer1);
        Assert.assertEquals(TRANSFER_2, retrievedTransfer2);
        Assert.assertEquals(TRANSFER_3, retrievedTransfer3);
    }

    @Test
    public void getTransferById_returns_null_with_nonexistent_id() {
        Transfer retrievedTransfer =  sut.getTransferById(4);
        Assert.assertNull(retrievedTransfer);
    }

    @Test
    public void listTransfersByUserId_returns_correctly_for_Users_1_2_3() {
        List<Transfer> transfersForUserIdOne = sut.getTransfersByUserId(1);
        List<Transfer> transfersForUserIdTwo = sut.getTransfersByUserId(2);
        List<Transfer> transfersForUserIdThree =  sut.getTransfersByUserId(3);

        Assert.assertEquals(2, transfersForUserIdOne.size());
        Assert.assertEquals(TRANSFER_1, transfersForUserIdOne.get(0));
        Assert.assertEquals(TRANSFER_3, transfersForUserIdOne.get(1));

        Assert.assertEquals(2, transfersForUserIdTwo.size());
        Assert.assertEquals(TRANSFER_1, transfersForUserIdTwo.get(0));
        Assert.assertEquals(TRANSFER_2, transfersForUserIdTwo.get(1));

        Assert.assertEquals(2, transfersForUserIdThree.size());
        Assert.assertEquals(TRANSFER_2, transfersForUserIdThree.get(0));
        Assert.assertEquals(TRANSFER_3, transfersForUserIdThree.get(1));
    }

    @Test
    public void listTransfersByUserId_returns_empty_list_for_id_not_in_db() {
        List<Transfer> transfers = sut.getTransfersByUserId(5);
        Assert.assertEquals(0, transfers.size());
    }

    @Test
    public void createTransfer_createsTransfer_for_request_dto_obj() {
        Transfer newlyCreatedTransfer = sut.createTransfer(NEW_TRANSFER_DTO_1);
        Assert.assertEquals(CREATED_TRANSFER_4, newlyCreatedTransfer);
    }

    @Test
    public void updateTransferStatus_works_given_correct_rejected_message() {
        Transfer updatedTransfer =  sut.updateTransferStatus(2, TRANSFER_STATUS_UPDATE_DTO_REJECTED);;
        Assert.assertEquals(UPDATED_TRANSFER_2, updatedTransfer);
    }

    @Test(expected = DaoException.class)
    public void updateTransferStatus_throws_exception_with_invalid_status_code() {
        sut.updateTransferStatus(2, new TransferStatusUpdateDto("Blah"));;
    }

}
