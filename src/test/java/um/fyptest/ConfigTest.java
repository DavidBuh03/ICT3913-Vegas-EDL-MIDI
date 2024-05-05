package um.fyptest;


import um.fyp.Config.EDLConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ConfigTest {
    public static void main (String[] args) {

        try {

        edlToFile();



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void edlToFile() throws IOException {
        File file = new File("OutputTests\\EDL\\test3.txt");
        FileWriter fw = new FileWriter(file);

        EDLConfig test = edlTest(true);

        String rowtestV = EDLConfig.toString(test);

        //test = edlTest(false);
        //String rowtestA = Config.toString(test);

        //String row = "";



        //fw.write(EDL.header());
        fw.write(rowtestV);
        //fw.write(rowtestA);
        fw.close();

        Scanner read = new Scanner(file);
        String rowRead = read.nextLine();
        EDLConfig rowTest = EDLConfig.lineToEDL(rowRead);

        System.out.println(test.equals(rowTest));
        read.close();

    }

    public static EDLConfig edlTest(boolean mediatype) {
        EDLConfig edltest = new EDLConfig();
        edltest.track = 1;
        edltest.playRate = 1;
        edltest.stretchMethod = 0;
        edltest.includeVideo = mediatype;
        edltest.fileName =  "D:\\users ssd\\Videos\\new vegas exports\\vodacom lordlite offset.mp4";
        edltest.streamStart = 0;
        edltest.fadeTimeIn = 0;
        edltest.fadeTimeOut = 0;
        edltest.curveIn = 4;
        edltest.curveOut = 4;



        return edltest;
    }

}
