package com.techelevator.tebucks.model;

public enum TransferTypeEnum {
    SEND("Send"),
    REQUEST("Request");

    private final String value;

    TransferTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
