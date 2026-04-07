import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Autentificare - Platforma Recrutare");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3,2,10,10));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel(""));
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autentificareUtilizator();
            }
        });

    }

    private void autentificareUtilizator() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Introdu emailul si parola","Eroare",JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM Utilizatori WHERE email = ? AND parola = ?";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,email);
            pstmt.setString(2,password);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                String rol = rs.getNString("rol");
                JOptionPane.showMessageDialog(this,"Autentificare reusita! Rol: "+ rol);

                this.dispose();

                if (rol.equals("hr")) {
                    int idCompanie = rs.getInt("id_companie");
                    new HRFrame(idCompanie).setVisible(true);
                } else if (rol.equals("candidat")) {
                    int idCandidat = rs.getInt("id_candidat");
                    new CandidatFrame(idCandidat).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email sau parola incorecte!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la conectarea cu baza de date!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
    }
}
