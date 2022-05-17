# Stargate test: comparing vert.x native driver vs stargte gRPC performance

The goal of this repo is to test what is the diffrerence in latency and throughput you will get when using Cassandra via stargate and directly using vertx-cassandra driver.

## Env

```$ java --version
java 18.0.1 2022-04-19
Java(TM) SE Runtime Environment (build 18.0.1+10-24)
Java HotSpot(TM) 64-Bit Server VM (build 18.0.1+10-24, mixed mode, sharing)
```

## Test#1 one request latency test(system.local table)

Query: `select release_version from system.local`

Results with default settings:
```
Grpc Stargate Latency Test x1000, avg=1,455ms, p50=1,712ms, p90=1,712ms p95=1,867ms p99=2,589ms, min=0,999ms, max=21,758ms
Native Latency Test x1000, avg=0,989ms, p50=1,92ms, p90=1,92ms p95=1,145ms p99=1,370ms, min=0,753ms, max=3,15ms
Grpc Stargate Latency Test x1000, avg=1,197ms, p50=1,275ms, p90=1,275ms p95=1,352ms p99=1,840ms, min=0,905ms, max=23,998ms
Native Latency Test x1000, avg=0,971ms, p50=1,70ms, p90=1,70ms p95=1,112ms p99=1,366ms, min=0,768ms, max=4,380ms
```

## Test#2 one request latency test(system.local table)

Schema:
```
CREATE KEYSPACE IF NOT EXISTS ks WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':'1'};
CREATE TABLE IF NOT EXISTS ks.test (k text, v int, PRIMARY KEY(k, v));
```



# Links and references

* [Developers guide](https://stargate.io/docs/stargate/1.0/developers-guide/install/install_cass_40.html)
* [Cassandra 4.0 example](https://github.com/stargate/docker-images/tree/master/examples/cassandra-4.0)
* [gRPC client examples](https://github.com/stargate/stargate/blob/master/grpc-examples/src/main/java/io/stargate/grpcexample/GrpcClientExecuteQuery.java)