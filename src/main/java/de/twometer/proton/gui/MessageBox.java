package de.twometer.proton.gui;

import de.twometer.proton.res.ResourceLoader;
import javafx.scene.control.Alert;

class MessageBox {

    static void show(Alert.AlertType type, String title, String header, String content, boolean monospaceFont) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (monospaceFont)
            alert.getDialogPane().getStylesheets().add(ResourceLoader.getResource("css/mono.css").toExternalForm());
        alert.show();
    }

    static void show(Alert.AlertType type, String title, String header, String content) {
        show(type, title, header, content, false);
    }


}
