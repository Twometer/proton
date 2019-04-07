package de.twometer.proton.gui;

import com.strobel.assembler.metadata.MethodDefinition;
import de.twometer.proton.decompiler.DecompiledClass;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.recompiler.classBuilder.IClassBuilder;
import de.twometer.proton.recompiler.classBuilder.InjectEmptyClassBuilder;

public class EditorController {

    public JavaCodeArea textAreaCode;

    private ProcyonDecompiler decompiler;

    private DecompiledClass currentClass;

    private MethodDefinition methodDefinition;

    void setDecompiler(ProcyonDecompiler decompiler) {
        this.decompiler = decompiler;
    }

    void setCurrentClass(DecompiledClass currentClass) {
        this.currentClass = currentClass;
    }

    void setMethodDefinition(MethodDefinition methodDefinition) {
        this.methodDefinition = methodDefinition;
    }

    void setup() {
        String method = decompiler.decompile(methodDefinition);
        textAreaCode.replaceText(method);
    }

    public void onCancel() {
        textAreaCode.getScene().getWindow().hide();
    }

    public void onCompile() {
        IClassBuilder builder = new InjectEmptyClassBuilder(decompiler, methodDefinition, textAreaCode.getText());
        textAreaCode.replaceText(builder.buildSource());
    }
}
