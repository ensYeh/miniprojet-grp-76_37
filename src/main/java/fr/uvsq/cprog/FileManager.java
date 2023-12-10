package fr.uvsq.cprog;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager implements Serializable {
    protected int NER;
    private String path;

    private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
    public String Output = "";
    public String Annot = "";
    private ConsoleManager fileManager;
    private AnnotationManager annotationManager;

    public FileManager(String path, ConsoleManager fileManager) {
        this.path = path;
        this.fileManager = fileManager;
        this.annotationManager = new AnnotationManager(fileManager);
    }

    public FileManager(int NER, String path) {
        this.NER = NER;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void viewFile(int NER) {
        String filePath = fileManager.getPathByNER(NER);

        if (filePath != null) {
            File targetFile = new File(filePath);

            if (targetFile.isFile()) {
                String fileName = targetFile.getName();

                if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {

                    try {
                        InputStream ips = new FileInputStream(targetFile);
                        InputStreamReader ipsr = new InputStreamReader(ips);
                        BufferedReader br = new BufferedReader(ipsr);
                        String ligne;
                        while ((ligne = br.readLine()) != null) {
                            Output += ligne;
                            System.out.println(ligne);
                        }
                        br.close();
                    } catch (IOException e) {
                        Output = "Error reading file: " + e.getMessage();
                        logger.error(Output);
                        System.out.println(Output);
                    }
                    if (Output.isEmpty()) {
                        Output = "Empty file";
                        System.out.println(Output);
                    }
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

    public void copyFile(int NER) {
        String sourceFilePath = fileManager.getPathByNER(NER);
        if (sourceFilePath != null) {
            File sourceFile = new File(sourceFilePath);
            if (sourceFile.isFile()) {
                String copiedFilePath = sourceFile.getAbsolutePath();
                fileManager.copiedFile[0] = NER;
                fileManager.copiedFile[1] = copiedFilePath;
                Output = "File copied successfully.";
                System.out.println(Output);
                // Maintenant, on appele la méthode pour copier l'annotation
                Annot = annotationManager.displayAnnotation(NER);
            } else {
                Output = "The element corresponding to NER is not a file.";
                System.out.println(Output);
            }
        } else {
            Output = "File not found for NER " + NER;
            System.out.println(Output);
        }
    }

    public void pasteFile(String operation) {
        if (fileManager.copiedFile[1] != null) {
            String sourceFilePath = (String) fileManager.copiedFile[1];

            File sourceFile = new File(sourceFilePath);
            if (sourceFile.isFile()) {
                String fileName = sourceFile.getName().replaceFirst("[.][^.]+$", "");
                String currentPath = fileManager.currentDirectory.getPath();
                String newFilePath = Paths.get(currentPath, sourceFile.getName()).toString();

                // Check if the file already exists in the target directory
                File targetFile = new File(newFilePath);
                if (targetFile.exists()) { // SI EXISTE AJOUTER -COPY-
                    String fileExtension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
                    String newFileName = fileName + "-copy" + fileExtension;
                    newFilePath = Paths.get(currentPath, newFileName).toString();
                }

                try (InputStream inStream = new FileInputStream(sourceFile);
                     OutputStream outStream = new FileOutputStream(newFilePath)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inStream.read(buffer)) > 0) {
                        outStream.write(buffer, 0, length);
                    }
                    Output = "File pasted successfully.";
                    System.out.println(Output);
                    int indexOfSpace = Annot.indexOf(' '); // Find the index of the first space
                    String annotationText = (indexOfSpace != -1) ? Annot.substring(indexOfSpace + 1) : Annot;

                    annotationManager.annotateER(fileManager.lastNER, annotationText);
                } catch (IOException e) {
                    Output = "Error pasting file: " + e.getMessage();
                    logger.error(Output);
                }
            } else {
                Output = "The copied element is not a file.";
                System.out.println(Output);
            }
            // If the operation is "cut", call the cut method
            if ("cut".equals(operation)) {
                cutFile((Integer) fileManager.copiedFile[0]); // NER DU FICHIER COUPER EN CUT
            }
        } else {
            Output = "Copied file not found.";
            System.out.println(Output);
        }
    }

    public void cutFile(int NER) {
        String path = (String) fileManager.copiedFile[1];
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
                    logger.error(Output);
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
}
