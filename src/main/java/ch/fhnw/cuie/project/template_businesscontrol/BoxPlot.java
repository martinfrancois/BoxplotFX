package ch.fhnw.cuie.project.template_businesscontrol;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.regex.Pattern;

/**
 * @author Dieter Holz
 */
public class BoxPlot extends Control {
    // ----- Properties --------------------------------
    private static final DoubleProperty lowerWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty upperWhisker = new SimpleDoubleProperty();
    private static final DoubleProperty median = new SimpleDoubleProperty();
    private static final DoubleProperty lowerQuartil = new SimpleDoubleProperty();
    private static final DoubleProperty upperQuartil = new SimpleDoubleProperty();
    private static final DoubleProperty minElement = new SimpleDoubleProperty();
    private static final DoubleProperty maxElement = new SimpleDoubleProperty();




    static final String FORMATTED_INTEGER_PATTERN = "%,d";

    private static final String INTEGER_REGEX    = "[+-]?[\\d']{1,14}";
    private static final Pattern INTEGER_PATTERN = Pattern.compile(INTEGER_REGEX);

    private final IntegerProperty value = new SimpleIntegerProperty();

    private static final PseudoClass MANDATORY_CLASS = PseudoClass.getPseudoClass("mandatory");
    private static final PseudoClass INVALID_CLASS   = PseudoClass.getPseudoClass("invalid");

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

    private final BooleanProperty readOnly     = new SimpleBooleanProperty();
    private final StringProperty  label        = new SimpleStringProperty();
    private final StringProperty  errorMessage = new SimpleStringProperty();

    private final StringProperty userFacingText = new SimpleStringProperty();

    public BoxPlot() {
        initializeSelf();
        addValueChangeListener();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BusinessSkin(this);
    }

    public void reset() {
        setUserFacingText(convertToString(getValue()));
    }

    public void increase() {
        setValue(getValue() + 1);
    }

    public void decrease() {
        setValue(getValue() - 1);
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

            if (isInteger(userInput)) {
                setInvalid(false);
                setErrorMessage(null);
                setValue(convertToInt(userInput));
            } else {
                setInvalid(true);
                setErrorMessage("Not an Integer");
            }
        });

        valueProperty().addListener((observable, oldValue, newValue) -> {
            setUserFacingText(convertToString(newValue.intValue()));
        });
    }

    private boolean isInteger(String userInput) {
        return INTEGER_PATTERN.matcher(userInput).matches();
    }

    private int convertToInt(String userInput) {
        return Integer.parseInt(userInput);
    }

    private String convertToString(int newValue) {
        return String.format(FORMATTED_INTEGER_PATTERN, newValue);
    }

    // all the getters and setters

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
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








    // ------- Properties -------------------------------------------
    public static double getLowerWhisker() {
        return lowerWhisker.get();
    }

    public static DoubleProperty lowerWhiskerProperty() {
        return lowerWhisker;
    }

    public static void setLowerWhisker(double lowerWhisker) {
        BoxPlot.lowerWhisker.set(lowerWhisker);
    }

    public static double getUpperWhisker() {
        return upperWhisker.get();
    }

    public static DoubleProperty upperWhiskerProperty() {
        return upperWhisker;
    }

    public static void setUpperWhisker(double upperWhisker) {
        BoxPlot.upperWhisker.set(upperWhisker);
    }

    public static double getMedian() {
        return median.get();
    }

    public static DoubleProperty medianProperty() {
        return median;
    }

    public static void setMedian(double median) {
        BoxPlot.median.set(median);
    }

    public static double getLowerQuartil() {
        return lowerQuartil.get();
    }

    public static DoubleProperty lowerQuartilProperty() {
        return lowerQuartil;
    }

    public static void setLowerQuartil(double lowerQuartil) {
        BoxPlot.lowerQuartil.set(lowerQuartil);
    }

    public static double getUpperQuartil() {
        return upperQuartil.get();
    }

    public static DoubleProperty upperQuartilProperty() {
        return upperQuartil;
    }

    public static void setUpperQuartil(double upperQuartil) {
        BoxPlot.upperQuartil.set(upperQuartil);
    }

    public static double getMinElement() {
        return minElement.get();
    }

    public static DoubleProperty minElementProperty() {
        return minElement;
    }

    public static void setMinElement(double minElement) {
        BoxPlot.minElement.set(minElement);
    }

    public static double getMaxElement() {
        return maxElement.get();
    }

    public static DoubleProperty maxElementProperty() {
        return maxElement;
    }

    public static void setMaxElement(double maxElement) {
        BoxPlot.maxElement.set(maxElement);
    }
}
