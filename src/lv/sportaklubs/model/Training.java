package lv.sportaklubs.model;

import java.time.LocalDateTime;

/**
 * Grupu nodarbība (tabula TRAININGS).
 */
public class Training {

    private int id;
    private String name;
    private int trainerId;
    private LocalDateTime startTime;
    private int durationMin;
    private String hall;
    private int maxParticipants;
    private String trainerName;
    private int booked;

    /**
     * Izveido nodarbības objektu.
     */
    public Training(int id, String name, int trainerId, LocalDateTime startTime,
                    int durationMin, String hall, int maxParticipants) {
        this.id = id;
        this.name = name;
        this.trainerId = trainerId;
        this.startTime = startTime;
        this.durationMin = durationMin;
        this.hall = hall;
        this.maxParticipants = maxParticipants;
    }

    /** @return nodarbības beigu laiks */
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMin);
    }

    /** @return brīvo vietu skaits */
    public int getFreeSpots() {
        return maxParticipants - booked;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public int getTrainerId() { return trainerId; }
    public LocalDateTime getStartTime() { return startTime; }
    public int getDurationMin() { return durationMin; }
    public String getHall() { return hall; }
    public int getMaxParticipants() { return maxParticipants; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public int getBooked() { return booked; }
    public void setBooked(int booked) { this.booked = booked; }
}
