package com.example.hockeystats.model;

/**
 * Observer interface for the Observer design pattern.
 * Any class that wants to be notified when player data
 * changes must implement this interface.
 */
public interface DataObserver {
    void onDataLoaded(int playerCount, String source);
}