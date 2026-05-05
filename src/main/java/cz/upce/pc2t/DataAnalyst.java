package cz.upce.pc2t;

import java.util.HashSet;
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

 public Zamestnanec najdiSpolupracovnikaSNejvicSpolecnymi() {
        if (getSpoluprace().isEmpty()) {
            return null;
        }
        Zamestnanec nejlepsi = null;
        int maxPocet = 0;
        for (Spoluprace s : getSpoluprace()) {
            Zamestnanec kolega = s.getSpolupracovnik();
            int pocet = pocetSpolecnychSpolupracovniku(kolega);
            if (pocet > maxPocet) {
                maxPocet = pocet;
                nejlepsi = kolega;
            }
        }
        return nejlepsi;
    }

     public int pocetSpolecnychSpolupracovniku(Zamestnanec druhy) {
        Set<Integer> mojeIds = new HashSet<>();
        for (Spoluprace s : getSpoluprace()) {
            mojeIds.add(s.getSpolupracovnik().getId());
        }

        Set<Integer> zapocitano = new HashSet<>();
        int pocet = 0;
        for (Spoluprace s : druhy.getSpoluprace()) {
            int id = s.getSpolupracovnik().getId();
            if (id != this.getId() && mojeIds.contains(id) && zapocitano.add(id)) {
                pocet++;
            }
        }
        return pocet;
    }
}
