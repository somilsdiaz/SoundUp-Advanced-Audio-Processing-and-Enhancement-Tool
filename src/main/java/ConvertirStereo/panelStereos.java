/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ConvertirStereo;

import Directorios.DirectoryEntry;
import Directorios.DirectoryFiles;
import Directorios.DirectoryTree;
import Directorios.FileEntry;
import MsgEmergentes.MsgEmerge;
import RMS.AudioEnhanceDir;
import com.mycompany.soundup.principal;
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

/**
 *
 * @author Somils
 */
public class panelStereos extends javax.swing.JPanel {

    String ruta;
    int nroCanciones;
    DefaultListModel<String> listModel;
    DirectoryFiles directoryFiles;

    /**
     * Creates new form panelStereos
     */
    public panelStereos(String route, int numeroCanciones, DirectoryTree tree) {
        initComponents();
        route = ruta;

        nroCanciones = numeroCanciones;
        setOpaque(true);
        jComboBox1.removeAllItems();

        listModel = new DefaultListModel<>();
        jList2.setModel(listModel);
        jLabel3.setText("¡Se han detectado " + numeroCanciones + " canciones con salida mono!");
        principal.treeStereo.printTree();
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
                for (FileEntry fileEntry : fileEntries) {
                    listModel.addElement(fileEntry.filePath);
                }
            }
        }

       /* for (FileEntry FileEntry : directoryFiles.files) {
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

                for (FileEntry FileEntry : directoryFiles.files) {
                    if (FileEntry.directoryId == idSelected) {
                        listModel.addElement(FileEntry.filePath);
                    }
                }

                if (listModel.getSize() == 1) {
                    jLabel1.setText(listModel.getSize() + " cancion");

                } else {
                    jLabel1.setText(listModel.getSize() + " canciones");

                }
            }

        });
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
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(8, 7, 44));
        setMinimumSize(new java.awt.Dimension(720, 670));
        setLayout(null);

        jPanel1.setBackground(new java.awt.Color(8, 7, 44));
        jPanel1.setMinimumSize(new java.awt.Dimension(720, 670));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(null);

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Microsoft YaHei", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Convertir de Mono a Stereo");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(30, 20, 660, 30);

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("¡Se han detectado 1821 canciones con salida mono!");
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
        jComboBox1.setBounds(30, 90, 650, 23);

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
        jScrollPane2.setBounds(30, 140, 650, 470);

        jButton2.setText("REALIZAR CONVERSION");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(230, 630, 190, 23);

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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        for (FileEntry FileEntry : directoryFiles.files) {
            try {
                System.out.println(FileEntry.absoluteFilePath);
                String wavRouteFile = RMS.AudioEnhanceFile.convertToWavString(FileEntry.absoluteFilePath);
                RMS.AudioEnhanceFile.replaceFile(FileEntry.absoluteFilePath, wavRouteFile);
                RMS.AudioEnhanceFile.eliminarArchivo(wavRouteFile);
                MsgEmerge msg = new MsgEmerge("Audios convertidos a stereo exitosamente");
                msg.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(panelStereos.class.getName()).log(Level.SEVERE, null, ex);
            }

        }


    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
