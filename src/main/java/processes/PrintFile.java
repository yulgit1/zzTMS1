package main.java.processes;

import java.io.File;
import java.util.HashMap;

public class PrintFile implements ProcessFile {
    public HashMap<String,String> process(File f) {
        System.out.println("File found: " + f.getName());
        return null;
    }
}
