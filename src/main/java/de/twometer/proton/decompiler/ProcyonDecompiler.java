package de.twometer.proton.decompiler;

import com.strobel.assembler.metadata.*;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import com.strobel.decompiler.languages.java.JavaLanguage;
import com.strobel.decompiler.languages.java.JavaOutputVisitor;
import com.strobel.decompiler.languages.java.ast.*;

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

    public String decompile(MethodDefinition method) {
        StringWriter writer = new StringWriter();
        CompilationUnit unit = ((JavaLanguage) decompilerSettings.getLanguage()).decompileTypeToAst(method.getDeclaringType(), decompilationOptions);
        TypeDeclaration declaration = unit.getTypes().firstOrNullObject();
        JavaOutputVisitor outputVisitor = new JavaOutputVisitor(new PlainTextOutput(writer), decompilerSettings);
        for (EntityDeclaration entity : declaration.getMembers()) {
            if (entity instanceof MethodDeclaration) {
                if (isDefinitionOf(method, (MethodDeclaration) entity))
                    outputVisitor.visitMethodDeclaration((MethodDeclaration) entity, null);
            } else if (entity instanceof ConstructorDeclaration) {
                if (isDefinitionOf(method, (ConstructorDeclaration) entity))
                    outputVisitor.visitConstructorDeclaration((ConstructorDeclaration) entity, null);
            }
        }

        return writer.toString();
    }

    private boolean methodsEqual(MethodDefinition definition, String name, AstNodeCollection<ParameterDeclaration> parameters) {
        if (!definition.getName().equals(name))
            return false;
        if (definition.getParameters().size() != parameters.size())
            return false;
        int idx = 0;
        for (ParameterDeclaration parameterDeclaration : parameters) {
            ParameterDefinition parameterDefinition = definition.getParameters().get(idx);
            if (!parameterDefinition.getParameterType().getName().equals(parameterDeclaration.getType().getUserData(Keys.TYPE_REFERENCE).getName()))
                return false;
            idx++;
        }
        return true;
    }

    private boolean isDefinitionOf(MethodDefinition definition, MethodDeclaration declaration) {
        return methodsEqual(definition, declaration.getName(), declaration.getParameters());
    }

    private boolean isDefinitionOf(MethodDefinition definition, ConstructorDeclaration declaration) {
        return methodsEqual(definition, "<init>", declaration.getParameters());
    }

    public DecompilerContext getDecompilerContext() {
        return decompilerContext;
    }
}
