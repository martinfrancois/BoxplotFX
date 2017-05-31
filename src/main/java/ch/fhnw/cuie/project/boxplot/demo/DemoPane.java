package ch.fhnw.cuie.project.boxplot.demo;

import ch.fhnw.cuie.project.boxplot.BoxPlotControl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import ch.fhnw.cuie.project.boxplot.BusinessControl;

/**
 * @author Dieter Holz
 */
public class DemoPane extends BorderPane {
//    private BusinessControl boxPlotControl;
    private BoxPlotControl boxPlotControl;

    private Slider ageSlider;

    private CheckBox  readOnlyBox;
    private CheckBox  mandatoryBox;
    private TextField labelField;

    // the value Property that needs to be set by the BusinessControl
    private final IntegerProperty age      = new SimpleIntegerProperty(42);
    private final StringProperty  ageLabel = new SimpleStringProperty("Age");
    private final BooleanProperty readOnly = new SimpleBooleanProperty(false);
    private final BooleanProperty mandatory = new SimpleBooleanProperty(true);

    public DemoPane() {
        initializeControls();
        layoutControls();
        setupValueChangeListeners();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

//        boxPlotControl = new BusinessControl();
        boxPlotControl = new BoxPlotControl(FXCollections.observableHashMap());

        ageSlider = new Slider(0, 130, 0);

        readOnlyBox = new CheckBox();
        readOnlyBox.setSelected(false);

        mandatoryBox = new CheckBox();
        mandatoryBox.setSelected(true);

        labelField = new TextField();

    }

    private void layoutControls() {
        setCenter(boxPlotControl);
        VBox box = new VBox(10, new Label("Business Control Properties"),
                            new Label("Age"), ageSlider,
                            new Label("readOnly"), readOnlyBox,
                            new Label("mandatory"), mandatoryBox,
                            new Label("Label"), labelField);

        box.setPadding(new Insets(10));
        box.setSpacing(10);
        setRight(box);
    }

    private void setupValueChangeListeners() {
    }

    private void setupBindings() {
        ageSlider.valueProperty().bindBidirectional(age);
        labelField.textProperty().bindBidirectional(ageLabel);
        readOnlyBox.selectedProperty().bindBidirectional(readOnly);
        mandatoryBox.selectedProperty().bindBidirectional(mandatory);

        boxPlotControl.valueProperty().bindBidirectional(age);
        boxPlotControl.labelProperty().bind(ageLabel);
        boxPlotControl.readOnlyProperty().bind(readOnly);
        boxPlotControl.mandatoryProperty().bind(mandatory);
    }

}
