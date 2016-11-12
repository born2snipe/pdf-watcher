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
package com.github.born2snipe.cli.openhtmltopdf;

import com.github.born2snipe.cli.PdfGenerator;
import com.openhtmltopdf.DOMBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRLog;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.File;
import java.io.OutputStream;

public class OpenHtmlToPdfGenerator implements PdfGenerator {
    @Override
    public void generateFrom(File input, OutputStream pdfOutput) throws Exception {
        // todo - can we turn off all the logging?
        XRLog.setLoggingEnabled(false);

        // make it possible to process a HTML5 doc
        Document doc = DOMBuilder.jsoup2DOM(Jsoup.parse(input, "UTF-8"));

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withW3cDocument(doc, input.toURI().toURL().toString());
        builder.toStream(pdfOutput);
        builder.run();
    }
}
