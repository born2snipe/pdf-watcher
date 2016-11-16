package com.github.born2snipe.cli;

import java.io.File;

public class InputFile {
    public static File get(File file, File workingDirectory) {
        if (file.getParentFile() == null) {
            return new File(workingDirectory, file.getName());
        }
        return file;
    }
}
