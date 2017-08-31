package main.java;

import main.java.processes.ProcessFile;
import main.java.processes.ExifFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ImageUtilities {

    //Connection conn;

    public ImageUtilities(Properties prop1) {
        //TmsConnection tmsconn = new TmsConnection(prop1.getProperty("tmshost"),
        //        prop1.getProperty("tmshost"),
        //        prop1.getProperty("tmspw"));
        //conn = tmsconn.getConnection();
    }

    public String test(String str) {
        return "this is a test with string " + str;
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

    public void process_directory(Properties prop1) throws Exception {
        File folder = new File(prop1.getProperty("imagefolder"));
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    if (listOfFiles[i].getName().equals(".DS_Store")) {
                        continue;
                    }
                    HashMap<String, String> fields = new HashMap<String, String>();
                    HashMap<String, String> exifs = new ExifFile().process(listOfFiles[i]);
                    HashMap<String, String> md5 = checksum(listOfFiles[i]); //note: could go in supplied
                    convert_to_thumbs(listOfFiles[i], prop1);
                    move_orig_file(listOfFiles[i], prop1);
                    HashMap<String, String> sup = supplied(listOfFiles[i], prop1);
                    fields.putAll(exifs);
                    fields.putAll(md5);
                    fields.putAll(sup);
                    printHashMap(fields);
                    renameFiles(listOfFiles[i],
                            prop1,
                            fields.get("coll"),
                            fields.get("obj"),
                            fields.get("objID"),
                            fields.get("accession"),
                            fields.get("imagetype"));


                }
            }
        } else {
            throw new Exception("Directory passed to iterate_directory must be a directory:" + folder.toString());
        }
    }

    public void printHashMap(HashMap<String,String> hm) {
        //putAll()
        for (Map.Entry entry : hm.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void convert_to_thumbs(File f,Properties prop1) throws Exception {
        //convert test-for-tms.jpg +matte -gravity Center -thumbnail 192x192 -density 72 -background white -extent 192x192 test-for-tms.jpg thumbnail.jpg
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"convert",
                "+matte",
                "-gravity", "Center",
                "-thumbnail", "192x192",
                "-density", "72",
                "-background", "white",
                "-extent", "192x192",
                f.getAbsolutePath(),
                prop1.getProperty("thumbnailfolder")+ "/thumb_" + f.getName()
        };
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);  //uncomment to see all fields
        }

        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

    }

    public HashMap<String,String> checksum(File f) throws IOException {
        HashMap<String,String> checksum = new HashMap<String,String>();
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"md5",
                f.getAbsolutePath()
        };
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            checksum.put("MD5",s.split(" = ")[1].trim());
            //System.out.println(s.split(" = ")[1].trim());
        }

        while ((s = stdError.readLine()) != null) {
            System.err.println("checksum error:"+s);
        }
        return checksum;
    }

    public void move_orig_file(File f,Properties prop1) throws IOException {
        Files.copy(Paths.get(f.getAbsolutePath()),
                Paths.get(prop1.getProperty("outfolder") +"/" +f.getName()),
                StandardCopyOption.REPLACE_EXISTING );
    }

    public void renameFile(File f, Properties prop1, String coll, String objectType,String idNum,String accessionNum,String imageType) {
        //obj\004\570\ba-obj-4570-0002-pub.jpg
        String s = f.getName();
        File orig = new File(prop1.getProperty("outfolder")+"/"+f.getName());
        File new_orig = new File(prop1.getProperty("outfolder")+"/"+
                coll+"-"+objectType+"-"+idNum+"-"+accessionNum+"-"+imageType+s.substring(s.indexOf("."),s.length()));
        orig.renameTo(new_orig);
    }

    public void renameThumbs(File f, Properties prop1, String coll, String objectType,String idNum,String accessionNum,String imageType) {
        String s = f.getName();
        File thumb = new File(prop1.getProperty("thumbnailfolder")+"/thumb_"+f.getName());
        File new_thumb = new File(prop1.getProperty("thumbnailfolder")+"/"+
                coll+"-"+objectType+"-"+idNum+"-"+accessionNum+"-"+imageType+s.substring(s.indexOf("."),s.length()));
        thumb.renameTo(new_thumb);
    }

    public void renameFiles(File f, Properties prop1, String coll, String objectType,String idNum,String accessionNum,String imageType) {
        renameFile(f,prop1,coll,objectType,idNum,accessionNum,imageType);
        renameThumbs(f,prop1,coll, objectType,idNum,accessionNum,imageType);
    }

    public HashMap<String,String> supplied(File f,Properties prop1) throws IOException {
        Properties metadata = new Properties();
        FileInputStream metadata_fis = null;
        try {
            metadata_fis =
                    new FileInputStream(prop1.getProperty("metadatafolder")+"/metadata_"+f.getName()+ ".properties");
            metadata.load(metadata_fis);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (metadata_fis != null) {
                try {
                    metadata_fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        HashMap<String,String> p = new HashMap<String,String>();
        p.put("coll",metadata.getProperty("coll"));
        p.put("obj",metadata.getProperty("obj"));
        p.put("imagetype",metadata.getProperty("imagetype"));
        p.put("objID",metadata.getProperty("objID"));
        p.put("accession",metadata.getProperty("accession"));
        p.put("pathID",metadata.getProperty("pathID"));
        p.put("formatID",metadata.getProperty("formatID"));
        p.put("colorDepthID",metadata.getProperty("colorDepthID"));
        p.put("duration",metadata.getProperty("duration"));
        p.put("parentrendid",metadata.getProperty("parentrendid"));
        p.put("iscolor",metadata.getProperty("iscolor"));
        p.put("thumbpathid",metadata.getProperty("thumbpathid"));
        p.put("thumbsize",getThumbsize(f,prop1));
        p.put("rendnum",getRendSortNum(p.get("coll"),p.get("obj"),p.get("objID"),p.get("accession"),p.get("imagetype")));
        p.put("sortnum",getRendSortNum(p.get("coll"),p.get("obj"),p.get("objID"),p.get("accession"),p.get("imagetype")));
        p.put("thumbfilename",getThumbFileName(p.get("coll"),p.get("obj"),p.get("objID"),p.get("accession"),p.get("imagetype")));
        p.put("tableID",tableIDMap.get(p.get("obj")));
        p.put("rank",getRank());
        p.put("primaryDisplay",metadata.getProperty("primaryDisplay"));
        p.put("displayOrder",getRank());
        p.put("displayInReport",metadata.getProperty("displayInReport"));
        return p;
    }

    public String getThumbsize(File f, Properties p) {
        File thumbfile = new File(p.getProperty("thumbnailfolder")+ "/thumb_" + f.getName());
        return Long.toString(thumbfile.length());
    }
    
    public String getRendSortNum(String coll,String obj,String objID,String accession,String imagetype) {
        //ba-obj-4570-0002-pub
        return coll + "-" + obj + "-" + objID + "-" + accession + "-" + imagetype;
    }

    public String getThumbFileName(String coll,String obj,String objID,String accession,String imagetype) {
        //obj\004\570\ba-obj-4570-0002-pub.jpg
        return obj +"/" + id_to_p(objID)+ "/" + coll + "-" + obj + "-" + objID + "-" + accession + "-" + imagetype;
    }

    public static final Map<String,String> tableIDMap = Collections.unmodifiableMap(
            //made up keys for now with exception of obj
            new HashMap<String,String>() {{
                put("con","23"); //Constituents
                put("exh","47"); //Exhibitions
                put("loa","81"); //Loans
                put("cli","97"); //CondLineItems
                put("obj","108"); //Objects
                put("obr","126"); //ObjRights
                put("his","187"); //HistEvents
                put("shi","345"); //Shipments
                put("cre","792"); //ConservationReports
            }});


    public String getRank() {
        //pseudo codeselect max(Rank) +1 from MediaXrefs;
        return "3";
    }

    public String id_to_p(String id) {
        String path = "";
        switch (id.length()) {
            case 1: path = "000/00" + id;
                break;
            case 2: path = "000/0" +id;
                break;
            case 3: path = "000/" + id;
                break;
            case 4: path = "00" + id.substring(0,1) +"/" + id.substring(1,4);
                break;
            case 5: path = "0" + id.substring(0,2) +"/" + id.substring(2,5);
                break;
            case 6: path = id.substring(0,3) +"/" + id.substring(3,6);
                break;
        }
        return path;
    }

    //TODO rank,thumbblob, (sql once got connection)
    //TODO CAMEL


}
