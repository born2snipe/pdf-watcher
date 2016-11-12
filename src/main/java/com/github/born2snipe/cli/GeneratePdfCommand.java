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

import cli.pi.CliLog;
import cli.pi.command.CliCommand;
import cli.pi.command.CommandContext;
import com.github.born2snipe.cli.openhtmltopdf.OpenHtmlToPdfGenerator;
import net.sourceforge.argparse4j.impl.Arguments;
import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@ServiceProvider(service = CliCommand.class)
public class GeneratePdfCommand extends CliCommand {
    private PdfGenerator pdfGenerator = new OpenHtmlToPdfGenerator();

    public GeneratePdfCommand() {
        argsParser.addArgument("-i", "--input")
                .required(true)
                .type(Arguments.fileType().verifyCanRead().verifyExists())
                .dest("input")
                .help("The input file used to generate the PDF");

        argsParser.addArgument("-o", "--output")
                .required(true)
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

        try {
            File output = new File((String) context.getNamespace().get("output")).getCanonicalFile();
            output.getParentFile().mkdirs();

            generatePdf(input, output, context.getLog());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generatePdf(File input, File output, CliLog log) {
        try (OutputStream o = new BufferedOutputStream(new FileOutputStream(output))) {
            long start = System.currentTimeMillis();
            pdfGenerator.generateFrom(input, o);
            log.info("@|green Generated in {1} millis:|@ {0}", output, System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new PdfGenerationFailedException(output, e);
        }
    }
}
