package lv.sportaklubs;

import java.util.List;
import lv.sportaklubs.dao.ClientDAO;
import lv.sportaklubs.model.Client;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Klientu CRUD, meklēšanas un filtrēšanas integrācijas testi (FP-03–FP-06).
 */
public class ClientDAOTest extends DaoTestBase {

    /** Pievienotais klients tiek saglabāts DB. */
    @Test
    public void testAdd() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        assertTrue(c.getId() > 0);
        assertEquals("Jānis Bērziņš", clients.getById(c.getId()).getFullName());
    }

    /** Labotie dati tiek saglabāti DB. */
    @Test
    public void testUpdate() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        Client fromDb = clients.getById(c.getId());
        assertNull(fromDb.getUserId());
        Client changed = new Client(c.getId(), fromDb.getUserId(), "Jānis", "Kalniņš", "26000000",
                "janis@inbox.lv", null);
        assertTrue(clients.update(changed));
        assertEquals("Kalniņš", clients.getById(c.getId()).getLastName());
    }

    /** Dzēstais klients DB vairs nav atrodams. */
    @Test
    public void testDelete() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        assertTrue(clients.delete(c.getId()));
        assertNull(clients.getById(c.getId()));
    }

    /** Meklēšana pēc vārda daļas un filtrēšana pēc abonementa. */
    @Test
    public void testSearchAndFilter() throws Exception {
        Client a = client("Jānis", "janis@inbox.lv");
        client("Pēteris", "peteris@inbox.lv");
        memberships.add(a.getId(), "MĒNEŠA", today, 35);
        assertEquals(1, clients.search("pēt", ClientDAO.ALL, today).size());
        List<Client> active = clients.search("", ClientDAO.ACTIVE, today);
        assertEquals(1, active.size());
        assertEquals(a.getId(), active.get(0).getId());
        assertEquals(1, clients.search("", ClientDAO.INACTIVE, today).size());
    }

    /** Robežgadījums: e-pasts jau eksistē (reģistrnejutīgi). */
    @Test
    public void testEmailExists() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        assertTrue(clients.emailExists("JANIS@inbox.lv", 0));
        assertFalse(clients.emailExists("janis@inbox.lv", c.getId()));
    }
}
