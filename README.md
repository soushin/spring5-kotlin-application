# spring5-kotlin-application

## Overview

This repository contains the todo list application that implemented by Spring Boot 2.0.0.M1 (support kotlin).

## Motivation

HTTP routing service and gRPC clinet/server both applications are able to run on Spring Framework 5.0 ? I want to know that.  
So that this repository of applicaiton developed according to following diagram.

![diagram](https://raw.githubusercontent.com/nsoushi/spring5-kotlin-application/master/docs/spring5-kotlin.png)

## Running the applications

Running docker containers.
```
(spring5-kotlin-application) $ docker-compose up -d
```
After running docker containers, you can confirm api response via API of HTTP and gRPC client.

**via API of HTTP**

```
$ curl -XGET http://localhost:8080/api/task/1
{"id":1,"title":"task title"}%
```

**via gRPC client**

use grpc-gateway

```
(spring5-kotlin-application/gateway) $ go get ./...
(spring5-kotlin-application/gateway) $ go run gateway.go
```

then request to the grpc-client on api-server via grpc-gateway.

```
curl -XGET http://localhost:8081/v1/task?task_id=1
{"task_id":1,"title":"task title"}%
```
