/**
 *
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.born2snipe.cli;

import cli.pi.CliLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MockCliLog extends CliLog {
    private List<String> printedLines = new ArrayList<>();
    private List<Listener> listeners = new ArrayList<>();
    private boolean printLines;

    public MockCliLog() {
        this(false);
    }

    public MockCliLog(boolean printTheLines) {
        this.printLines = printTheLines;
    }

    public synchronized void println(String message) {
        if (printLines) {
            super.println(message);
        }

        printedLines.add(message);

        for (Listener listener : listeners) {
            listener.messagePrinted(message);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public Collection<String> getLines() {
        return Collections.unmodifiableList(printedLines);
    }

    public interface Listener {
        void messagePrinted(String message);
    }
}
