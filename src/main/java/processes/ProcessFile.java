package main.java.processes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

//https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#approach7

public interface ProcessFile {
    public HashMap<String,String> process(File f) throws IOException;
}

