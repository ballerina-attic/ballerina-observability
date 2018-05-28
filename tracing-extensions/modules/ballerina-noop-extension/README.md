### Ballerina Jaeger Extension

##### Install Guide

- Build `ballerina-noop-extension` and put it in `bre/lib/` directory.
- Create a `ballerina.conf` file with following properties.
```toml
[b7a.observability.tracing]
enabled=true
name="noop"
```
- Run your Ballerina service with that `ballerina.conf` file.
  - Either place `ballerina.conf` in your applications directory.
  - Or use `-Bballerina.conf=path/to/ballerina.conf`