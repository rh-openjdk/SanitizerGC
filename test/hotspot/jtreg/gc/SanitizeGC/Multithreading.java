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
 * @test Multithreading.java
 * @summary SanitizeGC test for a basic multithreading task of allocating objets.
 * @build gc.SanitizeGC.SanitizeGCTestObj
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.Multithreading
 */

package gc.SanitizeGC;

import java.util.ArrayList;
import java.util.List;
import gc.SanitizeGC.SanitizeGCTestObj;

class AllocateObjects extends Thread {
    private List<SanitizeGCTestObj> list;

    AllocateObjects() {
        this.list = new ArrayList();
    }

    public void run() {
        for (int i = 0; i < 10_000; i++) {
            list.add(new SanitizeGCTestObj(1000));
        }
        System.out.println(Thread.currentThread().getName() + " finished running, allocated " + getList().size() + " objects.");
    }

    public List<SanitizeGCTestObj> getList() {
        return this.list;
    }
}

public class Multithreading {
    public static void main(String[] args) {
        AllocateObjects t1 = new AllocateObjects();
        AllocateObjects t2 = new AllocateObjects();
        AllocateObjects t3 = new AllocateObjects();
        AllocateObjects t4 = new AllocateObjects();
        AllocateObjects t5 = new AllocateObjects();

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (t1.getList().size() == 10_000 && t2.getList().size() == 10_000 && t3.getList().size() == 10_000 && t4.getList().size() == 10_000 && t5.getList().size() == 10_000) {
            System.out.println("All threads correctly allocated the right number of objects.");
        } else {
            throw new AssertionError("Some thread allocated a wrong number of objects.");
        }
    }
}