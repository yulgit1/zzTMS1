package main.java.processes;

import java.io.File;

public class PrintFile implements ProcessFile {
    public void process(File f) {
        System.out.println("File found: " + f.getName());
    }
}
