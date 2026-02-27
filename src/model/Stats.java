package src.model;

public class Stats {
    private String id;
    private double value;

    public Stats(String id, double value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }
}
