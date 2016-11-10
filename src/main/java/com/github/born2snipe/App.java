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
package com.github.born2snipe;

import com.github.born2snipe.io.DirectoryWatcher;
import com.github.born2snipe.io.DirectoryWatcherFactory;
import com.github.born2snipe.util.Exiter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.List;

public class App {
    public static Exiter exiter = new Exiter();
    public static DirectoryWatcherFactory directoryWatcherFactory = new DirectoryWatcherFactory();

    public static void main(String... args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("app")
                .description("Watch a directory or directories for file changes to regenerate a PDF file");

        parser.addArgument("DIR")
                .type(String.class)
                .nargs("+")
                .dest("directoriesToWatch")
                .help("The directories to watch for changes from");

        try {
            Namespace res = parser.parseArgs(args);

            List<String> directoriesToWatch = res.getList("directoriesToWatch");
            DirectoryWatcher directoryWatcher = directoryWatcherFactory.build(directoriesToWatch);
            directoryWatcher.start(null);

        } catch (ArgumentParserException e) {
            parser.handleError(e);
            exiter.exit(13);
        }
    }
}
