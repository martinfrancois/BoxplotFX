package ch.fhnw.cuie.project.template_businesscontrol;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlotTest {
    private static final double MARGIN_OF_ERROR = 1e-6;
    double[] array = {1, 5, 10, 16, 19, 26, 29, 50, 56, 79, 4, 33, 33, 333, 102, 202};
    HashMap<Object, Double> map = new HashMap<>();
    private double min = 1;
    private double lowerWhisker = -56.375;
    private double q1 = 14.5; // unteres Quartil
    private double median = 31;
    private double q3 = 61.75; // oberes Quartil
    private double upperWhisker = 132.625;
    private double max = 333;
    private BoxPlot boxPlot;

    @Before
    public void setup(){
        for (double num : array) {
            map.put(new Object(), num);
        }
        boxPlot = new BoxPlot(map);
    }

    @Test
    public void calculateParams() throws Exception {
        assertEquals("min", min, boxPlot.getMin(), MARGIN_OF_ERROR);
        assertEquals("lowerWhisker", lowerWhisker, boxPlot.getLowerWhisker(),MARGIN_OF_ERROR);
        assertEquals("q1", q1, boxPlot.getQ1(), MARGIN_OF_ERROR);
        assertEquals("median", median, boxPlot.getMedian(), MARGIN_OF_ERROR);
        assertEquals("q3", q3, boxPlot.getQ3(), MARGIN_OF_ERROR);
        assertEquals("upperWhisker", upperWhisker, boxPlot.getUpperWhisker(), MARGIN_OF_ERROR);
        assertEquals("max", max, boxPlot.getMax(), MARGIN_OF_ERROR);
    }

}
