### Ballerina WSO2 Stream Processor Extension

##### Install Guide

- Start WSO2 Stream processor dashboard runtime. And [setup the Distributed Message Tracing](https://docs.wso2.com/display/SP420/Distributed+Message+Tracer).
- Build `ballerina-sp-extension` and copy the jar files found in `<ballerina-sp-extension>/target/distribution/`
 into the `bre/lib/` directory of the ballerina distribution.

- Create a `ballerina.conf` file with following properties.
```toml
[b7a.observability.tracing]
enabled=true
name="wso2sp"

[b7a.observability.tracing.wso2sp]
reporter.wso2sp.publisher.type="thrift"
reporter.wso2sp.publisher.username="admin"
reporter.wso2sp.publisher.password="*****"
reporter.wso2sp.publisher.url="tcp://localhost:7611"
reporter.wso2sp.publisher.authUrl="ssl://localhost:7711"
reporter.wso2sp.publisher.databridge.agent.config="/Users/wso2/resources/data.agent.config.yaml"
javax.net.ssl.trustStore="/Users/wso2/resources/wso2carbon.jks"
javax.net.ssl.trustStorePassword="*****"
reporter.wso2sp.publisher.service.name="ballerina_hello_world"
```
- Run your Ballerina service with that `ballerina.conf` file.
  - Either place `ballerina.conf` in your applications directory.
  - Or use `--config path/to/ballerina.conf`
- Once everything is up and running, you can use wso2 stream processor "Distributed Message Tracer" dashboard to view traces.
