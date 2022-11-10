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

import java.util.List;

import junit.framework.TestCase;

public class ConstructorAccessTest extends TestCase {
	static private boolean java17;
	static {
		try {
			Object version = Runtime.class.getDeclaredMethod("version").invoke(null);
			java17 = ((List<Integer>)version.getClass().getDeclaredMethod("version").invoke(version)).get(0) >= 17;
		} catch (Exception ignored) {
			java17 = false;
		}
	}

	public void testNewInstance () {
		ConstructorAccess<SomeClass> access = ConstructorAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject, access.newInstance());
		assertEquals(someObject, access.newInstance());
	}

	public void testPackagePrivateNewInstance () {
		if (java17) return;
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
		} catch (RuntimeException re) {
			System.out.println("Expected exception happened: " + re);
		} catch (Throwable t) {
			t.printStackTrace();
			assertTrue(false);
		}
	}

	public void testHasPrivateConstructor () {
		try {
			ConstructorAccess.get(HasPrivateConstructor.class);
			assertTrue(false);
		} catch (RuntimeException re) {
			System.out.println("Expected exception happened: " + re);
		} catch (Throwable t) {
			t.printStackTrace();
			assertTrue(false);
		}
	}

	public void testHasProtectedConstructor () {
		if (java17) return;
		try {
			ConstructorAccess<HasProtectedConstructor> access = ConstructorAccess.get(HasProtectedConstructor.class);
			HasProtectedConstructor newInstance = access.newInstance();
			assertEquals("cow", newInstance.getMoo());
		} catch (Throwable t) {
			t.printStackTrace();
			assertTrue(false);
		}
	}

	public void testHasPackagePrivateConstructor () {
		if (java17) return;
		try {
			ConstructorAccess<HasPackagePrivateConstructor> access = ConstructorAccess.get(HasPackagePrivateConstructor.class);
			HasPackagePrivateConstructor newInstance = access.newInstance();
			assertEquals("cow", newInstance.getMoo());
		} catch (Throwable t) {
			t.printStackTrace();
			assertTrue(false);
		}
	}

	public void testHasPublicConstructor () {
		try {
			ConstructorAccess<HasPublicConstructor> access = ConstructorAccess.get(HasPublicConstructor.class);
			HasPublicConstructor newInstance = access.newInstance();
			assertEquals("cow", newInstance.getMoo());
		} catch (Throwable t) {
			t.printStackTrace();
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

		public String getMoo () {
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

	static public class HasPackagePrivateConstructor extends HasProtectedConstructor {
		HasPackagePrivateConstructor () {
			super();
		}
	}

	static public class HasPublicConstructor extends HasPackagePrivateConstructor {
		public HasPublicConstructor () {
			super();
		}
	}
}
