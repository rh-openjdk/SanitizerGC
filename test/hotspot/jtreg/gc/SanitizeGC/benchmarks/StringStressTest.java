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
 * @test StringStressTest.java
 * @summary A GC stress test using Strings.
 * @run main/othervm/timeout=300 -XX:+UseG1GC -XX:+SanitizeGC -Xmx400m -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.benchmarks.StringStressTest
 */

package gc.SanitizeGC.benchmarks;

public class StringStressTest {
    public static void main(String[] args) {
        System.out.println("Starting simple GC stress test (String version).");

        long counter = 0;
        try {
            for (int i = 0; i < 25_000; i++) {
                // allocate a lot of short-lived String objects
                String[] data = new String[1024];
                for (int j = 0; j < data.length; j++) {
                    // generate some string content
                    data[j] = new String("#" + i + "|" + j + " jrnfkdmendianjenajkdsnvasjkdrnwekjfnajksdfnakjewnrfjfs" +
                    "fadsfadsfadfaejlknvmaoeirjaoijfdakjhekbnvakljjbenbasdmfnaelfndjsbfjaksdlraewerjwekrsdjfkldsjvkls" +
                    "fjakenklvnskldaneknaklsdnfkacvznldnalkjewnfkjalsdhfajlsdfakljsdfhajskdlfhasjkldfhasdkjlfhasdljkf" +
                    "asdfjelwknvnjscvjkasdhrejwwbffjkadnbfalkjenfakdsjnjakvslbvajkenjakldvnasdhfjefbnjkxcbvchkjxbveee" +
                    "aenjkxncvuirenwuinuiasbdyuiadsbfahjkbeamfbscvbaioueyrtqppasdfasdufasdfasdfhasdhcvauisdvbckvhkvha" +
                    "byuviqwertyqweprqwyeurioywetyuasdfasdlfadslfhlkhghjkzxcvzxcmzxcvbznmxvnbmdfgakhjsdflaeoiryusfgas" +
                    "weruioqpuewroiqweuroiqweurqiopweurqoipwehfadsjhbvajkhspeworuqwoepruqwioerusiovhsashdkjfahsjdfasd" +
                    "431890327409127650661230946123940060151327098902357129374819237412390847128571059751092375161066" +
                    "[][/''],/[]'[;;,l'],/.[/;,.][/,./[,-=-#$%@^!&^$,/.',;[],l;/,;l=;,;/,.-/=,/;l][,/];/'],/,]/'()^&'" +
                    "FHJAODHFAENJNVPAUIOUERAPUSEIORADJVAOISDJFAIEWOFJAISDFASDBFASDFDSFADSFHIAGSEFHAUISODHFASDIFHAOSDD" +
                    "abcdefghijklmnopqrstuvwxyzABDCEFGHIJKLMNOPQRSTUVWXYZ01234567890abcdefghijklmnopqrstuvwxyzABCDEFG");
                }
                counter++;
                if (counter % 10 == 0) {
                    System.out.println("Iteration: " + counter);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory after " + counter + " iterations.");
        }
    }
}
