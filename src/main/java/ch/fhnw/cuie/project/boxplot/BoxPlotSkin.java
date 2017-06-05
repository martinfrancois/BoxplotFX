package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
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
    private double offset;
    private static final double STROKE_WIDTH = 2;
    private final ObservableMap<T, Double> outliers;
    private final BoxPlot<T> boxPlot;
    private final HashMap<T, Circle> circles = new HashMap<>();

    private Pane drawingPane;

    // ----- Properties --------------------------------
    private static final DoubleProperty lowerWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty upperWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty median = new SimpleDoubleProperty();
    private static final DoubleProperty lowerQuartile = new SimpleDoubleProperty();
    private static final DoubleProperty upperQuartile = new SimpleDoubleProperty();
    private static final DoubleProperty minElement = new SimpleDoubleProperty();
    private static final DoubleProperty maxElement = new SimpleDoubleProperty();
    private static final DoubleProperty currentObject = new SimpleDoubleProperty();

    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "style.css";

    // ----- Parts -------------------------------------
    private Line range;
    private Rectangle quartiles;
    private Line lowerWhiskerLine;
    private Line upperWhiskerLine;
    private Line medianLine;
    private Line currentObjectLine;

    // ----- Scale below -------------------------------
    private Line scale;
    private Label minimum;
    private Label maximum;
    private Label lowerWhiskerLabel;
    private Label upperWhiskerLabel;
    private Label lowerQuartileLabel;
    private Label upperQuartileLabel;
    private Label medianLabel;
    private Label currentObjectLabel;

    // ------ Variables ---------------------------------
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final DoubleProperty widthFactor = new SimpleDoubleProperty();

    public BoxPlotSkin(BoxPlotControl control) {
        super(control);
        boxPlot = getSkinnable().getBoxPlot();
        outliers = boxPlot.getOutliers();

        initializeSelf();
        initializeParts();
        layoutParts();
        setupAnimations();
        setupEventHandlers();
        setupBindings();
        initOutliers();
        setupValueChangeListeners();
    }

    private void initializeSelf() {
        // ----- Initialize Properties ----------------------
        drawingPane = new Pane();

        minElement.set(-15);
        //    Computes the offset, from 0 to the minElement. This is needed for scaling
        offset = minElement.get() * -1;

        lowerWhisker.set(-13 + offset);
        upperWhisker.set(4 + offset);
        median.set(-2 + offset);
        lowerQuartile.set(-7 + offset);
        upperQuartile.set(1 + offset);
        maxElement.set(5 + offset);
        currentObject.set(-4 + offset);
        minElement.set(0);

        // ----- CSS ----------------------------------------
        String fonts = getClass().getResource(FONTS_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(fonts);

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(stylesheet);
    }

    private void initializeParts() {
        drawingPane.getStyleClass().add("drawingPane");
        drawingPane.setPrefSize(600, 50);

        range = new Line();
        quartiles = new Rectangle();
        lowerWhiskerLine = new Line();
        upperWhiskerLine = new Line();
        medianLine = new Line();
        currentObjectLine = new Line();

        range.setStroke(Color.rgb(138, 0, 138));
        quartiles.setFill(Color.rgb(227, 227, 227));
        quartiles.setStroke(Color.rgb(0, 0, 138));

        range.setStrokeWidth(STROKE_WIDTH);
        quartiles.setStrokeWidth(STROKE_WIDTH);
        medianLine.setStroke(Color.rgb(0, 99, 0));
        lowerWhiskerLine.setStrokeWidth(STROKE_WIDTH);
        lowerWhiskerLine.setStroke(Color.rgb(138, 0, 138));
        upperWhiskerLine.setStrokeWidth(STROKE_WIDTH);
        medianLine.setStrokeWidth(STROKE_WIDTH * 2);
        upperWhiskerLine.setStroke(Color.rgb(138, 0, 138));
        currentObjectLine.setStrokeWidth(STROKE_WIDTH);

        // ---- Scale below -----------------------------
        scale = new Line();
        minimum = new Label();
        maximum = new Label();
        lowerWhiskerLabel = new Label();
        upperWhiskerLabel = new Label();
        lowerQuartileLabel = new Label();
        upperQuartileLabel = new Label();
        medianLabel = new Label();
        currentObjectLabel = new Label();

    }

    private void layoutParts() {
        drawingPane.getChildren().addAll(range, quartiles, lowerWhiskerLine, upperWhiskerLine, medianLine, currentObjectLine);
        drawingPane.getChildren().addAll(scale, minimum, maximum, lowerWhiskerLabel, upperWhiskerLabel, lowerQuartileLabel, upperQuartileLabel, medianLabel, currentObjectLabel);
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
            adapt(drawingPane.getHeight());
        });

        drawingPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            adapt(drawingPane.getHeight());
        });
    }

    //    Draws the BoxPlot
    private void adapt(double height) {
        double translateY = 20;
        widthFactor.set(width.get() / (maxElement.get() - minElement.get()));

        range.startXProperty().set(lowerWhisker.get() * widthFactor.get());
        range.startYProperty().set((height - translateY) / 2);
        range.endXProperty().set(upperWhisker.get() * widthFactor.get());
        range.endYProperty().set((height - translateY) / 2);

        quartiles.xProperty().set(lowerQuartile.get() * widthFactor.get());
        quartiles.yProperty().set(0);
        quartiles.widthProperty().set((upperQuartile.get() - lowerQuartile.get()) * widthFactor.get());
        quartiles.heightProperty().set(height - translateY);

        drawLine(lowerWhiskerLine, lowerWhisker, height);
        drawLine(upperWhiskerLine, upperWhisker, height);
        drawLine(medianLine, median, height);
        drawLine(currentObjectLine, currentObject, height);
    }

    private void drawLine(Line line, DoubleProperty element, double height) {
        line.startXProperty().set(element.get() * widthFactor.get());
        line.startYProperty().set(0);
        line.endXProperty().set(element.get() * widthFactor.get());
        line.endYProperty().set(height - 20);
    }

    private void setupBindings() {
        width.bind(drawingPane.widthProperty());
        height.bind(drawingPane.heightProperty());

        // ---- Scale below -----------------------------
        double translateY = 15;
        scale.startXProperty().bind(minElement.multiply(widthFactor));
        scale.startYProperty().bind(height.subtract(translateY));
        scale.endXProperty().bind(maxElement.multiply(widthFactor));
        scale.endYProperty().bind(height.subtract(translateY));

        drawLabel(minimum, minElement);
        drawLabel(maximum, maxElement);
        drawLabel(lowerWhiskerLabel, lowerWhisker);
        drawLabel(upperWhiskerLabel, upperWhisker);
        drawLabel(lowerQuartileLabel, lowerQuartile);
        drawLabel(upperQuartileLabel, upperQuartile);
        drawLabel(medianLabel, median);
    }

    private void drawLabel(Label label, DoubleProperty element){
        label.textProperty().bind(element.subtract(offset).asString());
        label.translateXProperty().bind(element.multiply(widthFactor).subtract(label.widthProperty().divide(2)));
        label.translateYProperty().bind(height.subtract(15));
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
