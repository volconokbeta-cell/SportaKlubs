package lv.sportaklubs.dao;

import java.sql.*;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Client;
import lv.sportaklubs.model.User;
import lv.sportaklubs.util.PasswordUtil;

/**
 * Datu piekļuve tabulai USERS: reģistrācija un pieslēgšanās.
 */
public class UserDAO {

    /**
     * Pārbauda, vai lietotājvārds jau ir aizņemts.
     *
     * @param username lietotājvārds
     * @return true, ja lietotājvārds eksistē
     * @throws SQLException datu bāzes kļūda
     */
    public boolean usernameExists(String username) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT 1 FROM USERS WHERE LOWER(username) = ?")) {
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Reģistrē klientu: izveido kontu (loma CLIENT) un klienta ierakstu vienā transakcijā.
     *
     * @param username lietotājvārds
     * @param password parole atklātā tekstā
     * @param client   klienta dati
     * @return izveidotā lietotāja ID
     * @throws SQLException datu bāzes kļūda (izmaiņas tiek atceltas)
     */
    public int register(String username, String password, Client client) throws SQLException {
        Connection con = DBConnection.getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO USERS (username, password_hash, role) VALUES (?, ?, 'CLIENT')",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username.trim());
            ps.setString(2, PasswordUtil.hash(password));
            ps.executeUpdate();
            int userId;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                userId = keys.getInt(1);
            }
            client.setUserId(userId);
            new ClientDAO().add(client);
            con.commit();
            return userId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Pārbauda lietotājvārdu un paroli.
     *
     * @param username lietotājvārds
     * @param password parole
     * @return lietotājs vai null, ja dati nav pareizi
     * @throws SQLException datu bāzes kļūda
     */
    public User login(String username, String password) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM USERS WHERE LOWER(username) = ? AND password_hash = ?")) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, PasswordUtil.hash(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("password_hash"), rs.getString("role"));
                }
            }
        }
        return null;
    }
}
