package dir;

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

    public DirectoryTree(String rootPath) {
        this.rootPath = new File(rootPath).getAbsolutePath();
        this.nextId = 1; // Start IDs from 1
        root = new Node(new File(rootPath).getName(), true, nextId++);
        buildTree(new File(rootPath), root);
    }

    private void buildTree(File dir, Node node) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                Node child = new Node(file.getName(), file.isDirectory(), nextId++);
                node.children.add(child);
                if (file.isDirectory()) {
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
                Node newNode = new Node(part, part.equals(parts[parts.length - 1]) && file.isFile(), nextId++);
                current.children.add(newNode);
                current = newNode;
            }
        }
    }

    public DirectoryFiles getAllDirectoriesAndFiles() {
        List<DirectoryEntry> directories = new ArrayList<>();
        List<FileEntry> files = new ArrayList<>();
        getAllDirectoriesAndFiles(root, "", directories, files);
        return new DirectoryFiles(directories, files);
    }

    private void getAllDirectoriesAndFiles(Node node, String path, List<DirectoryEntry> directories, List<FileEntry> files) {
        String currentPath = path.isEmpty() ? node.name : path + File.separator + node.name;
        if (node.isDirectory) {
            directories.add(new DirectoryEntry(node.id, currentPath));
            for (Node child : node.children) {
                getAllDirectoriesAndFiles(child, currentPath, directories, files);
            }
        } else {
            files.add(new FileEntry(node.id, currentPath, path));
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
            System.out.println("ID: " + file.id + ", Path: " + file.path + ", Directory: " + file.directoryPath);
        }
    }
}

class DirectoryFiles {
    List<DirectoryEntry> directories;
    List<FileEntry> files;

    DirectoryFiles(List<DirectoryEntry> directories, List<FileEntry> files) {
        this.directories = directories;
        this.files = files;
    }
}

class DirectoryEntry {
    int id;
    String path;

    DirectoryEntry(int id, String path) {
        this.id = id;
        this.path = path;
    }
}

class FileEntry {
    int id;
    String path;
    String directoryPath;

    FileEntry(int id, String path, String directoryPath) {
        this.id = id;
        this.path = path;
        this.directoryPath = directoryPath;
    }
}
