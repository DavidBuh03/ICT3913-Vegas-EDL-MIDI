package um.fyp.EDLObjects;

import um.fyp.Config.EDLConfig;
import um.fyp.GUIHelper.Window;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.naturalOrder;

public class Analysis {
    public static void timings(List<EDLConfig> timings, boolean usefulness) {
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
                double timing = conf.streamStart*conf.playRate;
                if (usefulness) {
                    timing = usefulness(timing);
                }

                if (!timingValues.contains((timing))) {
                    timingValues.add((timing));
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
        DecimalFormat sevenDigits = new DecimalFormat("0.0000000");
        ArrayList<JLabel> labels = new ArrayList<>();
        ArrayList<JLabel> valueLabels = new ArrayList<>();
        Font textFont = new Font("Arial", Font.PLAIN, 23);


        if (timings == null || timings.isEmpty()) {
            //null
            JOptionPane.showMessageDialog(null, "No loaded file", "Analysis", JOptionPane.ERROR_MESSAGE);

        }
        else {
            List<Double> timingValues = new ArrayList<>();
            for (EDLConfig conf : timings) {
                if (!timingValues.contains((conf.streamStart * conf.playRate))) {
                    timingValues.add((conf.streamStart * conf.playRate));
                }
            }
            Collections.sort(timingValues);
            String[] names = {
                    "Lowest timing",
                    "Highest timing",
                    "Timing range",
                    "Mean",
                    "Median",
                    "Standard Deviation",
                    "Skewness"
            };
            String[] values = {
                    sevenDigits.format(timingValues.getFirst()),
                    sevenDigits.format(timingValues.getLast()),
                    sevenDigits.format(range(timingValues)),
                    sevenDigits.format(mean(timingValues)),
                    sevenDigits.format(median(timingValues)),
                    sevenDigits.format(standardDeviation(timingValues)),
                    sevenDigits.format(skewness(timingValues))
            };
            Window timingWindow = new Window(300, 300, "Timings", true, false) {
                @Override
                public void menuBarItems(JMenuBar bar) {

                }
                @Override
                public void uiElements() {
                    getFrame().setLayout(new GridLayout(7, 1));
                    for (int i = 0; i < names.length; i++) {
                        labels.add(new JLabel(names[i]));
                        valueLabels.add(new JLabel(values[i]));
                        JPanel dataPanel = new JPanel();
                        dataPanel.setLayout(new GridLayout(1, 2));
                        dataPanel.add(valueLabels.get(i), SwingConstants.CENTER);
                        dataPanel.add(labels.get(i), SwingConstants.CENTER);

                        add(dataPanel, false);

                    }


                }
            };
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
        double mean = mean(list);
        double varianceNumerator = 0;
        for (Double num : list) {
            varianceNumerator += Math.pow((num-mean), 2);
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
        double firstMultiplier = (double) 1 /size;
        for (Double num : list) {
            skewSum += Math.pow((num-mean)/stdDev, 3);
        }
        return (firstMultiplier * skewSum);


    }
    public static double range(List<Double> list) {
        return (list.getLast()-list.getFirst());
    }

    public static double usefulness(double x) {
        return ((double)1/2) + ((double)1/3)*(Math.sin((x+70)/(1+(x/30))));
    }
}


