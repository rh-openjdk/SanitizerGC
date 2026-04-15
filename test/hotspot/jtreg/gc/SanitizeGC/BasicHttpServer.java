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
 * @test BasicHttpServer.java
 * @summary Testing a basic HTTP server functionality.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.BasicHttpServer
 */

package gc.SanitizeGC;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.lang.AssertionError;

public class BasicHttpServer {
    public static void main(String[] args) throws IOException {
        // create a basic server on port 8998
        HttpServer server = HttpServer.create(new InetSocketAddress(8998), 0);
        server.createContext("/", new ResponseHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP server started on http://localhost:8998.");

        // try getting a response from the server
        try {
            Thread.sleep(1000);
            URL url = new URL("http://localhost:8998/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            if (response.toString().equals("Hello there!")) {
                System.out.println("Correct response from the server was received.");
            } else {
                server.stop(0);
                throw new AssertionError("Incorrect response from the server was received: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            server.stop(0);
            System.exit(1);
        }

        server.stop(0);
    }

    // handler that returns "Hello there!"
    static class ResponseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello there!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
