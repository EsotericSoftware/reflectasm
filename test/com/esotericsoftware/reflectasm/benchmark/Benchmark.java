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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

public class Benchmark {
	public boolean warmup = true;
	public HashMap<String, Long> testTimes = new HashMap();
	private long s;

	public void start () {
		s = System.nanoTime();
	}

	public void end (String name) {
		if (warmup) return;
		long e = System.nanoTime();
		long time = e - s;
		Long oldTime = testTimes.get(name);
		if (oldTime == null || time < oldTime) testTimes.put(name, time);
		System.out.println(name + ": " + time / 1000000f + " ms");
	}

	public void chart (String title) {
		Comparator<Entry> comparator = new Comparator<Entry>() {
			public int compare (Entry o1, Entry o2) {
				// return ((String)o1.getKey()).compareTo((String)o2.getKey());
				return (int)((Long)o1.getValue() - (Long)o2.getValue());
			}
		};
		ArrayList<Entry> list = new ArrayList(testTimes.entrySet());
		Collections.sort(list, comparator);

		StringBuilder names = new StringBuilder(512);
		StringBuilder times = new StringBuilder(512);
		long max = 0;
		int count = 0;
		for (Entry<String, Long> entry : list) {
			String name = entry.getKey();
			names.insert(0, '|');
			names.insert(0, name);
			long time = entry.getValue();
			times.append(time);
			times.append(',');
			max = Math.max(max, time);
			count++;
		}
		times.setLength(times.length() - 1);
		names.setLength(names.length() - 1);
		int height = count * 18 + 21;
		int width = Math.min(700, 300000 / height);
		System.out.println("[img]http://chart.apis.google.com/chart?chtt=" + title + "&" + "chs=" + width + "x" + height
			+ "&chd=t:" + times + "&chds=0," + max + "&chxl=0:|" + names + "&cht=bhg&chbh=10&chxt=y&"
			+ "chco=660000|660033|660066|660099|6600CC|6600FF|663300|663333|"
			+ "663366|663399|6633CC|6633FF|666600|666633|666666[/img]\n");
	}
}
