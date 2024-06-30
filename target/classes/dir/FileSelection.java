/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dir;

import com.mycompany.soundup.AudioEnhanceFile;
import com.mycompany.soundup.MsgEmerge;
import com.mycompany.soundup.MsgLoadd;
import com.mycompany.soundup.StartMenu;
import pruebas.AudioNormalizer;
import java.awt.BorderLayout;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.jaudiotagger.tag.TagException;

/**
 *
 * @author Somils
 */
public class FileSelection extends javax.swing.JFrame {

    private Point point;
    private String route1;
    private String route2;

    /**
     * Creates new form FileSelection
     */
    public FileSelection(String ruta1, String ruta2) {
        try {
            initComponents();
            route1 = ruta1;
            route2 = ruta2;
            this.setLocationRelativeTo(this);
            int duracion1 = AudioNormalizer.DuracionCancion(ruta1);

            panelMusic panel_antes = new panelMusic(ruta1, duracion1);
            jPanel1.setLayout(new BorderLayout());
            jPanel1.add(panel_antes);
            this.add(jPanel1);

            panelMusic panel_despues = new panelMusic(ruta2, duracion1);
            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(panel_despues);
            this.add(jPanel3);

        } catch (TagException ex) {
            Logger.getLogger(FileSelection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(900, 650));
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(8, 7, 44));
        jPanel1.setLayout(null);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(80, 70, 300, 400);

        jPanel3.setBackground(new java.awt.Color(8, 7, 44));
        jPanel3.setLayout(null);
        getContentPane().add(jPanel3);
        jPanel3.setBounds(490, 70, 300, 400);

        jPanel2.setBackground(new java.awt.Color(30, 43, 75));
        jPanel2.setLayout(null);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/mycompany/soundup/img/minimizar.png"))); // NOI18N
        jLabel5.setText("jLabel5");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel5);
        jLabel5.setBounds(820, 10, 30, 30);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cerrar.png"))); // NOI18N
        jLabel6.setText("jLabel5");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel6MouseEntered(evt);
            }
        });
        jPanel2.add(jLabel6);
        jLabel6.setBounds(860, 10, 30, 30);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 900, 70);

        jPanel4.setBackground(new java.awt.Color(30, 43, 75));
        jPanel4.setLayout(null);
        getContentPane().add(jPanel4);
        jPanel4.setBounds(380, 70, 110, 400);

        jPanel5.setBackground(new java.awt.Color(30, 43, 75));
        jPanel5.setLayout(null);
        getContentPane().add(jPanel5);
        jPanel5.setBounds(790, 70, 110, 400);

        jPanel6.setBackground(new java.awt.Color(30, 43, 75));
        jPanel6.setLayout(null);
        getContentPane().add(jPanel6);
        jPanel6.setBounds(0, 70, 80, 400);

        jPanel7.setBackground(new java.awt.Color(30, 43, 75));
        jPanel7.setLayout(null);

        jButton1.setText("APLICAR CAMBIOS");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton1);
        jButton1.setBounds(350, 80, 160, 50);

        getContentPane().add(jPanel7);
        jPanel7.setBounds(0, 470, 900, 180);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        this.setExtendedState(ICONIFIED);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        AudioNormalizer.finalizarProcesoCancion(route2);
        AudioEnhanceFile.eliminarArchivo(route2);
        this.dispose();

        StartMenu menu = new StartMenu();
        menu.setVisible(true);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        int CurrentX = this.getLocation().x;
        int CurrentY = this.getLocation().y;

        int MoveX = (CurrentX + evt.getX()) - (CurrentX + point.x);
        int MoveY = (CurrentY + evt.getY()) - (CurrentY + point.y);

        int x = CurrentX + MoveX;
        int y = CurrentY + MoveY;

        this.setLocation(x, y);
    }//GEN-LAST:event_formMouseDragged

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    }//GEN-LAST:event_formMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        point = evt.getPoint();
        getComponentAt(point);
    }//GEN-LAST:event_formMousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here

        MsgLoadd cargando = new MsgLoadd();
        cargando.setVisible(true);
        Thread backgroundProcessThread = new Thread(() -> {
            AudioNormalizer.detenerCancion();
            AudioNormalizer.finalizarProcesoCancion(route1);
            AudioNormalizer.finalizarProcesoCancion(route2);
            AudioEnhanceFile.eliminarArchivo(route2);
            AudioEnhanceFile.Mejorar(route1, 1);
            MsgEmerge cambiosrealizados = new MsgEmerge("Los cambios han sido realizados");

            cambiosrealizados.setVisible(true);
            this.dispose();
            cargando.setVisible(false);  //por ejemplo pones para que se ejecute una ventana de cargando, cuando
            //termine el proceso haz que se quite la ventana de cargando.
            StartMenu st = new StartMenu();
            st.setVisible(true);

            // Actualizar el estado del JFrame
            SwingUtilities.invokeLater(() -> {

            });
        });
        backgroundProcessThread.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel6MouseEntered

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FileSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FileSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FileSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FileSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FileSelection("vacio", "vacio").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    // End of variables declaration//GEN-END:variables
}
