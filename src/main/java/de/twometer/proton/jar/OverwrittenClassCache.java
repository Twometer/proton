package de.twometer.proton.jar;

import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;

import java.util.HashMap;
import java.util.Map;

public class OverwrittenClassCache implements ITypeLoader {

    private Map<String, byte[]> overwrittenClasses = new HashMap<>();

    @Override
    public boolean tryLoadType(String internalName, Buffer buffer) {
        System.out.println("Checking for " + internalName);
        if (overwrittenClasses.containsKey(internalName + ".class")) {
            byte[] overwrittenClass = overwrittenClasses.get(internalName + ".class");
            buffer.putByteArray(overwrittenClass, 0, overwrittenClass.length);
            buffer.position(0);
            return true;
        }
        return false;
    }

    public boolean isOverwritten(String classPath) {
        return overwrittenClasses.containsKey(classPath);
    }

    public byte[] getClassData(String classPath) {
        return overwrittenClasses.get(classPath);
    }

    public void overwrite(String classPath, byte[] classData) {
        overwrittenClasses.put(classPath, classData);
    }

}
