package lv.sportaklubs;

import java.time.LocalDate;
import lv.sportaklubs.util.PasswordUtil;
import lv.sportaklubs.util.Validator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Datu validācijas testi (klases Validator un PasswordUtil).
 */
public class ValidatorTest {

    /** Pareizs un nepareizs e-pasts. */
    @Test
    public void testEmail() {
        assertTrue(Validator.isValidEmail("janis.berzins@inbox.lv"));
        assertFalse(Validator.isValidEmail("janis.inbox.lv"));
        assertFalse(Validator.isValidEmail(""));
    }

    /** Tālruņa formāts: 8 cipari, sākas ar 2 vai 6. */
    @Test
    public void testPhone() {
        assertTrue(Validator.isValidPhone("26123456"));
        assertTrue(Validator.isValidPhone("+371 26123456"));
        assertFalse(Validator.isValidPhone("1234"));
        assertFalse(Validator.isValidPhone("36123456"));
    }

    /** Robežgadījums: parolē 5 un 6 simboli. */
    @Test
    public void testPasswordLength() {
        assertFalse(Validator.isValidPassword("abc12"));
        assertTrue(Validator.isValidPassword("abc123"));
    }

    /** Datuma un pozitīva skaitļa pārveidošana. */
    @Test
    public void testParse() {
        assertEquals(LocalDate.of(2026, 10, 1), Validator.parseDate("2026-10-01"));
        assertNull(Validator.parseDate("01.10.2026"));
        assertEquals(35.5, Validator.parsePositive("35,5"), 0.001);
        assertEquals(-1, Validator.parsePositive("-5"), 0.001);
        assertEquals(-1, Validator.parsePositive("abc"), 0.001);
    }

    /** Paroles jaucējkods ir 64 simbolus garš un nemainīgs. */
    @Test
    public void testHash() {
        assertEquals(64, PasswordUtil.hash("admin123").length());
        assertEquals(PasswordUtil.hash("abc123"), PasswordUtil.hash("abc123"));
    }
}
