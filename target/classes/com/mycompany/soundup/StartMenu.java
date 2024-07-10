/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.soundup;

/**
 *
 * @author Somils
 */
import RMS.AudioEnhanceFile;
import RMS.AudioEnhanceDir;
import MsgEmergentes.MsgEmerge;
import MsgEmergentes.MsgLoadd;
import MsgEmergentes.MsgLoadNumeric;
import static RMS.AudioEnhanceDir.EncontrarNecesitanNormalizar;
import static RMS.AudioEnhanceDir.vamosAmejorar;
import RMS.AudioEnhanceFile.BooleanDoublePair;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import jnafilechooser.api.JnaFileChooser;
import VisualComponent.AudioVisualizer;
import VisualComponent.AudioNormalizer;
import RMS.FileSelection;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartMenu extends javax.swing.JFrame {

    private Point point;
    private static AtomicInteger totalAudioFiles = new AtomicInteger(0);
    MsgLoadNumeric cargando = new MsgLoadNumeric();
    private static principal pp;

    public StartMenu() {
        setUndecorated(true);
        initComponents();

        this.setLocationRelativeTo(this);
        SetImageLabel(jLabel3, "/resources/icon2.png");
        SetImageLabel(jLabel4, "/resources/background.png");
        String rutacancion = "/resources/excusa.wav";
        AudioNormalizer audioProcessor = new AudioNormalizer(rutacancion);
        AudioVisualizer visualizer = new AudioVisualizer(audioProcessor.getAudioBytes());

        visualizer.setOpaque(true);
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(visualizer, BorderLayout.CENTER);

        this.add(jPanel2, BorderLayout.CENTER);
        this.getContentPane().setBackground(new Color(0, 0, 0, 0));

    }

    public void asignar(int num, int opcion) {

        cargando.updateTotalAudioFiles(num, opcion);

    }

    public static void CerrarPrincipal() {
        pp.setVisible(false);
        StartMenu st = new StartMenu();
        st.setVisible(true);

    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void SetImageLabel(JLabel labelName, String root) {
        ImageIcon image = new ImageIcon(getClass().getResource(root));
        Icon icon = new ImageIcon(
                image.getImage().getScaledInstance(labelName.getWidth(), labelName.getHeight(), Image.SCALE_DEFAULT)
        );
        labelName.setIcon(icon);
        this.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(35, 35, 35));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0, 0));
        jPanel2.setOpaque(false);
        jPanel1.add(jPanel2);
        jPanel2.setBounds(60, 90, 840, 120);

        jLabel1.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(47, 237, 203));
        jLabel1.setText("SoundUp ");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(80, 30, 390, 40);

        jLabel2.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Software development by Somil");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(70, 70, 166, 15);

        jLabel3.setText("jLabel3");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 70);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/minimizar.png"))); // NOI18N
        jLabel5.setText("jLabel5");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel5);
        jLabel5.setBounds(910, 10, 30, 30);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cerrar.png"))); // NOI18N
        jLabel6.setText("jLabel5");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel6);
        jLabel6.setBounds(950, 10, 30, 30);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(null);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icon_music.png"))); // NOI18N
        jLabel8.setText("jLabel8");
        jLabel8.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel8MouseMoved(evt);
            }
        });
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel8MouseExited(evt);
            }
        });
        jPanel5.add(jLabel8);
        jLabel8.setBounds(0, 0, 250, 250);

        jLabel9.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("SELECCIONAR MUSICA");
        jPanel5.add(jLabel9);
        jLabel9.setBounds(40, 230, 190, 19);

        jPanel1.add(jPanel5);
        jPanel5.setBounds(560, 260, 250, 250);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(null);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icon_dir.png"))); // NOI18N
        jLabel7.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel7MouseMoved(evt);
            }
        });
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel7MouseExited(evt);
            }
        });
        jPanel3.add(jLabel7);
        jLabel7.setBounds(0, 0, 250, 240);

        jPanel1.add(jPanel3);
        jPanel3.setBounds(170, 260, 250, 240);

        jLabel10.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText(" SELECCIONAR CARPETA");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(210, 490, 190, 30);

        jLabel4.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        jLabel4.setText("jLabel4");
        jLabel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel4MouseDragged(evt);
            }
        });
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        jPanel1.add(jLabel4);
        jLabel4.setBounds(0, 0, 990, 530);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 989, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
        point = evt.getPoint();
        getComponentAt(point);
    }//GEN-LAST:event_jLabel4MousePressed

    private void jLabel4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseDragged
        int CurrentX = this.getLocation().x;
        int CurrentY = this.getLocation().y;

        int MoveX = (CurrentX + evt.getX()) - (CurrentX + point.x);
        int MoveY = (CurrentY + evt.getY()) - (CurrentY + point.y);

        int x = CurrentX + MoveX;
        int y = CurrentY + MoveY;

        this.setLocation(x, y);
    }//GEN-LAST:event_jLabel4MouseDragged

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        this.setExtendedState(ICONIFIED);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked

        JnaFileChooser ch = new JnaFileChooser();
        ch.setMode(JnaFileChooser.Mode.Directories);
        boolean action = ch.showOpenDialog(this);

        cargando.setVisible(true);

        Thread backgroundProcessThread = new Thread(() -> {
            if (action) {
                try {
                    File selectedFile = ch.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();

                    totalAudioFiles.set(AudioEnhanceDir.contarArchivosDeAudio(filePath));
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.scheduleAtFixedRate(() -> {
                        asignar(AudioEnhanceDir.returnNumeroActual(), 0);
                    }, 0, 1, TimeUnit.SECONDS);

                    List<AudioEnhanceDir.RutaRmsPar> NecesitaNormalizacion = EncontrarNecesitanNormalizar(filePath);
                    scheduler.shutdown();
                    scheduler.awaitTermination(1, TimeUnit.MINUTES);

                    if (NecesitaNormalizacion != null && !NecesitaNormalizacion.isEmpty()) {
                        System.out.println("Archivos que necesitan normalización:");
                        ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
                        scheduler2.scheduleAtFixedRate(() -> {
                            asignar(AudioEnhanceDir.returnNumeroActual(), 1);
                        }, 0, 1, TimeUnit.SECONDS);
                        List<AudioEnhanceDir.Rutas> estanMejorados = vamosAmejorar(NecesitaNormalizacion);
                        scheduler.shutdown();
                        scheduler.awaitTermination(1, TimeUnit.MINUTES);
                        pp = new principal(estanMejorados, filePath);
                        pp.setVisible(true);
                        this.dispose();
                    } else {
                        MsgEmerge me = new MsgEmerge("No hay archivos de audio por mejorar");
                        me.setVisible(true);
                    }
                    /*    
                     */
                } catch (InterruptedException ex) {
                    Logger.getLogger(StartMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            cargando.setVisible(false);  //por ejemplo pones para que se ejecute una ventana de cargando, cuando
            //termine el proceso haz que se quite la ventana de cargando.
            // Actualizar el estado del JFrame
            SwingUtilities.invokeLater(() -> {

            });
        });
        backgroundProcessThread.start();
        /*      } else {
            MsgEmerge mg = new MsgEmerge("Selecione un archivo");
            mg.setVisible(true);
        }*/
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        JnaFileChooser ch = new JnaFileChooser();
        boolean action = ch.showOpenDialog(this);
        if (action) {
            File selectedFile = ch.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            MsgLoadd cargando = new MsgLoadd();
            cargando.setVisible(true);

            Thread backgroundProcessThread = new Thread(() -> {
                // Simular un proceso que toma tiempo (por ejemplo, 10 segundos)
                //    Thread.sleep(10000);
                BooleanDoublePair need = AudioEnhanceFile.necesitaNormalizacion(filePath);
                if (need.flag) {
                    String outputFilePath = AudioEnhanceFile.convertToWavString(filePath);
                    String rutaArchivoMejorado = AudioEnhanceFile.Mejorar(outputFilePath, 0, need.value);
                    this.dispose();
                    //     cargando.setVisible(false);
                    FileSelection fileselection = new FileSelection(filePath, outputFilePath, rutaArchivoMejorado, need.value);
                    fileselection.setVisible(true);

                } else {
                    MsgEmerge cambiosrealizados = new MsgEmerge("Este archivo de audio no necesita mejora");
                    cambiosrealizados.setVisible(true);
                }
                cargando.setVisible(false);
                // Actualizar el estado del JFrame
                SwingUtilities.invokeLater(() -> {

                });
            });
            backgroundProcessThread.start();

        }


    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel7MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseMoved
        // TODO add your handling code here:
        SetImageLabel(jLabel7, "/resources/icon_dir_selection.png");

    }//GEN-LAST:event_jLabel7MouseMoved

    private void jLabel7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseExited
        SetImageLabel(jLabel7, "/resources/icon_dir.png");
    }//GEN-LAST:event_jLabel7MouseExited

    private void jLabel8MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseMoved
        SetImageLabel(jLabel8, "/resources/icon_music_selection.png");
    }//GEN-LAST:event_jLabel8MouseMoved

    private void jLabel8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseExited
        SetImageLabel(jLabel8, "/resources/icon_music.png");
    }//GEN-LAST:event_jLabel8MouseExited

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
            java.util.logging.Logger.getLogger(StartMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartMenu().setVisible(true);

            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    // End of variables declaration//GEN-END:variables
}
