### Ballerina Jaeger Extension

##### Install Guide

- Start Jaeger. You can use their docker image using following command. `docker run -d -p5775:5775/udp 
-p6831:6831/udp -p6832:6832/udp -p5778:5778 -p16686:16686 -p14268:14268 
jaegertracing/all-in-one:latest`.
- Build `ballerina-jaeger-extension` and copy the jar files found in `<ballerina-jaeger-extension>/target/distribution/`
 into the `bre/lib/` directory of the ballerina distribution.
- Create a `ballerina.conf` file with following properties.
```toml
[b7a.observability.tracing]
enabled=true
name="jaeger"

[b7a.observability.tracing.jaeger]
reporter.hostname="localhost"
reporter.port=5775
sampler.param=1.0
sampler.type="const"
reporter.flush.interval.ms=2000
reporter.log.spans=true
reporter.max.buffer.spans=1000
```

- Run your Ballerina service with that `ballerina.conf` file.
  - Either place `ballerina.conf` in your applications directory.
  - Or use `--config path/to/ballerina.conf`
- Once everything is up and running, you can use jaeger dashboard to view traces.
