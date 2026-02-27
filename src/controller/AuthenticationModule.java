package src.controller;

import src.model.*;

public class AuthenticationModule {

    private UserManagement userManagement;
    private User currentUser;

    public AuthenticationModule() {
        System.out.println("AuthenticationModule initialized");
        userManagement = new UserManagement();
    }

    public boolean login(String userId, String password) {
        System.out.println("AuthenticationModule.login called");

        boolean valid = userManagement.validateCredentials(userId, password);

        if (valid) {
            currentUser = userManagement.getUser(userId);
            System.out.println("Login successful. Role: " + currentUser.getRole());
            return true;
        }

        System.out.println("Login failed.");
        return false;
    }

    public void launchDashboard() {
        System.out.println("AuthenticationModule.launchDashboard called");

        if (currentUser == null) {
            System.out.println("No authenticated user.");
            return;
        }

        switch (currentUser.getRole()) {

            case ADMIN:
                System.out.println("Loading Admin Dashboard...");
                break;

            case COACH:
                System.out.println("Loading Coach Dashboard...");
                break;

            case PLAYER:
                System.out.println("Loading Player Dashboard...");
                break;
        }
    }

    public UserRole getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getRole();
        }
        return null;
    }
}
