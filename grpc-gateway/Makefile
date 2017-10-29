VERSION=$(shell git rev-parse --verify HEAD)

run:
	ENDPOINT=localhost:50051 \
	go run main.go

build:
	GO15VENDOREXPERIMENT=1 GOOS=linux GOARCH=amd64 go build -o bin/grpc-gateway main.go

deps: install-glide
	GO15VENDOREXPERIMENT=1 glide install --cache

deps-update: install-glide
	rm -rf ./vendor
	GO15VENDOREXPERIMENT=1 glide update --cache


install-grpcgateway:
	go get github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway
	go get github.com/grpc-ecosystem/grpc-gateway/protoc-gen-swagger

install-protodep:
	go get github.com/stormcat24/protodep

update-proto:
	protodep up -f --identity-file=github_rsa

protoc:
	make protoc-stub
	make protoc-gateway
	make protoc-swagger

protoc-stub:
	mkdir -p grpc/gen/
	protoc -I/usr/local/include \
	-I./proto \
	-I$$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--go_out=plugins=grpc:grpc/gen/ \
	./proto/soushin/spring5-kotlin-application/task/task.proto

protoc-gateway:
	mkdir -p grpc/gen/
	protoc -I/usr/local/include \
	-I./proto \
	-I$$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--grpc-gateway_out=logtostderr=true:grpc/gen/ \
	./proto/soushin/spring5-kotlin-application/task/task.proto

protoc-swagger:
	mkdir -p grpc/gen/
	protoc -I/usr/local/include \
	-I./proto \
	-I$$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--swagger_out=logtostderr=true:grpc/gen/ \
	./proto/soushin/spring5-kotlin-application/task/task.proto
