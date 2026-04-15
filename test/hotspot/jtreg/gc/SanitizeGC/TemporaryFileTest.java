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
 * @test TemporaryFileTest.java
 * @summary A test for creating a temporary file, writing to it and reading its content back.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.TemporaryFileTest
 */

package gc.SanitizeGC;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TemporaryFileTest {
    public static void main(String[] args) throws Exception {
        String content = "Hello, SanitizeGC!";

        // create a temporary file
        Path tempFile = Files.createTempFile("sanitizegc_tempfile", ".txt");

        try {
            System.out.println("Created temporary file at: " + tempFile.toAbsolutePath());
            Files.writeString(tempFile, content); // write content to the file
            String readContent = Files.readString(tempFile); // read content back from the file

            if (content.equals(readContent)) {
                System.out.println("Content matches.");
            } else {
                throw new RuntimeException("Content mismatch. Expected: " + content + ", but got: " + readContent);
            }
        } finally {
            // clean up the file after the test
            Files.deleteIfExists(tempFile);
            System.out.println("Temporary file deleted.");
        }
    }
}
