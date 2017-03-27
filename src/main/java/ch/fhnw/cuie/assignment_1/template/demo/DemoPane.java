package ch.fhnw.cuie.assignment_1.template.demo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import ch.fhnw.cuie.assignment_1.template.SimpleControl;

/**
 * @author Dieter Holz
 */
public class DemoPane extends BorderPane {

    // declare the custom control
    private SimpleControl cc;

    // Placeholders for properties of the application's PresentationModel
    private final DoubleProperty pmValue = new SimpleDoubleProperty();

    // all controls
    private Slider slider;

    public DemoPane() {
        initializeControls();
        layoutControls();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        cc = new SimpleControl();

        slider = new Slider();
        slider.setShowTickLabels(true);
    }

    private void layoutControls() {
        VBox controlPane = new VBox(new Label("SimpleControl Properties"),
                                    slider);
        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        setCenter(cc);
        setRight(controlPane);
    }

    private void setupBindings() {
        slider.valueProperty().bindBidirectional(pmValue);

        cc.valueProperty().bindBidirectional(pmValue);
    }

}
