package fr.uvsq.cprog;

import java.io.File;
import java.util.Arrays;

/*** La classe ConsoleManager représente
 * un gestionnaire de console pour les opérations de fichier.*/
public class ConsoleManager {
  protected DirectoryManager currentDirectory;
  public int lastNer;
  public String output = "";
  public String annot = "";
  private String operation = "";
  // Soit copy, Soit cut
  // pour la faire passer dans pastFile à fin de savoir si on supprime le fichier copié ou non
  Object[] copiedFile = new Object[2]; // Stocker path et NER du fichier copier
  private final FileManager fileManager;
  private final AnnotationManager annotationManager;
  private final DirectoryManager directoryManager;

  /*** Construit une instance de ConsoleManager avec le chemin racine spécifié.
   *
   * @param rootPath Le chemin racine du système de fichiers.*/
  public ConsoleManager(String rootPath) {
    this.currentDirectory = new DirectoryManager(0, rootPath, this);
    lastNer = 0;
    this.fileManager = new FileManager(rootPath, this);
    this.annotationManager = new AnnotationManager(this);
    this.directoryManager = new DirectoryManager(0, rootPath, this);
  }

  /*** Traite la commande entrée par l'utilisateur et effectue les opérations correspondantes.
   *
   * @param command La commande entrée par l'utilisateur.*/
  public void processCommand(String command) {
    String[] parts = command.split(" ");
    int ner;
    String cmd;

    cmd = parts[0].toLowerCase();
    if (parts[0].matches("\\d+")) {
      try {
        ner = Integer.parseInt(parts[0]);
        cmd = (parts.length > 1) ? parts[1].toLowerCase() : " ";
      } catch (NumberFormatException e) {
        cmd = parts[0].toLowerCase();
        ner = lastNer;
      }
      lastNer = ner;
    } else {
      ner = lastNer;
    }
    if (!cmd.isEmpty()) {
      switch (cmd) {
        case "copy":
          operation = "copy";
          fileManager.copyFile(ner);
          output = fileManager.output;
          annot = fileManager.annot;
          break;
        case "cut":
          operation = "cut";
          fileManager.copyFile(ner);
          output = fileManager.output;
          annot = fileManager.annot;
          break;
        case "past":
          fileManager.pasteFile(operation);
          output = fileManager.output;
          annot = fileManager.annot;
          break;
        case "..":
          directoryManager.navigateUp();
          output = directoryManager.output;
          break;
        case ".":
          directoryManager.navigateIntoDirectory(ner);
          output = directoryManager.output;
          break;
        case "mkdir":
          if (parts.length > 1) {
            directoryManager.createDirectory(parts[1]);
            output = directoryManager.output;
          } else {
            output = "Missing directory name.";
            System.out.println(output);
          }
          break;
        case "visu":
          fileManager.viewFile(ner);
          output = fileManager.output;
          break;
        case "find":
          if (parts.length > 1) {
            findElement(parts[1]);
          } else {
            output = "Missing file name.";
            System.out.println(output);
          }
          break;
        case "+":
          if (parts.length > 2) {
            String annotationText = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
            annotationManager.annotateEr(ner, annotationText);
            output = annotationManager.output;
          } else {
            output = "Missing annotation text.";
            System.out.println(output);
          }
          break;
        case "-":
          annotationManager.removeAnnotation(ner);
          output = annotationManager.output;
          break;
        case " ":
          designateElement(ner);
          break;
        case "help":
          displayHelp();
          break;
        default:
          output = "Unrecognized command.";
          System.out.println(output);
          break;
      }
    } else if (parts.length == 1 && parts[0].equalsIgnoreCase("mkdir")) {
      output = "Missing directory name.";
      System.out.println(output);
    } else {
      output = "Incomplete command.";
      System.out.println(output);
    }
  }

  /*** Obtient le chemin d'un élément par son NER.
   *
   * @param ner L'identifiant de element (NER).
   * @return Le chemin de l'élément.*/
  public String get_path_by_Ner(int ner) {
    File[] files = new File(currentDirectory.getPath()).listFiles();
    if (files != null && ner >= 1 && ner <= files.length) {
      int ner1 = 1; // COMPTEUR
      for (File file : files) {
        if (ner1 == ner) {
          return file.getAbsolutePath();
        }
        ner1++;
      }
    }
    return null;
  }

