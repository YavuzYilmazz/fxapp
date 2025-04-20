package com.yavuz.fxapp.dto;

public class ConversionResponse {
    private String transactionId;
    private double convertedAmount;

    public ConversionResponse(String transactionId, double convertedAmount) {
        this.transactionId = transactionId;
        this.convertedAmount = convertedAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }
}
