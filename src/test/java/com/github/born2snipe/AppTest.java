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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppTest {
    @Mock
    private Exiter exiter;
    @Mock
    private DirectoryWatcherFactory directoryWatcherFactory;
    @Mock
    private DirectoryWatcher directoryWatcher;
    private PrintStream originalErr;
    private ByteArrayOutputStream capturedErrOutput;

    @Before
    public void setUp() throws Exception {
        App.exiter = exiter;
        App.directoryWatcherFactory = directoryWatcherFactory;

        capturedErrOutput = new ByteArrayOutputStream();
        originalErr = System.err;
        System.setErr(new PrintStream(capturedErrOutput));
    }

    @After
    public void tearDown() throws Exception {
        System.setErr(originalErr);
        App.exiter = new Exiter();
        App.directoryWatcherFactory = new DirectoryWatcherFactory();
    }

    @Test
    public void shouldAllowASingleDirectoryToBeWatched() {
        when(directoryWatcherFactory.build(Arrays.asList("dir-1"))).thenReturn(directoryWatcher);
        App.main("dir-1");

        verify(directoryWatcher).start(null);
        assertSuccessfulExecution();
    }

    @Test
    public void shouldAllowMultipleDirectoriesToBeWatched() {
        when(directoryWatcherFactory.build(Arrays.asList("dir-1", "dir-2"))).thenReturn(directoryWatcher);

        App.main("dir-1", "dir-2");

        verify(directoryWatcher).start(null);
        assertSuccessfulExecution();
    }

    @Test
    public void shouldRequireProvidingADirectoryToWatch() {
        App.main();

        assertFailedParsingArgs();
    }

    private void assertFailedParsingArgs() {
        verify(exiter).exit(13);
        assertTrue(capturedErrOutput.toByteArray().length > 0);
    }

    private void assertSuccessfulExecution() {
        byte[] capturedOutput = capturedErrOutput.toByteArray();
        assertTrue("Captured ERR output:\n" + new String(capturedOutput), capturedOutput.length == 0);
        verifyZeroInteractions(exiter);
    }
}
