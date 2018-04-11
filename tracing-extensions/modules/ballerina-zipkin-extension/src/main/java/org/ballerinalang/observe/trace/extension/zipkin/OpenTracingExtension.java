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
package org.ballerinalang.observe.trace.extension.zipkin;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.util.tracer.OpenTracer;
import org.ballerinalang.util.tracer.exception.InvalidConfigurationException;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.io.PrintStream;
import java.util.Map;

import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_HOSTNAME;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_PORT;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_HOST_NAME_CONFIG;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_PORT_CONFIG;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.TRACER_NAME;

/**
 * This is the open tracing extension class for {@link OpenTracer}.
 */
@JavaSPIService("org.ballerinalang.util.tracer.OpenTracer")
public class OpenTracingExtension implements OpenTracer {

    private static final PrintStream console = System.out;
    private static final PrintStream consoleError = System.err;
    private static final String NAME = "zipkin";
    private Map<String, String> configProperties;

    @Override
    public void init(Map<String, String> configProperties) {
        console.println("ballerina: started publishing tracers to Jaeger on "
                + configProperties.getOrDefault(REPORTER_HOST_NAME_CONFIG, DEFAULT_REPORTER_HOSTNAME) + ":" +
                getValidIntegerConfig(configProperties.get(REPORTER_PORT_CONFIG),
                        DEFAULT_REPORTER_PORT, REPORTER_PORT_CONFIG));
        this.configProperties = configProperties;
    }

    @Override
    public Tracer getTracer(String tracerName, String serviceName) throws InvalidConfigurationException {
        if (!tracerName.equalsIgnoreCase(TRACER_NAME)) {
            throw new InvalidConfigurationException("Unexpected tracer name! " +
                    "The tracer name supported by this extension is : " + TRACER_NAME + " but found : "
                    + tracerName);
        }
        Sender sender = OkHttpSender.create(
                "http://" +
                        configProperties.getOrDefault(REPORTER_HOST_NAME_CONFIG, DEFAULT_REPORTER_HOSTNAME) + ":" +
                        getValidIntegerConfig(configProperties
                                .get(REPORTER_PORT_CONFIG), DEFAULT_REPORTER_PORT, REPORTER_PORT_CONFIG) +
                        Constants.REPORTING_API_CONTEXT);
        return BraveTracer.newBuilder(Tracing.newBuilder()
                .localServiceName(serviceName)
                .spanReporter(AsyncReporter.create(sender))
                .build()).activeScopeManager(NoOpScopeManager.INSTANCE).build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private int getValidIntegerConfig(String config, int defaultValue, String configName) {
        if (config == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(config);
            } catch (NumberFormatException ex) {
                consoleError.println("ballerina: observability tracing configuration " + configName
                        + " is invalid. Default value of " + defaultValue + " will be used.");
                return defaultValue;
            }
        }
    }
}
