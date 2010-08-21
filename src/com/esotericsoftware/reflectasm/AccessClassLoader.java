
package com.esotericsoftware.reflectasm;

class AccessClassLoader extends ClassLoader {
	static private AccessClassLoader instance;

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
		return defineClass(name, bytes, 0, bytes.length);
	}
}
