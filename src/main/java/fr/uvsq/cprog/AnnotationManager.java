package fr.uvsq.cprog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class AnnotationManager {

    private final ConsoleManager consoleManager;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
    public String Output = "";

    public AnnotationManager(ConsoleManager fileManager) {
        this.consoleManager = fileManager;
    }

    public void annotateER(int NER, String annotationText) {
        String currentDirectoryPath = consoleManager.currentDirectory.getPath();
        String notesFilePath = Paths.get(currentDirectoryPath, "notes.txt").toString();
        String targetPath = consoleManager.getPathByNER(NER);
        String fileName = targetPath != null ? new File(targetPath).getName() : null;
        String lineNumberPrefix = fileName + " ";

        if (targetPath != null) {
            try {
                Path path = Paths.get(notesFilePath);
                List<String> lines = Files.readAllLines(path);
                // Vérifier si le NER existe déjà dans le fichier notes.txt
                boolean nerExistsInNotes = lines.stream().anyMatch(line -> line.startsWith(lineNumberPrefix));
                if (nerExistsInNotes) {
                    // Si le numéro NER existe, concaténer le nouveau texte à l'ancien dans la même ligne
                    for (int i = 0; i < lines.size(); i++) {
                        if (lines.get(i).startsWith(lineNumberPrefix)) {
                            lines.set(i, lineNumberPrefix + lines.get(i).substring(lineNumberPrefix.length()) + annotationText);
                            break;
                        }
                    }
                } else {
                    // Si le numéro NER n'existe pas, ajouter une nouvelle ligne avec le NER et le texte
                    lines.add(lineNumberPrefix + annotationText);
                }
                // Réécrire le fichier avec les mises à jour
                Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
                Output = "Annotation added to notes.txt for NER " + NER + " in the current directory.";
                System.out.println(Output);
            } catch (IOException e) {
                Output = "Error adding annotation to notes.txt: " + e.getMessage();
                logger.error(Output);
            }
        } else {
            Output = "Element not found for NER " + NER;
            System.out.println(Output);
        }
    }

    public void removeAnnotation(int NER) {
        String currentDirectoryPath = consoleManager.currentDirectory.getPath();
        String notesFilePath = Paths.get(currentDirectoryPath, "notes.txt").toString();
        String targetPath = consoleManager.getPathByNER(NER);
        String fileName = targetPath != null ? new File(targetPath).getName() : null;
        String lineNumberPrefix = fileName + " ";


        try {
            Path path = Paths.get(notesFilePath);
            List<String> lines = Files.readAllLines(path);
            boolean nerExistsInCurrentDirectory = lines.stream().anyMatch(line -> line.startsWith(lineNumberPrefix));
            if (!nerExistsInCurrentDirectory) {
                // Si le numéro NER n'existe pas dans le répertoire courant, ne rien faire
                Output = "NER " + NER + " does not have an annotation in the current directory.";
                System.out.println(Output);
                return;
            }
            // Supprimer la ligne correspondante au NER
            lines.removeIf(line -> line.startsWith(lineNumberPrefix));
            // Réécrire le fichier avec les mises à jour
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
            Output = "Annotation removed from notes.txt for NER " + NER + " in the current directory.";
            System.out.println(Output);
        } catch (IOException e) {
            Output = "Error removing annotation from notes.txt: " + e.getMessage();
            logger.error(Output);
        }
    }

    public String displayAnnotation(int NER) {
        String annotation = "";
        String currentDirectoryPath = consoleManager.currentDirectory.getPath();
        String notesFilePath = Paths.get(currentDirectoryPath, "notes.txt").toString();
        String targetPath = consoleManager.getPathByNER(NER);
        String fileName = targetPath != null ? new File(targetPath).getName() : null;
        String lineNumberPrefix = fileName + " ";

        try {
            List<String> lines = Files.readAllLines(Paths.get(notesFilePath));
            boolean nerExistsInNotes = lines.stream().anyMatch(line -> line.startsWith(lineNumberPrefix));

            if (nerExistsInNotes) {
                // Trouver et afficher l'annotation correspondante
                annotation = lines.stream()
                        .filter(line -> line.startsWith(lineNumberPrefix))
                        .findFirst()
                        .orElse("");
                Output = "Annotation: " + annotation;
                System.out.println(Output);
            } else {
                // Si aucune annotation n'est trouvée, afficher une chaîne vide
                Output = "Annotation: ";
                System.out.println(Output);
            }

        } catch (IOException e) {
            Output = "Error reading annotation from notes.txt: " + e.getMessage();
            logger.error(Output);
        }
        return annotation;
    }
}
