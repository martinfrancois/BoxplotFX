package com.github.martinfrancois;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @author Dieter Holz
 */
public class BusinessControl<T> extends Control {
    private final static Logger LOGGER = Logger.getLogger(BusinessControl.class.getName());

    private final DoubleProperty value = new SimpleDoubleProperty();

    private static final PseudoClass MANDATORY_CLASS = PseudoClass.getPseudoClass("mandatory");
    private static final PseudoClass INVALID_CLASS = PseudoClass.getPseudoClass("invalid");

    private final BooleanProperty mandatory = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(MANDATORY_CLASS, get());
        }
    };

    private final BooleanProperty invalid = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(INVALID_CLASS, get());
        }
    };

    private final BooleanProperty readOnly = new SimpleBooleanProperty();
    private final StringProperty label = new SimpleStringProperty();
    private final StringProperty errorMessage = new SimpleStringProperty();

    private final StringProperty userFacingText = new SimpleStringProperty();
    private ObservableMap<T, Double> map;
    private final BoxPlotControl<T> boxPlotControl;

    public BusinessControl(ObservableMap<T, Double> map) {
        setupLogger();
        this.map = map;
        boxPlotControl = new BoxPlotControl<>(map);
        initializeSelf();
        addValueChangeListener();
    }

    private void setupLogger() {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.INFO);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BusinessSkin(this);
    }

    private void initializeSelf() {
        getStyleClass().add("businessControl");
        setUserFacingText(convertToString(getValue()));
    }

    private void addValueChangeListener() {
        userFacingText.addListener((observable, oldValue, userInput) -> {
            if (isMandatory() && (userInput == null || userInput.isEmpty())) {
                setInvalid(true);
                setErrorMessage("Mandatory Field");
                return;
            }
        });

        valueProperty().addListener((observable, oldValue, newValue) -> {
            setUserFacingText(convertToString(newValue.doubleValue()));
        });
    }

    private String convertToString(double newValue) {
        return String.valueOf(newValue);
    }

    // all the getters and setters

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    public boolean isReadOnly() {
        return readOnly.get();
    }

    public BooleanProperty readOnlyProperty() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly.set(readOnly);
    }

    public boolean isMandatory() {
        return mandatory.get();
    }

    public BooleanProperty mandatoryProperty() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory.set(mandatory);
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public boolean getInvalid() {
        return invalid.get();
    }

    public BooleanProperty invalidProperty() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    public String getUserFacingText() {
        return userFacingText.get();
    }

    public StringProperty userFacingTextProperty() {
        return userFacingText;
    }

    public void setUserFacingText(String userFacingText) {
        this.userFacingText.set(userFacingText);
    }

    public boolean isInvalid() {
        return invalid.get();
    }

    public BoxPlotControl<T> getBoxPlotControl() {
        return boxPlotControl;
    }
}
