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
package com.github.born2snipe.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class DirectoryWatcherFactoryTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private DirectoryWatcherFactory factory;
    private File existingDir;

    @Before
    public void setUp() throws Exception {
        factory = new DirectoryWatcherFactory();
        existingDir = tmpFolder.newFolder("exists");
    }

    @Test(expected = NotADirectoryException.class)
    public void shouldBlowUpIfTheProvidedPathIsNotADirectory() throws IOException {
        String filePath = tmpFolder.newFile("test.txt").getAbsolutePath();

        factory.build(Arrays.asList(filePath));
    }

    @Test
    public void shouldCreateAWatcherIfTheFolderDoesExist() throws IOException {
        String existingDirPath = existingDir.getAbsolutePath();

        assertNotNull(factory.build(Arrays.asList(existingDirPath)));
    }

    @Test(expected = DirectoryDoesNotExistException.class)
    public void shouldBlowUpIfOneOfTheFoldersProvidedDoNotExist() {
        String existingDirPath = existingDir.getAbsolutePath();

        factory.build(Arrays.asList(existingDirPath, "doesNotExist"));
    }

    @Test(expected = DirectoryDoesNotExistException.class)
    public void shouldBlowUpIfTheDirectoryDoesNotExist() {
        factory.build(Arrays.asList("doesNotExist"));
    }
}