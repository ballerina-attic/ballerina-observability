/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.observe.trace.extension.wso2sp;

import io.opentracing.Tracer;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.util.observability.ObservabilityConstants;
import org.ballerinalang.util.tracer.OpenTracer;
import org.ballerinalang.util.tracer.exception.InvalidConfigurationException;
import org.wso2.sp.open.tracer.client.AnalyticsTracerInitializationException;
import org.wso2.sp.open.tracer.client.InvalidTracerConfigurationException;
import org.wso2.sp.open.tracer.client.StreamProcessorTracerClient;

import java.io.PrintStream;
import java.util.Properties;

import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.DEFAULT_WSO2SP_REPORTER_AUTHURL;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.DEFAULT_WSO2SP_REPORTER_PUBLISHER_TYPE_CONFIG;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.DEFAULT_WSO2SP_REPORTER_SERVICE_NAME;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.DEFAULT_WSO2SP_REPORTER_URL;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.DEFAULT_WSO2SP_REPORTER_USERNAME;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_AUTHURL;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_DATABRIDGE_AGENT_CONFIG;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_PASSWORD;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_PUBLISHER_TYPE;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_SERVICE_NAME;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_TRUSTSTORE;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_TRUSTSTORE_PASSWORD;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_URL;
import static org.ballerinalang.observe.trace.extension.wso2sp.Constants.WSO2SP_REPORTER_USERNAME;

/**
 * This is the open tracing extension class for {@link OpenTracer}.
 */
@JavaSPIService("org.ballerinalang.util.tracer.OpenTracer")
public class OpenTracingExtension implements OpenTracer {
    private static final PrintStream console = System.out;
    private static final String TRACER_NAME = "trace.name";
    private static final String TRACER_VALUE = "wso2sp";
    private ConfigRegistry configRegistry;
    private Properties tracerProperties;
    private StreamProcessorTracerClient streamProcessorTracerClient;
    @Override
    public void init() throws InvalidConfigurationException {
        this.streamProcessorTracerClient = new StreamProcessorTracerClient();
        configRegistry = ConfigRegistry.getInstance();
        tracerProperties = new Properties();
        tracerProperties.setProperty(WSO2SP_REPORTER_PUBLISHER_TYPE,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig(
                        WSO2SP_REPORTER_PUBLISHER_TYPE), DEFAULT_WSO2SP_REPORTER_PUBLISHER_TYPE_CONFIG));
        tracerProperties.setProperty(WSO2SP_REPORTER_USERNAME,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig
                        (WSO2SP_REPORTER_USERNAME), DEFAULT_WSO2SP_REPORTER_USERNAME));
        tracerProperties.setProperty(WSO2SP_REPORTER_PASSWORD,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig
                        (WSO2SP_REPORTER_PASSWORD), null));
        tracerProperties.setProperty(WSO2SP_REPORTER_URL,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig
                        (WSO2SP_REPORTER_URL), DEFAULT_WSO2SP_REPORTER_URL));
        tracerProperties.setProperty(WSO2SP_REPORTER_AUTHURL,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig
                        (WSO2SP_REPORTER_AUTHURL), DEFAULT_WSO2SP_REPORTER_AUTHURL));
        tracerProperties.setProperty(WSO2SP_REPORTER_AUTHURL,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig
                        (WSO2SP_REPORTER_AUTHURL), DEFAULT_WSO2SP_REPORTER_AUTHURL));
        tracerProperties.setProperty(WSO2SP_REPORTER_DATABRIDGE_AGENT_CONFIG,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig(WSO2SP_REPORTER_DATABRIDGE_AGENT_CONFIG),
                        null));
        tracerProperties.setProperty(WSO2SP_REPORTER_TRUSTSTORE,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig(WSO2SP_REPORTER_TRUSTSTORE),
                        null));
        tracerProperties.setProperty(WSO2SP_REPORTER_TRUSTSTORE_PASSWORD,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig(WSO2SP_REPORTER_TRUSTSTORE_PASSWORD),
                        null));
        tracerProperties.setProperty(TRACER_NAME, TRACER_VALUE);
        try {
            this.streamProcessorTracerClient.init(tracerProperties);
        } catch (InvalidTracerConfigurationException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }
        console.println("ballerina: started publishing tracers to WSO2 Stream Processor on " +
                tracerProperties.getProperty(WSO2SP_REPORTER_URL) + " via " +
                tracerProperties.getProperty(WSO2SP_REPORTER_PUBLISHER_TYPE));
    }

    @Override
    public Tracer getTracer(String tracerName, String serviceName) {
        tracerProperties.setProperty(WSO2SP_REPORTER_SERVICE_NAME,
                configRegistry.getConfigOrDefault(getFullQualifiedConfig
                        (WSO2SP_REPORTER_SERVICE_NAME), DEFAULT_WSO2SP_REPORTER_SERVICE_NAME));
        try {
            return this.streamProcessorTracerClient.getTracer(serviceName, NoOpScopeManager.INSTANCE);
        } catch (AnalyticsTracerInitializationException e) {
            console.println("Unable to initialize the " + tracerName + " tracer. " + e.getCause());
        }
        return null;
    }

    private String getFullQualifiedConfig(String configName) {
        return ObservabilityConstants.CONFIG_TABLE_TRACING + "." + TRACER_VALUE + "." + configName;
    }

    @Override
    public String getName() {
        return TRACER_VALUE;
    }
}
