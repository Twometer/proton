package de.twometer.proton.gui;

import com.strobel.assembler.metadata.MethodDefinition;
import de.twometer.proton.BuildInfo;
import de.twometer.proton.decompiler.DecompiledClass;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.jar.loader.JarLoader;
import de.twometer.proton.jar.loader.PathType;
import de.twometer.proton.jar.node.CondensedPackageNode;
import de.twometer.proton.jar.node.JarEntryNode;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.node.JarNode;
import de.twometer.proton.recompiler.Recompiler;
import de.twometer.proton.res.ResourceLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class ProtonUiController {

    public TreeView<JarNode> treeViewMain;
    public CodeArea textAreaCode;
    public Label status;
    public ListView<MethodDefinition> methodsListView;

    public MenuItem editAsJava;
    public MenuItem editAsBytecode;
    public MenuItem findUsages;

    private Image jarImage = new Image(ResourceLoader.getResourceAsStream("icons/jar.png"));
    private Image packageImage = new Image(ResourceLoader.getResourceAsStream("icons/package.png"));
    private Image classImage = new Image(ResourceLoader.getResourceAsStream("icons/class.png"));
    private Image methodImage = new Image(ResourceLoader.getResourceAsStream("icons/method.png"));

    private Recompiler recompiler;
    private ProcyonDecompiler decompiler;
    private DecompiledClass currentClass;
    private JarNode currentJar;

    @FXML
    public void initialize() {
        recompiler = new Recompiler();

        treeViewMain.setOnMouseClicked(event -> {
            TreeItem<JarNode> selectedItem;
            JarNode selectedNode;
            if (event.getClickCount() == 2 && (selectedItem = treeViewMain.getSelectionModel().getSelectedItem()) != null) {
                selectedNode = selectedItem.getValue();
                if (selectedNode.getPathInfo().getPathType() != PathType.CLASS) return;
                if (selectedNode.getPathInfo().getPath().isEmpty()) return;
                currentClass = decompiler.decompile(selectedNode.getPathInfo().getTypeName());
                textAreaCode.replaceText(currentClass.getCode());
                methodsListView.getItems().clear();
                for (MethodDefinition methodDefinition : currentClass.getTypeDefinition().getDeclaredMethods())
                    methodsListView.getItems().add(methodDefinition);
            }
        });

        methodsListView.setCellFactory(param -> new ListCell<MethodDefinition>() {
            ImageView imageView = new ImageView();

            @Override
            protected void updateItem(MethodDefinition item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    imageView.setImage(methodImage);
                    setGraphic(imageView);
                    setText(item.getSimpleDescription());
                } else {
                    setGraphic(null);
                    setText("");
                }
            }
        });

        methodsListView.setOnContextMenuRequested(event -> {
            MethodDefinition selectedItem = methodsListView.getSelectionModel().getSelectedItem();
            editAsJava.setDisable(selectedItem == null);
            editAsBytecode.setDisable(selectedItem == null);
            findUsages.setDisable(selectedItem == null);
        });

        methodsListView.setOnMouseClicked(event -> {
            MethodDefinition selectedItem = methodsListView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2 && selectedItem != null)
                textAreaCode.replaceText(decompiler.decompile(selectedItem));
        });

        textAreaCode.setEditable(false);
    }

    @FXML
    public void onOpenJar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JAR file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Archive", "*.jar"));
        File file = fileChooser.showOpenDialog(treeViewMain.getScene().getWindow());

        JarLoader loader = new JarLoader();
        try {
            loadJar(loader.load(file));
        } catch (IOException e) {
            e.printStackTrace();
            MessageBox.show(Alert.AlertType.ERROR, "Error", "Failed to load JAR file", "Please make sure that you load a valid JAR file.");
        }
    }

    @FXML
    public void onEditAsJava() throws IOException {
        MethodDefinition selectedMethod = methodsListView.getSelectionModel().getSelectedItem();
        if (selectedMethod == null)
            return;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(ResourceLoader.getResource("layout/editor.fxml"));
        Parent root = loader.load();
        EditorController controller = loader.getController();
        controller.setDecompiler(decompiler);
        controller.setCurrentClass(currentClass);
        controller.setMethodDefinition(selectedMethod);
        controller.setRecompiler(recompiler);
        controller.setCurentJar(currentJar);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ResourceLoader.getResource("css/java.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Java Editor");
        stage.show();
        controller.setup();
    }

    @FXML
    public void onEditAsBytecode() {

    }

    @FXML
    public void onFindUsages() {

    }

    private void loadJar(JarFileNode jar) {
        TreeItem<JarNode> root = new TreeItem<>();
        copyNodes(jar, root);
        sortNodes(root);
        treeViewMain.setRoot(root.getChildren().get(0));

        decompiler = new ProcyonDecompiler(jar.getTypeLoader());
        currentJar = jar;
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
        while (src.getPathInfo().getPathType() == PathType.PACKAGE && src.getChildren().size() == 1 && src.getChildren().get(0).getPathInfo().getPathType() == PathType.PACKAGE) {
            if (!(newItem.getValue() instanceof CondensedPackageNode))
                newItem.setValue(CondensedPackageNode.createFrom((JarEntryNode) newItem.getValue()));
            src = src.getChildren().get(0);
            ((CondensedPackageNode) newItem.getValue()).appendPackage(src.getPathInfo().getName());
        }
        for (JarNode child : src.getChildren()) copyNodes(child, newItem);
    }

    @FXML
    public void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(String.format("About %s", BuildInfo.NAME_SHORT));
        alert.setHeaderText(String.format("%s v%s", BuildInfo.NAME_LONG, BuildInfo.VERSION));
        alert.setContentText("(c) 2019 Twometer Applications");
        alert.show();
    }
}
