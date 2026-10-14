package lv.sportaklubs.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Trainer;

/**
 * Datu piekļuve tabulai TRAINERS.
 */
public class TrainerDAO {

    /**
     * Pievieno treneri.
     */
    public void add(Trainer t) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO TRAINERS (first_name, last_name, specialization, phone) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, t);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) {
                    t.setId(k.getInt(1));
                }
            }
        }
    }

    /**
     * Saglabā trenera izmaiņas.
     */
    public boolean update(Trainer t) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE TRAINERS SET first_name = ?, last_name = ?, specialization = ?, phone = ? WHERE id = ?")) {
            fill(ps, t);
            ps.setInt(5, t.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Dzēš treneri.
     */
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM TRAINERS WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Pārbauda, vai trenerim ir piesaistītas nodarbības.
     */
    public boolean hasTrainings(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT 1 FROM TRAININGS WHERE trainer_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Trainer> getAll() throws SQLException {
        return search("", "");
    }

    /**
     * Meklē trenerus pēc vārda/uzvārda un specializācijas.
     */
    public List<Trainer> search(String text, String specialization) throws SQLException {
        String sql = "SELECT * FROM TRAINERS WHERE (LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ?)" + " AND LOWER(specialization) LIKE ? ORDER BY last_name";
        List<Trainer> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String t = "%" + text.trim().toLowerCase() + "%";
            ps.setString(1, t);
            ps.setString(2, t);
            ps.setString(3, "%" + specialization.trim().toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Trainer(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("specialization"), rs.getString("phone")));
                }
            }
        }
        return list;
    }

    private void fill(PreparedStatement ps, Trainer t) throws SQLException {
        ps.setString(1, t.getFirstName().trim());
        ps.setString(2, t.getLastName().trim());
        ps.setString(3, t.getSpecialization().trim());
        ps.setString(4, t.getPhone() == null ? null : t.getPhone().trim());
    }
}
