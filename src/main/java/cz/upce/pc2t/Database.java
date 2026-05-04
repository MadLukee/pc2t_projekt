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
}
