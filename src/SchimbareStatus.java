import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SchimbareStatus extends JDialog {
    private boolean aFostSalvat = false;

    public SchimbareStatus(JFrame parinte,int idAplicatie,String numeCandidat,String statusCurent) {
        super(parinte,"Schimba status",true);
        setSize(350,180);
        setLocationRelativeTo(parinte);
        setLayout(new GridLayout(3,1,10,10));

        JPanel panouInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panouInfo.add(new JLabel("Candidat: "+ numeCandidat));

        JPanel panouStatus = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panouStatus.add(new JLabel("Etapa noua: "));

        String[] etapePipeline = {"Nou","Interviu","Oferta","Respins"};
        JComboBox<String> comboStatus = new JComboBox<>(etapePipeline);
        comboStatus.setSelectedItem(statusCurent);
        panouStatus.add(comboStatus);

        JPanel panouButoane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSalveaza = new JButton("Salveaza");
        JButton btnAnuleaza = new JButton("Anuleaza");
        panouButoane.add(btnSalveaza);
        panouButoane.add(btnAnuleaza);

        add(panouInfo);
        add(panouStatus);
        add(panouButoane);

        btnSalveaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String statusNou=(String) comboStatus.getSelectedItem();
                actualizareStatus(idAplicatie,statusNou);
                aFostSalvat=true;
                dispose();
            }
        });
        btnAnuleaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
    }
    private void actualizareStatus(int idAplicatie,String statusNou){
        String sql = "UPDATE Aplicatii SET status = ? WHERE id_aplicatie = ?";

        try(Connection conn = ConexiuneDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,statusNou);
            pstmt.setInt(2,idAplicatie);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,"Candidatul a fost mutat in etapa: "+statusNou,"Succes",JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Eroare la actualizarea statusului","Eroare",JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean aFostSalvat(){
        return aFostSalvat;
    }
}
