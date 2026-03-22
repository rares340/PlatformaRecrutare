import javax.swing.*;
import java.awt.*;

public class HRFrame extends JFrame {
    public HRFrame() {
        setTitle("Panou de Control HR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,400);
        setLocationRelativeTo(null);
        add(new Label("Bune venit in interfata HR",SwingConstants.CENTER));
    }
}
