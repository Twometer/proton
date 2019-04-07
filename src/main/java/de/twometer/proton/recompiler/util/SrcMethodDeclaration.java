package de.twometer.proton.recompiler.util;

import com.strobel.decompiler.languages.EntityType;
import com.strobel.decompiler.languages.java.ast.EntityDeclaration;
import com.strobel.decompiler.languages.java.ast.IAstVisitor;
import com.strobel.decompiler.languages.java.ast.TextNode;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class SrcMethodDeclaration extends EntityDeclaration {

    private String src;

    public SrcMethodDeclaration(String src) {
        this.src = src;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.METHOD;
    }

    @Override
    public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data) {
        for (String s : src.split("[\\r\\n]+"))
            visitor.visitText(new TextNode(s), data);
        return null;
    }

    @Override
    public boolean matches(INode other, Match match) {
        return false;
    }

}
