package lv.sportaklubs.model;

import java.time.LocalDate;

/**
 * Klienta abonements (tabula MEMBERSHIPS).
 */
public class Membership {

    /** Abonementu veidi. */
    public static final String[] TYPES = {"MĒNEŠA", "GADA", "10 APMEKLĒJUMI"};
    /** Abonementu statusi. */
    public static final String[] STATUSES = {"AKTĪVS", "APTURĒTS", "BEIDZIES"};

    private int id;
    private int clientId;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
    private String status;
    private String clientName;

    /**
     * Izveido abonementa objektu.
     *
     * @param id        abonementa ID (0 – jauns)
     * @param clientId  klienta ID
     * @param type      veids
     * @param startDate sākuma datums
     * @param endDate   beigu datums
     * @param price     cena
     * @param status    statuss
     */
    public Membership(int id, int clientId, String type, LocalDate startDate,
                      LocalDate endDate, double price, String status) {
        this.id = id;
        this.clientId = clientId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.status = status;
    }

    /**
     * Aprēķina abonementa derīguma dienu skaitu pēc veida.
     *
     * @param type abonementa veids
     * @return dienu skaits (30, 365 vai 90)
     */
    public static int daysFor(String type) {
        switch (type) {
            case "GADA": return 365;
            case "10 APMEKLĒJUMI": return 90;
            default: return 30;
        }
    }

    /**
     * Pārbauda, vai abonements ir derīgs norādītajā datumā.
     *
     * @param date pārbaudāmais datums
     * @return true, ja statuss ir AKTĪVS un datums ir derīguma periodā
     */
    public boolean isActive(LocalDate date) {
        return "AKTĪVS".equals(status) && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public String getType() { return type; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    @Override
    public String toString() {
        return "#" + id + " " + type + " (" + startDate + " – " + endDate + ")";
    }
}
