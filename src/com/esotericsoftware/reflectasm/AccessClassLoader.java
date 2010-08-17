
package com.esotericsoftware.reflectasm;

class AccessClassLoader extends ClassLoader {
	static private AccessClassLoader instance;

	static synchronized AccessClassLoader getInstance () {
		// Initialize lazily to avoid ExceptionInInitializerError when lacking the createClassLoader RuntimePermission.
		if (instance == null) instance = new AccessClassLoader();
		return instance;
	}

	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		return defineClass(name, bytes, 0, bytes.length);
	}
}
