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
import cli.pi.io.PressingOfEnterListener;
import com.github.born2snipe.cli.io.DirectoryWatcher;
import net.sourceforge.argparse4j.impl.Arguments;
import org.openide.util.lookup.ServiceProvider;

import java.io.File;
import java.util.concurrent.CountDownLatch;

@ServiceProvider(service = CliCommand.class)
public class WatchAndRegeneratePdfCommand extends GeneratePdfCommand {
    private final CountDownLatch waitForControlC = new CountDownLatch(1);
    private final Object GENERATING_PDF_LOCK = new Object();

    public WatchAndRegeneratePdfCommand() {
        argsParser.addArgument("-dw", "--debug-watch")
                .type(boolean.class)
                .action(Arguments.storeTrue())
                .dest("debug")
                .help("Display all watch events to the console");
    }

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
        final boolean debug = context.getNamespace().get("debug");

        PressingOfEnterListener pressingOfEnterListener = new PressingOfEnterListener() {
            public void enterPressed() {
                synchronized (GENERATING_PDF_LOCK) {
                    WatchAndRegeneratePdfCommand.super.executeParsedArgs(context);
                    logWatchingMessage(log);
                }
            }
        };
        pressingOfEnterListener.start();


        DirectoryWatcher watcher = new DirectoryWatcher();
        watcher.addDir(input.getParentFile());
        watcher.start((file, kindOfChange) -> {
            File changedFile = file.toFile();
            if (debug) {
                log.info("@|yellow [FILE CHANGE]|@ " + kindOfChange + " -- " + file);
            }

            if (changedFile.equals(input)) {
                synchronized (GENERATING_PDF_LOCK) {
                    WatchAndRegeneratePdfCommand.super.executeParsedArgs(context);
                    logWatchingMessage(log);
                }
            }
        });

        logWatchingMessage(log);
        try {
            waitForControlC.await();
        } catch (InterruptedException e) {

        }
    }

    private void logWatchingMessage(CliLog log) {
        log.info("\n@|yellow,bold Watching for changes...|@ or Press @|green [ENTER]|@ to regenerate pdf");
    }

    public void simulateControlC() {
        waitForControlC.countDown();
    }
}
