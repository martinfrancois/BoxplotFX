package ch.fhnw.cuie.project.boxplot;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
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

import java.util.HashMap;
import java.util.function.UnaryOperator;

/**
 * @author Dieter Holz
 */
public class BoxPlotSkin<T> extends SkinBase<BoxPlotControl> {
    private static final String DECIMALS = "0";
    private static final double STROKE_WIDTH = 3;
    private final ObservableMap<T, Double> outliers;
    private final BoxPlot<T> boxPlot;
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
    private Line scaleLeft;
    private Line dataScale;
    private Line scaleRight;

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
    private final DoubleProperty offset = new SimpleDoubleProperty();
    public static final double TRANSLATE_Y = 20;
    public static final double FACTOR_BOXPLOT_START = 0d;
    public static final double FACTOR_BOXPLOT_END = 0.75d;
    public static final double FACTOR_SCALE_END = 1d;
    private final UnaryOperator<Double> scaleWidth = value -> (value - getOffset()) * getWidthFactor();
    private final UnaryOperator<Double> scaleHeight = factor -> (height.get() * factor);

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

        dataScale.setStroke(Color.rgb(255, 200, 0));
        dataScale.setStrokeWidth(STROKE_WIDTH);
    }

    private void layoutParts() {
        drawingPane.getChildren().addAll(range, quartiles, lowerWhiskerLine, upperWhiskerLine, medianLine, currentObjectLine);
        drawingPane.getChildren().addAll(scale, minimum, maximum, lowerWhiskerLabel, upperWhiskerLabel, lowerQuartileLabel, upperQuartileLabel, medianLabel, currentObjectLabel);
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

        boxPlot.getData().addListener((InvalidationListener) observable -> {
            adapt();
        });

        drawingPane.heightProperty().addListener(observable -> {
            adapt();
        });

        drawingPane.widthProperty().addListener(observable -> {
            adapt();
        });
    }

    //    Draws the BoxPlot
    private void adapt() {

        range.startXProperty().set(scaleWidth.apply(boxPlot.getLowerWhisker()));
        range.startYProperty().set((height.get() - TRANSLATE_Y) / 2);
        range.endXProperty().set((boxPlot.getUpperWhisker() - offset.get()) * widthFactor.get());
        range.endYProperty().set((height.get() - TRANSLATE_Y) / 2);

        quartiles.xProperty().set((boxPlot.getQ1() - offset.get()) * widthFactor.get());
        quartiles.yProperty().set(0);
        quartiles.widthProperty().set((boxPlot.getQ3() - boxPlot.getQ1()) * widthFactor.get());
        quartiles.heightProperty().set(height.get() - TRANSLATE_Y);

        setupVerticalLineBindings(lowerWhiskerLine, boxPlot.lowerWhiskerProperty());
        setupVerticalLineBindings(upperWhiskerLine, boxPlot.upperWhiskerProperty());
        setupVerticalLineBindings(medianLine, boxPlot.medianProperty());
//        setupVerticalLineBindings(currentObjectLine, currentObject, height);
    }

    private void setupVerticalLineBindings(Line line, ReadOnlyDoubleProperty value) {
        line.startXProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scaleWidth.apply(value.get()), offset, widthFactor
                )
        );
        line.endXProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scaleWidth.apply(value.get()), offset, widthFactor
                )
        );
        line.startYProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scaleWidth.apply(scaleHeight.apply(FACTOR_BOXPLOT_START)), offset, widthFactor
                )
        );
        line.endYProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scaleWidth.apply(scaleHeight.apply(FACTOR_BOXPLOT_END)), offset, widthFactor
                )
        );
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
                        }, boxPlot.maxProperty(), boxPlot.upperWhiskerProperty(), width
                )
        );

        // ---- Scale below -----------------------------
        double translateY = 15;
        scale.startXProperty().bind(boxPlot.minProperty().subtract(offset).multiply(widthFactor));
        scale.startYProperty().bind(height.subtract(translateY));
        scale.endXProperty().bind(boxPlot.maxProperty().subtract(offset).multiply(widthFactor));
        scale.endYProperty().bind(height.subtract(translateY));
        dataScale.startXProperty().bind(boxPlot.minProperty().subtract(offset).multiply(widthFactor));
        dataScale.startYProperty().bind(height.subtract(TRANSLATE_Y * FACTOR_BOXPLOT_END));
        dataScale.endXProperty().bind(boxPlot.maxProperty().subtract(offset).multiply(widthFactor));
        dataScale.endYProperty().bind(height.subtract(TRANSLATE_Y * FACTOR_BOXPLOT_END));

        scaleLeft.startXProperty().set(0);
        scaleLeft.startYProperty().bind(height.subtract(TRANSLATE_Y * FACTOR_BOXPLOT_END));
        scaleLeft.endXProperty().bind(dataScale.startXProperty());
        scaleLeft.endYProperty().bind(height.subtract(TRANSLATE_Y * FACTOR_BOXPLOT_END));

        scaleRight.startXProperty().bind(dataScale.endXProperty());
        scaleRight.startYProperty().bind(height.subtract(TRANSLATE_Y * FACTOR_BOXPLOT_END));
        scaleRight.endXProperty().bind(width);
        scaleRight.endYProperty().bind(height.subtract(TRANSLATE_Y * FACTOR_BOXPLOT_END));

        drawLabel(minimum, boxPlot.minProperty());
        drawLabel(maximum, boxPlot.maxProperty());
        drawLabel(lowerWhiskerLabel, boxPlot.lowerWhiskerProperty());
        drawLabel(upperWhiskerLabel, boxPlot.upperWhiskerProperty());
        drawLabel(lowerQuartileLabel, boxPlot.q1Property());
        drawLabel(upperQuartileLabel, boxPlot.q3Property());
        drawLabel(medianLabel, boxPlot.medianProperty());
    }

    private void drawLabel(Label label, ReadOnlyDoubleProperty element) {
        label.textProperty().bind(element.asString("%." + DECIMALS + "f"));
        label.translateXProperty().bind(element.subtract(offset).multiply(widthFactor).subtract(label.widthProperty().divide(2)));
        label.translateYProperty().bind(height.subtract(15));
    }

    private void drawOutlier(T element, double value) {
        // TODO: Do some magic to create the outlier
        System.out.println("Create Outlier: " + element.toString() + " with: " + value);
        Circle outlier = new Circle();
        outlier.centerYProperty().bind(height.subtract(TRANSLATE_Y).divide(2));
        outlier.centerXProperty().bind(Bindings.createDoubleBinding(() -> {
            System.out.println("Offset: " + offset.get() + " WidthFactor: " + widthFactor.get());
            return (value-offset.get()) * widthFactor.get();
        },offset, widthFactor));
        outlier.setRadius(10);
        outlier.getStyleClass().add("outliers");
        outlier.setOnMouseClicked(event -> getSkinnable().setCurrentElement(element));
        circles.put(element, outlier);
        drawingPane.getChildren().add(outlier);
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
