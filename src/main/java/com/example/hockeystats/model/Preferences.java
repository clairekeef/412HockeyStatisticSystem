package com.example.hockeystats.model;

/**
 * Stores user-configurable preferences for the Hockey Statistics System.
 */
public class Preferences {

    private String settings;
    private String preferredMetric;   // e.g. "Goals", "Points"
    private String preferredCountry;  // e.g. "Canada"

    public Preferences(String settings) {
        this.settings        = settings;
        this.preferredMetric  = "Points";
        this.preferredCountry = "";
    }


    public Preferences(String settings, String preferredMetric, String preferredCountry) {
        this.settings         = settings;
        this.preferredMetric  = preferredMetric;
        this.preferredCountry = preferredCountry;
    }

    public String getSettings()         { return settings; }
    public String getPreferredMetric()  { return preferredMetric; }
    public String getPreferredCountry() { return preferredCountry; }

    public void setSettings(String settings)                 { this.settings = settings; }
    public void setPreferredMetric(String preferredMetric)   { this.preferredMetric = preferredMetric; }
    public void setPreferredCountry(String preferredCountry) { this.preferredCountry = preferredCountry; }

    @Override
    public String toString() {
        return "Preferences{preferredMetric='" + preferredMetric
            + "', preferredCountry='" + preferredCountry + "'}";
    }
}