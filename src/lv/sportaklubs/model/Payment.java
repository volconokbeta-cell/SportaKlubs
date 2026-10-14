package lv.sportaklubs.model;

import java.time.LocalDate;

/**
 * Maksājums par abonementu (tabula PAYMENTS).
 */
public class Payment {

    /** Maksājuma veidi. */
    public static final String[] METHODS = {"Skaidra nauda", "Karte", "Pārskaitījums"};

    private int id;
    private int clientId;
    private int membershipId;
    private double amount;
    private LocalDate payDate;
    private String method;
    private String clientName;

    /**
     * Izveido maksājuma objektu.
     */
    public Payment(int id, int clientId, int membershipId, double amount, LocalDate payDate, String method) {
        this.id = id;
        this.clientId = clientId;
        this.membershipId = membershipId;
        this.amount = amount;
        this.payDate = payDate;
        this.method = method;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public int getMembershipId() { return membershipId; }
    public double getAmount() { return amount; }
    public LocalDate getPayDate() { return payDate; }
    public String getMethod() { return method; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
}
