package lv.sportaklubs.gui;

import java.awt.Component;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.*;

/**
 * Kopīgas grafiskās saskarnes palīgmetodes: ziņojumi, tabulas un noformējums.
 */
public final class Ui {

    private Ui() { }

    /** Uzstāda Nimbus noformējumu, vienotu fontu un latviskus dialogu pogu tekstus. */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // ja Nimbus nav pieejams, paliek noklusētais noformējums
        }
        UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("Dialog", Font.PLAIN, 13));
        UIManager.put("OptionPane.okButtonText", "Labi");
        UIManager.put("OptionPane.cancelButtonText", "Atcelt");
        UIManager.put("OptionPane.yesButtonText", "Jā");
        UIManager.put("OptionPane.noButtonText", "Nē");
    }

    /**
     * Parāda informatīvu ziņojumu.
     */
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Informācija", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Parāda kļūdas ziņojumu.
     */
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Kļūda", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Parāda brīdinājumu.
     */
    public static void warn(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Uzmanību", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Uzdod jautājumu ar pogām Jā/Nē.
     */
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Apstiprinājums", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Parāda datu bāzes kļūdu.
     */
    public static void dbError(Component parent, SQLException e) {
        error(parent, "Datu bāzes kļūda: " + e.getMessage());
    }

    /**
     * Noformē GUI Builder izveidotu tabulu: vienas rindas izvēle, rindas augstums, šaura ID kolonna.
     */
    public static void setupTable(JTable t) {
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.setRowHeight(22);
        t.getTableHeader().setReorderingAllowed(false);
        if (t.getColumnCount() > 0 && "ID".equals(t.getColumnName(0))) {
            t.getColumnModel().getColumn(0).setMaxWidth(50);
        }
    }

    /**
     * Atgriež tabulā izvēlētās rindas ID (pirmā kolonna) vai parāda brīdinājumu.
     */
    public static Integer selectedId(Component parent, JTable t, String what) {
        int row = t.getSelectedRow();
        if (row < 0) {
            warn(parent, "Lūdzu, izvēlieties " + what + " tabulā!");
            return null;
        }
        return (Integer) t.getModel().getValueAt(row, 0);
    }
}
