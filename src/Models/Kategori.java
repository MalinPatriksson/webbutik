package Models;

public class Kategori {
    int id;
    String kategorinamn;

    public Kategori() {
    }
    public String getKategorinamn() {
        return kategorinamn;
    }
    public void setKategorinamn(String kategorinamn) {
        this.kategorinamn = kategorinamn;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

}