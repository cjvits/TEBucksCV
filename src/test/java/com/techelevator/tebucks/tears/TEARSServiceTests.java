package com.techelevator.tebucks.tears;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.TxLog;
import com.techelevator.tebucks.model.TxLogDto;
import com.techelevator.tebucks.services.TEARSService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;
public class TEARSServiceTests {

    private final TEARSService sut = new TEARSService();

    private final String authToken = sut.getToken();

    TxLogDto testTxLogDto = new TxLogDto("Request-Test", "User_1", "User_2", 2000.00);

    TxLog updatedTestTxLog = new TxLog("Send-Test", "User_2", "User_3", 2000.00, 912, "2023-11-03T13:56:47.35");

    int txLogId = 0;


    @Before
    public void setup() {
        sut.setAuthToken(authToken);
    }

    @Test
    public void getsLog_happy_path() {
        TxLog[] testAllTxLogs = sut.getLog();
        Assert.assertNotNull(testAllTxLogs);
        Assert.assertTrue(testAllTxLogs[0].getLogId() > 1);
    }

    @Test
    public void addsTxLog_happy_path() {
        TxLog txLog = sut.addToLog(testTxLogDto, authToken);
        Assert.assertNotNull(txLog);
        Assert.assertEquals(txLog.getDescription(), testTxLogDto.getDescription());
        txLogId = txLog.getLogId();
    }

    @Test (expected = ResponseStatusException.class)
    public void addsTxLog_TxLog_incomplete_log() {
        TxLogDto emptyTxLogDto = new TxLogDto();
        TxLog txLog = sut.addToLog(emptyTxLogDto, authToken);
    }

    @Test (expected = ResponseStatusException.class)
    public void addsTxLog_TxLog_null() {
        TxLogDto emptyTxLogDto = null;
        TxLog txLog = sut.addToLog(emptyTxLogDto, authToken);
    }

    @Test
    public void getsLogById_happy_path() {
        TxLog testAllTxLog = sut.getTxLogById(1000);
        Assert.assertNotNull(testAllTxLog);
    }

    @Test (expected = DaoException.class)
    public void getsLogById_ID_not_in_database() {
        TxLog testAllTxLog = sut.getTxLogById(500);
        Assert.assertNotNull(testAllTxLog);
    }

    @Test
    public void updatesLog_happy_path() {
        Assert.assertTrue(sut.updateTxLog(updatedTestTxLog));
        Assert.assertEquals(sut.getTxLogById(1000).getDescription(), updatedTestTxLog.getDescription());


    }

    @Test
    public void deletesLog_happy_path() {
        Assert.assertTrue(sut.deleteTxLog(910));


    }

    @Test  (expected = DaoException.class)
    public void deletesLog_invalid_ID() {
        Assert.assertTrue(sut.deleteTxLog(-5));

    }


}
