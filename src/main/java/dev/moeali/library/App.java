package dev.moeali.library;

import dev.moeali.library.cli.Commands;
import picocli.CommandLine;

public class App {
    public static void main(String[] args) {
        int exit = new CommandLine(new Commands()).execute(args);
        System.exit(exit);
    }
}