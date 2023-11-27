package com.techelevator.tebucks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TxLog {

    private String description;

    @JsonProperty("username_from")
    private String usernameFrom;

    @JsonProperty("username_to")
    private String usernameTo;

    private double amount;

    @JsonProperty("log_id")
    private int logId;

    @JsonProperty("createdDate")
    private String isoDateTime;


    public TxLog(String description, String usernameFrom, String usernameTo, double amount, int logId, String isoDateTime) {
        this.description = description;
        this.usernameFrom = usernameFrom;
        this.usernameTo = usernameTo;
        this.amount = amount;
        this.logId = logId;
        this.isoDateTime = isoDateTime;
    }

    public TxLog() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsernameFrom() {
        return usernameFrom;
    }

    public void setUsernameFrom(String usernameFrom) {
        this.usernameFrom = usernameFrom;
    }

    public String getUsernameTo() {
        return usernameTo;
    }

    public void setUsernameTo(String usernameTo) {
        this.usernameTo = usernameTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getIsoDateTime() {
        return isoDateTime;
    }

    public void setIsoDateTime(String isoDateTime) {
        this.isoDateTime = isoDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxLog txLog = (TxLog) o;
        return Double.compare(txLog.amount, amount) == 0 && logId == txLog.logId && Objects.equals(description, txLog.description) && Objects.equals(usernameFrom, txLog.usernameFrom) && Objects.equals(usernameTo, txLog.usernameTo) && Objects.equals(isoDateTime, txLog.isoDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, usernameFrom, usernameTo, amount, logId, isoDateTime);
    }
}
