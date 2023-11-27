package com.techelevator.tebucks.model;

public enum TransferStatusEnum {

    APPROVED("Approved"),
    PENDING("Pending"),
    REJECTED("Rejected");

    private final String value;

    TransferStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
