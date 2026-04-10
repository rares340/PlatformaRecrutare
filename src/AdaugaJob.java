import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdaugaJob extends JDialog {
    private boolean aFostSalvat=false;
    private int idCompanieCurenta;

    private JTextField txtTitlu;
    private JTextArea txtDescriere;
    private JTextField txtOras;
    private JTextField txtSalariuMin;
    private JTextField txtSalariuMax;

    public AdaugaJob(JFrame parent,int idCompanie){
        super(parent,"Adauga un Job Nou",true);
        this.idCompanieCurenta = idCompanie;
        setSize(400,450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        JPanel panouFormular = new JPanel(new GridLayout(5,2,5,10));
        panouFormular.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        txtTitlu = new JTextField();
        txtOras = new JTextField();
        txtSalariuMin = new JTextField();
        txtSalariuMax = new JTextField();

        panouFormular.add(new JLabel("Titlu Job (*):"));
        panouFormular.add(txtTitlu);
        panouFormular.add(new JLabel("Oras (*):"));
        panouFormular.add(txtOras);
        panouFormular.add(new JLabel("Salariu Minim (RON):"));
        panouFormular.add(txtSalariuMin);
        panouFormular.add(new JLabel("Salariu Maxim (RON):"));
        panouFormular.add(txtSalariuMax);

        JPanel panouDescriere = new JPanel(new BorderLayout());
        panouDescriere.setBorder(BorderFactory.createEmptyBorder(0,15,10,15));
        txtDescriere = new JTextArea(4,20);
        txtDescriere.setLineWrap(true);
        panouDescriere.add(new JLabel("Descriere scurta: "),BorderLayout.NORTH);
        panouDescriere.add(new JScrollPane(txtDescriere),BorderLayout.CENTER);

        JPanel panouButoane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalveaza = new JButton("Posteaza Jobul");
        JButton btnAnuleaza = new JButton("Anuleaza");

        panouButoane.add(btnSalveaza);
        panouButoane.add(btnAnuleaza);

        add(panouFormular,BorderLayout.NORTH);
        add(panouDescriere,BorderLayout.CENTER);
        add(panouButoane,BorderLayout.SOUTH);

        btnAnuleaza.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });

        btnSalveaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                validareSiSalvare();
            }
        });
    }

    private void validareSiSalvare(){
        String titlu = txtTitlu.getText().trim();
        String oras = txtOras.getText().trim();
        String descriere = txtDescriere.getText().trim();
        String salMinStr = txtSalariuMin.getText().trim();
        String salMaxStr = txtSalariuMax.getText().trim();

        if (titlu.isEmpty() || oras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Câmpurile marcate cu (*) sunt obligatorii!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int salMin = 0, salMax = 0;
        try {
            if (!salMinStr.isEmpty()) salMin = Integer.parseInt(salMinStr);
            if (!salMaxStr.isEmpty()) salMax = Integer.parseInt(salMaxStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salariile trebuie să conțină doar numere!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inserareJobInDB(titlu,descriere,oras,salMin,salMax);
    }

    private void inserareJobInDB(String titlu,String descriere,String oras,int salMin,int salMax){
        String sql = "INSERT INTO Joburi(id_companie,titlu,descriere,oras,salariu_min,salariu_max) VALUES(?,?,?,?,?,?)";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCompanieCurenta);
            pstmt.setString(2, titlu);
            pstmt.setString(3, descriere);
            pstmt.setString(4, oras);
            pstmt.setInt(5, salMin);
            pstmt.setInt(6, salMax);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,"Jobul a fost postat cu succes!","Succes",JOptionPane.INFORMATION_MESSAGE);
            aFostSalvat = true;
            dispose();
        } catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare salvare!","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean aFostSalvat(){
        return aFostSalvat;
    }
}
