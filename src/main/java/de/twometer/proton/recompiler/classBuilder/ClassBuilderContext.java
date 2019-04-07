package de.twometer.proton.recompiler.classBuilder;

import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaLanguage;
import com.strobel.decompiler.languages.java.ast.CompilationUnit;
import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.recompiler.util.JavaOutputVisitorEx;

import java.io.StringWriter;

class ClassBuilderContext {

    private StringWriter stringWriter;

    private CompilationUnit compilationUnit;

    private TypeDeclaration typeDeclaration;

    private JavaOutputVisitorEx visitor;

    private ClassBuilderContext(StringWriter stringWriter, CompilationUnit compilationUnit, TypeDeclaration typeDeclaration, JavaOutputVisitorEx visitor) {
        this.stringWriter = stringWriter;
        this.compilationUnit = compilationUnit;
        this.typeDeclaration = typeDeclaration;
        this.visitor = visitor;
    }

    static ClassBuilderContext create(ProcyonDecompiler decompiler, TypeDefinition def) {
        DecompilationOptions options = decompiler.getDecompilationOptions();
        DecompilerSettings settings = options.getSettings();
        CompilationUnit unit = new JavaLanguage().decompileTypeToAst(def, options);
        TypeDeclaration declaration = unit.getTypes().firstOrNullObject();
        StringWriter writer = new StringWriter();
        JavaOutputVisitorEx outputVisitor = new JavaOutputVisitorEx(new PlainTextOutput(writer), settings);
        return new ClassBuilderContext(writer, unit, declaration, outputVisitor);
    }

    StringWriter getStringWriter() {
        return stringWriter;
    }

    CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    JavaOutputVisitorEx getVisitor() {
        return visitor;
    }
}
