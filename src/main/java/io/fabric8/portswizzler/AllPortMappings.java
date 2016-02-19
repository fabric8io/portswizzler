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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.fabric8.utils.Ports;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents the ports owned by apps
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AllPortMappings {
    public static final int DEFAULT_FROM_PORT = 10000;
    public static final int DEFAULT_TO_PORT = 50000;

    private Map<String, PortMappings> apps = new TreeMap<>();;
    private Integer fromPort = DEFAULT_FROM_PORT;
    private Integer endPort = DEFAULT_TO_PORT;
    private boolean checkPortAvailble = true;

    @JsonIgnore
    private Set<Integer> allocatedPorts = new HashSet<>();

    /**
     * Returns the port mappings for the given app
     */
    public PortMappings mappingsForApp(String appName) {
        PortMappings answer = apps.get(appName);
        if (answer == null) {
            answer = new PortMappings();
            apps.put(appName, answer);
        }
        return answer;
    }

    public Map<String, PortMappings> getApps() {
        return apps;
    }

    public void setApps(Map<String, PortMappings> apps) {
        this.apps = apps;
    }

    public Integer getEndPort() {
        if (endPort == null) {
            endPort = DEFAULT_TO_PORT;
        }
        return endPort;
    }

    public void setEndPort(Integer endPort) {
        this.endPort = endPort;
    }

    public Integer getFromPort() {
        if (fromPort == null) {
            fromPort = DEFAULT_FROM_PORT;
        }
        return fromPort;
    }

    public void setFromPort(Integer fromPort) {
        this.fromPort = fromPort;
    }

    public boolean isCheckPortAvailble() {
        return checkPortAvailble;
    }

    public void setCheckPortAvailble(boolean checkPortAvailble) {
        this.checkPortAvailble = checkPortAvailble;
    }

    public void updateAllocatedPorts() {
        for (PortMappings portMappings : apps.values()) {
            allocatedPorts.addAll(portMappings.getPorts().values());
        }
    }

    public Integer allocateNewPort() {
        int answer = Ports.findFreeLocalPort(allocatedPorts, getFromPort(), getEndPort(), true);
        allocatedPorts.add(answer);
        return answer;
    }
}
