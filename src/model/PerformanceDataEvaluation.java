package src.model;

public class PerformanceDataEvaluation {
    private double overallPerformance;
    private String evaluation;

    public PerformanceDataEvaluation(double overallPerformance, String evaluation) {
        this.overallPerformance = overallPerformance;
        this.evaluation = evaluation;
    }

    public String player(PlayerStats playerStats) {
        return "Player ID: " + playerStats.getPlayerId() + 
        ", Goals: " + playerStats.getGoals() + ", Assists: " + 
        playerStats.getAssists();
    }
    
    public double calculateOverallPerformance(TeamStats teamStats, PlayerStats playerStats) {
        double teamPerformance = teamStats.getWins() / (teamStats.getWins() + teamStats.getLosses());
        double playerPerformance = (playerStats.getGoals() + playerStats.getAssists()) / 10;
        overallPerformance = (teamPerformance + playerPerformance) / 2;
        return overallPerformance;
    }

    public String getEvaluation() {
        if (overallPerformance >= 0.8) {
            evaluation = "Excellent";
        } else if (overallPerformance >= 0.5) {
            evaluation = "Good";
        } else {
            evaluation = "Needs Improvement";
        }
        return evaluation;
    }
}
