package cz.upce.pc2t;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

     public static boolean ulozitZamestnance(Zamestnanec zamestnanec) {
        String sql = "INSERT INTO zamestnanci (id, jmeno, prijmeni, rokNarozeni, typ) VALUES (?, ?, ?, ?, ?)";
        String typ = (zamestnanec instanceof DataAnalyst) ? "DA" : "SS";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zamestnanec.getId());
            pstmt.setString(2, zamestnanec.getJmeno());
            pstmt.setString(3, zamestnanec.getPrijmeni());
            pstmt.setInt(4, zamestnanec.getRokNarozeni());
            pstmt.setString(5, typ);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Chyba při ukládání zaměstnance: " + e.getMessage());
            return false;
        }
    }

    public static int ulozitVsechny(DatabazZamestnancu databaze) {
        System.out.println("\n=== Uložení dat do SQL databáze ===");
        System.out.printf("V paměti je: %d zaměstnanců%n", databaze.pocetZamestnancu());

        List<Zamestnanec> seznam = databaze.vsichniZamestnanci();
        System.out.println("V paměti:");
        for (Zamestnanec z : seznam) {
            System.out.printf("  ID %d: %s %s%n", z.getId(), z.getJmeno(), z.getPrijmeni());
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA journal_mode=DELETE");
            stmt.execute("PRAGMA foreign_keys=ON");
            stmt.execute("PRAGMA synchronous=FULL");

            conn.setAutoCommit(false);

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM spoluprace");
            int vymazanoSpoluprace = 0;
            if (rs1.next()) {
                vymazanoSpoluprace = rs1.getInt(1);
            }

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM zamestnanci");
            int vymazanoZamestnanci = 0;
            if (rs2.next()) {
                vymazanoZamestnanci = rs2.getInt(1);
            }

            System.out.printf("V SQL bylo: %d spolupráce, %d zaměstnanců%n", vymazanoSpoluprace, vymazanoZamestnanci);

            int delSpoluprace = stmt.executeUpdate("DELETE FROM spoluprace");
            System.out.printf("Smazáno spolupráce: %d%n", delSpoluprace);

            int delZamestnanci = stmt.executeUpdate("DELETE FROM zamestnanci");
            System.out.printf("Smazáno zaměstnanců: %d%n", delZamestnanci);

            String sqlInsertZam = "INSERT INTO zamestnanci (id, jmeno, prijmeni, rokNarozeni, typ) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertZam)) {
                for (Zamestnanec z : seznam) {
                    String typ = (z instanceof DataAnalyst) ? "DA" : "SS";
                    pstmt.setInt(1, z.getId());
                    pstmt.setString(2, z.getJmeno());
                    pstmt.setString(3, z.getPrijmeni());
                    pstmt.setInt(4, z.getRokNarozeni());
                    pstmt.setString(5, typ);
                    pstmt.addBatch();
                }
                int[] results = pstmt.executeBatch();
                System.out.printf("Vloženo zaměstnanců: %d%n", results.length);
                for (Zamestnanec z : seznam) {
                    System.out.printf("  ✓ Uložen: ID %d - %s %s%n", z.getId(), z.getJmeno(), z.getPrijmeni());
                }
            }

            String sqlSpoluprace = "INSERT INTO spoluprace (idZamestnance1, idZamestnance2, uroven) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlSpoluprace)) {
                for (Zamestnanec z : seznam) {
                    for (Spoluprace s : z.getSpoluprace()) {
                        int id1 = z.getId();
                        int id2 = s.getSpolupracovnik().getId();
                        if (id1 < id2) {
                            pstmt.setInt(1, id1);
                            pstmt.setInt(2, id2);
                            pstmt.setString(3, s.getUroven().name());
                            pstmt.addBatch();
                        }
                    }
                }
                pstmt.executeBatch();
                System.out.println("✓ Spolupráce vloženy");
            }

            conn.commit();
            System.out.println("✓ Transakce COMMIT");

            try (ResultSet rsCheck = stmt.executeQuery("SELECT COUNT(*) FROM zamestnanci")) {
                int dbCount = 0;
                if (rsCheck.next()) {
                    dbCount = rsCheck.getInt(1);
                }
                System.out.printf("OVĚŘENÍ: V databázi je nyní %d zaměstnanců%n", dbCount);
            }

        } catch (SQLException e) {
            System.out.println("❌ CHYBA při ukládání: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }

        try (Connection vacuumConn = DriverManager.getConnection(DB_URL);
             Statement vacuumStmt = vacuumConn.createStatement()) {
            vacuumStmt.execute("VACUUM");
            System.out.println("✓ VACUUM - data zkompaktována");
        } catch (SQLException e) {
            System.out.println("Chyba při VACUUM: " + e.getMessage());
        }

        System.out.println("=== Uložení HOTOVO ===\n");
        return seznam.size();
    }

    private static int ulozitVsechySpoluprace(DatabazZamestnancu databaze) {
        String sql = "INSERT INTO spoluprace (idZamestnance1, idZamestnance2, uroven) VALUES (?, ?, ?)";
        int pocet = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Zamestnanec z : databaze.vsichniZamestnanci()) {
                for (Spoluprace s : z.getSpoluprace()) {
                    int id1 = z.getId();
                    int id2 = s.getSpolupracovnik().getId();
                    if (id1 < id2) {
                        pstmt.setInt(1, id1);
                        pstmt.setInt(2, id2);
                        pstmt.setString(3, s.getUroven().name());
                        pstmt.addBatch();
                        pocet++;
                    }
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("Chyba při ukládání spolupráce: " + e.getMessage());
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

     public static void vyprazdnit() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM spoluprace");
            stmt.execute("DELETE FROM zamestnanci");
        } catch (SQLException e) {
            System.out.println("Chyba při vymazávání dat: " + e.getMessage());
        }
    }
}
