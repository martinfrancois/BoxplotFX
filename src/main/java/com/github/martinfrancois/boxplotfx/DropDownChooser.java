package com.github.martinfrancois.boxplotfx;

import java.util.logging.Logger;
import javafx.scene.layout.VBox;

/**
 * @author Dieter Holz
 */
public class DropDownChooser<T> extends VBox {
    private final static Logger LOGGER = Logger.getLogger(DropDownChooser.class.getName());

    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "dropDownChooser.css";

    private final BoxPlotControl<T> boxPlotControl;

    public DropDownChooser(BoxPlotControl<T> boxPlotControl) {
        this.boxPlotControl = boxPlotControl;
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
        getChildren().addAll(boxPlotControl);
    }

    private void setupBindings() {
    }
}
