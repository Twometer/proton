package de.twometer.proton.decompiler;

import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;

public class DecompilerContext {

    private ITypeLoader typeLoader;

    private MetadataSystem metadataSystem;

    DecompilerContext(ITypeLoader typeLoader) {
        this.typeLoader = typeLoader;
        this.metadataSystem = new MetadataSystem(typeLoader);
    }

    public ITypeLoader getTypeLoader() {
        return typeLoader;
    }

    MetadataSystem getMetadataSystem() {
        return metadataSystem;
    }

}
