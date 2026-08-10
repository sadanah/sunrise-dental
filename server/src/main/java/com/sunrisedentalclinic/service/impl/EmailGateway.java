package com.sunrisedentalclinic.service.impl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailGateway {

    private static final String HOST = System.getProperty("mail.host", "sandbox.smtp.mailtrap.io");
    private static final String PORT = System.getProperty("mail.port", "2525");
    private static final String USERNAME = System.getProperty("mail.username", "");
    private static final String PASSWORD = System.getProperty("mail.password", "");
    private static final String FROM_ADDRESS = System.getProperty("mail.from", "no-reply@sunrisedentalclinic.com");

    public void sendEmail(String recipient, String message) {
        if (recipient == null || recipient.isBlank()) {
            System.out.println("[EMAIL SKIPPED] No email address on file - message not sent: " + message);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(FROM_ADDRESS));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject("Sunrise Dental Clinic Notification");
            mimeMessage.setContent(buildHtml(message), "text/html; charset=utf-8");

            Transport.send(mimeMessage);
            System.out.println("[EMAIL SENT] to " + recipient);
        } catch (Exception e) {
            System.out.println("[EMAIL FAILED] to " + recipient + ": " + e.getMessage());
        }
    }

    private String buildHtml(String message) {
        return "<!DOCTYPE html>"
                + "<html><body style=\"margin:0;padding:0;background-color:#F8F9FD;font-family:Arial,Helvetica,sans-serif;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#F8F9FD;padding:32px 0;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"480\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#FFFFFF;border-radius:14px;overflow:hidden;box-shadow:0 1px 3px rgba(44,61,143,0.08);\">"
                + "<tr><td style=\"background-color:#2C3D8F;padding:24px 32px;\">"
                + "<span style=\"color:#FFFFFF;font-size:18px;font-weight:600;\">Sunrise Dental Clinic</span>"
                + "</td></tr>"
                + "<tr><td style=\"padding:32px;\">"
                + "<p style=\"margin:0 0 16px;color:#1F1F1F;font-size:15px;line-height:1.6;\">" + escapeHtml(message) + "</p>"
                + "<p style=\"margin:24px 0 0;color:#6A6E83;font-size:13px;\">If you have any questions, please contact the clinic directly.</p>"
                + "</td></tr>"
                + "<tr><td style=\"background-color:#F2F5FF;padding:16px 32px;\">"
                + "<span style=\"color:#6A6E83;font-size:12px;\">This is an automated message from Sunrise Dental Clinic. Please do not reply to this email.</span>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body></html>";
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}