package fr.uvsq.cprog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Classe qui gère les opérations liées aux répertoires.
 * CreateDirectory / .. / NER .
 */
public class DirectoryManager extends FileManager {
  private final List<FileManager> elements;
  private final ConsoleManager consoleManager;

  /*** Constructeur de la classe DirectoryManager.
   *
   * @param ner           Le numéro NER associé au répertoire.
   * @param path          Le chemin du répertoire.
   * @param fileManager   Le gestionnaire de fichiers associé.*/
  public DirectoryManager(int ner, String path, ConsoleManager fileManager) {
    super(ner, path);
    this.elements = new ArrayList<>();
    this.consoleManager = fileManager;
  }

  private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);

  /*** Récupère la liste des éléments du répertoire.
   *
   * @return La liste des éléments du répertoire.*/
  public List<FileManager> getElements() {
    return elements;
  }

  /*** Ajoute un élément à la liste des éléments du répertoire.
   *
   * @param element L'élément a ajouté.*/
  public void addElement(FileManager element) {
    elements.add(element);
  }

  /*** Navigue vers le répertoire parent.*/
  public void navigateUp() {
    if (consoleManager.currentDirectory.getPath().equals("C:\\")) {
      output = "Already at the root.";
      System.out.println(ConsoleColors.YELLOW + output + ConsoleColors.RESET);
      return;
    }
    String parentPath = consoleManager.currentDirectory.getPath();
    File parentFile = new File(parentPath).getParentFile();
    output = String.valueOf(parentFile);
    consoleManager.currentDirectory = new DirectoryManager(0,
            parentFile.getAbsolutePath(), consoleManager);
    if (parentFile != null && !parentPath.endsWith("C:\\")) {
      consoleManager.currentDirectory = new DirectoryManager(0,
            parentFile.getAbsolutePath(), consoleManager);
    }
  }

  /*** Navigue dans le répertoire associé au numéro NER spécifié.
   *
   * @param ner Le numéro NER du répertoire cible.*/
  public void navigateIntoDirectory(int ner) {
    String targetDirectoryPath = consoleManager.get_path_by_Ner(ner);
    if (targetDirectoryPath != null) {
      File targetDirectory = new File(targetDirectoryPath);
      if (targetDirectory.isDirectory()) {
        consoleManager.currentDirectory.setPath(targetDirectoryPath);
      } else {
        output = "The element corresponding to NER is a file, not a directory.";
        System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
      }
    } else {
      output = "Directory not found.";
      System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Crée un nouveau répertoire avec le nom spécifié.
   *
   * @param name Le nom du nouveau répertoire.*/
  public void createDirectory(String name) {
    int newNer = consoleManager.currentDirectory.getElements().size() + 1;
    DirectoryManager newDirectory = new DirectoryManager(newNer,
            consoleManager.currentDirectory.getPath(), consoleManager);
    Path newDirectoryPath = Paths.get(consoleManager.currentDirectory.getPath(), name);
    try {
      Files.createDirectory(newDirectoryPath);
      newDirectory.setPath(newDirectoryPath.toString());
      consoleManager.currentDirectory.addElement(newDirectory);
      // Crée le fichier notes.txt dans le nouveau répertoire
      Path notesFilePath = Paths.get(newDirectoryPath.toString(), "notes.txt");
      Files.createFile(notesFilePath);
      output = "Directory created successfully. notes.txt created.";
      System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
    } catch (IOException e) {
      output = "Error creating directory. " + e.getMessage();
      logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }
}
