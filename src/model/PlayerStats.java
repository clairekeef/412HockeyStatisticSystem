package src.model;
public class PlayerStats {

    private final String name;       // player name (also used as ID)
    private final String group;      // tournament group, e.g. "A"
    private final String country;    // country / team
    private final String position;   // "Forward" or "Defence"
    private final int    gp;         // games played
    private final int    goals;      // goals scored
    private final int    assists;    // assists
    private final int    pts;        // total points (G + A)
    private final int    pim;        // penalty minutes
    private final int    ppg;        // power-play goals

    public PlayerStats(String name, String group, String country, String position,
                       int gp, int goals, int assists, int pts, int pim, int ppg) {
        this.name     = name;
        this.group    = group;
        this.country  = country;
        this.position = position;
        this.gp       = gp;
        this.goals    = goals;
        this.assists  = assists;
        this.pts      = pts;
        this.pim      = pim;
        this.ppg      = ppg;
    }

    /**
     * Legacy constructor kept for backward compatibility.
     * GP, PTS, PIM and PPG are derived / defaulted automatically.
     */
    public PlayerStats(String playerId, double goals, double assists) {
        this.name     = playerId;
        this.group    = "";
        this.country  = "";
        this.position = "";
        this.gp       = 0;
        this.goals    = (int) goals;
        this.assists  = (int) assists;
        this.pts      = this.goals + this.assists;
        this.pim      = 0;
        this.ppg      = 0;
    }

    /** Player name — also serves as the player's unique identifier. */
    public String getName()     { return name; }

    /** Alias for getName(), used by legacy code that calls getPlayerId(). */
    public String getPlayerId() { return name; }

    public String getGroup()    { return group; }
    public String getCountry()  { return country; }
    public String getPosition() { return position; }

    public int getGp()      { return gp; }
    public int getGoals()   { return goals; }
    public int getAssists() { return assists; }
    public int getPts()     { return pts; }
    public int getPim()     { return pim; }
    public int getPpg()     { return ppg; }

    @Override
    public String toString() {
        return String.format("%-25s | %-7s | %-8s | GP:%-2d G:%-2d A:%-2d PTS:%-2d PIM:%-2d PPG:%d",
            name, country, position, gp, goals, assists, pts, pim, ppg);
    }
}