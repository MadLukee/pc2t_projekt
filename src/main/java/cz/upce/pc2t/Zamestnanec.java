package cz.upce.pc2t;

public abstract class Zamestnanec {

    private static int dalsiId = 1;

    private final int id;
    private String jmeno;
    private String prijmeni;
    private int rokNarozeni;

    protected Zamestnanec(String jmeno, String prijmeni, int rokNarozeni) {
        this.id = dalsiId++;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
    }
    public int getId() {
        return id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public int getRokNarozeni() {
        return rokNarozeni;
    }

    public abstract String getSkupina();

    public abstract String popisDovednosti();

    @Override
    public String toString() {
        return String.format("[%d] %s %s (nar. %d) - %s", id, jmeno, prijmeni, rokNarozeni, getSkupina());
    }
}
