package de.twometer.proton.recompiler.classBuilder;

import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.languages.java.ast.*;
import de.twometer.proton.decompiler.ProcyonDecompiler;

import java.util.ArrayList;
import java.util.List;

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
            else if (child instanceof PackageDeclaration)
                context.getVisitor().visitPackageDeclaration((PackageDeclaration) child, null);

        TypeDeclaration clone = context.getTypeDeclaration().clone();

        List<EntityDeclaration> newMembers = new ArrayList<>();
        for (EntityDeclaration decl : clone.getMembers()) {
            newMembers.add(MethodEraser.erase(decl));
        }
        clone.getMembers().replaceWith(newMembers);

        context.getVisitor().visitTypeDeclaration(clone, null);

        return context.getStringWriter().toString();
    }



}
