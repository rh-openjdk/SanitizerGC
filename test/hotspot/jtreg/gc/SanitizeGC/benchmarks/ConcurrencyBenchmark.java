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
 * @test ConcurrencyBenchmark.java
 * @summary Benchmarks working with multiple threads, incrementing an integer.
 * @run main/othervm/timeout=300 -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.benchmarks.ConcurrencyBenchmark
 */

package gc.SanitizeGC.benchmarks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

public class ConcurrencyBenchmark {
    private static final int THREAD_COUNT = 8;
    private static final int INCREMENTS_PER_THREAD = 1_000_000;

    private int syncCounter = 0;
    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        ConcurrencyBenchmark bench = new ConcurrencyBenchmark();
        System.out.println("Starting concurrency benchmark with " + THREAD_COUNT + " threads.");

        // run the benchmark 50 times
        for (int i = 0; i < 50; i++) {
            System.out.printf(">>> Iteration %d <<<\n", i);

            long startSync = System.nanoTime();
            bench.runSyncTest();
            long endSync = System.nanoTime();

            long startAtomic = System.nanoTime();
            bench.runAtomicTest();
            long endAtomic = System.nanoTime();

            // print the times it took
            System.out.printf("Run %d -- Synchronized: %.2f ms\n", i, (endSync - startSync) / 1_000_000.0);
            System.out.printf("Run %d -- AtomicInteger: %.2f ms\n", i, (endAtomic - startAtomic) / 1_000_000.0);
        }

    }

    // uses the "synchronized" block for adding to the counter
    private void runSyncTest() throws InterruptedException {
        syncCounter = 0;
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        Runnable r = () -> {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                synchronized(this) {
                    syncCounter++;
                }
            }
            latch.countDown();
        };
        startThreads(r, latch);
    }

    // uses the atomic incrementation for adding to the counter
    private void runAtomicTest() throws InterruptedException {
        atomicCounter.set(0);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        Runnable r = () -> {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                atomicCounter.incrementAndGet();
            }
            latch.countDown();
        };
        startThreads(r, latch);
    }

    private void startThreads(Runnable r, CountDownLatch latch) throws InterruptedException {
        // start all threads
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(r).start();
        }
        latch.await(); // wait for all threads to finish
    }
}
