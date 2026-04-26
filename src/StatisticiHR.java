import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticiHR extends JDialog {
    private int idCompanie;
    private JTable tabel;
    private DefaultTableModel modelTabel;

    private JLabel lblTotal;
    private JLabel lblRataOferta;
    private JLabel lblRataInterviu;
    public StatisticiHR(JFrame parent,int idCompanie){
        super(parent);
        this.idCompanie = idCompanie;
        setSize(700,400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15,15));

        JPanel panouConversie = new JPanel(new GridLayout(1,3,10,10));
        panouConversie.setBackground(new Color(240, 248, 255));
        panouConversie.setBorder(BorderFactory.createTitledBorder("Rata de Conversie:"));

        lblTotal = new JLabel("Total Candidati: 0",SwingConstants.CENTER);
        lblRataOferta = new JLabel("Rata Ofertare: 0%",SwingConstants.CENTER);
        lblRataInterviu = new JLabel("Rata Interviu: 0%",SwingConstants.CENTER);

        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblRataOferta.setFont(new Font("Arial", Font.BOLD, 14));
        lblRataInterviu.setFont(new Font("Arial", Font.BOLD, 14));

        panouConversie.add(lblTotal);
        panouConversie.add(lblRataInterviu);
        panouConversie.add(lblRataOferta);
        add(panouConversie,BorderLayout.NORTH);

        JPanel panouTabel = new JPanel(new BorderLayout());
        panouTabel.setBorder(BorderFactory.createTitledBorder("Pipeline Recrutare (Situatie per Job)"));

        String[] coloane = {"Titlu Job", "Total Aplicații", "Noi", "La Interviu", "Ofertati", "Respinși"};
        modelTabel = new DefaultTableModel(coloane, 0);
        tabel = new JTable(modelTabel);
        tabel.setEnabled(false);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(250);

        panouTabel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(panouTabel, BorderLayout.CENTER);

        JPanel panouJos = new JPanel();
        JButton btnInchide = new  JButton("Inchide");
        btnInchide.setFocusable(false);

        btnInchide.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        panouJos.add(btnInchide);
        add(panouJos, BorderLayout.SOUTH);

        incarcaDate();
    }
    private void incarcaDate(){
        String sql = "Select nume_job,total_candidati,stadiu_nou,stadiu_interviu"+
        ",stadiu_oferta,stadiu_respins FROM StatisticiHR WHERE id_companie = ?";

        int totalCompanie = 0;
        int totalInterviuri = 0;
        int totalOferte = 0;

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,idCompanie);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                int totalJob = rs.getInt("total_candidati");
                int interviuri = rs.getInt("stadiu_interviu");
                int oferte = rs.getInt("stadiu_oferta");

                Object[] rand = {
                        rs.getString("nume_job"),
                        totalJob,
                        rs.getInt("stadiu_nou"),
                        interviuri,
                        oferte,
                        rs.getInt("stadiu_respins")
                };
                modelTabel.addRow(rand);

                totalCompanie += totalJob;
                totalInterviuri += interviuri + oferte;
                totalOferte += oferte;
            }
                lblTotal.setText("Total Candidati: " + totalCompanie);
                if (totalCompanie > 0) {
                    int procentInterviu = (totalInterviuri * 100) / totalCompanie;
                    int procentOferta = (totalOferte * 100) / totalCompanie;

                    lblRataInterviu.setText("Rată Interviu: " + procentInterviu + "%");
                    lblRataOferta.setText("Rată Ofertare: " + procentOferta + "%");
                }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la statistica","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }
}
