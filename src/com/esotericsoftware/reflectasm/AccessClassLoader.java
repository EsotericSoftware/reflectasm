
package com.esotericsoftware.reflectasm;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;

class AccessClassLoader extends ClassLoader {
	static private final ArrayList<AccessClassLoader> accessClassLoaders = new ArrayList();

	static AccessClassLoader get (Class type) {
		ClassLoader parent = type.getClassLoader();
		synchronized (accessClassLoaders) {
			for (int i = 0, n = accessClassLoaders.size(); i < n; i++) {
				AccessClassLoader accessClassLoader = accessClassLoaders.get(i);
				if (accessClassLoader.getParent() == parent) return accessClassLoader;
			}
			AccessClassLoader accessClassLoader = new AccessClassLoader(parent);
			accessClassLoaders.add(accessClassLoader);
			return accessClassLoader;
		}
	}

	static void remove (ClassLoader parent) {
		synchronized (accessClassLoaders) {
			for (int i = accessClassLoaders.size() - 1; i >= 0; i--) {
				AccessClassLoader accessClassLoader = accessClassLoaders.get(i);
				if (accessClassLoader.getParent() == parent) accessClassLoaders.remove(i);
			}
		}
	}

	private AccessClassLoader (ClassLoader parent) {
		super(parent);
	}

	protected synchronized java.lang.Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException {
		// These classes come from the classloader that loaded AccessClassLoader.
		if (name.equals(FieldAccess.class.getName())) return FieldAccess.class;
		if (name.equals(MethodAccess.class.getName())) return MethodAccess.class;
		if (name.equals(ConstructorAccess.class.getName())) return ConstructorAccess.class;
		// All other classes come from the classloader that loaded the type we are accessing.
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes protected and default access members accessible.
			Method method = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] {String.class, byte[].class, int.class,
				int.class, ProtectionDomain.class});
			method.setAccessible(true);
			return (Class)method.invoke(getParent(), new Object[] {name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length),
				getClass().getProtectionDomain()});
		} catch (Exception ignored) {
		}
		return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
	}
}
