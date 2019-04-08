package de.twometer.proton.gui;

import com.strobel.assembler.metadata.MethodDefinition;
import de.twometer.proton.decompiler.DecompiledClass;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.node.JarNode;
import de.twometer.proton.recompiler.CompilerResult;
import de.twometer.proton.recompiler.DummyJarBuilder;
import de.twometer.proton.recompiler.Recompiler;
import javafx.scene.control.Alert;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EditorController {

    public JavaCodeArea textAreaCode;

    private ProcyonDecompiler decompiler;

    private JarNode curentJar;

    private DecompiledClass currentClass;

    private MethodDefinition methodDefinition;

    private Recompiler recompiler;

    void setCurentJar(JarNode curentJar) {
        this.curentJar = curentJar;
    }

    void setDecompiler(ProcyonDecompiler decompiler) {
        this.decompiler = decompiler;
    }

    void setCurrentClass(DecompiledClass currentClass) {
        this.currentClass = currentClass;
    }

    void setMethodDefinition(MethodDefinition methodDefinition) {
        this.methodDefinition = methodDefinition;
    }

    public void setRecompiler(Recompiler recompiler) {
        this.recompiler = recompiler;
    }

    void setup() {
        String method = decompiler.decompile(methodDefinition);
        textAreaCode.replaceText(method);
    }

    public void onCancel() {
        textAreaCode.getScene().getWindow().hide();
    }

    public void onCompile() {
        DummyJarBuilder jarBuilder = new DummyJarBuilder(decompiler, (JarFileNode) curentJar, methodDefinition, textAreaCode.getText());
        List<JavaFileObject> sources = jarBuilder.buildSources();

        CompilerResult result = recompiler.compile(currentClass.getTypeDefinition().getFullName(), sources);
        if (!result.isSuccessful()) {
            MessageBox.show(Alert.AlertType.ERROR, "Error", "Failed to compile", "Could not compile modified method. See the error list for more details.");
            for (Diagnostic d : result.getDiagnostics().getDiagnostics()) {
                System.out.println("===");
                System.out.println(d);
            }
        } else {
            File file = new File("TEMP_CLASS_OUTPUT");
            try {
                FileOutputStream os = new FileOutputStream(file);
                os.write(result.getClassFile());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
