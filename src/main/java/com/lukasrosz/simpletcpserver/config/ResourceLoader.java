package com.lukasrosz.simpletcpserver.config;

import java.util.IllegalFormatException;
import java.util.ResourceBundle;

public class ResourceLoader {

    private ResourceBundle resourceBundle;

    public ResourceLoader(String configFilename) {
        resourceBundle = ResourceBundle.getBundle(configFilename);
    }

    public int getServerPort() {
        return parseToInt(resourceBundle.getString("serverPort"));
    }

    private int parseToInt(String numb) {
        int parsed;
        try {
            parsed = Integer.parseInt(numb);
        } catch (IllegalFormatException e) {
            parsed = 0;
        }
        return parsed;
    }
}
