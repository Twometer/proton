package de.twometer.proton.recompiler.classBuilder;

import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.decompiler.languages.java.ast.*;
import de.twometer.proton.decompiler.MethodHelper;
import de.twometer.proton.decompiler.ProcyonDecompiler;
import de.twometer.proton.recompiler.util.SrcMethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class InjectEmptyClassBuilder implements IClassBuilder {

    private ProcyonDecompiler decompiler;

    private MethodDefinition method;

    private String code;

    InjectEmptyClassBuilder(ProcyonDecompiler decompiler, MethodDefinition definition, String code) {
        this.decompiler = decompiler;
        this.method = definition;
        this.code = code;
    }

    public String buildSource() {
        ClassBuilderContext context = ClassBuilderContext.create(decompiler, method.getDeclaringType());

        for (AstNode child : context.getCompilationUnit().getChildren())
            if (child instanceof ImportDeclaration)
                context.getVisitor().visitImportDeclaration((ImportDeclaration) child, null);
            else if (child instanceof PackageDeclaration)
                context.getVisitor().visitPackageDeclaration((PackageDeclaration) child, null);

        TypeDeclaration clone = context.getTypeDeclaration().clone();
        List<EntityDeclaration> newMembers = new ArrayList<>();
        for (EntityDeclaration decl : clone.getMembers()) {
            if (decl instanceof MethodDeclaration && MethodHelper.isDefinitionOf(method, (MethodDeclaration) decl)) {
                newMembers.add(new SrcMethodDeclaration(code));
                continue;
            } else if (decl instanceof ConstructorDeclaration && MethodHelper.isDefinitionOf(method, (ConstructorDeclaration) decl)) {
                newMembers.add(new SrcMethodDeclaration(code));
                continue;
            }
            newMembers.add(MethodEraser.erase(decl));
        }
        clone.getMembers().replaceWith(newMembers);

        context.getVisitor().visitTypeDeclaration(clone, null);

        return context.getStringWriter().toString();
    }


}
