package fr.uvsq.cprog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationManagerTest {
    String rootPath = ".\\RootTest";
    ConsoleManager consoleManager = new ConsoleManager(rootPath);
    AnnotationManager annotationManager = new AnnotationManager(consoleManager);


    @Test
    void testAnnotateER() {
        // 1 + "Ceci est une annotation de test."
        consoleManager.processCommand("1 + \"Ceci est une annotation de directory AA\"");
        assertEquals("Annotation added to notes.txt for NER 1 in the current directory.", consoleManager.Output);
    }

    @Test
    void testAnnotateER_ElementNotFound() throws Exception {
        // 22 + "Ceci est une annotation de test."
        consoleManager.processCommand("22 + \"Ceci est une annotation de test.\"");
        assertEquals("Element not found for NER 22", consoleManager.Output);
    }


    @Test
    void testRemoveAnnotation() throws Exception {
        // 1 -
        consoleManager.processCommand("1 + \"Ceci est une annotation de directory AA\""); // Pour ajouter l'annotation a supprimé
        consoleManager.processCommand("1 -");
        assertEquals("Annotation removed from notes.txt for NER 1 in the current directory.", consoleManager.Output);
    }

    @Test
    void testRemoveAnnotation_NERNotFound() {
        consoleManager.processCommand("22 -");
        assertEquals("NER 22 does not have an annotation in the current directory.", consoleManager.Output);
    }

    @Test
    void testDisplayAnnotation() {
        String result = annotationManager.displayAnnotation(3);
        assertEquals("Topologie.PNG \"Ceci est une annotation d'image PNG.\"", result);
    }

    @Test
    void testDisplayAnnotation_NotFound() {
        String result = annotationManager.displayAnnotation(22);
        assertEquals("", result); // Chaine vide
    }

}
