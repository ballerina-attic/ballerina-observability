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
import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.jvm.observability.ObservabilityConstants;
import org.ballerinalang.jvm.observability.tracer.InvalidConfigurationException;
import org.ballerinalang.jvm.observability.tracer.OpenTracer;

import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.PrintStream;
import java.util.Objects;

import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_API_CONTEXT;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_API_VERSION;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_COMPRESSION_ENABLED;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_HOSTNAME;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.DEFAULT_REPORTER_PORT;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_API_CONTEXT_CONFIG;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_API_VERSION;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_COMPRESSION_ENABLED_CONFIG;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_HOST_NAME_CONFIG;
import static org.ballerinalang.observe.trace.extension.zipkin.Constants.REPORTER_PORT_CONFIG;

/**
 * This is the open tracing extension class for {@link OpenTracer}.
 */
public class OpenTracingExtension implements OpenTracer {

    private static final PrintStream console = System.out;
    private static final String NAME = "zipkin";

    private ConfigRegistry configRegistry;
    private String hostname;
    private int port;
    private String apiContext;
    private boolean compressionEnabled;
    private String apiVersion;

    @Override
    public void init() throws InvalidConfigurationException {
        configRegistry = ConfigRegistry.getInstance();
        hostname = configRegistry.getConfigOrDefault(getFullQualifiedConfig(REPORTER_HOST_NAME_CONFIG),
                DEFAULT_REPORTER_HOSTNAME);
        port = Integer.parseInt(configRegistry.getConfigOrDefault(getFullQualifiedConfig(REPORTER_PORT_CONFIG),
                String.valueOf(DEFAULT_REPORTER_PORT)));
        apiContext = configRegistry.getConfigOrDefault(getFullQualifiedConfig(REPORTER_API_CONTEXT_CONFIG),
                DEFAULT_REPORTER_API_CONTEXT);
        compressionEnabled = Boolean.parseBoolean(configRegistry.getConfigOrDefault(
                getFullQualifiedConfig(REPORTER_COMPRESSION_ENABLED_CONFIG),
                String.valueOf(DEFAULT_REPORTER_COMPRESSION_ENABLED)));
        apiVersion = configRegistry.getConfigOrDefault(getFullQualifiedConfig(REPORTER_API_VERSION),
                DEFAULT_REPORTER_API_VERSION);
        console.println("ballerina: started publishing tracers to Zipkin on " + hostname + ":" + port);
    }

    @Override
    public Tracer getTracer(String tracerName, String serviceName) {

        if (Objects.isNull(configRegistry)) {
            throw new IllegalStateException("Tracer not initialized with configurations");
        }
        if (apiVersion.equalsIgnoreCase(DEFAULT_REPORTER_API_VERSION)) {
            Sender sender = OkHttpSender.create("http://" + hostname + ":" + port + apiContext);
            return BraveTracer.newBuilder(Tracing.newBuilder()
                    .localServiceName(serviceName)
                    .spanReporter(AsyncReporter.create(sender))
                    .build()).activeScopeManager(NoOpScopeManager.INSTANCE).build();
        } else {
            return BraveTracer.newBuilder(Tracing.newBuilder()
                    .localServiceName(serviceName)
                    .spanReporter(AsyncReporter.builder(URLConnectionSender.create("http://" + hostname + ":" + port
                            + apiContext).toBuilder().compressionEnabled(compressionEnabled).build())
                            .build(SpanBytesEncoder.JSON_V1))
                    .build()).activeScopeManager(NoOpScopeManager.INSTANCE).build();
        }
    }

    private String getFullQualifiedConfig(String configName) {
        return ObservabilityConstants.CONFIG_TABLE_TRACING + "." + NAME + "." + configName;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
