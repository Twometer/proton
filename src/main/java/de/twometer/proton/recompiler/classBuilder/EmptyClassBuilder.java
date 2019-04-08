package de.twometer.proton.recompiler.classBuilder;

import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.languages.java.ast.*;
import de.twometer.proton.decompiler.ProcyonDecompiler;

public class EmptyClassBuilder implements IClassBuilder {

    private ProcyonDecompiler decompiler;

    private TypeDefinition typeDefinition;

    EmptyClassBuilder(ProcyonDecompiler decompiler, TypeDefinition definition) {
        this.decompiler = decompiler;
        this.typeDefinition = definition;
    }

    @Override
    public String buildSource() {
        ClassBuilderContext context = ClassBuilderContext.create(decompiler, typeDefinition);

        for (AstNode child : context.getCompilationUnit().getChildren())
            if (child instanceof ImportDeclaration)
                context.getVisitor().visitImportDeclaration((ImportDeclaration) child, null);

        TypeDeclaration clone = context.getTypeDeclaration().clone();
        for (EntityDeclaration decl : clone.getMembers()) {
            if (decl instanceof MethodDeclaration)
                ((MethodDeclaration) decl).setBody(new BlockStatement());
            else if (decl instanceof ConstructorDeclaration)
                ((ConstructorDeclaration) decl).setBody(new BlockStatement());
        }

        context.getVisitor().visitTypeDeclaration(clone, null);

        return context.getStringWriter().toString();
    }

}
