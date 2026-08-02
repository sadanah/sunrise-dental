package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.service.IHelpService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class HelpService implements IHelpService {

    private final Map<String, String> helpContent = new LinkedHashMap<>();

    public HelpService() {
        helpContent.put("login", "Enter your username and password on the login page, then click Login. Contact your Admin if you've forgotten your credentials.");
        helpContent.put("register-appointment", "From the Receptionist Dashboard, select 'Register New Appointment', choose the patient, dentist, treatment, date and time, then submit. The system will reject double-booked slots automatically.");
        helpContent.put("cancel-appointment", "From the Receptionist Dashboard, select 'Cancel Appointment' and enter the appointment number. Only appointments with status SCHEDULED can be cancelled.");
        helpContent.put("search-appointment", "Use 'Search / Display Appointment' and enter the appointment number to view its full details.");
        helpContent.put("generate-bill", "From 'Generate Bill', enter the appointment number. You may optionally apply a discount percentage before generating or printing the receipt.");
        helpContent.put("manage-staff", "Admin users can add, update, or delete staff accounts from the Manage Staff Accounts page.");
        helpContent.put("manage-treatment", "Admin users can add, update, or delete treatment types and their base costs from Manage Treatment Types.");
        helpContent.put("generate-report", "Admin users can generate Revenue, Daily Appointment, or Dentist Schedule reports from the Generate Report page.");
    }

    @Override
    public String displayHelp(String topic) {
        return helpContent.getOrDefault(topic, "No help content found for this topic. Please select a topic from the list.");
    }

    @Override
    public List<String> listHelpTopics() {
        return new ArrayList<>(helpContent.keySet());
    }
}