/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package dir;

import java.awt.BorderLayout;
import java.awt.Color;
import pruebas.AudioNormalizer;
import pruebas.AudioVisualizer2;
import pruebas.LineMusic;

/**
 *
 * @author Somils
 */
public class panelMusic extends javax.swing.JPanel {

    /**
     * Creates new form panelMusic
     */
    public panelMusic() {
        initComponents();

        LineMusic line = new LineMusic();
        jPanel4.setLayout(new BorderLayout());
        line.setOpaque(false);
        jPanel4.add(line, BorderLayout.CENTER);
        this.add(jPanel4, BorderLayout.CENTER);
      //this.getContentPane().setBackground(new Color(0, 0, 0, 0));

        AudioNormalizer audioProcessor = new AudioNormalizer("src/main/java/resources/excusa.wav");
        AudioVisualizer2 visualizer = new AudioVisualizer2(audioProcessor.getAudioBytes());
        jPanel3.setLayout(new BorderLayout());
        visualizer.setOpaque(false);
        jPanel3.add(visualizer, BorderLayout.CENTER);
        this.add(jPanel3, BorderLayout.CENTER);
      //this.getContentPane().setBackground(new Color(0, 0, 0, 0));

        
      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jPanel2.setBackground(new java.awt.Color(8, 7, 44));
        jPanel2.setLayout(null);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/play.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(120, 260, 40, 40);

        jPanel3.setBackground(new java.awt.Color(8, 7, 44));
        jPanel3.setLayout(null);
        jPanel2.add(jPanel3);
        jPanel3.setBounds(40, 310, 200, 40);

        jPanel4.setBackground(new java.awt.Color(8, 7, 44));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4);
        jPanel4.setBounds(40, 210, 200, 40);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/albumMusic.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(40, 20, 200, 200);

        add(jPanel2);
        jPanel2.setBounds(0, 0, 300, 400);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
}
