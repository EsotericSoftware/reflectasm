
package com.esotericsoftware.reflectasm.benchmark;

import com.esotericsoftware.reflectasm.ConstructorAccess;

public class ConstructorAccessBenchmark extends Benchmark {
	public ConstructorAccessBenchmark () throws Exception {
		int count = 1000000;
		Object[] dontCompileMeAway = new Object[count];

		Class type = SomeClass.class;
		ConstructorAccess<SomeClass> access = ConstructorAccess.get(type);

		for (int i = 0; i < 100; i++)
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.newInstance();
		for (int i = 0; i < 100; i++)
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = type.newInstance();
		warmup = false;

		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.newInstance();
			end("ConstructorAccess");
		}
		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = type.newInstance();
			end("Reflection");
		}

		chart("Constructor");
	}

	static public class SomeClass {
		public String name;
	}

	public static void main (String[] args) throws Exception {
		new ConstructorAccessBenchmark();
	}
}
