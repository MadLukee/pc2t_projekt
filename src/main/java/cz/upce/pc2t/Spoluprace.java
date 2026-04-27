package cz.upce.pc2t;

public class Spoluprace {

    private final Zamestnanec spolupracovnik;
    private final UrovenSpoluprace uroven;

    public Spoluprace(Zamestnanec spolupracovnik, UrovenSpoluprace uroven) {
        this.spolupracovnik = spolupracovnik;
        this.uroven = uroven;
    }

    public Zamestnanec getSpolupracovnik() {
        return spolupracovnik;
    }

    public UrovenSpoluprace getUroven() {
        return uroven;
    }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %d) - úroveň: %s",
                spolupracovnik.getJmeno(),
                spolupracovnik.getPrijmeni(),
                spolupracovnik.getId(),
                uroven);
    }
}
