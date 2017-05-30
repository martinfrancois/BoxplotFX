package ch.fhnw.cuie.project.boxplot;

import java.util.Arrays;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * @author Dieter Holz
 */
public class BusinessSkin extends SkinBase<BusinessControl> {
    private static final int IMG_SIZE   = 12;
    private static final int IMG_OFFSET = 4;

    private static final String ANGLE_DOWN = "\uf107";
    private static final String ANGLE_UP   = "\uf106";

    private enum State {
        VALID("Valid",      "valid.png"),
        INVALID("Invalid",  "invalid.png");

        public final String    text;
        public final ImageView imageView;

        State(final String text, final String file) {
            this.text = text;
            String url = BusinessSkin.class.getResource("icons/" + file).toExternalForm();
            this.imageView = new ImageView(new Image(url,
                                                     IMG_SIZE, IMG_SIZE,
                                                     true, false));
        }
    }

    private static final String FONTS_CSS = "fonts.css";
    private static final String STYLE_CSS = "style.css";

    // all parts
    private TextField editableNode;
    private Label     readOnlyNode;
    private Popup     popup;
    private Pane      dropDownChooser;
    private Button    chooserButton;

    private StackPane drawingPane;

    private Animation      invalidInputAnimation;
    private FadeTransition fadeOutValidIconAnimation;

    public BusinessSkin(BusinessControl control) {
        super(control);
        initializeSelf();
        initializeParts();
        layoutParts();
        setupAnimations();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    private void initializeSelf() {
        String fonts = getClass().getResource(FONTS_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(fonts);

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getSkinnable().getStylesheets().add(stylesheet);
    }

    private void initializeParts() {
        editableNode = new TextField();
        editableNode.getStyleClass().add("editableNode");

        readOnlyNode = new Label();
        readOnlyNode.getStyleClass().add("readOnlyNode");

        State.VALID.imageView.setOpacity(0.0);

        chooserButton = new Button(ANGLE_DOWN);
        chooserButton.getStyleClass().add("chooserButton");

        dropDownChooser = new DropDownChooser(getSkinnable());

        popup = new Popup();
        popup.getContent().addAll(dropDownChooser);

        drawingPane = new StackPane();
        drawingPane.getStyleClass().add("drawingPane");
    }

    private void layoutParts() {
        StackPane.setAlignment(chooserButton, Pos.CENTER_RIGHT);
        drawingPane.getChildren().addAll(editableNode, chooserButton, readOnlyNode);

        Arrays.stream(State.values())
              .map(state -> state.imageView)
              .forEach(imageView -> {
                  imageView.setManaged(false);
                  drawingPane.getChildren().add(imageView);
              });

        StackPane.setAlignment(editableNode, Pos.CENTER_LEFT);
        StackPane.setAlignment(readOnlyNode, Pos.CENTER_LEFT);

        getChildren().add(drawingPane);
    }

    private void setupAnimations() {
        int      delta    = 5;
        Duration duration = Duration.millis(30);

        TranslateTransition moveRight = new TranslateTransition(duration, editableNode);
        moveRight.setFromX(0.0);
        moveRight.setByX(delta);
        moveRight.setAutoReverse(true);
        moveRight.setCycleCount(2);
        moveRight.setInterpolator(Interpolator.LINEAR);

        TranslateTransition moveLeft = new TranslateTransition(duration, editableNode);
        moveLeft.setFromX(0.0);
        moveLeft.setByX(-delta);
        moveLeft.setAutoReverse(true);
        moveLeft.setCycleCount(2);
        moveLeft.setInterpolator(Interpolator.LINEAR);

        invalidInputAnimation = new SequentialTransition(moveRight, moveLeft);
        invalidInputAnimation.setCycleCount(3);

        fadeOutValidIconAnimation = new FadeTransition(Duration.millis(500), State.VALID.imageView);
        fadeOutValidIconAnimation.setDelay(Duration.seconds(1));
        fadeOutValidIconAnimation.setFromValue(1.0);
        fadeOutValidIconAnimation.setToValue(0.0);
    }

    private void setupEventHandlers() {
        chooserButton.setOnAction(event -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
                popup.show(editableNode.getScene().getWindow());
            }
        });

        popup.setOnHidden(event -> chooserButton.setText(ANGLE_DOWN));

        popup.setOnShown(event -> {
            chooserButton.setText(ANGLE_UP);
            Point2D location = editableNode.localToScreen(editableNode.getWidth() - dropDownChooser.getPrefWidth() - 3,
                                                          editableNode.getHeight() -3);

            popup.setX(location.getX());
            popup.setY(location.getY());
        });

        editableNode.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    getSkinnable().reset();
                    event.consume();
                    break;
                case UP:
                    getSkinnable().increase();
                    event.consume();
                    break;
                case DOWN:
                    getSkinnable().decrease();
                    event.consume();
                    break;
            }
        });
    }

    private void setupValueChangedListeners() {
        getSkinnable().invalidProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                startInvalidInputAnimation();
            } else {
                State.VALID.imageView.setOpacity(1.0);
                startFadeOutValidIconTransition();
            }
        });
    }

    private void setupBindings() {
        readOnlyNode.textProperty().bind(getSkinnable().valueProperty().asString(BusinessControl.FORMATTED_INTEGER_PATTERN));
        editableNode.textProperty().bindBidirectional(getSkinnable().userFacingTextProperty());

        editableNode.promptTextProperty().bind(getSkinnable().labelProperty());

        editableNode.visibleProperty().bind(getSkinnable().readOnlyProperty().not());
        chooserButton.visibleProperty().bind(getSkinnable().readOnlyProperty().not());
        readOnlyNode.visibleProperty().bind(getSkinnable().readOnlyProperty());

        State.INVALID.imageView.visibleProperty().bind(getSkinnable().invalidProperty());

        State.INVALID.imageView.xProperty().bind(editableNode.translateXProperty().add(editableNode.layoutXProperty()).subtract(IMG_OFFSET));
        State.INVALID.imageView.yProperty().bind(editableNode.translateYProperty().add(editableNode.layoutYProperty()).subtract(IMG_OFFSET));
        State.VALID.imageView.xProperty().bind(editableNode.layoutXProperty().subtract(IMG_OFFSET));
        State.VALID.imageView.yProperty().bind(editableNode.layoutYProperty().subtract(IMG_OFFSET));
    }

    private void startFadeOutValidIconTransition() {
        if (fadeOutValidIconAnimation.getStatus().equals(Animation.Status.RUNNING)) {
            return;
        }
        fadeOutValidIconAnimation.play();
    }

    private void startInvalidInputAnimation() {
        if (invalidInputAnimation.getStatus().equals(Animation.Status.RUNNING)) {
            invalidInputAnimation.stop();
        }
        invalidInputAnimation.play();
    }


}
