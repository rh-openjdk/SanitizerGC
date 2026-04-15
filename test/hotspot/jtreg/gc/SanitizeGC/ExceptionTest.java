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
 * @test ExceptionTest.java
 * @summary Basic test that catches an exception.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.ExceptionTest
 */

package gc.SanitizeGC;

public class ExceptionTest {

    static void crash() {
        // this will throw ArithmeticException.
        int x = 10 / 0;
        System.out.println(x);
    }

    public static void main(String[] args) {
        try {
            crash();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
        }
    }
}
