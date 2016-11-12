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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GeneratePdfCommandTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private GeneratePdfCommand cmd;
    private File workingDir;
    private File inputDir;
    private File inputFile;
    private File outputDir;
    private File outputFile;

    @Before
    public void setUp() throws Exception {
        workingDir = tmpFolder.newFolder("working");
        inputDir = tmpFolder.newFolder("input");
        inputFile = new File(inputDir, "test.html");
        outputDir = tmpFolder.newFolder("output");
        outputFile = new File(outputDir, "test.pdf");

        copyTestHtmlTo(inputFile);

        cmd = new GeneratePdfCommand();
    }

    @Test
    public void shouldHandleWhenTheInputAndOutputFilesAreExpectedToBeInTheWorkingDirectory() {
        copyTestHtmlTo(new File(workingDir, inputFile.getName()));

        cmd.execute(new CliLog(), workingDir, inputFile.getName(), outputFile.getName());

        File expectedOutputFile = new File(workingDir, outputFile.getName());
        assertTrue(expectedOutputFile.exists());
        assertTrue(expectedOutputFile.length() > 0);
    }

    @Test
    public void shouldGenerateAPdf() {
        cmd.execute(new CliLog(), workingDir, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    @Test
    public void shouldMakeParentDirectoriesForTheOutputFile() throws IOException {
        File output = new File(outputDir, "output-child");
        File outputPdf = new File(output, "test.pdf");

        cmd.execute(new CliLog(), workingDir, inputFile.getAbsolutePath(), outputPdf.getAbsolutePath());

        assertTrue(outputPdf.getParentFile().exists());
    }

    @Test
    public void shouldBlowUpIfTheInputFileDoesNotExist() {
        assertCommandFails("File not found: 'does-not-exist.html'", "does-not-exist.html", "test.pdf");
    }

    @Test
    public void shouldBlowUpIfNoOutputFileIsProvided() {
        assertCommandFails("too few arguments");
    }

    @Test
    public void shouldBlowUpIfNoInputFileIsProvided() {
        assertCommandFails("", "test.pdf");
    }

    private void assertCommandFails(String expectedMissingArg, String... args) {
        try {
            cmd.execute(new CliLog(), workingDir, args);
            fail();
        } catch (RuntimeException e) {
            assertTrue("Actual message: " + e.getMessage(), e.getMessage().contains(expectedMissingArg));
        }
    }

    private void copyTestHtmlTo(File file) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.html")) {
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}