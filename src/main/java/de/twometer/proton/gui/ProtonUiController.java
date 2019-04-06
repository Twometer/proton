package de.twometer.proton.gui;

import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import de.twometer.proton.BuildInfo;
import de.twometer.proton.jar.loader.JarLoader;
import de.twometer.proton.jar.loader.PathType;
import de.twometer.proton.jar.node.CondensedPackageNode;
import de.twometer.proton.jar.node.JarEntryNode;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.node.JarNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.Objects;

public class ProtonUiController {

    public TreeView<JarNode> treeViewMain;
    public TextArea textAreaCode;
    public Label status;
    private Image jarImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("jar.png")));
    private Image packageImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("package.png")));
    private Image classImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("class.png")));
    private Image methodImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("method.png")));
    private JarFileNode currentJar;

    @FXML
    public void initialize() {
        treeViewMain.setOnMouseClicked(event -> {
            TreeItem<JarNode> selectedItem;
            JarNode selectedNode;
            if (event.getClickCount() == 2 && (selectedItem = treeViewMain.getSelectionModel().getSelectedItem()) != null) {
                selectedNode = selectedItem.getValue();
                if (selectedNode.getPathInfo().getPathType() != PathType.CLASS) return;
                if (selectedNode.getPathInfo().getPath().isEmpty()) return;
                StringWriter writer = new StringWriter();
                DecompilerSettings settings = DecompilerSettings.javaDefaults();
                settings.setTypeLoader(currentJar.getTypeLoader());
                Decompiler.decompile(selectedNode.getPathInfo().getTypeName(), new PlainTextOutput(writer), settings);
                textAreaCode.setText(writer.toString());
            }
        });
    }

    public void onOpenJar(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JAR file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Archive", "*.jar"));
        File file = fileChooser.showOpenDialog(treeViewMain.getScene().getWindow());

        JarLoader loader = new JarLoader();

        try {
            currentJar = loader.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText("Could not read the given JAR file. Please make sure it is a valid JAR file.");
            alert.show();
            return;
        }

        TreeItem<JarNode> root = new TreeItem<>();
        copyNodes(currentJar, root);
        sortNodes(root);
        treeViewMain.setRoot(root.getChildren().get(0));
    }

    private void sortNodes(TreeItem<JarNode> dst) {
        dst.getChildren().sort(Comparator.comparing(a -> a.getValue().getPathInfo().getPathType()));
        for (TreeItem<JarNode> child : dst.getChildren())
            sortNodes(child);
    }

    private void copyNodes(JarNode src, TreeItem<JarNode> dst) {
        TreeItem<JarNode> newItem;
        
        if (src.getPathInfo().getPathType() == PathType.JAR)
            newItem = new TreeItem<>(src, new ImageView(jarImage));
        else if (src.getPathInfo().getPathType() == PathType.PACKAGE)
            newItem = new TreeItem<>(src, new ImageView(packageImage));
        else if (src.getPathInfo().getPathType() == PathType.CLASS)
            newItem = new TreeItem<>(src, new ImageView(classImage));
        else throw new IllegalArgumentException("Unknown path type");

        dst.getChildren().add(newItem);
        if (src.getPathInfo().getPathType() == PathType.PACKAGE && src.getChildren().size() == 1)
            newItem.setValue(CondensedPackageNode.createFrom((JarEntryNode) newItem.getValue()));
        while (src.getPathInfo().getPathType() == PathType.PACKAGE && src.getChildren().size() == 1 && src.getChildren().get(0).getPathInfo().getPathType() == PathType.PACKAGE) {
            src = src.getChildren().get(0);
            ((CondensedPackageNode) newItem.getValue()).appendPackage(src.getPathInfo().getName());
        }
        for (JarNode child : src.getChildren()) copyNodes(child, newItem);
    }

    public void onAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(String.format("About %s", BuildInfo.NAME_SHORT));
        alert.setHeaderText(String.format("%s v%s", BuildInfo.NAME_LONG, BuildInfo.VERSION));
        alert.setContentText("(c) 2019 Twometer Applications");
        alert.show();
    }
}
