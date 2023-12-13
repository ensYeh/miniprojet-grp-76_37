package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsoleManagerTest {
    String rootPath = ".\\RootTest";
    ConsoleManager consoleManager = new ConsoleManager(rootPath);


    @Test
    void FindFile_Success() {
        consoleManager.processCommand("find Settings.json");
        int rootTestIndex = consoleManager.Output.indexOf("RootTest");
        String outputPath = "";
        if (rootTestIndex != -1) {
            outputPath = consoleManager.Output.substring(rootTestIndex);
        }
        assertEquals("RootTest\\Zola\\Settings.json", outputPath);
    }

    @Test
    void FindFile_NotFound() {
        consoleManager.processCommand("find AZERTY");
        assertEquals("File not found: AZERTY", consoleManager.Output);
    }

    @Test
    void TestDesignateElement_Success() {
        consoleManager.processCommand("3");
        assertEquals("User designates element number 3: Topologie.PNG", consoleManager.Output);
    }

    @Test
    void designateElement_NotFound() {
        consoleManager.processCommand("55");
        assertEquals("Element not found for NER 55", consoleManager.Output);
    }
}
