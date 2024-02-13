import Models.Kategori;
import Models.Märke;
import Models.Sko;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class Repository {

    // Metod för att validera inloggningen
    public int validateLogin(String username, String password) throws IOException {
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT Id FROM Kund WHERE Namn = ? AND Lösenord = ?";
            try (PreparedStatement stm = c.prepareStatement(sql)) {
                stm.setString(1, username);
                stm.setString(2, password);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("Id"); // Returnera kundens ID om inloggningen är framgångsrik
                    } else {
                        return -1; // Returnera -1 om inloggningen misslyckades
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Problem med att validera inloggning");
        }
    }


    // Metod för att hämta kategorier från databasen till combobox
    public List<String> getCategories() throws IOException {
        List<String> categories = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT kategorinamn FROM kategori";
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            // Lägger till varje kategorirad i listan
            while (rs.next()) {
                categories.add(rs.getString("Kategorinamn"));
            }
            return categories;

        } catch (SQLException e) {
            throw new RuntimeException("Problem med att hämta kategorier från databasen ");
        } catch (IOException e) {
            throw new RuntimeException("Problem med att ansluta till databasen ");
        }
    }

    // Metod för att hämta märke från databasen
    public List<String> getBrands() throws IOException {
        List<String> brands = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT märkesnamn FROM märke";
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                brands.add(rs.getString("Märkesnamn"));
            }
            return brands;

        } catch (SQLException e) {
            throw new RuntimeException("Problem med att hämta märken från databasen ");
        } catch (IOException e) {
            throw new RuntimeException("Problem med att ansluta till databasen ");
        }
    }

    // Metod för att hämta färger från databasen
    public List<String> getColors() throws IOException {
        List<String> colors = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT DISTINCT färg FROM sko";
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                colors.add(rs.getString("Färg"));
            }
            return colors;

        } catch (SQLException e) {
            throw new RuntimeException("Problem med att hämta färger från databasen ");
        } catch (IOException e) {
            throw new RuntimeException("Problem med att ansluta till databasen ");
        }
    }

    // Hämtar märkesnamnet baserat på id
    public String getBrandsName(int brandId) {
        String brandName = null;
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT märkesnamn FROM märke WHERE id = ?";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setInt(1, brandId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                brandName = rs.getString("märkesnamn");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Problem med att hämta märkesnamn från databasen ");
        } catch (IOException e) {
            throw new RuntimeException("Problem med att ansluta till databasen ");
        }
        return brandName;
    }

    // Hämtar kategorinamnet baserat på id
    public String getCategoryName(int skoId) {
        String categoryName = null;
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT DISTINCT k.kategoriNamn FROM kategori k " +
                    "JOIN skokategori sk ON k.id = sk.kategoriId " +
                    "WHERE sk.skoId = ?";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setInt(1, skoId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categoryName = rs.getString("Kategorinamn");
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Problem med att hämta kategorinamn från databasen ");
        }
        return categoryName;
    }

    // Metod för att hämta skor som matchar valda kriterier från databasen
    public List<Sko> getMatchingShoes(String selectedCategory, String selectedBrand, String selectedColor) {
        // Skapar en lista för att lagra matchande skor
        List<Sko> matchingShoes = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "SELECT s.*, k.Kategorinamn, m.Märkesnamn " +
                    "FROM Sko s " +
                    "JOIN SkoKategori sk ON s.Id = sk.SkoId " + // Gör en inner join med tabellen skoKategori, baserat på SkoId
                    "JOIN Kategori k ON sk.KategoriId = k.Id " +
                    "JOIN Märke m ON s.MärkesId = m.Id " +
                    "WHERE (k.Kategorinamn = ? OR ? IS NULL) " + // Villkor för att välja kategorin baserat på användarens val, eller om användaren inte valt någon kategori
                    "AND (m.Märkesnamn = ? OR ? IS NULL) " +
                    "AND (s.Färg = ? OR ? IS NULL)";

            PreparedStatement pst = c.prepareStatement(sql);

            // Sätter värden för de parametrar som används i sql-frågan
            pst.setString(1, selectedCategory);
            pst.setString(2, selectedCategory); // Repeterar kategorinamnet för att hantera null värden
            pst.setString(3, selectedBrand);
            pst.setString(4, selectedBrand);
            pst.setString(5, selectedColor);
            pst.setString(6, selectedColor);

            ResultSet rs = pst.executeQuery();

            // Loopar igenom rs och skapat Sko-objekt för varje rad
            while (rs.next()) {
                Sko sko = new Sko();
                sko.setId(rs.getInt("Id"));
                sko.setStorlek(rs.getInt("Storlek"));
                sko.setPris(rs.getInt("Pris"));
                sko.setFärg(rs.getString("Färg"));

                // Skapar och sätter Kategori-objekt för skon
                Kategori kategori = new Kategori();
                kategori.setKategorinamn(rs.getString("Kategorinamn"));
                sko.setKategori(kategori);

                Märke märke = new Märke();
                märke.setMärkesnamn(rs.getString("Märkesnamn"));
                sko.setMärke(märke);

                // Lägger till den skapade skon i listan av matchande skor
                matchingShoes.add(sko);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem med att ansluta till databasen ");
        } catch (SQLException e) {
            throw new RuntimeException("Problem med att hämta matchande skor i databasen ");
        }
        // Returnerar listan av matchande skor
        return matchingShoes;
    }

    // Metod för att lägga till skor i kundvagnen
    public int addToCart(int customerId, int shoeId) {
        try (Connection c = DatabaseManager.getConnection()) {
            String sql = "call AddToCart(?,?)";

            CallableStatement stm = c.prepareCall(sql);

            // Sätter värdet för de två parametrarna i sp
            stm.setInt(1, customerId);
            stm.setInt(2, shoeId);

            // Utför anropet till den lagrade proceduren
            int rowsAffected = stm.executeUpdate();

            // Returnera antalet påverkade rader
            return rowsAffected;
        } catch (SQLException | IOException e) {
            System.out.println("SQL-fel: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
