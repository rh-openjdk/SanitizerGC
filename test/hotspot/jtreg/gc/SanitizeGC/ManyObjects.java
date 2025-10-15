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
 * @test ManyObjects.java
 * @summary SanitizeGC test for allocating many objects.
 * @build gc.SanitizeGC.SanitizeGCTestObj
 * @run main/othervm -XX:-UseCompressedOops -XX:-UseCompressedClassPointers -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+remset=trace,gc+refine=trace,gc+barrier=trace,gc+phases=trace,gc+task=debug,gc+verify=debug,gc+region=trace gc.SanitizeGC.ManyObjects
 */

package gc.SanitizeGC;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import gc.SanitizeGC.SanitizeGCTestObj;

public class ManyObjects {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Creating objects that go out of scope immediately.");
        for (int i = 0; i < 10_000; i++) {
            SanitizeGCTestObj obj = new SanitizeGCTestObj(1000);
        }

        System.out.println("Creating objects to save in a list.");
        List<SanitizeGCTestObj> list = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            SanitizeGCTestObj obj = new SanitizeGCTestObj(1000);
            list.add(obj);
        }

        System.out.println("Working with object on index 248.");
        list.get(248).generateRandomData();
        String base64Data = Base64.getEncoder().encodeToString(list.get(248).getData());
        System.out.println("Data from object on index 248: " + base64Data);
    }
}
