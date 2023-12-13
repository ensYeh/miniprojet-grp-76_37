package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileManagerTest {
    String rootPath = ".\\RootTest\\Z_File";
    ConsoleManager consoleManager = new ConsoleManager(rootPath);

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
    void viewFile_ExistingFile_Success() throws IOException {
        consoleManager.processCommand("3 visu");
        assertEquals("Lorem ipsum dolor sit amet", consoleManager.Output);
    }

    @Test
    void viewFile_OutputFileNotFound() throws IOException {
        consoleManager.processCommand("22 visu");
        assertEquals("File not found for NER 22", consoleManager.Output);
    }

    @Test
    void viewFile_Empty() throws IOException {
        consoleManager.processCommand("2 visu");
        assertEquals("Empty file", consoleManager.Output);
    }

    @Test
    void viewFile_NotText() throws IOException {
        consoleManager.processCommand("6 visu");
        assertEquals("The file is not a text type. Displaying size: 41329 bytes", consoleManager.Output);
    }

    @Test
    void viewFile_Directory() throws IOException {
        consoleManager.processCommand("1 visu");
        assertEquals("The element corresponding to NER is not a file.", consoleManager.Output);
    }

    @Test
    void copyFile_ExistingFile_Success() {
        consoleManager.processCommand("3 copy");
        assertEquals("Element copied successfully.", consoleManager.Output);
        assertEquals("File.txt \" Fichier texte contient texte inutile \"", consoleManager.Annot);
    }

    @Test
    void copyFile_Directory_Success() {
        consoleManager.processCommand("1 copy");
        assertEquals("Element copied successfully.", consoleManager.Output);
        assertEquals("Bimo \"Repertoire Bimo \"", consoleManager.Annot);
    }

    @Test
    void copyElement_NotFound() {
        consoleManager.processCommand("22 copy");
        assertEquals("Element not found for NER 22", consoleManager.Output);
    }

    @Test
    void Copy_pasteFile_Success() {
        consoleManager.processCommand("3 copy");
        assertEquals("Element copied successfully.", consoleManager.Output);
        assertEquals("File.txt \" Fichier texte contient texte inutile \"", consoleManager.Annot);
        consoleManager.processCommand("1 .");
        consoleManager.processCommand("past");
        assertEquals("Element pasted successfully.", consoleManager.Output);
    }

    @Test
    void Copy_pasteDirectory_Success() {
        consoleManager.processCommand("4 copy");
        assertEquals("Element copied successfully.", consoleManager.Output);
        assertEquals("Malt \" Directory Malt \"", consoleManager.Annot);
        consoleManager.processCommand("1 .");
        consoleManager.processCommand("past");
        assertEquals("Element pasted successfully.", consoleManager.Output);
    }

    @Test
    void Cut_pasteFile_Success() {
        String rootPath = ".\\RootTest\\Z_File\\ZA_cut";
        ConsoleManager consoleManager = new ConsoleManager(rootPath);
        consoleManager.processCommand("1 cut");
        assertEquals("Element copied successfully.", consoleManager.Output);
        consoleManager.processCommand("..");
        consoleManager.processCommand("1 .");
        consoleManager.processCommand("past");
        assertEquals("File deletion successful.", consoleManager.Output);
    }

    @Test
    void Cut_pasteDirectory_Success() {
        // Créer un repertoire ZZ le couper et coller dans un autre repertoire
        // Générer une chaîne de 10 caractères aléatoire
        String randomString = generateRandomString(3);
        String directoryName = "ZZ_" + randomString;
        consoleManager.processCommand("mkdir " + directoryName);
        // TEST DE CUT
        consoleManager.processCommand("8 cut");
        assertEquals("Element copied successfully.", consoleManager.Output);
        consoleManager.processCommand("1 .");
        consoleManager.processCommand("past");
        assertEquals("Directory deletion successful.", consoleManager.Output);
    }
}
