package com.techelevator.tebucks.model;

import com.techelevator.tebucks.security.model.User;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Transfer {

    private static final List<String> TRANSFER_TYPES = Arrays.asList("Send", "Request");

    private static final List<String> TRANSFER_STATUSES = Arrays.asList("Pending", "Approved", "Rejected");

    @NotBlank
    private int transferId;
    @NotBlank
    private String transferType;
    @NotBlank
    private String transferStatus;
    @NotNull
    private User userFrom;
    @NotNull
    private User userTo;
    @Positive
    private double amount;

    @AssertTrue
    private boolean isTransferTypeCorrect() {
        return TRANSFER_TYPES.contains(transferType);
    }

    @AssertTrue
    private boolean isTransferStatusCorrect() {
        return TRANSFER_STATUSES.contains(transferStatus);
    }

    @AssertTrue
    private boolean isUserFromNotSameAsUserTo() {
        return userTo.getId() != userFrom.getId();
    }
    public Transfer() {}

    public Transfer(int transferId, String transferType, String transferStatus, User userTo, User userFrom, double amount) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
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
        Transfer transfer = (Transfer) o;
        return transferId == transfer.transferId && Double.compare(transfer.amount, amount) == 0 && Objects.equals(transferType, transfer.transferType) && Objects.equals(transferStatus, transfer.transferStatus) && Objects.equals(userFrom, transfer.userFrom) && Objects.equals(userTo, transfer.userTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, transferType, transferStatus, userFrom, userTo, amount);
    }
}
