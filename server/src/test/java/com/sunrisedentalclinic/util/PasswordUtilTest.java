package com.sunrisedentalclinic.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hash_samePasswordTwice_producesSameHash() {
        String h1 = PasswordUtil.hash("pwd123");
        String h2 = PasswordUtil.hash("pwd123");
        assertEquals(h1, h2);
    }

    @Test
    void hash_differentPasswords_produceDifferentHashes() {
        String h1 = PasswordUtil.hash("pwd123");
        String h2 = PasswordUtil.hash("differentPassword");
        assertNotEquals(h1, h2);
    }

    @Test
    void hash_neverReturnsPlaintext() {
        String hash = PasswordUtil.hash("pwd123");
        assertNotEquals("pwd123", hash);
    }

    @Test
    void hash_producesExpectedSha256Length() {
        // SHA-256 hex output is always 64 characters
        String hash = PasswordUtil.hash("anything");
        assertEquals(64, hash.length());
    }
}