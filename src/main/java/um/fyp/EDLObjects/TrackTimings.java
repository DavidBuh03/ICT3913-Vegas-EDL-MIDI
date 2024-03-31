package um.fyp.EDLObjects;

import um.fyp.Config.EDLConfig;
import um.fyp.EdlToMidi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.List;

public class TrackTimings {
    public String type;
    public List<Double> timings;
    public List<Double> playRate;
    public List<Double> fadeTimeIn;
    public List<Double> fadeTimeOut;
    public List<String> filePaths;
    public int curveIn;
    public int curveOut;

    public TrackTimings(String type, List<Double> timings, List<Double> playRate, List<Double> fadeTimeIn, List<Double> fadeTimeOut, List<String> filePaths, int curveIn, int curveOut) {
        this.type = type;
        this.timings = timings;
        this.playRate = playRate;
        this.fadeTimeIn = fadeTimeIn;
        this.fadeTimeOut = fadeTimeOut;
        this.filePaths = filePaths;
        this.curveIn = curveIn;
        this.curveOut = curveOut;
    }

    public static List<Integer> getPairs(List<TrackTimings> timings) {
        List<Integer> pairs = new ArrayList<>();
        for (int i = 0; i < timings.size(); i++) {
            if (!pairs.contains(i)) {
                for (int j = i+1; j < timings.size(); j++) {
                    if (!pairs.contains(j)) {
                        if (timings.get(i).filePaths.get(0).equals(timings.get(j).filePaths.get(0))) {
                            if (timings.get(i).type.equals("VIDEO")) {
                                pairs.add(i);
                                pairs.add(j);
                            } else {
                                pairs.add(j);
                                pairs.add(i);
                            }

                            break;
                        }
                    }
                }
            }

        }
        return pairs;
    }



    public TrackTimings() {

    }
}
