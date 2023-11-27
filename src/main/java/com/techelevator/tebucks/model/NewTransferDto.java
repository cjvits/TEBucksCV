package com.techelevator.tebucks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NewTransferDto {

    private static final List<String> TRANSFER_TYPES = Arrays.asList("Send", "Request");

    @Positive
    @JsonProperty("userFrom")
    private int userFromId;
    @JsonProperty("userTo")
    @Positive
    private int userToId;
    @Positive
    private double amount;
    @NotBlank
    private String transferType;

    @AssertTrue
    private boolean isTransferTypeCorrect() {
        return TRANSFER_TYPES.contains(transferType);
    }

    @AssertTrue
    private boolean isUserFromNotSameAsUserTo() {
        return userToId != userFromId;
    }
    public NewTransferDto(int userFromId, int userToId, double amount, String transferType) {
        this.userFromId = userFromId;
        this.userToId = userToId;
        this.amount = amount;
        this.transferType = transferType;
    }

    public NewTransferDto() {
    }

    public int getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(int userFromId) {
        this.userFromId = userFromId;
    }

    public int getUserToId() {
        return userToId;
    }

    public void setUserToId(int userToId) {
        this.userToId = userToId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewTransferDto that = (NewTransferDto) o;
        return userFromId == that.userFromId && userToId == that.userToId && Double.compare(that.amount, amount) == 0 && Objects.equals(transferType, that.transferType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userFromId, userToId, amount, transferType);
    }
}
