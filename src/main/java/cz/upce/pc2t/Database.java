package cz.upce.pc2t;

import java.io.File;
import java.sql.*;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:zamestnanci.db";

    public static void inicializovat() {
        File dbFile = new File("zamestnanci.db");
        if (!dbFile.exists()) {
            new File("zamestnanci.db-journal").delete();
            new File("zamestnanci.db-wal").delete();
            new File("zamestnanci.db-shm").delete();
            System.out.println("Vytváříme novou databázi...");
        }

        String sqlZamestnanci = "CREATE TABLE IF NOT EXISTS zamestnanci (" +
                "id INTEGER PRIMARY KEY, " +
                "jmeno TEXT NOT NULL, " +
                "prijmeni TEXT NOT NULL, " +
                "rokNarozeni INTEGER NOT NULL, " +
                "typ TEXT NOT NULL" +
                ")";

        String sqlSpoluprace = "CREATE TABLE IF NOT EXISTS spoluprace (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idZamestnance1 INTEGER NOT NULL, " +
                "idZamestnance2 INTEGER NOT NULL, " +
                "uroven TEXT NOT NULL, " +
                "FOREIGN KEY(idZamestnance1) REFERENCES zamestnanci(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(idZamestnance2) REFERENCES zamestnanci(id) ON DELETE CASCADE" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA journal_mode=DELETE");
            stmt.execute("PRAGMA foreign_keys=ON");
            stmt.execute("PRAGMA synchronous=FULL");

            stmt.execute(sqlZamestnanci);
            stmt.execute(sqlSpoluprace);
            System.out.printf("Databáze inicializována: %s%n", dbFile.getAbsolutePath());
        } catch (SQLException e) {
            System.out.println("Chyba při inicializaci databáze: " + e.getMessage());
            e.printStackTrace();
        }
    }

     public static int nacistVsechny(DatabazZamestnancu databaze) {
        int pocet = 0;
        String sql = "SELECT id, jmeno, prijmeni, rokNarozeni, typ FROM zamestnanci";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys=ON");

            System.out.println("=== Načítání z SQL databáze ===");

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String jmeno = rs.getString("jmeno");
                String prijmeni = rs.getString("prijmeni");
                int rokNarozeni = rs.getInt("rokNarozeni");
                String typ = rs.getString("typ");

                Zamestnanec z;
                if ("DA".equals(typ)) {
                    z = new DataAnalyst(id, jmeno, prijmeni, rokNarozeni);
                } else {
                    z = new SecuritySpecialist(id, jmeno, prijmeni, rokNarozeni);
                }

                if (databaze.pridatZamestnance(z)) {
                    pocet++;
                    System.out.printf("  ✓ Načten: ID %d - %s %s%n", z.getId(), z.getJmeno(), z.getPrijmeni());
                }
            }
            rs.close();

            System.out.printf("Načteno: %d zaměstnanců%n", pocet);

            nacistSpoluprace(databaze);

            if (pocet > 0) {
                databaze.obnoviPocitadloID();
            }

            System.out.println("=== Načítání dokončeno ===\n");

        } catch (SQLException e) {
            System.out.println("Chyba při načítání dat: " + e.getMessage());
            e.printStackTrace();
        }

        return pocet;
    }

    private static void nacistSpoluprace(DatabazZamestnancu databaze) {
        String sql = "SELECT idZamestnance1, idZamestnance2, uroven FROM spoluprace";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id1 = rs.getInt("idZamestnance1");
                int id2 = rs.getInt("idZamestnance2");
                String uroven = rs.getString("uroven");

                UrovenSpoluprace urov = UrovenSpoluprace.valueOf(uroven);
                databaze.pridatSpolupraci(id1, id2, urov);
            }
        } catch (SQLException e) {
            System.out.println("Chyba při načítání spolupráce: " + e.getMessage());
        }
    }
}
