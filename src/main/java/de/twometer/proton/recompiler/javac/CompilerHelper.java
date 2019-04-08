package de.twometer.proton.recompiler.javac;

import java.io.File;
import java.net.URI;

class CompilerHelper {

    private static final String EXTENSION = ".java";

    static URI toURI(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file.toURI();
        } else {
            try {
                final StringBuilder newUri = new StringBuilder();
                newUri.append("mfm:///");
                newUri.append(name.replace('.', '/'));
                if (name.endsWith(EXTENSION))
                    newUri.replace(newUri.length() - EXTENSION.length(), newUri.length(), EXTENSION);
                return URI.create(newUri.toString());
            } catch (Exception exp) {
                return URI.create("mfm:///com/sun/script/java/java_source");
            }
        }
    }

}
