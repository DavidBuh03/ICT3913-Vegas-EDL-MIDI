package um.fyp.MIDIObjects;


import javax.sound.midi.*;
import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            Sequence sequence = MidiSystem.getSequence(new File("D:\\users ssd\\Music\\sweep test.mid"));
            Track track[] = sequence.getTracks();
            MidiEvent event = track[0].get(0);

            System.out.println("loaded midi");
            System.out.println(sequence.getResolution());
            System.out.println(sequence.getTickLength());
            System.out.println(sequence.getMicrosecondLength());


            //Sequence output = new Sequence();

            System.out.println("loaded midi");





        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}