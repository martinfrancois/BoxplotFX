package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.Map;

import static com.google.common.math.Quantiles.quartiles;

/**
 * Created by François Martin on 29.05.17.
 */
public class BoxPlot<T> {
    private ObservableMap<T, Double> data;
    private ObservableMap<T, Double> outliers = FXCollections.observableHashMap();

    private final DoubleProperty min = new SimpleDoubleProperty();
    private final DoubleProperty lowerWhisker = new SimpleDoubleProperty();
    private final DoubleProperty q1 = new SimpleDoubleProperty(); // lower quartile
    private final DoubleProperty median = new SimpleDoubleProperty();
    private final DoubleProperty q3 = new SimpleDoubleProperty(); // upper quartile
    private final DoubleProperty upperWhisker = new SimpleDoubleProperty();
    private final DoubleProperty max = new SimpleDoubleProperty();

    public BoxPlot(ObservableMap<T, Double> data){
        this.data = data;
        calculate(data);
        data.addListener(
            (MapChangeListener<? super T, ? super Double>) change -> calculate(data)
        );
    }

    private void calculate(ObservableMap<T, Double> data) {
        if(data.size() != 0) {
            // convert into collection of values
            Collection<Double> values = data.values();

            // calculate quartiles
            Map<Integer, Double> quartiles = quartiles().indexes(0, 1, 2, 3, 4).compute(values);

            // set values
            setMin(quartiles.get(0));
            setQ1(quartiles.get(1));
            setMedian(quartiles.get(2));
            setQ3(quartiles.get(3));
            setMax(quartiles.get(4));
            double iqr = getQ3() - getQ1(); // interquartile range
            double iqr15 = iqr * 1.5;
            setLowerWhisker(getQ1() - iqr15);
            setUpperWhisker(getQ3() + iqr15);

            // define outliers
            outliers.clear();
            data.entrySet().stream()
                .distinct()
                .filter(entry -> entry.getValue() > getUpperWhisker() || entry.getValue() < getLowerWhisker())
                .forEach(entry -> outliers.put(entry.getKey(), entry.getValue()));
        }
    }

    public ObservableMap<T, Double> getOutliers() {
        return outliers;
    }

    public double getMin() {
        return min.get();
    }

    public ReadOnlyDoubleProperty minProperty() {
        return min;
    }

    public double getLowerWhisker() {
        return lowerWhisker.get();
    }

    public ReadOnlyDoubleProperty lowerWhiskerProperty() {
        return lowerWhisker;
    }

    public double getQ1() {
        return q1.get();
    }

    public ReadOnlyDoubleProperty q1Property() {
        return q1;
    }

    public double getMedian() {
        return median.get();
    }

    public ReadOnlyDoubleProperty medianProperty() {
        return median;
    }

    public double getQ3() {
        return q3.get();
    }

    public ReadOnlyDoubleProperty q3Property() {
        return q3;
    }

    public double getUpperWhisker() {
        return upperWhisker.get();
    }

    public ReadOnlyDoubleProperty upperWhiskerProperty() {
        return upperWhisker;
    }

    public double getMax() {
        return max.get();
    }

    public ReadOnlyDoubleProperty maxProperty() {
        return max;
    }

    private void setMin(double min) {
        this.min.set(min);
    }

    private void setLowerWhisker(double lowerWhisker) {
        this.lowerWhisker.set(lowerWhisker);
    }

    private void setQ1(double q1) {
        this.q1.set(q1);
    }

    private void setMedian(double median) {
        this.median.set(median);
    }

    private void setQ3(double q3) {
        this.q3.set(q3);
    }

    private void setUpperWhisker(double upperWhisker) {
        this.upperWhisker.set(upperWhisker);
    }

    private void setMax(double max) {
        this.max.set(max);
    }
}

