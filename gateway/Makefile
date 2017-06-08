all: client server

deps:
	@echo "--> Resolve Dependencies"
	go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway
	go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-swagger
	go get -u github.com/golang/protobuf/protoc-gen-go
	@echo ""

generate-proto:
	@echo "--> Generating Proto"
	cp protobuf/*/*.proto ../api/src/main/proto/ && cd ../api && ./gradlew clean generateProto
	cp protobuf/*/*.proto ../backend/src/main/proto/ && cd ../backend && ./gradlew clean generateProto
	protoc -I/usr/local/include -I. \
	-I$(GOPATH)/src \
	-I$(GOPATH)/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--go_out=plugins=grpc:. \
	protobuf/echo/echo.proto
	protoc -I/usr/local/include -I. \
	-I$(GOPATH)/src \
	-I$(GOPATH)/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--go_out=plugins=grpc:. \
	protobuf/task/task.proto
	@echo ""

generate-grpc-gateway:
	@echo "--> Generating gRPC-gateway"
	protoc -I/usr/local/include -I. \
	-I$(GOPATH)/src \
	-I$(GOPATH)/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--grpc-gateway_out=logtostderr=true:. \
	protobuf/echo/echo.proto
	protoc -I/usr/local/include -I. \
	-I$(GOPATH)/src \
	-I$(GOPATH)/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
	--grpc-gateway_out=logtostderr=true:. \
	protobuf/task/task.proto
	@echo ""
