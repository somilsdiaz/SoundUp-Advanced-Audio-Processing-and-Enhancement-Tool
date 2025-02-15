/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package RMS;

import com.mycompany.soundup.FileSelectionDir;
import Directorios.DirectoryEntry;
import Directorios.DirectoryFiles;
import Directorios.DirectoryTree;
import Directorios.FileEntry;
import MsgEmergentes.MsgConfirmar;
import RMS.AudioEnhanceDir.Rutas;
import MsgEmergentes.MsgEmerge;
import MsgEmergentes.MsgLoadd;
import MsgEmergentes.cambiosHechos;
import PDA.AudioEnhancer;
import com.mycompany.soundup.StartMenu;
import com.mycompany.soundup.principal;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Somils
 */
public class panelDir extends javax.swing.JPanel {

    /**
     * Creates new form panelDir
     */
    DefaultListModel<String> listModel;
    DirectoryFiles directoryFiles;
    int numerodecanciones;
    List<AudioEnhanceDir.Rutas> estanMejorados;
    String route;
    DirectoryTree tree;
    int idOp;

    public panelDir(List<AudioEnhanceDir.Rutas> estanMejorado, String ruta, DirectoryTree tree, int idOp) {
        initComponents();
        setOpaque(true);
        route = ruta;
        this.tree = tree;
        this.idOp = idOp;
        estanMejorados = estanMejorado;

        if (idOp == 0) {
            jLabel4.setText("Mejoramiento de audio por RMS");
        } else if (idOp == 1) {
            jLabel4.setText("Mejoramiento de audio por Superposicion Dinamica");
        }

        jLabel4.revalidate();
        jLabel4.repaint();
        jComboBox1.removeAllItems();

        listModel = new DefaultListModel<>();
        jList2.setModel(listModel);
        jList2.setCellRenderer(new AudioListRenderer());

        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = jList2.locationToIndex(evt.getPoint());
                if (index != -1) {
                    String item = listModel.getElementAt(index);

                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem noConvertir = new JMenuItem("No Convertir");
                    JMenuItem convertir = new JMenuItem("Convertir");

                    noConvertir.addActionListener(e -> {
                        JOptionPane.showMessageDialog(null, "No Convertir clicked for: " + item);
                        // TODO: Add logic for "No Convertir"
                    });

                    convertir.addActionListener(e -> {
                        JOptionPane.showMessageDialog(null, "Convertir clicked for: " + item);
                        // TODO: Add logic for "Convertir"
                    });

                    popup.add(noConvertir);
                    popup.add(convertir);

                    popup.show(jList2, evt.getX(), evt.getY());
                }
            }
        });

        numerodecanciones = RMS.AudioEnhanceDir.TotalCanciones;
        jLabel3.setText("¡Se han detectado " + numerodecanciones + " canciones que necesitan ser mejoradas! ");
        tree.printTree();
        directoryFiles = tree.getAllDirectoriesAndFiles();

        System.out.println("\nTodas las carpetas:");

        Map<Integer, List<FileEntry>> filesByDirectoryId = new HashMap<>();

// Agrupar archivos por directoryId
        for (FileEntry fileEntry : directoryFiles.files) {
            if (fileEntry != null) {
                filesByDirectoryId.computeIfAbsent(fileEntry.directoryId, k -> new ArrayList<>()).add(fileEntry);
            }
        }

