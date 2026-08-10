package com.sunrisedentalclinic.service.impl;

public class SmsGateway {
    public void sendSms(String recipient, String message) {
        System.out.println("[SMS to " + recipient + "]: " + message);
        // real implementation later
    }
}