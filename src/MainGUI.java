import Models.Kategori;
import Models.Märke;
import Models.Sko;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainGUI {
    Repository r = new Repository();
    private JFrame frame;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel cardpanel;
    private CardLayout cardLayout;
    private String currentUsername;
    private JList<Sko> matchingShoesList = new JList<>();
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> brandComboBox;
    private JComboBox<String> colorComboBox;
    private List<Sko> shoppingCart = new ArrayList<>();
    private JList<Sko> shoppingCartList = new JList<>();
    private int customerId;
    Color transparentBlue = new Color(0, 76, 153, 70);
    public MainGUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        cardLayout = new CardLayout();
        cardpanel = new JPanel(cardLayout);

        frame.add(cardpanel);

        cardpanel.add(createLoginPanel(), "Login");
        cardpanel.add(createShoeSelectionPanel(), "ShoeSelection");

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    // Metod för att skapa inloggningspanelen
    private JPanel createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setBackground(transparentBlue);
        loginPanel.setLayout(null);
        frame.setTitle("Webbutik - Logga in");

        JLabel userLabel = new JLabel("Användarnamn: ");
        userLabel.setBounds(10, 20, 80, 25);
        loginPanel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        loginPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Lösenord: ");
        passwordLabel.setBounds(10, 50, 80, 25);
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Logga in");
        loginButton.setBounds(95, 90, 100, 25);
        loginPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] passwordChar = passwordField.getPassword();
            String password = new String(passwordChar);

            try {
                // Hämta kund-ID vid inloggning
                customerId = r.validateLogin(username, password);

                if (customerId != -1) {
                    currentUsername = username;
                    cardLayout.show(cardpanel, "ShoeSelection");
                    frame.setSize(800, 400);
                    frame.setLocationRelativeTo(null);
                    frame.setTitle("Webbutik - " + currentUsername);

                } else {
                    JOptionPane.showMessageDialog(null, "Fel användarnamn eller lösenord");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        });
        return loginPanel;
    }
        // Metod för att skapa skopanelen
        private JPanel createShoeSelectionPanel () {
            JPanel shoeSelectionPanel = new JPanel(new GridBagLayout());
            shoeSelectionPanel.setBackground(transparentBlue);

            try {
                // Lägger in kategorier, märken och färger i listor
                List<String> categories = r.getCategories();
                List<String> brands = r.getBrands();
                List<String> colors = r.getColors();

                // Skapar combobox
                categoryComboBox = new JComboBox<>();
                brandComboBox = new JComboBox<>();
                colorComboBox = new JComboBox<>();

                categoryComboBox.addItem("Alla");
                brandComboBox.addItem("Alla");
                colorComboBox.addItem("Alla");

                // Fyller comboboxarna med elementen från listorna
                categories.forEach(categoryComboBox::addItem);
                brands.forEach(brandComboBox::addItem);
                colors.forEach(colorComboBox::addItem);

                categoryComboBox.addActionListener(e -> updateMatchingShoes());
                brandComboBox.addActionListener(e -> updateMatchingShoes());
                colorComboBox.addActionListener(e -> updateMatchingShoes());

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1.0;

                shoeSelectionPanel.add(new JLabel(" Välj märke: "), gbc);

                gbc.gridx = 1;
                gbc.gridy = 0;
                shoeSelectionPanel.add(brandComboBox, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                shoeSelectionPanel.add(new JLabel(" Välj kategori: "), gbc);

                gbc.gridx = 1;
                gbc.gridy = 1;
                shoeSelectionPanel.add(categoryComboBox, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                shoeSelectionPanel.add(new JLabel(" Välj färg: "), gbc);

                gbc.gridx = 1;
                gbc.gridy = 2;
                shoeSelectionPanel.add(colorComboBox, gbc);

                matchingShoesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(matchingShoesList);
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                shoeSelectionPanel.add(scrollPane, gbc);

                // Lägger till varukorg panelen
                JPanel shoppingCartPanel = new JPanel(new BorderLayout());
                shoppingCartPanel.setBackground(transparentBlue);
                JPanel buttonpanel = new JPanel(new GridLayout(1, 2));
                JButton addToCartButton = new JButton("Lägg till");
                JButton closeButton = new JButton("Avsluta");
                buttonpanel.add(addToCartButton);
                buttonpanel.add(closeButton);
                shoppingCartPanel.add(new JLabel("Kundvagn"), BorderLayout.NORTH);
                shoppingCartPanel.add(new JScrollPane(shoppingCartList), BorderLayout.CENTER);
                shoppingCartPanel.add(buttonpanel, BorderLayout.SOUTH);
                gbc.gridx = 2;
                gbc.gridy = 0;
                gbc.gridheight = 4;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                shoeSelectionPanel.add(shoppingCartPanel, gbc);

                addToCartButton.addActionListener(e -> {
                    Sko selectedSko = matchingShoesList.getSelectedValue();
                    if (selectedSko != null) {
                        try {
                            int beställningsId = r.addToCart(customerId, selectedSko.getId());
                            if (beställningsId != -1) {
                                shoppingCart.add(selectedSko);
                                updateShoppingCart();
                                JOptionPane.showMessageDialog(null, "Tillagt i varukorgen");
                            } else {
                                JOptionPane.showMessageDialog(null, "Kunde inte lägga till skon i varukorgen");
                            }
                        } catch (RuntimeException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Vänligen välj en sko att lägga till i kundvagnen.");
                    }
                });

                closeButton.addActionListener(e -> System.exit(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return shoeSelectionPanel;
        }
        // Metod för att uppdatera listan över matchande skor baserat på användarens val
        private void updateMatchingShoes () {

            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            String selectedBrand = (String) brandComboBox.getSelectedItem();
            String selectedColor = (String) colorComboBox.getSelectedItem();

            try {
                // Skapar en lista för att lagra matchande skor baserat på användarens val av kategori, märke och färg.
                List<Sko> matchingShoes = r.getMatchingShoes(
                        // Om användaren väljer "Alla" för kategorin, skicka null som parameter för att ignorera kategorifiltret.
                        (selectedCategory.equals("Alla") ? null : selectedCategory),
                        (selectedBrand.equals("Alla") ? null : selectedBrand),
                        (selectedColor.equals("Alla") ? null : selectedColor));

                // För varje matchande sko, uppdatera märke och kategori
                matchingShoes.forEach(sko -> {
                    // Hämta och sätt märkesnamnet för skon
                    String märkesnamn = r.getBrandsName(sko.getMärkesId());
                    if (märkesnamn != null) {
                        Märke märke = new Märke();
                        märke.setMärkesnamn(märkesnamn);
                        sko.setMärke(märke);
                    }
                });

                matchingShoes.forEach(sko -> {
                    String categoryName = r.getCategoryName(sko.getId());
                    if (categoryName != null) {
                        Kategori kategori = new Kategori();
                        kategori.setKategorinamn(categoryName);
                        sko.setKategori(kategori);
                    }

                    // Skapar en modell för listen över matchande skor och sätter den som modell för GUI-listan
                    DefaultListModel<Sko> model = new DefaultListModel<>();
                    // Itererar över varje Sko-objekt i matchingShoes-listan och lägger till dem i modellen
                    matchingShoes.forEach(model::addElement);
                    // Sätter modellen som modell för matchingShoesList
                    matchingShoesList.setModel(model);
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // Metod för att uppdatera varukorgen och dess lista
        private void updateShoppingCart () {
            DefaultListModel<Sko> model = new DefaultListModel<>();
            // Lägger till varje objekt från shoppingCart-listan till modellen
            shoppingCart.forEach(model::addElement);
            // Sätter den nya modellen som modellen för shoppingCartList
            shoppingCartList.setModel(model);
        }


        public static void main (String[]args) throws IOException {
            MainGUI mainGUI = new MainGUI();
        }
    }
