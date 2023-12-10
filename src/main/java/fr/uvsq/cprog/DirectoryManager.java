package fr.uvsq.cprog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirectoryManager extends FileManager {
    private final List<FileManager> elements;
    private final ConsoleManager fileManager;

    public DirectoryManager(int NER, String path, ConsoleManager fileManager) {
        super(NER, path);
        this.elements = new ArrayList<>();
        this.fileManager = fileManager;
    }

    private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);

    public List<FileManager> getElements() {
        return elements;
    }

    public void addElement(FileManager element) {
        elements.add(element);
    }

    public void navigateUp() {
        if (fileManager.currentDirectory.getPath().equals("Root")) {
            Output = "Already at the root.";
            System.out.println(Output);
            return;
        }
        String parentPath = fileManager.currentDirectory.getPath();
        File parentFile = new File(parentPath).getParentFile();

        if (parentFile != null && !parentPath.endsWith("Root")) {
            fileManager.currentDirectory = new DirectoryManager(0, parentFile.getAbsolutePath(), fileManager);
        } else {
            Output = "Already at the root.";
            System.out.println(Output);
        }
    }

    public void navigateIntoDirectory(int NER) {
        String targetDirectoryPath = fileManager.getPathByNER(NER);
        if (targetDirectoryPath != null) {
            File targetDirectory = new File(targetDirectoryPath);
            if (targetDirectory.isDirectory()) {
                fileManager.currentDirectory.setPath(targetDirectoryPath);
            } else {
                Output = "The element corresponding to NER is a file, not a directory.";
                System.out.println(Output);
            }
        } else {
            Output = "Directory not found.";
            System.out.println(Output);
        }
    }

    public void createDirectory(String name) {
        int newNER = fileManager.currentDirectory.getElements().size() + 1;
        DirectoryManager newDirectory = new DirectoryManager(newNER, fileManager.currentDirectory.getPath(), fileManager);
        Path newDirectoryPath = Paths.get(fileManager.currentDirectory.getPath(), name);
        try {
            Files.createDirectory(newDirectoryPath);
            newDirectory.setPath(newDirectoryPath.toString());
            fileManager.currentDirectory.addElement(newDirectory);
            // Crée le fichier notes.txt dans le nouveau répertoire
            Path notesFilePath = Paths.get(newDirectoryPath.toString(), "notes.txt");
            Files.createFile(notesFilePath);
            Output = "Directory created successfully. notes.txt created.";
            System.out.println(Output);
        } catch (IOException e) {
            Output = "Error creating directory.";
            logger.error(Output);
        }
    }

}
