package com.example.hockeystats.model;

/**
 * Subject interface for the Observer design pattern.
 * Classes that maintain player data and notify observers
 * when that data changes must implement this interface.
 */
public interface DataSubject {
    void addObserver(DataObserver observer);
    void removeObserver(DataObserver observer);
    void notifyObservers(int playerCount, String source);
}