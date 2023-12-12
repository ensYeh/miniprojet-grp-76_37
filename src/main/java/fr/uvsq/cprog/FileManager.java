package fr.uvsq.cprog;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class FileManager implements Serializable {
    protected int NER;
    private String path;

    private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
    public String Output = "";
    public String Annot = "";
    private ConsoleManager consoleManager;
    private AnnotationManager annotationManager;

    public FileManager(String path, ConsoleManager fileManager) {
        this.path = path;
        this.consoleManager = fileManager;
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
        String filePath = consoleManager.getPathByNER(NER);

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
                    if (Objects.equals(Output, "") || Output.isEmpty()) {
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
        String sourceFilePath = consoleManager.getPathByNER(NER);
        if (sourceFilePath != null) {
            File sourceFile = new File(sourceFilePath);
            String copiedFilePath = sourceFile.getAbsolutePath();
            consoleManager.copiedFile[0] = NER;
            consoleManager.copiedFile[1] = copiedFilePath;
            Output = "Element copied successfully.";
            System.out.println(Output);
            // Maintenant, on appele la méthode pour copier l'annotation
            Annot = annotationManager.displayAnnotation(NER);
        } else {
            Output = "Element not found for NER " + NER;
            System.out.println(Output);
        }
    }

    public void pasteFile(String operation) {
        if (consoleManager.copiedFile[1] != null) {
            String sourceFilePath = (String) consoleManager.copiedFile[1];
            File sourceFile = new File(sourceFilePath);
            if (sourceFile.exists()) {
                String fileName = sourceFile.getName().replaceFirst("[.][^.]+$", "");
                String currentPath = consoleManager.currentDirectory.getPath();
                String newFilePath = Paths.get(currentPath, sourceFile.getName()).toString();
                if (sourceFile.isDirectory()) {
                    // For directories, create a new directory and copy its contents
                    File targetFile = new File(newFilePath);
                    if (targetFile.exists()) { // SI EXISTE AJOUTER -COPY-
                        fileName = fileName + "-copy";
                    }
                    newFilePath = Paths.get(currentPath, fileName).toString();
                    copyDirectory(sourceFile.toPath(), Paths.get(newFilePath));
                } else {
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
                    } catch (IOException e) {
                        Output = "Error pasting file: " + e.getMessage();
                        logger.error(Output);
                        return;
                    }
                }
                Output = "Element pasted successfully.";
                System.out.println(Output);
                int indexOfSpace = Annot.indexOf(' '); // Find the index of the first space
                String annotationText = (indexOfSpace != -1) ? Annot.substring(indexOfSpace + 1) : Annot;
                annotationManager.annotateER(consoleManager.lastNER, annotationText);
                // If the operation is "cut", call the cut method
                if ("cut".equals(operation)) {
                    removeElement((Integer) consoleManager.copiedFile[0]); // NER DU FICHIER COUPER EN CUT
                }
            } else {
                Output = "The copied element does not exist.";
                System.out.println(Output);
            }
        } else {
            Output = "Copied file or directory not found.";
            System.out.println(Output);
        }
    }

    @SuppressWarnings("checkstyle:Indentation")
    private void copyDirectory(Path source, Path destination) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = destination.resolve(source.relativize(dir));
                    Files.copy(dir, targetDir, StandardCopyOption.COPY_ATTRIBUTES);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.COPY_ATTRIBUTES);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Output = "Error copying directory: " + e.getMessage();
            logger.error(Output);
        }
    }

    public void removeElement(int NER) {
        String path = (String) consoleManager.copiedFile[1];
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
