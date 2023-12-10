package fr.uvsq.cprog;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Define the root directory path (replace it with your desired path)
        String rootPath = ".\\Root";
        // Create an instance of the console file manager with the root directory path
        ConsoleManager fileManager = new ConsoleManager(rootPath);
        // Main logic of the application
        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            // Display the current directory and prompt the user for a command
            fileManager.displayCurrentDirectory();
            System.out.print("Enter a command (or 'exit' to quit): ");
            command = scanner.nextLine();
            // Process the user command
            if (!command.equalsIgnoreCase("exit")) {
                fileManager.processCommand(command);
            }
        } while (!command.equalsIgnoreCase("exit"));
        // Close the scanner
        scanner.close();
    }
}
