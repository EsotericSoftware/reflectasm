
package com.esotericsoftware.reflectasm;

import junit.framework.TestCase;

public class FieldAccessTest extends TestCase {
	public void testNameSetAndGet () {
		FieldAccess access = FieldAccess.get(SomeClass.class);
		SomeClass someClass = new SomeClass();

		assertEquals(null, someClass.name);
		access.set(someClass, "name", "first");
		assertEquals("first", someClass.name);
		assertEquals("first", access.get(someClass, "name"));

		assertEquals(0, someClass.intValue);
		access.set(someClass, "intValue", 1234);
		assertEquals(1234, someClass.intValue);
		assertEquals(1234, access.get(someClass, "intValue"));
	}

	public void testIndexSetAndGet () {
		FieldAccess access = FieldAccess.get(SomeClass.class);
		SomeClass someClass = new SomeClass();
		int index;

		assertEquals(null, someClass.name);
		index = access.getIndex("name");
		access.set(someClass, index, "first");
		assertEquals("first", someClass.name);
		assertEquals("first", access.get(someClass, index));

		index = access.getIndex("intValue");
		assertEquals(0, someClass.intValue);
		access.set(someClass, index, 1234);
		assertEquals(1234, someClass.intValue);
		assertEquals(1234, access.get(someClass, index));
	}

	static public class SomeClass {
		public String name;
		public int intValue;
		protected float test1;
		Float test2;
		private String test3;
	}
}
