package src.model;

public class User {

    private String userId;
    private String password;
    private UserRole role;
    private Profile profile;
    private Preferences preferences;

    public User(String userId, String password, UserRole role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.profile = new Profile("Default Profile");
        this.preferences = new Preferences("Default Preferences");
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}