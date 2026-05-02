package cz.upce.pc2t;

import java.util.Scanner;
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
}
