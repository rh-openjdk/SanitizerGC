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
 * @test TestSanitizeGC.java
 * @summary Basic SanitizeGC test.
 * @run main/othervm -XX:-UseCompressedOops -XX:-UseCompressedClassPointers -XX:+SanitizeGC -Xlog:gc+remset=trace,gc+refine=trace,gc+barrier=trace,gc+phases=trace,gc+task=debug,gc+verify=debug,gc+region=trace -Xint gc.SanitizeGC.TestSanitizeGC
 */

package gc.SanitizeGC;

public class TestSanitizeGC {
    public static void main(String args[]) {
        Integer printInt = new Integer(0);
        for (int i = 0; i < 1000; i++) {
            Integer j = 123;
            if (i == 500) {
                printInt = j;
            }
        }
        System.gc();
        System.out.println("end " + printInt);
    }
}