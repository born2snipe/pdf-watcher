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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class UserMakingChangesToHtmlFromAnotherEditor extends Thread {
    private final String HTML = "<html>\n" +
            "<body>\n" +
            "<h1>{placeholder}</h1>\n" +
            "</body>\n" +
            "</html>";
    private final File inputFile;
    private int numberOfModifications;


    public UserMakingChangesToHtmlFromAnotherEditor(int numberOfModifications, File inputFile) {
        this.numberOfModifications = numberOfModifications;
        this.inputFile = inputFile;
    }

    public void run() {
        for (int i = 0; i < numberOfModifications; i++) {
            String updatedHtml = HTML.replace("{placeholder}", "#" + i);

            try {
                Files.write(inputFile.toPath(), updatedHtml.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pause();
        }
        allModificationsCompleted();
    }

    public void allModificationsCompleted() {

    }

    private void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }
    }
}
