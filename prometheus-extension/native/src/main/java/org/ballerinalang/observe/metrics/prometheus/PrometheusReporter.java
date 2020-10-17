/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.ballerinalang.observe.metrics.prometheus;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.jvm.observability.ObservabilityConstants;
import org.ballerinalang.jvm.observability.metrics.spi.MetricReporter;
import org.ballerinalang.jvm.scheduling.StrandMetadata;
import org.ballerinalang.jvm.services.EmbeddedExecutorProvider;
import org.ballerinalang.jvm.services.spi.EmbeddedExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Properties;

import static org.ballerinalang.jvm.util.BLangConstants.BALLERINA_BUILTIN_PKG;

/**
 * This is the reporter extension for the Prometheus.
 *
 * @since 0.980.0
 */
public class PrometheusReporter implements MetricReporter {

    private static final PrintStream console = System.out;
    private static final String PROMETHEUS_PACKAGE = "prometheus";
    private static final String PROMETHEUS_HOST_CONFIG = ObservabilityConstants.CONFIG_TABLE_METRICS
            + "." + PROMETHEUS_PACKAGE + ".host";
    private static final String PROMETHEUS_PORT_CONFIG = ObservabilityConstants.CONFIG_TABLE_METRICS + "."
            + PROMETHEUS_PACKAGE + ".port";
    private static final String DEFAULT_PROMETHEUS_HOST = "0.0.0.0";
    private static final String DEFAULT_PROMETHEUS_PORT = "9797";

    @Override
    public void init() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("prometheus-reporter.properties");
        String prometheusModuleVersion;
        try {
            Properties reporterProperties = new Properties();
            reporterProperties.load(stream);
            prometheusModuleVersion = (String) reporterProperties.get("moduleVersion");
        } catch (IOException | ClassCastException e) {
            console.println("ballerina: unexpected failure in detecting prometheus extension version");
            return;
        }

        String hostname = ConfigRegistry.getInstance().
                getConfigOrDefault(PROMETHEUS_HOST_CONFIG, DEFAULT_PROMETHEUS_HOST);
        String port = ConfigRegistry.getInstance().getConfigOrDefault(PROMETHEUS_PORT_CONFIG,
                DEFAULT_PROMETHEUS_PORT);

        StrandMetadata metaData = new StrandMetadata(BALLERINA_BUILTIN_PKG, PROMETHEUS_PACKAGE,
                prometheusModuleVersion, "startReporter");
        EmbeddedExecutor executor = EmbeddedExecutorProvider.getInstance().getExecutor();
        Optional<RuntimeException> prometheus = executor.executeService(PROMETHEUS_PACKAGE, prometheusModuleVersion,
                null, metaData);
        if (prometheus.isPresent()) {
            console.println("ballerina: failed to start Prometheus HTTP listener " + hostname + ":" + port + " "
                    + prometheus.get().getMessage());
            return;
        }
        console.println("ballerina: started Prometheus HTTP listener " + hostname + ":" + port);
    }

    @Override
    public String getName() {
        return PROMETHEUS_PACKAGE;
    }
}
