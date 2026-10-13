package lv.sportaklubs.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Booking;
import lv.sportaklubs.model.Training;

/**
 * Datu piekļuve tabulai BOOKINGS (pieraksti nodarbībām).
 */
public class BookingDAO {

    /**
     * Pieraksta klientu nodarbībai, pārbaudot visus nosacījumus.
     *
     * @param clientId   klienta ID
     * @param trainingId nodarbības ID
     * @param now        pašreizējais laiks
     * @return pieraksta rezultāts
     * @throws SQLException datu bāzes kļūda
     */
    public Booking.Result book(int clientId, int trainingId, LocalDateTime now) throws SQLException {
        Training t = new TrainingDAO().getById(trainingId);
        if (t.getStartTime().isBefore(now)) {
            return Booking.Result.IN_PAST;
        }
        if (isBooked(clientId, trainingId)) {
            return Booking.Result.ALREADY_BOOKED;
        }
        if (new MembershipDAO().getActive(clientId, t.getStartTime().toLocalDate()) == null) {
            return Booking.Result.NO_MEMBERSHIP;
        }
        if (t.getFreeSpots() <= 0) {
            return Booking.Result.NO_FREE_SPOTS;
        }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO BOOKINGS (client_id, training_id) VALUES (?, ?)")) {
            ps.setInt(1, clientId);
            ps.setInt(2, trainingId);
            ps.executeUpdate();
        }
        return Booking.Result.SUCCESS;
    }

    /**
     * Atceļ klienta pierakstu (tikai paša klienta pierakstu).
     *
     * @param clientId   klienta ID
     * @param trainingId nodarbības ID
     * @return true, ja pieraksts atcelts
     * @throws SQLException datu bāzes kļūda
     */
    public boolean cancel(int clientId, int trainingId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "DELETE FROM BOOKINGS WHERE client_id = ? AND training_id = ?")) {
            ps.setInt(1, clientId);
            ps.setInt(2, trainingId);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * @param clientId   klienta ID
     * @param trainingId nodarbības ID
     * @return true, ja klients jau ir pierakstīts
     * @throws SQLException datu bāzes kļūda
     */
    public boolean isBooked(int clientId, int trainingId) throws SQLException {
        return getTrainingIds(clientId).contains(trainingId);
    }

    /**
     * @param clientId klienta ID
     * @return to nodarbību ID kopa, kurām klients ir pierakstīts
     * @throws SQLException datu bāzes kļūda
     */
    public Set<Integer> getTrainingIds(int clientId) throws SQLException {
        Set<Integer> ids = new HashSet<>();
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT training_id FROM BOOKINGS WHERE client_id = ?")) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        }
        return ids;
    }
}
