package ch.fhnw.cuie.project.boxplot;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import java.time.Duration;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final DoubleProperty widthFactor = new SimpleDoubleProperty();
    private final DoubleProperty offset = new SimpleDoubleProperty();
    public static final double FACTOR_BOXPLOT_START = 0d;
    public static final double FACTOR_BOXPLOT_END = 0.75d;
    public static final double FACTOR_DATA_POINTS_END = 0.82d;
    public static final double FACTOR_DATA_SCALE_END = 0.97d;
    public static final double FACTOR_DATA_SCALE_LABELS_END = 1d;

    private final UnaryOperator<Double> scaleWidth = value -> (value - getOffset()) * getWidthFactor();
    private final Function<ReadOnlyDoubleProperty, DoubleBinding> scaleWidthBinding = value -> Bindings.createDoubleBinding(
            () -> {
                System.out.println("scaleWidthBinding: " + value.get());
                return scaleWidth.apply(value.get());
            }, offset, widthFactor, value
    );

    private final UnaryOperator<Double> scaleHeight = factor -> (height.get() * factor);
    private final Function<Double, DoubleBinding> scaleHeightBinding = factor -> Bindings.createDoubleBinding(
            () -> scaleHeight.apply(factor), height
    );

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

    private void setupAnimatedProperties() {
        // https://tomasmikula.github.io/blog/2015/02/13/animated-transitions-made-easy.html
        Var<Double> center = Var.doubleVar(scaleLeft.endXProperty());
        Val<Double> animated = Val.animate(center, Duration.ofMillis(500), EASE_BOTH_DOUBLE);
        scaleLeft.endXProperty().bind(animated);
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

        // Make Animations work
        LayoutAnimator animator = new LayoutAnimator();
        ObservableList<Line> lineList = FXCollections.observableArrayList();
        lineList.addAll(range, lowerWhiskerLine, upperWhiskerLine, medianLine, scaleLeft, dataScale, scaleRight);
        animator.observe(lineList);



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
                removeOutlier(change.getKey());
            } else if (change.wasAdded()) {
                drawOutlier(change.getKey(), change.getValueAdded());
            }
        });
    }

    private void setupVerticalLineBindings(Line line, ReadOnlyDoubleProperty value) {
        line.startXProperty().bind(scaleWidthBinding.apply(value));
        line.endXProperty().bind(scaleWidthBinding.apply(value));
        line.startYProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_START));
        line.endYProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_END));
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

        // Range
        range.startXProperty().bind(scaleWidthBinding.apply(boxPlot.lowerWhiskerProperty()));
        range.endXProperty().bind(scaleWidthBinding.apply(boxPlot.upperWhiskerProperty()));
        range.startYProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_END/2));
        range.endYProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_END/2));

        // Quartiles
        quartiles.xProperty().bind(scaleWidthBinding.apply(boxPlot.q1Property()));
        quartiles.yProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_START));
        quartiles.widthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> (boxPlot.getQ3() - boxPlot.getQ1()) * widthFactor.get(),
                        widthFactor, boxPlot.q3Property(), boxPlot.q1Property()
                )
        );
        quartiles.heightProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_END));

        // all vertical lines
        setupVerticalLineBindings(lowerWhiskerLine, boxPlot.lowerWhiskerProperty());
        setupVerticalLineBindings(upperWhiskerLine, boxPlot.upperWhiskerProperty());
        setupVerticalLineBindings(medianLine, boxPlot.medianProperty());
//        setupVerticalLineBindings(currentObjectLine, currentObject, height);

        // ---- Scale below -----------------------------
        dataScale.startXProperty().bind(scaleWidthBinding.apply(boxPlot.minProperty()));
        dataScale.endXProperty().bind(scaleWidthBinding.apply(boxPlot.maxProperty()));
        dataScale.startYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_END));
        dataScale.endYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_END));

        scaleLeft.startXProperty().set(0);
        scaleLeft.endXProperty().bind(dataScale.startXProperty());
        scaleLeft.startYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_END));
        scaleLeft.endYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_END));

        scaleRight.startXProperty().bind(dataScale.endXProperty());
        scaleRight.endXProperty().bind(width);
        scaleRight.startYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_END));
        scaleRight.endYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_END));

        drawLabel(minimum, boxPlot.minProperty(), false);
        drawLabel(maximum, boxPlot.maxProperty(), false);
        drawLabel(lowerWhiskerLabel, boxPlot.lowerWhiskerProperty(), true);
        drawLabel(upperWhiskerLabel, boxPlot.upperWhiskerProperty(), true);
        drawLabel(lowerQuartileLabel, boxPlot.q1Property(), true);
        drawLabel(upperQuartileLabel, boxPlot.q3Property(), true);
        drawLabel(medianLabel, boxPlot.medianProperty(), true);
    }

    private void drawLabel(Label label, ReadOnlyDoubleProperty value, boolean isDataPoint) {
        label.textProperty().bind(value.asString(LABEL_FORMATTING));
        label.translateXProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scaleWidth.apply(value.get()) - (label.widthProperty().get() / 2),
                        offset, widthFactor, value, label.widthProperty()
                )
        );
        if (isDataPoint) {
            label.translateYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_POINTS_END));
        }else{
            label.translateYProperty().bind(scaleHeightBinding.apply(FACTOR_DATA_SCALE_LABELS_END));
        }
    }

    private void drawOutlier(T element, double value) {
        System.out.println("Create Outlier: " + element.toString() + " with: " + value);
        Circle outlier = new Circle();
        outlier.centerYProperty().bind(scaleHeightBinding.apply(FACTOR_BOXPLOT_END/2));
        outlier.centerXProperty().bind(Bindings.createDoubleBinding(() -> (value-offset.get()) * widthFactor.get(),offset, widthFactor));
        outlier.setRadius(5);
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
