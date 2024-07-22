/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.soundup;

import MsgEmergentes.MsgConfirmar;
import MsgEmergentes.MsgEmerge;
import MsgEmergentes.MsgLoadd;
import RMS.AudioEnhanceFile;
import RMS.FileSelection;
import java.awt.Color;
import java.awt.Point;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Somils
 */
public class menuFiles extends javax.swing.JFrame {

    /**
     * Creates new form menuFiles
     */
    public String rutaOriginal;
    public String rutaArchivoWav;
    public String rutaRMS;
    public String rutaPDA;
    public boolean stereo;
    public boolean RMS;
    public boolean PDA;

    public menuFiles(boolean stereo, boolean RMS, boolean PDA) {
        initComponents();
        this.setLocationRelativeTo(this);
        this.stereo = stereo;
        this.RMS = RMS;
        this.PDA = PDA;

        //RMS
        if (RMS) {
            jPanel2.setBackground(new Color(153, 255, 153));
            jPanel2.revalidate();
            jPanel2.repaint();

            jLabel3.setForeground(new Color(0, 0, 0));
            jLabel3.revalidate();
            jLabel3.repaint();

            jLabel6.setForeground(new Color(0, 0, 0));
            jLabel6.revalidate();
            jLabel6.repaint();

        }
        //PDA
        if (PDA) {
            jPanel5.setBackground(new Color(153, 255, 153));
            jPanel5.revalidate();
            jPanel5.repaint();

            jLabel4.setForeground(new Color(0, 0, 0));
            jLabel4.revalidate();
            jLabel4.repaint();

            jLabel7.setForeground(new Color(0, 0, 0));
            jLabel7.revalidate();
            jLabel7.repaint();
        }
        //STEREO
        if (stereo) {
            jPanel6.setBackground(new Color(153, 255, 153));
            jPanel6.revalidate();
            jPanel7.repaint();

            jLabel2.setForeground(new Color(0, 0, 0));
            jLabel2.revalidate();
            jLabel2.repaint();

            jLabel5.setForeground(new Color(0, 0, 0));
            jLabel5.revalidate();
            jLabel5.repaint();
        }

    }

