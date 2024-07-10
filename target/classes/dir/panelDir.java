/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package dir;

import Directorios.DirectoryEntry;
import Directorios.DirectoryFiles;
import Directorios.FileEntry;
import com.mycompany.soundup.AudioEnhanceDir;
import com.mycompany.soundup.AudioEnhanceDir.Rutas;
import static com.mycompany.soundup.AudioEnhanceDir.tree;
import com.mycompany.soundup.AudioEnhanceFile;
import com.mycompany.soundup.MsgEmerge;
import com.mycompany.soundup.MsgLoadd;
import com.mycompany.soundup.StartMenu;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

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

    public panelDir(List<AudioEnhanceDir.Rutas> estanMejorado, String ruta) {
        initComponents();
        setOpaque(true);
        route = ruta;
        estanMejorados = estanMejorado;
        jComboBox1.removeAllItems();

        listModel = new DefaultListModel<>();
        jList2.setModel(listModel);

        numerodecanciones = estanMejorados.size();
        jLabel3.setText("¡Se han detectado " + numerodecanciones + " canciones que necesitan ser mejoradas! ");
        tree.printTree();
        directoryFiles = tree.getAllDirectoriesAndFiles();

        System.out.println("\nTodas las carpetas:");
        for (DirectoryEntry dir : directoryFiles.directories) {
            // System.out.println("ID: " + dir.id + ", Path: " + dir.path);
            jComboBox1.addItem(dir.path);
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
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setLayout(null);

        jPanel1.setBackground(new java.awt.Color(8, 7, 44));
        jPanel1.setMinimumSize(new java.awt.Dimension(720, 670));
        jPanel1.setLayout(null);

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Se han detectado 1821 canciones que necesitan ser mejorados! ");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(30, 20, 660, 30);

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
        jComboBox1.setBounds(30, 60, 540, 23);

        jList2.setBackground(new java.awt.Color(195, 194, 190));
        jList2.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jList2.setForeground(new java.awt.Color(0, 0, 0));
        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList2.setOpaque(false);
        jScrollPane2.setViewportView(jList2);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(30, 120, 650, 470);

        jButton1.setText("ESCUCHAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(580, 60, 100, 23);

        jButton2.setText("APLICAR CAMBIOS");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(230, 620, 190, 23);

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("10 canciones");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(30, 100, 170, 17);

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

        AudioEnhanceDir.RemplazarNormalizados(estanMejorados);
        StartMenu.CerrarPrincipal();
        AudioEnhanceDir.eliminarArchivosNormalizados(route);
        MsgEmerge me = new MsgEmerge("Los cambios se han aplicado");
        me.setVisible(true);


    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
