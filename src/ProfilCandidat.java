import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilCandidat extends JDialog {
    private boolean aFostSalvat = false;
    private int idCandidat;

    private JTextField txtNume;
    private JTextField txtPrenume;
    private JTextField txtEmail;
    private JTextField txtTelefon;
    private JTextArea txtCV;

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

        JPanel panouCV = new JPanel(new BorderLayout());
        panouCV.setBorder(BorderFactory.createEmptyBorder(0,15,10,15));
        panouCV.add(new JLabel("CV: "));

        txtCV = new JTextArea();
        txtCV.setLineWrap(true);
        txtCV.setWrapStyleWord(true);
        panouCV.add(new JScrollPane(txtCV),BorderLayout.CENTER);

        JPanel panouButoane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalveaza = new  JButton("Salveaza Modificarile");
        btnSalveaza.setFocusable(false);
        btnSalveaza.setBackground(new Color(70,130,180));
        btnSalveaza.setForeground(Color.WHITE);
        JButton btnAnuleaza = new JButton("Anuleaza");
        panouButoane.add(btnSalveaza);
        panouButoane.add(btnAnuleaza);

        add(panouFormular,BorderLayout.NORTH);
        add(panouCV,BorderLayout.CENTER);
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
        String sql = "SELECT nume,prenume,email,telefon,cv_text FROM Candidati "+
                "WHERE id_candidat = ?";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCandidat);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                txtNume.setText(rs.getString("nume"));
                txtPrenume.setText(rs.getString("prenume"));
                txtEmail.setText(rs.getString("email"));
                txtTelefon.setText(rs.getString("telefon"));
                txtCV.setText(rs.getString("cv_text"));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la incarcarea profilului","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salveazaDateProfil(){
        String sql = "UPDATE Candidati SET nume = ?, prenume = ?, email =?, telefon = ?, cv_text = ? WHERE id_candidat = ? ";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,txtNume.getText().trim());
            pstmt.setString(2,txtPrenume.getText().trim());
            pstmt.setString(3,txtEmail.getText().trim());
            pstmt.setString(4,txtTelefon.getText().trim());
            pstmt.setString(5,txtCV.getText().trim());
            pstmt.setInt(6,this.idCandidat);

            pstmt.executeUpdate();

            aFostSalvat = true;
            JOptionPane.showMessageDialog(this,"Profilul a fost salvat cu succes!","Succes",JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la salvarea profilului","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean aFostSalvat() {
        return aFostSalvat;
    }
}
