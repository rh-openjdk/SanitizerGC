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
 * @test LoopbackConnectionTest.java
 * @summary Basic loopback test with a server sending a byte to a connected client.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.LoopbackConnectionTest
 */

package gc.SanitizeGC;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

public class LoopbackConnectionTest {
    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();

        // server side -- start a server on an ephemeral port
        try (ServerSocket server = new ServerSocket(0, 50, loopback)) {
            int port = server.getLocalPort();
            System.out.println("Server started on " + loopback + ":" + port);

            // start a thread to handle the client connection
            Thread serverThread = new Thread(() -> {
                try (Socket accept = server.accept();
                     OutputStream out = accept.getOutputStream()) {
                    out.write(67); // send a random byte
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();

            // client side -- connect to the server
            try (Socket client = new Socket(loopback, port);
                 InputStream in = client.getInputStream()) {

                int result = in.read();
                if (result != 67) {
                    throw new RuntimeException("Unexpected data received: " + result);
                }
                System.out.println("Successfully received data.");
            }

            serverThread.join(5000); // wait for server thread to finish
        }
    }
}
