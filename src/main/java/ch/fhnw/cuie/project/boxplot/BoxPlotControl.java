package ch.fhnw.cuie.project.boxplot;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @author Dieter Holz
 */
public class BoxPlotControl<T> extends Control {
    /**
     * Used to switch between different outliers when clicking on them.
     * Changes to the most recently clicked on outlier.
     */
    private final ObjectProperty<T> currentElement = new SimpleObjectProperty<>();

    /**
     * Can be used to bind the currently selected item, for example in a TableView,
     * to be shown in the BoxPlot as well.
     */
    private final ObjectProperty<T> selectedElement = new SimpleObjectProperty<>();

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

    public ReadOnlyObjectProperty<T> currentElementProperty() {
        return currentElement;
    }

    private void setCurrentElement(T currentElement) {
        this.currentElement.set(currentElement);
    }

    BoxPlot<T> getBoxPlot() {
        return boxPlot;
    }

    public T getSelectedElement() {
        return selectedElement.get();
    }

    public ObjectProperty<T> selectedElementProperty() {
        return selectedElement;
    }

    public void setSelectedElement(T selectedElement) {
        this.selectedElement.set(selectedElement);
    }
}
