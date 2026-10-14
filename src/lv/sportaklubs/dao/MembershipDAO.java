package lv.sportaklubs.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.Membership;

/**
 * Datu piekļuve tabulai MEMBERSHIPS.
 */
public class MembershipDAO {

    private static final String SELECT = "SELECT m.*, c.first_name || ' ' || c.last_name AS cname " + "FROM MEMBERSHIPS m JOIN CLIENTS c ON c.id = m.client_id ";

    /**
     * Izveido jaunu abonementu. Beigu datums tiek aprēķināts pēc veida; ja klientam
     * jau ir aktīvs abonements, jaunais sākas nākamajā dienā pēc tā beigām.
     *
     */
    public Membership add(int clientId, String type, LocalDate start, double price) throws SQLException {
        Membership active = getActive(clientId, start);
        if (active != null) {
            start = active.getEndDate().plusDays(1);
        }
        LocalDate end = start.plusDays(Membership.daysFor(type) - 1);
        Membership m = new Membership(0, clientId, type, start, end, price, "AKTĪVS");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO MEMBERSHIPS (client_id, type, start_date, end_date, price, status) " + "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, clientId);
            ps.setString(2, type);
            ps.setDate(3, Date.valueOf(start));
            ps.setDate(4, Date.valueOf(end));
            ps.setDouble(5, price);
            ps.setString(6, "AKTĪVS");
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) {
                    m.setId(k.getInt(1));
                }
            }
        }
        return m;
    }

    /**
     * Saglabā abonementa izmaiņas (veids, datumi, cena, statuss).
     */
    public boolean update(Membership m) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE MEMBERSHIPS SET type = ?, start_date = ?, end_date = ?, price = ?, status = ? WHERE id = ?")) {
            ps.setString(1, m.getType());
            ps.setDate(2, Date.valueOf(m.getStartDate()));
            ps.setDate(3, Date.valueOf(m.getEndDate()));
            ps.setDouble(4, m.getPrice());
            ps.setString(5, m.getStatus());
            ps.setInt(6, m.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Pagarina abonementu par vienu tā veida periodu un padara to aktīvu.
     */
    public boolean extend(Membership m) throws SQLException {
        m.setEndDate(m.getEndDate().plusDays(Membership.daysFor(m.getType())));
        m.setStatus("AKTĪVS");
        return update(m);
    }

    /**
     * Dzēš abonementu.
     */
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM MEMBERSHIPS WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Abonementiem, kuru beigu datums ir pagājis, iestata statusu BEIDZIES.
     */
    public int expireOld(LocalDate today) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE MEMBERSHIPS SET status = 'BEIDZIES' WHERE end_date < ? AND status = 'AKTĪVS'")) {
            ps.setDate(1, Date.valueOf(today));
            return ps.executeUpdate();
        }
    }

    /**
     * Meklē abonementus pēc klienta vārda un statusa.
     */
    public List<Membership> search(String clientText, String status) throws SQLException {
        String sql = SELECT + "WHERE LOWER(c.first_name || ' ' || c.last_name) LIKE ?";
        boolean all = status == null || "Visi".equals(status);
        if (!all) {
            sql += " AND m.status = ?";
        }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql + " ORDER BY m.end_date DESC")) {
            ps.setString(1, "%" + clientText.trim().toLowerCase() + "%");
            if (!all) {
                ps.setString(2, status);
            }
            return read(ps);
        }
    }
    
    public List<Membership> getByClient(int clientId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(SELECT + "WHERE m.client_id = ? ORDER BY m.end_date DESC")) {
            ps.setInt(1, clientId);
            return read(ps);
        }
    }

    /**
     * Atrod klienta aktīvo abonementu norādītajā datumā.
     */
    public Membership getActive(int clientId, LocalDate date) throws SQLException {
        for (Membership m : getByClient(clientId)) {
            if (m.isActive(date)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Atgriež abonementus, par kuriem samaksāts mazāk par cenu.
     */
    public List<Membership> getUnpaid() throws SQLException {
        String sql = SELECT + "WHERE m.price > (SELECT COALESCE(SUM(p.amount), 0) FROM PAYMENTS p " + "WHERE p.membership_id = m.id) ORDER BY m.start_date";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            return read(ps);
        }
    }

    private List<Membership> read(PreparedStatement ps) throws SQLException {
        List<Membership> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Membership m = new Membership(rs.getInt("id"), rs.getInt("client_id"), rs.getString("type"),
                        rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                        rs.getDouble("price"), rs.getString("status"));
                m.setClientName(rs.getString("cname"));
                list.add(m);
            }
        }
        return list;
    }
}
