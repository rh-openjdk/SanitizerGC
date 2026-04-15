/*
 * Copyright (c) 2026, IBM.
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
 * @test CalculationsBenchmark.java
 * @summary Stress test allocating a lot of objects and doing calculations on them.
 * @build gc.SanitizeGC.benchmarks.SanitizeGCTestObj
 * @run main/othervm/timeout=300 -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.benchmarks.CalculationsBenchmark
 */

package gc.SanitizeGC.benchmarks;

import java.util.ArrayList;
import java.util.List;

import gc.SanitizeGC.SanitizeGCTestObj;

public class CalculationsBenchmark {
    public static void main(String[] args) {
        System.out.println("Starting a benchmark doing basic calculations.");

        try {
            for (int i = 0; i < 10_000; i++) {
                // allocate a lot of objects
                List<SanitizeGCTestObj> list = new ArrayList<>();
                for(int j = 0; j < 250; j++) {
                    SanitizeGCTestObj obj = new SanitizeGCTestObj(1024);
                    obj.generateRandomData();
                    list.add(obj);
                }

                long xorSum = 0;
                double operations = 0;

                // do some calculations on the objects
                for(int k = 249; k >= 0; k--) {
                    SanitizeGCTestObj current = list.get(k);
                    byte[] data = current.getData();
                    for (byte b : data) {
                        int value = b & 0xFF; // convert it to unsigned
                        xorSum ^= value; // add it to the XOR "sum"
                        operations += Math.sin(value / 255.0); // normalize the value and get its Sine
                        if (value != 0) {
                            operations += (Math.PI / value); // floating point division
                        }
                        operations += Math.pow(value, 1.1); // power of 1.1
                    }
                }

                if (i % 100 == 0) {
                    System.out.printf("Iteration: %d | xorSum: %d | operations: %.4f\n", i, xorSum, operations);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory.");
        }
    }
}
