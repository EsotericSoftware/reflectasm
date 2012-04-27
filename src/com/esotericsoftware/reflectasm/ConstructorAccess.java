
package com.esotericsoftware.reflectasm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public abstract class ConstructorAccess<T> {
	static public <T> ConstructorAccess<T> get (Class<T> type) {
		AccessClassLoader loader = new AccessClassLoader(type.getClassLoader());

		String className = type.getName();
		String accessClassName = className + "ConstructorAccess";
		if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;
		Class accessClass = null;
		try {
			accessClass = loader.loadClass(accessClassName);
		} catch (ClassNotFoundException ignored) {
		}
		if (accessClass == null) {
			String accessClassNameInternal = accessClassName.replace('.', '/');
			String classNameInternal = className.replace('.', '/');

			ClassWriter cw = new ClassWriter(0);
			cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null,
				"com/esotericsoftware/reflectasm/ConstructorAccess", null);
			MethodVisitor mv;
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "com/esotericsoftware/reflectasm/ConstructorAccess", "<init>", "()V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
				mv.visitCode();
				mv.visitTypeInsn(NEW, classNameInternal);
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V");
				mv.visitInsn(ARETURN);
				mv.visitMaxs(2, 1);
				mv.visitEnd();
			}
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			accessClass = loader.defineClass(accessClassName, data);
		}
		try {
			return (ConstructorAccess)accessClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Error constructing field access class: " + accessClassName, ex);
		}
	}

	abstract public T newInstance ();
}
