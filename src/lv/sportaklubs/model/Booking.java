package lv.sportaklubs.model;

import java.time.LocalDateTime;

/**
 * Klienta pieraksts nodarbībai (tabula BOOKINGS).
 */
public class Booking {

    /** Pieraksta mēģinājuma rezultāti. */
    public enum Result { SUCCESS, NO_MEMBERSHIP, NO_FREE_SPOTS, ALREADY_BOOKED, IN_PAST }

    private int id;
    private int clientId;
    private int trainingId;
    private LocalDateTime bookedAt;

    /**
     * Izveido pieraksta objektu.
     */
    public Booking(int id, int clientId, int trainingId, LocalDateTime bookedAt) {
        this.id = id;
        this.clientId = clientId;
        this.trainingId = trainingId;
        this.bookedAt = bookedAt;
    }

    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public int getTrainingId() { return trainingId; }
    public LocalDateTime getBookedAt() { return bookedAt; }
}
