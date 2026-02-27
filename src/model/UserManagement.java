package src.model;

import java.util.HashMap;
import java.util.Map;

public class UserManagement {

    private Map<String, User> users;

    public UserManagement() {
        System.out.println("UserManagement initialized");

        users = new HashMap<>();

        // Stub users for testing
        users.put("admin1", new User("admin1", "password", UserRole.ADMIN));
        users.put("coach1", new User("coach1", "password", UserRole.COACH));
        users.put("player1", new User("player1", "password", UserRole.PLAYER));
    }

    public User getUser(String userId) {
        System.out.println("UserManagement.getUser called");
        return users.get(userId);
    }

    public UserRole getUserRole(String userId) {
        System.out.println("UserManagement.getUserRole called");

        User user = users.get(userId);
        if (user != null) {
            return user.getRole();
        }

        return null;
    }

    public boolean updateUserProfile(String userId, Profile profile) {
        System.out.println("UserManagement.updateUserProfile called");

        User user = users.get(userId);
        if (user != null) {
            user.setProfile(profile);
            return true;
        }

        return false;
    }

    public Preferences getUserPreferences(String userId) {
        System.out.println("UserManagement.getUserPreferences called");

        User user = users.get(userId);
        if (user != null) {
            return user.getPreferences();
        }

        return null;
    }

    // Helper for authentication
    public boolean validateCredentials(String userId, String password) {
        System.out.println("UserManagement.validateCredentials called");

        User user = users.get(userId);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }

        return false;
    }
}