// Iterar sobre los directorios y agregar elementos a los componentes del GUI
        for (DirectoryEntry dir : directoryFiles.directories) {
            // System.out.println("ID: " + dir.id + ", Path: " + dir.path);
            List<FileEntry> fileEntries = filesByDirectoryId.get(dir.id);
            if (fileEntries != null && !fileEntries.isEmpty()) {
                jComboBox1.addItem(dir.path);
                /*    for (FileEntry fileEntry : fileEntries) {
                    listModel.addElement(fileEntry.filePath);
                }*/
            }
        }

        Collections.sort(estanMejorados, Comparator.comparingDouble(r -> r.RMS));
        for (Rutas rutas : estanMejorados) {
            for (FileEntry FileEntry : directoryFiles.files) {

                if (FileEntry.absoluteFilePath.equals(rutas.rutaOriginal)) {
                    listModel.addElement(FileEntry.filePath);
                }
            }

        }
        /*   for (FileEntry FileEntry : directoryFiles.files) {
            if (directoryFiles.directories.getFirst().id == FileEntry.directoryId) {
                listModel.addElement(FileEntry.filePath);
            }
        }*/
        if (listModel.getSize() == 1) {
            jLabel1.setText(listModel.getSize() + " cancion");

        } else {
            jLabel1.setText(listModel.getSize() + " canciones");

        }

        numerodecanciones = estanMejorado.size();
        jLabel3.setText("¡Se han detectado " + numerodecanciones + " canciones que necesitan ser mejoradas! ");

        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.removeAllElements();
                JComboBox comboBox = (JComboBox) e.getSource();
                String selectedItem = (String) comboBox.getSelectedItem();
                int idSelected = 0;
                for (DirectoryEntry dir : directoryFiles.directories) {
                    // System.out.println("ID: " + dir.id + ", Path: " + dir.path);
                    if (dir.path == selectedItem) {
                        idSelected = dir.id;
                        break;
                    }
                }
                System.out.println("Item seleccionado: " + selectedItem);

                for (Rutas rutas : estanMejorados) {
                    for (FileEntry FileEntry : directoryFiles.files) {
                        if (FileEntry.absoluteFilePath.equals(rutas.rutaOriginal)) {
                            if (FileEntry.directoryId == idSelected) {
                                listModel.addElement(FileEntry.filePath);
                            }
                        }
                    }

                }

                /*   for (FileEntry FileEntry : directoryFiles.files) {
                    if (FileEntry.directoryId == idSelected) {
                        listModel.addElement(FileEntry.filePath);
                    }
                    System.out.println("Directory ID: " + FileEntry.directoryId + ", File Path: " + FileEntry.filePath + ", Directory Path: " + FileEntry.directoryPath);
                }*/
                if (listModel.getSize() == 1) {
                    jLabel1.setText(listModel.getSize() + " cancion");

                } else {
                    jLabel1.setText(listModel.getSize() + " canciones");

                }
            }

        });

        System.out.println("\nTodos los archivos:");
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
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(8, 7, 44));
        setLayout(null);

        jPanel1.setBackground(new java.awt.Color(8, 7, 44));
        jPanel1.setMinimumSize(new java.awt.Dimension(720, 670));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(null);

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Microsoft YaHei", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(247, 248, 211));
        jLabel4.setText("Convertir de Mono a Stereo");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(30, 20, 660, 30);

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("¡Se han detectado 1821 canciones que necesitan ser mejorados! ");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(30, 50, 660, 30);

        jComboBox1.setBackground(new java.awt.Color(195, 194, 190));
        jComboBox1.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(0, 0, 0));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1);
        jComboBox1.setBounds(30, 90, 540, 23);

        jScrollPane2.setBackground(new java.awt.Color(8, 7, 44));
        jScrollPane2.setForeground(new java.awt.Color(8, 7, 44));
        jScrollPane2.setOpaque(false);

        jList2.setBackground(new java.awt.Color(224, 232, 244));
        jList2.setFont(new java.awt.Font("Microsoft YaHei", 1, 12)); // NOI18N
        jList2.setForeground(new java.awt.Color(0, 0, 0));
        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList2.setOpaque(false);
        jList2.setSelectionBackground(new java.awt.Color(115, 199, 251));
        jList2.setSelectionForeground(new java.awt.Color(51, 51, 255));
        jScrollPane2.setViewportView(jList2);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(30, 140, 650, 470);

        jButton1.setBackground(new java.awt.Color(44, 7, 75));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("ESCUCHAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(580, 90, 100, 22);

        jButton2.setBackground(new java.awt.Color(7, 44, 77));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("APLICAR CAMBIOS");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(230, 630, 190, 22);

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("10 canciones");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(30, 120, 170, 17);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 720, 670);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //   System.out.println("Item de la lista seleccionado: " + jList2.getSelectedValue());
        if (jList2.getSelectedValue() != null) {
            MsgLoadd cargando = new MsgLoadd();
            cargando.setVisible(true);

            Thread backgroundProcessThread = new Thread(() -> {
                for (FileEntry FileEntry : directoryFiles.files) {
                    if (FileEntry.filePath == jList2.getSelectedValue()) {
                        try {
                            String rutaOriginal = FileEntry.absoluteFilePath;
                            String rutaFileWav = AudioEnhanceFile.convertToWavString(rutaOriginal);
                            for (Rutas rutas : estanMejorados) {
                                if (rutas.rutaOriginal == rutaOriginal) {
                                    FileSelectionDir fs = new FileSelectionDir(rutaFileWav, rutas.rutaMejorada);
                                    fs.setVisible(true);
                                    break;
                                }
                            }
                            break;
                        } catch (IOException ex) {
                            Logger.getLogger(panelDir.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

                cargando.setVisible(false);  //por ejemplo pones para que se ejecute una ventana de cargando, cuando
                //termine el proceso haz que se quite la ventana de cargando.

                // Actualizar el estado del JFrame
                SwingUtilities.invokeLater(() -> {

                });
            });
            backgroundProcessThread.start();
        } else {
            MsgEmerge mg = new MsgEmerge("Selecione una cancion de la lista");
            mg.setVisible(true);
        }


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        SwingUtilities.invokeLater(() -> {
            MsgConfirmar msgConfirmar = new MsgConfirmar("¿Estás seguro de aplicar los cambios?");
            msgConfirmar.setVisible(true);
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

                            System.out.println("Los cambios seran realizados");
                            AudioEnhanceDir.RemplazarMuchosArchivos(estanMejorados);

                            if (idOp == 0) {
                                AudioEnhanceDir.eliminarArchivosNormalizados();
                            } else if (idOp == 1) {
                                AudioEnhancer.eliminarArchivosPDA();
                            }
                            // StartMenu.CerrarPrincipal();
                            principal.jPanel3.removeAll();
                            cambiosHechos ch = new cambiosHechos();
                            principal.jPanel3.setLayout(new BorderLayout());
                            principal.jPanel3.add(ch);
                            principal.jPanel3.revalidate();
                            principal.jPanel3.repaint();
                            cargando.setVisible(false);
                            MsgEmerge me = new MsgEmerge("Los cambios se han aplicado");
                            me.setVisible(true);
                            if (idOp == 0) {
                                principal.cambiosAplicadosRMS();
                            } else if (idOp == 1) {
                                principal.cambiosAplicadosPDA();
                            }

                            SwingUtilities.invokeLater(() -> {

                            });
                        });
                        backgroundProcessThread.start();
                    }
                    msgConfirmar.setVisible(false);
                }
            }.execute();
        });
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
