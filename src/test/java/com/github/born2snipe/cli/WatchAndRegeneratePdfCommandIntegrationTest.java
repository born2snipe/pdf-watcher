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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertEquals;
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
    private Developer dev;

    @Before
    public void setUp() throws Exception {
        workingDir = tmpFolder.newFolder("working");
        inputDir = tmpFolder.newFolder("input");
        inputFile = TestHtmlFile.writeTo(inputDir);
        outputDir = tmpFolder.newFolder("output");
        outputFile = new File(outputDir, "test.pdf");

        Files.write(outputFile.toPath(), "test".getBytes(), StandardOpenOption.CREATE_NEW);
        outputFileLastModifiedAt = outputFile.lastModified();

        cmd = new WatchAndRegeneratePdfCommand();
        dev = new Developer(cmd);
    }

    @Test
    public void shouldHandleWhenTryingToProcessAnInputFileFromTheWorkingDirectory() {
        inputFile = TestHtmlFile.writeTo(workingDir);

        dev.willKillTheAppAfterSeeingTheFileGenerated();

        cmd.execute(dev.getTerminal(), workingDir, inputFile.getName(), outputFile.getAbsolutePath());
    }

    @Test
    public void shouldNotHaveAnIndefiniteLoopIfTheOutputPdfIsInTheSameDirectoryAsTheInputFile() {
        outputFile = new File(inputFile.getParentFile(), "test.pdf");

        dev.willEditTheFileOnce(inputFile);
        dev.willKillTheAppAfterPonderingCareerChoiceForAFewMomentsAfterDoneEditingTheFile();
        dev.startsWorking();

        cmd.execute(dev.getTerminal(), workingDir, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        assertEquals(2, dev.getNumberOfTimesThePdfWasGenerated());
    }

    @Test
    public void shouldRegeneratePdfAsChangesAreMade() {
        dev.willEditTheFile(inputFile, 3);
        dev.startsWorking();

        cmd.execute(dev.getTerminal(), workingDir, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        assertEquals(4, dev.getNumberOfTimesThePdfWasGenerated());
        assertTrue(outputFile.exists());
        assertTrue(outputFileLastModifiedAt < outputFile.lastModified());
    }

    @Test
    public void shouldNotAttemptToRegenerateFileIfTheInputFileWasDeleted() {
        dev.willDeleteAndExitApp(inputFile);
        dev.startsWorking();

        cmd.execute(dev.getTerminal(), workingDir, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        assertEquals(1, dev.getNumberOfTimesThePdfWasGenerated());
        assertEquals(0, dev.getNumberOfErrorsSeen());
    }
}