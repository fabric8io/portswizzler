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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.utils.Systems;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 */
public class PortMapper {
    public static final String PORTSWIZZLER_MAPPINGS = "PORTSWIZZLER_MAPPINGS";

    private String mappingFileName;
    private AllPortMappings portMappings;
    private ObjectMapper json = new ObjectMapper();

    public String getReplacementPort(String appName, String currentPort) throws IOException {
        Integer port;
        try {
            port = Integer.parseInt(currentPort);
        } catch (NumberFormatException e) {
            // ignore bad numbers
            return currentPort;
        }
        AllPortMappings mappings = getPortMappings();
        PortMappings portMappings = mappings.mappingsForApp(appName);

        Map<Integer, Integer> ports = portMappings.getPorts();
        Integer mappedPort = ports.get(port);
        if (mappedPort == null) {
            // we previously mapped this port already!
            if (ports.values().contains(port)) {
                mappedPort = port;
            }
        }
        if (mappedPort == null) {
            mappedPort = mappings.allocateNewPort();
            ports.put(port, mappedPort);
            savePortMappings();
        }
        return mappedPort.toString();
    }

    public String getMappingFileName() {
        if (mappingFileName == null) {
            mappingFileName = Systems.getEnvVarOrSystemProperty(PORTSWIZZLER_MAPPINGS, "portswizzler.json");
        }
        return mappingFileName;
    }

    public void setMappingFileName(String mappingFileName) {
        this.mappingFileName = mappingFileName;
    }

    public AllPortMappings getPortMappings() throws IOException {
        if (portMappings == null) {
            File file = getMappingFile();
            if (file.exists() && file.isFile()) {
                portMappings = json.readerFor(AllPortMappings.class).readValue(file);
                portMappings.updateAllocatedPorts();
            }
        }
        if (portMappings == null) {
            portMappings = new AllPortMappings();
        }
        return portMappings;
    }

    protected File getMappingFile() {
        File file = new File(getMappingFileName());
        File dir = file.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        return file;
    }

    protected void savePortMappings() throws IOException {
        json.writer().withDefaultPrettyPrinter().writeValue(getMappingFile(), getPortMappings());
    }

}
