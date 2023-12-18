package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DirectoryManagerTest {
    String rootPath = ".\\RootTest";
    ConsoleManager consoleManager = new ConsoleManager(rootPath);
    String outputPath = "";
    // Générer une chaîne de 10 caractères aléatoire
    String randomString = generateRandomString(10);
    String directoryName = "Directory_" ;

    // Méthode pour générer une chaîne aléatoire qui sera utilisé pour generation du nom de repertoire pour les tests
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    @Test
    void TestCreateDirectory_Success() throws IOException {
        String rootPath = ".\\RootTest\\Zola";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        String directoryName = "Directory_" + randomString ;
        consoleManager.processCommand("mkdir " + directoryName);
        assertEquals("Directory created successfully. notes.txt created.", consoleManager.output);
    }
    @Test
    void TestCreateDirectory_ExistDeja() throws IOException {
        String rootPath = ".\\RootTest\\Zola";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        consoleManager.processCommand("mkdir Directory_");
        assertEquals("Error creating directory. .\\RootTest\\Zola\\Directory_", consoleManager.output);
    }

    @Test
    void TestCreateDirectory_MissingName() {
        consoleManager.processCommand("mkdir");
        assertEquals("Missing directory name.", consoleManager.output);
    }

    @Test
    void NavigateUp_Root() {
        String rootPath = "C:\\";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        consoleManager.processCommand("..");
        assertEquals("Already at the root.", consoleManager.output);
    }

    @Test
    void TestNavigateUp_Success() {
        // Arrange
        String rootPath = ".\\RootTest\\Zola";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        consoleManager.processCommand("..");
        int rootTestIndex = consoleManager.output.indexOf("RootTest");
        // Si "RootTest" est trouvé, extraire la sous-chaîne à partir de cet index
        if (rootTestIndex != -1) {
            outputPath = consoleManager.output.substring(rootTestIndex);
        }
        // Assert
        assertEquals("RootTest", outputPath);
    }


    @Test
    void TestNavigateIntoDirectory_Success() {
        consoleManager.processCommand("1 .");
        // Pour que le chemin commence de la racine définie par nous
        int rootTestIndex = consoleManager.currentDirectory.getPath().indexOf("RootTest");
        // Si "RootTest" est trouvé, extraire la sous-chaîne à partir de cet index
        if (rootTestIndex != -1) {
            outputPath = consoleManager.currentDirectory.getPath().substring(rootTestIndex);
        }
        String Output = "Current Directory: " + outputPath;
        assertEquals("Current Directory: RootTest\\AA", Output);
    }

    @Test
    void TestNavigateIntoFile() {
        consoleManager.processCommand("2 .");
        assertEquals("The element corresponding to NER is a file, not a directory.", consoleManager.output);
    }

    @Test
    void TestNavigateIntoDirectory_NotFound() {
        consoleManager.processCommand("22 .");
        assertEquals("Directory not found.", consoleManager.output);
    }
}
