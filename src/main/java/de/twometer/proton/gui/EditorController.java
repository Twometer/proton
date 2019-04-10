package de.twometer.proton.gui;

import de.twometer.proton.Context;
import de.twometer.proton.recompiler.CompilerResult;
import de.twometer.proton.recompiler.DummyJarBuilder;
import de.twometer.proton.transformer.InjectingTransformer;
import javafx.scene.control.Alert;
import org.apache.commons.io.IOUtils;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

public class EditorController {

    public JavaCodeArea textAreaCode;

    private Context context;

    void setContext(Context context) {
        this.context = context;
    }

    void setup() {
        String method = context.getDecompiler().decompile(context.getCurrentMethod());
        textAreaCode.replaceText(method);
    }

    public void onCancel() {
        textAreaCode.getScene().getWindow().hide();
    }

    public void onCompile() {
        DummyJarBuilder jarBuilder = new DummyJarBuilder(context.getDecompiler(), context.getCurrentJar(), context.getCurrentMethod(), textAreaCode.getText());
        List<JavaFileObject> sources = jarBuilder.buildSources();

        CompilerResult result = context.getRecompiler().compile(context.getCurrentClass().getTypeDefinition().getFullName(), sources);
        if (!result.isSuccessful()) {
            MessageBox.show(Alert.AlertType.ERROR, "Error", "Failed to compile", "Could not compile modified method. See the error list for more details.");
            for (Diagnostic d : result.getDiagnostics().getDiagnostics()) {
                System.out.println("===");
                System.out.println(d);
            }

        } else {

            byte[] originalClass = null;
            try {
                originalClass = IOUtils.toByteArray(context.getCurrentJar().getJarFile().getInputStream(context.getCurrentJarEntry().getJarEntry()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            InjectingTransformer transformer = new InjectingTransformer(originalClass, result.getClassFile(), context.getCurrentMethod());

            String modifiedClassPath = context.getCurrentClass().getTypeDefinition().getInternalName() + ".class";
            byte[] modifiedClass = transformer.createClassFile();

            context.getClassCache().overwrite(modifiedClassPath, modifiedClass);
            MessageBox.show(Alert.AlertType.INFORMATION, "Success", "Compiled successfully", "Method has been compiled successfully. Modified bytecode will be written when you export a new JAR file.");
            textAreaCode.getScene().getWindow().hide();
        }
    }

}
