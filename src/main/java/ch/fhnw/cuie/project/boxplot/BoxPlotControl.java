package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

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
        currentElement.addListener((observable, oldValue, newValue) -> {
            String output = "";
            if (oldValue != newValue) {
                output = "currentElement ";
                if(oldValue != null){
                    output += "from: " + oldValue.toString() + " ";
                }
                output += "to: " + newValue.toString();
            }
            System.out.println(output);
        });
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
