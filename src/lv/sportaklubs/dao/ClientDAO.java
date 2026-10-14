package lv.sportaklubs.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Client;

/**
 * Datu piekļuve tabulai CLIENTS (CRUD, meklēšana, filtrēšana).
 */
public class ClientDAO {

    /** Filtrs: visi klienti. */
    public static final String ALL = "Visi";
    /** Filtrs: klienti ar aktīvu abonementu. */
    public static final String ACTIVE = "Ar aktīvu abonementu";
    /** Filtrs: klienti bez aktīva abonementa. */
    public static final String INACTIVE = "Bez aktīva abonementa";

    private static final String ACTIVE_SQL = "EXISTS (SELECT 1 FROM MEMBERSHIPS m WHERE m.client_id = c.id " + "AND m.status = 'AKTĪVS' AND ? BETWEEN m.start_date AND m.end_date)";

    /**
     * Pievieno klientu.
     */
    public void add(Client c) throws SQLException {
        String sql = "INSERT INTO CLIENTS (user_id, first_name, last_name, phone, email, birth_date) " + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, c);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) {
                    c.setId(k.getInt(1));
                }
            }
        }
    }

    /**
     * Saglabā klienta izmaiņas.
     */
    public boolean update(Client c) throws SQLException {
        String sql = "UPDATE CLIENTS SET user_id = ?, first_name = ?, last_name = ?, phone = ?, " + "email = ?, birth_date = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            fill(ps, c);
            ps.setInt(7, c.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Dzēš klientu (kopā ar viņa abonementiem, pierakstiem un maksājumiem).
     */
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM CLIENTS WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Client> getAll() throws SQLException {
        return search("", ALL, LocalDate.now());
    }

    /**
     * Meklē un filtrē klientus.
     */
    public List<Client> search(String text, String filter, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM CLIENTS c WHERE (LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? " + "OR phone LIKE ? OR LOWER(email) LIKE ?)";
        if (ACTIVE.equals(filter)) {
            sql += " AND " + ACTIVE_SQL;
        } else if (INACTIVE.equals(filter)) {
            sql += " AND NOT " + ACTIVE_SQL;
        }
        sql += " ORDER BY last_name, first_name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String t = "%" + (text == null ? "" : text.trim().toLowerCase()) + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, t);
            }
            if (!ALL.equals(filter)) {
                ps.setDate(5, Date.valueOf(date));
            }
            return read(ps);
        }
    }

    /**
     * Atrod klientu pēc ID.
     */
    public Client getById(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM CLIENTS c WHERE id = ?")) {
            ps.setInt(1, id);
            List<Client> l = read(ps);
            return l.isEmpty() ? null : l.get(0);
        }
    }

    /**
     * Atrod klientu, kuram pieder konts.
     */
    public Client getByUserId(int userId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM CLIENTS c WHERE user_id = ?")) {
            ps.setInt(1, userId);
            List<Client> l = read(ps);
            return l.isEmpty() ? null : l.get(0);
        }
    }

    /**
     * Pārbauda, vai e-pasts jau pieder citam klientam.
     */
    public boolean emailExists(String email, int excludeId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT 1 FROM CLIENTS WHERE LOWER(email) = ? AND id <> ?")) {
            ps.setString(1, email.trim().toLowerCase());
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void fill(PreparedStatement ps, Client c) throws SQLException {
        if (c.getUserId() == null) {
            ps.setNull(1, Types.INTEGER);
        } else {
            ps.setInt(1, c.getUserId());
        }
        ps.setString(2, c.getFirstName().trim());
        ps.setString(3, c.getLastName().trim());
        ps.setString(4, c.getPhone().trim());
        ps.setString(5, c.getEmail().trim().toLowerCase());
        ps.setDate(6, c.getBirthDate() == null ? null : Date.valueOf(c.getBirthDate()));
    }

    private List<Client> read(PreparedStatement ps) throws SQLException {
        List<Client> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date bd = rs.getDate("birth_date");
                int uid = rs.getInt("user_id");
                Integer userId = rs.wasNull() ? null : uid;
                list.add(new Client(rs.getInt("id"), userId,
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("phone"),
                        rs.getString("email"), bd == null ? null : bd.toLocalDate()));
            }
        }
        return list;
    }
}
