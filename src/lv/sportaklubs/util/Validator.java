package lv.sportaklubs.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Ievaddatu pārbaudes metodes.
 */
public final class Validator {

    private Validator() { }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && phone.replace(" ", "").matches("^(\\+371)?[26]\\d{7}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Pārveido tekstu formātā gggg-mm-dd par datumu.
     *
     */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Pārveido tekstu par pozitīvu skaitli.
     *
     */
    public static double parsePositive(String text) {
        try {
            double d = Double.parseDouble(text.trim().replace(',', '.'));
            return d > 0 ? d : -1;
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }
}
