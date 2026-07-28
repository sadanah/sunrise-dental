package com.sunrisedentalclinic.service.impl;

public class EmailGateway {
    public void sendEmail(String recipient, String message) {
        System.out.println("[EMAIL to " + recipient + "]: " + message);
        // real implementation later
    }
}