### Ballerina Zipkin Extension

##### Install Guide

- Start Zipkin. You can use their docker image using following command. `docker run -d -p 9411:9411 openzipkin/zipkin`.
- Build `ballerina-zipkin-extension` and copy the jar files found in `<ballerina-zipkin-extension>/target/distribution/`
 into the `bre/lib/` directory of the ballerina distribution.
- This extension can be used to connect to Zipkin, and other APM vendors such as Honeycomb, who has open tracing 
proxy that can be used to convert the Zipkin traces to their own format. 

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
