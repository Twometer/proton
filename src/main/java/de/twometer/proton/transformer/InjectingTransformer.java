package de.twometer.proton.transformer;

import com.strobel.assembler.metadata.MethodDefinition;
import org.objectweb.asm.*;

import java.util.Arrays;

public class InjectingTransformer {

    private static final int ASM_VER = Opcodes.ASM7;

    private byte[] originalClass;

    private byte[] overwrittenClass;

    private MethodDefinition overwrittenMethod;

    public InjectingTransformer(byte[] originalClass, byte[] overwrittenClass, MethodDefinition overwrittenMethod) {
        this.originalClass = originalClass;
        this.overwrittenClass = overwrittenClass;
        this.overwrittenMethod = overwrittenMethod;
    }

    public byte[] createClassFile() {
        ClassReader overwrittenReader = new ClassReader(overwrittenClass);

        ClassReader originalReader = new ClassReader(this.originalClass);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor transformer = new ClassVisitor(ASM_VER, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (methodEquals(name, descriptor, overwrittenMethod)) {
                    return new MethodVisitor(ASM_VER, null) {
                        @Override
                        public void visitCode() {
                            ClassVisitor overwrittenClassVisitor = new ClassVisitor(ASM_VER) {
                                @Override
                                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                                    if (methodEquals(name, descriptor, overwrittenMethod))
                                        return new DelegatingMethodVisitor(ASM_VER, visitor);
                                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                                }
                            };
                            overwrittenReader.accept(overwrittenClassVisitor, 0);
                        }
                    };
                }
                return visitor;
            }
        };
        originalReader.accept(transformer, 0);

        return writer.toByteArray();
    }


    private boolean methodEquals(String name, String descriptor, MethodDefinition def) {
        String desc2 = Arrays.toString(Arrays.stream(Type.getArgumentTypes(descriptor)).map(Type::getClassName).toArray());
        String desc1 = Arrays.toString(def.getParameters().stream().map(c -> c.getParameterType().getFullName()).toArray());
        return desc1.equals(desc2) && name.equals(def.getName());
    }

}
