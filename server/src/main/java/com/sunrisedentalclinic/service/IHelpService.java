package com.sunrisedentalclinic.service;

import java.util.List;

public interface IHelpService {
    String displayHelp(String topic);
    List<String> listHelpTopics();
}