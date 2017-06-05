package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

/**
 * @author Dieter Holz
 */
public class BoxPlotSkin<T> extends SkinBase<BoxPlotControl> {
    private static final double STROKE_WIDTH = 2;
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

        range.setStroke(Color.rgb(138,0,138));
        quartiles.setFill(Color.rgb(227,227,227));
        quartiles.setStroke(Color.rgb(0,0,138));

        range.setStrokeWidth(STROKE_WIDTH);
        quartiles.setStrokeWidth(STROKE_WIDTH);
        medianLine.setStroke(Color.rgb(0,99,0));
        lowerWhiskerLine.setStrokeWidth(STROKE_WIDTH);
        lowerWhiskerLine.setStroke(Color.rgb(138,0,138));
        upperWhiskerLine.setStrokeWidth(STROKE_WIDTH);
        medianLine.setStrokeWidth(STROKE_WIDTH);
        upperWhiskerLine.setStroke(Color.rgb(138,0,138));
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
            adapt(drawingPane.getHeight());
        });

        drawingPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            adapt(drawingPane.getHeight());
        });
    }

    //    Draws the BoxPlot
    private void adapt(double height) {
        widthFactor.set(width.get() / (boxPlot.getMax() - boxPlot.getMin()));
        
        range.startXProperty().set(boxPlot.getLowerWhisker() * widthFactor.get());
        range.startYProperty().set(height / 2);
        range.endXProperty().set(boxPlot.getUpperWhisker() * widthFactor.get());
        range.endYProperty().set(height / 2);
        
        quartiles.xProperty().set(boxPlot.getQ1() * widthFactor.get());
        quartiles.yProperty().set(0);
        quartiles.widthProperty().set((boxPlot.getQ3() - boxPlot.getQ1()) * widthFactor.get());
        quartiles.heightProperty().set(height);

        lowerWhiskerLine.startXProperty().set(boxPlot.getLowerWhisker() * widthFactor.get());
        lowerWhiskerLine.startYProperty().set(0);
        lowerWhiskerLine.endXProperty().set(boxPlot.getLowerWhisker() * widthFactor.get());
        lowerWhiskerLine.endYProperty().set(height);

        upperWhiskerLine.startXProperty().set(boxPlot.getUpperWhisker() * widthFactor.get());
        upperWhiskerLine.startYProperty().set(0);
        upperWhiskerLine.endXProperty().set(boxPlot.getUpperWhisker() * widthFactor.get());
        upperWhiskerLine.endYProperty().set(height);

        medianLine.startXProperty().set(boxPlot.getMedian() * widthFactor.get());
        medianLine.startYProperty().set(0);
        medianLine.endXProperty().set(boxPlot.getMedian() * widthFactor.get());
        medianLine.endYProperty().set(height);
    }

    private void setupBindings() {
        width.bind(drawingPane.widthProperty());
        height.bind(drawingPane.heightProperty());
    }

    private void drawOutlier(T element, double value) {
        // TODO: Do some magic to create the outlier
        Circle outlier = new Circle();
        outlier.getStyleClass().add("outliers");
        outlier.setOnMouseClicked(event -> getSkinnable().setCurrentElement(element));
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

}
