package de.twometer.proton.decompiler;

import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.decompiler.languages.java.ast.*;

public class MethodHelper {

    private static boolean methodsEqual(MethodDefinition definition, String name, AstNodeCollection<ParameterDeclaration> parameters) {
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

    public static boolean isDefinitionOf(MethodDefinition definition, MethodDeclaration declaration) {
        return methodsEqual(definition, declaration.getName(), declaration.getParameters());
    }

    public static boolean isDefinitionOf(MethodDefinition definition, ConstructorDeclaration declaration) {
        return methodsEqual(definition, "<init>", declaration.getParameters());
    }


}
