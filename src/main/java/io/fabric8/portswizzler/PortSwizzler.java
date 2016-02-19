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
import io.fabric8.utils.Strings;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class PortSwizzler {
    private String appName = "no_app_configured_yet";
    private String basedir = ".";
    private String[] fileIncludes = {};
    private String[] fileExcludes = {};
    private String replaceRegex;
    private PortMapper portMapper = new PortMapper();
    private String portYmlFile;

    public static void main(String[] args) {
        PortSwizzler swizzler = new PortSwizzler();

        if (args.length < 4) {
            System.out.println("Usage: appName baseDir filePattern replaceRegex portYmlFile");
            System.out.println();
            System.out.println("e.g.: myapp /opt/foo 'etc/*.cfg' '\\.port\\s*=\\s*(\\d+)'");
            System.out.println();
            System.out.println("Port Swizzler uses the first regex group as being the port number to be replaced.");
            System.exit(1);
        }

        swizzler.setAppName(args[0]);
        swizzler.setBasedir(args[1]);
        swizzler.setFileIncludes(args[2]);
        swizzler.setReplaceRegex(args[3]);
        if (args.length > 3) {
            swizzler.setPortYmlFile(args[4]);
        }

        try {
            swizzler.swizzle();
            System.out.println("Port Swizzler has swizzled!");
        } catch (IOException e) {
            System.err.println("Failed to port swizzle: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Recursively processes all files in the classpath
     */
    public void swizzle() throws IOException {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(fileIncludes);
        ds.setExcludes(fileExcludes);
        ds.setBasedir(basedir);
        ds.scan();


        File dir = new File(basedir);
        String[] matches = ds.getIncludedFiles();
        System.out.println("Port Swizzler is swizzling!");
        for (String match : matches) {
            File file = new File(dir, match);
            swizzleFile(file);
        }
        if (Strings.isNotBlank(portYmlFile)) {
            File file = new File(portYmlFile);
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            portMapper.writePortYml(appName, file);
        }
    }

    public void swizzleFile(File file) throws IOException {
        System.out.println("  swizzling: " + file);
        List<String> lines = Files.readLines(file);
        Pattern pattern = Pattern.compile(replaceRegex);

        try (FileWriter writer = new FileWriter(file)) {
            for (String line : lines) {
                String text = line;
                String answer = "";
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    int start = matcher.start(1);
                    int end = matcher.end(1);
                    String currentPort = text.substring(start, end);
                    String replacePort = portMapper.getReplacementPort(appName, currentPort);
                    answer += text.substring(0, start) + replacePort;
                    text = text.substring(end);
                    matcher = pattern.matcher(text);
                }
                answer += text;
                writer.append(answer);
                writer.append("\n");
            }
        }
    }

    public PortMapper getPortMapper() {
        return portMapper;
    }

    public void setPortMapper(PortMapper portMapper) {
        this.portMapper = portMapper;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public String[] getFileExcludes() {
        return fileExcludes;
    }

    public void setFileExcludes(String... fileExcludes) {
        this.fileExcludes = fileExcludes;
    }

    public String[] getFileIncludes() {
        return fileIncludes;
    }

    public void setFileIncludes(String... fileIncludes) {
        this.fileIncludes = fileIncludes;
    }

    public String getReplaceRegex() {
        return replaceRegex;
    }

    public void setReplaceRegex(String replaceRegex) {
        this.replaceRegex = replaceRegex;
    }

    public String getPortYmlFile() {
        return portYmlFile;
    }

    public void setPortYmlFile(String portYmlFile) {
        this.portYmlFile = portYmlFile;
    }
}
