import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VeziProfil extends JDialog {

    VeziProfil(JFrame parent,int idAplicatie) {
        super(parent,"Profil Candidat",true);
        setSize(500,500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        JPanel panouSus = new JPanel(new GridLayout(4,1,5,5));
        panouSus.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JLabel lblNume = new JLabel("Nume: ");
        lblNume.setFont(new Font("Arial",Font.BOLD,16));
        JLabel lblEmail = new JLabel("Email: ");
        JLabel lblTelefon = new JLabel("Telefon: ");
        JLabel lblJobAplicat = new JLabel("A aplicat pentru: ");

        panouSus.add(lblNume);
        panouSus.add(lblEmail);
        panouSus.add(lblTelefon);
        panouSus.add(lblJobAplicat);

        JPanel panouCentru = new JPanel(new BorderLayout());
        panouCentru.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panouCentru.add(new JLabel("CV / Prezentare:"), BorderLayout.NORTH);

        JTextArea txtCV = new JTextArea();
        txtCV.setEditable(false);
        txtCV.setLineWrap(true);
        txtCV.setWrapStyleWord(true);
        panouCentru.add(txtCV, BorderLayout.CENTER);

        JButton btnInchide = new JButton("Inchide");
        JPanel panousJos = new JPanel();
        panousJos.add(btnInchide);

        add(panouSus,BorderLayout.NORTH);
        add(panouCentru,BorderLayout.CENTER);
        add(panousJos,BorderLayout.SOUTH);

        btnInchide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });

        incarcaDateProfil(idAplicatie,lblNume,lblEmail,lblTelefon,lblJobAplicat,txtCV);
    }

    private void incarcaDateProfil(int idAplicatie,JLabel lblNume,JLabel lblEmail,JLabel lblTelefon,JLabel lblJobAplicat,JTextArea txtCV){

        String sql= "SELECT c.nume, c.prenume, c.email, c.telefon, c.cv_text, j.titlu " +
                    "FROM Aplicatii a " +
                    "JOIN Candidati c ON a.id_candidat = c.id_candidat " +
                    "JOIN Joburi j ON a.id_job = j.id_job " +
                    "WHERE a.id_aplicatie = ?";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,idAplicatie);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                lblNume.setText("Nume: " + rs.getString("nume") + " " + rs.getString("prenume"));
                lblEmail.setText("Email: " + rs.getString("email"));
                lblTelefon.setText("Telefon: " + rs.getString("telefon"));
                lblJobAplicat.setText("A aplicat pentru: " + rs.getString("titlu"));
                txtCV.setText(rs.getString("cv_text"));
            } else{
                JOptionPane.showMessageDialog(this,"Nu s-au gasit date pentru acest candidat","Eroare",JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la aducerea datelor","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }
}
