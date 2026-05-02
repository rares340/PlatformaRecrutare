import javax.swing.*;
import javax.swing.event.ListSelectionListener;
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
    private JButton btnStatistici;
    private JTabbedPane tabbedPane;
    private JTextField txtCautareCV;

    public HRFrame(int idCompanie) {
        this.idCompanie = idCompanie;
        setTitle("Panou de Control HR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        JPanel panouSus = new JPanel(new BorderLayout(5,10));

        String numeCompanie = getCompany();
        JLabel lblHeader = new JLabel("Companie: "+numeCompanie);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15,15,15,10));
        panouSus.add(lblHeader,BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFocusable(false);
        JPanel panouRefresh = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panouRefresh.add(btnRefresh);
        panouSus.add(panouRefresh, BorderLayout.EAST);

        add(panouSus,BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel panouJoburi = PanouJoburi();
        tabbedPane.addTab("Management Joburi", panouJoburi);

        JPanel panouAplicatii = PanouAplicatii();
        tabbedPane.addTab("Aplicatii & Candidati", panouAplicatii);

        add(tabbedPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                tabelJoburi.clearSelection();
                if(txtCautareCV!=null)
                    txtCautareCV.setText("");
                incarcaJoburiDinDB();
                incarcaAplicatiiDinDB();
            }
        });
    }

    private JPanel PanouJoburi(){
        JPanel panou = new JPanel(new BorderLayout(10,10));
        panou.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel panouActiuniSus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdaugaJob = new JButton("Postează Job Nou");
        btnAdaugaJob.setBackground(new Color(70, 130, 180));
        btnAdaugaJob.setForeground(Color.WHITE);
        btnAdaugaJob.setFocusable(false);

        btnStatistici = new JButton("Dashboard Statistici");
        btnStatistici.setFocusable(false);

        panouActiuniSus.add(btnAdaugaJob);
        panouActiuniSus.add(btnStatistici);
        panou.add(panouActiuniSus, BorderLayout.NORTH);

        String[] coloane = {"ID","Titlu","Oras","Salariu Min", "Salariu Max"};
        modelTabelJoburi = new DefaultTableModel(coloane,0);
        tabelJoburi = new JTable(modelTabelJoburi);

        tabelJoburi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelJoburi.getTableHeader().setReorderingAllowed(false);
        tabelJoburi.getTableHeader().setResizingAllowed(false);

        tabelJoburi.getColumnModel().getColumn(0).setMinWidth(0);
        tabelJoburi.getColumnModel().getColumn(0).setMaxWidth(0);

        tabelJoburi.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabelJoburi.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabelJoburi.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabelJoburi.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tabelJoburi);
        panou.add(scrollPane,BorderLayout.CENTER);


        JPanel panouJos = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVeziCandidati = new JButton("Vezi Candidații pentru Jobul Selectat");
        panouJos.add(btnVeziCandidati);
        panou.add(panouJos, BorderLayout.SOUTH);

        btnAdaugaJob.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                AdaugaJob dialog = new AdaugaJob(HRFrame.this,idCompanie);
                dialog.setVisible(true);

                if(dialog.aFostSalvat()){
                    incarcaJoburiDinDB();
                }
            }
        });

        btnStatistici.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e){
               StatisticiHR dialog = new StatisticiHR(HRFrame.this,idCompanie);
               dialog.setVisible(true);
           }
        });

        btnVeziCandidati.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
            int rand = tabelJoburi.getSelectedRow();
            if (rand == -1) {
                JOptionPane.showMessageDialog(HRFrame.this, "Selectează un job din tabel!", "Atenție", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idJob = (int) tabelJoburi.getValueAt(rand, 0);
            incarcaAplicatiiPentruJob(idJob);
            tabbedPane.setSelectedIndex(1);
            }
        });

        incarcaJoburiDinDB();
        return panou;
    }

    private JPanel PanouAplicatii(){
        JPanel panou = new JPanel(new BorderLayout(10,10));
        panou.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel panouSus = new JPanel();
        panouSus.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
        JButton btnVeziProfil = new JButton("Vezi Profil");
        JButton btnSchimbaStatus = new JButton("Schimba Status");
        JLabel lblCauta = new JLabel("Cauta abilitati:");
        JTextField txtCauta = new JTextField(15);
        JButton btnCauta = new JButton("Cauta");

        btnVeziProfil.setFocusable(false);
        btnSchimbaStatus.setFocusable(false);
        btnCauta.setFocusable(false);

        panouSus.add(btnVeziProfil);
        panouSus.add(btnSchimbaStatus);
        panouSus.add(lblCauta);
        panouSus.add(txtCauta);
        panouSus.add(btnCauta);

        panou.add(panouSus,BorderLayout.NORTH);

        String[] coloane = {"ID","Nume Candidat","Job Aplicat","Data","Scor (%)","Status"};
        modelTabelAplicatii = new DefaultTableModel(coloane,0);
        tabelAplicatii = new JTable(modelTabelAplicatii);

        tabelAplicatii.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelAplicatii.getTableHeader().setReorderingAllowed(false);
        tabelAplicatii.getTableHeader().setResizingAllowed(false);

        tabelAplicatii.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelAplicatii.getColumnModel().getColumn(0).setMinWidth(0);
        tabelAplicatii.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabelAplicatii.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelAplicatii.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabelAplicatii.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelAplicatii.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabelAplicatii.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tabelAplicatii);
        panou.add(scrollPane,BorderLayout.CENTER);

        btnCauta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String cuvinteCheie = txtCauta.getText().trim();
                if(cuvinteCheie.isEmpty()){
                    incarcaAplicatiiDinDB();
                } else{
                    cautaAplicatiiDupaCV(cuvinteCheie);
                }
            }
        });

        btnVeziProfil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                int randSelectat = tabelAplicatii.getSelectedRow();
                if(randSelectat == -1){
                    JOptionPane.showMessageDialog(HRFrame.this,"Selecteaza o aplicatie","Atentie",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idAplicatie = (int)tabelAplicatii.getValueAt(randSelectat,0);

                VeziProfil dialogProfil = new VeziProfil(HRFrame.this,idAplicatie);
                dialogProfil.setVisible(true);
            }
        });
        btnSchimbaStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                int randSelectat = tabelAplicatii.getSelectedRow();
                if(randSelectat == -1){
                    JOptionPane.showMessageDialog(HRFrame.this,"Selecteaza o aplicatie","Atentie",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idAplicatie = (int)tabelAplicatii.getValueAt(randSelectat,0);
                String statusCurent = (String)tabelAplicatii.getValueAt(randSelectat,4);
                String numeCandidat = (String)tabelAplicatii.getValueAt(randSelectat,1);

                SchimbareStatus dialog = new SchimbareStatus(HRFrame.this,idAplicatie,numeCandidat,statusCurent);
                dialog.setVisible(true);

                if(dialog.aFostSalvat()){
                    incarcaAplicatiiDinDB();
                }
            }
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

        String sql = "SELECT a.id_aplicatie, CONCAT(c.nume,' ',c.prenume) AS nume_complet, j.titlu, a.data_aplicarii, a.status, " +
                "COALESCE( " +
                "   (SELECT COUNT(*) FROM CompetenteCandidati cc " +
                "    JOIN CompetenteJob cj ON cc.id_competenta = cj.id_competenta " +
                "    WHERE cc.id_candidat = a.id_candidat AND cj.id_job = a.id_job) " +
                "   * 100.0 / " +
                "   NULLIF((SELECT COUNT(*) FROM CompetenteJob WHERE id_job = a.id_job), 0), 0) AS scor_potrivire " +
                "FROM Aplicatii a JOIN Candidati c ON a.id_candidat=c.id_candidat "+
                "JOIN Joburi j ON a.id_job=j.id_job WHERE j.id_companie = ? ORDER BY a.data_aplicarii DESC";
        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCompanie);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                int scor = Math.round(rs.getFloat("scor_potrivire"));
                Object[] rand ={
                        rs.getInt("id_aplicatie"),
                        rs.getString("nume_complet"),
                        rs.getString("titlu"),
                        rs.getTimestamp("data_aplicarii"),
                        scor+"%",
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

    public void incarcaAplicatiiPentruJob(int idJobSelectat){
        modelTabelAplicatii.setRowCount(0);

        String sql = "SELECT a.id_aplicatie, CONCAT(c.nume, ' ', c.prenume) AS nume_complet, j.titlu, a.data_aplicarii, a.status, " +
                "COALESCE( " +
                "(SELECT COUNT(*) FROM CompetenteCandidati cc " +
                "JOIN CompetenteJob cj ON cc.id_competenta = cj.id_competenta " +
                "WHERE cc.id_candidat = c.id_candidat AND cj.id_job = ?) " +
                "* 100.0 / " +
                "NULLIF((SELECT COUNT(*) FROM CompetenteJob WHERE id_job = ?), 0), 0) AS scor_potrivire " +
                "FROM Aplicatii a JOIN Candidati c ON a.id_candidat=c.id_candidat "+
                "JOIN Joburi j ON a.id_job=j.id_job WHERE a.id_job = ? " +
                "ORDER BY scor_potrivire DESC, a.data_aplicarii DESC";
        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,idJobSelectat);
            pstmt.setInt(2,idJobSelectat);
            pstmt.setInt(3,idJobSelectat);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int scor = Math.round(rs.getFloat("scor_potrivire"));
                Object[] rand={
                        rs.getInt("id_aplicatie"),
                        rs.getString("nume_complet"),
                        rs.getString("titlu"),
                        rs.getTimestamp("data_aplicarii"),
                        scor + "%",
                        rs.getString("status")
                };
                modelTabelAplicatii.addRow(rand);
            }
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la aducerea datelor din DB","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cautaAplicatiiDupaCV(String cuvinteCheie){
        modelTabelAplicatii.setRowCount(0);

        String sql = "SELECT a.id_aplicatie, CONCAT(c.nume, ' ',c.prenume) as nume_complet, j.titlu, a.data_aplicarii, a.status, " +
                "COALESCE((SELECT COUNT(*) FROM CompetenteCandidati cc " +
                "JOIN CompetenteJob cj ON cc.id_competenta = cj.id_competenta " +
                "WHERE cc.id_candidat = a.id_candidat AND cj.id_job = a.id_job) " +
                "* 100.0 / NULLIF((SELECT COUNT(*) FROM CompetenteJob WHERE id_job = a.id_job), 0), 0) AS scor_potrivire " +
                "FROM Aplicatii a JOIN Candidati c ON a.id_candidat = c.id_candidat " +
                "JOIN Joburi j ON a.id_job = j.id_job " +
                "WHERE j.id_companie = ? AND MATCH(c.cv_text) AGAINST(? IN BOOLEAN MODE) " +
                "ORDER BY a.data_aplicarii DESC";

        try (Connection conn = ConexiuneDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1,this.idCompanie);
            pstmt.setString(2,cuvinteCheie+"*");
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int scor = Math.round(rs.getFloat("scor_potrivire"));
                Object[] rand={
                        rs.getInt("id_aplicatie"),
                        rs.getString("nume_complet"),
                        rs.getString("titlu"),
                        rs.getTimestamp("data_aplicarii"),
                        scor+"%",
                        rs.getString("status")
                };
                modelTabelAplicatii.addRow(rand);
            }
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la cautarea in CV","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

}
