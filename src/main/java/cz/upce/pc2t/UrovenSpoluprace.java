package cz.upce.pc2t;

public enum UrovenSpoluprace {
    SPATNA("Špatná"),
    PRUMERNA("Průměrná"),
    DOBRA("Dobrá");

    private final String popis;

    UrovenSpoluprace(String popis) {
        this.popis = popis;
    }

    public String getPopis() {
        return popis;
    }

    @Override
    public String toString() {
        return popis;
    }
}
