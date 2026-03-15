package src.main.java.com.example.hockeystats.model;

/**
 * Represents a single generic statistic: a named entity paired with a numeric value.
 * Used by Statistics.normalizeData() and for general metric storage.
 */
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

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Stats{id='" + id + "', value=" + value + "}";
    }
}