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
 * @test ObjectStressTest.java
 * @summary A GC stress test allocating millions of objects of different sizes.
 * @build gc.SanitizeGC.benchmarks.SanitizeGCTestObj
 * @run main/othervm/timeout=300 -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.benchmarks.ObjectStressTest
 */

package gc.SanitizeGC.benchmarks;

import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;
import gc.SanitizeGC.SanitizeGCTestObj;

public class ObjectStressTest {
    public static void main(String[] args) {
        System.out.println("Starting an object stress test.");

        try {
            for (int i = 0; i < 25_000; i++) {
                // allocate a lot of short-lived objects of different sizes
                List<SanitizeGCTestObj> list = new ArrayList<>();
                for(int j = 0; j < 2000; j++) {
                    SanitizeGCTestObj obj;
                    if (true) {
                        obj = new SanitizeGCTestObj(1234);
                    } else if (j % 3 == 0) {
                        obj = new SanitizeGCTestObj(4236);
                    } else {
                        obj = new SanitizeGCTestObj(394);
                    }

                    list.add(obj);
                }

                if (i % 10 == 0) {
                    System.out.println("Iteration: " + i);
                }

                if (i == 12500) {
                    System.out.println("Triggering full GC.");
                    // Uses a hack to trigger a full gc here, from:
                    // https://stackoverflow.com/a/6915221
                    Object obj = new Object();
                    WeakReference ref = new WeakReference<Object>(obj);
                    obj = null;
                    while(ref.get() != null) {
                        System.gc();
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory.");
        }
    }
}
