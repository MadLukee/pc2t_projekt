package cz.upce.pc2t;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileIO {

    private static final String DELIMITER = "||";

    public static boolean ulozitZamestnance(String filePath, Zamestnanec zamestnanec) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath();
            Files.createDirectories(path.getParent());

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
                String radka = formatujZamestnance(zamestnanec);
                writer.println(radka);
                writer.flush();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Chyba při uložení zaměstnance: " + e.getMessage());
            return false;
        }
    }

    public static int nacistZamestnance(String filePath, DatabazZamestnancu databaze) {
        int pocet = 0;
        Path path = Paths.get(filePath).toAbsolutePath();
        File soubor = path.toFile();

        if (!soubor.exists()) {
            System.out.printf("Soubor neexistuje: %s%n", path);
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(soubor))) {
            String radka;
            while ((radka = reader.readLine()) != null) {
                if (radka.trim().isEmpty()) {
                    continue;
                }
                Zamestnanec z = parsujZamestnance(radka);
                if (z != null && databaze.pridatZamestnance(z)) {
                    pocet++;
                }
            }
        } catch (IOException e) {
            System.out.println("Chyba při čtení souboru: " + e.getMessage());
        }

        return pocet;
    }

    public static int ulozitVsechny(String filePath, DatabazZamestnancu databaze) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath();
            Files.createDirectories(path.getParent());

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
                List<Zamestnanec> seznam = databaze.vsichniZamestnanci();
                for (Zamestnanec z : seznam) {
                    String radka = formatujZamestnance(z);
                    writer.println(radka);
                }
                writer.flush();
                System.out.printf("Soubor vytvořen: %s%n", path);
                return seznam.size();
            }
        } catch (IOException e) {
            System.out.println("Chyba při uložení: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private static String formatujZamestnance(Zamestnanec z) {
        String typ = (z instanceof DataAnalyst) ? "DA" : "SS";
        return typ + DELIMITER + z.getId() + DELIMITER + z.getJmeno() + DELIMITER + z.getPrijmeni() + DELIMITER + z.getRokNarozeni();
    }

    private static Zamestnanec parsujZamestnance(String radka) {
        try {
            String[] casti = radka.split("\\" + DELIMITER);
            if (casti.length != 5) {
                System.out.printf("Varování: Řádek má špatný formát (očekáváno 5 polí, nalezeno %d): %s%n", casti.length, radka);
                return null;
            }

            String typ = casti[0].trim();
            int id = Integer.parseInt(casti[1].trim());
            String jmeno = casti[2].trim();
            String prijmeni = casti[3].trim();
            int rokNarozeni = Integer.parseInt(casti[4].trim());

            if ("DA".equals(typ)) {
                return new DataAnalyst(id, jmeno, prijmeni, rokNarozeni);
            } else if ("SS".equals(typ)) {
                return new SecuritySpecialist(id, jmeno, prijmeni, rokNarozeni);
            } else {
                System.out.printf("Varování: Neznámý typ zaměstnance: %s%n", typ);
            }
        } catch (Exception e) {
            System.out.println("Chyba při parsování řádku: " + e.getMessage());
        }
        return null;
    }
}
