
package com.esotericsoftware.reflectasm;

@SuppressWarnings("UnusedDeclaration")
public class MethodAccess {
    public final ClassAccess classAccess;
    public final ClassAccess.ClassAccessor classAccessor;

    @Override
    public String toString() {
        return classAccess.toString();
    }

    protected MethodAccess(ClassAccess classAccess) {
        this.classAccess = classAccess;
        classAccessor = classAccess.classAccessor;
    }

    public Object invoke(Object object, int methodIndex, Object... args) {
        return classAccessor.invoke(object, methodIndex, args);
    }

    /** Invokes the method with the specified name and the specified param types. */
	public Object invoke (Object object, String methodName, Class[] paramTypes, Object... args) {
		return invoke(object, getIndex(methodName, paramTypes), args);
	}

	/** Invokes the first method with the specified name and the specified number of arguments. */
	public Object invoke (Object object, String methodName, Object... args) {
		return invoke(object, getIndex(methodName, args == null ? 0 : args.length), args);
	}

	/** Returns the index of the first method with the specified name. */
	public int getIndex (String methodName) {
        return classAccess.indexOfMethod(methodName);
	}

	/** Returns the index of the first method with the specified name and param types. */
	public int getIndex (String methodName, Class... paramTypes) {
        return classAccess.indexOfMethod(methodName, paramTypes);
	}

	/** Returns the index of the first method with the specified name and the specified number of arguments. */
	public int getIndex (String methodName, int paramsCount) {
        return classAccess.indexOfMethod(methodName, paramsCount);
	}

	public String[] getMethodNames () {
		return classAccess.getMethodNames();
	}

	public Class[][] getParameterTypes () {
		return classAccess.getParameterTypes();
	}

	public Class[] getReturnTypes () {
		return classAccess.getReturnTypes();
	}

	static public MethodAccess get (Class type) {
        return new MethodAccess(ClassAccess.get(type));
	}
}
