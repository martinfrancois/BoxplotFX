package ch.fhnw.cuie.project.template_businesscontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlot {
    Number[] valueArray;

    Number min;
    Number lowerWhisker;
    Number q1; // unteres Quartil
    Number median;
    Number q3; // oberes Quartil
    Number upperWhisker;
    Number max;

    Number iqr; // Interquartilabstand

    public BoxPlot(HashMap<Object, Number> data){
        valueArray = (Number[]) data.values().toArray();
        calculateParams(valueArray);
    }

    public Number[] calculateParams(Number[] values) {
        Arrays.sort(valueArray);
        min = values[0];
        q1 = quartileSorted(values, 25);
        median = quartileSorted(values, 50);
        q3 = quartileSorted(values, 75);
        max = values[values.length-1];
        iqr = q3.doubleValue() - q1.doubleValue();
        Number iqr15 = iqr.doubleValue() * 1.5;
        lowerWhisker = q1.doubleValue() - iqr15.doubleValue();
        lowerWhisker = q3.doubleValue() + iqr15.doubleValue();

    }

    /**
     * Runs on sorted array.
     * @param quartileValue 25 will return q1
     *                      50 will return median
     *                      75 will return q3
     */
    public Number quartileSorted(Number[] values, double quartileValue) {
        int n = (int) Math.round(values.length * quartileValue / 100);
        return values[n];
    }

    public Number quartile(Number[] values, double quartileValue) {
        Number[] v = new Number[values.length];
        System.arraycopy(values, 0, v, 0, values.length);
        Arrays.sort(v);
        return quartileSorted(values, quartileValue);
    }
}

}
