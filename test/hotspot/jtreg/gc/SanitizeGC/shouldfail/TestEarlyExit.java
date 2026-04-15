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
 * @test TestEarlyExit.java
 * @summary THIS TEST SHOULD FAIL! Basic early exit test using System.exit() with random exit code.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.shouldfail.TestEarlyExit
 */

package gc.SanitizeGC.shouldfail;

public class TestEarlyExit {
    public static void main(String[] args) {
        byte[] data1 = new byte[1024];
        byte[] data2 = new byte[1024];
        byte[] data3 = new byte[1024];
        byte[] data4 = new byte[1024];
        byte[] data5 = new byte[1024];

        System.exit(67);

        // never reached
        byte[] data6 = new byte[1024];
        byte[] data7 = new byte[1024];
        byte[] data8 = new byte[1024];
        byte[] data9 = new byte[1024];
        byte[] data0 = new byte[1024];
    }
}
