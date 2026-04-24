package cz.upce.pc2t;

public class SecuritySpecialist extends Zamestnanec {

    public SecuritySpecialist(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String getSkupina() {
        return "Bezpečnostní specialista";
    }

    @Override
    public String popisDovednosti() {
        return "Bezpečnostní analýza, výpočet rizikového skóre";
    }
}
