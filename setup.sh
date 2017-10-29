#! /bin/bash

DOCKER_COMPOSE_COMMAND=`which docker-compose`

if [ ! -x $DOCKER_COMPOSE_COMMAND ]; then
  echo "error, should install docker-compose" >&2
  exit -1
fi

REPOSITORY_DIR=$(pwd)

cd $REPOSITORY_DIR/api && ./gradlew clean generateProto build -x test
cd $REPOSITORY_DIR/backend && ORG_GRADLE_PROJECT_IGNORE_PROTO_DEP=true ./gradlew clean generateProto build -x test
cd $REPOSITORY_DIR/grpc-gateway && make build

docker-compose up -d
