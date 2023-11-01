import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        String[] commands = input.split(" ");
        if (commands.length > 0) {
            commandName = commands[0];
            args = Arrays.copyOfRange(commands, 1, commands.length);
        } else {
            System.out.println("Invalid command!");
            return false;
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}


class Terminal {
    Parser parser;

    // Store the current directory as a string

    public Terminal() {
        parser = new Parser();
    }

    public String pwd() {
        String currentDirectory = System.getProperty("user.dir");
        return currentDirectory;
    }

    public void cd(String[] args) {
        if (args.length == 0) {
            // Case 1: Change to the home directory.
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (args[0].equals("..")) {
            // Case 2: Change to the previous directory.
            String currentDir = System.getProperty("user.dir");
            String parentDir = new File(currentDir).getParent();
            if (parentDir != null) {
                System.setProperty("user.dir", parentDir);
            } else {
                System.out.println("Already at the root directory.");
            }
        } else {
            // Case 3: Change to the specified directory.
            try {
                File directory = new File(args[0]);

                if (directory.isAbsolute()) {
                    if (directory.exists() && directory.isDirectory()) {
                        System.setProperty("user.dir", directory.getCanonicalPath());
                    } else {
                        System.out.println("Invalid directory: " + args[0]);
                    }
                } else {
                    String currentDir = System.getProperty("user.dir");
                    File newDirectory = new File(currentDir, args[0]);

                    if (newDirectory.exists() && newDirectory.isDirectory()) {
                        System.setProperty("user.dir", newDirectory.getCanonicalPath());
                    } else {
                        System.out.println("Invalid directory: " + args[0]);
                    }
                }
            } catch (Exception ioe) {
                System.out.println("Error changing directory: " + ioe.getMessage());
            }
        }
    }

    public void rmdir(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: Command not found or invalid parameters are entered!");
            return;
        }
        if(args[0].equals("*")) {
            File currentDirectory = new File(pwd());
            File[] files = currentDirectory.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory() && file.list().length == 0) {
                    if (!file.delete()) {
                        System.out.println("Failed to delete the directory.");
                    }
                }
            }
        }
        else{

        String dirName = args[0];
        File dir = new File(dirName);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory.");
            return;
        }

        String[] subDirectories = dir.list();

        if (subDirectories != null && subDirectories.length == 0) {
            if (!dir.delete()) {
                System.out.println("Failed to delete the directory.");
            }
        } else {
            System.out.println("Directory is not empty. Cannot remove it.");
        }
    }}

    public void touch(String[] args) {
        if (args.length == 1) {
            String filePath = args[0];
            File file = new File(filePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
        } else {
            System.out.println("Error: Command not found or invalid parameters are entered!");
        }
    }
    public void chooseCommandAction() {
        System.out.print(">");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        parser.parse(input);
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        switch (commandName) {

            case "cp":
                if(args.length==3 && args[0].equals("-r")){
                    cp_r(args);}
                else if(args.length==2)
                {
                cp(args);}
                else {
                    System.out.println("Error: Command not found or invalid parameters are entered!");
                }
                break;
            case "pwd":
                if(args.length != 0) {
                    System.out.println("Error: Command not found or invalid parameters are entered!");
                    break;
                }
                System.out.println(pwd());
                break;
            case "ls":
                if (args.length == 1 && args[0].equals("-r")) {
                    ls_r();
                } else if(args.length == 0) {
                    ls();
                }
                else {
                    System.out.println("Error: Command not found or invalid parameters are entered!");
                }
                break;
            case "rm":
                rm(args);
                break;
            case "cat":
                cat(args);
                break;
            case "echo":
                echo(args);
                break;
            case "mkdir":
                mkdir(args);
                break;
                case "rmdir":
                rmdir(args);
                break;
            case "wc":
                wc(args);
                break;
            case "cd":
                cd(args);
                break;
            case "touch":
                touch(args);
                break;

            case "exit":
                System.exit(0);
                break;

            default:
                System.out.println("Invalid command!");
                break;
        }
    }

    public void ls() {
        File currentDirectory = new File(pwd());
        File[] files = currentDirectory.listFiles();
        for (File file : files) {
            System.out.print(file.getName()+ "  ");
        }
        System.out.println();
    }
    public void ls_r(){
    // print the files in the current directory in reverse order
        File currentDirectory = new File(pwd());
        File[] files = currentDirectory.listFiles();
        assert files != null;
        Collections.reverse(Arrays.asList(files));
        for (File file : files) {
            System.out.print(file.getName()+ "  ");
        }
        System.out.println();

    }
    public void wc(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Command not found or invalid parameters are entered!");
            return;
        }
        for (String arg : args) {
            try {
                File file = new File(arg);
                Scanner scanner = new Scanner(file);
                int lines = 0;
                int words = 0;
                int characters = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    lines++;
                    characters += line.length();
                    String[] wordsArray = line.split(" ");
                    words += wordsArray.length;
                }
                System.out.println(lines + " " + words + " " + characters + " " + arg);
                scanner.close();
            } catch ( FileNotFoundException e) {
                System.out.println(System.getProperty("user.dir")+ ":  No such file!");
            }

        }

    }

    public void rm(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: Command not found or invalid parameters are entered!");
            return;
        }

        String currentDir = System.getProperty("user.dir");
        File file = new File(currentDir, args[0]); // Create a file object in the current directory

        if (file.exists()) {
            if(file.isDirectory()) {
                System.out.println("Error: The file '" + args[0] + "' is a directory.");
                return;
            }
            file.delete();
        } else {
            System.out.println("Error: The file '" + args[0] + "' does not exist in the current directory.");
        }
    }



    public void cat(String[] args) {
        if(args.length == 1) {
            try {
                File file = new File(args[0]);
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(line);
                }

                scanner.close();
            } catch ( FileNotFoundException e) {
                System.out.println(System.getProperty("user.dir")+ ":  No such file!");
            }

        }
        else if(args.length == 2) {
     // concatenates the content of the 2 files and prints it.
            try {
                File file1 = new File(args[0]);
                File file2 = new File(args[1]);
                Scanner scanner1 = new Scanner(file1);
                Scanner scanner2 = new Scanner(file2);

                while (scanner1.hasNextLine()) {
                    String line = scanner1.nextLine();
                    System.out.println(line);
                }
                while (scanner2.hasNextLine()) {
                    String line = scanner2.nextLine();
                    System.out.println(line);
                }

                scanner1.close();
                scanner2.close();
            } catch ( FileNotFoundException e) {
                System.out.println(System.getProperty("user.dir")+ ":  No such files!");
            }
        }
        else {
            System.out.println("Error: Command not found or invalid parameters are entered!");
        }
    }
    public void echo(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i] + " ");
        }
        System.out.println();
    }
    public void mkdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Command not found or invalid parameters are entered!");
            return;
        }
        for (String arg : args) {
            File file = new File(arg);
            try {
                if (file.isAbsolute()) {
                    if (!file.exists()) {
                        if (!file.mkdir()) {
                            System.out.println("Failed to create directory: " + file.getAbsolutePath());
                        }
                    } else {
                        System.out.println(file.getAbsolutePath() + ": File already exists!");
                    }
                } else {
                    String currentDir = System.getProperty("user.dir");
                    String fullPath = currentDir + File.separator + arg;
                    File relativeDirectory = new File(fullPath);
                    if (!relativeDirectory.exists()) {
                        if (!relativeDirectory.mkdir()) {
                            System.out.println("Failed to create directory: " + relativeDirectory.getAbsolutePath());
                        }
                    } else {
                        System.out.println(relativeDirectory.getAbsolutePath() + ": File already exists!");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid command or bad parameters for " + arg + " - " + e.getMessage());
            }
        }
    }

    public void cp(String [] args){
        if (args.length != 2) {
            System.out.println("Error: Command not found or invalid parameters are entered!");
            return;
        }

        File firstFile = new File(args[0]);
        File secondFile = new File(args[1]);

        if (!firstFile.exists() || !firstFile.isFile()) {
            System.out.println("Error: target '" + args[0] + "' does not exist or is not a file.");
            return;
        }

        try (FileReader reader = new FileReader(firstFile);
             FileWriter writer = new FileWriter(secondFile)) {

            int charRead;
            while ((charRead = reader.read()) != -1) {
                writer.write(charRead);
            }

        } catch (IOException e) {
            System.out.println("Error: Unable to copy file - " + e.getMessage());
        }

    }
    public void cp_r(String []args)
    {
        if (args.length != 3) {
            System.out.println("Error: Command not found or invalid parameters are entered!");
            return;
        }

        File firstFile = new File(args[1]);
        File secondFile = new File(args[2]);

        if (!firstFile.exists() || !firstFile.isDirectory()) {
            System.out.println("Error: target '" + args[1] + "' does not exist or is not a directory.");
        }
        if (secondFile.exists() && !secondFile.isDirectory()) {
            System.out.println("Error: Destination '" + args[1] + "' is not a directory.");
            return;
        }
        if (!secondFile.exists()) {
            secondFile.mkdirs();
    }
        copyDirectory(firstFile, secondFile);
    }
    private void copyDirectory(File sourceDir, File destinationDir) {
        File[] sourceFiles = sourceDir.listFiles();
        if (sourceFiles != null) {
            for (File sourceFile : sourceFiles) {
                File destinationFile = new File(destinationDir, sourceFile.getName());

                if (sourceFile.isDirectory()) {
                    copyDirectory(sourceFile, destinationFile);
                } else {
                    try {
                        Files.copy(sourceFile.toPath(), destinationFile.toPath());
                    } catch (IOException e) {
                        System.out.println("Error: Unable to copy file - " + e.getMessage());
                    }
                }
            }
        }
    }

        public static void main(String[] args) {
        Terminal terminal = new Terminal();
        while (true) {
            terminal.chooseCommandAction();
        }


    }
}
