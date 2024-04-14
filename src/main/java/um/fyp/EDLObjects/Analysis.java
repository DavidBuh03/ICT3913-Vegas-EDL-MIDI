package um.fyp.EDLObjects;

import um.fyp.Config.EDLConfig;
import um.fyp.GUIHelper.Window;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.naturalOrder;

public class Analysis {
    public static void timings(List<EDLConfig> timings) {
        DecimalFormat sevenDigits = new DecimalFormat("0.0000000");
        JTextArea timingArea = new JTextArea();
        Window timingWindow = new Window(300, 500, "Timings", true, false) {
            @Override
            public void menuBarItems(JMenuBar bar) {

            }
            @Override
            public void uiElements() {
                add(timingArea, false);
            }
        };
        if (timings == null || timings.isEmpty()) {
            timingArea.setText("Config is null or empty. Please load a file.");
        }
        else {
            List<Double> timingValues = new ArrayList<>();
            for (EDLConfig conf : timings) {
                if (!timingValues.contains((conf.streamStart*conf.playRate))) {
                    timingValues.add((conf.streamStart*conf.playRate));
                }
            }
            Collections.sort(timingValues);
            String timingMessage = "";
            for (Double timing : timingValues) {
                timingMessage+=sevenDigits.format(timing) + "\n";
            }
            timingArea.setText(timingMessage);

        }
    }

    public static void timingAnalysis(List<EDLConfig> timings) {
        if (timings == null || timings.isEmpty()) {
            //null
        }
        else {
            List<Double> timingValues = new ArrayList<>();
            for (EDLConfig conf : timings) {
                if (!timingValues.contains((conf.streamStart * conf.playRate))) {
                    timingValues.add((conf.streamStart * conf.playRate));
                }
            }

            double range = range(timingValues);
            double mean = mean(timingValues);
            double median = median(timingValues);
            double stdDev = standardDeviation(timingValues);
            double skewness = skewness(timingValues);
            double lowest = timingValues.getFirst();
            double highest = timingValues.getLast();


        }
    }

    public static double mean(List<Double> list) {
        double sum = 0;
        for (Double num : list) {
            sum+=num;
        }
        return sum/list.size();
    }

    public static double standardDeviation(List<Double> list) {
        double sums = 0;
        double mean = mean(list);
        double varianceNumerator = 0;
        for (Double num : list) {
            varianceNumerator = Math.pow((num-mean), 2);
        }
        double variance = varianceNumerator/list.size();
        return Math.sqrt(variance);
    }

    public static double median(List<Double> list) {
        int size = list.size();
        int halfSize = (int) Math.ceil((double) size /2);
        if (size%2 == 0) {
            return ((list.get(halfSize) + list.get(halfSize-1)) / 2);
        }
        else {
            return (list.get(halfSize-1));
        }
    }
    public static double skewness(List<Double> list) {
        double stdDev = standardDeviation(list);
        double mean = mean(list);
        double skewSum = 0;
        int size = list.size();
        for (Double num : list) {
            skewSum = Math.pow((num-mean)/stdDev, 3);
        }

        return (((double) size /((size-1)*(size-2))) * skewSum);


    }
    public static double range(List<Double> list) {
        return (list.getLast()-list.getFirst());
    }
}