    public void Asignar(String rutaOriginal, String rutaArchivoWav, String rutaRMS, String rutaPDA) {
        this.rutaOriginal = rutaOriginal;
        this.rutaArchivoWav = rutaArchivoWav;
        this.rutaRMS = rutaRMS;
        this.rutaPDA = rutaPDA;
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
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(550, 230));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(550, 230));
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(17, 17, 17));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(153, 255, 153));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });
        jPanel2.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("RMS");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(80, 20, 100, 40);

        jLabel6.setFont(new java.awt.Font("Microsoft YaHei", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Mejoramiento");
        jPanel2.add(jLabel6);
        jLabel6.setBounds(70, 60, 130, 19);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(20, 20, 250, 90);

        jPanel5.setBackground(new java.awt.Color(238, 100, 100));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });
        jPanel5.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("PDA");
        jPanel5.add(jLabel4);
        jLabel4.setBounds(80, 20, 100, 40);

        jLabel7.setFont(new java.awt.Font("Microsoft YaHei", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Mejoramiento");
        jPanel5.add(jLabel7);
        jLabel7.setBounds(70, 60, 130, 19);

        jPanel1.add(jPanel5);
        jPanel5.setBounds(280, 20, 250, 90);

        jPanel6.setBackground(new java.awt.Color(238, 100, 100));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });
        jPanel6.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("STEREO");
        jPanel6.add(jLabel2);
        jLabel2.setBounds(50, 20, 130, 40);

        jLabel5.setFont(new java.awt.Font("Microsoft YaHei", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Conversion");
        jPanel6.add(jLabel5);
        jLabel5.setBounds(70, 60, 130, 19);

        jPanel1.add(jPanel6);
        jPanel6.setBounds(20, 120, 250, 90);

        jPanel7.setBackground(new java.awt.Color(238, 36, 45));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cerrar-sesion.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel7);
        jPanel7.setBounds(280, 120, 250, 90);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 550, 230);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed

    }//GEN-LAST:event_formMousePressed

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        AudioEnhanceFile.eliminarArchivo(rutaRMS);
        AudioEnhanceFile.eliminarArchivo(rutaPDA);
        AudioEnhanceFile.eliminarArchivo(rutaArchivoWav);
        this.dispose();
    }//GEN-LAST:event_jPanel7MouseClicked

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked

        if (RMS) {
            FileSelection fileselection = new FileSelection(rutaOriginal, rutaArchivoWav, rutaRMS);
            fileselection.isMenu(true, stereo, RMS, PDA, rutaOriginal, rutaArchivoWav, rutaRMS, rutaPDA, true, false);
            fileselection.setVisible(true);
            this.dispose();
        } else {
            MsgEmerge cambiosrealizados = new MsgEmerge("Esta cancion no necesita mejoramiento RMS");
            cambiosrealizados.setVisible(true);
        }


    }//GEN-LAST:event_jPanel2MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

        if (RMS) {
            FileSelection fileselection = new FileSelection(rutaOriginal, rutaArchivoWav, rutaPDA);
            fileselection.isMenu(true, stereo, RMS, PDA, rutaOriginal, rutaArchivoWav, rutaRMS, rutaPDA, false, true);
            fileselection.setVisible(true);
            this.dispose();
        } else {
            MsgEmerge cambiosrealizados = new MsgEmerge("Esta cancion no necesita mejoramiento RMS");
            cambiosrealizados.setVisible(true);
        }
    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        if (stereo) {
            SwingUtilities.invokeLater(() -> {
                MsgConfirmar msgConfirmar = new MsgConfirmar("¿Estás seguro de aplicar los cambios?");
                msgConfirmar.setVisible(true);
                // Crear un SwingWorker para esperar hasta que el usuario tome una decisión
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        // Esperar hasta que el usuario tome una decisión
                        while (msgConfirmar.isConfirmed() == -1) {
                            try {
                                Thread.sleep(100); // Dormir por 100 milisegundos para no bloquear el hilo de UI
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Obtener el resultado y continuar con la lógica
                        int resultado = msgConfirmar.isConfirmed();
                        if (resultado == 1) {
                            MsgLoadd cargando = new MsgLoadd();
                            cargando.setVisible(true);
                            Thread backgroundProcessThread = new Thread(() -> {

                                AudioEnhanceFile.replaceFile(rutaOriginal, rutaArchivoWav);
                                cargando.setVisible(false);  //por ejemplo pones para que se ejecute una ventana de cargando, cuando
                                //termine el proceso haz que se quite la ventana de cargando.

                                // Actualizar el estado del JFrame
                                SwingUtilities.invokeLater(() -> {

                                });
                            });
                            backgroundProcessThread.start();
                            jPanel6.setBackground(new Color(153, 255, 153));
                            jPanel6.revalidate();
                            jPanel7.repaint();

                            jLabel2.setForeground(new Color(0, 0, 0));
                            jLabel2.revalidate();
                            jLabel2.repaint();

                            jLabel5.setForeground(new Color(0, 0, 0));
                            jLabel5.revalidate();
                            jLabel5.repaint();
                            MsgEmerge cambiosrealizados = new MsgEmerge("Los cambios han sido realizados");
                            cambiosrealizados.setVisible(true);
                            System.out.println("El usuario confirmó.");
                        } else if (resultado == 0) {
                            // Código a ejecutar si el usuario cancela
                            System.out.println("El usuario canceló.");
                        }
                        msgConfirmar.setVisible(false);
                    }
                }.execute();
            });
        } else {
            MsgEmerge cambiosrealizados = new MsgEmerge("Esta cancion ya esta en Stereo");
            cambiosrealizados.setVisible(true);
        }
    }//GEN-LAST:event_jPanel6MouseClicked

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
            java.util.logging.Logger.getLogger(menuFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menuFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menuFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menuFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menuFiles(false, false, false).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    // End of variables declaration//GEN-END:variables
}
