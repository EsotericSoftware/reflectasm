
package com.esotericsoftware.reflectasm;

import junit.framework.TestCase;

public class FieldAccessTest extends TestCase {
	public void testNameSetAndGet () {
		FieldAccess access = FieldAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();

		assertEquals(null, someObject.name);
		access.set(someObject, "name", "first");
		assertEquals("first", someObject.name);
		assertEquals("first", access.get(someObject, "name"));

		assertEquals(0, someObject.intValue);
		access.set(someObject, "intValue", 1234);
		assertEquals(1234, someObject.intValue);
		assertEquals(1234, access.get(someObject, "intValue"));
	}

	public void testIndexSetAndGet () {
		FieldAccess access = FieldAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		int index;

		assertEquals(null, someObject.name);
		index = access.getIndex("name");
		access.set(someObject, index, "first");
		assertEquals("first", someObject.name);
		assertEquals("first", access.get(someObject, index));

		index = access.getIndex("intValue");
		assertEquals(0, someObject.intValue);
		access.set(someObject, index, 1234);
		assertEquals(1234, someObject.intValue);
		assertEquals(1234, access.get(someObject, index));
	}

	static public class SomeClass {
		public String name;
		public int intValue;
		protected float test1;
		Float test2;
		private String test3;
	}
}
