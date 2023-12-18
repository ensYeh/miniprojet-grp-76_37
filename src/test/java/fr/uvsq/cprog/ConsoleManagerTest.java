package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConsoleManagerTest {
    String rootPath = ".\\RootTest";
    ConsoleManager consoleManager = new ConsoleManager(rootPath);


    @Test
    void TestUnrecognizedCommand() {
        consoleManager.processCommand("invalid");
        assertEquals("Unrecognized command.", consoleManager.output);
    }

    @Test
    void TestgetPathByNer_Success() {
        String path = consoleManager.get_path_by_Ner(4);
        int rootTestIndex = path.indexOf("Root");
        String outputPath = "";
        if (rootTestIndex != -1) {
            outputPath = path.substring(rootTestIndex);
        }
        assertEquals("RootTest\\Zola", outputPath);
    }

    @Test
    void TestgetPathByNer_ReturnNull() {
        consoleManager.get_path_by_Ner(50);
        assertEquals(null, consoleManager.get_path_by_Ner(50));
    }


    @Test
    void TestFindFile_Success() {
        consoleManager.processCommand("find Settings.json");
        int rootTestIndex = consoleManager.output.indexOf("RootTest");
        String outputPath = "";
        if (rootTestIndex != -1) {
            outputPath = consoleManager.output.substring(rootTestIndex);
        }
        assertEquals("RootTest\\Zola\\Settings.json", outputPath);
    }

    @Test
    void TestFindFile_NotFound() {
        consoleManager.processCommand("find AZERTY");
        assertEquals("File not found: AZERTY", consoleManager.output);
    }

    @Test
    void TestDesignateElement_Success() {
        consoleManager.processCommand("3");
        assertEquals("User designates element number 3: Topologie.PNG", consoleManager.output);
    }

    @Test
    void TestdesignateElement_NotFound() {
        consoleManager.processCommand("55");
        assertEquals("Element not found for NER 55", consoleManager.output);
    }

    @Test
    void TestDisplayHelp(){
        ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOut));
        consoleManager.processCommand("help");
        // Effectue des assertions sur la sortie
        assertTrue(systemOut.toString().contains("Les commandes du gestion de fichiers à implémenter sont:"));
        assertTrue(systemOut.toString().contains("[<NER>] copy"));
        assertTrue(systemOut.toString().contains("[<NER>] cut"));
        assertTrue(systemOut.toString().contains("past"));
    }
}
