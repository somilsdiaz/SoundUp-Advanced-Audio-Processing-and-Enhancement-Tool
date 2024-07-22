/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.soundup;

import ConvertirStereo.panelStereos;
import Directorios.DirectoryTree;
import MsgEmergentes.MsgLoadd;
import MsgEmergentes.NoFound;
import MsgEmergentes.cambiosHechos;
import PDA.AudioEnhancer;
import RMS.AudioEnhanceDir;
import RMS.AudioEnhanceDir.ListasRMS_PDA;
import static RMS.AudioEnhanceDir.isAudioFile;
import RMS.panelDir;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingUtilities;

/**
 *
 * @author Somils
 */
public class principal extends javax.swing.JFrame {

    /**
     * Creates new form principal
     */
    int statusStereo = 0;
    static int cambiosAplicadosStereo = 0;
    static int cambiosAplicadosRMS = 0;
    static int cambiosAplicadosPDA = 0;
    List<AudioEnhanceDir.Rutas> estanMejorados;
    int statusPDA = 0;
    String route;
    private Point point;
    public static DirectoryTree treeStereo;
    panelStereos ps;
    NoFound nf;
    panelDir pdPDA;
    boolean yaBuscoStereos = false;
    int numeroCancionesStereo = 0;
    int numeroCancionePDA = 0;
    panelDir pd;
    int cop1 = 0;
    int cop2 = 0;
    int cop3 = 0;
    int cop4 = 0;
    ListasRMS_PDA listas;

    public principal(List<AudioEnhanceDir.Rutas> estanMejorados, String ruta, ListasRMS_PDA listas) {
        initComponents();
        setIconImage(getIconImage());
        route = ruta;
        this.estanMejorados = estanMejorados;
        this.listas = listas;
        this.setLocationRelativeTo(this);
        jPanel4.setBackground(new java.awt.Color(102, 102, 102));
        NoFound nf = new NoFound("por mejorar");

        if (!(estanMejorados == null)) {
            pd = new panelDir(estanMejorados, ruta, RMS.AudioEnhanceDir.tree, 0);
            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(pd);
            this.add(jPanel3);
        } else {
            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(nf);
            this.add(jPanel3);
        }
    }

    public static void cambiosAplicadosStereo() {
        cambiosAplicadosStereo = 1;
    }

    public static void cambiosAplicadosRMS() {
        cambiosAplicadosRMS = 2;
    }

    public static void cambiosAplicadosPDA() {
        cambiosAplicadosPDA = 3;
    }

    public void ponerPanelCambios() {
        jPanel3.removeAll();
        cambiosHechos ch = new cambiosHechos();
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(ch);
        this.add(jPanel3);
        jPanel3.revalidate();
        jPanel3.repaint();

    }

    @Override
    public Image getIconImage() {
        java.net.URL url = ClassLoader.getSystemResource("resources/iconMain.png");
        if (url != null) {
            return Toolkit.getDefaultToolkit().getImage(url);
        } else {
            System.err.println("Resource not found: resources/iconMain.png");
            return null;
        }
    }

    private boolean isStereo(File audioFile) {
        boolean is = false;
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(audioFile);
            AudioFormat format = fileFormat.getFormat();
            int channels = format.getChannels();

            if (channels == 1) {
                is = false;
            } else if (channels == 2) {
                is = true;
            } else {
                System.out.println("The audio file has " + channels + " channels.");
            }
        } catch (IOException | UnsupportedAudioFileException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        }

