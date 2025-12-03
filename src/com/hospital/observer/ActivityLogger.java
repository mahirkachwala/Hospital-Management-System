package com.hospital.observer;

import com.hospital.service.FileManager;

public class ActivityLogger implements Observer {
    private FileManager fileManager;

    public ActivityLogger() {
        this.fileManager = FileManager.getInstance();
    }

    @Override
    public void update(String eventType, Object data) {
        String logMessage = "Event: " + eventType + " | Data: " + data.toString();
        fileManager.logActivity(logMessage);
        System.out.println("ACTIVITY_LOGGER: Logged - " + logMessage.substring(0, Math.min(logMessage.length(), 100)) + (logMessage.length() > 100 ? "..." : "")); // Print shortened log to console
    }
}