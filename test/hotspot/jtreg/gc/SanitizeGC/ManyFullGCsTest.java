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
 * @test ManyFullGCsTest.java
 * @summary Basic SanitizeGC test that allocates a large object and then triggers a full GC. 20 times in total.
 * @build gc.SanitizeGC.SanitizeGCTestObj
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.ManyFullGCsTest
 */

package gc.SanitizeGC;

import java.lang.ref.WeakReference;
import gc.SanitizeGC.SanitizeGCTestObj;

public class ManyFullGCsTest {
    public static void main(String[] args) {
        SanitizeGCTestObj obj0 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj1 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj2 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj3 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj4 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj5 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj6 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj7 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj8 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj9 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj10 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj11 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj12 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj13 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj14 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj15 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj16 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj17 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj18 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj19 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
        SanitizeGCTestObj obj20 = new SanitizeGCTestObj(10_000);
        triggerFullGC();
    }

    private static void triggerFullGC() {
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
