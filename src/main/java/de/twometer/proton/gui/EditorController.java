package de.twometer.proton.gui;

import com.strobel.assembler.metadata.MethodDefinition;
import de.twometer.proton.decompiler.DecompiledClass;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.jar.node.JarEntryNode;
import de.twometer.proton.jar.node.JarFileNode;
import de.twometer.proton.jar.writer.JarWriter;
import de.twometer.proton.recompiler.CompilerResult;
import de.twometer.proton.recompiler.DummyJarBuilder;
import de.twometer.proton.recompiler.Recompiler;
import de.twometer.proton.transformer.InjectingTransformer;
import javafx.scene.control.Alert;
import org.apache.commons.io.IOUtils;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

public class EditorController {

    public JavaCodeArea textAreaCode;

    private ProcyonDecompiler decompiler;

    private JarFileNode curentJar;

    private JarEntryNode currentJarEntry;

    private DecompiledClass currentClass;

    private MethodDefinition methodDefinition;

    private Recompiler recompiler;

    private JarWriter jarWriter;

    void setCurentJar(JarFileNode curentJar) {
        this.curentJar = curentJar;
    }

    void setCurrentJarEntry(JarEntryNode currentJarEntry) {
        this.currentJarEntry = currentJarEntry;
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

    void setJarWriter(JarWriter jarWriter) {
        this.jarWriter = jarWriter;
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
        DummyJarBuilder jarBuilder = new DummyJarBuilder(decompiler, curentJar, methodDefinition, textAreaCode.getText());
        List<JavaFileObject> sources = jarBuilder.buildSources();

        CompilerResult result = recompiler.compile(currentClass.getTypeDefinition().getFullName(), sources);
        if (!result.isSuccessful()) {
            MessageBox.show(Alert.AlertType.ERROR, "Error", "Failed to compile", "Could not compile modified method. See the error list for more details.");
            for (Diagnostic d : result.getDiagnostics().getDiagnostics()) {
                System.out.println("===");
                System.out.println(d);
            }

        } else {

            byte[] originalClass = null;
            try {
                originalClass = IOUtils.toByteArray(curentJar.getJarFile().getInputStream(currentJarEntry.getJarEntry()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            InjectingTransformer transformer = new InjectingTransformer(originalClass, result.getClassFile(), methodDefinition);

            String modifiedClassPath = currentClass.getTypeDefinition().getInternalName() + ".class";
            byte[] modifiedClass = transformer.createClassFile();

            jarWriter.overwriteClass(modifiedClassPath, modifiedClass);
            MessageBox.show(Alert.AlertType.INFORMATION, "Success", "Compiled successfully", "Method has been compiled successfully. Modified bytecode will be written when you export a new JAR file.");
        }
    }

}
