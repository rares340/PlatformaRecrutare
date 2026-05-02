import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProfilCandidat extends JDialog {
    private boolean aFostSalvat = false;
    private int idCandidat;

    private JTextField txtNume;
    private JTextField txtPrenume;
    private JTextField txtEmail;
    private JTextField txtTelefon;
    private JTextArea txtCV;

    private JPanel panouCheckboxes;
    private List<JCheckBox> listaCheckboxes = new ArrayList<>();

    public ProfilCandidat(JFrame parent,int idCandidat) {
        super(parent,"Profil Candidat",true);
        this.idCandidat = idCandidat;
        setSize(500,600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        JPanel panouFormular =  new JPanel(new GridLayout(4,2,5,10));
        panouFormular.setBorder(BorderFactory.createEmptyBorder(15,15,5,15));

        txtNume = new JTextField();
        txtPrenume = new JTextField();
        txtEmail = new JTextField();
        txtEmail.setEditable(false);
        txtTelefon = new JTextField();

        panouFormular.add(new JLabel("Nume:"));
        panouFormular.add(txtNume);
        panouFormular.add(new JLabel("Prenume:"));
        panouFormular.add(txtPrenume);
        panouFormular.add(new JLabel("Email:"));
        panouFormular.add(txtEmail);
        panouFormular.add(new JLabel("Telefon:"));
        panouFormular.add(txtTelefon);

        JPanel panouMijloc = new JPanel(new BorderLayout(5, 10));
        panouMijloc.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panouMijloc.add(creeazaZonaCompetente(), BorderLayout.NORTH);

        JPanel panouCV = new JPanel(new BorderLayout());
        panouCV.setBorder(BorderFactory.createEmptyBorder(0,15,10,15));
        panouCV.add(new JLabel("CV: "),BorderLayout.NORTH);

        txtCV = new JTextArea();
        txtCV.setLineWrap(true);
        txtCV.setWrapStyleWord(true);
        panouCV.add(new JScrollPane(txtCV),BorderLayout.CENTER);
        panouMijloc.add(panouCV,BorderLayout.CENTER);

        JPanel panouButoane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalveaza = new  JButton("Salveaza Modificarile");
        btnSalveaza.setFocusable(false);
        btnSalveaza.setBackground(new Color(70,130,180));
        btnSalveaza.setForeground(Color.WHITE);
        JButton btnAnuleaza = new JButton("Anuleaza");
        panouButoane.add(btnSalveaza);
        panouButoane.add(btnAnuleaza);

        add(panouFormular,BorderLayout.NORTH);
        add(panouMijloc,BorderLayout.CENTER);
        add(panouButoane,BorderLayout.SOUTH);

        incarcaDateCurente();

        btnAnuleaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        btnSalveaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                salveazaDateProfil();
            }
        });

    }

    private void incarcaDateCurente(){
        try (Connection conn = ConexiuneDB.getConnection()) {

            // 1. Aducem datele personale
            String sqlDate = "SELECT nume, prenume, email, telefon, cv_text FROM Candidati WHERE id_candidat = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDate)) {
                pstmt.setInt(1, this.idCandidat);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    txtNume.setText(rs.getString("nume"));
                    txtPrenume.setText(rs.getString("prenume"));
                    txtEmail.setText(rs.getString("email"));
                    txtTelefon.setText(rs.getString("telefon"));
                    txtCV.setText(rs.getString("cv_text"));
                }
            }

            // 2. Aducem competențele pe care le avea deja bifate și le bifăm pe ecran!
            String sqlComp = "SELECT id_competenta FROM CompetenteCandidati WHERE id_candidat = ?";
            try (PreparedStatement pstmtComp = conn.prepareStatement(sqlComp)) {
                pstmtComp.setInt(1, this.idCandidat);
                ResultSet rsComp = pstmtComp.executeQuery();

                Set<String> abilitatiBifate = new HashSet<>();
                while (rsComp.next()) {
                    abilitatiBifate.add(String.valueOf(rsComp.getInt("id_competenta")));
                }

                for (JCheckBox cb : listaCheckboxes) {
                    if (abilitatiBifate.contains(cb.getName())) {
                        cb.setSelected(true);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la aducerea profilului!", "Eroare DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel creeazaZonaCompetente(){
        JPanel panouPrincipal = new JPanel(new BorderLayout(5, 5));
        panouPrincipal.setBorder(BorderFactory.createTitledBorder("Abilitățile Mele:"));

        JPanel panouFiltru = new JPanel(new BorderLayout());
        panouFiltru.add(new JLabel("Caută abilitate: "), BorderLayout.WEST);
        JTextField txtFiltru = new JTextField();
        panouFiltru.add(txtFiltru, BorderLayout.CENTER);
        panouPrincipal.add(panouFiltru, BorderLayout.NORTH);

        panouCheckboxes = new JPanel(new GridLayout(0, 3));

        String sql = "SELECT id_competenta, nume_competenta FROM Competente";
        try (Connection conn = ConexiuneDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                JCheckBox cb = new JCheckBox(rs.getString("nume_competenta"));
                cb.setName(String.valueOf(rs.getInt("id_competenta")));
                listaCheckboxes.add(cb);
                panouCheckboxes.add(cb);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        JScrollPane scroll = new JScrollPane(panouCheckboxes);
        scroll.setPreferredSize(new Dimension(500, 120));
        panouPrincipal.add(scroll, BorderLayout.CENTER);

        txtFiltru.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtreaza(); }
            public void removeUpdate(DocumentEvent e) { filtreaza(); }
            public void changedUpdate(DocumentEvent e) { filtreaza(); }

            private void filtreaza() {
                String textCautat = txtFiltru.getText().toLowerCase().trim();
                for (JCheckBox cb : listaCheckboxes) {
                    cb.setVisible(cb.getText().toLowerCase().contains(textCautat));
                }
                panouCheckboxes.revalidate();
                panouCheckboxes.repaint();
            }
        });

        return panouPrincipal;
    }

    private void salveazaDateProfil(){
        try (Connection conn = ConexiuneDB.getConnection()) {

            // Folosesc tranzacții ca să nu se salveze datele pe jumătate
            conn.setAutoCommit(false);

            String sqlUpdate = "UPDATE Candidati SET nume=?, prenume=?, email=?, telefon=?, cv_text=? WHERE id_candidat=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setString(1, txtNume.getText().trim());
                pstmt.setString(2, txtPrenume.getText().trim());
                pstmt.setString(3, txtEmail.getText().trim());
                pstmt.setString(4, txtTelefon.getText().trim());
                pstmt.setString(5, txtCV.getText().trim());
                pstmt.setInt(6, this.idCandidat);
                pstmt.executeUpdate();
            }

            String sqlDelete = "DELETE FROM CompetenteCandidati WHERE id_candidat = ?";
            try (PreparedStatement pstmtDel = conn.prepareStatement(sqlDelete)) {
                pstmtDel.setInt(1, this.idCandidat);
                pstmtDel.executeUpdate();
            }

            String sqlInsert = "INSERT INTO CompetenteCandidati (id_candidat, id_competenta) VALUES (?, ?)";
            try (PreparedStatement pstmtIns = conn.prepareStatement(sqlInsert)) {
                for (JCheckBox cb : listaCheckboxes) {
                    if (cb.isSelected()) {
                        pstmtIns.setInt(1, this.idCandidat);
                        pstmtIns.setInt(2, Integer.parseInt(cb.getName()));
                        pstmtIns.addBatch();
                    }
                }
                pstmtIns.executeBatch();
            }

            conn.commit(); // Confirm tranzacția
            conn.setAutoCommit(true);

            aFostSalvat = true;
            JOptionPane.showMessageDialog(this, "Profilul a fost actualizat cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la salvarea profilului!", "Eroare DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean aFostSalvat() {
        return aFostSalvat;
    }
}
