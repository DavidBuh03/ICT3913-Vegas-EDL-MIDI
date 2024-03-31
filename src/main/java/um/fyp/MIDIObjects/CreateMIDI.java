package um.fyp.MIDIObjects;
import javax.sound.midi.*;
import java.io.File;
import java.util.SimpleTimeZone;

import static java.lang.Math.log;
import static java.lang.Math.negateExact;
import static javax.sound.midi.ShortMessage.*;

public class CreateMIDI {

    //microseconds in a minute
    public static final int MINUTE = 60000000;

    //defining MIDI control bytes that are not specified in javax.sound.midi
    public static final int SET_TEMPO = 0x51;

    public static final int SET_TIME_SIGNATURE = 0x58;
    public static final int END_OF_TRACK = 0x2F;
    public static final int OMNI_ON = 0x7D;
    public static final int POLY_ON = 0x7F;
    public static final int SET_TRACK_NAME = 0x03;

    //public static boolean enabledOmniAndPoly = false;



    //creates the basic events in a MIDI file that initialise the file
    public static void initialiseMIDI(Sequence sequence, int bpm, int numerator, int denominator, boolean enableGM, boolean addEnd) {
        try {
            //create track in sequence
            Track t = sequence.createTrack();

            //--Set MIDI Time Signature
            MetaMessage meta = new MetaMessage();
            byte[] timeSignature = {(byte)numerator, (byte)(log(denominator)/log(2)), (byte)sequence.getResolution(), 8};
            meta.setMessage(SET_TIME_SIGNATURE, timeSignature, 4);
            MidiEvent event = new MidiEvent(meta, 0);
            t.add(event);

            endTrack(t, 0);

            t = sequence.createTrack();

            if ((enableGM)) {
                //-- Enable General MIDI SysEx event --

                //define message byte array:
                byte[] gmMessage = {(byte) 0xF0, //SysEx control byte
                        0x7E, //Non-Realtime SysEx
                        0x7F, //Set SysEx Channel (7F is disregard)
                        0x09, //General MIDI (Enable or disable)
                        0x01, //01 to enable, 00 to disable
                        (byte) 0xF7 //End of SysEx message
                };
                //define sysex message
                SysexMessage sysex = new SysexMessage();
                //set message to the byte array
                sysex.setMessage(gmMessage, 6);
                //create event out of this message at tick 0
                event = new MidiEvent(sysex, (long) 0);
                //add this event to the track
                t.add(event);
            }
            //-- Set MIDI Tempo --

            //reset metamessage
            meta = new MetaMessage();
            //calculate microseconds per quarter note
            int uSPQN = MINUTE / bpm;
            //convert this value into a byte array of 3 bytes, in big endian
            byte[] tempo = {
                    (byte) Byte.toUnsignedInt((byte) (uSPQN >> 16)), //high byte
                    (byte) Byte.toUnsignedInt((byte) (uSPQN >> 8)),  //mid byte
                    (byte) Byte.toUnsignedInt((byte) (uSPQN))        //low byte
            };
            //set metamessage values
            meta.setMessage(SET_TEMPO, tempo, 3);
            //create midi event out of this message at tick 0
            event = new MidiEvent(meta, 0);
            //add this event to the track
            t.add(event);


            //-- Define end of track (via method call) --
            if (addEnd) endTrack(t, 10);



        } catch (Exception e) {
            handleException(e);
        }
    }

