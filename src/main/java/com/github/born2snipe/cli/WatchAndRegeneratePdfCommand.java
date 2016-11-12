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
package com.github.born2snipe.cli;

import cli.pi.command.CliCommand;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = CliCommand.class)
public class WatchAndRegeneratePdfCommand extends CliCommand {
    @Override
    public String getName() {
        return "watch-and-regenerate";
    }

    @Override
    public String getDescription() {
        return "Watch a directory for changes and regenerate the PDF to a provided output directory";
    }
}
