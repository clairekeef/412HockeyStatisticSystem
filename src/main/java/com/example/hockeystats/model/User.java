package src.main.java.com.example.hockeystats.model;

/**
 * Represents a system user with authentication credentials, a role,
 * a personal profile, and configurable preferences.
 */
public class User {

    private String      userId;
    private String      password;
    private UserRole    role;
    private Profile     profile;
    private Preferences preferences;

    public User(String userId, String password, UserRole role) {
        this.userId      = userId;
        this.password    = password;
        this.role        = role;
        this.profile     = new Profile("Default Profile", userId, "");
        this.preferences = new Preferences("Default Preferences");
    }

    // --- Getters ---

    public String      getUserId()     { return userId; }
    public String      getPassword()   { return password; }
    public UserRole    getRole()       { return role; }
    public Profile     getProfile()    { return profile; }
    public Preferences getPreferences(){ return preferences; }

    // --- Setters ---

    public void setProfile(Profile profile)          { this.profile = profile; }
    public void setPreferences(Preferences prefs)    { this.preferences = prefs; }
    public void setRole(UserRole role)               { this.role = role; }

    @Override
    public String toString() {
        return "User{userId='" + userId + "', role=" + role + "}";
    }
}