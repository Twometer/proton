package de.twometer.proton.decompiler;

import com.strobel.assembler.metadata.DeobfuscationUtilities;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;

import java.io.StringWriter;

public class ProcyonDecompiler {

    private DecompilerContext decompilerContext;

    private DecompilerSettings decompilerSettings;

    private DecompilationOptions decompilationOptions;

    public ProcyonDecompiler(ITypeLoader typeLoader) {
        this.decompilerSettings = DecompilerSettings.javaDefaults();
        this.decompilerSettings.setTypeLoader(typeLoader);
        this.decompilerContext = new DecompilerContext(typeLoader);

        if (this.decompilerSettings.getJavaFormattingOptions() == null)
            this.decompilerSettings.setJavaFormattingOptions(JavaFormattingOptions.createDefault());

        decompilationOptions = new DecompilationOptions();
        decompilationOptions.setSettings(decompilerSettings);
        decompilationOptions.setFullDecompilation(true);
    }

    public DecompiledClass decompile(String internalName) {
        TypeReference typeReference = decompilerContext.getMetadataSystem().lookupType(internalName);
        TypeDefinition typeDefinition = typeReference.resolve();
        DeobfuscationUtilities.processType(typeDefinition);

        StringWriter writer = new StringWriter();
        decompilerSettings.getLanguage().decompileType(typeDefinition, new PlainTextOutput(writer), decompilationOptions);

        return new DecompiledClass(writer.toString(), typeDefinition);
    }

    public DecompilerContext getDecompilerContext() {
        return decompilerContext;
    }
}
