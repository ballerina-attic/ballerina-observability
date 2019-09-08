
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

/**
 * This is the constants class that defines all the constants
 * that are used by the {@link OpenTracingExtension}.
 */
public class Constants {

    private Constants() {

    }

    static final String REPORTER_HOST_NAME_CONFIG = "reporter.hostname";
    static final String REPORTER_PORT_CONFIG = "reporter.port";
    static final String REPORTER_API_CONTEXT_CONFIG = "reporter.api.context";
    static final String REPORTER_COMPRESSION_ENABLED_CONFIG = "reporter.compression.enabled";
    static final String REPORTER_API_VERSION = "reporter.api.version";

    static final String DEFAULT_REPORTER_API_CONTEXT = "/api/v2/spans";
    static final String DEFAULT_REPORTER_HOSTNAME = "localhost";
    static final int DEFAULT_REPORTER_PORT = 9411;
    static final boolean DEFAULT_REPORTER_COMPRESSION_ENABLED = true;
    static final String DEFAULT_REPORTER_API_VERSION = "v2";


}
