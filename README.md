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

## Test#2 single insert latency test

Schema:
```
CREATE KEYSPACE IF NOT EXISTS ks WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':'1'};
CREATE TABLE IF NOT EXISTS ks.test (k text, v int, PRIMARY KEY(k, v));
```

Query: `INSERT INTO ks.test (k, v) VALUES (?, ?)`

### gRPC faliure

Stargate via gRPC fails when trying to perform an insert with a following error:
```
Exception in thread "main" io.grpc.StatusRuntimeException: INTERNAL: Prepared query with ID 1f31fb40b0d8aa8289e9a099271d8816 not found (either the query was not prepared on this host (maybe the host has been restarted?) or you have prepared too many queries and it has been evicted from the internal cache)
	at io.grpc.stub.ClientCalls.toStatusRuntimeException(ClientCalls.java:262)
	at io.grpc.stub.ClientCalls.getUnchecked(ClientCalls.java:243)
	at io.grpc.stub.ClientCalls.blockingUnaryCall(ClientCalls.java:156)
	at io.stargate.proto.StargateGrpc$StargateBlockingStub.executeQuery(StargateGrpc.java:236)
	...
```

Stargate logs: `QueryProcessor.java:110 - 3 prepared statements discarded in the last minute because cache limit reached (10 MB)`.
It seems like Stargate is creating a prepared statement for every insert, despite the fact that all the queries are the same except the values being inserted.
While with native driver you just prepare once and use it all the time. Here is an issue on that topic: https://github.com/stargate/stargate/issues/931.
`PreparedStatement`s in stargate are not being cached and reused in Stargate.

### Test results

After decreasing the amount of inserts we are getting the results:
```
Native insert x1000, avg=0,928ms, p50=1,45ms, p90=1,45ms p95=1,98ms p99=1,353ms, min=0,675ms, max=3,247ms
Grpc Stargate insert x100, avg=2,578ms, p50=3,82ms, p90=3,82ms p95=3,821ms p99=6,986ms, min=1,673ms, max=6,986ms
```
Inserts with native driver are around 2.5x times faster than when using Stargate gRPC.

## Test#3 select 4k rows

Query: `SELECT v FROM ks.test WHERE k = 'x'`

Results with default settings:
```
Native select 4k x10, avg=7,385ms, p50=11,810ms, p90=11,810ms p95=11,810ms p99=11,810ms, min=6,57ms, max=11,810ms
```
There is no results for the gRPC Stargate because it cant select more than 100 rows.


## Test#4 select 100 rows

Query: `SELECT v FROM ks.test WHERE k = 'y'`

Results with default settings:
```
Native select 100rows x10, avg=1,860ms, p50=2,851ms, p90=2,851ms p95=2,851ms p99=2,851ms, min=1,196ms, max=2,851ms
Grpc Stargate select 100rows x10, avg=3,351ms, p50=5,26ms, p90=5,26ms p95=5,26ms p99=5,26ms, min=2,667ms, max=5,26m
```

# Links and references

* [Developers guide](https://stargate.io/docs/stargate/1.0/developers-guide/install/install_cass_40.html)
* [Cassandra 4.0 example](https://github.com/stargate/docker-images/tree/master/examples/cassandra-4.0)
* [gRPC client examples](https://github.com/stargate/stargate/blob/master/grpc-examples/src/main/java/io/stargate/grpcexample/GrpcClientExecuteQuery.java)