package lv.sportaklubs.model;

/**
 * Sistēmas lietotāja konts (tabula USERS).
 */
public class User {

    /** Administratora loma. */
    public static final String ADMIN = "ADMIN";
    /** Klienta loma. */
    public static final String CLIENT = "CLIENT";

    private int id;
    private String username;
    private String passwordHash;
    private String role;

    /**
     * Izveido lietotāja objektu.
     */
    public User(int id, String username, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /** @return true, ja lietotājs ir administrators */
    public boolean isAdmin() {
        return ADMIN.equals(role);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
}
