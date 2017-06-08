FROM java:openjdk-8

COPY . /api-server

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y ca-certificates&& \
    echo "export LANG=C.UTF-8" > /etc/profile.d/locale.sh && \
    rm -rf /etc/localtime && \
    ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    \
    cd /api-server && ./gradlew clean && \
    cd /api-server && rm -rf ~/.gradle && ./gradlew clean build -x test && \
    mkdir -p /usr/local/api-server/lib && \
    cp -R /api-server/build/libs/* /usr/local/api-server/lib/ && \
    rm -rf /api-server

ENTRYPOINT java $JAVA_OPTS -jar /usr/local/api-server/lib/app.api.jar

EXPOSE 8080 8080
EXPOSE 50051 50051
