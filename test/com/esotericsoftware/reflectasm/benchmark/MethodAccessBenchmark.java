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

package com.esotericsoftware.reflectasm.benchmark;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Method;

public class MethodAccessBenchmark extends Benchmark {
	public MethodAccessBenchmark () throws Exception {
		int count = 100000;
		Object[] dontCompileMeAway = new Object[count];
		Object[] args = new Object[0];

		MethodAccess access = MethodAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		int index = access.getIndex("getName");

		Method method = SomeClass.class.getMethod("getName");
		// method.setAccessible(true); // Improves reflection a bit.

		for (int i = 0; i < 100; i++) {
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.invoke(someObject, index, args);
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = method.invoke(someObject, args);
		}
		warmup = false;

		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.invoke(someObject, index, args);
			end("MethodAccess");
		}
		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = method.invoke(someObject, args);
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
