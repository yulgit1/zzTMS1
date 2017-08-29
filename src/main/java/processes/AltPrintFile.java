package main.java.processes;

import java.io.File;

public class AltPrintFile implements ProcessFile {
    public void process(File f) {
        System.out.println("AltFile found: " + f.getName());
    }
}
