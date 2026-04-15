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
 * @test SimpleSwingTest.java
 * @summary THIS TEST NEEDS TO BE RUN MANUALLY, IT WILL FAIL WITH JTREG! Basic Swing GUI test. It opens a window and closes it in 5 seconds.
 * @run main/othervm -XX:+UseG1GC -XX:+SanitizeGC -Xlog:gc+phases=debug,gc+task=debug,gc+region=trace gc.SanitizeGC.shouldfail.SimpleSwingTest
 */

package gc.SanitizeGC.shouldfail;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleSwingTest {
    public static void main(String[] args) {
        // create a frame
        JFrame frame = new JFrame("A random frame");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add a label
        JLabel label = new JLabel("This GUI application will close itself in 5 seconds...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        frame.add(label);

        // center on screen and show
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // schedule the window close after 5 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                frame.dispose(); // close the window
                System.exit(0);  // exit
            }
        }, 5000);
    }
}