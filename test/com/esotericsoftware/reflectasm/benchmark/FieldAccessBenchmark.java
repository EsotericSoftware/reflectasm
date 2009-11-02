
package com.esotericsoftware.reflectasm.benchmark;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.esotericsoftware.reflectasm.FieldAccess;

public class FieldAccessBenchmark extends Benchmark {
	public FieldAccessBenchmark () throws Exception {
		int count = 1000000;
		Object[] dontCompileMeAway = new Object[count];

		FieldAccess access = FieldAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		int index = access.getIndex("name");

		Field field = SomeClass.class.getField("name");

		for (int i = 0; i < 100; i++) {
			for (int ii = 0; ii < count; ii++) {
				access.set(someObject, index, "first");
				dontCompileMeAway[ii] = access.get(someObject, index);
			}
			for (int ii = 0; ii < count; ii++) {
				field.set(someObject, "first");
				dontCompileMeAway[ii] = field.get(someObject);
			}
		}
		warmup = false;

		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++) {
				access.set(someObject, index, "first");
				dontCompileMeAway[ii] = access.get(someObject, index);
			}
			end("FieldAccess");
		}
		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++) {
				field.set(someObject, "first");
				dontCompileMeAway[ii] = field.get(someObject);
			}
			end("Reflection");
		}

		chart("Field Set/Get");
	}

	static public class SomeClass {
		public String name;
	}

	public static void main (String[] args) throws Exception {
		new FieldAccessBenchmark();
	}
}
