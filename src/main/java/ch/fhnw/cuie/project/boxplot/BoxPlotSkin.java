package ch.fhnw.cuie.project.boxplot;

import java.util.function.BiFunction;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.reactfx.util.Interpolator;
import org.reactfx.util.TetraFunction;
import org.reactfx.util.TriFunction;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import java.time.Duration;
import java.util.HashMap;

import static org.reactfx.util.Interpolator.EASE_BOTH_DOUBLE;

/**
 * @author Dieter Holz
 */
public class BoxPlotSkin<T> extends SkinBase<BoxPlotControl> {
    private static final String DECIMALS = "0";
    public static final String LABEL_FORMATTING = "%." + DECIMALS + "f";
    private static final double STROKE_WIDTH = 3;
    private final ObservableMap<T, Double> outliers;
    private final BoxPlot<T> boxPlot;
    private final ObservableMap<T, Double> data;
    private final HashMap<T, Circle> circles = new HashMap<>();

    private Pane drawingPane;

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
    private Line scaleLeft;
    private Line dataScale;
    private Line scaleRight;
    private Label minimum;
    private Label maximum;
    private Label lowerWhiskerLabel;
    private Label upperWhiskerLabel;
    private Label lowerQuartileLabel;
    private Label upperQuartileLabel;
    private Label medianLabel;
    private Label currentObjectLabel;

    // ------ Variables ---------------------------------
    private static final Duration ANIMATION_DURATION = Duration.ofMillis(500);
    private static final Interpolator<Double> ANIMATION_INTERPOLATOR = EASE_BOTH_DOUBLE;
    private static final TriFunction<Double, Double, Double, Double> WIDTH_CONVERTER = (value, offset, widthFactor) -> (value - offset) * widthFactor;
    private static final BiFunction<Double, Double, Double> HEIGHT_CONVERTER = (factor, height) -> factor * height;

    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final DoubleProperty widthFactor = new SimpleDoubleProperty();
    private final DoubleProperty offset = new SimpleDoubleProperty();
    private Var<Double> widthVar = Var.doubleVar(width);
    private Var<Double> heightVar = Var.doubleVar(height);
    private Var<Double> widthFactorVar = Var.doubleVar(widthFactor);
    private Var<Double> offsetVar = Var.doubleVar(offset);

