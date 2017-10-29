FROM java:openjdk-8

COPY ./build/libs/app.backend.jar /api-server/app.backend.jar
COPY ./entrypoint.sh /entrypoint.sh

ENV ORG_GRADLE_PROJECT_IGNORE_PROTO_DEP=true

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y ca-certificates&& \
    echo "export LANG=C.UTF-8" > /etc/profile.d/locale.sh && \
    rm -rf /etc/localtime && \
    ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    \
    mkdir -p /usr/local/api-server/lib && \
    cp -R /api-server/app.backend.jar /usr/local/api-server/lib/app.backend.jar && \
    rm -rf /api-server

ENTRYPOINT ["/entrypoint.sh"]

EXPOSE 50051 50051
