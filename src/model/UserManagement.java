package src.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all user accounts in the Hockey Statistics System.
 * Provides creation, lookup, credential validation, and profile/preference updates.
 */
public class UserManagement {

    private Map<String, User> users;

    public UserManagement() {
        System.out.println("UserManagement initialized");

        users = new HashMap<>();

        // Pre-loaded users for demo / testing
        users.put("admin1",  new User("admin1",  "password", UserRole.ADMIN));
        users.put("coach1",  new User("coach1",  "password", UserRole.COACH));
        users.put("player1", new User("player1", "password", UserRole.PLAYER));
    }

  
    public User getUser(String userId) {
        System.out.println("UserManagement.getUser called — userId: " + userId);
        return users.get(userId);
    }

    public UserRole getUserRole(String userId) {
        System.out.println("UserManagement.getUserRole called — userId: " + userId);
        User user = users.get(userId);
        return user != null ? user.getRole() : null;
    }

    public List<User> getAllUsers() {
        System.out.println("UserManagement.getAllUsers called");
        return new ArrayList<>(users.values());
    }


    /**
     * Adds a new user. Returns false if the userId already exists.
     */
    public boolean addUser(String userId, String password, UserRole role) {
        System.out.println("UserManagement.addUser called — userId: " + userId);
        if (users.containsKey(userId)) return false;
        users.put(userId, new User(userId, password, role));
        return true;
    }

    /**
     * Removes a user. Returns false if the userId does not exist.
     */
    public boolean removeUser(String userId) {
        System.out.println("UserManagement.removeUser called — userId: " + userId);
        return users.remove(userId) != null;
    }

    public boolean updateUserProfile(String userId, Profile profile) {
        System.out.println("UserManagement.updateUserProfile called — userId: " + userId);
        User user = users.get(userId);
        if (user != null) {
            user.setProfile(profile);
            return true;
        }
        return false;
    }

    public Preferences getUserPreferences(String userId) {
        System.out.println("UserManagement.getUserPreferences called — userId: " + userId);
        User user = users.get(userId);
        return user != null ? user.getPreferences() : null;
    }

    public boolean updateUserPreferences(String userId, Preferences preferences) {
        System.out.println("UserManagement.updateUserPreferences called — userId: " + userId);
        User user = users.get(userId);
        if (user != null) {
            user.setPreferences(preferences);
            return true;
        }
        return false;
    }

    public boolean validateCredentials(String userId, String password) {
        System.out.println("UserManagement.validateCredentials called — userId: " + userId);
        User user = users.get(userId);
        return user != null && user.getPassword().equals(password);
    }
}