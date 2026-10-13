package lv.sportaklubs.gui;

/**
 * Palīdzības sistēmas (F1) teksts, kas tiek parādīts galvenā loga apakšlogā "Palīdzība".
 */
public final class HelpText {

    /** Palīdzības teksts HTML formātā. */
    public static final String HTML = "<html><body style='font-family:Dialog; font-size:12pt; margin:8px'>"
            + "<h2>SportaKlubs – palīdzība</h2>"
            + "<h3>Pieslēgšanās un reģistrācija</h3>"
            + "<p>Ievadiet lietotājvārdu un paroli un nospiediet <b>Pieslēgties</b>. Jauns klients var izveidot kontu ar "
            + "pogu <b>Reģistrēties</b>. Datumus ievada formātā <b>gggg-mm-dd</b>, piemēram, 2026-10-15.</p>"
            + "<h3>Administratoram</h3><ul>"
            + "<li><b>Klienti</b> – pievienošana, labošana, dzēšana; meklēšana pēc vārda, tālruņa vai e-pasta; "
            + "filtrs pēc abonementa.</li>"
            + "<li><b>Treneri</b> – treneru saraksts; treneri ar nodarbībām nevar dzēst.</li>"
            + "<li><b>Abonementi</b> – izvēlieties klientu un veidu, beigu datums tiek aprēķināts automātiski; "
            + "<b>Pagarināt</b> pagarina abonementu par vienu periodu.</li>"
            + "<li><b>Nodarbību grafiks</b> – nodarbības pievienošana ar treneri, laiku, zāli un vietu skaitu.</li>"
            + "<li><b>Maksājumi</b> – maksājuma reģistrēšana; filtrs <i>Neapmaksātie abonementi</i> parāda, "
            + "cik vēl jāpiemaksā.</li></ul>"
            + "<p>Lai labotu vai dzēstu ierakstu, vispirms izvēlieties to tabulā. Poga <b>Notīrīt</b> notīra laukus.</p>"
            + "<h3>Klientam</h3>"
            + "<p>Logā <b>Mans konts</b> redzams jūsu abonements, maksājumi un nodarbību grafiks. Izvēlieties "
            + "nodarbību un nospiediet <b>Pierakstīties</b> vai <b>Atcelt pierakstu</b>. Pierakstīties var tikai "
            + "ar aktīvu abonementu, ja nodarbībā ir brīvas vietas.</p>"
            + "<h3>Iziešana</h3><p>Izvēlnē <b>Fails → Iziet no konta</b> var pieslēgties ar citu lietotāju, "
            + "<b>Fails → Iziet</b> aizver programmu.</p></body></html>";

    private HelpText() { }
}
