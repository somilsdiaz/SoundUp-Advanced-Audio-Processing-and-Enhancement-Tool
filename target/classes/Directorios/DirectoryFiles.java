/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Directorios;

import java.util.List;

/**
 *
 * @author Somils
 */
public class DirectoryFiles {
    public List<DirectoryEntry> directories;
    public List<FileEntry> files;

    DirectoryFiles(List<DirectoryEntry> directories, List<FileEntry> files) {
        this.directories = directories;
        this.files = files;
    }
}