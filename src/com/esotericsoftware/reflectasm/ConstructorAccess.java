
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

	/** Constructor for top-level classes and static nested classes.
	 * <p>
	 * If the underlying class is a inner (non-static nested) class, a new instance will be created using <code>null</code> as the
	 * this$0 synthetic reference. The instantiated object will work as long as it actually don't use any member variable or method
	 * fron the enclosing instance. */
	abstract public T newInstance ();

	/** Constructor for inner classes (non-static nested classes).
	 * @param enclosingInstance The instance of the enclosing type to which this inner instance is related to (assigned to its
	 *           synthetic this$0 field). */
	abstract public T newInstance (Object enclosingInstance);

	static public <T> ConstructorAccess<T> get (Class<T> type) {
		Class enclosingType = type.getEnclosingClass();
		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

		String className = type.getName();
		String accessClassName = className + "ConstructorAccess";
		if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;
		Class accessClass = null;

		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			try {
				accessClass = loader.loadClass(accessClassName);
			} catch (ClassNotFoundException ignored) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = className.replace('.', '/');
				String enclosingClassNameInternal;

				if (!isNonStaticMemberClass) {
					enclosingClassNameInternal = null;
					try {
						type.getDeclaredConstructor((Class[])null);
					} catch (Exception ex) {
						throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
					}
				} else {
					enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
					try {
						type.getDeclaredConstructor(enclosingType); // Inner classes should have this.
					} catch (Exception ex) {
						throw new RuntimeException("Non-static member class cannot be created (missing enclosing class constructor): "
							+ type.getName());
					}
				}

				ClassWriter cw = new ClassWriter(0);
				cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null,
					"com/esotericsoftware/reflectasm/ConstructorAccess", null);

				insertConstructor(cw);
				insertNewInstance(cw, classNameInternal);
				insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

				cw.visitEnd();
				accessClass = loader.defineClass(accessClassName, cw.toByteArray());
			}
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
			mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("Not an inner class.");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
			mv.visitInsn(ATHROW);
			mv.visitMaxs(3, 2);
		}
		mv.visitEnd();
	}
}
