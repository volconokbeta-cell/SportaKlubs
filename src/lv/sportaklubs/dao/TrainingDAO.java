package lv.sportaklubs.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Training;

/**
 * Datu piekļuve tabulai TRAININGS (nodarbību grafiks).
 */
public class TrainingDAO {

    private static final String SELECT = "SELECT t.*, tr.first_name || ' ' || tr.last_name AS tname, " + "(SELECT COUNT(*) FROM BOOKINGS b WHERE b.training_id = t.id) AS booked " + "FROM TRAININGS t JOIN TRAINERS tr ON tr.id = t.trainer_id ";

    /**
     * Pievieno nodarbību.
     */
    public void add(Training t) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO TRAININGS (name, trainer_id, start_time, duration_min, hall, max_participants) " + "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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
     * Saglabā nodarbības izmaiņas.
     */
    public boolean update(Training t) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE TRAININGS SET name = ?, trainer_id = ?, start_time = ?, duration_min = ?, hall = ?, " + "max_participants = ? WHERE id = ?")) {
            fill(ps, t);
            ps.setInt(7, t.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Dzēš nodarbību (kopā ar tās pierakstiem).
     */
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM TRAININGS WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Atgriež nodarbības ar filtru pēc datuma un trenera.
     */
    public List<Training> search(LocalDate date, int trainerId) throws SQLException {
        String sql = SELECT + "WHERE 1 = 1";
        if (date != null) {
            sql += " AND t.start_time >= ? AND t.start_time < ?";
        }
        if (trainerId > 0) {
            sql += " AND t.trainer_id = ?";
        }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql + " ORDER BY t.start_time")) {
            int i = 1;
            if (date != null) {
                ps.setTimestamp(i++, Timestamp.valueOf(date.atStartOfDay()));
                ps.setTimestamp(i++, Timestamp.valueOf(date.plusDays(1).atStartOfDay()));
            }
            if (trainerId > 0) {
                ps.setInt(i, trainerId);
            }
            return read(ps);
        }
    }

    /**
     * Atrod nodarbību pēc ID.
     */
    public Training getById(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(SELECT + "WHERE t.id = ?")) {
            ps.setInt(1, id);
            List<Training> l = read(ps);
            return l.isEmpty() ? null : l.get(0);
        }
    }

    /**
     * Pārbauda, vai zāle norādītajā laikā ir aizņemta ar citu nodarbību.
     */
    public boolean isHallBusy(Training t, int excludeId) throws SQLException {
        for (Training o : search(t.getStartTime().toLocalDate(), 0)) {
            boolean overlap = t.getStartTime().isBefore(o.getEndTime()) && o.getStartTime().isBefore(t.getEndTime());
            if (o.getId() != excludeId && o.getHall().equalsIgnoreCase(t.getHall()) && overlap) {
                return true;
            }
        }
        return false;
    }

    private void fill(PreparedStatement ps, Training t) throws SQLException {
        ps.setString(1, t.getName().trim());
        ps.setInt(2, t.getTrainerId());
        ps.setTimestamp(3, Timestamp.valueOf(t.getStartTime()));
        ps.setInt(4, t.getDurationMin());
        ps.setString(5, t.getHall().trim());
        ps.setInt(6, t.getMaxParticipants());
    }

    private List<Training> read(PreparedStatement ps) throws SQLException {
        List<Training> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                Training t = new Training(rs.getInt("id"), rs.getString("name"), rs.getInt("trainer_id"), start,
                        rs.getInt("duration_min"), rs.getString("hall"), rs.getInt("max_participants"));
                t.setTrainerName(rs.getString("tname"));
                t.setBooked(rs.getInt("booked"));
                list.add(t);
            }
        }
        return list;
    }
}
