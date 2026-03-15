package src.main.java.com.example.hockeystats.tester;
 
import src.main.java.com.example.hockeystats.controller.*;
import src.main.java.com.example.hockeystats.model.*;
import src.main.java.com.example.hockeystats.view.Dashboard;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
 
public class Tester {
 
    public static void main(String[] args) {
        System.out.println();
        System.out.println("===== STARTING SYSTEM INTEGRATION TEST =====\n");
 
        testAuthenticationFlow();
        testStatisticsAPIs();
        testDataRepository();
        testPlayerStats();
        testTeamStats();
        testStats();
        testMetricCalculator();
        testUserManagement();
        testUser();
        testUserRole();
        testProfile();
        testPreferences();
        testPerformanceDataEvaluation();
        testTeam();
        testGameData();
 
        System.out.println("\n===== SYSTEM TEST COMPLETE =====");
    }
 
    // -----------------------------------------------------------------------
    // Authentication
    // -----------------------------------------------------------------------
 
    private static void testAuthenticationFlow() {
        System.out.println("---- Testing Authentication Flow ----");
 
        AuthenticationModule auth = new AuthenticationModule();
 
        boolean loginSuccess = auth.login("admin1", "password");
        if (loginSuccess) {
            System.out.println("PASS: Login succeeded");
            auth.launchDashboard();
        } else {
            System.out.println("FAIL: Login failed unexpectedly");
        }
 
        boolean loginFail = auth.login("admin1", "wrong");
        if (!loginFail) {
            System.out.println("PASS: Invalid login rejected");
        } else {
            System.out.println("FAIL: Invalid login accepted");
        }
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // Statistics
    // -----------------------------------------------------------------------
 
    private static void testStatisticsAPIs() {
        System.out.println("---- Testing Statistics ----");
 
        Statistics stats = new Statistics();
 
        // comparePlayers — uses real CSV names
        String playerCompare = stats.comparePlayers(
                List.of("Adam Tambellini", "Jordan Weal", "Kent Johnson"),
                List.of("GroupA"),
                List.of("Goals", "Assists", "Points")
        );
        System.out.println(playerCompare != null ? "PASS: comparePlayers returned result" : "FAIL: comparePlayers returned null");
        if (playerCompare != null) System.out.println(playerCompare);
 
        // compareTeams — uses real country names
        String teamCompare = stats.compareTeams(
                List.of("Canada", "USA", "Germany", "China"),
                List.of("GroupA"),
                List.of("Goals", "Assists", "Points")
        );
        System.out.println(teamCompare != null ? "PASS: compareTeams returned result" : "FAIL: compareTeams returned null");
        if (teamCompare != null) System.out.println(teamCompare);
 
        // getPlayerStats
        PlayerStats ps = stats.getPlayerStats("Adam Tambellini", "GroupA");
        System.out.println(ps != null ? "PASS: getPlayerStats returned object — " + ps : "FAIL: getPlayerStats returned null");
 
        // getTeamStats
        TeamStats ts = stats.getTeamStats("Canada", "GroupA");
        System.out.println(ts != null ? "PASS: getTeamStats returned object — " + ts : "FAIL: getTeamStats returned null");
 
        // calculateMetric
        double goals = stats.calculateMetric("Adam Tambellini", "Goals", "GroupA");
        System.out.println(goals >= 0 ? "PASS: calculateMetric(Goals) = " + goals : "FAIL: calculateMetric returned invalid value");
 
        double assists = stats.calculateMetric("Jordan Weal", "Assists", "GroupA");
        System.out.println(assists >= 0 ? "PASS: calculateMetric(Assists) = " + assists : "FAIL");
 
        // getAvailableMetrics
        List<String> metrics = stats.getAvailableMetrics("player");
        System.out.println(metrics != null && !metrics.isEmpty() ? "PASS: getAvailableMetrics returned " + metrics : "FAIL: getAvailableMetrics failed");
 
        // normalizeData — multi-value list so normalization actually runs
        List<Stats> dummyStats = new ArrayList<>();
        dummyStats.add(new Stats("Canada", 22.0));
        dummyStats.add(new Stats("USA",     5.0));
        dummyStats.add(new Stats("Germany", 1.0));
        dummyStats.add(new Stats("China",   2.0));
        List<Stats> normalized = stats.normalizeData(dummyStats, List.of("GroupA"));
        System.out.println(normalized != null ? "PASS: normalizeData returned list" : "FAIL: normalizeData returned null");
        if (normalized != null) {
            for (Stats s : normalized) System.out.println("  " + s);
        }
 
        System.out.println();
 
        // Dashboard integration (claire's code)
        Dashboard dashboard = new Dashboard();
        System.out.println("Testing Tournament Overview...");
        System.out.println(dashboard.viewTournamentOverview());
        System.out.println("\nTesting Game Details...");
        dashboard.viewGameDetails("G1");
        System.out.println("\nTesting Team Comparison...");
        System.out.println(dashboard.compareTeams("USA", "Canada"));
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // DataRepository
    // -----------------------------------------------------------------------
 
    private static void testDataRepository() {
        System.out.println("---- Testing DataRepository ----");
 
        DataRepository repo = new DataRepository();
 
        // getAllPlayers
        List<PlayerStats> all = repo.getAllPlayers();
        System.out.println(all != null && !all.isEmpty()
            ? "PASS: getAllPlayers returned " + all.size() + " players"
            : "FAIL: getAllPlayers returned empty or null");
 
        // getPlayerByName
        PlayerStats p = repo.getPlayerByName("Adam Tambellini");
        System.out.println(p != null ? "PASS: getPlayerByName found — " + p : "FAIL: getPlayerByName returned null");
 
        // getPlayerByName — case insensitive
        PlayerStats pLower = repo.getPlayerByName("adam tambellini");
        System.out.println(pLower != null ? "PASS: getPlayerByName is case-insensitive" : "FAIL: case-insensitive lookup failed");
 
        // getPlayersByCountry
        List<PlayerStats> canadians = repo.getPlayersByCountry("Canada");
        System.out.println(!canadians.isEmpty()
            ? "PASS: getPlayersByCountry(Canada) returned " + canadians.size() + " players"
            : "FAIL: getPlayersByCountry returned empty");
 
        // getPlayersByGroup
        List<PlayerStats> groupA = repo.getPlayersByGroup("A");
        System.out.println(!groupA.isEmpty()
            ? "PASS: getPlayersByGroup(A) returned " + groupA.size() + " players"
            : "FAIL: getPlayersByGroup returned empty");
 
        // getPlayersByPosition
        List<PlayerStats> forwards = repo.getPlayersByPosition("Forward");
        System.out.println(!forwards.isEmpty()
            ? "PASS: getPlayersByPosition(Forward) returned " + forwards.size() + " players"
            : "FAIL: getPlayersByPosition returned empty");
 
        // getTopScorers
        List<PlayerStats> top5 = repo.getTopScorers(5);
        System.out.println(top5.size() == 5 ? "PASS: getTopScorers(5) returned 5 players" : "FAIL: getTopScorers wrong size");
        System.out.println("  Top 5 scorers overall:");
        for (PlayerStats tp : top5) System.out.println("    " + tp);
 
        // getTopScorersByCountry
        List<PlayerStats> topCanada = repo.getTopScorersByCountry("Canada", 3);
        System.out.println(topCanada.size() == 3
            ? "PASS: getTopScorersByCountry(Canada,3) returned 3 players"
            : "FAIL: getTopScorersByCountry wrong size");
 
        // getGoalsByCountry
        Map<String, Integer> goals = repo.getGoalsByCountry();
        System.out.println(goals != null && !goals.isEmpty()
            ? "PASS: getGoalsByCountry returned " + goals : "FAIL: getGoalsByCountry failed");
 
        // getPointsByCountry
        Map<String, Integer> pts = repo.getPointsByCountry();
        System.out.println(pts != null && !pts.isEmpty()
            ? "PASS: getPointsByCountry returned " + pts : "FAIL: getPointsByCountry failed");
 
        // getAvailableCountries
        List<String> countries = repo.getAvailableCountries();
        System.out.println(countries != null && !countries.isEmpty()
            ? "PASS: getAvailableCountries returned " + countries : "FAIL: getAvailableCountries failed");
 
        // getMetricForPlayer
        double g = repo.getMetricForPlayer("Adam Tambellini", "Goals");
        System.out.println(g >= 0 ? "PASS: getMetricForPlayer(Goals) = " + g : "FAIL: getMetricForPlayer returned invalid");
 
        // getMetricForCountry
        double countryGoals = repo.getMetricForCountry("Canada", "Goals");
        System.out.println(countryGoals >= 0
            ? "PASS: getMetricForCountry(Canada, Goals) = " + countryGoals
            : "FAIL: getMetricForCountry returned invalid");
 
        // unknown player returns 0
        double unknown = repo.getMetricForPlayer("Unknown Player", "Goals");
        System.out.println(unknown == 0.0 ? "PASS: unknown player returns 0.0" : "FAIL: unknown player did not return 0.0");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // PlayerStats
    // -----------------------------------------------------------------------
 
    private static void testPlayerStats() {
        System.out.println("---- Testing PlayerStats ----");
 
        // Full constructor
        PlayerStats p = new PlayerStats("Test Player", "A", "Canada", "Forward", 5, 3, 4, 7, 2, 1);
        System.out.println(p.getName().equals("Test Player")   ? "PASS: getName()"     : "FAIL: getName()");
        System.out.println(p.getGroup().equals("A")            ? "PASS: getGroup()"    : "FAIL: getGroup()");
        System.out.println(p.getCountry().equals("Canada")     ? "PASS: getCountry()"  : "FAIL: getCountry()");
        System.out.println(p.getPosition().equals("Forward")   ? "PASS: getPosition()" : "FAIL: getPosition()");
        System.out.println(p.getGp()  == 5 ? "PASS: getGp()"      : "FAIL: getGp()");
        System.out.println(p.getGoals()  == 3 ? "PASS: getGoals()"   : "FAIL: getGoals()");
        System.out.println(p.getAssists()== 4 ? "PASS: getAssists()" : "FAIL: getAssists()");
        System.out.println(p.getPts()    == 7 ? "PASS: getPts()"     : "FAIL: getPts()");
        System.out.println(p.getPim()    == 2 ? "PASS: getPim()"     : "FAIL: getPim()");
        System.out.println(p.getPpg()    == 1 ? "PASS: getPpg()"     : "FAIL: getPpg()");
 
        // Legacy constructor — backward compatibility
        PlayerStats legacy = new PlayerStats("LegacyPlayer", 2.0, 3.0);
        System.out.println(legacy.getPlayerId().equals("LegacyPlayer") ? "PASS: legacy getPlayerId()" : "FAIL: legacy getPlayerId()");
        System.out.println(legacy.getGoals()   == 2 ? "PASS: legacy getGoals()"   : "FAIL: legacy getGoals()");
        System.out.println(legacy.getAssists() == 3 ? "PASS: legacy getAssists()" : "FAIL: legacy getAssists()");
        System.out.println(legacy.getPts()     == 5 ? "PASS: legacy getPts() = G+A" : "FAIL: legacy getPts()");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // TeamStats
    // -----------------------------------------------------------------------
 
    private static void testTeamStats() {
        System.out.println("---- Testing TeamStats ----");
 
        TeamStats ts = new TeamStats("Canada", 22.0, 6.0);
        System.out.println(ts.getTeamId().equals("Canada") ? "PASS: getTeamId()" : "FAIL: getTeamId()");
        System.out.println(ts.getWins()   == 22.0 ? "PASS: getWins()"   : "FAIL: getWins()");
        System.out.println(ts.getLosses() == 6.0  ? "PASS: getLosses()" : "FAIL: getLosses()");
        System.out.println("toString: " + ts);
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // Stats
    // -----------------------------------------------------------------------
 
    private static void testStats() {
        System.out.println("---- Testing Stats ----");
 
        Stats s = new Stats("Canada_Goals", 22.0);
        System.out.println(s.getId().equals("Canada_Goals") ? "PASS: getId()"   : "FAIL: getId()");
        System.out.println(s.getValue() == 22.0             ? "PASS: getValue()" : "FAIL: getValue()");
 
        s.setValue(25.0);
        System.out.println(s.getValue() == 25.0 ? "PASS: setValue()" : "FAIL: setValue()");
        System.out.println("toString: " + s);
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // MetricCalculator
    // -----------------------------------------------------------------------
 
    private static void testMetricCalculator() {
        System.out.println("---- Testing MetricCalculator ----");
 
        // Lambda implementation
        MetricCalculator goalsCalc = id -> id.equals("Adam Tambellini") ? 3.0 : 0.0;
        double result = goalsCalc.calculate("Adam Tambellini");
        System.out.println(result == 3.0 ? "PASS: MetricCalculator lambda returned 3.0" : "FAIL: MetricCalculator returned " + result);
 
        // Wired through DataRepository
        DataRepository repo = new DataRepository();
        MetricCalculator repoCalc = id -> repo.getMetricForPlayer(id, "Goals");
        double repoResult = repoCalc.calculate("Adam Tambellini");
        System.out.println(repoResult >= 0 ? "PASS: MetricCalculator via DataRepository = " + repoResult : "FAIL");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // UserManagement
    // -----------------------------------------------------------------------
 
    private static void testUserManagement() {
        System.out.println("---- Testing UserManagement ----");
 
        UserManagement um = new UserManagement();
 
        // getUser
        User u = um.getUser("admin1");
        System.out.println(u != null ? "PASS: getUser returned user" : "FAIL: getUser returned null");
 
        // getUserRole
        UserRole role = um.getUserRole("admin1");
        System.out.println(role == UserRole.ADMIN ? "PASS: getUserRole = ADMIN" : "FAIL: getUserRole = " + role);
 
        // validateCredentials — correct
        boolean valid = um.validateCredentials("admin1", "password");
        System.out.println(valid ? "PASS: validateCredentials correct password" : "FAIL: correct password rejected");
 
        // validateCredentials — wrong
        boolean invalid = um.validateCredentials("admin1", "wrong");
        System.out.println(!invalid ? "PASS: validateCredentials wrong password rejected" : "FAIL: wrong password accepted");
 
        // getAllUsers
        List<User> all = um.getAllUsers();
        System.out.println(all != null && !all.isEmpty() ? "PASS: getAllUsers returned " + all.size() + " users" : "FAIL: getAllUsers empty");
 
        // addUser
        boolean added = um.addUser("newuser1", "pass123", UserRole.PLAYER);
        System.out.println(added ? "PASS: addUser succeeded" : "FAIL: addUser failed");
 
        // addUser duplicate
        boolean duplicate = um.addUser("admin1", "pass", UserRole.PLAYER);
        System.out.println(!duplicate ? "PASS: addUser duplicate rejected" : "FAIL: duplicate user added");
 
        // updateUserProfile
        Profile newProfile = new Profile("Updated", "Admin One", "admin@hockey.com");
        boolean updated = um.updateUserProfile("admin1", newProfile);
        System.out.println(updated ? "PASS: updateUserProfile succeeded" : "FAIL: updateUserProfile failed");
 
        // getUserPreferences
        Preferences prefs = um.getUserPreferences("coach1");
        System.out.println(prefs != null ? "PASS: getUserPreferences returned — " + prefs : "FAIL: getUserPreferences returned null");
 
        // updateUserPreferences
        Preferences newPrefs = new Preferences("Custom", "Goals", "Canada");
        boolean prefsUpdated = um.updateUserPreferences("coach1", newPrefs);
        System.out.println(prefsUpdated ? "PASS: updateUserPreferences succeeded" : "FAIL: updateUserPreferences failed");
 
        // removeUser
        boolean removed = um.removeUser("newuser1");
        System.out.println(removed ? "PASS: removeUser succeeded" : "FAIL: removeUser failed");
 
        // removeUser — non-existent
        boolean removedFake = um.removeUser("doesnotexist");
        System.out.println(!removedFake ? "PASS: removeUser non-existent returns false" : "FAIL");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // User
    // -----------------------------------------------------------------------
 
    private static void testUser() {
        System.out.println("---- Testing User ----");
 
        User user = new User("testuser", "pass", UserRole.COACH);
        System.out.println(user.getUserId().equals("testuser") ? "PASS: getUserId()"  : "FAIL: getUserId()");
        System.out.println(user.getPassword().equals("pass")   ? "PASS: getPassword()" : "FAIL: getPassword()");
        System.out.println(user.getRole() == UserRole.COACH    ? "PASS: getRole()"    : "FAIL: getRole()");
        System.out.println(user.getProfile()     != null       ? "PASS: getProfile() not null"      : "FAIL: getProfile() null");
        System.out.println(user.getPreferences() != null       ? "PASS: getPreferences() not null"  : "FAIL: getPreferences() null");
 
        Profile p = new Profile("New Profile", "Test User", "test@hockey.com");
        user.setProfile(p);
        System.out.println(user.getProfile().getDisplayName().equals("Test User") ? "PASS: setProfile()" : "FAIL: setProfile()");
 
        user.setRole(UserRole.ADMIN);
        System.out.println(user.getRole() == UserRole.ADMIN ? "PASS: setRole()" : "FAIL: setRole()");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // UserRole
    // -----------------------------------------------------------------------
 
    private static void testUserRole() {
        System.out.println("---- Testing UserRole ----");
 
        System.out.println(UserRole.ADMIN  != null ? "PASS: UserRole.ADMIN exists"  : "FAIL");
        System.out.println(UserRole.COACH  != null ? "PASS: UserRole.COACH exists"  : "FAIL");
        System.out.println(UserRole.PLAYER != null ? "PASS: UserRole.PLAYER exists" : "FAIL");
        System.out.println(UserRole.values().length == 3 ? "PASS: UserRole has 3 values" : "FAIL: unexpected number of roles");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // Profile
    // -----------------------------------------------------------------------
 
    private static void testProfile() {
        System.out.println("---- Testing Profile ----");
 
        Profile p = new Profile("A coach profile", "Jane Coach", "jane@hockey.com");
        System.out.println(p.getDescription().equals("A coach profile")     ? "PASS: getDescription()" : "FAIL: getDescription()");
        System.out.println(p.getDisplayName().equals("Jane Coach")           ? "PASS: getDisplayName()" : "FAIL: getDisplayName()");
        System.out.println(p.getEmail().equals("jane@hockey.com")            ? "PASS: getEmail()"       : "FAIL: getEmail()");
 
        p.setDisplayName("Jane Updated");
        System.out.println(p.getDisplayName().equals("Jane Updated") ? "PASS: setDisplayName()" : "FAIL: setDisplayName()");
 
        p.setEmail("new@hockey.com");
        System.out.println(p.getEmail().equals("new@hockey.com") ? "PASS: setEmail()" : "FAIL: setEmail()");
 
        // Minimal constructor
        Profile minimal = new Profile("Minimal");
        System.out.println(minimal.getDescription().equals("Minimal") ? "PASS: minimal constructor" : "FAIL: minimal constructor");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // Preferences
    // -----------------------------------------------------------------------
 
    private static void testPreferences() {
        System.out.println("---- Testing Preferences ----");
 
        Preferences prefs = new Preferences("Custom Settings", "Goals", "Canada");
        System.out.println(prefs.getSettings().equals("Custom Settings")       ? "PASS: getSettings()"         : "FAIL: getSettings()");
        System.out.println(prefs.getPreferredMetric().equals("Goals")          ? "PASS: getPreferredMetric()"  : "FAIL: getPreferredMetric()");
        System.out.println(prefs.getPreferredCountry().equals("Canada")        ? "PASS: getPreferredCountry()" : "FAIL: getPreferredCountry()");
 
        prefs.setPreferredMetric("Assists");
        System.out.println(prefs.getPreferredMetric().equals("Assists") ? "PASS: setPreferredMetric()" : "FAIL: setPreferredMetric()");
 
        prefs.setPreferredCountry("USA");
        System.out.println(prefs.getPreferredCountry().equals("USA") ? "PASS: setPreferredCountry()" : "FAIL: setPreferredCountry()");
 
        // Minimal constructor defaults
        Preferences minimal = new Preferences("Default");
        System.out.println(minimal.getPreferredMetric().equals("Points") ? "PASS: default preferredMetric = Points" : "FAIL: default preferredMetric");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // PerformanceDataEvaluation
    // -----------------------------------------------------------------------
 
    public static void testPerformanceDataEvaluation() {
        System.out.println("---- Testing PerformanceDataEvaluation ----");
 
        TeamStats teamStats   = new TeamStats("USA", 2, 0);
        PlayerStats playerStats = new PlayerStats("Joe Smith", 1, 0);
        PerformanceDataEvaluation evaluation = new PerformanceDataEvaluation(0, null);
 
        String playerInfo = evaluation.player(playerStats);
        System.out.println(playerInfo != null ? "PASS: player() returned result" : "FAIL: player() returned null");
 
        double overallPerformance = evaluation.calculateOverallPerformance(teamStats, playerStats);
        System.out.println(overallPerformance >= 0 ? "PASS: calculateOverallPerformance = " + overallPerformance : "FAIL");
 
        String evalResult = evaluation.getEvaluation();
        System.out.println(evalResult != null ? "PASS: getEvaluation = " + evalResult : "FAIL: getEvaluation returned null");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // Team
    // -----------------------------------------------------------------------
 
    public static void testTeam() {
        System.out.println("---- Testing Team ----");
 
        List<String> roster = new ArrayList<>();
        roster.add("Joe Smith");
        roster.add("Ryan Johnson");
 
        Team team = new Team("USA", roster);
 
        System.out.println(team.getTeamCountry() != null ? "PASS: getTeamCountry = " + team.getTeamCountry() : "FAIL: getTeamCountry");
        System.out.println(team.getRoster() != null && !team.getRoster().isEmpty() ? "PASS: getRoster returned " + team.getRoster().size() + " players" : "FAIL: getRoster");
 
        Statistics stats = team.getTeamStats();
        System.out.println(stats != null ? "PASS: getTeamStats returned Statistics object" : "FAIL: getTeamStats returned null");
 
        List<GameSummary> history = team.getOpponentHistory("Canada");
        System.out.println(history != null ? "PASS: getOpponentHistory returned list" : "FAIL: getOpponentHistory returned null");
 
        List<String> newRoster = new ArrayList<>();
        newRoster.add("Player3");
        team.updateRoster(newRoster);
        System.out.println(team.getRoster().contains("Player3") ? "PASS: updateRoster succeeded" : "FAIL: updateRoster failed");
 
        System.out.println();
    }
 
    // -----------------------------------------------------------------------
    // Game & GameSummary
    // -----------------------------------------------------------------------
 
    public static void testGameData() {
        System.out.println("---- Testing Game & GameSummary ----");
 
        Game game1 = new Game("G001", "USA", "Canada", "Milano Cortina", new Date());
 
        String gameData = game1.getGameData();
        System.out.println(gameData != null ? "PASS: getGameData returned result" : "FAIL: getGameData returned null");
 
        game1.updateGameStatus("In Progress");
        System.out.println(game1.getGameStatus().equals("In Progress") ? "PASS: updateGameStatus = In Progress" : "FAIL: updateGameStatus");
 
        game1.updateGameStatus("Completed");
        System.out.println(game1.getGameStatus().equals("Completed") ? "PASS: updateGameStatus = Completed" : "FAIL: updateGameStatus");
 
        GameSummary summary1 = new GameSummary("S001", "G001", "USA", "Canada", new Date());
 
        List<String> charts = summary1.displayCharts();
        System.out.println(charts != null && !charts.isEmpty() ? "PASS: displayCharts returned result" : "FAIL: displayCharts");
 
        List<String> patterns = summary1.displayPerformancePatterns();
        System.out.println(patterns != null && !patterns.isEmpty() ? "PASS: displayPerformancePatterns returned result" : "FAIL: displayPerformancePatterns");
 
        String statsResult = summary1.displayStats();
        System.out.println(statsResult != null ? "PASS: displayStats returned result" : "FAIL: displayStats");
 
        String teamCompare = summary1.compareTeams("Canada");
        System.out.println(teamCompare != null ? "PASS: compareTeams returned result" : "FAIL: compareTeams");
 
        System.out.println();
    }
}