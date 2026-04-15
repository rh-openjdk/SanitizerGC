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
 * @test ArrayAndListBenchmark.java
 * @summary Benchmark comparing the speed between a primitive integer array and an ArrayList of integers.
 * @run main/othervm/timeout=300 -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.benchmarks.ArrayAndListBenchmark
 */

package gc.SanitizeGC.benchmarks;

import java.util.ArrayList;
import java.util.List;

public class ArrayAndListBenchmark {
    private static final int ELEMS = 100_000;

    public static void main(String[] args) {
        System.out.println("Starting Memory Benchmark.");

        for (int i = 0; i < 50; i++) {
            System.out.printf(">>> Iteration %d <<<\n", i);

            // primitive integer array
            long startP = System.nanoTime();
            int[] primitives = runPrimitiveTest(ELEMS);
            long endP = System.nanoTime();

            // boxed integers (in an ArrayList)
            long startB = System.nanoTime();
            List<Integer> boxed = runBoxedTest(ELEMS);
            long endB = System.nanoTime();

            System.out.printf("Array: %.2f ms\n", ((endP - startP) / 1_000_000.0));
            System.out.printf("ArrayList: %.2f ms\n", ((endB - startB) / 1_000_000.0));

            // prevent the lists from being optimized by the JIT compilers during the loop
            if (primitives.length != boxed.size()) {
                throw new RuntimeException();
            }
        }
    }

    private static int[] runPrimitiveTest(int count) {
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) {
            arr[i] = i;
        }
        return arr;
    }

    private static List<Integer> runBoxedTest(int count) {
        List<Integer> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(i);
        }
        return list;
    }
}