  /*** Affiche le répertoire courant et ses éléments.*/
  public void displayCurrentDirectory() {
    // Afficher le chemin du répertoire courant
    System.out.println("Current Directory: " + currentDirectory.getPath());
    File[] files = new File(currentDirectory.getPath()).listFiles();
    if (files != null) {
      int ner = 1;
      System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t TYPE%n", "NER", "NOM");
      for (File file : files) {
        if (file.isDirectory()) {
          System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t (directory)%n", ner, file.getName());
        } else {
          System.out.printf("%-" + 5 + "s \t %-" + 30 + "s \t (file)%n", ner, file.getName());
        }
        ner++;
      }
    } else {
      System.out.println("Error retrieving directory items.");
    }
  }

  /*** Trouve un élément par son nom de fichier de manière récursive.
   *
   * @param fileName Le nom du fichier à rechercher.*/
  public void findElement(String fileName) {
    output = "";
    findElement_recursive(currentDirectory.getPath(), fileName);
    if (output == null || output.isEmpty()) {
      output = "File not found: " + fileName;
      System.out.println(output);
    }
  }

  /*** Trouve un élément de manière récursive.
   *
   * @param directoryPath Le chemin du répertoire à explorer.
   * @param fileName      Le nom du fichier à rechercher.*/
  public void findElement_recursive(String directoryPath, String fileName) {
    File directory = new File(directoryPath);
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().equals(fileName)) {
          output = file.getAbsolutePath();
          System.out.println(output);
          return; // Ajout pour arrêter la recherche une fois le fichier trouvé
        }
        if (file.isDirectory()) {
          findElement_recursive(file.getAbsolutePath(), fileName);
        }
      }
    }
  }

  /*** Désigne un élément par son NER, affiche son nom et son annotation s'il existe.
   *
   * @param ner L'identifiant de la Reconnaissance d'Entité Nommée (NER).*/
  public void designateElement(int ner) {
    String targetPath = get_path_by_Ner(ner);
    if (targetPath != null) {
      File targetElement = new File(targetPath);
      if (targetElement.exists()) {
        // Afficher le nom de l'élément
        output = "User designates element number " + ner + ": " + targetElement.getName();
        System.out.println(output);
        // Afficher l'annotation correspondante
        annotationManager.displayAnnotation(ner);
      } else {
        output = "Element not found for NER " + ner;
        System.out.println(output);
      }
    } else {
      output = "Element not found for NER " + ner;
      System.out.println(output);
    }
  }

  /*** Affiche le message d'aide avec la liste des commandes disponibles.*/
  private void displayHelp() {
    System.out.println("\nLes commandes du gestion de fichiers à implémenter sont:");
    System.out.println("[<NER>] copy");
    System.out.println("[<NER>] cut");
    System.out.println("past");
    System.out.println("\tSi élément existe, alors le nom du nouvel sera concaténé avec \"-copy\"");
    System.out.println("..");
    System.out.println("\tPour remonter d’un cran dans le système de fichiers");
    System.out.println("[<NER>] .");
    System.out.println("\t Pour entrer dans un répertoire Exemple : \"4 .\"");
    System.out.println("mkdir <nom> ");
    System.out.println("\tPour créer un répertoire");
    System.out.println("[<NER>] visu");
    System.out.print("\tVoir le contenu d un fichier texte.");
    System.out.println("Si il n est pas de type texte,on affiche sa taille.");
    System.out.println("find <nom fichier>");
    System.out.print("\tRecherche dans toutes les sous-répertoires du répertoire courant");
    System.out.println("le(s) fichier(s) et les affiche.");
    System.out.println("3 + \"ceci est un texte\" ");
    System.out.println("\tLe texte (note) est ajouté ou concaténé au texte existant de ER");
    System.out.println("3 - ");
    System.out.println("\tRetire tout le texte associé à l’ER 3");
  }
}