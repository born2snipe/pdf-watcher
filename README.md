
 [![Build Status](https://travis-ci.org/born2snipe/pdf-watcher.svg)](https://travis-ci.org/born2snipe/pdf-watcher)

 A simple CLI that will generate a PDF from HTML using the library [Open HTML to PDF](https://github.com/danfickle/openhtmltopdf/).

 The current supported commands:

  - `generate` - a one off command that will do as described, generate a PDF from the provided HTML
  - `watch-and-regenerate` - using the lib [rxjava-file-utils](https://github.com/ReactiveX/RxJavaFileUtils) allows watching a directory for changes
  and will regenerate the PDF as the source file changes.

   