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
import com.github.born2snipe.cli.openhtmltopdf.OpenHtmlToPdfGenerator;
import net.sourceforge.argparse4j.impl.Arguments;
import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@ServiceProvider(service = CliCommand.class)
public class GeneratePdfCommand extends CliCommand {
    private PdfGenerator pdfGenerator = new OpenHtmlToPdfGenerator();

    public GeneratePdfCommand() {
        argsParser.addArgument("-i", "--input")
                .required(true)
                .type(Arguments.fileType())
                .dest("input")
                .help("The input file used to generate the PDF");

        argsParser.addArgument("-o", "--output")
                .required(true)
                .type(Arguments.fileType())
                .dest("output")
                .help("The output file for the PDF");
    }

    @Override
    public String getName() {
        return "generate";
    }

    @Override
    public String getDescription() {
        return "One off command to generate a pdf from a provided file and a provided output directory";
    }

    @Override
    protected void executeParsedArgs(CommandContext context) {
        File input = context.getNamespace().get("input");
        File output = context.getNamespace().get("output");

        if (input.getParentFile() == null) {
            input = new File(context.getWorkingDirectory(), input.getName());
        }

        if (!input.exists()) {
            throw new IllegalArgumentException("File not found: '" + input.getName() + "'");
        }

        if (output.getParentFile() == null) {
            output = new File(context.getWorkingDirectory(), output.getName());
        } else {
            output.getParentFile().mkdirs();
        }


        generatePdf(input, output, context.getLog());
    }

    private void generatePdf(File input, File output, CliLog log) {
        try (OutputStream o = new BufferedOutputStream(new FileOutputStream(output))) {
            long start = System.currentTimeMillis();
            pdfGenerator.generateFrom(input, o);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            log.info("@|green Generated @ {2} in {1} millis:|@ {0}", output, System.currentTimeMillis() - start, dateFormat.format(new Date()));
        } catch (Exception e) {
            throw new PdfGenerationFailedException(output, e);
        }
    }
}
