package src.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameSummary {
    private String summaryID;
    private String gameID;
    private String teamCountry;
    private String opponentTeam;
    private Date date;
    private List<String> charts;
    private List<String> performancePatterns;
    

    public GameSummary(String summaryID,String gameID, String teamCountry,
        String opponentTeam, Date date){
        
        this.summaryID = summaryID;
        this.gameID = gameID;
        this.teamCountry = teamCountry;
        this.opponentTeam = opponentTeam;
        this.date = date;
        this.charts = new ArrayList<>();
        this.performancePatterns = new ArrayList<>();

        this.charts.add("Chart");
        this.charts.add("Zone Chart");
        this.performancePatterns.add("Offensive Pattern");
        this.performancePatterns.add("Defensive Pattern");
    }

    public List<String>displayCharts(){
        System.out.println("Display Charts");
        return charts;
    }

    public List<String>displayPerformancePatterns(){
        System.out.println("Display patterns");
        return performancePatterns;
    }

    public String displayStats(){
        System.out.println("Stats Display");
        return "Stats Data";
    }

    public String compareTeams(String opponentTeam){
        System.out.println("Team comparison with: " + opponentTeam);
        return "Comparison Result";
    }

    public String getSummaryID(){
        return summaryID;
    }

    public Date getDate() {
        return date;
    }

    public void addChart(String chart) {
        this.charts.add(chart);
    }

    public void addPerformancePattern(String pattern) {
        this.performancePatterns.add(pattern);
    }
}
