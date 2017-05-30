package ch.fhnw.cuie.project.boxplot;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlotTest {
    private static final double MARGIN_OF_ERROR = 1e-6;
    double[] array = {1, 5, 10, 16, 19, 26, 29, 50, 56, 79, 4, 33, 33, 333, 102, 202};
    ObservableMap<Object, Double> map = FXCollections.observableHashMap();
    private double min = 1;
    private double lowerWhisker = -56.375;
    private double q1 = 14.5; // unteres Quartil
    private double median = 31;
    private double q3 = 61.75; // oberes Quartil
    private double upperWhisker = 132.625;
    private double max = 333;
    private BoxPlot<Object> boxPlot;

    private final CsvParserSettings parserSettings = new CsvParserSettings();
    private CsvParser parser;
    private RowListProcessor rowProcessor;

    @Before
    public void setup(){
    }

    @Test
    public void testCalculate() throws Exception {
        for (double num : array) {
            map.put(new Object(), num);
        }


        assertEquals("min", min, boxPlot.getMin(), MARGIN_OF_ERROR);
        assertEquals("lowerWhisker", lowerWhisker, boxPlot.getLowerWhisker(),MARGIN_OF_ERROR);
        assertEquals("q1", q1, boxPlot.getQ1(), MARGIN_OF_ERROR);
        assertEquals("median", median, boxPlot.getMedian(), MARGIN_OF_ERROR);
        assertEquals("q3", q3, boxPlot.getQ3(), MARGIN_OF_ERROR);
        assertEquals("upperWhisker", upperWhisker, boxPlot.getUpperWhisker(), MARGIN_OF_ERROR);
        assertEquals("max", max, boxPlot.getMax(), MARGIN_OF_ERROR);
        assertEquals(2, boxPlot.getOutliers().size());
        assertTrue(boxPlot.getOutliers().containsValue(202d));
        assertTrue(boxPlot.getOutliers().containsValue(333d));
    }

    @Test
    public void testCalculatePerformance() {
        // setup csvparser
        parserSettings.setLineSeparatorDetectionEnabled(true);
        rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parser = new CsvParser(parserSettings);
        File csv = new File(Thread.currentThread().getContextClassLoader().getResource("countries_population.csv").getFile());
        parser.parse(csv);

        // initialize test data
        List<String[]> rows = rowProcessor.getRows();
        map.clear();
        rows.forEach(strings -> map.put(strings[0],Double.valueOf(strings[1])));
        long startTime = System.nanoTime();
        boxPlot = new BoxPlot<>(map);
        long endTime = System.nanoTime();
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(endTime - startTime) + " ms");
        System.out.println("Min: " + boxPlot.getMin());
        System.out.println("Lower Whisker: " + boxPlot.getLowerWhisker());
        System.out.println("Q1: " + boxPlot.getQ1());
        System.out.println("Median: " + boxPlot.getMedian());
        System.out.println("Q3: " + boxPlot.getQ3());
        System.out.println("Upper Whisker: " + boxPlot.getUpperWhisker());
        System.out.println("Max: " + boxPlot.getMax());
        System.out.println("Outliers:");
        boxPlot.getOutliers().forEach((country, population) -> {
            System.out.println(country + ", " + population);
        });
    }

}
