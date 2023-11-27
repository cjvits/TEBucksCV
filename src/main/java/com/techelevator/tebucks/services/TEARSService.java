package com.techelevator.tebucks.services;


import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.*;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

public class TEARSService {

    private static final String API_BASE_URL = "https://tears.azurewebsites.net";

    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getToken() {
        LoginUser loginUser = new LoginUser("caleb", "test123");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginUser> entity = new HttpEntity<>(loginUser, headers);

        ReturnUser returnUser = new ReturnUser();

        try {
            ResponseEntity<ReturnUser> response = restTemplate.exchange(API_BASE_URL + "/login", HttpMethod.POST,
                    entity, ReturnUser.class);
             returnUser = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
        }
        return returnUser.getToken();
    }

    public TxLog[] getLog() {
        TxLog[] txLogs = null;

        try {
            ResponseEntity<TxLog[]> response = restTemplate.exchange(API_BASE_URL + "/api/TxLog", HttpMethod.GET,
                                                                    makeAuthEntity(), TxLog[].class);
            txLogs = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
        }
        return txLogs;
    }

    public TxLog addToLog(TxLogDto newTxLogDto, String authToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<TxLogDto> entity = new HttpEntity<>(newTxLogDto, headers);

        String url = API_BASE_URL + "/api/TxLog";

        try {
            ResponseEntity<TxLog> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, TxLog.class);
            return responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The transfer log was invalid.");
        }

    }

    public TxLog getTxLogById(int logId) {
        TxLog txLog = null;
        try {
            ResponseEntity<TxLog> response =
                    restTemplate.exchange(API_BASE_URL + "/api/TxLog/" + logId,
                            HttpMethod.GET, makeAuthEntity(), TxLog.class);
            txLog = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new DaoException("Unable to find Log with that ID", e);
        }
        return txLog;
    }

    public boolean updateTxLog(TxLog updatedTxLog) {
        boolean success = false;
        try {
            restTemplate.put(API_BASE_URL + "/api/TxLog/" + updatedTxLog.getLogId(),
                    makeTxLogEntity(updatedTxLog));
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
        }
        return success;
    }

    public boolean deleteTxLog(int logId) {
        boolean success = false;
        if (logId > 0) {
            try {
                restTemplate.exchange(API_BASE_URL + "/api/TxLog/" + logId, HttpMethod.DELETE, makeAuthEntity(), Void.class);
                success = true;
            } catch (RestClientResponseException | ResourceAccessException e) {
                throw new DaoException("Unable to conenct to the server", e);
            }
        }
        else {
            throw new DaoException("Invalid Log Id");
            }

        return success;
    }

    public HttpEntity<TxLog> makeTxLogEntity(TxLog txLog) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(txLog, headers);
    }

    public HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }




}
