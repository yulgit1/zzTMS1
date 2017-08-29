package main.java;

import main.java.processes.ProcessFile;
import main.java.processes.ExifFile;

import java.io.File;

public class ImageUtilities {

    public String test(String str) {
        return "this is a test with string " + str;
    }

    public void imagemagick_convert(String image_path) {

    }

    public void iterate_directory(String folderStr, ProcessFile pf) throws Exception {
        File folder = new File(folderStr);
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    //System.out.println("File found: " + listOfFiles[i].getName());
                    pf.process(listOfFiles[i]);
                    //placeholder for recursing intodirectory
                //} else if (listOfFiles[i].isDirectory()) {
                //    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
        } else {
            throw new Exception("Directory passed to iterate_directory must be a directory:" + folder.toString());
        }
    }

    public void process_directory(String folderStr) throws Exception {
        File folder = new File(folderStr);
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    new ExifFile().process(listOfFiles[i]);
                }
            }
        } else {
            throw new Exception("Directory passed to iterate_directory must be a directory:" + folder.toString());
        }
    }
}
