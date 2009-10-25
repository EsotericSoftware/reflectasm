
package com.esotericsoftware.reflectasm;

class AccessClassLoader extends ClassLoader {
	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		return defineClass(name, bytes, 0, bytes.length);
	}
}
