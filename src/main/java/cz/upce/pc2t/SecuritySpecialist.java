package cz.upce.pc2t;

public class SecuritySpecialist extends Zamestnanec {

    public SecuritySpecialist(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    public SecuritySpecialist(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }
    
    @Override
    public String getSkupina() {
        return "Bezpečnostní specialista";
    }

    @Override
    public String popisDovednosti() {
        return "Bezpečnostní analýza, výpočet rizikového skóre";
    }

    public double vypoctiRizikoveSkore() {
        if (getSpoluprace().isEmpty()) {
            return 0.0;
        }

        double skore = 0.0;
        for (Spoluprace s : getSpoluprace()) {
            switch (s.getUroven()) {
                case SPATNA -> skore += 3.0;
                case PRUMERNA -> skore += 1.0;
                case DOBRA -> skore += 0.5;
            }
        }
        return skore / getSpoluprace().size();
    }
}
