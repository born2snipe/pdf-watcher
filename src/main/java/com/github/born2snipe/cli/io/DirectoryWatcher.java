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
package com.github.born2snipe.cli.io;

import rx.Observable;
import rx.Scheduler;
import rx.fileutils.FileSystemEvent;
import rx.fileutils.FileSystemEventKind;
import rx.fileutils.FileSystemWatcher;
import rx.schedulers.Schedulers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static rx.fileutils.FileSystemEventKind.*;

public class DirectoryWatcher {
    private ArrayList<Path> directoriesToWatch = new ArrayList<>();

    public void start(Listener listener) {
        Scheduler scheduler = Schedulers.newThread();
        FileSystemWatcher.Builder builder = FileSystemWatcher.newBuilder();
        builder.withScheduler(scheduler);

        for (Path path : directoriesToWatch) {
            builder.addPath(path, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        }
        Observable<FileSystemEvent> observable = builder.build();

        observable.subscribe(fileSystemEvent -> {
            FileSystemEventKind kind = fileSystemEvent.getFileSystemEventKind();
            Path path = fileSystemEvent.getPath();

            // this appears to be only a problem on Mac...
            Path massaged = Paths.get(path.toString().replaceFirst("^/private", ""));

            if (kind == ENTRY_CREATE) {
                listener.fileCreated(massaged);
            } else if (kind == ENTRY_MODIFY) {
                listener.fileModified(massaged);
            } else if (kind == ENTRY_DELETE) {
                listener.fileDeleted(massaged);
            }
        });
    }

    public void stop() {

    }

    public void addDir(String directoryToListenTo) {
        addDir(new File(directoryToListenTo));
    }

    public void addDir(File directoryToListenTo) {
        // todo - handle relative paths
        // todo - handle ~ path syntax

        if (!directoryToListenTo.exists()) {
            throw new DirectoryDoesNotExistException(directoryToListenTo.toString());
        } else if (!directoryToListenTo.isDirectory()) {
            throw new NotADirectoryException(directoryToListenTo.toString());
        }

        directoriesToWatch.add(directoryToListenTo.toPath());
    }


    public interface Listener {
        void fileCreated(Path file);

        void fileDeleted(Path file);

        void fileModified(Path file);
    }

}
