package cz.upce.pc2t;

public abstract class Zamestnanec {

    private static int dalsiId = 1;

    private final int id;
    private String jmeno;
    private String prijmeni;
    private int rokNarozeni;

    protected Zamestnanec(String jmeno, String prijmeni, int rokNarozeni) {
        this.id = dalsiId++;
        this.jmeno = kapitalizuj(jmeno);
        this.prijmeni = kapitalizuj(prijmeni);
        this.rokNarozeni = rokNarozeni;
    }
    public int getId() {
        return id;
    }

    public String getJmeno() {
        return jmeno;
    }
    
    public void setJmeno(String jmeno) {
        this.jmeno = kapitalizuj(jmeno);
    }

    public String getPrijmeni() {
        return prijmeni;
    }

     public void setPrijmeni(String prijmeni) {
        this.prijmeni = kapitalizuj(prijmeni);
    }


    public int getRokNarozeni() {
        return rokNarozeni;
    }

    public void setRokNarozeni(int rokNarozeni) {
        this.rokNarozeni = rokNarozeni;
    }
    
    public abstract String getSkupina();

    public abstract String popisDovednosti();

    private static String kapitalizuj(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s (nar. %d) - %s", id, jmeno, prijmeni, rokNarozeni, getSkupina());
    }
}
