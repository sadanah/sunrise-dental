package com.sunrisedentalclinic.service;

import org.junit.jupiter.api.Test;
import com.sunrisedentalclinic.service.impl.HelpService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpServiceTest {

    private final HelpService helpService = new HelpService();

    @Test
    void displayHelp_knownTopic_returnsContent() {
        String result = helpService.displayHelp("login");
        assertTrue(result.contains("username"));
    }

    @Test
    void displayHelp_unknownTopic_returnsFallbackMessage() {
        String result = helpService.displayHelp("nonexistent-topic");
        assertEquals("No help content found for this topic. Please select a topic from the list.", result);
    }

    @Test
    void listHelpTopics_returnsAllEightTopics() {
        List<String> topics = helpService.listHelpTopics();
        assertEquals(8, topics.size());
        assertTrue(topics.contains("register-appointment"));
        assertTrue(topics.contains("generate-report"));
    }
}