        return is;
    }

    private void RecorrerDirectorioFindStereos(String ruta) {
        yaBuscoStereos = true;
        treeStereo = new DirectoryTree(ruta);
        String directoryPath = ruta;

        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(audioFiles.size(), Runtime.getRuntime().availableProcessors()));

            customThreadPool.submit(()
                    -> audioFiles.parallelStream().forEach(audioFile -> {
                        boolean is = isStereo(audioFile.toFile());
                        if (!is) {
                            treeStereo.addFile(audioFile.toAbsolutePath().toString());
                            numeroCancionesStereo = numeroCancionesStereo + 1;
                        }
                    })
            ).get();

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SoundUp");
        setMinimumSize(new java.awt.Dimension(900, 670));
        setUndecorated(true);
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

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cerrar.png"))); // NOI18N
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel6MouseEntered(evt);
            }
        });
        getContentPane().add(jLabel6);
        jLabel6.setBounds(860, 10, 30, 30);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/minimizar.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel5);
        jLabel5.setBounds(820, 10, 30, 30);

        jPanel3.setBackground(new java.awt.Color(8, 7, 44));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(null);
        getContentPane().add(jPanel3);
        jPanel3.setBounds(180, 0, 720, 670);

        jPanel1.setBackground(new java.awt.Color(8, 7, 44));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(30, 43, 75));
        jPanel2.setLayout(null);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icon2.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(0, 0, 70, 60);

        jLabel2.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(47, 237, 203));
        jLabel2.setText("SoundUp ");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(60, 10, 120, 40);

        jLabel3.setFont(new java.awt.Font("Microsoft YaHei", 0, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SANDY   HNS   SANDOVAL");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(20, 650, 170, 20);

        jLabel4.setFont(new java.awt.Font("Microsoft YaHei", 0, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("SOFTWARE  ESPECIALIZADO ");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(20, 610, 170, 20);

        jLabel7.setFont(new java.awt.Font("Microsoft YaHei", 0, 10)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("PARA ORGANIZACION");
        jPanel2.add(jLabel7);
        jLabel7.setBounds(30, 630, 140, 20);

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));
        jPanel7.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel7MouseMoved(evt);
            }
        });
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel7MouseExited(evt);
            }
        });
        jPanel7.setLayout(null);

        jLabel13.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("de volumen");
        jPanel7.add(jLabel13);
        jLabel13.setBounds(50, 30, 140, 20);

        jLabel14.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Ajuste estandarizado");
        jPanel7.add(jLabel14);
        jLabel14.setBounds(20, 10, 140, 20);

        jPanel2.add(jPanel7);
        jPanel7.setBounds(0, 370, 180, 60);

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel6MouseMoved(evt);
            }
        });
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel6MouseExited(evt);
            }
        });
        jPanel6.setLayout(null);

        jLabel12.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Convertidor a Stereo");
        jPanel6.add(jLabel12);
        jLabel12.setBounds(30, 20, 130, 20);

        jPanel2.add(jPanel6);
        jPanel6.setBounds(0, 280, 180, 60);

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel5MouseMoved(evt);
            }
        });
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel5MouseExited(evt);
            }
        });
        jPanel5.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("de Audio por Superposicion ");
        jPanel5.add(jLabel9);
        jLabel9.setBounds(10, 30, 170, 20);

        jLabel10.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Potenciación Dinámica ");
        jPanel5.add(jLabel10);
        jLabel10.setBounds(30, 10, 140, 20);

        jPanel2.add(jPanel5);
        jPanel5.setBounds(0, 190, 180, 60);

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel4MouseMoved(evt);
            }
        });
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel4MouseExited(evt);
            }
        });
        jPanel4.setLayout(null);

        jLabel8.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Mejoramiento por RMS");
        jPanel4.add(jLabel8);
        jLabel8.setBounds(20, 20, 140, 20);

        jPanel2.add(jPanel4);
        jPanel4.setBounds(0, 100, 180, 60);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 180, 670);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 900, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        this.setExtendedState(ICONIFIED);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        AudioEnhanceDir.eliminarArchivosNormalizados();
        AudioEnhancer.eliminarArchivosPDA();

        this.dispose();

        StartMenu menu = new StartMenu();
        menu.setVisible(true);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel6MouseEntered

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        int CurrentX = this.getLocation().x;
        int CurrentY = this.getLocation().y;

        int MoveX = (CurrentX + evt.getX()) - (CurrentX + point.x);
        int MoveY = (CurrentY + evt.getY()) - (CurrentY + point.y);

        int x = CurrentX + MoveX;
        int y = CurrentY + MoveY;

        this.setLocation(x, y);
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        point = evt.getPoint();
        getComponentAt(point);
    }//GEN-LAST:event_formMousePressed

    private void jPanel4MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseMoved
        jPanel4.setBackground(new java.awt.Color(102, 102, 102));
    }//GEN-LAST:event_jPanel4MouseMoved

    private void jPanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseExited
        if (cop1 == 0) {
            jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        }
    }//GEN-LAST:event_jPanel4MouseExited

    private void jPanel5MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseMoved
        jPanel5.setBackground(new java.awt.Color(102, 102, 102));
    }//GEN-LAST:event_jPanel5MouseMoved

    private void jPanel5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseExited
        if (cop2 == 0) {
            jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        }
    }//GEN-LAST:event_jPanel5MouseExited

    private void jPanel6MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseMoved
        jPanel6.setBackground(new java.awt.Color(102, 102, 102));
    }//GEN-LAST:event_jPanel6MouseMoved

    private void jPanel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseExited
        if (cop3 == 0) {
            jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        }
    }//GEN-LAST:event_jPanel6MouseExited

    private void jPanel7MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseMoved
        jPanel7.setBackground(new java.awt.Color(102, 102, 102));
    }//GEN-LAST:event_jPanel7MouseMoved

    private void jPanel7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseExited
        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
    }//GEN-LAST:event_jPanel7MouseExited

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        jPanel3.removeAll();
        cop1 = 0;
        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        cop2 = 0;
        jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        cop3 = 1;
        cop4 = 0;
        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        if (cambiosAplicadosStereo != 1) {
            numeroCancionesStereo = 0;
            MsgLoadd ml = new MsgLoadd();
            ml.setVisible(true);
            Thread backgroundProcessThread = new Thread(() -> {
                if (statusStereo == 0) {
                    RecorrerDirectorioFindStereos(route);
                    statusStereo = 1;
                    if (numeroCancionesStereo != 0) {
                        ps = new panelStereos(route, numeroCancionesStereo, treeStereo);
                        jPanel3.setLayout(new BorderLayout());
                        jPanel3.add(ps);
                        this.add(jPanel3);
                        jPanel3.revalidate();
                        jPanel3.repaint();
                        yaBuscoStereos = true;

                    } else {
                        nf = new NoFound("en mono");
                        jPanel3.setLayout(new BorderLayout());
                        jPanel3.add(nf);
                        this.add(jPanel3);
                        jPanel3.revalidate();
                        jPanel3.repaint();
                        yaBuscoStereos = false;
                    }
                }
                ml.setVisible(false);
                SwingUtilities.invokeLater(() -> {

                });
            });
            backgroundProcessThread.start();
            jPanel3.setLayout(new BorderLayout());
            if (yaBuscoStereos) {
                jPanel3.add(ps);
            } else {
                if (nf != null) {
                    jPanel3.add(nf);
                }
            }
            this.add(jPanel3);
            jPanel3.revalidate();
            jPanel3.repaint();

            jLabel5.revalidate();
            jLabel6.revalidate();
            jLabel5.repaint();
            jLabel6.repaint();
        } else if (cambiosAplicadosStereo == 1) {
            principal.jPanel3.removeAll();
            cambiosHechos ch = new cambiosHechos();
            principal.jPanel3.setLayout(new BorderLayout());
            principal.jPanel3.add(ch);
            principal.jPanel3.revalidate();
            principal.jPanel3.repaint();
        }

    }//GEN-LAST:event_jPanel6MouseClicked

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        jPanel3.removeAll();
        cop1 = 1;
        cop2 = 0;
        jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        cop3 = 0;
        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        cop4 = 0;
        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        if (cambiosAplicadosRMS != 2) {
            if (!(estanMejorados == null)) {
                jPanel3.setLayout(new BorderLayout());
                jPanel3.add(pd);
                this.add(jPanel3);
                jPanel3.revalidate();
            } else {
                NoFound nf = new NoFound("por mejorar");
                jPanel3.setLayout(new BorderLayout());
                jPanel3.add(nf);
                this.add(jPanel3);
                jPanel3.revalidate();
            }

            jLabel5.revalidate();
            jLabel5.repaint();
            jLabel6.revalidate();
            jLabel6.repaint();

            jPanel3.repaint();
        } else if (cambiosAplicadosRMS == 2) {
            jPanel3.removeAll();
            cambiosHechos ch = new cambiosHechos();
            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(ch);
            jPanel3.revalidate();
            jPanel3.repaint();
        }
    }//GEN-LAST:event_jPanel4MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

        jPanel3.removeAll();
        cop1 = 0;
        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        cop2 = 1;
        cop3 = 0;
        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        cop4 = 0;
        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        if (cambiosAplicadosPDA != 3) {
            if (statusPDA == 0) {
                numeroCancionePDA = listas.listaPDA.size();
                pdPDA = new panelDir(listas.listaPDA, route, RMS.AudioEnhanceDir.treePDA, 1);
                statusPDA = 1;
            }

            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(pdPDA);
            this.add(jPanel3);
            jPanel3.revalidate();

            jLabel5.revalidate();
            jLabel5.repaint();
            jLabel6.revalidate();
            jLabel6.repaint();

            jPanel3.repaint();
        } else if (cambiosAplicadosPDA == 3) {
            principal.jPanel3.removeAll();
            cambiosHechos ch = new cambiosHechos();
            principal.jPanel3.setLayout(new BorderLayout());
            principal.jPanel3.add(ch);
            principal.jPanel3.revalidate();
            principal.jPanel3.repaint();
        }
    }//GEN-LAST:event_jPanel5MouseClicked

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
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new principal(null, "vacio", null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    public static javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    // End of variables declaration//GEN-END:variables
}
