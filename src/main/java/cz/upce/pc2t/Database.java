package cz.upce.pc2t;

import java.io.File;
import java.sql.*;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:zamestnanci.db";

     public static boolean inicializovat() {
        File dbFile = new File("zamestnanci.db");
        if (!dbFile.exists()) {
            new File("zamestnanci.db-journal").delete();
            new File("zamestnanci.db-wal").delete();
            new File("zamestnanci.db-shm").delete();
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

            stmt.execute("PRAGMA foreign_keys=ON");
     
            stmt.execute(sqlZamestnanci);
            stmt.execute(sqlSpoluprace);
            return true;
        } catch (SQLException e) {
             System.out.println("SQL záloha není dostupná: " + e.getMessage());
            return false;
        }
    }

    public static int nacistVsechny(DatabazZamestnancu databaze) {
        int pocet = 0;
        String sql = "SELECT id, jmeno, prijmeni, rokNarozeni, typ FROM zamestnanci";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            stmt.execute("PRAGMA foreign_keys=ON");

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
                }
            }

            nacistSpoluprace(databaze);

            if (pocet > 0) {
                databaze.obnoviPocitadloID();
            }

        } catch (SQLException e) {
            System.out.println("Chyba při načítání z SQL: " + e.getMessage());
            return 0;
        }

        return pocet;
    }

    public static int ulozitVsechny(DatabazZamestnancu databaze) {
        List<Zamestnanec> seznam = databaze.vsichniZamestnanci();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys=ON");
            
            conn.setAutoCommit(false);
            
            stmt.executeUpdate("DELETE FROM spoluprace");
            stmt.executeUpdate("DELETE FROM zamestnanci");
            
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
                pstmt.executeBatch();
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
            }
            
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Chyba při ukládání do SQL: " + e.getMessage());
            return 0;
        }

        return seznam.size();  
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
           System.out.println("Chyba při načítání spolupráce z SQL: " + e.getMessage());
        }
    }
}
