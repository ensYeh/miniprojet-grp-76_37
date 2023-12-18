package fr.uvsq.cprog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * La classe AnnotationManager représente un gestionnaire d'annotations.
 */
public class AnnotationManager {
  private final ConsoleManager consoleManager;
  private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
  public String output = "";

  /*** Construit une instance de AnnotationManager avec le gestionnaire de console spécifié.
   *
   * @param fileManager Le gestionnaire de console.*/
  public AnnotationManager(ConsoleManager fileManager) {
    this.consoleManager = fileManager;
  }

  /**
   * Ajoute une annotation à un élément avec un NER et un texte d'annotation donné.
   *
   * @param ner             L'identifiant de element (NER).
   * @param annotationText Le texte de l'annotation.*/
  public void annotateEr(int ner, String annotationText) {
    String currentDirectoryPath = consoleManager.currentDirectory.getPath();
    String notesFilePath = Paths.get(currentDirectoryPath, "notes.txt").toString();
    String targetPath = consoleManager.get_path_by_Ner(ner);
    String fileName = targetPath != null ? new File(targetPath).getName() : null;
    String lineNumberPrefix = fileName + " ";
    if (targetPath != null) {
      try {
        Path path = Paths.get(notesFilePath);
        List<String> lines = Files.readAllLines(path);
        // Vérifier si le NER existe déjà dans le fichier notes.txt
        boolean nerExistsInNotes = lines.stream().anyMatch(line ->
                line.startsWith(lineNumberPrefix));
        if (nerExistsInNotes) {
          // Si le numéro NER existe, concaténer le nouveau texte à l'ancien dans la même ligne
          for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(lineNumberPrefix)) {
              lines.set(i, lineNumberPrefix
                      + lines.get(i).substring(lineNumberPrefix.length()) + annotationText);
              break;
            }
          }
        } else {
          // Si le numéro NER n'existe pas, ajouter une nouvelle ligne avec le NER et le texte
          lines.add(lineNumberPrefix + annotationText);
        }
        // Réécrire le fichier avec les mises à jour
        Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
        output = "Annotation added to notes.txt for NER " + ner + " in the current directory.";
        System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
      } catch (IOException e) {
        output = "Error adding annotation to notes.txt: " + e.getMessage();
        logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
      }
    } else {
      output = "Element not found for NER " + ner;
      System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Supprime l'annotation pour un NER donné.
   *
   * @param ner L'identifiant de element (NER).*/
  public void removeAnnotation(int ner) {
    String currentDirectoryPath = consoleManager.currentDirectory.getPath();
    String notesFilePath = Paths.get(currentDirectoryPath, "notes.txt").toString();
    String targetPath = consoleManager.get_path_by_Ner(ner);
    String fileName = targetPath != null ? new File(targetPath).getName() : null;
    String lineNumberPrefix = fileName + " ";
    try {
      Path path = Paths.get(notesFilePath);
      List<String> lines = Files.readAllLines(path);
      boolean nerExistsInCurrentDirectory = lines.stream().anyMatch(line ->
              line.startsWith(lineNumberPrefix));
      if (!nerExistsInCurrentDirectory) {
        // Si le numéro NER n'existe pas dans le répertoire courant, ne rien faire
        output = "NER " + ner + " does not have an annotation in the current directory.";
        System.out.println(ConsoleColors.YELLOW + output + ConsoleColors.RESET);
        return;
      }
      // Supprimer la ligne correspondante au NER
      lines.removeIf(line -> line.startsWith(lineNumberPrefix));
      // Réécrire le fichier avec les mises à jour
      Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
      output = "Annotation removed from notes.txt for NER " + ner + " in the current directory.";
      System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
    } catch (IOException e) {
      output = "Error removing annotation from notes.txt: " + e.getMessage();
      logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Affiche l'annotation pour un NER donné.
   *
   * @param ner L'identifiant de element (NER).
   * @return Le texte de l'annotation.
   */
  public String displayAnnotation(int ner) {
    String annotation = "";
    String currentDirectoryPath = consoleManager.currentDirectory.getPath();
    String notesFilePath = Paths.get(currentDirectoryPath, "notes.txt").toString();
    String targetPath = consoleManager.get_path_by_Ner(ner);
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
        output = "Annotation: " + annotation;
        System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
      } else {
        // Si aucune annotation n'est trouvée, afficher une chaîne vide
        output = "Annotation: ";
        System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
      }
    } catch (IOException e) {
      output = "Error reading annotation from notes.txt: " + e.getMessage();
      logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
    return annotation;
  }
}
