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
 * @test SubprocessTest.java
 * @summary SanitizeGC test starting a simple subprocess.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.SubprocessTest
 */

package gc.SanitizeGC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class SubprocessTest {
    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "echo $TEST_VAR");

        // add a custom environment variable to the process
        Map<String, String> env = pb.environment();
        env.put("TEST_VAR", "SANITIZEGC");

        System.out.println("Starting sub-process...");
        Process p = pb.start();

        // read the output from the child process
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String output = reader.readLine();
            int exitCode = p.waitFor();

            System.out.println("Process exited with code: " + exitCode);
            System.out.println("Process output: " + output);

            if (exitCode != 0) {
                throw new RuntimeException("Process failed with exit code " + exitCode);
            }

            if (!"SANITIZEGC".equals(output)) {
                throw new RuntimeException("Unexpected output: " + output);
            }
        }

        System.out.println("Everything was okay!");
    }
}
