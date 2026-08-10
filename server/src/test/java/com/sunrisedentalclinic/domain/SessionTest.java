package com.sunrisedentalclinic.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void isValid_returnsTrueBeforeExpiry() {
        Session session = new Session("S1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        assertTrue(session.isValid());
    }

    @Test
    void isValid_returnsFalseAfterExpiry() {
        Session session = new Session("S1", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        assertFalse(session.isValid());
    }
}