    public static Track newTrack(Sequence s, String name, int channel, int instrument, boolean enabledOmniAndPoly) {
        try {
            Track t = s.createTrack();
            //define metamessage
            MetaMessage meta = new MetaMessage();
            //set metamessage values
            meta.setMessage(SET_TRACK_NAME, name.getBytes(), name.length());
            //create midi event out of this message at tick 0
            MidiEvent event = new MidiEvent(meta, 0);
            //add this event to the track
            t.add(event);

            if (!enabledOmniAndPoly) {
                enableOmniAndPoly(t);
                enabledOmniAndPoly = true;
            }

            //-- Set Channel instrument to specified instrument --

            //clear shortmessage
            ShortMessage sm = new ShortMessage();
            //set the message:
            sm.setMessage(0xC0&0xFF | channel&0xFF, //high nibble: C - Program Change. low nibble: channel number
                                instrument, //Set the instrument as specified
                                0x00);//unused
            //create midi event out of this message at tick 0
            event = new MidiEvent(sm, 0);
            //add this event to the track
            t.add(event);

            return t;
        } catch (Exception e) {
            handleException(e);
        }
        return null;


    }
    //Define end of track
    public static void endTrack(Track t, long tick) {
        try {
            //define metamessage
            MetaMessage meta = new MetaMessage();
            //empty byte array, since this message has no bytes after its control byte
            byte[] empty = {};
            //set metamessage values
            meta.setMessage(END_OF_TRACK, empty, 0);
            //create midi event out of this message at tick 10
            MidiEvent event = new MidiEvent(meta, tick);
            //add this event to the track
            t.add(event);
        } catch (Exception e) {
            handleException(e);
        }

    }
    public static void enableOmniAndPoly(Track t) {
        try {
            //-- Enable omni mode --

            //define a shortmessage
            ShortMessage sm = new ShortMessage();
            //set the message to omni on, last byte unused
            sm.setMessage(CONTROL_CHANGE, OMNI_ON, 0x00);
            //create midi event out of this message at tick 0
            MidiEvent event = new MidiEvent(sm, 0);
            //add this event to the track
            t.add(event);

            //-- Enable poly mode --

            //clear shortmessage
            sm = new ShortMessage();
            //set the message to poly on, last byte unused
            sm.setMessage(CONTROL_CHANGE, POLY_ON, 0x00);
            //create midi event out of this message at tick 0
            event = new MidiEvent(sm, 0);
            //add this event to the track
            t.add(event);
        } catch (Exception e) {
            handleException(e);
        }
    }

    //gets the length of a single tick in microseconds
    public static int getTickLength(Sequence sequence) {
        //get midi resolution in ticks per quarter note
        int resolution = sequence.getResolution();
        //get all tracks
        Track[] tracks = sequence.getTracks();
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                //if a message from the first track is a metamessage
                if (track.get(i).getMessage() instanceof MetaMessage) {
                    //store that metamessage
                    MetaMessage meta = (MetaMessage) (track.get(i).getMessage());
                    //if that metamessage is of type set tempo
                    if (meta.getType() == SET_TEMPO) {
                        //get the message's byte array
                        byte[] tempo = meta.getMessage();
                        //get the microseconds per quarter note by interpreting the last 3 bytes as a 24-bit big-endian number
                        //then divide by the resolution in PPQN to get the microseconds per tick
                        int tickLength = ((tempo[3] & 0xFF) << 16 //high byte, bitshift left by 16 bits
                                | (tempo[4] & 0xFF) << 8  //logical or with mid byte to add it, then bitshift left by 8 bits
                                | (tempo[5] & 0xFF))      //logical or with low byte to add it
                                / resolution;             //divide the final value by the PPQN of the midi file
                        return tickLength;
                    }
                }


            }
        }

        return 0;
    }


    public static long addNote(Track t, int tickLength, int note, double startMillis, double lengthMillis, double volume) {
        try {
            long startTick = (long)(startMillis*1000)/tickLength;
            long OffsetTick = (long)(lengthMillis*1000)/tickLength;
            long endTick = startTick + OffsetTick;
            byte velocity = (byte)(volume*1.27f);

            ShortMessage noteEvent = new ShortMessage();
            noteEvent.setMessage(NOTE_ON, note, velocity);
            MidiEvent event = new MidiEvent(noteEvent, startTick);
            t.add(event);

            noteEvent = new ShortMessage();
            noteEvent.setMessage(NOTE_OFF, note, velocity);
            event = new MidiEvent(noteEvent, endTick);
            t.add(event);

            return endTick;

        } catch (Exception e) {
            handleException(e);
        }
        return 0;

    }

    public static void writeMIDIToFile(Sequence sequence, String path) {
        try {
            File f = new File(path);
            MidiSystem.write(sequence, 1, f);
        } catch (Exception e) {
            handleException(e);
        }

    }


    public static void main (String[] args) {
        try {
            Sequence s = new Sequence(Sequence.PPQ, 24);
            initialiseMIDI(s, 60, 4, 4, true, true);
            newTrack(s, "Track", 0, 0, true);
            int tickLength = getTickLength(s);
            Track[] tracks = s.getTracks();
            long endTick = 0;
            for (int i = 0; i < 4; i++) {
                endTick = addNote(tracks[tracks.length-1], tickLength, 24 + (i*12), 1000*i, 1000, 20 + (20*i));
            }

            endTrack(tracks[0], endTick+1);
            writeMIDIToFile(s, "OutputTests\\MIDI\\TestMidi.mid");


        } catch (Exception e) {
            handleException(e);
        }

    }

    public static void handleException(Exception e) {
        System.out.println("Exception occurred!\n" + e.getMessage());
    }
}
