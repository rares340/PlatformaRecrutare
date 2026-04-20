import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetaliiJob extends JDialog {
    private int idCandidat;
    private int idJob;
    private boolean aAplicat = false;

    public DetaliiJob(JFrame parent,int idCandidat,int idJob) {
        this.idCandidat = idCandidat;
        this.idJob = idJob;
        super(parent,"Detalii Job",true);

        setSize(500,500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        JPanel panouInfo = new JPanel(new GridLayout(4,1,5,5));
        panouInfo.setBorder(BorderFactory.createEmptyBorder(15,15,5,15));

        JLabel lblTitlu = new JLabel("Se încarca ");
        lblTitlu.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblCompanie = new JLabel("Companie: ");
        JLabel lblOras = new JLabel("Oras: ");
        JLabel lblSalariu = new JLabel("Salariu: ");

        panouInfo.add(lblTitlu);
        panouInfo.add(lblCompanie);
        panouInfo.add(lblOras);
        panouInfo.add(lblSalariu);

        JPanel panouDescriere = new JPanel(new BorderLayout());
        panouDescriere.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        panouDescriere.add(new JLabel("Descrierea Jobului:"), BorderLayout.NORTH);

        JTextArea txtDescriere = new JTextArea();
        txtDescriere.setEditable(false);
        txtDescriere.setLineWrap(true);
        txtDescriere.setWrapStyleWord(true);
        panouDescriere.add(new JScrollPane(txtDescriere), BorderLayout.CENTER);

        JPanel panouButoane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAplica = new JButton("Aplica");
        btnAplica.setBackground(new Color(34, 139, 34));
        btnAplica.setForeground(Color.WHITE);
        JButton btnInchide = new JButton("Inchide");

        panouButoane.add(btnAplica);
        panouButoane.add(btnInchide);

        add(panouInfo, BorderLayout.NORTH);
        add(panouDescriere, BorderLayout.CENTER);
        add(panouButoane, BorderLayout.SOUTH);

        btnInchide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });

        btnAplica.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e){
               aplicaLaJob();
           }
        });

        incarcaDateJob(lblTitlu,lblCompanie,lblOras,lblSalariu,txtDescriere);
    }

    private void incarcaDateJob(JLabel lblTitlu,JLabel lblCompanie,JLabel lblOras,JLabel lblSalariu,JTextArea txtDescriere) {
        String sql ="SELECT j.titlu,c.nume AS companie, j.oras, j.salariu_min,j.salariu_max, j.descriere "+
                "FROM Joburi j JOIN Companii c ON j.id_companie=c.id_companie " +
                "WHERE id_job=?";
        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,idJob);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                lblTitlu.setText(rs.getString("titlu"));
                lblCompanie.setText("Companie: "+rs.getString("companie"));
                lblOras.setText("Oras: "+ rs.getString("oras"));
                int min = rs.getInt("salariu_min");
                int max = rs.getInt("salariu_max");
                lblSalariu.setText("Salariu: " + min + " - " + max + " RON");
                txtDescriere.setText(rs.getString("descriere"));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la incarcarea jobului","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicaLaJob(){
        String sql ="INSERT INTO Aplicatii (id_job, id_candidat) VALUES (?,?)";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,idJob);
            pstmt.setInt(2,idCandidat);
            pstmt.executeUpdate();

            aAplicat = true;
            JOptionPane.showMessageDialog(this,"Ai aplicat cu succes!","Succes",JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la aplicare","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean aAplicatAcum(){
        return aAplicat;
    }
}
