/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.soundup;

import java.awt.BorderLayout;
import VisualComponent.AudioNormalizer;
import VisualComponent.AudioVisualizer2;
import VisualComponent.LineMusic;

/**
 *
 * @author Somils
 */
public class panelMusic extends javax.swing.JPanel {

    private int duracion;
    private String ruta;
    LineMusic line;
    AudioNormalizer audioProcessor;
    AudioVisualizer2 visualizer;
    private int status = 0;
    int statusInicialGlobal = 0;

    /**
     * Creates new form panelMusic
     */
    public panelMusic(String route, int duration) {
        initComponents();
        duracion = duration;
        ruta = route;
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
        jLabel3 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jPanel2.setBackground(new java.awt.Color(17, 17, 17));
        jPanel2.setLayout(null);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/play.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel2);
        jLabel2.setBounds(100, 260, 40, 40);

        jPanel3.setBackground(new java.awt.Color(17, 17, 17));
        jPanel3.setLayout(null);
        jPanel2.add(jPanel3);
        jPanel3.setBounds(40, 310, 200, 40);

        jPanel4.setBackground(new java.awt.Color(17, 17, 17));

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

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pause.png"))); // NOI18N
        jLabel3.setText("jLabel2");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel3);
        jLabel3.setBounds(150, 260, 40, 40);

        add(jPanel2);
        jPanel2.setBounds(0, 0, 300, 400);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        //LIMPIAR JPANEL PARA PINTAR
        jPanel3.removeAll();
        jPanel4.removeAll();
        statusInicialGlobal = 1;

        //LINEA DE REPRODUCCION
        line = new LineMusic(duracion, ruta);
        jPanel4.setLayout(new BorderLayout());
        line.setOpaque(false);
        jPanel4.add(line, BorderLayout.CENTER);
        this.add(jPanel4, BorderLayout.CENTER);

        //BARRAS DE REPRODUCCION
        audioProcessor = new AudioNormalizer(ruta);
        visualizer = new AudioVisualizer2(audioProcessor.getAudioBytes());
        jPanel3.setLayout(new BorderLayout());
        visualizer.setOpaque(false);
        jPanel3.add(visualizer, BorderLayout.CENTER);
        this.add(jPanel3, BorderLayout.CENTER);

        AudioNormalizer.reproducirCancion(ruta);
        line.start();
        visualizer.startBars();


    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        int tiempo;
        if (statusInicialGlobal == 1) {
            if (status == 0) {

                AudioNormalizer.detenerCancion();
                line.stop();
                visualizer.stopBars();
                status = 1;
                line.status(0);

            } else if (status == 1) {
                //  AudioNormalizer.reproducirCancion(ruta);
                tiempo = line.start();
                visualizer.startBars();
                status = 0;
                AudioNormalizer.reproducirCancionDesde(ruta, tiempo);
                statusInicialGlobal = 1;
                line.status(1);

            }
        }
    }//GEN-LAST:event_jLabel3MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
}