    // Height Conversions
    private static final Val<Double> FACTOR_BOXPLOT_START = Val.constant(0d);
    private static final Val<Double> FACTOR_BOXPLOT_END = Val.constant(0.75d);
    private static final Val<Double> FACTOR_DATA_POINTS_END = Val.constant(0.82d);
    private static final Val<Double> FACTOR_DATA_SCALE_END = Val.constant(0.97d);
    private static final Val<Double> FACTOR_DATA_SCALE_LABELS_END = Val.constant(1d);
    private static final Val<Double> FACTOR_BOXPLOT_CENTER = Val.constant(FACTOR_BOXPLOT_END.getValue() / 2);
    private Val<Double> convertedHeightBoxPlotStart = Val.combine(FACTOR_BOXPLOT_START, heightVar, HEIGHT_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
    private Val<Double> convertedHeightBoxPlotEnd = Val.combine(FACTOR_BOXPLOT_END, heightVar, HEIGHT_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
    private Val<Double> convertedHeightDataPointsEnd = Val.combine(FACTOR_DATA_POINTS_END, heightVar, HEIGHT_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
    private Val<Double> convertedHeightDataScaleEnd = Val.combine(FACTOR_DATA_SCALE_END, heightVar, HEIGHT_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
    private Val<Double> convertedHeightDataScaleLabelsEnd = Val.combine(FACTOR_DATA_SCALE_LABELS_END, heightVar, HEIGHT_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
    private Val<Double> convertedHeightBoxPlotCenter = Val.combine(FACTOR_BOXPLOT_CENTER, heightVar, HEIGHT_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);

    public BoxPlotSkin(BoxPlotControl control) {
        super(control);
        boxPlot = getSkinnable().getBoxPlot();
        outliers = boxPlot.getOutliers();
        data = boxPlot.getData();
        initializeSelf();
        initializeParts();
        layoutParts();
        setupAnimations();
        setupEventHandlers();
        setupAnimatedBindings();
        setupBindings();
        initCircles();
        setupValueChangeListeners();

    }

    private void initializeSelf() {
        // ----- Initialize Properties ----------------------
        drawingPane = new Pane();

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

        // ---- Scale below -----------------------------
        scaleLeft = new Line();
        dataScale = new Line();
        scaleRight = new Line();
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
        drawingPane.getChildren().addAll(
                range,
                quartiles,
                lowerWhiskerLine,
                upperWhiskerLine,
                medianLine
//                currentObjectLine
        );
        drawingPane.getChildren().addAll(
                scaleLeft,
                dataScale,
                scaleRight,
                minimum,
                maximum,
                lowerWhiskerLabel,
                upperWhiskerLabel,
                lowerQuartileLabel,
                upperQuartileLabel,
                medianLabel
//                currentObjectLabel
        );
        getChildren().add(drawingPane);

        range.setStroke(Color.rgb(138, 0, 138));
        quartiles.setFill(Color.rgb(227, 227, 227));
        quartiles.setStroke(Color.rgb(0, 0, 138));

        range.setStrokeWidth(STROKE_WIDTH);
        quartiles.setStrokeWidth(STROKE_WIDTH);
        medianLine.setStroke(Color.rgb(0, 99, 0));
        lowerWhiskerLine.setStrokeWidth(STROKE_WIDTH);
        lowerWhiskerLine.setStroke(Color.rgb(138, 0, 138));
        upperWhiskerLine.setStrokeWidth(STROKE_WIDTH);
        medianLine.setStrokeWidth(STROKE_WIDTH);
        upperWhiskerLine.setStroke(Color.rgb(138, 0, 138));
        currentObjectLine.setStrokeWidth(STROKE_WIDTH);

        dataScale.setStroke(Color.rgb(255, 200, 0));
        dataScale.setStrokeWidth(STROKE_WIDTH);

    }

    private void setupAnimations() {

    }

    private void setupEventHandlers() {

    }

    private void setupValueChangeListeners() {
        outliers.addListener((MapChangeListener<? super T, ? super Double>) change -> {
            if (change.wasRemoved()) {
                removeCircle(change.getKey());
            } else if (change.wasAdded()) {
                drawOutlier(change.getKey(), change.getValueAdded());
            }
        });

        getSkinnable().selectedElementProperty().addListener((observable, oldObject, newObject) -> {
            if (oldObject != newObject) {
                removeCircle((T)oldObject);
                drawSelectedElement((T)newObject);
            }
        });
    }

    private void setupBindings() {
        width.bind(drawingPane.widthProperty());
        height.bind(drawingPane.heightProperty());

        offset.bind(
            Bindings.createDoubleBinding(
                () -> {
                    if (boxPlot.getMin() < boxPlot.getLowerWhisker()){
                        return boxPlot.getMin();
                    }
                    return boxPlot.getLowerWhisker();
                }, boxPlot.minProperty(), boxPlot.lowerWhiskerProperty()
            )
        );

        widthFactor.bind(
            Bindings.createDoubleBinding(
                () -> {
                    if (boxPlot.getMax() > boxPlot.getUpperWhisker()) {
                        scaleRight.setVisible(false);
                        return width.get() / (boxPlot.getMax() - offset.get());
                    }
                    scaleRight.setVisible(true);
                    return width.get() / (boxPlot.getUpperWhisker() - offset.get());
                }, boxPlot.maxProperty(), boxPlot.upperWhiskerProperty(), width, offset
            )
        );
    }

    private void setupAnimatedBindings() {
        // https://tomasmikula.github.io/blog/2015/02/13/animated-transitions-made-easy.html
        // https://github.com/TomasMikula/ReactFX/blob/master/reactfx-demos/src/main/java/org/reactfx/demo/AnimatedValDemo.java
        // https://github.com/TomasMikula/ReactFX/wiki/Creating-a-Val-or-Var-Instance
        // https://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html

        Var<Double> lowerWhiskerVar = Var.doubleVar(boxPlot.lowerWhiskerProperty());
        Var<Double> upperWhiskerVar = Var.doubleVar(boxPlot.upperWhiskerProperty());
        Var<Double> q1Var = Var.doubleVar(boxPlot.q1Property());
        Var<Double> q3Var = Var.doubleVar(boxPlot.q3Property());
        Var<Double> medianVar = Var.doubleVar(boxPlot.medianProperty());
        Var<Double> minVar = Var.doubleVar(boxPlot.minProperty());
        Var<Double> maxVar = Var.doubleVar(boxPlot.maxProperty());

        // Width Conversions
        Val<Double> convertedLowerWhisker = Val.combine(lowerWhiskerVar, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedUpperWhisker = Val.combine(upperWhiskerVar, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedQ1 = Val.combine(q1Var, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedQ3 = Val.combine(q3Var, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedMedian = Val.combine(medianVar, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedMin = Val.combine(minVar, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedMax = Val.combine(maxVar, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        Val<Double> convertedQuartileWidth = Val.combine(q3Var, q1Var, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);

        // Range
        range.startXProperty().bind(convertedLowerWhisker);
        range.endXProperty().bind(convertedUpperWhisker);
        range.startYProperty().bind(convertedHeightBoxPlotCenter);
        range.endYProperty().bind(convertedHeightBoxPlotCenter);

        // Quartiles
        quartiles.xProperty().bind(convertedQ1);
        quartiles.yProperty().bind(convertedHeightBoxPlotStart);
        quartiles.widthProperty().bind(convertedQuartileWidth);
        quartiles.heightProperty().bind(convertedHeightBoxPlotEnd);

        // all vertical lines
        setupVerticalLineBindings(lowerWhiskerLine, convertedLowerWhisker);
        setupVerticalLineBindings(upperWhiskerLine, convertedUpperWhisker);
        setupVerticalLineBindings(medianLine, convertedMedian);
        //setupVerticalLineBindings(currentObjectLine, currentObject, height);

        // ---- Scale below -----------------------------
        dataScale.startXProperty().bind(convertedMin);
        dataScale.endXProperty().bind(convertedMax);
        dataScale.startYProperty().bind(convertedHeightDataScaleEnd);
        dataScale.endYProperty().bind(convertedHeightDataScaleEnd);

        scaleLeft.startXProperty().set(0);
        scaleLeft.endXProperty().bind(dataScale.startXProperty());
        scaleLeft.startYProperty().bind(convertedHeightDataScaleEnd);
        scaleLeft.endYProperty().bind(convertedHeightDataScaleEnd);

        scaleRight.startXProperty().bind(dataScale.endXProperty());
        scaleRight.endXProperty().bind(width);
        scaleRight.startYProperty().bind(convertedHeightDataScaleEnd);
        scaleRight.endYProperty().bind(convertedHeightDataScaleEnd);

        drawLabel(minimum, boxPlot.minProperty(), false);
        drawLabel(maximum, boxPlot.maxProperty(), false);
        drawLabel(lowerWhiskerLabel, boxPlot.lowerWhiskerProperty(), true);
        drawLabel(upperWhiskerLabel, boxPlot.upperWhiskerProperty(), true);
        drawLabel(lowerQuartileLabel, boxPlot.q1Property(), true);
        drawLabel(upperQuartileLabel, boxPlot.q3Property(), true);
        drawLabel(medianLabel, boxPlot.medianProperty(), true);
    }

    private void setupVerticalLineBindings(Line line, Val<Double> convertedValue) {
        line.startXProperty().bind(convertedValue);
        line.endXProperty().bind(convertedValue);
        line.startYProperty().bind(convertedHeightBoxPlotStart);
        line.endYProperty().bind(convertedHeightBoxPlotEnd);
    }

    private void drawLabel(Label label, DoubleProperty value, boolean isDataPoint) {
        label.textProperty().bind(value.asString(LABEL_FORMATTING));
        Var<Double> valueVar = Var.doubleVar(value);
        Val<Double> labelWidth = Val.wrap(label.widthProperty().asObject());
        TetraFunction<Double, Double, Double, Double, Double> widthConverterExtended = (doubleValue, offset, widthFactor, labelWidthVal) -> ((doubleValue - offset) * widthFactor) - (labelWidthVal / 2);
        Val<Double> convertedValue = Val.combine(valueVar, offsetVar, widthFactorVar, labelWidth, widthConverterExtended).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        label.translateXProperty().bind(convertedValue);
        if (isDataPoint) {
            label.translateYProperty().bind(convertedHeightDataPointsEnd);
        }else{
            label.translateYProperty().bind(convertedHeightDataScaleLabelsEnd);
        }
    }

    private void drawOutlier(T element, double value) {
        System.out.println("Create Outlier: " + element.toString() + " with: " + value);
        Circle outlier = makeCircle(element, value);
        outlier.setOnMouseClicked(event -> {
            getSkinnable().setCurrentElement(null);
            getSkinnable().setCurrentElement(element);
        });
        outlier.getStyleClass().add("outliers");
    }

    private void drawSelectedElement(T element) {
        if(element != null){
            double value = data.get(element);
            System.out.println("Create SelectedElement: " + element.toString() + " with: " + value);
            Circle outlier = makeCircle(element, value);
            outlier.getStyleClass().add("selectedElement");
        }
    }

    private Circle makeCircle(T element, double value){
        Circle circle = new Circle();
        circle.centerYProperty().bind(convertedHeightBoxPlotCenter);
        Val<Double> valueVal = Val.constant(value);
        Val<Double> convertedValue = Val.combine(valueVal, offsetVar, widthFactorVar, WIDTH_CONVERTER).animate(ANIMATION_DURATION, ANIMATION_INTERPOLATOR);
        circle.centerXProperty().bind(convertedValue);
        circle.setRadius(5);
        circles.put(element, circle);
        drawingPane.getChildren().add(circle);
        return circle;
    }

    private void removeCircle(T element) {
        // remove the circle associated with this element
        drawingPane.getChildren().remove(circles.get(element));
        circles.remove(element);
    }

    private void initCircles() {
        // outliers
        outliers.entrySet().stream()
                .forEach(entry -> {
                    drawOutlier(entry.getKey(), entry.getValue());
                });
        // currently selected element
        drawSelectedElement((T) getSkinnable().getSelectedElement());
    }

    public double getOffset() {
        return offset.get();
    }

    public DoubleProperty offsetProperty() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset.set(offset);
    }

    public double getWidthFactor() {
        return widthFactor.get();
    }

    public DoubleProperty widthFactorProperty() {
        return widthFactor;
    }

    public void setWidthFactor(double widthFactor) {
        this.widthFactor.set(widthFactor);
    }
}
