package main.java;

//import main.java.processes.PrintFile;
import main.java.processes.AltPrintFile;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class RunProject {

    public static void main(String[] args)
    {

        //ConnectMSSQLServer connServer = new ConnectMSSQLServer();
        //tmsUrl=jdbc:jtds:sqlserver://bac5-dev.yu.yale.edu/zzTMS
        //connServer.dbConnect("jdbc:sqlserver://130.132.119.56;databaseName=zzTMS", "YALE\\ermadmix",
        //        "");
        Properties prop1 = new Properties();
        FileInputStream input1 = null;
        try {
            input1 = new FileInputStream("src/main/resources/global.properties");
            prop1.load(input1);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input1 != null) {
                try {
                    input1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ImageUtilities iu = new ImageUtilities(prop1);
        //System.out.println(iu.test("here"));
        try {
            //iu.iterate_directory(prop1.getProperty("imagefolder"), new ExifFile());
            iu.process_directory(prop1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        TmsConnection tmsconn = new TmsConnection(prop1.getProperty("tmshost"),
                prop1.getProperty("tmshost"),
                prop1.getProperty("tmspw"));
        Connection conn = tmsconn.getConnection();
        tmsconn.closeConnection();
        */
        //System.out.println(prop1.getProperty("tmshost"));
    }
}
