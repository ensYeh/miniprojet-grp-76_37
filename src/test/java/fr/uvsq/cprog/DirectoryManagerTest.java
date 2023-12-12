package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryManagerTest {
    String rootPath = ".\\RootTest";
    ConsoleManager consoleManager = new ConsoleManager(rootPath);
    String outputPath = "";

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
    void CreateDirectory_Success() throws IOException {
        String rootPath = ".\\RootTest\\Zola";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        // Générer une chaîne de 10 caractères aléatoire
        String randomString = generateRandomString(10);
        String directoryName = "Directory_" + randomString;
        consoleManager.processCommand("mkdir " + directoryName);
        assertEquals("Directory created successfully. notes.txt created.", consoleManager.Output);
    }

    @Test
    void CreateDirectory_MissingName() {
        consoleManager.processCommand("mkdir");
        assertEquals("Missing directory name.", consoleManager.Output);
    }

    @Test
    void NavigateUp_Root() {
        String rootPath = ".\\Root";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        consoleManager.processCommand("..");
        assertEquals("Already at the root.", consoleManager.Output);
    }

    @Test
    void NavigateUp_Success() {
        // Arrange
        String rootPath = ".\\RootTest\\Zola";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        consoleManager.processCommand("..");
        int rootTestIndex = consoleManager.Output.indexOf("RootTest");
        // Si "RootTest" est trouvé, extraire la sous-chaîne à partir de cet index
        if (rootTestIndex != -1) {
            outputPath = consoleManager.Output.substring(rootTestIndex);
        }
        // Assert
        assertEquals("RootTest", outputPath);
    }


    @Test
    void NavigateIntoDirectory_Success() {
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
    void NavigateIntoFile() {
        consoleManager.processCommand("2 .");
        assertEquals("The element corresponding to NER is a file, not a directory.", consoleManager.Output);
    }

    @Test
    void NavigateIntoDirectory_NotFound() {
        consoleManager.processCommand("22 .");
        assertEquals("Directory not found.", consoleManager.Output);
    }
}
