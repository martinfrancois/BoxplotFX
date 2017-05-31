package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

/**
 * @author Dieter Holz
 */
public class BoxPlotSkin<T> extends SkinBase<BoxPlotControl> {
    private final ObservableMap<T, Double> outliers;
    private final BoxPlot<T> boxPlot;
    private final HashMap<T, Circle> circles = new HashMap<>();

    private StackPane drawingPane;

    // ----- Properties --------------------------------
    private static final DoubleProperty lowerWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty upperWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty median = new SimpleDoubleProperty();
    private static final DoubleProperty lowerQuartile = new SimpleDoubleProperty();
    private static final DoubleProperty upperQuartile = new SimpleDoubleProperty();
    private static final DoubleProperty minElement = new SimpleDoubleProperty();
    private static final DoubleProperty maxElement = new SimpleDoubleProperty();

    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "style.css";

    // ----- Parts -------------------------------------
    private Line range;
    private Rectangle quartiles;
    private Line lowerWhiskerLine;
    private Line upperWhiskerLine;
    private Line medianLine;

    // ------ Variables ---------------------------------
    private double width;
    private double height;
    private double offset;

    public BoxPlotSkin(BoxPlotControl control) {
        super(control);
        initializeSelf();
        initializeParts();
        layoutParts();
        setupAnimations();
        setupEventHandlers();
        //setupValueChangedListeners();
        setupBindings();
        boxPlot = getSkinnable().getBoxPlot();
        outliers = boxPlot.getOutliers();
        initOutliers();
        setupValueChangeListeners();
    }

    private void drawOutlier(T element, double value) {
        // TODO: Do some magic to create the outlier and attach listener to setOnAction to change currently selected object
        Circle outlier = new Circle();
        circles.put(element, outlier);
    }

    private void removeOutlier(T element) {
        // remove the outlier associated with this element
        drawingPane.getChildren().remove(circles.get(element));
    }

    private void initOutliers() {
        outliers.entrySet().stream()
                .forEach(entry -> {
                    drawOutlier(entry.getKey(), entry.getValue());
                });
    }

    private void setupBindings() {

    }

    private void initializeSelf() {
        // ----- Initialize Properties ----------------------
        drawingPane = new StackPane();
        drawingPane.setPrefHeight(100);
        drawingPane.setPrefWidth(200);

        height=100;
        width=200;

        minElement.set(-15);
        //    Computes the offset, from 0 to the minElement. This is needed for scaling
        offset = minElement.get() * -1;

        lowerWhisker.set(-13 + offset);
        upperWhisker.set(4 + offset);
        median.set(-2 + offset);
        lowerQuartile.set(-7 + offset);
        upperQuartile.set(1 + offset);
        maxElement.set(5 + offset);
        minElement.set(0);

        System.out.println(lowerWhisker.get());
        System.out.println(upperWhisker.get());
        System.out.println(median.get());
        System.out.println(lowerQuartile.get());
        System.out.println(upperQuartile.get());
        System.out.println(maxElement.get());
        System.out.println(minElement.get());

        // ----- CSS ----------------------------------------
        String fonts = getClass().getResource(FONTS_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(fonts);

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(stylesheet);
    }

    private void initializeParts() {
        drawingPane.getStyleClass().add("drawingPane");

        double widthFactor = getWidthFactor();
        range = new Line(0, height / 2, width, height / 2);
        System.out.println(widthFactor);
        System.out.println(range);
        quartiles = new Rectangle(lowerQuartile.get() * widthFactor, 0, (upperQuartile.get() - lowerQuartile.get()) * widthFactor, height);
        quartiles.setFill(Color.TRANSPARENT);
        quartiles.setStroke(Color.BLACK);
        lowerWhiskerLine = new Line(lowerWhisker.get() * widthFactor, 0, lowerWhisker.get() * widthFactor, height);
        upperWhiskerLine = new Line(upperWhisker.get() * widthFactor, 0, upperWhisker.get() * widthFactor, height);
//        outliers = new Circle(5, -15, 5);
        medianLine = new Line(median.get() * widthFactor, 0, median.get() * widthFactor, height);
    }

    //    Returns the factor, which is needed to resize the data
    private double getWidthFactor() {
        return width / (maxElement.get() - minElement.get());
    }

    private void layoutParts() {
        drawingPane.getChildren().addAll(range, quartiles, lowerWhiskerLine, upperWhiskerLine, medianLine);
        getChildren().add(drawingPane);
    }

    private void setupAnimations() {

    }

    private void setupEventHandlers() {

    }

    private void setupValueChangeListeners() {
        outliers.addListener((MapChangeListener<? super T, ? super Double>) change -> {
            if (change.wasRemoved()) {
                removeOutlier(change.getKey());
            } else if (change.wasAdded()) {
                drawOutlier(change.getKey(), change.getValueAdded());
            }
        });

        drawingPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.doubleValue();
        });
        drawingPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.doubleValue();
        });
    }

    private void setupBinding() {

    }

    // ------- Properties -------------------------------------------
    public static double getLowerWhisker() {
        return lowerWhisker.get();
    }

    public static DoubleProperty lowerWhiskerProperty() {
        return lowerWhisker;
    }

    public static void setLowerWhisker(double lowerWhisker) {
        BoxPlotSkin.lowerWhisker.set(lowerWhisker);
    }

    public static double getUpperWhisker() {
        return upperWhisker.get();
    }

    public static DoubleProperty upperWhiskerProperty() {
        return upperWhisker;
    }

    public static void setUpperWhisker(double upperWhisker) {
        BoxPlotSkin.upperWhisker.set(upperWhisker);
    }

    public static double getMedian() {
        return median.get();
    }

    public static DoubleProperty medianProperty() {
        return median;
    }

    public static void setMedian(double median) {
        BoxPlotSkin.median.set(median);
    }

    public static double getLowerQuartile() {
        return lowerQuartile.get();
    }

    public static DoubleProperty lowerQuartilProperty() {
        return lowerQuartile;
    }

    public static void setLowerQuartile(double lowerQuartile) {
        BoxPlotSkin.lowerQuartile.set(lowerQuartile);
    }

    public static double getUpperQuartile() {
        return upperQuartile.get();
    }

    public static DoubleProperty upperQuartilProperty() {
        return upperQuartile;
    }

    public static void setUpperQuartile(double upperQuartile) {
        BoxPlotSkin.upperQuartile.set(upperQuartile);
    }

    public static double getMinElement() {
        return minElement.get();
    }

    public static DoubleProperty minElementProperty() {
        return minElement;
    }

    public static void setMinElement(double minElement) {
        BoxPlotSkin.minElement.set(minElement);
    }

    public static double getMaxElement() {
        return maxElement.get();
    }

    public static DoubleProperty maxElementProperty() {
        return maxElement;
    }

    public static void setMaxElement(double maxElement) {
        BoxPlotSkin.maxElement.set(maxElement);
    }

}
