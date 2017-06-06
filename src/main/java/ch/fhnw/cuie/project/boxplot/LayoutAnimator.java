package ch.fhnw.cuie.project.boxplot;

import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Animates an object when its position is changed. For instance, when
 * additional items are added to a Region, and the layout has changed, then the
 * layout animator makes the transition by sliding each item into its final
 * place.
 */
public class LayoutAnimator implements ChangeListener<Number>, ListChangeListener<Line> {

    public static final int TRANSITION_DURATION_MS = 100;
    private Map<Line, TranslateTransition> lineXTransitions = new HashMap<>();
    private Map<Line, TranslateTransition> lineYTransitions = new HashMap<>();

    /**
     * Animates all the children of a Region.
     * <code>
     *   VBox myVbox = new VBox();
     *   LayoutAnimator animator = new LayoutAnimator();
     *   animator.observe(myVbox.getChildren());
     * </code>
     *
     * @param lines
     */
    public void observe(ObservableList<Line> lines) {
        for (Line line : lines) {
            this.observe(line);
        }
        lines.addListener(this);
    }

    public void unobserve(ObservableList<Line> lines) {
        lines.removeListener(this);
    }

    public void observe(Line n) {
        n.startXProperty().addListener(this);
        n.endXProperty().addListener(this);
        n.startYProperty().addListener(this);
        n.endYProperty().addListener(this);
    }

    public void unobserve(Line n) {
        n.startXProperty().removeListener(this);
        n.endXProperty().removeListener(this);
        n.startYProperty().removeListener(this);
        n.endYProperty().removeListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
        final double delta = newValue.doubleValue() - oldValue.doubleValue();
        System.out.println("Delta: " + delta);
        final DoubleProperty doubleProperty = (DoubleProperty) ov;

        final Line line = (Line) doubleProperty.getBean();
        System.out.println("Property Name: " + doubleProperty.getName());

        TranslateTransition t;
        switch (doubleProperty.getName()) {
            case  "startX":
                t = lineXTransitions.get(line);
                if (t == null) {
                    t = new TranslateTransition(Duration.millis(TRANSITION_DURATION_MS), line);
                    t.setToX(0);
                    lineXTransitions.put(line, t);
                }
                t.setFromX(line.getTranslateX() - delta);
                line.setTranslateX(line.getTranslateX() - delta);
                break;

            case  "endX":
                t = lineXTransitions.get(line);
                if (t == null) {
                    t = new TranslateTransition(Duration.millis(TRANSITION_DURATION_MS), line);
                    t.setToX(0);
                    lineXTransitions.put(line, t);
                }
                t.setFromX(line.getTranslateX() - delta);
                line.setTranslateX(line.getTranslateX() - delta);
                break;

            default: // "startY / endY"
                t = lineYTransitions.get(line);
                if (t == null) {
                    t = new TranslateTransition(Duration.millis(TRANSITION_DURATION_MS), line);
                    t.setToY(0);
                    lineYTransitions.put(line, t);
                }
                t.setFromY(line.getTranslateY() - delta);
                line.setTranslateY(line.getTranslateY() - delta);
        }

        t.playFromStart();
    }

    @Override
    public void onChanged(Change change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (Line line : (List<Line>) change.getAddedSubList()) {
                    this.observe(line);
                }
            } else if (change.wasRemoved()) {
                for (Line line : (List<Line>) change.getRemoved()) {
                    this.unobserve(line);
                }
            }
        }
    }
}
