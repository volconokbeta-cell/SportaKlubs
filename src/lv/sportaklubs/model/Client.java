package lv.sportaklubs.model;

import java.time.LocalDate;

/**
 * Sporta kluba klients (tabula CLIENTS).
 */
public class Client {

    private int id;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private LocalDate birthDate;

    /**
     * Izveido klienta objektu.
     */
    public Client(int id, Integer userId, String firstName, String lastName,
                  String phone, String email, LocalDate birthDate) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
    }

    /** @return vārds un uzvārds vienā virknē */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public LocalDate getBirthDate() { return birthDate; }

    @Override
    public String toString() {
        return getFullName();
    }
}
