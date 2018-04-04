### Ballerina Jaeger Extension

##### Install Guide

- Build `ballerina-noop-extension` and put it in `bre/lib/` directory.
- Create a `trace-config.yaml` with following properties.
```yaml
tracers:
 - name: noop
   enabled: true
   className: org.ballerinalang.observe.trace.extension.noop.OpenTracingExtension
   configuration:
     reporter.hostname: localhost
```
- Create a `ballerina.conf` file with `trace.config` property, which points to the above `trace-config.yaml`.
- Run your Ballerina service with that `ballerina.conf` file.
  - Either place `ballerina.conf` in your applications directory.
  - Or use `-Bballerina.conf=path/to/ballerina.conf`