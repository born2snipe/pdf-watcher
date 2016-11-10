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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher {
    private ArrayList<Path> directoriesToWatch = new ArrayList<>();
    private ExecutorService executor;
    private CountDownLatch waitForThreadToBegin;
    private WatchService watcher;

    public void start(Listener listener) {
        executor = Executors.newFixedThreadPool(directoriesToWatch.size());
        waitForThreadToBegin = new CountDownLatch(directoriesToWatch.size());
        try {
            watcher = FileSystems.getDefault().newWatchService();

            Path dir = directoriesToWatch.iterator().next();
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            executor.submit(newRunnable(watcher, listener, dir));
            waitForThreadToBegin.await(10, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        closeWatcher();
        killThreads();
    }

    public void addDir(File directoryToListenTo) {
        directoriesToWatch.add(directoryToListenTo.toPath());
    }

    private void killThreads() {
        executor.shutdownNow();
        while (!executor.isTerminated()) {
            sleep();
        }
    }

    private void closeWatcher() {
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException e) {

            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {

        }
    }

    private Runnable newRunnable(final WatchService watcher, final Listener listener, final Path dir) {
        return () -> {
            waitForThreadToBegin.countDown();
            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;

                    Path filePath = Paths.get(dir.toString(), ev.context().toString());
                    if (kind == ENTRY_DELETE) {
                        listener.fileDeleted(filePath);
                    } else if (kind == ENTRY_CREATE) {
                        listener.fileCreated(filePath);
                    } else if (kind == ENTRY_MODIFY) {
                        listener.fileModified(filePath);
                    }

                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        };
    }

    interface Listener {
        void fileCreated(Path file);

        void fileDeleted(Path file);

        void fileModified(Path file);
    }

}
