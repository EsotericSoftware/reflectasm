
package com.esotericsoftware.reflectasm.benchmark;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.esotericsoftware.reflectasm.MethodAccess;

public class MethodAccessBenchmark extends Benchmark {
	public MethodAccessBenchmark () throws Exception {
		int count = 100000;
		Object[] dontCompileMeAway = new Object[count];

		MethodAccess access = MethodAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		int index = access.getIndex("getName");

		Method method = SomeClass.class.getMethod("getName");

		for (int i = 0; i < 100; i++) {
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.invoke(someObject, index);
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = method.invoke(someObject);
		}
		warmup = false;

		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.invoke(someObject, index);
			end("MethodAccess");
		}
		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = method.invoke(someObject);
			end("Reflection");
		}

		chart("Method Call");
	}

	static public class SomeClass {
		private String name = "something";

		public String getName () {
			return name;
		}
	}

	public static void main (String[] args) throws Exception {
		new MethodAccessBenchmark();
	}
}
