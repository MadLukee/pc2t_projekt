package cz.upce.pc2t;

import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class Main {
    
    private static final DatabazZamestnancu databaze = new DatabazZamestnancu();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Databáze zaměstnanců ===");
         System.out.println();

        boolean bezi = true;
        while (bezi) {
            zobrazitMenu();
            String volba = scanner.nextLine().trim();

            switch (volba) {
                case "1" -> pridatZamestnance();
                case "2" -> vypsatVsechny();
                case "3" -> vypsatPodleSkupiny();
                case "4" -> pridatSpolupraci();
                case "5" -> zobrazitSpoluprace();
                case "6" -> najdiSpolecneSpolupracovniky();
                case "7" -> vypoctiRizikoveSkore();
                case "8" -> odstranirZamestnance();
                case "9" -> vyhledatZamestnance();    
                case "0" -> {
                    System.out.println("Ukončuji aplikaci...");
                    bezi = false;
                }
                default -> System.out.println("Neplatná volba, zkuste to znovu.");
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void zobrazitMenu() {
        System.out.println("--- Hlavní nabídka ---");
        System.out.println("1. Přidat zaměstnance");
        System.out.println("2. Vypsat všechny zaměstnance");
        System.out.println("3. Vypsat zaměstnance podle skupiny");
        System.out.println("4. Přidat spolupráci mezi zaměstnanci");
        System.out.println("5. Zobrazit spolupráce zaměstnance");
        System.out.println("6. Najít společné spolupracovníky (Datový analytik)");
        System.out.println("7. Vypočítat rizikové skóre (Bezpečnostní specialista)");
        System.out.println("8. Odstranit zaměstnance");
        System.out.println("9. Vyhledat zaměstnance podle ID");
        System.out.println("0. Ukončit");
        System.out.print("Vaše volba: ");
    }

    private static void pridatZamestnance() {
        System.out.println("Typ zaměstnance:");
        System.out.println("1. Datový analytik");
        System.out.println("2. Bezpečnostní specialista");
        System.out.print("Volba: ");
        String typ = scanner.nextLine().trim();

        System.out.print("Jméno: ");
        String jmeno = scanner.nextLine().trim();

        System.out.print("Příjmení: ");
        String prijmeni = scanner.nextLine().trim();

        System.out.print("Rok narození: ");
        int rokNarozeni;
        try {
            rokNarozeni = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatný rok narození.");
            return;
        }

        Zamestnanec zamestnanec;
        switch (typ) {
            case "1" -> zamestnanec = new DataAnalyst(jmeno, prijmeni, rokNarozeni);
            case "2" -> zamestnanec = new SecuritySpecialist(jmeno, prijmeni, rokNarozeni);
            default -> {
                System.out.println("Chyba: Neplatný typ zaměstnance.");
                return;
            }
        }

        databaze.pridatZamestnance(zamestnanec);
        System.out.printf("Zaměstnanec přidán: %s (ID: %d)%n", zamestnanec, zamestnanec.getId());
    }
    
    private static void vypsatVsechny() {
        List<Zamestnanec> seznam = databaze.vsichniZamestnanci();
        if (seznam.isEmpty()) {
            System.out.println("Databáze je prázdná.");
            return;
        }

        System.out.printf("--- Všichni zaměstnanci --- (%d)%n", seznam.size());
        for (Zamestnanec z : seznam) {
            System.out.printf("  ID: %d | %s %s | Skupiny: %s | Rok: %d | Spolupráce: %d%n",
                    z.getId(), z.getJmeno(), z.getPrijmeni(), z.getSkupina(), z.getRokNarozeni(), z.getSpoluprace().size());
        }
    }

    private static void vypsatPodleSkupiny() {
        System.out.println("Vyberte skupinu:");
        System.out.println("1. Datový analytik");
        System.out.println("2. Bezpečnostní specialista");
        System.out.print("Volba: ");
        String volba = scanner.nextLine().trim();

        String skupina;
        switch (volba) {
            case "1" -> skupina = "Datový analytik";
            case "2" -> skupina = "Bezpečnostní specialista";
            default -> {
                System.out.println("Chyba: Neplatná volba.");
                return;
            }
        }

        List<Zamestnanec> seznam = databaze.zamestnanciPodleSkupiny(skupina);
        if (seznam.isEmpty()) {
            System.out.printf("Žádní zaměstnanci ve skupině '%s'.%n", skupina);
            return;
        }

        System.out.printf("--- Skupina: %s --- (%d)%n", skupina, seznam.size());
        for (Zamestnanec z : seznam) {
            System.out.printf("  ID: %d | %s %s | Rok: %d | Spolupráce: %d%n",
                    z.getId(), z.getJmeno(), z.getPrijmeni(), z.getRokNarozeni(), z.getSpoluprace().size());
        }
    }
    
    private static void pridatSpolupraci() {
        System.out.print("ID prvního zaměstnance: ");
        int id1;
        try {
            id1 = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        System.out.print("ID druhého zaměstnance: ");
        int id2;
        try {
            id2 = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        if (id1 == id2) {
            System.out.println("Chyba: Zaměstnanec se nemůže spolupracovat sám se sebou.");
            return;
        }

        System.out.println("Úroveň spolupráce:");
        System.out.println("1. Špatná");
        System.out.println("2. Průměrná");
        System.out.println("3. Dobrá");
        System.out.print("Volba: ");
        String volbaUrovne = scanner.nextLine().trim();

        UrovenSpoluprace uroven;
        switch (volbaUrovne) {
            case "1" -> uroven = UrovenSpoluprace.SPATNA;
            case "2" -> uroven = UrovenSpoluprace.PRUMERNA;
            case "3" -> uroven = UrovenSpoluprace.DOBRA;
            default -> {
                System.out.println("Chyba: Neplatná úroveň.");
                return;
            }
        }

        if (databaze.pridatSpolupraci(id1, id2, uroven)) {
            System.out.println("Spolupráce úspěšně přidána.");
        } else {
            System.out.println("Chyba: Zaměstnanec s daným ID nebyl nalezen.");
        }
    }

    private static void zobrazitSpoluprace() {
        System.out.print("ID zaměstnance: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        Zamestnanec zamestnanec = databaze.najitPodleId(id);
        if (zamestnanec == null) {
            System.out.println("Chyba: Zaměstnanec nenalezen.");
            return;
        }

        List<Spoluprace> spoluprace = zamestnanec.getSpoluprace();
        if (spoluprace.isEmpty()) {
            System.out.printf("Zaměstnanec %s %s nemá žádné spolupráce.%n",
                    zamestnanec.getJmeno(), zamestnanec.getPrijmeni());
            return;
        }

        System.out.printf("--- Spolupráce: %s %s (ID: %d) --- (%d)%n",
                zamestnanec.getJmeno(), zamestnanec.getPrijmeni(), id, spoluprace.size());
        for (Spoluprace s : spoluprace) {
            System.out.printf("  ID: %d | %s %s - úroveň: %s%n",
                    s.getSpolupracovnik().getId(),
                    s.getSpolupracovnik().getJmeno(),
                    s.getSpolupracovnik().getPrijmeni(),
                    s.getUroven());
        }
    }

    private static void najdiSpolecneSpolupracovniky() {
        System.out.print("ID prvního datového analytika: ");
        int id1;
        try {
            id1 = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        System.out.print("ID druhého zaměstnance: ");
        int id2;
        try {
            id2 = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        Zamestnanec z1 = databaze.najitPodleId(id1);
        Zamestnanec z2 = databaze.najitPodleId(id2);

        if (z1 == null || z2 == null) {
            System.out.println("Chyba: Zaměstnanec nenalezen.");
            return;
        }

        if (!(z1 instanceof DataAnalyst analytik)) {
            System.out.println("Chyba: První zaměstnanec není datový analytik.");
            return;
        }

        List<Zamestnanec> spolecni = analytik.najdiSpolecneSpolupracovniky(z2);
        if (spolecni.isEmpty()) {
            System.out.println("Žádní společní spolupracovníci nebyli nalezeni.");
            return;
        }

        System.out.printf("--- Společní spolupracovníci mezi %s (ID: %d) a %s (ID: %d) --- (%d)%n",
                z1.getJmeno(), z1.getId(), z2.getJmeno(), z2.getId(), spolecni.size());
        for (Zamestnanec z : spolecni) {
            System.out.printf("  ID: %d | %s %s | Skupiny: %s%n",
                    z.getId(), z.getJmeno(), z.getPrijmeni(), z.getSkupina());
        }
    }

    private static void vypoctiRizikoveSkore() {
        System.out.print("ID bezpečnostního specialisty: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        Zamestnanec zamestnanec = databaze.najitPodleId(id);
        if (zamestnanec == null) {
            System.out.println("Chyba: Zaměstnanec nenalezen.");
            return;
        }

        if (!(zamestnanec instanceof SecuritySpecialist specialista)) {
            System.out.println("Chyba: Zaměstnanec není bezpečnostní specialista.");
            return;
        }

        double skore = specialista.vypoctiRizikoveSkore();
        System.out.printf("Rizikové skóre pro %s %s: %.2f%n",
                specialista.getJmeno(), specialista.getPrijmeni(), skore);
    }

    private static void odstranirZamestnance() {
        System.out.print("ID zaměstnance k odstranění: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        Zamestnanec z = databaze.najitPodleId(id);
        if (z == null) {
            System.out.println("Chyba: Zaměstnanec nenalezen.");
            return;
        }

        System.out.printf("Jste si jistí, že chcete odstranit %s %s (ID: %d)? (ano/ne): ",
                z.getJmeno(), z.getPrijmeni(), z.getId());
        String potvrzeni = scanner.nextLine().trim();

        if ("ano".equalsIgnoreCase(potvrzeni)) {
            if (databaze.odstranit(id)) {
                System.out.println("Zaměstnanec byl odstraněn včetně všech vazeb.");
            } else {
                System.out.println("Chyba: Zaměstnance se nepodařilo odstranit.");
            }
        } else {
            System.out.println("Operace byla zrušena.");
        }
    }

    private static void vyhledatZamestnance() {
        System.out.print("ID zaměstnance: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        Map<String, Object> stat = databaze.ziskejStatistikyZamestnance(id);
        if (stat == null) {
            System.out.println("Chyba: Zaměstnanec nenalezen.");
            return;
        }

        System.out.println("--- Detaily zaměstnance ---");
        System.out.printf("ID: %d%n", stat.get("id"));
        System.out.printf("Jméno: %s %s%n", stat.get("jmeno"), stat.get("prijmeni"));
        System.out.printf("Skupiny: %s%n", stat.get("skupina"));
        System.out.printf("Rok narození: %d%n", stat.get("rokNarozeni"));
        System.out.printf("Počet spolupráce: %d%n", stat.get("pocetSpoluprace"));

        @SuppressWarnings("unchecked")
        Map<UrovenSpoluprace, Integer> pocty = (Map<UrovenSpoluprace, Integer>) stat.get("poctyUroven");
        if (!pocty.isEmpty()) {
            System.out.println("Spolupráce podle úrovně:");
            for (Map.Entry<UrovenSpoluprace, Integer> entry : pocty.entrySet()) {
                System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
            }
        }

        if (stat.containsKey("prumerSkore")) {
            System.out.printf("Průměrné skóre: %.2f%n", stat.get("prumerSkore"));
        }
    }
}

