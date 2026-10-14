package lv.sportaklubs.model;

/**
 * Sporta kluba treneris (tabula TRAINERS).
 */
public class Trainer {

    private int id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;

    /**
     * Izveido trenera objektu.
     */
    public Trainer(int id, String firstName, String lastName, String specialization, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
    }

    /** @return vārds un uzvārds */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpecialization() { return specialization; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return getFullName() + " (" + specialization + ")";
    }
}
