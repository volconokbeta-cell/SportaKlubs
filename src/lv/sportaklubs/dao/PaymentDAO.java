package lv.sportaklubs.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Payment;

/**
 * Datu piekļuve tabulai PAYMENTS.
 */
public class PaymentDAO {

    private static final String SELECT = "SELECT p.*, c.first_name || ' ' || c.last_name AS cname "
            + "FROM PAYMENTS p JOIN CLIENTS c ON c.id = p.client_id ";

    /**
     * Reģistrē maksājumu.
     *
     * @param p maksājums (tiek iestatīts ID)
     * @throws SQLException datu bāzes kļūda
     */
    public void add(Payment p) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO PAYMENTS (client_id, membership_id, amount, pay_date, method) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, p);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) {
                    p.setId(k.getInt(1));
                }
            }
        }
    }

    /**
     * Saglabā maksājuma izmaiņas.
     *
     * @param p maksājums
     * @return true, ja atjaunots
     * @throws SQLException datu bāzes kļūda
     */
    public boolean update(Payment p) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE PAYMENTS SET client_id = ?, membership_id = ?, amount = ?, pay_date = ?, method = ? "
                + "WHERE id = ?")) {
            fill(ps, p);
            ps.setInt(6, p.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Dzēš maksājumu.
     *
     * @param id maksājuma ID
     * @return true, ja dzēsts
     * @throws SQLException datu bāzes kļūda
     */
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM PAYMENTS WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Meklē maksājumus pēc klienta vārda.
     *
     * @param clientText klienta vārda daļa
     * @return maksājumu saraksts (jaunākie pirmie)
     * @throws SQLException datu bāzes kļūda
     */
    public List<Payment> search(String clientText) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(SELECT
                + "WHERE LOWER(c.first_name || ' ' || c.last_name) LIKE ? ORDER BY p.pay_date DESC")) {
            ps.setString(1, "%" + clientText.trim().toLowerCase() + "%");
            return read(ps);
        }
    }

    /**
     * @param clientId klienta ID
     * @return klienta maksājumi
     * @throws SQLException datu bāzes kļūda
     */
    public List<Payment> getByClient(int clientId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(SELECT + "WHERE p.client_id = ? ORDER BY p.pay_date DESC")) {
            ps.setInt(1, clientId);
            return read(ps);
        }
    }

    /**
     * @param membershipId abonementa ID
     * @return kopējā samaksātā summa par abonementu
     * @throws SQLException datu bāzes kļūda
     */
    public double getPaidAmount(int membershipId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT COALESCE(SUM(amount), 0) FROM PAYMENTS WHERE membership_id = ?")) {
            ps.setInt(1, membershipId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }

    private void fill(PreparedStatement ps, Payment p) throws SQLException {
        ps.setInt(1, p.getClientId());
        ps.setInt(2, p.getMembershipId());
        ps.setDouble(3, p.getAmount());
        ps.setDate(4, Date.valueOf(p.getPayDate()));
        ps.setString(5, p.getMethod());
    }

    private List<Payment> read(PreparedStatement ps) throws SQLException {
        List<Payment> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Payment p = new Payment(rs.getInt("id"), rs.getInt("client_id"), rs.getInt("membership_id"),
                        rs.getDouble("amount"), rs.getDate("pay_date").toLocalDate(), rs.getString("method"));
                p.setClientName(rs.getString("cname"));
                list.add(p);
            }
        }
        return list;
    }
}
