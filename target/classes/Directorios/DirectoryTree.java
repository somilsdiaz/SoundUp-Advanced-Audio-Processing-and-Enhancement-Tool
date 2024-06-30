package Directorios;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DirectoryTree {

    private class Node {

        String name;
        boolean isDirectory;
        List<Node> children;
        int id;

        Node(String name, boolean isDirectory, int id) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.id = id;
            this.children = new ArrayList<>();
        }
    }

    private Node root;
    private String rootPath;
    private int nextId;
    private List<FileEntry> addedFiles; // List to keep track of added files

    public DirectoryTree(String rootPath) {
        this.rootPath = new File(rootPath).getAbsolutePath();
        this.nextId = 1; // Start IDs from 1
        this.addedFiles = new ArrayList<>();
        root = new Node(new File(rootPath).getName(), true, nextId++);
        buildTree(new File(rootPath), root);
    }

    private void buildTree(File dir, Node node) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    Node child = new Node(file.getName(), true, nextId++);
                    node.children.add(child);
                    buildTree(file, child);
                }
            }
        }
    }

    public void addFile(String filePath) {
        File file = new File(filePath);
        String absoluteFilePath = file.getAbsolutePath();

        // Ensure the file is within the root directory
        if (!absoluteFilePath.startsWith(rootPath)) {
            System.out.println("El archivo no está en el directorio raíz especificado.");
            return;
        }

        String relativePath = absoluteFilePath.substring(rootPath.length() + 1);
        String[] parts = relativePath.split(Pattern.quote(File.separator));

        Node current = root;
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            boolean found = false;
            for (Node child : current.children) {
                if (child.name.equals(part)) {
                    current = child;
                    found = true;
                    break;
                }
            }
            if (!found) {
                Node newNode;
                if (part.equals(parts[parts.length - 1]) && file.isFile()) {
                    // If it's a file, assign the same ID as the directory it's in
                    newNode = new Node(part, false, current.id);

                    // Ensure the file path has a parent directory before adding
                    String directoryPath = relativePath.lastIndexOf(File.separator) != -1
                            ? relativePath.substring(0, relativePath.lastIndexOf(File.separator))
                            : "";
                    addedFiles.add(new FileEntry(current.id, relativePath, directoryPath, absoluteFilePath));
                } else {
                    newNode = new Node(part, true, nextId++);
                }
                current.children.add(newNode);
                current = newNode;
            }
        }
    }

    public DirectoryFiles getAllDirectoriesAndFiles() {
        List<DirectoryEntry> directories = new ArrayList<>();
        getAllDirectories(root, "", directories);
        return new DirectoryFiles(directories, addedFiles); // Use addedFiles list for files
    }

    private void getAllDirectories(Node node, String path, List<DirectoryEntry> directories) {
        String currentPath = path.isEmpty() ? node.name : path + File.separator + node.name;
        if (node.isDirectory) {
            directories.add(new DirectoryEntry(node.id, currentPath));
            for (Node child : node.children) {
                getAllDirectories(child, currentPath, directories);
            }
        }
    }

    public void printTree() {
        printTree(root, "", true);
    }

    public static void printTree(Node node, String prefix, boolean isLast) {
        System.out.println(prefix + (isLast ? "+-- " : "|-- ") + node.name);
        for (int i = 0; i < node.children.size(); i++) {
            printTree(node.children.get(i), prefix + (isLast ? "    " : "|   "), i == node.children.size() - 1);
        }
    }

    public static void main(String[] args) {
        DirectoryTree tree = new DirectoryTree("C:/Users/Somils/Desktop/Muestra");
        tree.addFile("C:/Users/Somils/Desktop/Muestra/Nueva carpeta/coco.wav");
        tree.addFile("C:/Users/Somils/Desktop/Muestra/excusa.wav");
        tree.addFile("C:/Users/Somils/Desktop/Muestra/Nueva carpeta/Nueva carpeta/CorazoncitoRoto.wav");

        tree.printTree();

        // Obtener y mostrar todas las carpetas y archivos
        DirectoryFiles directoryFiles = tree.getAllDirectoriesAndFiles();
        System.out.println("\nTodas las carpetas:");
        for (DirectoryEntry dir : directoryFiles.directories) {
            System.out.println("ID: " + dir.id + ", Path: " + dir.path);
        }

        System.out.println("\nTodos los archivos:");
        for (FileEntry file : directoryFiles.files) {
            System.out.println("Directory ID: " + file.directoryId + ", File Path: " + file.filePath + ", Directory Path: " + file.directoryPath + ", Absolute File Path: " + file.absoluteFilePath);
        }
    }
}
