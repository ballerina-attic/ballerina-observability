### Ballerina Zipkin Extension

##### Install Guide

- Start Zipkin. You can use their docker image using following command. `docker run -d -p 9411:9411 openzipkin/zipkin`.
- Build `ballerina-zipkin-extension` and copy the jar files found in `<ballerina-zipkin-extension>/target/distribution/`
 into the `bre/lib/` directory of the ballerina distribution.
- This extension can be used to connect to Zipkin, and other APM vendors such as Honeycomb, who has open tracing 
proxy that can be used to convert the Zipkin traces to their own format. 

- Create a `Ballerina.toml` file with following properties.
```toml
...

[platform]
target = "java8"

    [[platform.libraries]]
    artifactId = "ballerina-zipkin-extension"
    version = "1.0.0-rc1-SNAPSHOT"
    path = "/Users/grainier/ballerina/bre/lib/ballerina-zipkin-extension-1.0.0-rc1-SNAPSHOT.jar"
    groupId = "org.ballerinalang"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "brave-opentracing"
    version = "4.17.1"
    path = "/Users/grainier/ballerina/bre/lib/brave-4.17.1.jar"
    groupId = "io.opentracing.brave"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "brave"
    version = "0.29.0"
    path = "/Users/grainier/ballerina/bre/lib/brave-opentracing-0.29.0.jar"
    groupId = "io.zipkin.brave"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "zipkin-reporter"
    version = "2.6.1"
    path = "/Users/grainier/ballerina/bre/lib/zipkin-2.6.1.jar"
    groupId = "io.zipkin.reporter2"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "zipkin"
    version = "2.5.0"
    path = "/Users/grainier/ballerina/bre/lib/zipkin-reporter-2.5.0.jar"
    groupId = "io.zipkin.zipkin2"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "zipkin-sender-okhttp3"
    version = "2.5.0"
    path = "/Users/grainier/ballerina/bre/lib/zipkin-sender-okhttp3-2.5.0.jar"
    groupId = "io.zipkin.reporter2"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "zipkin-sender-urlconnection"
    version = "2.5.0"
    path = "/Users/grainier/ballerina/bre/lib/zipkin-sender-urlconnection-2.5.0.jar"
    groupId = "io.zipkin.reporter2"
    modules = ["yourModuleName"]

    [[platform.libraries]]
    artifactId = "kotlin-stdlib"
    version = "1.3.31"
    path = "/Users/grainier/ballerina/bre/lib/kotlin-stdlib-1.3.31.jar"
    groupId = "org.jetbrains.kotlin"
    modules = ["yourModuleName"]

```

- Create a `ballerina.conf` file with following properties.
```toml
[b7a.observability.tracing]
enabled=true
name="zipkin"

[b7a.observability.tracing.zipkin]
reporter.hostname="localhost"
reporter.port=9411

# The below properties may needed if you are need to use Zipkin V1 to send the APIs.
#reporter.api.context="/api/v1/spans" 
#reporter.api.version="v1"

# The below properties can allow to disable the compression if needed. By default it's enabled. 
#reporter.compression.enabled=false

```
- Run your Ballerina service with that `ballerina.conf` file.
  - Either place `ballerina.conf` in your applications directory.
  - Or use `--config path/to/ballerina.conf`
- Once everything is up and running, you can use zipkin dashboard to view traces.
