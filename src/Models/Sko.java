package Models;

public class Sko {
    Kategori kategori;
    Märke märke;
    int id;
    int storlek;
    int pris;
    String färg;
    int märkesId;
    public Sko() {
    }

    public String toString() {
        return (märke != null ? märke.getMärkesnamn() : "") + ", " +
                (kategori != null ? kategori.getKategorinamn() : "") + ", " +
                storlek + ", " + färg + ", " + pris;
    }
    public void setMärke(Märke märke) {
        this.märke = märke;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStorlek() {
        return storlek;
    }

    public void setStorlek(int storlek) {
        this.storlek = storlek;
    }

    public int getPris() {
        return pris;
    }

    public void setPris(int pris) {
        this.pris = pris;
    }

    public String getFärg() {
        return färg;
    }

    public void setFärg(String färg) {
        this.färg = färg;
    }

    public int getMärkesId() {
        return märkesId;
    }

    public void setMärkesId(int märkesId) {
        this.märkesId = märkesId;
    }
}