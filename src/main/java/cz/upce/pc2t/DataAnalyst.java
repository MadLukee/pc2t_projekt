package cz.upce.pc2t;

public class DataAnalyst extends Zamestnanec {

    public DataAnalyst(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String getSkupina() {
        return "Datový analytik";
    }

    @Override
    public String popisDovednosti() {
        return "Analýza dat, hledání společných spolupracovníků";
    }
}
