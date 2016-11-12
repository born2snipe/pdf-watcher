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
package com.github.born2snipe.cli.io;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.born2snipe.AssertRetry.assertRetry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestListener implements DirectoryWatcher.Listener {
    private static final Duration MAX_DURATION = Duration.ofSeconds(30);
    private List<File> filesCreated = Collections.synchronizedList(new ArrayList<>());
    private List<File> filesDeleted = Collections.synchronizedList(new ArrayList<>());
    private List<File> filesModified = Collections.synchronizedList(new ArrayList<>());


    @Override
    public void fileCreated(Path file) {
        filesCreated.add(new File(file.toAbsolutePath().toString()));
    }

    @Override
    public void fileDeleted(Path file) {
        filesDeleted.add(new File(file.toAbsolutePath().toString()));
    }

    @Override
    public void fileModified(Path file) {
        filesModified.add(new File(file.toAbsolutePath().toString()));
    }

    public void assertFileWasCreated() {
        assertRetry(MAX_DURATION, () -> {
            assertTrue("no files were created", filesCreated.size() > 0);
        });
    }

    public void assertFileWasCreatedIn(File dir, String expectedFilename) {
        final File expectedFile = new File(dir, expectedFilename);

        assertRetry(MAX_DURATION, () -> {
            assertTrue("file does not exist: " + expectedFile, filesCreated.contains(expectedFile));
        });
    }

    public void assertFileWasDeletedIn(File dir, String expectedFilename) {
        final File expectedFile = new File(dir, expectedFilename);

        assertRetry(MAX_DURATION, () -> {
            assertTrue("file was not deleted: " + expectedFile, filesDeleted.contains(expectedFile));
        });
    }

    public void assertFileWasModifiedIn(File dir, String expectedFilename) {
        final File expectedFile = new File(dir, expectedFilename);

        assertRetry(MAX_DURATION, () -> {
            assertTrue("file was not modified: " + expectedFile, filesModified.contains(expectedFile));
        });
    }

    public void assertNoFileWasDeleted() {
        assertTrue(filesDeleted.isEmpty());
    }

    public void assertNoFileWasCreated() {
        assertTrue(filesCreated.isEmpty());
    }

    public void assertFileWasDeleted() {
        assertRetry(MAX_DURATION, () -> {
            assertFalse(filesDeleted.isEmpty());
        });
    }
}
