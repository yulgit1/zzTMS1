package main.java.processes;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ExifFile implements ProcessFile {

    public HashMap<String,String> process(File f) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"exiftool",f.getAbsolutePath()};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        HashMap<String,String> exif_fields = new HashMap<String,String>();

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            //System.out.println(s);  //uncomment to see all fields
            if (s.contains("File Size")) {
                exif_fields.put("File Size",s.split(" : ")[1].replaceAll("\\D+","").trim());
            }
            if (s.contains("Image Width")) {
                exif_fields.put("Image Width",s.split(" : ")[1].trim());
            }
            if (s.contains("Image Height")) {
                exif_fields.put("Image Height",s.split(" : ")[1].trim());
            }
        }

        while ((s = stdError.readLine()) != null) {
            throw new IOException("error running exif");
        }
        return exif_fields;
    }
}
