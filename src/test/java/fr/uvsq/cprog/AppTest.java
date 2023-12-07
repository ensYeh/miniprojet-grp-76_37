package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void processCommand_CopyFile_Success() {
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        fileManager.processCommand("2 copy");
        assertEquals("File copied successfully. New file: file-copy.txt", fileManager.Output);
    }

    @Test
    void processCommand_PasteFile_Success() {
        ConsoleFileManager fileManager = new ConsoleFileManager("..\\RootTest");
        fileManager.processCommand("2 copy");
        fileManager.processCommand("past");
        assertEquals("File pasted successfully.", fileManager.Output);
    }

    @Test
    void processCommand_PasteFile_Success_navigate() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Copy the file
        fileManager.processCommand("2 copy");
        // Navigate to another directory
        fileManager.processCommand("1 .");
        // Paste the file
        fileManager.processCommand("past");
        // Assert
        assertEquals("File pasted successfully.", fileManager.Output);
    }

    @Test
    void processCommand_UnrecognizedCommand() {
        ConsoleFileManager fileManager = new ConsoleFileManager("..\\RootTest");
        fileManager.processCommand("invalid");
        assertEquals("Unrecognized command.", fileManager.Output);
    }

    @Test
    void createDirectory_Success() {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Act
        fileManager.processCommand("mkdir NewDirectory");
        // Assert
        assertEquals("Directory created successfully. notes.txt created.", fileManager.Output);
    }

    @Test
    void processCommand_NavigateUp_Success() {
        // Arrange
        String rootPath = "..\\Root";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Act
        fileManager.processCommand("..");
        // Assert
        assertEquals("Already at the root.", fileManager.Output);
    }

    @Test
    void processCommand_NavigateIntoDirectory_Success() {
        // Arrange
        String rootPath = "..\\RootTest";

        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Create a directory to navigate into
        Path directoryPath = Paths.get(rootPath, "Dir");
        int rootTestI = directoryPath.toString().indexOf("RootTest");
        String output = "";
        if (rootTestI != -1) {
            output = directoryPath.toString().substring(rootTestI);
        }
        // Act
        fileManager.processCommand("1 .");
        // Trouver l'index de "RootTest"
        int rootTestIndex = fileManager.currentDirectory.getPath().indexOf("RootTest");
        // Si "RootTest" est trouvé, extraire la sous-chaîne à partir de cet index
        String outputPath = "";
        if (rootTestIndex != -1) {
            outputPath = fileManager.currentDirectory.getPath().substring(rootTestIndex);
        }
        // Assert
        assertEquals(output, outputPath);
    }

    @Test
    void processCommand_CreateDirectory_MissingName() {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Act
        fileManager.processCommand("mkdir");
        // Assert - Check if an error message is displayed
        assertEquals("Missing directory name.", fileManager.Output);
    }

}
