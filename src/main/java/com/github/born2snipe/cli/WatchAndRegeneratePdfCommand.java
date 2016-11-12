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
import cli.pi.command.CliCommand;
import cli.pi.command.CommandContext;
import com.github.born2snipe.cli.io.DirectoryWatcher;
import org.openide.util.lookup.ServiceProvider;

import java.io.File;
import java.util.concurrent.CountDownLatch;

@ServiceProvider(service = CliCommand.class)
public class WatchAndRegeneratePdfCommand extends GeneratePdfCommand {
    private final CountDownLatch waitForControlC = new CountDownLatch(1);

    @Override
    public String getName() {
        return "watch-and-regenerate";
    }

    @Override
    public String getDescription() {
        return "Watch the directory of the input file for changes and regenerate the PDF";
    }

    @Override
    protected void executeParsedArgs(CommandContext context) {
        super.executeParsedArgs(context);

        final CliLog log = context.getLog();

        final File input = context.getNamespace().get("input");

        DirectoryWatcher watcher = new DirectoryWatcher();
        watcher.addDir(input.getParentFile());
        watcher.start((file, kindOfChange) -> {
            File changedFile = file.toFile();
            if (changedFile.equals(input)) {
                WatchAndRegeneratePdfCommand.super.executeParsedArgs(context);
                logWatchingMessage(log);
            }
        });

        logWatchingMessage(log);
        try {
            waitForControlC.await();
        } catch (InterruptedException e) {

        }
    }

    private void logWatchingMessage(CliLog log) {
        log.warn("\nWatching for changes...\n");
    }

    public void simulateControlC() {
        waitForControlC.countDown();
    }
}
