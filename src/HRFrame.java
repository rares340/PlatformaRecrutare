import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HRFrame extends JFrame {
    private JTable tabelJoburi;
    private DefaultTableModel modelTabel;
    private JButton btnAdaugaJob;
    private JButton btnRefresh;

    public HRFrame() {
        setTitle("Panou de Control HR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        JPanel panouSus = new JPanel();
        panouSus.setLayout(new FlowLayout(FlowLayout.CENTER));

        btnAdaugaJob = new JButton("Adauga Job Nou");
        btnRefresh = new JButton("Refresh Tabel");
        btnAdaugaJob.setFocusable(false);
        btnRefresh.setFocusable(false);

        panouSus.add(btnAdaugaJob);
        panouSus.add(btnRefresh);
        add(panouSus,BorderLayout.NORTH);

        String[] coloane = {"ID Job","Titlu","Oras","Salariu Min", "Salariu Max"};
        modelTabel = new DefaultTableModel(coloane,0);
        tabelJoburi = new JTable(modelTabel);
        tabelJoburi.setRowSelectionAllowed(false);
        tabelJoburi.setCellSelectionEnabled(true);
        tabelJoburi.getTableHeader().setReorderingAllowed(false);
        tabelJoburi.getTableHeader().setResizingAllowed(false);

        //Dimensiune Coloane
        tabelJoburi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelJoburi.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabelJoburi.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabelJoburi.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabelJoburi.getColumnModel().getColumn(4).setPreferredWidth(90);


        JScrollPane scrollPane = new JScrollPane(tabelJoburi);
        add(scrollPane,BorderLayout.CENTER);

        incarcaJoburiDinDB();

        btnAdaugaJob.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(HRFrame.this,"Adauga Job Nou");
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                incarcaJoburiDinDB();
            }
        });
    }

    private void incarcaJoburiDinDB(){
        modelTabel.setRowCount(0);

        String sql = "SELECT id_job,titlu,oras,salariu_min,salariu_max FROM Joburi";

        try(Connection conn = ConexiuneDB.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                int id = rs.getInt("id_job");
                String titlu = rs.getString("titlu");
                String oras = rs.getString("oras");
                int salMin = rs.getInt("salariu_min");
                int salMax = rs.getInt("salariu_max");

                Object[] randNou = {id,titlu,oras,salMin,salMax};
                modelTabel.addRow(randNou);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la aducerea datelor din DB");
        }
    }
}
