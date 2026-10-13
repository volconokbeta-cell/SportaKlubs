package lv.sportaklubs.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Ievaddatu pārbaudes metodes.
 */
public final class Validator {

    private Validator() { }

    /**
     * @param s pārbaudāmā virkne
     * @return true, ja virkne nav null un nav tukša
     */
    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * @param email e-pasta adrese
     * @return true, ja adrese atbilst formātam vards@domens.lv
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    }

    /**
     * @param phone tālruņa numurs
     * @return true, ja numurs sastāv no 8 cipariem un sākas ar 2 vai 6
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && phone.replace(" ", "").matches("^(\\+371)?[26]\\d{7}$");
    }

    /**
     * @param password parole
     * @return true, ja parolē ir vismaz 6 simboli
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Pārveido tekstu formātā gggg-mm-dd par datumu.
     *
     * @param text datuma teksts
     * @return datums vai null, ja teksts ir nekorekts
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
     * @param text skaitļa teksts (atļauts arī komats)
     * @return skaitlis vai -1, ja teksts nav pozitīvs skaitlis
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
