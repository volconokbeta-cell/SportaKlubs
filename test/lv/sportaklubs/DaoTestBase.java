package lv.sportaklubs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lv.sportaklubs.dao.*;
import lv.sportaklubs.db.DBConnection;
import lv.sportaklubs.model.*;
import org.junit.Before;

/**
 * Kopīga sagatave integrācijas testiem: katram testam jauna Derby datu bāze atmiņā.
 */
public abstract class DaoTestBase {

    protected final ClientDAO clients = new ClientDAO();
    protected final TrainerDAO trainers = new TrainerDAO();
    protected final MembershipDAO memberships = new MembershipDAO();
    protected final TrainingDAO trainings = new TrainingDAO();
    protected final BookingDAO bookings = new BookingDAO();
    protected final PaymentDAO payments = new PaymentDAO();
    protected final UserDAO users = new UserDAO();
    protected final LocalDate today = LocalDate.of(2026, 10, 1);
    protected final LocalDateTime now = today.atTime(9, 0);

    /** Izveido tukšu datu bāzi atmiņā. */
    @Before
    public void createDb() throws Exception {
        DBConnection.setUrl("jdbc:derby:memory:test" + System.nanoTime() + ";create=true");
        DBConnection.getConnection();
    }

    /** Palīgmetode: pievieno klientu. */
    protected Client client(String first, String email) throws Exception {
        Client c = new Client(0, null, first, "Bērziņš", "26123456", email, LocalDate.of(2000, 5, 14));
        clients.add(c);
        return c;
    }

    /** Palīgmetode: pievieno treneri un nodarbību. */
    protected Training training(LocalDateTime start, int places) throws Exception {
        Trainer t = new Trainer(0, "Anna", "Bērziņa", "Joga", "26111222");
        trainers.add(t);
        Training tr = new Training(0, "Joga", t.getId(), start, 60, "Zāle A", places);
        trainings.add(tr);
        return tr;
    }
}
