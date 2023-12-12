package fr.uvsq.cprog;

import java.io.File;
import java.util.Arrays;

public class ConsoleManager {
    protected DirectoryManager currentDirectory;
    public int lastNER;
    public String Output = "";
    public String Annot = "";
    private String operation = ""; // Soit copy, Soit cut pour la faire passer dans pastFile à fin de savoir si on supprime le fichier copié ou non
    Object[] copiedFile = new Object[2]; // Stocker path et NER du fichier copier
    private final FileManager fileManager;
    private final AnnotationManager annotationManager;
    private final DirectoryManager directoryManager;

    public ConsoleManager(String rootPath) {
        this.currentDirectory = new DirectoryManager(0, rootPath, this);
        lastNER = 0;
        this.fileManager = new FileManager(rootPath, this);
        this.annotationManager = new AnnotationManager(this);
        this.directoryManager = new DirectoryManager(0, rootPath, this);
    }

    public void processCommand(String command) {
        String[] parts = command.split(" ");
        int NER;
        String cmd;

        cmd = parts[0].toLowerCase();
        if (parts[0].matches("\\d+")) {
            try {
                NER = Integer.parseInt(parts[0]);
                cmd = (parts.length > 1) ? parts[1].toLowerCase() : " ";
            } catch (NumberFormatException e) {
                cmd = parts[0].toLowerCase();
                NER = lastNER;
            }
            lastNER = NER;
        } else {
            NER = lastNER;
        }
        if (!cmd.isEmpty()) {
            switch (cmd) {
                case "copy":
                    operation = "copy";
                    fileManager.copyFile(NER);
                    Output = fileManager.Output;
                    Annot = fileManager.Annot;
                    break;
                case "cut":
                    operation = "cut";
                    fileManager.copyFile(NER);
                    Output = fileManager.Output;
                    Annot = fileManager.Annot;
                    break;
                case "past":
                    fileManager.pasteFile(operation);
                    Output = fileManager.Output;
                    Annot = fileManager.Annot;
                    break;
                case "..":
                    directoryManager.navigateUp();
                    Output = directoryManager.Output;
                    break;
                case ".":
                    directoryManager.navigateIntoDirectory(NER);
                    Output = directoryManager.Output;
                    break;
                case "mkdir":
                    if (parts.length > 1) {
                        directoryManager.createDirectory(parts[1]);
                        Output = directoryManager.Output;
                    } else {
                        Output = "Missing directory name.";
                        System.out.println(Output);
                    }
                    break;
                case "visu":
                    fileManager.viewFile(NER);
                    Output = fileManager.Output;
                    break;
                case "find":
                    if (parts.length > 1) {
                        findFile(parts[1]);
                    } else {
                        Output = "Missing file name.";
                        System.out.println(Output);
                    }
                    break;
                case "+":
                    if (parts.length > 2) {
                        String annotationText = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                        annotationManager.annotateER(NER, annotationText);
                        Output = annotationManager.Output;
                    } else {
                        Output = "Missing annotation text.";
                        System.out.println(Output);
                    }
                    break;
                case "-":
                    annotationManager.removeAnnotation(NER);
                    Output = annotationManager.Output;
                    break;
                case " ":
                    designateElement(NER);
                    break;
                case "help":
                    displayHelp();
                    break;
                default:
                    Output = "Unrecognized command.";
                    System.out.println(Output);
                    break;
            }
        } else if (parts.length == 1 && parts[0].equalsIgnoreCase("mkdir")) {
            Output = "Missing directory name.";
            System.out.println(Output);
        } else {
            Output = "Incomplete command.";
            System.out.println(Output);
        }
    }

    public String getPathByNER(int NER) {
        File[] files = new File(currentDirectory.getPath()).listFiles();

        if (files != null && NER >= 1 && NER <= files.length) {
            int ner = 1;
            for (File file : files) {
                if (ner == NER) {
                    return file.getAbsolutePath();
                }
                ner++;
            }
        }
        return null;
    }

    public void displayCurrentDirectory() {
        // Afficher le chemin du répertoire courant
        int rootTestIndex = currentDirectory.getPath().indexOf("Root");

        // Si "Root" est trouvé, extraire la sous-chaîne à partir de cet index
        String outputPath = "";
        if (rootTestIndex != -1) {
            outputPath = currentDirectory.getPath().substring(rootTestIndex);
        }
        System.out.println("Current Directory: " + outputPath);

        File[] files = new File(currentDirectory.getPath()).listFiles();

        if (files != null) {
            int ner = 1;
            System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t TYPE%n", "NER", "NOM");
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t (directory)%n", ner, file.getName());
                } else {
                    System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t (file)%n", ner, file.getName());
                }
                ner++;
            }
        } else {
            System.out.println("Error retrieving directory items.");
        }
    }

    public void findFile(String fileName) {
        Output = "";
        findFileRecursive(currentDirectory.getPath(), fileName);
        if (Output == null || Output.isEmpty()) {
            Output = "File not found: " + fileName;
            System.out.println(Output);
        }
    }

    public void findFileRecursive(String directoryPath, String fileName) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(fileName)) {
                    Output = file.getAbsolutePath();
                    int rootTestIndex = currentDirectory.getPath().indexOf("Root");
                    // Si "Root" est trouvé, extraire la sous-chaîne à partir de cet index
                    if (rootTestIndex != -1) {
                        Output = Output.substring(rootTestIndex);
                    }
                    System.out.println(Output);
                    return; // Ajout pour arrêter la recherche une fois le fichier trouvé
                }
                if (file.isDirectory()) {
                    findFileRecursive(file.getAbsolutePath(), fileName);
                }
            }
        }
    }

    public void designateElement(int NER) {
        String targetPath = getPathByNER(NER);

        if (targetPath != null) {
            File targetElement = new File(targetPath);

            if (targetElement.exists()) {
                // Afficher le nom de l'élément
                Output = "User designates element number " + NER + ": " + targetElement.getName();
                System.out.println(Output);

                // Afficher l'annotation correspondante
                annotationManager.displayAnnotation(NER);
            } else {
                Output = "Element not found for NER " + NER;
                System.out.println(Output);
            }
        } else {
            Output = "Element not found for NER " + NER;
            System.out.println(Output);
        }
    }

    private void displayHelp() {
        System.out.println("Les commandes du gestion de fichiers à implémenter sont:");
        System.out.println("[<NER>] copy");
        System.out.println("past ; si l’élément existe, alors le nom du nouvel élément sera concaténé avec \"-copy\"");
        System.out.println("[<NER>] cut");
        System.out.println(".. ; pour remonter d’un cran dans le système de fichiers");
        System.out.println("[<NER>] . ; pour entrer dans un répertoire à condition que le NER désigne un répertoire. Exemple \"4 .\"");
        System.out.println("mkdir <nom> ; pour créer un répertoire");
        System.out.println("[<NER>] visu ; permet de voir le contenu d’un fichier texte. Si le fichier n’est pas de type texte, vous afficherez sa taille.");
        System.out.println("find <nom fichier> ; Recherche dans toutes les sous-répertoires du répertoire courant, le(s) fichier(s) et les affiche.");
        System.out.println("3 + \"ceci est un texte\" ; le texte est ajouté ou concaténé au texte existant sur l’ER");
        System.out.println("3 - ; retire tout le texte associé à l’ER 3");
    }
}