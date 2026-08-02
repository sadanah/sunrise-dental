package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.service.impl.HelpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelpServiceTest {

    private HelpService helpService;

    @BeforeEach
    void setUp() {
        helpService = new HelpService();
    }

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
    void listHelpTopics_returnsNonEmptyList() {
        assertFalse(helpService.listHelpTopics().isEmpty());
    }
}