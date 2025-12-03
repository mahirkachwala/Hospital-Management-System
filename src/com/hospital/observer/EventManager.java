package com.hospital.observer;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements Subject {
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventType, Object data) {
        // Iterate over a copy to avoid ConcurrentModificationException if observers unregister themselves
        for (Observer observer : new ArrayList<>(observers)) {
            observer.update(eventType, data);
        }
    }

    public void publishEvent(String eventType, Object data) {
        System.out.println("EVENT_MANAGER: Publishing event - " + eventType + ": " + data.toString());
        notifyObservers(eventType, data);
    }
}