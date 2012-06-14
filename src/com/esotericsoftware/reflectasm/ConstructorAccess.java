
package com.esotericsoftware.reflectasm;

import java.lang.reflect.Modifier;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public abstract class ConstructorAccess<T> {
	boolean isNonStaticMemberClass;

	public boolean isNonStaticMemberClass () {
		return isNonStaticMemberClass;
	}

	abstract public T newInstance ();

	abstract public T newInstance (Object enclosingInstance);

	static public <T> ConstructorAccess<T> get (Class<T> type) {
		AccessClassLoader loader = AccessClassLoader.get(type);

		String className = type.getName();
		String accessClassName = className + "ConstructorAccess";
		if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;
		Class enclosingType = type.getEnclosingClass();
		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());
		Class accessClass = null;
		try {
			accessClass = loader.loadClass(accessClassName);
		} catch (ClassNotFoundException ignored) {
		}
		if (accessClass == null) {
			String accessClassNameInternal = accessClassName.replace('.', '/');
			String classNameInternal = className.replace('.', '/');
			String enclosingClassNameInternal = isNonStaticMemberClass ? enclosingType.getName().replace('.', '/') : null;

			ClassWriter cw = new ClassWriter(0);
			cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null,
				"com/esotericsoftware/reflectasm/ConstructorAccess", null);
			MethodVisitor mv;

			insertConstructor(cw);
			insertNewInstance(cw, classNameInternal);
			insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

			cw.visitEnd();
			byte[] data = cw.toByteArray();
			accessClass = loader.defineClass(accessClassName, data);
		}
		try {
			ConstructorAccess<T> access = (ConstructorAccess<T>)accessClass.newInstance();
			access.isNonStaticMemberClass = isNonStaticMemberClass;
			return access;
		} catch (Exception ex) {
			throw new RuntimeException("Error constructing constructor access class: " + accessClassName, ex);
		}
	}

	static private void insertConstructor (ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "com/esotericsoftware/reflectasm/ConstructorAccess", "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	static void insertNewInstance (ClassWriter cw, String classNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, classNameInternal);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}

	static void insertNewInstanceInner (ClassWriter cw, String classNameInternal, String enclosingClassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();

		if (enclosingClassNameInternal != null) {
			mv.visitTypeInsn(NEW, classNameInternal);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, enclosingClassNameInternal);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "(L" + enclosingClassNameInternal + ";)V");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 2);
		} else {
			mv.visitTypeInsn(NEW, classNameInternal);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
		}

		mv.visitEnd();
	}
}
