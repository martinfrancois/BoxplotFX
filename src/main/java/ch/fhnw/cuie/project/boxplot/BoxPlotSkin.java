package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    // needed for resizing
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
    private Circle ausreisser;
    private Line medianLine;

    public BoxPlotSkin(BoxPlotControl control) {
        super(control);
        initializeSelf();
        initializeParts();
        layoutParts();
        //setupAnimations();
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
    }

    private void initializeSelf() {
        // ----- Initialize Properties ----------------------
        lowerWhisker.set(-13);
        upperWhisker.set(4);
        median.set(-2);
        lowerQuartil.set(-7);
        upperQuartil.set(1);
        minElement.set(-15);
        maxElement.set(5);

        // ----- CSS ----------------------------------------
        String fonts = getClass().getResource(FONTS_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(fonts);

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(stylesheet);
    }

    private void initializeParts() {
        //    line id="range" class="st1" x1="325.6" y1="455.7" x2="1798.4" y2="455.7"
//    rect id="quartiles" x="831.7" y="331.4" class="st2" width="673.7" height="247.7"
//    line id="lowerWhisker" class="st1" x1="325.6" y1="366.1" x2="325.6" y2="539.8"
//    line id="upperWhisker" class="st1" x1="1798.4" y1="366.1" x2="1798.4" y2="539.8"
//    circle id="Ausreisser" class="st1" cx="199.3" cy="453" r="14"
//    line id="Median" class="st3" x1="1257.8" y1="331.4" x2="1257.8" y2="579.1"
        range = new Line(5,-15, 50,-15);
        quartiles = new Rectangle(15, -20, 20, 20);
        lowerWhiskerLine = new Line(10, -5, 10, -15);
        upperWhiskerLine = new Line(40, -5, 40, -15);
        ausreisser = new Circle(5, -15, 5);
        medianLine = new Line(30, -5, 30, -15);

        // always needed
        drawingPane = new StackPane();
        drawingPane.getStyleClass().add("drawingPane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
    }

    private void layoutParts() {
        drawingPane.getChildren().addAll();

        getChildren().add(drawingPane);
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
