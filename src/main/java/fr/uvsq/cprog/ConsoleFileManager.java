package fr.uvsq.cprog;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConsoleFileManager {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleFileManager.class);
    private Directory currentDirectory;
    private int lastNER;
    private String copiedFilePath;
    public String Output = "" ;
    public void processCommand(String command) {
        String[] parts = command.split(" ");
        int NER;
        String cmd;

        cmd = parts[0].toLowerCase();
        if (parts.length > 0 && parts[0].matches("\\d+")) {
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
                    copyFile(NER);
                    break;
                case "cut":
                    cutFile(NER);
                    break;
                case "past":
                    pasteFile();
                    break;
                case "..":
                    navigateUp();
                    break;
                case ".":
                    navigateIntoDirectory(NER);
                    break;
                case "mkdir":
                    if (parts.length > 1) {
                        createDirectory(parts[1]);
                    } else {
                        Output = "Missing directory name.";
                        logger.info(Output);
                        System.out.println(Output);
                    }
                    break;
                case "visu":
                    viewFile(NER);
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
                    annotateER(NER, parts);
                    break;
                case "-":
                    removeAnnotation(NER);
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
    public ConsoleFileManager(String rootPath) {
        this.currentDirectory = new Directory(0, "Root", rootPath);
        lastNER = 0;
    }
    private String getPathByNER(int NER) {
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
    public List<String> displayCurrentDirectory() {
        // Afficher le chemin du répertoire courant
        System.out.println("Current Directory: " + currentDirectory.getPath());

        List<String> contentList = new ArrayList<>();
        File[] files = new File(currentDirectory.getPath()).listFiles();

        if (files != null) {
            int ner = 1;
            System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t TYPE%n","NER","NOM");
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

        return contentList;
    }
    private void copyFile(int NER) {
        String sourceFilePath = getPathByNER(NER);
        if (sourceFilePath != null) {
            File sourceFile = new File(sourceFilePath);
            if (sourceFile.isFile()) {
                String fileName = sourceFile.getName().replaceFirst("[.][^.]+$", "");
                String fileExtension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
                String newFileName = fileName + "-copy" + fileExtension;
                copiedFilePath = sourceFile.getAbsolutePath();

                Output = "File copied successfully. New file: " + newFileName ;
                System.out.println(Output);
            } else {
                Output = "The element corresponding to NER is not a file.";
                System.out.println(Output);
            }
        } else {
            Output = "File not found for NER " + NER;
            System.out.println(Output);
        }
    }
    private void pasteFile() {
        if (copiedFilePath != null) {
            String sourceFilePath = copiedFilePath;

            if (sourceFilePath != null) {
                File sourceFile = new File(sourceFilePath);
                if (sourceFile.isFile()) {
                    String fileName = sourceFile.getName().replaceFirst("[.][^.]+$", "");
                    String fileExtension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
                    String newFileName = fileName + "-copy" + fileExtension;
                    String currentPath = currentDirectory.getPath();

                    String newFilePath = Paths.get(currentPath, newFileName).toString();

                    try (InputStream inStream = new FileInputStream(sourceFile);
                         OutputStream outStream = new FileOutputStream(newFilePath)) {

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inStream.read(buffer)) > 0) {
                            outStream.write(buffer, 0, length);
                        }

                        Output = "File pasted successfully." ;
                        System.out.println(Output);
                    } catch (IOException e) {
                        Output = "Error pasting file: " + e.getMessage() ;
                        System.out.println(Output);
                    }
                } else {
                    Output = "The copied element is not a file.";
                    System.out.println(Output);
                }
            } else {
                Output = "Copied file not found.";
                System.out.println(Output);
            }
        } else {
            Output = "No file has been copied previously.";
            System.out.println(Output);
        }
    }
    private void cutFile(int NER) {
        String path = getPathByNER(NER);
        if (path != null) {
            File fileOrDirectory = new File(path);

            if (fileOrDirectory.exists()) {
                try {
                    if (fileOrDirectory.isFile()) {
                        Files.deleteIfExists(Paths.get(path));
                        Output = "File deletion successful.";
                        System.out.println(Output);
                    } else if (fileOrDirectory.isDirectory()) {
                        FileUtils.deleteDirectory(fileOrDirectory);
                        Output = "Directory deletion successful.";
                        System.out.println(Output);
                    }
                } catch (IOException e) {
                    Output = "Error deleting: " + e.getMessage();
                    System.out.println(Output);
                }
            } else {
                Output = "File or directory not found for NER" + NER;
                System.out.println(Output);
            }
        } else {
            Output = "File or directory not found for NER " + NER;
            System.out.println(Output);
        }
    }
    public void findFile(String fileName) {
        Output = "" ;
        findFileRecursive(currentDirectory.getPath(), fileName);
        if (Output == null || Output.isEmpty()) {
            Output = "File not found: " + fileName;
            System.out.println(Output);
        }
    }
    private void findFileRecursive(String directoryPath, String fileName) {
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(fileName)) {
                    Output = file.getAbsolutePath();
                    System.out.println(Output);
                    return; // Ajout pour arrêter la recherche une fois le fichier trouvé
                }
                if (file.isDirectory()) {
                    findFileRecursive(file.getAbsolutePath(), fileName);
                }
            }
        }
    }
    private void navigateUp() {
        if (currentDirectory.getPath().equals("Root")) {
            Output = "Already at the root.";
            System.out.println(Output);
            return;
        }

        String parentPath = currentDirectory.getPath();
        File parentFile = new File(parentPath).getParentFile();

        if (parentFile != null &&  !parentPath.endsWith("Root") ) {
            currentDirectory = new Directory(0, parentFile.getName(), parentFile.getAbsolutePath());
        } else {
            Output = "Already at the root.";
            System.out.println(Output);
        }
    }
    private void navigateIntoDirectory(int NER) {
        String targetDirectoryPath = getPathByNER(NER);

        if (targetDirectoryPath != null) {
            File targetDirectory = new File(targetDirectoryPath);
            if (targetDirectory.isDirectory()) {
                currentDirectory.setPath(targetDirectoryPath);
            } else {
                Output = "The element corresponding to NER is a file, not a directory.";
                System.out.println(Output);
            }
        } else {
            Output = "Directory not found.";
            System.out.println(Output);
        }
    }
    private void createDirectory(String name) {
        int newNER = currentDirectory.getElements().size() + 1;
        Directory newDirectory = new Directory(newNER, name, currentDirectory.getPath());
        Path newDirectoryPath = Paths.get(currentDirectory.getPath(), name);

        try {
            Files.createDirectory(newDirectoryPath);
            newDirectory.setPath(newDirectoryPath.toString());
            currentDirectory.addElement(newDirectory);

            // Crée le fichier notes.txt dans le nouveau répertoire
            Path notesFilePath = Paths.get(newDirectoryPath.toString(), "notes.txt");
            Files.createFile(notesFilePath);

            Output = "Directory created successfully. notes.txt created.";
            System.out.println(Output);
        } catch (IOException e) {
            Output = "Error creating directory.";
            System.out.println(Output);
        }
    }
    private void viewFile(int NER) {
        String filePath = getPathByNER(NER);

        if (filePath != null) {
            File targetFile = new File(filePath);

            if (targetFile.isFile()) {
                String fileName = targetFile.getName();

                if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {

                    try{
                        InputStream ips=new FileInputStream(targetFile);
                        InputStreamReader ipsr=new InputStreamReader(ips);
                        BufferedReader br=new BufferedReader(ipsr);
                        String ligne;
                        while ((ligne=br.readLine())!=null){
                            System.out.println(ligne);
                        }
                        br.close();
                    }
                    catch (IOException e) {
                        Output = "Error reading file: " + e.getMessage();
                        System.out.println(Output);
                    }
                    if (Output.isEmpty()){Output ="Empty file";}
                } else {
                    Output = "The file is not a text type. Displaying size: " + targetFile.length() + " bytes";
                    System.out.println(Output);
                }
            } else {
                Output = "The element corresponding to NER is not a file.";
                System.out.println(Output);
            }
        } else {
            Output = "File not found for NER " + NER;
            System.out.println(Output);
        }
    }
    private void annotateER(int NER, String[] parts) {
        String filePath = getPathByNER(NER);
        if (filePath != null) {
            File targetFile = new File(filePath);

            if (targetFile.isFile()) {
                String annotationText = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));

                try (FileWriter writer = new FileWriter(filePath, true);
                     BufferedWriter bw = new BufferedWriter(writer)) {
                    bw.write(annotationText);
                    Output = "Annotation added successfully to NER " + NER;
                    System.out.println(Output);
                } catch (IOException e) {
                    Output = "Error adding annotation: " + e.getMessage();
                    System.out.println(Output);
                }
            } else {
                Output = "The element corresponding to NER is not a file.";
                System.out.println(Output);
            }
        } else {
            Output = "File not found for NER " + NER;
            System.out.println(Output);
        }
    }
    private void removeAnnotation(int NER) {
        String filePath = getPathByNER(NER);
        if (filePath != null) {
            File targetFile = new File(filePath);

            if (targetFile.isFile()) {
                try (FileWriter ignored = new FileWriter(filePath, false)) {
                    Output = "Annotation removed successfully from NER " + NER;
                    System.out.println(Output);
                } catch (IOException e) {
                    Output = "Error removing annotation: " + e.getMessage();
                    System.out.println(Output);
                }
            } else {
                Output = "The element corresponding to NER is not a file.";
                System.out.println(Output);
            }
        } else {
            Output = "File not found for NER " + NER;
            System.out.println(Output);
        }
    }
    private void designateElement(int NER) {
        String targetPath = getPathByNER(NER);

        if (targetPath != null) {
            File targetElement = new File(targetPath);

            if (targetElement.exists()) {
                Output = "User designates element number " + NER + ": " + targetElement.getName();
                System.out.println(Output);
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
