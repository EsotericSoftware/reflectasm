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
