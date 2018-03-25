package com.github.martinfrancois.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * @author Dieter Holz
 */
public class DemoStarter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Region rootPanel = new DemoPane();

        Scene scene = new Scene(rootPanel);

        primaryStage.setTitle("Business Control Demo");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
