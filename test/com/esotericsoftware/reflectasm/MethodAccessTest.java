/**
 * Copyright (c) 2008, Nathan Sweet
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.esotericsoftware.reflectasm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static junit.framework.Assert.assertEquals;

import junit.framework.TestCase;

public class MethodAccessTest extends TestCase {
	public void testInvoke () {
		MethodAccess access = MethodAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		Object value;

		value = access.invoke(someObject, "getName");
		assertEquals(null, value);
		value = access.invoke(someObject, "setName", "sweet");
		assertEquals(null, value);
		value = access.invoke(someObject, "getName");
		assertEquals("sweet", value);
		value = access.invoke(someObject, "setName", (Object)null);
		assertEquals(null, value);
		value = access.invoke(someObject, "getName");
		assertEquals(null, value);

		value = access.invoke(someObject, "getIntValue");
		assertEquals(0, value);
		value = access.invoke(someObject, "setIntValue", 1234);
		assertEquals(null, value);
		value = access.invoke(someObject, "getIntValue");
		assertEquals(1234, value);

		value = access.invoke(someObject, "methodWithManyArguments", 1, 2f, 3, 4.2f, null, null, null);
		assertEquals("test", value);

		int index = access.getIndex("methodWithManyArguments", int.class, float.class, Integer.class, Float.class, SomeClass.class,
			SomeClass.class, SomeClass.class);
		assertEquals(access.getIndex("methodWithManyArguments"), index);

		value = access.invoke(null, "staticMethod", "moo", 1234);
		assertEquals("meow! moo, 1234", value);
	}

	public void testEmptyClass () {
		MethodAccess access = MethodAccess.get(EmptyClass.class);
		try {
			access.getIndex("name");
			fail();
		} catch (IllegalArgumentException expected) {
			// expected.printStackTrace();
		}
		try {
			access.getIndex("name", String.class);
			fail();
		} catch (IllegalArgumentException expected) {
			// expected.printStackTrace();
		}
		try {
			access.invoke(new EmptyClass(), "meow", "moo");
			fail();
		} catch (IllegalArgumentException expected) {
			// expected.printStackTrace();
		}
		try {
			access.invoke(new EmptyClass(), 0);
			fail();
		} catch (IllegalArgumentException expected) {
			// expected.printStackTrace();
		}
		try {
			access.invoke(new EmptyClass(), 0, "moo");
			fail();
		} catch (IllegalArgumentException expected) {
			// expected.printStackTrace();
		}
	}

	public void testInvokeInterface () {
		MethodAccess access = MethodAccess.get(ConcurrentMap.class);
		access = MethodAccess.get(ConcurrentMap.class);
		ConcurrentHashMap<String, String> someMap = new ConcurrentHashMap<String, String>();
		someMap.put("first", "one");
		someMap.put("second", "two");
		Object value;

		// invoke a method declared directly in the ConcurrentMap interface
		value = access.invoke(someMap, "replace", "first", "foo");
		assertEquals("one", value);
		// invoke a method declared in the Map superinterface
		value = access.invoke(someMap, "size");
		assertEquals(someMap.size(), value);
	}

	static public class EmptyClass {
	}

	static public class SomeClass {
		private String name;
		private int intValue;

		public String getName () {
			return name;
		}

		public void setName (String name) {
			this.name = name;
		}

		public int getIntValue () {
			return intValue;
		}

		public void setIntValue (int intValue) {
			this.intValue = intValue;
		}

		public String methodWithManyArguments (int i, float f, Integer I, Float F, SomeClass c, SomeClass c1, SomeClass c2) {
			return "test";
		}

		static public String staticMethod (String a, int b) {
			return "meow! " + a + ", " + b;
		}
	}
}
