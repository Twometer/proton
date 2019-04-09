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

        System.out.println("creating class file");

        ClassVisitor transformer = new ClassVisitor(ASM_VER, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (methodEquals(name, descriptor, overwrittenMethod)) {
                    System.out.println("Building new method visitor " + name);
                    return new MethodVisitor(ASM_VER, null) {
                        @Override
                        public void visitCode() {
                            System.out.println("Visiting code in ORIGINAL_CLASS @" + name);
                            ClassVisitor overwrittenClassVisitor = new ClassVisitor(ASM_VER) {
                                @Override
                                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                                    System.out.println("Visiting " + name);
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
        System.out.printf("Checking equality: '%s'='%s' && '%s'='%s'%n", desc1, desc2, def.getName(), name);
        return desc1.equals(desc2) && name.equals(def.getName());
    }

}
