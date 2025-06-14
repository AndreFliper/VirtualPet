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

    private DefaultListModel<String> petListModel = new DefaultListModel<>();
    private JList<String> petList = new JList<>(petListModel);
    private JLabel petImage = new JLabel();

    public VirtualPetGUI() {
        setTitle("Virtual Pet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        buildCreatePanel();
        buildInteractPanel();
        buildMenuPanel();

        add(panelCreate, "Create");
        add(panelMenu, "Menu");
        add(panelInteract, "Interact");

        loadPets();
        updatePetList();

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
        panelCreate.add(criarBtn);

        JButton voltarBtn = new JButton("Voltar");
        voltarBtn.addActionListener(e -> showPanel("Menu"));
        panelCreate.add(voltarBtn);
    }

    private void buildMenuPanel() {
        panelMenu.setLayout(new BorderLayout());

        petList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petList.addListSelectionListener(e -> updateMenuPetImage());

        JButton criarNovoBtn = new JButton("Novo Pet");
        criarNovoBtn.addActionListener(e -> showPanel("Create"));

        JButton selecionarBtn = new JButton("Selecionar Pet");
        selecionarBtn.addActionListener(e -> {
            int index = petList.getSelectedIndex();
            if (index >= 0) {
                currentPet = pets.get(index);
                output.setText("");
                updatePetImage();
                showPanel("Interact");
            }
        });

        JButton excluirBtn = new JButton("Excluir Pet");
        excluirBtn.addActionListener(e -> {
            int index = petList.getSelectedIndex();
            if (index >= 0) {
                pets.remove(index);
                updatePetList();
                savePets();
                petImage.setIcon(null);
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(criarNovoBtn);
        buttons.add(selecionarBtn);
        buttons.add(excluirBtn);

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JScrollPane(petList), BorderLayout.CENTER);
        left.add(buttons, BorderLayout.SOUTH);

        panelMenu.add(left, BorderLayout.WEST);
        panelMenu.add(petImage, BorderLayout.CENTER);
    }

    private void buildInteractPanel() {
        panelInteract.setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        JButton falarBtn = new JButton("Falar");
        JButton carinhoBtn = new JButton("Carinho");
        JButton petiscoBtn = new JButton("Petisco");
        JButton voltarBtn = new JButton("Voltar");

        falarBtn.addActionListener(e -> interagir("falar"));
        carinhoBtn.addActionListener(e -> interagir("carinho"));
        petiscoBtn.addActionListener(e -> interagir("petisco"));
        voltarBtn.addActionListener(e -> showPanel("Menu"));

        buttons.add(falarBtn);
        buttons.add(carinhoBtn);
        buttons.add(petiscoBtn);
        buttons.add(voltarBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(buttons, BorderLayout.NORTH);
        top.add(petImage, BorderLayout.CENTER);

        panelInteract.add(top, BorderLayout.NORTH);
        panelInteract.add(new JScrollPane(output), BorderLayout.CENTER);
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
        updatePetList();
        showPanel("Menu");
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
            // Se não houver arquivo, tudo bem.
        }
    }

    private void updatePetList() {
        petListModel.clear();
        for (Pet p : pets) {
            petListModel.addElement(p.getNome() + " (" + p.getRaca() + ")");
        }
    }

    private void updateMenuPetImage() {
        int index = petList.getSelectedIndex();
        if (index >= 0) {
            Pet p = pets.get(index);
            petImage.setIcon(new ImageIcon(p.getImagem()));
        } else {
            petImage.setIcon(null);
        }
    }

    private void updatePetImage() {
        if (currentPet != null) {
            petImage.setIcon(new ImageIcon(currentPet.getImagem()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VirtualPetGUI::new);
    }
}
