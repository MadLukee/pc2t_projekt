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
}
