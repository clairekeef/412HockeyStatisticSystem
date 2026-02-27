package src.model;

@FunctionalInterface
public interface MetricCalculator {
    double calculate(String entityId);

}
