package lv.sportaklubs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lv.sportaklubs.util.PasswordUtil;

/**
 * Savienojums ar Apache Derby datu bāzi un tabulu izveide.
 */
public final class DBConnection {

    private static String url = "jdbc:derby:sportaklubsDB;create=true";
    private static Connection connection;

    /** Datu bāzes tabulu izveides SQL komandas. */
    public static final String[] DDL = {
        "CREATE TABLE USERS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "username VARCHAR(30) NOT NULL UNIQUE, password_hash VARCHAR(64) NOT NULL, "
            + "role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN','CLIENT')))",
        "CREATE TABLE CLIENTS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "user_id INT REFERENCES USERS(id) ON DELETE SET NULL, "
            + "first_name VARCHAR(50) NOT NULL, last_name VARCHAR(50) NOT NULL, "
            + "phone VARCHAR(15) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE, birth_date DATE)",
        "CREATE TABLE TRAINERS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "first_name VARCHAR(50) NOT NULL, last_name VARCHAR(50) NOT NULL, "
            + "specialization VARCHAR(60) NOT NULL, phone VARCHAR(15))",
        "CREATE TABLE MEMBERSHIPS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "client_id INT NOT NULL REFERENCES CLIENTS(id) ON DELETE CASCADE, "
            + "type VARCHAR(20) NOT NULL, start_date DATE NOT NULL, end_date DATE NOT NULL, "
            + "price DECIMAL(7,2) NOT NULL CHECK (price > 0), "
            + "status VARCHAR(10) DEFAULT 'AKTĪVS' NOT NULL, CHECK (end_date >= start_date))",
        "CREATE TABLE TRAININGS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "name VARCHAR(60) NOT NULL, trainer_id INT NOT NULL REFERENCES TRAINERS(id), "
            + "start_time TIMESTAMP NOT NULL, duration_min INT NOT NULL CHECK (duration_min > 0), "
            + "hall VARCHAR(20) NOT NULL, max_participants INT NOT NULL CHECK (max_participants > 0))",
        "CREATE TABLE BOOKINGS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "client_id INT NOT NULL REFERENCES CLIENTS(id) ON DELETE CASCADE, "
            + "training_id INT NOT NULL REFERENCES TRAININGS(id) ON DELETE CASCADE, "
            + "booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, UNIQUE (client_id, training_id))",
        "CREATE TABLE PAYMENTS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
            + "client_id INT NOT NULL REFERENCES CLIENTS(id) ON DELETE CASCADE, "
            + "membership_id INT NOT NULL REFERENCES MEMBERSHIPS(id) ON DELETE CASCADE, "
            + "amount DECIMAL(7,2) NOT NULL CHECK (amount > 0), pay_date DATE NOT NULL, "
            + "method VARCHAR(20) NOT NULL)"
    };

    private DBConnection() { }

    /**
     * Maina datu bāzes adresi (izmanto testos atmiņas datu bāzei).
     *
     * @param newUrl JDBC adrese
     */
    public static void setUrl(String newUrl) {
        url = newUrl;
        connection = null;
    }

    /**
     * Atgriež savienojumu ar datu bāzi; pirmajā reizē izveido tabulas.
     *
     * @return savienojums
     * @throws SQLException ja savienojumu neizdodas izveidot
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
            createTables(connection);
        }
        return connection;
    }

    /**
     * Izveido tabulas un administratora kontu, ja tabulu vēl nav.
     *
     * @param con savienojums
     * @throws SQLException ja SQL komanda neizdodas
     */
    private static void createTables(Connection con) throws SQLException {
        try (ResultSet rs = con.getMetaData().getTables(null, null, "USERS", null)) {
            if (rs.next()) {
                return;
            }
        }
        try (Statement st = con.createStatement()) {
            for (String sql : DDL) {
                st.executeUpdate(sql);
            }
            st.executeUpdate("INSERT INTO USERS (username, password_hash, role) VALUES ('admin', '"
                    + PasswordUtil.hash("admin123") + "', 'ADMIN')");
        }
    }
}
