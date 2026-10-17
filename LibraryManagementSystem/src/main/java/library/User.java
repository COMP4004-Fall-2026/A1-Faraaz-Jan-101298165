package library;

import java.util.Objects;

public abstract class User {
    private final String username;
    private final String password;
    private final Role role;

    protected User(String username, String password, Role role) {
        this.username = requireText(username, "Username");
        this.password = requireText(password, "Password");
        this.role = Objects.requireNonNull(role, "Role is required.");
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    boolean passwordMatches(String candidate) {
        return password.equals(candidate);
    }

    static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
