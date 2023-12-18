package fr.uvsq.cprog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*** La classe FileManager gère les opérations de gestion de fichiers.
 Visu - Copy - Cut - Past ... */
public class FileManager implements Serializable {
  protected int ner;
  private String path;
  private static final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
  public String output = "";
  public String annot = "";
  private ConsoleManager consoleManager;
  private AnnotationManager annotationManager;

  /*** Constructeur de la classe FileManager.
   *
   * @param path        Le chemin du fichier.
   * @param fileManager Le gestionnaire de fichiers associé.*/
  public FileManager(String path, ConsoleManager fileManager) {
    this.path = path;
    this.consoleManager = fileManager;
    this.annotationManager = new AnnotationManager(fileManager);
  }

  /*** Constructeur de la classe FileManager.
   *
   * @param ner  Le numéro NER associé au fichier.
   * @param path Le chemin du fichier.*/
  public FileManager(int ner, String path) {
    this.ner = ner;
    this.path = path;
  }

  /*** Définit le chemin du fichier.
   *
   * @param path Le nouveau chemin du fichier.*/
  public void setPath(String path) {
    this.path = path;
  }

  /*** Obtient le chemin du fichier.
   *
   * @return Le chemin du fichier.*/
  public String getPath() {
    return path;
  }

  /**
   * Affiche le contenu d'un fichier spécifié par son numéro NER.
   *
   * @param ner Le numéro NER du fichier à afficher.
   */
  public void viewFile(int ner) {
    String filePath = consoleManager.get_path_by_Ner(ner);
    if (filePath != null) {
      File targetFile = new File(filePath);
      if (targetFile.isFile()) {
        String fileName = targetFile.getName();
        if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
          try {
            InputStream ips = new FileInputStream(targetFile);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            while ((ligne = br.readLine()) != null) {
              output += ligne;
              System.out.println(ligne);
            }
            br.close();
          } catch (IOException e) {
            output = "Error reading file: " + e.getMessage();
            logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
          }
          if (Objects.equals(output, "") || output.isEmpty()) {
            output = "Empty file";
            System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
          }
        } else {
          output = "The file is not a text type. Displaying size: "
            + targetFile.length() + "bytes";
          System.out.println("The file is not a text type. Displaying size: " 
                  + ConsoleColors.GREEN + targetFile.length() + ConsoleColors.RESET 
                  + ConsoleColors.GREEN + "bytes" + ConsoleColors.RESET);
        }
      } else {
        output = "The element corresponding to NER is not a file.";
        System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
      }
    } else {
      output = "File not found for NER " + ner;
      System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Copie le fichier spécifié par son numéro NER.
   *
   * @param ner Le numéro NER d'element à copier.*/
  public void copyFile(int ner) {
    String sourceFilePath = consoleManager.get_path_by_Ner(ner);
    if (sourceFilePath != null) {
      File sourceFile = new File(sourceFilePath);
      String copiedFilePath = sourceFile.getAbsolutePath();
      consoleManager.copiedFile[0] = ner;
      consoleManager.copiedFile[1] = copiedFilePath;
      output = "Element copied successfully.";
      System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
      // Maintenant, on appele la méthode pour copier l'annotation
      annot = annotationManager.displayAnnotation(ner);
    } else {
      output = "Element not found for NER " + ner;
      System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Colle le fichier ou le répertoire copié dans le répertoire courant.
   *
   * @param operation L'opération précédente ("copy" ou "cut").*/
  public void pasteFile(String operation) {
    if (consoleManager.copiedFile[1] != null) {
      String sourceFilePath = (String) consoleManager.copiedFile[1];
      File sourceFile = new File(sourceFilePath);
      if (sourceFile.exists()) {
        String fileName = sourceFile.getName().replaceFirst("[.][^.]+$", "");
        String currentPath = consoleManager.currentDirectory.getPath();
        String newFilePath = Paths.get(currentPath, sourceFile.getName()).toString();
        if (sourceFile.isDirectory()) {
          // For directories, create a new directory and copy its contents
          File targetFile = new File(newFilePath);
          int copyCount = 1;
          while (targetFile.exists()) {
            // If the directory already exists, generate a new name
            fileName = fileName + "-copy-" + copyCount;
            newFilePath = Paths.get(currentPath, fileName).toString();
            targetFile = new File(newFilePath);
            copyCount++;
          }
          copyDirectory(sourceFile.toPath(), Paths.get(newFilePath));
        } else {
          // Check if the file already exists in the target directory
          File targetFile = new File(newFilePath);
          if (targetFile.exists()) { // SI EXISTE AJOUTER -COPY-
            String fileExtension = sourceFile.getName()
                    .substring(sourceFile.getName().lastIndexOf("."));
            String newFileName = fileName + "-copy" + fileExtension;
            newFilePath = Paths.get(currentPath, newFileName).toString();
          }
          try (InputStream inStream = new FileInputStream(sourceFile);
                OutputStream outStream = new FileOutputStream(newFilePath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
              outStream.write(buffer, 0, length);
            }
          } catch (IOException e) {
            output = "Error pasting file: " + e.getMessage();
            logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
            return;
          }
        }
        output = "Element pasted successfully.";
        System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
        int indexOfSpace = annot.indexOf(' '); // Find the index of the first space
        String annotationText = (indexOfSpace != -1) ? annot.substring(indexOfSpace + 1) : annot;
        annotationManager.annotateEr(consoleManager.lastNer, annotationText);
        // If the operation is "cut", call the cut method
        if ("cut".equals(operation)) {
          removeElement((Integer) consoleManager.copiedFile[0]); // NER DU FICHIER COUPER EN CUT
        }
      } else {
        output = "The copied element does not exist.";
        System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
      }
    } else {
      output = "Copied file or directory not found.";
      System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Copie le contenu d'un répertoire source vers un répertoire de destination.
   *
   * @param source      Le chemin du répertoire source.
   * @param destination Le chemin du répertoire de destination.*/
  private void copyDirectory(Path source, Path destination) {
    try {
      Files.walkFileTree(source, new SimpleFileVisitor<>() {
        @Override
          public FileVisitResult preVisitDirectory(Path dir,
                                                   BasicFileAttributes attrs) throws IOException {
            Path targetDir = destination.resolve(source.relativize(dir));
            Files.copy(dir, targetDir, StandardCopyOption.COPY_ATTRIBUTES);
            return FileVisitResult.CONTINUE;
        }

        @Override
          public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attrs) throws IOException {
            Files.copy(file, destination.resolve(source.relativize(file)),
                    StandardCopyOption.COPY_ATTRIBUTES);
            return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
      output = "Error copying directory: " + e.getMessage();
      logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }

  /*** Supprime le fichier ou le répertoire spécifié par son numéro NER.
   *
   * @param ner Le numéro NER du fichier ou répertoire à supprimer.*/
  public void removeElement(int ner) {
    String path = (String) consoleManager.copiedFile[1];
    if (path != null) {
      File fileOrDirectory = new File(path);
      if (fileOrDirectory.exists()) {
        try {
          if (fileOrDirectory.isFile()) {
            Files.deleteIfExists(Paths.get(path));
            output = "File deletion successful.";
            System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
          } else if (fileOrDirectory.isDirectory()) {
            FileUtils.deleteDirectory(fileOrDirectory);
            output = "Directory deletion successful.";
            System.out.println(ConsoleColors.GREEN + output + ConsoleColors.RESET);
          }
        } catch (IOException e) {
          output = "Error deleting: " + e.getMessage();
          logger.error(ConsoleColors.RED + output + ConsoleColors.RESET);
        }
      } else {
        output = "File or directory not found for NER" + ner;
        System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
      }
    } else {
      output = "File or directory not found for NER " + ner;
      System.out.println(ConsoleColors.RED + output + ConsoleColors.RESET);
    }
  }
}
