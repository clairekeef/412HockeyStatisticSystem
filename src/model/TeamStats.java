package src.model;

/**
 * Represents aggregated statistics for a team (country).
 * wins  = total goals scored by the team across all players
 * losses = total penalty minutes (PIM) — placeholder until game result data is available
 */
public class TeamStats {

    private String teamId;
    private double wins;
    private double losses;

    public TeamStats(String teamId, double wins, double losses) {
        this.teamId = teamId;
        this.wins   = wins;
        this.losses = losses;
    }

    public String getTeamId()  { return teamId; }
    public double getWins()    { return wins; }
    public double getLosses()  { return losses; }

    @Override
    public String toString() {
        return "TeamStats{teamId='" + teamId + "', wins=" + wins + ", losses=" + losses + "}";
    }
}