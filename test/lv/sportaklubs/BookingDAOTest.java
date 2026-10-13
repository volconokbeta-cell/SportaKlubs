package lv.sportaklubs;

import lv.sportaklubs.model.Booking;
import lv.sportaklubs.model.Client;
import lv.sportaklubs.model.Training;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pieraksta uz nodarbību biznesa loģikas testi (FP-09, FP-10).
 */
public class BookingDAOTest extends DaoTestBase {

    /** Bez aktīva abonementa pierakstīties nevar. */
    @Test
    public void testNoMembership() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        Training t = training(now.plusDays(1), 5);
        assertEquals(Booking.Result.NO_MEMBERSHIP, bookings.book(c.getId(), t.getId(), now));
    }

    /** Veiksmīgs pieraksts un atkārtots pieraksts. */
    @Test
    public void testSuccessAndAlreadyBooked() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        memberships.add(c.getId(), "MĒNEŠA", today, 35);
        Training t = training(now.plusDays(1), 5);
        assertEquals(Booking.Result.SUCCESS, bookings.book(c.getId(), t.getId(), now));
        assertEquals(Booking.Result.ALREADY_BOOKED, bookings.book(c.getId(), t.getId(), now));
        assertEquals(1, trainings.getById(t.getId()).getBooked());
    }

    /** Robežgadījums: pilna nodarbība un pagājusi nodarbība. */
    @Test
    public void testFullAndPast() throws Exception {
        Client a = client("Jānis", "janis@inbox.lv");
        Client b = client("Ilze", "ilze@inbox.lv");
        memberships.add(a.getId(), "MĒNEŠA", today, 35);
        memberships.add(b.getId(), "MĒNEŠA", today, 35);
        Training t = training(now.plusDays(1), 1);
        bookings.book(a.getId(), t.getId(), now);
        assertEquals(Booking.Result.NO_FREE_SPOTS, bookings.book(b.getId(), t.getId(), now));
        assertEquals(Booking.Result.IN_PAST, bookings.book(b.getId(), t.getId(), now.plusDays(2)));
    }

    /** Pieraksta atcelšana atbrīvo vietu. */
    @Test
    public void testCancel() throws Exception {
        Client c = client("Jānis", "janis@inbox.lv");
        memberships.add(c.getId(), "MĒNEŠA", today, 35);
        Training t = training(now.plusDays(1), 1);
        bookings.book(c.getId(), t.getId(), now);
        assertTrue(bookings.cancel(c.getId(), t.getId()));
        assertEquals(1, trainings.getById(t.getId()).getFreeSpots());
    }

    /** Zāle nav pieejama, ja nodarbību laiki pārklājas; treneri ar nodarbībām nevar dzēst. */
    @Test
    public void testHallBusyAndTrainerDelete() throws Exception {
        Training t = training(now.plusDays(1), 5);
        Training other = new Training(0, "Pilates", t.getTrainerId(), t.getStartTime().plusMinutes(30), 60, "Zāle A", 5);
        assertTrue(trainings.isHallBusy(other, 0));
        Training later = new Training(0, "Pilates", t.getTrainerId(), t.getStartTime().plusMinutes(60), 60, "Zāle A", 5);
        assertFalse(trainings.isHallBusy(later, 0));
        assertTrue(trainers.hasTrainings(t.getTrainerId()));
    }
}
