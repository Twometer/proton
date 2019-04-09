package de.twometer.proton.transformer;

import org.objectweb.asm.*;

public class DelegatingMethodVisitor extends MethodVisitor {

    private MethodVisitor delegate;

    DelegatingMethodVisitor(int i, MethodVisitor delegate) {
        super(i);
        this.delegate = delegate;
        System.out.println("Delegating method visitor created");
    }

    @Override
    public void visitParameter(String s, int i) {
        delegate.visitParameter(s, i);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return delegate.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return delegate.visitAnnotation(s, b);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typePath, String s, boolean b) {
        return delegate.visitTypeAnnotation(i, typePath, s, b);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int i, String s, boolean b) {
        return delegate.visitParameterAnnotation(i, s, b);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        delegate.visitAttribute(attribute);
    }

    @Override
    public void visitCode() {
        delegate.visitCode();
        System.out.println("Visit code clear");
    }

    @Override
    public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
        delegate.visitFrame(i, i1, objects, i2, objects1);
    }

    @Override
    public void visitInsn(int i) {
        delegate.visitInsn(i);
    }

    @Override
    public void visitIntInsn(int i, int i1) {
        delegate.visitIntInsn(i, i1);
    }

    @Override
    public void visitVarInsn(int i, int i1) {
        delegate.visitVarInsn(i, i1);
    }

    @Override
    public void visitTypeInsn(int i, String s) {
        delegate.visitTypeInsn(i, s);
    }

    @Override
    public void visitFieldInsn(int i, String s, String s1, String s2) {
        delegate.visitFieldInsn(i, s, s1, s2);
    }

    @Deprecated
    @Override
    public void visitMethodInsn(int i, String s, String s1, String s2) {
        delegate.visitMethodInsn(i, s, s1, s2);
    }

    @Override
    public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
        delegate.visitMethodInsn(i, s, s1, s2, b);
    }

    @Override
    public void visitInvokeDynamicInsn(String s, String s1, Handle handle, Object... objects) {
        delegate.visitInvokeDynamicInsn(s, s1, handle, objects);
    }

    @Override
    public void visitJumpInsn(int i, Label label) {
        delegate.visitJumpInsn(i, label);
    }

    @Override
    public void visitLabel(Label label) {
        delegate.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object o) {
        delegate.visitLdcInsn(o);
    }

    @Override
    public void visitIincInsn(int i, int i1) {
        delegate.visitIincInsn(i, i1);
    }

    @Override
    public void visitTableSwitchInsn(int i, int i1, Label label, Label... labels) {
        delegate.visitTableSwitchInsn(i, i1, label, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        delegate.visitLookupSwitchInsn(label, ints, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String s, int i) {
        delegate.visitMultiANewArrayInsn(s, i);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int i, TypePath typePath, String s, boolean b) {
        return delegate.visitInsnAnnotation(i, typePath, s, b);
    }

    @Override
    public void visitTryCatchBlock(Label label, Label label1, Label label2, String s) {
        delegate.visitTryCatchBlock(label, label1, label2, s);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int i, TypePath typePath, String s, boolean b) {
        return delegate.visitTryCatchAnnotation(i, typePath, s, b);
    }

    @Override
    public void visitLocalVariable(String s, String s1, String s2, Label label, Label label1, int i) {
        delegate.visitLocalVariable(s, s1, s2, label, label1, i);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int i, TypePath typePath, Label[] labels, Label[] labels1, int[] ints, String s, boolean b) {
        return delegate.visitLocalVariableAnnotation(i, typePath, labels, labels1, ints, s, b);
    }

    @Override
    public void visitLineNumber(int i, Label label) {
        delegate.visitLineNumber(i, label);
    }

    @Override
    public void visitMaxs(int i, int i1) {
        delegate.visitMaxs(i, i1);
    }

    @Override
    public void visitEnd() {
        delegate.visitEnd();
    }
}
