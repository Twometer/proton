package de.twometer.proton.recompiler.classBuilder;

import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.languages.java.ast.*;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

import java.util.ArrayList;
import java.util.List;

class MethodEraser {

    static EntityDeclaration erase(EntityDeclaration decl) {
        TypeReference typeRef = decl.getReturnType().getUserData(Keys.TYPE_REFERENCE);
        if (decl instanceof MethodDeclaration && !typeRef.getName().equals("void")) {
            if (!((MethodDeclaration) decl).getBody().isNull()) {
                BlockStatement statements = new BlockStatement();
                statements.add(new ReturnStatement(buildNullExpression(typeRef.getName())));
                ((MethodDeclaration) decl).setBody(statements);
            }
            return decl;
        }

        if (decl instanceof MethodDeclaration) {
            if (decl.getName().equals("<clinit>")) {
                for (Statement stmt : ((MethodDeclaration) decl).getBody()) {
                    if (stmt instanceof ExpressionStatement) {
                        Expression expr = ((ExpressionStatement) stmt).getExpression();
                        if (expr instanceof AssignmentExpression) {
                            ((AssignmentExpression) expr).setRight(new NullReferenceExpression());
                        } else if (expr instanceof InvocationExpression)
                            erasePrimitives((InvocationExpression) expr);
                    }
                }
            } else if (!((MethodDeclaration) decl).getBody().isNull())
                ((MethodDeclaration) decl).setBody(new BlockStatement());
        } else if (decl instanceof ConstructorDeclaration) {

            BlockStatement stm = ((ConstructorDeclaration) decl).getBody();
            for (Statement stmt : stm) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();
                    if (expr instanceof InvocationExpression) {
                        erasePrimitives((InvocationExpression) expr);
                    }
                }
            }
        }
        return decl;
    }

    private static InvocationExpression erasePrimitives(InvocationExpression expression) {

        List<Expression> args = new ArrayList<>();
        for (Expression expr : expression.getArguments()) {
            if (expr instanceof InvocationExpression)
                args.add(erasePrimitives((InvocationExpression) expr));
            else if (expr instanceof PrimitiveExpression)
                args.add(erase((PrimitiveExpression) expr));
            else if (expr instanceof IdentifierExpression)
                args.add(expr);
            else if (expr instanceof ArrayCreationExpression)
                args.add(new TextExpression("(Object[])null"));
            else
                args.add(new TextExpression("/*" + expr.toString() + "; " + expr.getClass().getSimpleName() + "*/null"));
        }
        expression.getArguments().replaceWith(args);
        return expression;
    }


    private static Expression erase(PrimitiveExpression expression) {
        String clazz = expression.getValue().getClass().getSimpleName();
        return buildNullExpression(clazz);
    }

    private static Expression buildNullExpression(String typeName) {
        switch (typeName) {
            case "int":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, 0);
            case "double":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, 0d);
            case "float":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, 0f);
            case "byte":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, (byte) 0);
            case "long":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, 0L);
            case "short":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, (short) 0);
            case "boolean":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, false);
            case "String":
                return new PrimitiveExpression(Expression.MYSTERY_OFFSET, "");
            default:
                return new NullReferenceExpression();
        }
    }

    private static class TextExpression extends Expression {

        private String text;

        TextExpression(String text) {
            super(Expression.MYSTERY_OFFSET);
            this.text = text;
        }

        @Override
        public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data) {
            return visitor.visitText(new TextNode(text), data);
        }

        @Override
        public boolean matches(INode other, Match match) {
            return false;
        }
    }

}
