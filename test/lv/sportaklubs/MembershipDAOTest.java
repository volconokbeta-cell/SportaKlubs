package lv.sportaklubs;

import java.time.LocalDate;
import lv.sportaklubs.model.Client;
import lv.sportaklubs.model.Membership;
import lv.sportaklubs.model.Payment;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Abonementu un maksājumu biznesa loģikas testi (FP-08, FP-11).
 */
public class MembershipDAOTest extends DaoTestBase {

    /** Mēneša abonementa beigu datums ir pēc 30 dienām. */
    @Test
    public void testEndDate() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        Membership m = memberships.add(c.getId(), "MĒNEŠA", today, 35);
        assertEquals(LocalDate.of(2026, 10, 30), m.getEndDate());
        assertTrue(m.isActive(today));
        assertFalse(m.isActive(LocalDate.of(2026, 10, 31)));
    }

    /** Otrs abonements sākas nākamajā dienā pēc aktīvā beigām. */
    @Test
    public void testSecondStartsAfterActive() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        Membership first = memberships.add(c.getId(), "MĒNEŠA", today, 35);
        Membership second = memberships.add(c.getId(), "GADA", today, 300);
        assertEquals(first.getEndDate().plusDays(1), second.getStartDate());
    }

    /** Pagarināšana un novecojušo abonementu statusa maiņa. */
    @Test
    public void testExtendAndExpire() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        Membership m = memberships.add(c.getId(), "MĒNEŠA", today, 35);
        memberships.extend(m);
        assertEquals(LocalDate.of(2026, 11, 29), m.getEndDate());
        assertEquals(1, memberships.expireOld(LocalDate.of(2026, 12, 1)));
        assertNull(memberships.getActive(c.getId(), LocalDate.of(2026, 11, 2)));
    }

    /** Daļēji apmaksāts abonements ir neapmaksāto sarakstā, pilnībā apmaksāts – nav. */
    @Test
    public void testUnpaid() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        Membership m = memberships.add(c.getId(), "MĒNEŠA", today, 35);
        payments.add(new Payment(0, c.getId(), m.getId(), 20, today, "Karte"));
        assertEquals(1, memberships.getUnpaid().size());
        payments.add(new Payment(0, c.getId(), m.getId(), 15, today, "Karte"));
        assertEquals(35, payments.getPaidAmount(m.getId()), 0.001);
        assertTrue(memberships.getUnpaid().isEmpty());
    }
}
