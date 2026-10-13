package lv.sportaklubs;

import java.time.LocalDate;
import lv.sportaklubs.model.Client;
import lv.sportaklubs.model.User;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Reģistrācijas un pieslēgšanās testi (FP-01, FP-02).
 */
public class UserDAOTest extends DaoTestBase {

    /** Administratora konts tiek izveidots automātiski. */
    @Test
    public void testAdminLogin() throws Exception {
        User u = users.login("admin", "admin123");
        assertNotNull(u);
        assertTrue(u.isAdmin());
    }

    /** Reģistrēts klients var pieslēgties, un konts ir saistīts ar klientu. */
    @Test
    public void testRegisterAndLogin() throws Exception {
        Client c = new Client(0, null, "Ilze", "Liepa", "29876543", "ilze@inbox.lv", LocalDate.of(1999, 1, 2));
        int userId = users.register("ilze", "parole1", c);
        User u = users.login("ilze", "parole1");
        assertEquals("CLIENT", u.getRole());
        assertEquals(c.getId(), clients.getByUserId(userId).getId());
        assertEquals(Integer.valueOf(userId), clients.getById(c.getId()).getUserId());
    }

    /** Robežgadījums: nepareiza parole. */
    @Test
    public void testWrongPassword() throws Exception {
        assertNull(users.login("admin", "nepareiza"));
    }

    /** Robežgadījums: jau esošs lietotājvārds (reģistrnejutīgi). */
    @Test
    public void testUsernameExists() throws Exception {
        assertTrue(users.usernameExists("ADMIN"));
        assertFalse(users.usernameExists("janis"));
    }
}
