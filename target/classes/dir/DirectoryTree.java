package dir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class DirectoryTree {

    private class Node {

        String name;
        List<Node> children;

        Node(String name) {
            this.name = name;
            this.children = new ArrayList<>();
        }
    }

    private Node root;
    private String rootPath;

    public DirectoryTree(String rootPath) {
        this.rootPath = new File(rootPath).getAbsolutePath();
        root = new Node(new File(rootPath).getName());
        buildTree(new File(rootPath), root);
    }

    private void buildTree(File dir, Node node) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    Node child = new Node(file.getName());
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
                Node newNode = new Node(part);
                current.children.add(newNode);
                current = newNode;
            }
        }
    }

    public void printTree() {
        printTree(root, "", true);
    }

    private void printTree(Node node, String prefix, boolean isLast) {
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
    }
}
