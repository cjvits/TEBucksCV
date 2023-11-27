package com.techelevator.tebucks.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class TransferStatusUpdateDto {

    private static final List<String> TRANSFER_STATUSES = Arrays.asList("Pending", "Approved", "Rejected");
    @NotBlank
    private String transferStatus;
    @AssertTrue
    private boolean isTransferStatusCorrect() {
        return TRANSFER_STATUSES.contains(transferStatus);
    }
    public TransferStatusUpdateDto() {}

    public TransferStatusUpdateDto(String transferStatus) {
        this.transferStatus = transferStatus;
    }
    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferStatusUpdateDto that = (TransferStatusUpdateDto) o;
        return Objects.equals(transferStatus, that.transferStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferStatus);
    }
}
