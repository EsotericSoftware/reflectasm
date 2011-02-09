
package com.esotericsoftware.reflectasm;

import java.lang.reflect.Method;

class AccessClassLoader extends ClassLoader {
	AccessClassLoader (ClassLoader parent) {
		super(parent);
	}

	protected synchronized java.lang.Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException {
		// These classes come from the classloader that loaded AccessClassLoader.
		if (name.equals(FieldAccess.class.getName())) return FieldAccess.class;
		if (name.equals(MethodAccess.class.getName())) return MethodAccess.class;
		// All other classes come from the classloader that loaded the type we are accessing.
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes protected and default access members accessible.
			Method method = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] {String.class, byte[].class, int.class,
				int.class});
			method.setAccessible(true);
			return (Class)method.invoke(getParent(), new Object[] {name, bytes, new Integer(0), new Integer(bytes.length)});
		} catch (Exception ignored) {
		}
		return defineClass(name, bytes, 0, bytes.length);
	}
}
