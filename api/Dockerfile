FROM java:openjdk-8

COPY ./build/libs/app.api.jar /api-server/app.api.jar

ENV ORG_GRADLE_PROJECT_IGNORE_PROTO_DEP=true

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y ca-certificates&& \
    echo "export LANG=C.UTF-8" > /etc/profile.d/locale.sh && \
    rm -rf /etc/localtime && \
    ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    \
    mkdir -p /usr/local/api-server/lib && \
    cp -R /api-server/app.api.jar /usr/local/api-server/lib/app.api.jar && \
    rm -rf /api-server

ENTRYPOINT java $JAVA_OPTS -jar /usr/local/api-server/lib/app.api.jar

EXPOSE 8080 8080
