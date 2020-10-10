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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;

public class ConstructorAccessTest extends TestCase {
	public void testNewInstance () {
		ConstructorAccess<SomeClass> access = ConstructorAccess.get(SomeClass.class, String.class, int.class, float.class, Float.class, String.class);
		SomeClass someObject = new SomeClass();
		SomeClass someObject0 = new SomeClass("11", 12, 13f, 14f, "15");
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject0, access.newInstance0("11", 12, 13f, 14f, "15"));
		assertEquals(someObject0, access.newInstance0("11", 12, 13f, 14f, "15"));
		assertEquals(someObject0, access.newInstance0("11", 12, 13f, 14f, "15"));
	}

	public void testPackagePrivateNewInstance () {
		ConstructorAccess<PackagePrivateClass> access = ConstructorAccess.get(PackagePrivateClass.class);
		PackagePrivateClass someObject = new PackagePrivateClass();
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject, access.newInstance());
	}

	public void testHasArgumentConstructor () {
		try {
			ConstructorAccess.get(HasArgumentConstructor.class);
			assertTrue(false);
		}
		catch (RuntimeException re) {
			System.out.println("Expected exception happened: " + re);
		}
		catch (Throwable t) {
			System.out.println("Unexpected exception happened: " + t);
			assertTrue(false);
		}
	}

	public void testHasPrivateConstructor () {
		try {
			ConstructorAccess.get(HasPrivateConstructor.class);
			assertTrue(false);
		}
		catch (RuntimeException re) {
			System.out.println("Expected exception happened: " + re);
		}
		catch (Throwable t) {
			System.out.println("Unexpected exception happened: " + t);
			assertTrue(false);
		}
	}

	public void testHasProtectedConstructor () {
		try {
			ConstructorAccess<HasProtectedConstructor> access = ConstructorAccess.get(HasProtectedConstructor.class);
			HasProtectedConstructor newInstance = access.newInstance();
			assertEquals("cow", newInstance.getMoo());
		}
		catch (Throwable t) {
			System.out.println("Unexpected exception happened: " + t);
			assertTrue(false);
		}
	}

	public void testHasPackageProtectedConstructor () {
		try {
			ConstructorAccess<HasPackageProtectedConstructor> access = ConstructorAccess.get(HasPackageProtectedConstructor.class);
			HasPackageProtectedConstructor newInstance = access.newInstance();
			assertEquals("cow", newInstance.getMoo());
		}
		catch (Throwable t) {
			System.out.println("Unexpected exception happened: " + t);
			assertTrue(false);
		}
	}

	public void testHasPublicConstructor () {
		try {
			ConstructorAccess<HasPublicConstructor> access = ConstructorAccess.get(HasPublicConstructor.class);
			HasPublicConstructor newInstance = access.newInstance();
			assertEquals("cow", newInstance.getMoo());
		}
		catch (Throwable t) {
			System.out.println("Unexpected exception happened: " + t);
			assertTrue(false);
		}
	}

	static class PackagePrivateClass {
		public String name;
		public int intValue;
		protected float test1;
		Float test2;
		private String test3;

		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PackagePrivateClass other = (PackagePrivateClass)obj;
			if (intValue != other.intValue) return false;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			if (Float.floatToIntBits(test1) != Float.floatToIntBits(other.test1)) return false;
			if (test2 == null) {
				if (other.test2 != null) return false;
			} else if (!test2.equals(other.test2)) return false;
			if (test3 == null) {
				if (other.test3 != null) return false;
			} else if (!test3.equals(other.test3)) return false;
			return true;
		}
	}

	static public class SomeClass {
		public String name;
		public int intValue;
		protected float test1;
		Float test2;
		private String test3;

		public SomeClass() {
		}

		public SomeClass(String name, int intValue, float test1, Float test2, String test3) {
			this.name = name;
			this.intValue = intValue;
			this.test1 = test1;
			this.test2 = test2;
			this.test3 = test3;
		}

		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SomeClass other = (SomeClass)obj;
			if (intValue != other.intValue) return false;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			if (Float.floatToIntBits(test1) != Float.floatToIntBits(other.test1)) return false;
			if (test2 == null) {
				if (other.test2 != null) return false;
			} else if (!test2.equals(other.test2)) return false;
			if (test3 == null) {
				if (other.test3 != null) return false;
			} else if (!test3.equals(other.test3)) return false;
			return true;
		}
	}
	
	static public class HasArgumentConstructor {
		public String moo;

		public HasArgumentConstructor (String moo) {
			this.moo = moo;
		}

		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			HasArgumentConstructor other = (HasArgumentConstructor)obj;
			if (moo == null) {
				if (other.moo != null) return false;
			} else if (!moo.equals(other.moo)) return false;
			return true;
		}
		
		public String getMoo() {
			return moo;
		}
	}

	static public class HasPrivateConstructor extends HasArgumentConstructor {
		private HasPrivateConstructor () {
			super("cow");
		}
	}

	static public class HasProtectedConstructor extends HasPrivateConstructor {
		@SuppressWarnings("synthetic-access")
		protected HasProtectedConstructor () {
			super();
		}
	}

	static public class HasPackageProtectedConstructor extends HasProtectedConstructor {
		HasPackageProtectedConstructor () {
			super();
		}
	}

	static public class HasPublicConstructor extends HasPackageProtectedConstructor {
		HasPublicConstructor () {
			super();
		}
	}
}
