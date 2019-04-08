package de.twometer.proton.recompiler.classBuilder;

import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.TypeDefinition;
import de.twometer.proton.decompiler.ProcyonDecompiler;

public class ClassBuilderFactory {

    public static IClassBuilder buildInjected(ProcyonDecompiler decompiler, MethodDefinition definition, String code) {
        return new InjectEmptyClassBuilder(decompiler, definition, code);
    }

    public static IClassBuilder buildDefault(ProcyonDecompiler decompiler, TypeDefinition definition) {
        return new EmptyClassBuilder(decompiler, definition);
    }

}
