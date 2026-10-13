package lv.sportaklubs.util;

/**
 * Glabā informāciju par pieslēgto lietotāju.
 */
public final class Session {

    /** Pieslēgtā lietotāja ID (-1 – nav pieslēdzies). */
    public static int userId = -1;
    /** Lietotāja loma: ADMIN vai CLIENT. */
    public static String role;
    /** Klienta ID, ja pieslēdzies klients, citādi -1. */
    public static int clientId = -1;
    /** Lietotājvārds. */
    public static String username;

    private Session() { }

    /** @return true, ja pieslēdzies administrators */
    public static boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /** Notīra sesiju (iziešana no konta). */
    public static void clear() {
        userId = -1;
        clientId = -1;
        role = null;
        username = null;
    }
}
