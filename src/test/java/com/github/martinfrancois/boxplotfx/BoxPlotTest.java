package com.github.martinfrancois.boxplotfx;

import com.github.martinfrancois.boxplotfx.BoxPlot;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlotTest {
    private static final double MARGIN_OF_ERROR = 1e-6;
    double[] array = {1, 5, 10, 16, 19, 26, 29, 50, 56, 79, 4, 33, 33, 333, 102, 202};
    ObservableMap<Object, Double> map = FXCollections.observableHashMap();
    private BoxPlot<Object> boxPlot;

    @Before
    public void setup(){
    }

    @Test
    public void testCalculate() throws Exception {
        for (double num : array) {
            map.put(new Object(), num);
        }
        boxPlot = new BoxPlot<>(map);
        assertEquals("min", 1, boxPlot.getMin(), MARGIN_OF_ERROR);
        assertEquals("lowerWhisker", -56.375, boxPlot.getLowerWhisker(),MARGIN_OF_ERROR);
        assertEquals("q1", 14.5, boxPlot.getQ1(), MARGIN_OF_ERROR);
        assertEquals("median", 31, boxPlot.getMedian(), MARGIN_OF_ERROR);
        assertEquals("q3", 61.75, boxPlot.getQ3(), MARGIN_OF_ERROR);
        assertEquals("upperWhisker", 132.625, boxPlot.getUpperWhisker(), MARGIN_OF_ERROR);
        assertEquals("max", 333, boxPlot.getMax(), MARGIN_OF_ERROR);
        assertEquals(2, boxPlot.getOutliers().size());
        assertTrue(boxPlot.getOutliers().containsValue(202d));
        assertTrue(boxPlot.getOutliers().containsValue(333d));
    }

    @Test
    public void testCalculatePerformance() {
        // setup csvparser
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        File csv = new File(BoxPlotTest.class.getResource(
            "countries_population.csv").getFile());
        parser.parse(csv);

        // initialize test data
        List<String[]> rows = rowProcessor.getRows();
        map.clear();
        rows.forEach(strings -> map.put(strings[0],Double.valueOf(strings[1])));

        long startTime = System.nanoTime();
        boxPlot = new BoxPlot<>(map);
        long endTime = System.nanoTime();
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(endTime - startTime) + " ms");

        assertEquals("min", 992, boxPlot.getMin(), MARGIN_OF_ERROR);
        assertEquals("lowerWhisker", -3236108.5, boxPlot.getLowerWhisker(),MARGIN_OF_ERROR);
        assertEquals("q1", 77289.5, boxPlot.getQ1(), MARGIN_OF_ERROR);
        assertEquals("median", 610430.5, boxPlot.getMedian(), MARGIN_OF_ERROR);
        assertEquals("q3", 2286221.5, boxPlot.getQ3(), MARGIN_OF_ERROR);
        assertEquals("upperWhisker", 5599619.5, boxPlot.getUpperWhisker(), MARGIN_OF_ERROR);
        assertEquals("max", 137122000, boxPlot.getMax(), MARGIN_OF_ERROR);
        assertEquals(23, boxPlot.getOutliers().size());

        System.out.println("Outliers:");
        boxPlot.getOutliers().forEach((country, population) -> System.out.println(country + ", " + population));
    }

}
