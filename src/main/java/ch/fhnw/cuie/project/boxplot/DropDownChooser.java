package ch.fhnw.cuie.project.boxplot;

import javafx.scene.layout.VBox;

/**
 * @author Dieter Holz
 */
public class DropDownChooser<T> extends VBox {
    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "dropDownChooser.css";

    private final BusinessControl<T> businessControl;

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

    }

    private void layoutParts() {
        getChildren().addAll(new BoxPlotControl<>(businessControl.getMap()));
    }

    private void setupBindings() {
    }
}
