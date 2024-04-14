package um.fyp.MIDIObjects;

public class Note {
    public int note;
    public int velocity;
    public long startTick;

    public Note(int note, int velocity, long startTick) {
        this.note = note;
        this.velocity = velocity;
        this.startTick = startTick;
    }
}
