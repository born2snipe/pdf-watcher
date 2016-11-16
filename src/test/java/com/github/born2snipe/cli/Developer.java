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
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class Developer {
    private final WatchAndRegeneratePdfCommand cmd;
    private MockCliLog cliLog = new MockCliLog(true);
    private Optional<Thread> userEditorAction = Optional.empty();
    private Optional<Function> afterAllModificationsHandler = Optional.empty();

    public Developer(WatchAndRegeneratePdfCommand cmd) {
        this.cmd = cmd;
    }

    public MockCliLog getTerminal() {
        return cliLog;
    }

    public Developer willKillTheAppAfterSeeingTheFileGenerated() {
        cliLog.addListener(message -> {
            if (isPdfGeneratedMessage(message)) {
                cmd.simulateControlC();
            }
        });
        return this;
    }

    public Developer willEditTheFileOnce(File fileToEdit) {
        return willEditTheFile(fileToEdit, 1);
    }

    public Developer willEditTheFile(File fileToEdit, int numberOfModifications) {
        userEditorAction = Optional.of(new UserMakingChangesToHtmlFromAnotherEditor(numberOfModifications, fileToEdit) {
            public void allModificationsCompleted() {
                if (afterAllModificationsHandler.isPresent()) {
                    afterAllModificationsHandler.get().apply(null);
                }
                cmd.simulateControlC();
            }
        });
        return this;
    }

    public void startsWorking() {
        if (userEditorAction.isPresent()) {
            cliLog.addListener(message -> {
                Thread thread = userEditorAction.get();
                if (isWatchingForChanges(message) && !thread.isAlive()) {
                    thread.start();
                }
            });
        }
    }

    public Developer willKillTheAppAfterPonderingCareerChoiceForAFewMomentsAfterDoneEditingTheFile() {
        afterAllModificationsHandler = Optional.of((Function) o -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {

            }
            return null;
        });
        return this;
    }

    public long getNumberOfTimesThePdfWasGenerated() {
        return cliLog.getLines().stream().filter(this::isPdfGeneratedMessage).count();
    }

    private boolean isPdfGeneratedMessage(String message) {
        return message.contains("Generated @");
    }

    private boolean isWatchingForChanges(String message) {
        return message.contains("Watching for changes...");
    }

    public Developer willDeleteAndExitApp(final File fileToDelete) {
        userEditorAction = Optional.of(new UserInteractionThread() {
            @Override
            protected void performEdits() {
                assertTrue("We failed to delete the file", fileToDelete.delete());
                pause();
            }

            @Override
            public void allModificationsCompleted() {
                cmd.simulateControlC();
            }
        });
        return this;
    }

    public long getNumberOfErrorsSeen() {
        return cliLog.getLines().stream().filter((line) -> line.contains("@|red")).count();
    }
}
