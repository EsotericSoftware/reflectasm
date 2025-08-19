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

import com.esotericsoftware.reflectasm.ConstructorAccess;

import java.lang.reflect.Constructor;

public class ConstructorAccessBenchmark2 extends Benchmark {
	public ConstructorAccessBenchmark2() throws Exception {
		int count = 1000000;
		Object[] dontCompileMeAway = new Object[count];

		Class type = SomeClass.class;
		Constructor constructor = type.getDeclaredConstructor(String.class);
		Constructor constructor0 = type.getDeclaredConstructor();
		ConstructorAccess<SomeClass> access = ConstructorAccess.get(type, String.class);

		for (int i = 0; i < 100; i++)
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.newInstance0("11");
		for (int i = 0; i < 100; i++)
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = constructor.newInstance("11");
		warmup = false;

		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = access.newInstance0("11");
			end("ConstructorAccess");
		}
		for (int i = 0; i < 100; i++) {
			start();
			for (int ii = 0; ii < count; ii++)
				dontCompileMeAway[ii] = constructor.newInstance("11");
			end("Constructor");
		}

		chart("Constructor");
	}

	static public class SomeClass {
		public String name;

		public SomeClass() {
		}

		public SomeClass(String name) {
			this.name = name;
		}
	}

	public static void main (String[] args) throws Exception {
		new ConstructorAccessBenchmark2();
	}
}
