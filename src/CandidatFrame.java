import javax.swing.*;

public class CandidatFrame extends JFrame {
    private int idCandidat;

    public CandidatFrame(int idCandidat) {
        this.idCandidat = idCandidat;
        setTitle("Portal Candidat");
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new JLabel("Bun venit! Id-ul tau de candidat este: "+idCandidat,SwingConstants.CENTER));
    }
}
