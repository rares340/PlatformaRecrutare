import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CandidatFrame extends JFrame {
    private int idCandidat;

    private JTable tabelJoburi;
    private DefaultTableModel modelTabelJoburi;
    private JTextField txtCautaJob;
    private JComboBox<String> comboOras;

    private JTable tabelAplicatii;
    private DefaultTableModel modelAplicatii;

    public CandidatFrame(int idCandidat) {
        this.idCandidat = idCandidat;
        setTitle("Portal Candidat");
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel panouHeader = new JPanel(new BorderLayout());
        panouHeader.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JLabel lblSalut = new JLabel("Bun venit in portalul tau!");
        lblSalut.setFont(new Font("Arial", Font.BOLD, 18));
        panouHeader.add(lblSalut,BorderLayout.WEST);

        JButton btnProfil = new  JButton("Profilul meu");
        btnProfil.setBackground(new Color(70,130,180));
        btnProfil.setForeground(Color.WHITE);
        btnProfil.setFocusable(false);
        panouHeader.add(btnProfil,BorderLayout.EAST);

        add(panouHeader,BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panouExploreaza = creeazaPanouExploreaza();
        tabbedPane.addTab("Exploreaza Joburi",panouExploreaza);

        JPanel panouAplicatii = creeazaPanouAplicatii();
        tabbedPane.addTab("Aplicatiile Mele",panouAplicatii);

        add(tabbedPane,BorderLayout.CENTER);

        btnProfil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilCandidat dialogProfil = new ProfilCandidat(CandidatFrame.this,idCandidat);
                dialogProfil.setVisible(true);

            }
        });



        incarcaAplicatiileMele();
        incarcaJoburi();

    }

    private JPanel creeazaPanouAplicatii(){
        JPanel panou = new JPanel(new BorderLayout(10,10));
        panou.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String[] coloane = {"Companie","Titlu Job","Data Aplicarii","Status"};
        modelAplicatii = new DefaultTableModel(coloane,0);
        tabelAplicatii = new JTable(modelAplicatii);
        tabelAplicatii.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panou.add(new JScrollPane(tabelAplicatii),BorderLayout.CENTER);

        return panou;
    }

    private JPanel creeazaPanouExploreaza() {
        JPanel panou = new JPanel(new BorderLayout());
        panou.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel panouFiltre =  new JPanel(new FlowLayout(FlowLayout.LEFT, 15,5));

        txtCautaJob = new JTextField(15);
        String[] orase ={"Remote","Bucuresti","Iasi","Baia Mare","Cluj","Satu Mare","Craiova","Constanta"};
        comboOras = new JComboBox<>(orase);
        JButton btnFiltreaza = new JButton("Filtreaza");
        btnFiltreaza.setFocusable(false);

        panouFiltre.add(new JLabel("Caută (Cuvânt cheie):"));
        panouFiltre.add(txtCautaJob);
        panouFiltre.add(new JLabel("Oraș:"));
        panouFiltre.add(comboOras);
        panouFiltre.add(btnFiltreaza);

        panou.add(panouFiltre, BorderLayout.NORTH);

        String[] coloane = {"ID", "Titlu Job", "Companie", "Oraș", "Salariu Min"};
        modelTabelJoburi = new DefaultTableModel(coloane, 0);
        tabelJoburi = new JTable(modelTabelJoburi);
        tabelJoburi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelJoburi.getColumnModel().getColumn(0).setMinWidth(0);
        tabelJoburi.getColumnModel().getColumn(0).setMaxWidth(0);

        panou.add(new JScrollPane(tabelJoburi),BorderLayout.CENTER);

        JPanel panouActiuni = new JPanel(new FlowLayout());
        JButton btnDetalii  = new JButton("Vezi Detalii");
        btnDetalii.setFocusable(false);
        JButton btnAplica = new  JButton("Aplica");
        btnAplica.setBackground(new Color(34, 139, 34));
        btnAplica.setForeground(Color.WHITE);
        btnAplica.setFocusable(false);

        panouActiuni.add(btnDetalii);
        panouActiuni.add(btnAplica);
        panou.add(panouActiuni,BorderLayout.SOUTH);

        btnFiltreaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        btnDetalii.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rand = tabelJoburi.getSelectedRow();
                if(rand==-1){
                    JOptionPane.showMessageDialog(CandidatFrame.this,"Te rog sa selectezi un job","Atentie!",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int idJob = (int) tabelJoburi.getValueAt(rand,0);

                DetaliiJob dialogDetalii = new DetaliiJob(CandidatFrame.this,idCandidat,idJob);
                dialogDetalii.setVisible(true);

                if(dialogDetalii.aAplicatAcum()){
                    incarcaAplicatiileMele();
                }
            }
        });

        btnAplica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rand = tabelJoburi.getSelectedRow();
                if(rand==-1){
                    JOptionPane.showMessageDialog(CandidatFrame.this,"Te rog sa alegi un job","Atentie!",JOptionPane.INFORMATION_MESSAGE);
                }
                int idJob = (int) tabelJoburi.getValueAt(rand,0);

                aplicaLaJob(idJob);
                incarcaAplicatiileMele();
            }
        });

        return panou;
    }

    private void incarcaAplicatiileMele(){
        modelAplicatii.setRowCount(0);

        String sql = "SELECT c.nume AS companie,j.titlu, a.data_aplicarii, a.status "+
                "FROM Aplicatii a INNER JOIN Joburi j ON a.id_job = j.id_job "+
                "INNER JOIN Companii c ON j.id_companie = c.id_companie " +
                "WHERE id_candidat=? ORDER BY a.data_aplicarii DESC";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,this.idCandidat);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Object[] rand ={
                        rs.getString("companie"),
                        rs.getString("titlu"),
                        rs.getTimestamp("data_aplicarii"),
                        rs.getString("status")
                };
                modelAplicatii.addRow(rand);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la incarcarea aplicatiilor","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void incarcaJoburi(){
        String sql = "SELECT j.id_job,j.titlu,c.nume AS companie, j.oras,j.salariu_min "+
                "FROM Joburi j INNER JOIN Companii c ON j.id_companie = c.id_companie "+
                "ORDER BY j.id_job DESC";

        modelTabelJoburi.setRowCount(0);

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()){

            while(rs.next()){
                Object[] rand = {
                        rs.getInt("id_job"),
                        rs.getString("titlu"),
                        rs.getString("companie"),
                        rs.getString("oras"),
                        rs.getInt("salariu_min")
                };
                modelTabelJoburi.addRow(rand);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la incarcarea Joburilor","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void aplicaLaJob(int idJob){
        String sql ="INSERT INTO Aplicatii (id_job, id_candidat) VALUES (?,?)";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,idJob);
            pstmt.setInt(2,idCandidat);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,"Ai aplicat cu succes!","Succes",JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la aplicare","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }
}
