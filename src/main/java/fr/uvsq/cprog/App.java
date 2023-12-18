package fr.uvsq.cprog;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


/**
 *  App class represents the main application entry point.
 */
public class App {
  /**
   * The main method is the entry point for the application.
   *
   * @param args Command-line arguments (not used in this application).
   */
  public static void main(String[] args) {
    // Define the root directory path (replace it with your desired path)
    String rootPath = ".\\Root";
    // Create an instance of the console file manager
    // with the root directory path
    ConsoleManager consoleManager = new ConsoleManager(rootPath);
    try {
      Terminal terminal = TerminalBuilder.builder().system(true).build();
      LineReader reader = LineReaderBuilder.builder()
              .terminal(terminal)
              .completer(new StringsCompleter("help", "mkdir", "visu", ".", "..", "visu", "+",
                      "-", "find", "copy", "cut", "past", "exit"))
              .parser(new DefaultParser())
              .build();

      String prompt = "Enter a command (or 'exit' to quit): ";
      String line;
      while (true) {
        consoleManager.displayCurrentDirectory();
        line = reader.readLine(prompt);
        String command = line.trim();

        if (command.equalsIgnoreCase("exit")) {
          break; // Sortir de la boucle si la commande est "exit"
        }
        consoleManager.processCommand(command);
      }
    } catch (Exception e) {
      System.out.print("");
    }
  }
}
