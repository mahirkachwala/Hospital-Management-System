package com.hospital.observer;

public interface Observer {
    void update(String eventType, Object data);
}