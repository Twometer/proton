package de.twometer.proton.recompiler.javac;

import javax.tools.SimpleJavaFileObject;

public class StringInputBuffer extends SimpleJavaFileObject {

    private String code;

    public StringInputBuffer(String filename, String code) {
        super(CompilerHelper.toURI(filename), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
