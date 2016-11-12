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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertTrue;

public class WatchAndRegeneratePdfCommandIntegrationTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private WatchAndRegeneratePdfCommand cmd;
    private File workingDir;
    private File inputDir;
    private File inputFile;
    private File outputDir;
    private File outputFile;
    private long outputFileLastModifiedAt;

    @Before
    public void setUp() throws Exception {
        workingDir = tmpFolder.newFolder("working");
        inputDir = tmpFolder.newFolder("input");
        inputFile = new File(inputDir, "test.html");
        outputDir = tmpFolder.newFolder("output");
        outputFile = new File(outputDir, "test.pdf");

        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.html")) {
            Files.copy(input, inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        Files.write(outputFile.toPath(), "test".getBytes(), StandardOpenOption.CREATE_NEW);
        outputFileLastModifiedAt = outputFile.lastModified();

        cmd = new WatchAndRegeneratePdfCommand();
    }

    @Test
    public void shouldNotHaveAnIndefiniteLoopIfTheOutputPdfIsInTheSameDirectoryAsTheInputFile() {
        outputFile = new File(inputFile.getParentFile(), "test.pdf");

        UserMakingChangesToHtmlFromAnotherEditor user = new UserMakingChangesToHtmlFromAnotherEditor(10, inputFile) {
            public void allModificationsCompleted() {
                cmd.simulateControlC();
            }
        };
        user.start();

        cmd.execute(new CliLog(), workingDir, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
    }

    @Test
    public void shouldRegeneratePdfAsChangesAreMade() {
        UserMakingChangesToHtmlFromAnotherEditor user = new UserMakingChangesToHtmlFromAnotherEditor(10, inputFile) {
            public void allModificationsCompleted() {
                cmd.simulateControlC();
            }
        };
        user.start();

        cmd.execute(new CliLog(), workingDir, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        assertTrue(outputFile.exists());
        assertTrue(outputFileLastModifiedAt < outputFile.lastModified());
        assertTrue(outputFile.lastModified() - outputFileLastModifiedAt > 2000L);
    }
}