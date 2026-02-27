package src.model;

public class TeamStats {
    private String teamId;
    private double wins;
    private double losses;

    public TeamStats(String teamId, double wins, double losses) {
        this.teamId = teamId;
        this.wins = wins;
        this.losses = losses;
    }

    public String getTeamId() {
        return teamId;
    }

    public double getWins() {
        return wins;
    }

    public double getLosses() {
        return losses;
    }
}
