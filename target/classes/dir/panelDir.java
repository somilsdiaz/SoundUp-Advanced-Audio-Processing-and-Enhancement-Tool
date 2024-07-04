/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package dir;

import Directorios.DirectoryEntry;
import Directorios.DirectoryFiles;
import Directorios.FileEntry;
import com.mycompany.soundup.AudioEnhanceDir;
import static com.mycompany.soundup.AudioEnhanceDir.tree;
import com.mycompany.soundup.AudioEnhanceFile;
import com.mycompany.soundup.MsgEmerge;
import com.mycompany.soundup.MsgLoadd;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public panelDir(List<AudioEnhanceDir.Rutas> estanMejorados) {
        initComponents();
        setOpaque(true);

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
        for (FileEntry FileEntry : directoryFiles.files) {
            if (directoryFiles.directories.getFirst().id == FileEntry.directoryId) {
                listModel.addElement(FileEntry.filePath);
            }
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
                    System.out.println("Directory ID: " + FileEntry.directoryId + ", File Path: " + FileEntry.filePath + ", Directory Path: " + FileEntry.directoryPath);
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
        jScrollPane2.setBounds(30, 100, 650, 540);

        jButton1.setText("ESCUCHAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(580, 60, 100, 23);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 720, 670);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("Item de la lista seleccionado: " + jList2.getSelectedValue());
        if (jList2.getSelectedValue() != null) {
            MsgLoadd cargando = new MsgLoadd();
            cargando.setVisible(true);
            Thread backgroundProcessThread = new Thread(() -> {
                for (FileEntry FileEntry : directoryFiles.files) {
                    if (FileEntry.filePath == jList2.getSelectedValue()) {
                        String ruta1 = FileEntry.absoluteFilePath;
                        AudioEnhanceFile.BooleanDoublePair necesitaNormalizar = AudioEnhanceFile.necesitaNormalizacion(ruta1);
                        String ruta2 = AudioEnhanceFile.Mejorar(ruta1, 0, necesitaNormalizar.value);
                        
                        FileSelectionDir fs = new FileSelectionDir(ruta1, ruta2);
                        fs.setVisible(true);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
