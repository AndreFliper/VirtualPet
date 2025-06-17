import javax.swing.*;

//roda o programa e roda VirtualPetGUI
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VirtualPetGUI();
        });
    }
}