package src.model;

public class PerformanceDataEvaluation {
    
    public double calculateOverallPerformance(TeamStats teamStats, PlayerStats playerStats) {
        double teamPerformance = teamStats.getWins() / (teamStats.getWins() + teamStats.getLosses());
        double playerPerformance = (playerStats.getGoals() + playerStats.getAssists()) / 10;
        return (teamPerformance + playerPerformance) / 2;
    }

    public String evaluatePerformance(double overallPerformance) {
        if (overallPerformance >= 0.8) {
            return "Excellent";
        } else if (overallPerformance >= 0.5) {
            return "Good";
        } else {
            return "Needs Improvement";
        }
    }
}
