/*
 * Copyright (c) 2025, IBM.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test PeriodicGCBenchmark.java
 * @summary Simple object GC stress test.
 * @build gc.SanitizeGC.benchmarks.SanitizeGCTestObj
 * @run main/othervm/timeout=300 -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -XX:G1PeriodicGCInterval=100 -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.benchmarks.PeriodicGCBenchmark
 */

package gc.SanitizeGC.benchmarks;

import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;
import gc.SanitizeGC.SanitizeGCTestObj;

public class PeriodicGCBenchmark {
    public static void main(String[] args) {
        System.out.println("Starting periodic GC benchmark, it should trigger a GC every 100 milliseconds.");

        try {
            for (int i = 0; i < 25_000; i++) {
                // allocate a lot of short-lived objects
                List<SanitizeGCTestObj> list = new ArrayList<>();
                for(int j = 0; j < 1000; j++) {
                    SanitizeGCTestObj obj = new SanitizeGCTestObj(1024);
                    list.add(obj);
                }
                if (i % 10 == 0) {
                    System.out.println("Iteration: " + i);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory.");
        }
    }
}
