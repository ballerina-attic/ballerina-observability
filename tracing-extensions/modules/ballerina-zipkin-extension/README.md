### Ballerina Zipkin Extension

##### Install Guide

- Start Zipkin. You can use their docker image using following command. `docker run -d -p 9411:9411 openzipkin/zipkin`.
- Build `ballerina-zipkin-extension` and copy the jar files found in `<ballerina-zipkin-extension>/target/distribution/`
 into the `bre/lib/` directory of the ballerina distribution.

- Create a `ballerina.conf` file with following properties.
```toml
[b7a.observability.tracing]
enabled=true
name="zipkin"

[b7a.observability.tracing.zipkin]
reporter.hostname="localhost"
reporter.port=9411
```
- Run your Ballerina service with that `ballerina.conf` file.
  - Either place `ballerina.conf` in your applications directory.
  - Or use `-Bballerina.conf=path/to/ballerina.conf`
- Once everything is up and running, you can use zipkin dashboard to view traces.
