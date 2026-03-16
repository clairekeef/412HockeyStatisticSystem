package com.example.hockeystats.model;

@FunctionalInterface
public interface MetricCalculator {
    double calculate(String entityId);

}
