package ch.fhnw.cuie.project.boxplot;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Dieter Holz
 */
public class DropDownChooser extends VBox {
    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "dropDownChooser.css";

    private final BusinessControl businessControl;

    private Label tobeReplacedLabel;

    public DropDownChooser(BusinessControl businessControl) {
        this.businessControl = businessControl;
        initializeSelf();
        initializeParts();
        layoutParts();
        setupBindings();
    }

    private void initializeSelf() {
        getStyleClass().add("dropDownChooser");

        String fonts = getClass().getResource(FONTS_CSS).toExternalForm();
        getStylesheets().add(fonts);

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getStylesheets().add(stylesheet);
    }

    private void initializeParts() {
        tobeReplacedLabel = new Label("to be replaced");
    }

    private void layoutParts() {
        getChildren().addAll(new BoxPlotControl<String>(FXCollections.observableHashMap()));
    }

    private void setupBindings() {
    }
}
