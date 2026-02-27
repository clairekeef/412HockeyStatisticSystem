package src.model;

public class PlayerStats {

    private String playerId;
    private double goals;
    private double assists;

    public PlayerStats(String playerId, double goals, double assists) {
        this.playerId = playerId;
        this.goals = goals;
        this.assists = assists;
    }

    public String getPlayerId() {
        return playerId;
    }

    public double getGoals() {
        return goals;
    }

    public double getAssists() {
        return assists;
    }
}
