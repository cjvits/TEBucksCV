package com.techelevator.tebucks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TxLogDto {

    private String description;

    @JsonProperty("username_from")
    private String usernameFrom;

    @JsonProperty("username_to")
    private String usernameTo;

    private double amount;

    public TxLogDto(String description, String usernameFrom, String usernameTo, double amount) {
        this.description = description;
        this.usernameFrom = usernameFrom;
        this.usernameTo = usernameTo;
        this.amount = amount;
    }

    public TxLogDto() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxLogDto txLogDTO = (TxLogDto) o;
        return Double.compare(txLogDTO.amount, amount) == 0 && Objects.equals(description, txLogDTO.description) && Objects.equals(usernameFrom, txLogDTO.usernameFrom) && Objects.equals(usernameTo, txLogDTO.usernameTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, usernameFrom, usernameTo, amount);
    }
}
