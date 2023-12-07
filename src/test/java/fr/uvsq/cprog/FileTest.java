package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileTest {

    @Test
    void processCommand_ViewFile_Success() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Act
        fileManager.processCommand("2 visu");
        assertEquals("azzzzzzzzzzzzzzzz", fileManager.Output);
    }

    @Test
    void processCommand_ViewDirectory() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);

        // Act
        fileManager.processCommand("1 visu");

        // Assert
        assertEquals("The element corresponding to NER is not a file.", fileManager.Output);
    }

    @Test
    void processCommand_ViewNonExistentFile() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);

        // Act
        fileManager.processCommand("10 visu");

        // Assert
        assertEquals("File not found for NER 10", fileManager.Output);
    }

    @Test
    void processCommand_ViewEmptyTextFile_Success() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);

        // Act
        fileManager.processCommand("3 visu");
        // Assert
        assertEquals("Empty file", fileManager.Output);
    }

    @Test
    void processCommand_AnnotateER_Success() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Create a text file to annotate
        Path filePath = Paths.get(rootPath, "TestFile.txt");
        // Act
        fileManager.processCommand("3 + This is an annotation");
        // Assert - Check if the annotation is added to the file
        assertTrue(Files.readString(filePath).contains("This is an annotation"));
    }

    @Test
    void processCommand_RemoveAnnotation_Success() throws IOException {
        // Arrange
        String rootPath = "..\\RootTest";
        ConsoleFileManager fileManager = new ConsoleFileManager(rootPath);
        // Create a text file with an annotation
        Path filePath = Paths.get(rootPath, "TestFile.txt");
        // Act
        fileManager.processCommand("3 -");
        // Assert - Check if the annotation is removed from the file
        assertTrue(Files.readString(filePath).isEmpty());
    }


}
