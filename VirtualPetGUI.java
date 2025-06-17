import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;

//criação de classe  para a interface interativa
public class VirtualPetGUI extends JFrame {
    private ArrayList<Pet> pets = new ArrayList<>();
    private Pet currentPet;

    //criação das 3 telas
    private JPanel panelCreate = new JPanel();
    private JPanel panelSelect = new JPanel();
    private JPanel panelInteract = new JPanel();

    //criação dos campos visuais pra exibir atributos
    private JTextField nomeField = new JTextField(10);
    private JTextField racaField = new JTextField(10);
    private JTextField imagemField = new JTextField(10);
    private JButton escolherImagemBtn = new JButton("Escolher Imagem");
    private JComboBox<String> tipoBox = new JComboBox<>(new String[]{"Cachorro", "Gato", "Papagaio", "Hamster"});

    //criação de campos pra exibir card do pet
    private JTextArea output = new JTextArea(5, 20);
    private JLabel petImageLabel = new JLabel();
    private JProgressBar felicidadeBar = new JProgressBar(0, 100);
    private JProgressBar sujeiraBar = new JProgressBar(0, 100);

    //criação dos containers de cards
    private JPanel cardsContainer = new JPanel(new GridLayout(0, 3, 20, 20));
    private Timer globalTimer;

    //cada pet tem suas barras
    private HashMap<Pet, JProgressBar[]> petBarMap = new HashMap<>();

    //criaçao de classe interna para exceção
    public class InvalidPetDataException extends Exception {
        public InvalidPetDataException(String message) {
            super(message);
        }
    }

