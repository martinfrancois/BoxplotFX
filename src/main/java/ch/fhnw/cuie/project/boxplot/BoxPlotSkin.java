package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
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

    // always needed
    private static final double ARTBOARD_WIDTH = 100;
    private static final double ARTBOARD_HEIGHT = 100;
    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;
    private static final double MINIMUM_WIDTH = 25;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;
    private static final double MAXIMUM_WIDTH = 800;
public class BoxPlotSkin<T> extends SkinBase<BoxPlotControl> {

    private StackPane drawingPane;

    // ----- Properties --------------------------------
    private static final DoubleProperty lowerWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty upperWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty median = new SimpleDoubleProperty();
    private static final DoubleProperty lowerQuartil = new SimpleDoubleProperty();
    private static final DoubleProperty upperQuartil = new SimpleDoubleProperty();
    private static final DoubleProperty minElement = new SimpleDoubleProperty();
    private static final DoubleProperty maxElement = new SimpleDoubleProperty();

    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "style.css";

    // ----- Parts -------------------------------------
    private Line range;
    private Rectangle quartiles;
    private Line lowerWhiskerLine;
    private Line upperWhiskerLine;
    private Circle outliers;
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
        //setupBindings();
        boxPlot = getSkinnable().getBoxPlot();
        outliers = boxPlot.getOutliers();
        initOutliers();
        setupValueChangeListeners();
    }

    private void drawOutlier(T element, double value){
        // do some magic to create the outlier and attach listener to setOnAction to change currently selected object
        Circle outlier = new Circle();
        circles.put(element, outlier);
    }

    private void removeOutlier(T element){
        // remove the outlier associated with this element
        drawingPane.getChildren().remove(circles.get(element));
    }

    private void initOutliers(){
        outliers.entrySet().stream()
                .forEach(entry -> {
                    drawOutlier(entry.getKey(), entry.getValue());
                });
        setupBindings();
        setupListeners();
    }

    private void setupBindings() {

    }

    private void initializeSelf() {
        // ----- Initialize Properties ----------------------
        minElement.set(-15);
        //    Computes the offset, from 0 to the minElement. This is needed for scaling
        offset = minElement.get() * -1;

        lowerWhisker.set(-13 + offset);
        upperWhisker.set(4 + offset);
        median.set(-2 + offset);
        lowerQuartil.set(-7 + offset);
        upperQuartil.set(1 + offset);
        maxElement.set(5 + offset);
        minElement.set(0);

        // ----- CSS ----------------------------------------
        String fonts = getClass().getResource(FONTS_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(fonts);

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(stylesheet);
    }

    private void initializeParts() {
        drawingPane = new StackPane();
        drawingPane.getStyleClass().add("drawingPane");

        //    line id="range" class="st1" x1="325.6" y1="455.7" x2="1798.4" y2="455.7"
//    rect id="quartiles" x="831.7" y="331.4" class="st2" width="673.7" height="247.7"
//    line id="lowerWhisker" class="st1" x1="325.6" y1="366.1" x2="325.6" y2="539.8"
//    line id="upperWhisker" class="st1" x1="1798.4" y1="366.1" x2="1798.4" y2="539.8"
//    circle id="Ausreisser" class="st1" cx="199.3" cy="453" r="14"
//    line id="Median" class="st3" x1="1257.8" y1="331.4" x2="1257.8" y2="579.1"

        double widthFactor = getWidthFactor();
        range = new Line(0,height/2, width,height/2);
        quartiles = new Rectangle(lowerQuartil.get()*widthFactor,0,(upperQuartil.get() - lowerQuartil.get())*widthFactor, height);
        lowerWhiskerLine = new Line(lowerWhisker.get()*widthFactor, 0, lowerWhisker.get()*widthFactor, height);
        upperWhiskerLine = new Line(upperWhisker.get()*widthFactor, 0, upperWhisker.get()*widthFactor, height);
//        outliers = new Circle(5, -15, 5);
        medianLine = new Line(median.get()*widthFactor, 0, median.get()*widthFactor, height);
    }

//    Returns the factor, which is needed to resize the data
    private double getWidthFactor() {
        return width/(maxElement.get()-minElement.get());
    }

    private void layoutParts() {
        drawingPane.getChildren().addAll(range, quartiles, lowerWhiskerLine, upperWhiskerLine, outliers, medianLine);
        getChildren().add(drawingPane);
    }

    private void setupAnimations() {

    }

    private void setupEventHandlers() {

    }

    private void setupValueChangeListeners() {
        outliers.addListener((MapChangeListener<? super T, ? super Double>) change -> {
            if(change.wasRemoved()) {
                removeOutlier(change.getKey());
            } else if (change.wasAdded()) {
                drawOutlier(change.getKey(), change.getValueAdded());
            }
        });

        drawingPane.widthProperty().addListener(e -> {
            width = drawingPane.getWidth();
        });

        drawingPane.heightProperty().addListener(e -> {
            height = drawingPane.getHeight();
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

    public static double getLowerQuartil() {
        return lowerQuartil.get();
    }

    public static DoubleProperty lowerQuartilProperty() {
        return lowerQuartil;
    }

    public static void setLowerQuartil(double lowerQuartil) {
        BoxPlotSkin.lowerQuartil.set(lowerQuartil);
    }

    public static double getUpperQuartil() {
        return upperQuartil.get();
    }

    public static DoubleProperty upperQuartilProperty() {
        return upperQuartil;
    }

    public static void setUpperQuartil(double upperQuartil) {
        BoxPlotSkin.upperQuartil.set(upperQuartil);
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
