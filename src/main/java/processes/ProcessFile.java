package main.java.processes;

import java.io.File;
import java.io.IOException;

//https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#approach7

public interface ProcessFile {
    public void process(File f) throws IOException;
}

