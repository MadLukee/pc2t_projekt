package cz.upce.pc2t;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataAnalyst extends Zamestnanec {

    public DataAnalyst(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    public DataAnalyst(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String getSkupina() {
        return "Datový analytik";
    }

    @Override
    public String popisDovednosti() {
        return "Analýza dat, hledání společných spolupracovníků";
    }

     public List<Zamestnanec> najdiSpolecneSpolupracovniky(Zamestnanec druhy) {
        Set<Integer> mojeIds = new HashSet<>();
        for (Spoluprace s : this.getSpoluprace()) {
            mojeIds.add(s.getSpolupracovnik().getId());
        }

        List<Zamestnanec> spolecni = new ArrayList<>();
        Set<Integer> pridani = new HashSet<>();
        for (Spoluprace s : druhy.getSpoluprace()) {
            int id = s.getSpolupracovnik().getId();
            if (mojeIds.contains(id) && pridani.add(id)) {
                spolecni.add(s.getSpolupracovnik());
            }
        }
        return spolecni;
    }
}
