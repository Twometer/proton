package de.twometer.proton.recompiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class CompilerResult {

    private boolean successful;

    private byte[] classFile;

    private DiagnosticCollector<JavaFileObject> diagnostics;

    CompilerResult(boolean successful, byte[] classFile, DiagnosticCollector<JavaFileObject> diagnostics) {
        this.successful = successful;
        this.classFile = classFile;
        this.diagnostics = diagnostics;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public byte[] getClassFile() {
        return classFile;
    }

    public DiagnosticCollector<JavaFileObject> getDiagnostics() {
        return diagnostics;
    }
}
