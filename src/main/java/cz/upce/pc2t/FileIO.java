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
}
