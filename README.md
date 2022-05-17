# Stargate test

The goal of this repo is to test what is the diffrerence in latency and throughput you will get when using Cassandra via stargate and directly using vertx-cassandra driver.

## Env

```$ java --version
java 18.0.1 2022-04-19
Java(TM) SE Runtime Environment (build 18.0.1+10-24)
Java HotSpot(TM) 64-Bit Server VM (build 18.0.1+10-24, mixed mode, sharing)
```

## Test#1 one request latency testing: vert.x native vs stargte gRPC

Query: `select release_version from system.local`

Results with default settings:
```
GRPC Stargate latency test x10000 average=1,372ms, min=0,809ms, max=185,635ms
Native latency test x10000 average=1,385ms, min=0,722ms, max=721,549ms
GRPC Stargate latency test x10000 average=1,404ms, min=0,836ms, max=1005,304ms
Native latency test x10000 average=1,410ms, min=0,751ms, max=1005,97ms
```




# Links and references

* https://stargate.io/docs/stargate/1.0/developers-guide/install/install_cass_40.html
* https://github.com/stargate/docker-images/tree/master/examples/cassandra-4.0
* https://github.com/stargate/stargate/blob/master/grpc-examples/src/main/java/io/stargate/grpcexample/GrpcClientExecuteQuery.java