package ch.fhnw.cuie.project.template_businesscontrol.demo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import ch.fhnw.cuie.project.template_businesscontrol.BusinessControl;

/**
 * @author Dieter Holz
 */
public class DemoPane extends BorderPane {
    private BusinessControl businessControl;

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

        businessControl = new BusinessControl();

        ageSlider = new Slider(0, 130, 0);

        readOnlyBox = new CheckBox();
        readOnlyBox.setSelected(false);

        mandatoryBox = new CheckBox();
        mandatoryBox.setSelected(true);

        labelField = new TextField();
    }

    private void layoutControls() {
        setCenter(businessControl);
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

        businessControl.valueProperty().bindBidirectional(age);
        businessControl.labelProperty().bind(ageLabel);
        businessControl.readOnlyProperty().bind(readOnly);
        businessControl.mandatoryProperty().bind(mandatory);
    }

}
