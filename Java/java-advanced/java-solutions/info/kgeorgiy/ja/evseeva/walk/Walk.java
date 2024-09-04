package info.kgeorgiy.ja.evseeva.walk;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedReader;

public class Walk {
    private static final byte[] buffer = new byte[2048];

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("2 arguments are required!");
            return;
        } else if (args.length != 2) {
            System.err.println("2 arguments are required!");
            return;
        } else if (args[0] == null) {
            System.err.println("The input file is required to be entered!");
            return;
        } else if (args[1] == null) {
            System.err.println("The output file is required to be entered!");
            return;
        }
        Path inputFile;
        Path outputFile;
        try {
             inputFile = Path.of(args[0]);
             outputFile = Path.of(args[1]);
        } catch (InvalidPathException e) {
            System.err.println("Incorrect File: " + e.getMessage());
            return;
        }
        if (outputFile.getParent() != null) {
            try {
                Files.createDirectories(outputFile.getParent());
            } catch (IOException e) {
                System.err.println("Error of create directory " + e.getMessage());
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
            try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
                String name = reader.readLine();
                while (name != null) {
                    writer.write(String.format("%08x", readFile(name)) + " " + name);
                    writer.newLine();
                    name = reader.readLine();
                }
            } catch (FileNotFoundException | SecurityException | NoSuchFileException e){
                System.err.println("Output file access error: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Output file opening error " + outputFile + " " + e.getMessage());
            }
        } catch (FileNotFoundException | SecurityException | NoSuchFileException e){
            System.err.println("Input file access error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Input file opening error " + inputFile + " " + e.getMessage());
        }
    }
    static int readFile(final String name) {
        int result = 0;
        try (InputStream inputReader = Files.newInputStream(Path.of(name))) {
            int bytesRead;
            while ((bytesRead = inputReader.read(buffer)) >= 0) {
                result = hash(result, bytesRead);
            }
            result += (result << 3);
            result ^= (result >>> 11);
            result += (result << 15);
        } catch (InvalidPathException | IOException e) {
            return 0;
        }
        return result;
    }

    static int hash(int hash, int len) {
        for (int i = 0; i < len; i++) {
            hash += (buffer[i] & 0xff);
            hash += (hash << 10);
            hash ^= (hash >>> 6);
        }
        return hash;
    }

}