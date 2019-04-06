package de.twometer.proton.gui;

import de.twometer.proton.BuildInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ProtonUiApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("main.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle(String.format("%s v%s", BuildInfo.NAME_SHORT, BuildInfo.VERSION));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
