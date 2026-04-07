import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HRFrame extends JFrame {
    private int idCompanie;

    private JTable tabelJoburi;
    private DefaultTableModel modelTabelJoburi;

    private JTable tabelAplicatii;
    private DefaultTableModel modelTabelAplicatii;

    private JButton btnAdaugaJob;
    private JButton btnRefresh;

    public HRFrame(int idCompanie) {
        this.idCompanie = idCompanie;
        setTitle("Panou de Control HR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        JPanel panouSus = new JPanel(new BorderLayout(5,10));
        panouSus.setLayout(new FlowLayout(FlowLayout.LEFT));

        String numeCompanie = getCompany();
        JLabel lblHeader = new JLabel("Companie: "+numeCompanie);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
        panouSus.add(lblHeader,BorderLayout.NORTH);

        btnAdaugaJob = new JButton("Adauga Job Nou");
        btnRefresh = new JButton("Refresh");
        btnAdaugaJob.setFocusable(false);
        btnRefresh.setFocusable(false);

        panouSus.add(btnAdaugaJob);
        panouSus.add(btnRefresh);
        add(panouSus,BorderLayout.NORTH);

        JPanel panouJoburi = PanouJoburi();
        JPanel panouAplicatii = PanouAplicatii();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,panouJoburi,panouAplicatii);
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);

        add(splitPane,BorderLayout.CENTER);

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
                incarcaAplicatiiDinDB();
            }
        });
    }

    private JPanel PanouJoburi(){
        JPanel panou = new JPanel(new BorderLayout(5,5));
        panou.setBorder(BorderFactory.createTitledBorder("Joburi"));

        String[] coloane = {"ID","Titlu","Oras","Salariu Min", "Salariu Max"};
        modelTabelJoburi = new DefaultTableModel(coloane,0);
        tabelJoburi = new JTable(modelTabelJoburi);
        //tabelJoburi.setRowSelectionAllowed(false);
        //tabelJoburi.setCellSelectionEnabled(true);
        tabelJoburi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelJoburi.getTableHeader().setReorderingAllowed(false);
        tabelJoburi.getTableHeader().setResizingAllowed(false);

        //Dimensiune Coloane
        tabelJoburi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelJoburi.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabelJoburi.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabelJoburi.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabelJoburi.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tabelJoburi);
        panou.add(scrollPane,BorderLayout.CENTER);

        incarcaJoburiDinDB();
        return panou;
    }

    private JPanel PanouAplicatii(){
        JPanel panou = new JPanel(new BorderLayout(5,5));
        panou.setBorder(BorderFactory.createTitledBorder("Aplicatii primite"));

        JPanel panouSus = new JPanel();
        panouSus.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton btnVeziProfil = new JButton("Vezi Profil");
        JButton btnSchimbaStatus = new JButton("Schimba Status");

        btnVeziProfil.setFocusable(false);
        btnSchimbaStatus.setFocusable(false);
        panouSus.add(btnVeziProfil);
        panouSus.add(btnSchimbaStatus);
        panou.add(panouSus,BorderLayout.NORTH);

        String[] coloane = {"ID","Nume Candidat","Job Aplicat","Data","Status"};
        modelTabelAplicatii = new DefaultTableModel(coloane,0);
        tabelAplicatii = new JTable(modelTabelAplicatii);

        tabelAplicatii.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelAplicatii.getTableHeader().setReorderingAllowed(false);
        tabelAplicatii.getTableHeader().setResizingAllowed(false);

        tabelAplicatii.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelAplicatii.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabelAplicatii.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabelAplicatii.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabelAplicatii.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tabelAplicatii);
        panou.add(scrollPane,BorderLayout.CENTER);

        btnVeziProfil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){

            }
        });
        btnSchimbaStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){}
        });

        incarcaAplicatiiDinDB();
        return  panou;
    }

    private String getCompany(){

        String sql="SELECT nume FROM Companii WHERE id_companie = ?";
        String numeCompanie="";
        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCompanie);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
            numeCompanie = rs.getString("nume");
            }
        } catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare incarcare nume Companie","Eroare",JOptionPane.ERROR_MESSAGE);
        }
        return numeCompanie;
    }

    private void incarcaAplicatiiDinDB(){
        modelTabelAplicatii.setRowCount(0);

        String sql = "SELECT a.id_aplicatie,CONCAT(c.nume,' ',c.prenume) AS nume_complet, j.titlu, a.data_aplicarii, a.status " +
                     "FROM Aplicatii a JOIN Candidati c ON a.id_candidat=c.id_candidat "+
                     "JOIN Joburi j ON a.id_job=j.id_job WHERE j.id_companie = ? ORDER BY a.data_aplicarii DESC";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCompanie);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Object[] rand ={
                        rs.getInt("id_aplicatie"),
                        rs.getString("nume_complet"),
                        rs.getString("titlu"),
                        rs.getTimestamp("data_aplicarii"),
                        rs.getString("status")
                };
                modelTabelAplicatii.addRow(rand);
            }
        } catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare incarcare Aplicatii","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void incarcaJoburiDinDB(){
        modelTabelJoburi.setRowCount(0);

        String sql = "SELECT id_job,titlu,oras,salariu_min,salariu_max FROM Joburi WHERE id_companie = ?";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCompanie);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id_job");
                String titlu = rs.getString("titlu");
                String oras = rs.getString("oras");
                int salMin = rs.getInt("salariu_min");
                int salMax = rs.getInt("salariu_max");

                Object[] randNou = {id,titlu,oras,salMin,salMax};
                modelTabelJoburi.addRow(randNou);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la aducerea datelor din DB","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }
}
