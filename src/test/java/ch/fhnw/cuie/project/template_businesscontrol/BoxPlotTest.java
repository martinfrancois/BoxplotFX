package ch.fhnw.cuie.project.template_businesscontrol;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlotTest {
    private static final double MARGIN_OF_ERROR = 10;
    int[] array = {1, 5, 10, 16, 19, 26, 29, 50, 56, 79, 4, 33, 33, 333, 102, 202};
    HashMap<Object, Number> map = new HashMap<>();
    private int min = 1;
    private int lowerWhisker = -75;
    private int q1 = 20; // unteres Quartil
    private int median = 33;
    private int q3 = 84; // oberes Quartil
    private int upperWhisker = 180;
    private int max = 333;
    private BoxPlot boxPlot;

    @Before
    public void setup(){
        for (int num : array) {
            map.put(new Object(), num);
        }
        boxPlot = new BoxPlot(map);
    }

    @Test
    public void calculateParams() throws Exception {
        assertEquals("min", min, boxPlot.getMin());
        assertEquals("lowerWhisker", lowerWhisker, boxPlot.getLowerWhisker().doubleValue(),MARGIN_OF_ERROR);
        assertEquals("q1", q1, boxPlot.getQ1().doubleValue(), MARGIN_OF_ERROR);
        assertEquals("median", median, boxPlot.getMedian());
        assertEquals("q3", q3, boxPlot.getQ3().doubleValue(), MARGIN_OF_ERROR);
        assertEquals("upperWhisker", upperWhisker, boxPlot.getUpperWhisker().doubleValue(), MARGIN_OF_ERROR);
        assertEquals("max", max, boxPlot.getMax());
    }

}
