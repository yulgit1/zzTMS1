package main.java.processes;

import java.io.File;
import java.util.HashMap;

public class AltPrintFile implements ProcessFile {
    public HashMap<String,String> process(File f) {
        System.out.println("AltFile found: " + f.getName());
        return null;
    }
}
