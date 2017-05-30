package ch.fhnw.cuie.project.template_businesscontrol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.math.Quantiles.quartiles;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlot {
    private HashMap<Object, Double> data;

    private double min;
    private double lowerWhisker;
    private double q1; // unteres Quartil
    private double median;
    private double q3; // oberes Quartil
    private double upperWhisker;
    private double max;

    double iqr; // Interquartilabstand

    public BoxPlot(HashMap<Object, Double> data){
        this.data = data;
        Collection<Double> numberList = data.values();
        calculateParams(numberList);
    }

    public void calculateParams(Collection<Double> values) {
        Map<Integer, Double> quartiles = quartiles().indexes(0, 1, 2, 3, 4).compute(values);
        min = quartiles.get(0);
        q1 = quartiles.get(1);
        median = quartiles.get(2);
        q3 = quartiles.get(3);
        max = quartiles.get(4);
        iqr = q3 - q1;
        double iqr15 = iqr * 1.5;
        lowerWhisker = q1 - iqr15;
        upperWhisker = q3 + iqr15;
    }

    public double getMin() {
        return min;
    }

    public double getLowerWhisker() {
        return lowerWhisker;
    }

    public double getQ1() {
        return q1;
    }

    public double getMedian() {
        return median;
    }

    public double getQ3() {
        return q3;
    }

    public double getUpperWhisker() {
        return upperWhisker;
    }

    public double getMax() {
        return max;
    }
}

