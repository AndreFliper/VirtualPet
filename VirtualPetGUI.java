import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class VirtualPetGUI extends JFrame {
    private ArrayList<Pet> pets = new ArrayList<>();
    private Pet currentPet;

    private JPanel panelCreate = new JPanel();
    private JPanel panelInteract = new JPanel();
    private JPanel panelMenu = new JPanel();

    private JTextField nomeField = new JTextField(10);
    private JTextField racaField = new JTextField(10);
    private JTextField imagemField = new JTextField(10);
    private JButton escolherImagemBtn = new JButton("Escolher Imagem");
    private JComboBox<String> tipoBox = new JComboBox<>(new String[]{"Cachorro", "Gato", "Papagaio", "Hamster"});

    private JTextArea output = new JTextArea(5, 20);

    private JLabel petImageLabel = new JLabel();
    private JProgressBar happinessBar = new JProgressBar(0, 100);
    private JProgressBar dirtinessBar = new JProgressBar(0, 100);

    private Timer timer;

    public VirtualPetGUI() {
        setTitle("Virtual Pet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        buildCreatePanel();
        buildInteractPanel();
        buildMenuPanel();

        add(panelCreate, "Create");
        add(panelInteract, "Interact");
        add(panelMenu, "Menu");

        loadPets();
        showPanel("Menu");

        pack();
        setVisible(true);
    }

    private void escolherImagem() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            imagemField.setText(selected.getAbsolutePath());
        }
    }

    private void buildCreatePanel() {
        panelCreate.setLayout(new GridLayout(7, 2));

        panelCreate.add(new JLabel("Tipo:"));
        panelCreate.add(tipoBox);
        panelCreate.add(new JLabel("Nome:"));
        panelCreate.add(nomeField);
        panelCreate.add(new JLabel("Raça:"));
        panelCreate.add(racaField);
        panelCreate.add(new JLabel("Imagem:"));
        panelCreate.add(imagemField);

        escolherImagemBtn.addActionListener(e -> escolherImagem());
        panelCreate.add(new JLabel());
        panelCreate.add(escolherImagemBtn);

        JButton criarBtn = new JButton("Criar Pet");
        criarBtn.addActionListener(e -> criarPet());
        JButton voltarBtn = new JButton("Voltar");
        voltarBtn.addActionListener(e -> showPanel("Menu"));

        panelCreate.add(criarBtn);
        panelCreate.add(voltarBtn);
    }

    private void buildMenuPanel() {
        panelMenu.setLayout(new BorderLayout());

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JButton criarNovo = new JButton("Criar Novo Pet");
        criarNovo.addActionListener(e -> showPanel("Create"));

        panelMenu.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        panelMenu.add(criarNovo, BorderLayout.SOUTH);

        // Atualiza lista sempre que abrir
        panelMenu.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                listPanel.removeAll();
                for (Pet pet : pets) {
                    JButton btn = new JButton(pet.getNome());
                    btn.addActionListener(a -> {
                        currentPet = pet;
                        showPanel("Interact");
                        updatePetImage();
                    });
                    listPanel.add(btn);
                }
                listPanel.revalidate();
                listPanel.repaint();
            }
        });
    }

    private void buildInteractPanel() {
        panelInteract.setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        JButton falarBtn = new JButton("Falar");
        JButton brincarBtn = new JButton("Brincar");
        JButton petiscoBtn = new JButton("Petisco");
        JButton passearBtn = new JButton("Passear");
        JButton banhoBtn = new JButton("Dar Banho");
        JButton voltarBtn = new JButton("Voltar");

        falarBtn.addActionListener(e -> interagir("falar"));
        brincarBtn.addActionListener(e -> interagir("brincar"));
        petiscoBtn.addActionListener(e -> interagir("petisco"));
        passearBtn.addActionListener(e -> interagir("passear"));
        banhoBtn.addActionListener(e -> interagir("banho"));
        voltarBtn.addActionListener(e -> {
            if (timer != null) timer.stop();
            showPanel("Menu");
        });

        buttons.add(falarBtn);
        buttons.add(brincarBtn);
        buttons.add(petiscoBtn);
        buttons.add(passearBtn);
        buttons.add(banhoBtn);
        buttons.add(voltarBtn);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(petImageLabel);
        rightPanel.add(new JLabel("Felicidade:"));
        rightPanel.add(happinessBar);
        rightPanel.add(new JLabel("Sujeira:"));
        rightPanel.add(dirtinessBar);

        panelInteract.add(buttons, BorderLayout.NORTH);
        panelInteract.add(new JScrollPane(output), BorderLayout.CENTER);
        panelInteract.add(rightPanel, BorderLayout.EAST);

        happinessBar.setValue(100);
        dirtinessBar.setValue(0);
    }

    private void criarPet() {
        String tipo = (String) tipoBox.getSelectedItem();
        String nome = nomeField.getText();
        String raca = racaField.getText();
        String imagem = imagemField.getText();

        if (nome.isEmpty() || raca.isEmpty() || imagem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos, incluindo a imagem.");
            return;
        }

        switch (tipo) {
            case "Cachorro":
                currentPet = new Cachorro(nome, raca, imagem);
                break;
            case "Gato":
                currentPet = new Gato(nome, raca, imagem);
                break;
            case "Papagaio":
                currentPet = new Papagaio(nome, raca, imagem);
                break;
            case "Hamster":
                currentPet = new Hamster(nome, raca, imagem);
                break;
        }

        pets.add(currentPet);
        savePets();
        showPanel("Menu");
    }

    private void interagir(String acao) {
        try {
            if (currentPet == null) throw new PetException("Nenhum pet selecionado.");
            String res = "";
            switch (acao) {
                case "falar": res = currentPet.falar(); break;
                case "brincar": res = currentPet.carinho(); updateHappiness(100); break;
                case "petisco": res = currentPet.petisco(); break;
                case "passear": res = currentPet.carinho(); updateHappiness(100); break;
                case "banho": res = currentPet.getNome() + " tomou um banho!"; dirtinessBar.setValue(0); break;
            }
            output.append(res + "\n");
        } catch (PetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updateHappiness(int amount) {
        happinessBar.setValue(Math.min(100, amount));
    }

    private void updatePetImage() {
        if (currentPet != null) {
            ImageIcon icon = new ImageIcon(currentPet.getImagem());
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            petImageLabel.setIcon(new ImageIcon(img));
            happinessBar.setValue(100);
            dirtinessBar.setValue(0);

            if (timer != null) timer.stop();

            timer = new Timer(1000, e -> updateBars());
            timer.start();
        }
    }

    private void updateBars() {
        int happiness = happinessBar.getValue();
        int dirtiness = dirtinessBar.getValue();

        happiness = Math.max(0, happiness - 1);
        dirtiness = Math.min(100, dirtiness + 2);

        happinessBar.setValue(happiness);
        dirtinessBar.setValue(dirtiness);

        happinessBar.setForeground(getGradientColor(happiness));
        dirtinessBar.setForeground(getGradientColor(100 - dirtiness));
    }

    private Color getGradientColor(int value) {
        int r = (int) Math.min(255, 255 * (100 - value) / 100.0);
        int g = (int) Math.min(255, 255 * (value) / 100.0);
        return new Color(r, g, 0);
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), name);
    }

    private void savePets() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("pets.txt"))) {
            for (Pet p : pets) {
                writer.println(p.getClass().getSimpleName() + ";" + p.getNome() + ";" + p.getRaca() + ";" + p.getImagem());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPets() {
        try (BufferedReader reader = new BufferedReader(new FileReader("pets.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    String tipo = parts[0];
                    String nome = parts[1];
                    String raca = parts[2];
                    String imagem = parts[3];

                    switch (tipo) {
                        case "Cachorro":
                            pets.add(new Cachorro(nome, raca, imagem));
                            break;
                        case "Gato":
                            pets.add(new Gato(nome, raca, imagem));
                            break;
                        case "Papagaio":
                            pets.add(new Papagaio(nome, raca, imagem));
                            break;
                        case "Hamster":
                            pets.add(new Hamster(nome, raca, imagem));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            // tudo bem se não existir arquivo
        }
    }

    public static void main(String[] args) {
        new VirtualPetGUI();
    }
}
