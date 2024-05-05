package um.fyptest.Obsolete;


import java.io.*;
import java.util.Scanner;

public class EDLTest {
    public static void main (String[] args) {

        try {

        edlToFile();



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void edlToFile() throws IOException {
        File file = new File("OutputTests\\EDL\\test2.txt");
        FileWriter fw = new FileWriter(file);

        EDL test = edlTest(true);

        String rowtestV = EDL.toString(test);

        test = edlTest(false);
        String rowtestA = EDL.toString(test);

        //String row = "";



        //fw.write(EDL.header());
        fw.write(rowtestV);
        //fw.write(rowtestA);
        fw.close();

        Scanner read = new Scanner(file);
        String rowRead = read.nextLine();
        EDL rowTest = EDL.lineToEDL(rowRead);
        read.close();

    }

    public static EDL edlTest(boolean mediatype) {
        EDL edltest = new EDL();
        edltest.id = 1;
        edltest.track = 1;
        edltest.startTime = 0;
        edltest.length = 10643.9666;
        edltest.playRate = 1;
        edltest.locked = false;
        edltest.normalized = false;
        edltest.stretchMethod = 0;
        edltest.looped = true;
        edltest.onRuler = false;
        edltest.mediaType = mediatype;
        edltest.fileName =  "D:\\users ssd\\Videos\\new vegas exports\\vodacom lordlite offset.mp4";
        edltest.stream = 0;
        edltest.streamStart = 0;
        edltest.streamLength = 10643.9666;
        edltest.fadeTimeIn = 0;
        edltest.fadeTimeOut = 0;
        edltest.sustainGain = 1;
        edltest.curveIn = 4;
        edltest.gainIn = 0;
        edltest.curveOut = 4;
        edltest.gainOut = 0;
        edltest.layer = 0;
        edltest.color = -1;
        edltest.curveInR = 4;
        edltest.curveOutR = 4;
        edltest.playPitch = 0;
        edltest.lockPitch = false;
        edltest.firstChannel = 0;
        edltest.channels = 0;



        return edltest;
    }

}
