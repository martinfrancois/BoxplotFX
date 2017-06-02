package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.*;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.regex.Pattern;

/**
 * @author Dieter Holz
 */
public class BoxPlotControl<T> extends Control {
    private final ObjectProperty<T> currentElement = new SimpleObjectProperty<>();
    private final BoxPlot<T> boxPlot;

    public BoxPlotControl(ObservableMap<T, Double> map) {
        initializeSelf();
        addValueChangeListener();
        boxPlot = new BoxPlot<>(map);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BoxPlotSkin(this);
    }

    private void initializeSelf() {
         getStyleClass().add("boxPlotControl");
    }

    private void addValueChangeListener() {

    }

    // all the getters and setters

    public T getCurrentElement() {
        return currentElement.get();
    }

    public ObjectProperty<T> currentElementProperty() {
        return currentElement;
    }

    public void setCurrentElement(T currentElement) {
        this.currentElement.set(currentElement);
    }

    BoxPlot<T> getBoxPlot() {
        return boxPlot;
    }
}
