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

    private JTextField nomeField = new JTextField(10);
    private JTextField racaField = new JTextField(10);
    private JComboBox<String> tipoBox = new JComboBox<>(new String[]{"Cachorro", "Gato", "Papagaio", "Hamster"});

    private JTextArea output = new JTextArea(5, 20);

    public VirtualPetGUI() {
        setTitle("Virtual Pet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        buildCreatePanel();
        buildInteractPanel();

        add(panelCreate, "Create");
        add(panelInteract, "Interact");

        loadPets();

        showPanel("Create");

        pack();
        setVisible(true);
    }

    private void buildCreatePanel() {
        panelCreate.setLayout(new GridLayout(5, 2));
        panelCreate.add(new JLabel("Tipo:"));
        panelCreate.add(tipoBox);
        panelCreate.add(new JLabel("Nome:"));
        panelCreate.add(nomeField);
        panelCreate.add(new JLabel("Raça:"));
        panelCreate.add(racaField);

        JButton criarBtn = new JButton("Criar Pet");
        criarBtn.addActionListener(e -> criarPet());

        panelCreate.add(criarBtn);
    }

    private void buildInteractPanel() {
        panelInteract.setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        JButton falarBtn = new JButton("Falar");
        JButton carinhoBtn = new JButton("Carinho");
        JButton petiscoBtn = new JButton("Petisco");

        falarBtn.addActionListener(e -> interagir("falar"));
        carinhoBtn.addActionListener(e -> interagir("carinho"));
        petiscoBtn.addActionListener(e -> interagir("petisco"));

        buttons.add(falarBtn);
        buttons.add(carinhoBtn);
        buttons.add(petiscoBtn);

        panelInteract.add(buttons, BorderLayout.NORTH);
        panelInteract.add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void criarPet() {
        String tipo = (String) tipoBox.getSelectedItem();
        String nome = nomeField.getText();
        String raca = racaField.getText();

        if (nome.isEmpty() || raca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        switch (tipo) {
            case "Cachorro":
                currentPet = new Cachorro(nome, raca);
                break;
            case "Gato":
                currentPet = new Gato(nome, raca);
                break;
            case "Papagaio":
                currentPet = new Papagaio(nome, raca);
                break;
            case "Hamster":
                currentPet = new Hamster(nome, raca);
                break;
        }

        pets.add(currentPet);
        savePets();
        showPanel("Interact");
    }

    private void interagir(String acao) {
        try {
            if (currentPet == null) throw new PetException("Nenhum pet selecionado.");
            String res = "";
            if (acao.equals("falar")) res = currentPet.falar();
            if (acao.equals("carinho")) res = currentPet.carinho();
            if (acao.equals("petisco")) res = currentPet.petisco();
            output.append(res + "\n");
        } catch (PetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), name);
    }

    private void savePets() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("pets.txt"))) {
            for (Pet p : pets) {
                writer.println(p.getClass().getSimpleName() + ";" + p.getNome() + ";" + p.getRaca());
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
                if (parts.length == 3) {
                    String tipo = parts[0];
                    String nome = parts[1];
                    String raca = parts[2];
                    switch (tipo) {
                        case "Cachorro":
                            pets.add(new Cachorro(nome, raca));
                            break;
                        case "Gato":
                            pets.add(new Gato(nome, raca));
                            break;
                        case "Papagaio":
                            pets.add(new Papagaio(nome, raca));
                            break;
                        case "Hamster":
                            pets.add(new Hamster(nome, raca));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            // Se não houver arquivo, tudo bem.
        }
    }
}
