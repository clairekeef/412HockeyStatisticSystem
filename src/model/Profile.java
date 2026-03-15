package src.model;

/**
 * Stores personal/display information for a system user.
 */
public class Profile {

    private String description;
    private String displayName;
    private String email;

    public Profile(String description) {
        this.description = description;
        this.displayName = "";
        this.email       = "";
    }


    public Profile(String description, String displayName, String email) {
        this.description = description;
        this.displayName = displayName;
        this.email       = email;
    }

    public String getDescription() { return description; }
    public String getDisplayName() { return displayName; }
    public String getEmail()       { return email; }

    public void setDescription(String description) { this.description = description; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email)             { this.email = email; }

    @Override
    public String toString() {
        return "Profile{displayName='" + displayName + "', email='" + email + "'}";
    }
}