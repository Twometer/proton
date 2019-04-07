package de.twometer.proton;

import de.twometer.proton.res.ResourceLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ProtonApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(ResourceLoader.getResource("layout/main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ResourceLoader.getResource("css/java.css").toExternalForm());
        primaryStage.getIcons().add(new Image(ResourceLoader.getResourceAsStream("icons/main.png")));
        primaryStage.setTitle(String.format("%s v%s", BuildInfo.NAME_SHORT, BuildInfo.VERSION));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
