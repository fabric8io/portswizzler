/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.portswizzler;

import io.fabric8.utils.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 */
public class PortSwizzleTest {
    PortSwizzler swizzler = new PortSwizzler();
    @Test
    public void testSwizzle() throws Exception {
        File testDir = new File(getBasedir(), "target/test-data");
        String portSwizzlerFile = new File(getBasedir(), "target/test-portswizzler/portmappings.json").getAbsolutePath();
        swizzler.getPortMapper().setMappingFileName(portSwizzlerFile);

        File testDataDir = new File(getBasedir(), "src/test/resources");
        Files.recursiveDelete(testDir);
        Files.copy(testDataDir, testDir);

        // lets swizzle multiple times
        for (int i = 1; i <= 3; i++) {
            System.out.println("Lets swizzle run: " + i);

            if (i == 3) {
                // lets force swizzler to recreate the port mapper!
                swizzler.setPortMapper(new PortMapper());
                swizzler.getPortMapper().setMappingFileName(portSwizzlerFile);
            }
            assertSwizzle(testDir, "**/*.cfg", "\\.port\\s*=\\s*(\\d+)");

            assertFileContains(testDir, "etc/foo.cfg", "foo.port = 10000 bar.port = 10001");
            assertFileContains(testDir, "etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port=10002");
        }


    }

    protected void assertFileContains(File dir, String path, String text) throws IOException {
        File file = new File(dir, path);
        assertThat(file).exists().isFile();
        List<String> lines = Files.readLines(file);
        for (String line : lines) {
            if (line.contains(text)) {
                return;
            }
        }
        fail("File " + file + " does not contain the text `" + text + "`");
    }

    protected void assertSwizzle(File testDir, String fileIncludes, String replaceRegex) throws IOException {
        System.out.println("Replacing files in " + testDir + " matching " + fileIncludes + " with regex: " + replaceRegex);
        swizzler.setAppName("cheese");
        swizzler.setBasedir(testDir.getAbsolutePath());
        swizzler.setFileIncludes(fileIncludes);
        swizzler.setReplaceRegex(replaceRegex);
        swizzler.swizzle();
    }

    public static File getBasedir() {
        String dir = System.getProperty("basedir", ".");
        return new File(dir);
    }

}
