/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Directorios;

/**
 *
 * @author Somils
 */
public class FileEntry {
    public int directoryId;
    public String filePath;
    public String directoryPath;
    public String absoluteFilePath;

    public FileEntry(int directoryId, String filePath, String directoryPath, String absoluteFilePath) {
        this.directoryId = directoryId;
        this.filePath = filePath;
        this.directoryPath = directoryPath;
        this.absoluteFilePath = absoluteFilePath;
    }
}