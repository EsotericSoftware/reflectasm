/**
 * Copyright (c) 2008, Nathan Sweet
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.esotericsoftware.reflectasm;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

abstract public class ConstructorAccess<T> {
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

	abstract public T newInstance0 (Object... args);

	public T newInstance0 () {
		return newInstance();
	}

	/** Constructor for inner classes (non-static nested classes).
	 * @param enclosingInstance The instance of the enclosing type to which this inner instance is related to (assigned to its
	 *           synthetic this$0 field). */
	abstract public T newInstance (Object enclosingInstance);

	static public <T> ConstructorAccess<T> get (Class<T> type) {
		return get(type, (Class<?>[])null);
	}

	static public <T> ConstructorAccess<T> get (Class<T> type, Constructor<?> constructor) {
		Class<?>[] parameterTypes = null;
		if (constructor != null) {
			parameterTypes = constructor.getParameterTypes();
		}
		return get(type, parameterTypes);
	}

	static public <T> ConstructorAccess<T> get (Class<T> type, Class<?>... args) {
		Class enclosingType = type.getEnclosingClass();
		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

		String className = type.getName();
		String accessClassName = className + "ConstructorAccess";
		if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;

		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = className.replace('.', '/');
				String enclosingClassNameInternal;
				Constructor<T> constructor = null;
				int modifiers = 0;
				if (!isNonStaticMemberClass) {
					enclosingClassNameInternal = null;
					try {
						constructor = type.getDeclaredConstructor((Class[])null);
						modifiers = constructor.getModifiers();
					} catch (Exception ex) {
						throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
					}
					if (Modifier.isPrivate(modifiers)) {
						throw new RuntimeException("Class cannot be created (the no-arg constructor is private): " + type.getName());
					}
				} else {
					enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
					try {
						constructor = type.getDeclaredConstructor(enclosingType); // Inner classes should have this.
						modifiers = constructor.getModifiers();
					} catch (Exception ex) {
						throw new RuntimeException(
							"Non-static member class cannot be created (missing enclosing class constructor): " + type.getName(), ex);
					}
					if (Modifier.isPrivate(modifiers)) {
						throw new RuntimeException(
							"Non-static member class cannot be created (the enclosing class constructor is private): " + type.getName());
					}
				}
				String superclassNameInternal = Modifier.isPublic(modifiers)
					? "com/esotericsoftware/reflectasm/PublicConstructorAccess"
					: "com/esotericsoftware/reflectasm/ConstructorAccess";

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, superclassNameInternal, null);

				insertConstructor(cw, superclassNameInternal);
				insertNewInstance(cw, classNameInternal);
				if (args != null) {
					insertNewInstance0(cw, classNameInternal, args);
				}
				insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

				cw.visitEnd();
				accessClass = loader.defineAccessClass(accessClassName, cw.toByteArray());
			}
		}
		ConstructorAccess<T> access;
		try {
			access = (ConstructorAccess<T>)accessClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("Exception constructing constructor access class: " + accessClassName, t);
		}
		if (!(access instanceof PublicConstructorAccess) && !AccessClassLoader.areInSameRuntimeClassLoader(type, accessClass)) {
			// Must test this after the try-catch block, whether the class has been loaded as if has been defined.
			// Throw a Runtime exception here instead of an IllegalAccessError when invoking newInstance()
			throw new RuntimeException((!isNonStaticMemberClass
				? "Class cannot be created (the no-arg constructor is protected or package-protected, and its ConstructorAccess could not be defined in the same class loader): "
				: "Non-static member class cannot be created (the enclosing class constructor is protected or package-protected, and its ConstructorAccess could not be defined in the same class loader): ")
				+ type.getName());
		}
		access.isNonStaticMemberClass = isNonStaticMemberClass;
		return access;
	}

	static private void insertConstructor (ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V");
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

	static void insertNewInstance0 (ClassWriter cw, String classNameInternal, Class<?>[] args) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance0", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, classNameInternal);
		mv.visitInsn(DUP);

		StringBuilder buffer = new StringBuilder(128);
		buffer.setLength(0);
		buffer.append('(');
		if (args == null)
			args = new Class[0];
		for (int i = 0; i < args.length; i++) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitIntInsn(BIPUSH, i);
			mv.visitInsn(AALOAD);
			Type paramType = Type.getType(args[i]);
			switch (paramType.getSort()) {
				case org.objectweb.asm.Type.BOOLEAN:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
					break;
				case org.objectweb.asm.Type.BYTE:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
					break;
				case org.objectweb.asm.Type.CHAR:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
					break;
				case org.objectweb.asm.Type.SHORT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
					break;
				case org.objectweb.asm.Type.INT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
					break;
				case org.objectweb.asm.Type.FLOAT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
					break;
				case org.objectweb.asm.Type.LONG:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
					break;
				case org.objectweb.asm.Type.DOUBLE:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
					break;
				case org.objectweb.asm.Type.ARRAY:
					mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
					break;
				case org.objectweb.asm.Type.OBJECT:
					mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
					break;
			}
			buffer.append(paramType.getDescriptor());
		}
		buffer.append(")V");
		mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", buffer.toString());
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
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
