package de.twometer.proton.decompiler;

import com.strobel.assembler.metadata.TypeDefinition;

public class DecompiledClass {

    private String code;

    private TypeDefinition typeDefinition;

    DecompiledClass(String code, TypeDefinition typeDefinition) {
        this.code = code;
        this.typeDefinition = typeDefinition;
    }

    public String getCode() {
        return code;
    }

    public TypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

}
