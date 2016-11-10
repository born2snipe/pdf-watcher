/**
 * Copyright to the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.born2snipe.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryWatcherTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private TestListener listener;
    private File listenToDir;
    private DirectoryWatcher watcher;

    @Before
    public void setUp() throws Exception {
        listenToDir = tmpFolder.newFolder("listen-to-dir");
        createFileIn(listenToDir, "existing.txt");

        listener = new TestListener();
        watcher = new DirectoryWatcher();
        watcher.addDir(listenToDir);
    }

    @After
    public void tearDown() throws Exception {
        watcher.stop();
    }

    @Test
    public void shouldAllowListeningToMultipleDirectories() throws IOException, InterruptedException {
        File otherFolder = tmpFolder.newFolder("other-folder");
        watcher.addDir(otherFolder);
        watcher.start(listener);

        String filename = "test.txt";

        Thread.sleep(1000L);
        createFileIn(listenToDir, filename);
        createFileIn(otherFolder, filename);

        listener.assertFileWasCreatedIn(listenToDir, filename);
        listener.assertFileWasCreatedIn(otherFolder, filename);
    }

    @Test
    public void shouldNotifyWhenAFileHasBeenModified() throws InterruptedException {
        watcher.start(listener);

        /*
            Total hack to make this test work more consistent, at least on a Mac.
            A race condition between the WatchService finding all the files in the directory
              and making the modification here.
         */
        Thread.sleep(1000L);

        String filename = "existing.txt";
        modifyFileIn(listenToDir, filename);

        listener.assertFileWasModifiedIn(listenToDir, filename);
    }

    @Test
    public void shouldNotifyWhenAFileHasBeenDeleted() {
        watcher.start(listener);

        String filename = "existing.txt";
        new File(listenToDir, filename).delete();

        listener.assertFileWasDeletedIn(listenToDir, filename);
    }

    @Test
    public void shouldNotifyWhenAFileHasBeenCreated() {
        watcher.start(listener);

        String filename = "temp.txt";

        createFileIn(listenToDir, filename);

        listener.assertFileWasCreatedIn(listenToDir, filename);
    }

    private void createFileIn(File dir, String filename) {
        File file = new File(dir, filename);
        try (OutputStream output = new FileOutputStream(file)) {
            output.write(UUID.randomUUID().toString().getBytes());
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void modifyFileIn(File dir, String filename) {
        File file = new File(dir, filename);
        try (OutputStream output = new FileOutputStream(file, true)) {
            output.write(UUID.randomUUID().toString().getBytes());
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}