    //instanciação da interface
    public VirtualPetGUI() {
        setTitle("Virtual Pet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());
        setPreferredSize(new Dimension(800, 600));

        //instancia as telas
        buildCreatePanel();
        buildSelectPanel();
        buildInteractPanel();

        add(panelCreate, "Create");
        add(panelSelect, "Select");
        add(panelInteract, "Interact");

        //carrega os pets e inicia o timer ao abrir o programa
        loadPets();
        startGlobalTimer();
        showPanel("Select");

        //torna os itens visíveis pro user
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //painel de criação de pet
    private void buildCreatePanel() {
        panelCreate.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelCreate.setLayout(new GridLayout(8, 2, 10, 10));

        panelCreate.add(new JLabel("Tipo:"));
        panelCreate.add(tipoBox);
        panelCreate.add(new JLabel("Nome:"));
        panelCreate.add(nomeField);
        panelCreate.add(new JLabel("Raça:"));
        panelCreate.add(racaField);
        panelCreate.add(new JLabel("Imagem:"));
        panelCreate.add(imagemField);

        //cria os botões pra criar pet
        escolherImagemBtn.addActionListener(e -> escolherImagem());
        panelCreate.add(new JLabel());
        panelCreate.add(escolherImagemBtn);

        JButton criarBtn = new JButton("Criar Pet");
        criarBtn.addActionListener(e -> criarPet());
        panelCreate.add(new JLabel());
        panelCreate.add(criarBtn);

        JButton voltarBtn = new JButton("Voltar");
        voltarBtn.addActionListener(e -> showPanel("Select"));
        panelCreate.add(new JLabel());
        panelCreate.add(voltarBtn);
    }

    //cria painel de seleção
    private void buildSelectPanel() {
        panelSelect.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelSelect.setLayout(new BorderLayout(10, 10));

        //botão de criar novo pet
        JButton novoBtn = new JButton("Novo Pet");
        novoBtn.addActionListener(e -> showPanel("Create"));

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelSelect.add(novoBtn, BorderLayout.NORTH);
        panelSelect.add(scroll, BorderLayout.CENTER);
    }

    //painel de interação do pet
    private void buildInteractPanel() {
        panelInteract.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelInteract.setLayout(new BorderLayout(20, 20));

        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        petImageLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel.add(petImageLabel, BorderLayout.CENTER);

        JPanel barsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        felicidadeBar.setStringPainted(true);
        felicidadeBar.setForeground(Color.GREEN.darker());
        sujeiraBar.setStringPainted(true);
        sujeiraBar.setForeground(Color.ORANGE.darker());
        barsPanel.add(felicidadeBar);
        barsPanel.add(sujeiraBar);

        infoPanel.add(barsPanel, BorderLayout.SOUTH);

        //cria botões de interação
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        String[] actions = {"Falar", "Brincar", "Passear", "Dar Banho", "Petisco", "Voltar"};
        for (String act : actions) {
            JButton btn = new JButton(act);
            btn.addActionListener(e -> {
                if (act.equals("Voltar")) showPanel("Select");
                else interagir(act.toLowerCase());
            });
            buttons.add(btn);
        }

        output.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(output);

        panelInteract.add(infoPanel, BorderLayout.WEST);
        panelInteract.add(buttons, BorderLayout.NORTH);
        panelInteract.add(outputScroll, BorderLayout.CENTER);
    }

    //funções principais do painel
    //enviar caminho da imagem
    private void escolherImagem() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            imagemField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    //salva input e cria pet
    private void criarPet() {
        String tipo = (String) tipoBox.getSelectedItem();
        String nome = nomeField.getText().trim();
        String raca = racaField.getText().trim();
        String imagem = imagemField.getText().trim();

        //cria tratamento de exceção
        try {
            if (nome.isEmpty() || raca.isEmpty() || imagem.isEmpty()) {
                throw new InvalidPetDataException("Preencha todos os campos.");
            }
        } catch (InvalidPetDataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        
        currentPet = criarPetPorTipo(tipo, nome, raca, imagem);
        pets.add(currentPet);
        savePets();
        refreshCards();
        showPanel("Select");
    }

    //salva o pet na classe devida
    private Pet criarPetPorTipo(String tipo, String nome, String raca, String imagem) {
        return switch (tipo) {
            case "Cachorro" -> new Cachorro(nome, raca, imagem);
            case "Gato" -> new Gato(nome, raca, imagem);
            case "Papagaio" -> new Papagaio(nome, raca, imagem);
            case "Hamster" -> new Hamster(nome, raca, imagem);
            default -> throw new IllegalArgumentException("Tipo inválido: " + tipo);
        };
    }

    //restarta os cards
    private void refreshCards() {
        cardsContainer.removeAll();
        petBarMap.clear();

        for (Pet p : pets) {
            PetCard card = new PetCard(p);
            cardsContainer.add(card);
            petBarMap.put(p, new JProgressBar[]{card.happyBar, card.dirtBar});
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
        pack();
    }

    //atualiza os cards cm o pet novo
    private void updateInteractPanel() {
        petImageLabel.setIcon(new ImageIcon(new ImageIcon(currentPet.getImagem()).getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
        atualizarBarrasPet(currentPet, felicidadeBar, sujeiraBar);
    }

    //cria metodo de interacao do pet
    private void interagir(String acao) {
        try {
            if (currentPet == null) throw new Exception("Nenhum pet selecionado.");
            String res = switch (acao) {
                case "falar" -> currentPet.falar();
                case "brincar" -> currentPet.brincar();
                case "passear" -> currentPet.passear();
                case "dar banho" -> {
                    currentPet.darBanho();
                    yield "Você deu banho em " + currentPet.getNome();
                }
                case "petisco" -> currentPet.petisco();
                default -> "";
            };
            output.append(res + "\n");
            updateInteractPanel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    //atualiza felicidade e sujeira em tempo real
    private void atualizarBarrasPet(Pet p, JProgressBar felicidade, JProgressBar sujeira) {
        felicidade.setValue((int) p.getFelicidade());
        felicidade.setString("Felicidade: " + (int) p.getFelicidade() + "%");
        sujeira.setValue((int) p.getSujeira());
        sujeira.setString("Sujeira: " + (int) p.getSujeira() + "%");
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), name);
    }

    //salva os pets no txt
    private void savePets() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("pets.txt"))) {
            for (Pet p : pets) {
                writer.println(p.getClass().getSimpleName() + ";" + p.getNome() + ";" + p.getRaca() + ";" + p.getImagem());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //le os pets do txt
    private void loadPets() {
        try (BufferedReader reader = new BufferedReader(new FileReader("pets.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    pets.add(criarPetPorTipo(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException ignored) {}
        refreshCards();
    }

    //starta um timer global pra todos pets
    private void startGlobalTimer() {
        globalTimer = new Timer(1000, e -> {
            for (Pet p : pets) {
                p.decreaseHappiness(0.5);
                p.increaseDirt(1.0);
                JProgressBar[] bars = petBarMap.get(p);
                if (bars != null) atualizarBarrasPet(p, bars[0], bars[1]);
            }
            if (currentPet != null) atualizarBarrasPet(currentPet, felicidadeBar, sujeiraBar);
        });
        globalTimer.start();
    }

    //classe do card do pet
    private class PetCard extends JPanel {
        JProgressBar happyBar = new JProgressBar(0, 100);
        JProgressBar dirtBar = new JProgressBar(0, 100);

        public PetCard(Pet p) {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setBackground(Color.WHITE);

            //imagem do pet
            ImageIcon icon = new ImageIcon(new ImageIcon(p.getImagem()).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            JLabel imgLabel = new JLabel(icon);
            imgLabel.setHorizontalAlignment(JLabel.CENTER);

            //nome e raça
            JLabel nameLabel = new JLabel("<html><b>" + p.getNome() + "</b><br/>Raça: " + p.getRaca() + "</html>");
            nameLabel.setHorizontalAlignment(JLabel.CENTER);

            //barras
            happyBar.setValue((int) p.getFelicidade());
            happyBar.setStringPainted(true);

            dirtBar.setValue((int) p.getSujeira());
            dirtBar.setStringPainted(true);
            dirtBar.setForeground(Color.ORANGE.darker());

            //instancia barras
            JPanel barsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            barsPanel.add(happyBar);
            barsPanel.add(dirtBar);

            JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

            //ação de selecionar o pet
            JButton selectBtn = new JButton("Selecionar");
            selectBtn.addActionListener(e -> {
                currentPet = p;
                updateInteractPanel();
                showPanel("Interact");
            });

            //ação de deletar o pet
            JButton deleteBtn = new JButton("Deletar");
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja deletar " + p.getNome() + "?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    pets.remove(p);
                    savePets();
                    refreshCards();
                }
            });

            //instancia botões
            buttonsPanel.add(selectBtn);
            buttonsPanel.add(deleteBtn);

            //montagem final
            add(nameLabel, BorderLayout.NORTH);
            add(imgLabel, BorderLayout.CENTER);
            add(barsPanel, BorderLayout.SOUTH);
            add(buttonsPanel, BorderLayout.EAST);
        }
    }

    //roda o programa principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VirtualPetGUI::new);
    }
}