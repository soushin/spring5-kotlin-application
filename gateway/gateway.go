package main


import (
"flag"
"net/http"

"github.com/golang/glog"
"golang.org/x/net/context"
"github.com/grpc-ecosystem/grpc-gateway/runtime"
"google.golang.org/grpc"

gwecho "github.com/nsoushi/spring5-kotlin-application/gateway/protobuf/echo"
gwtask "github.com/nsoushi/spring5-kotlin-application/gateway/protobuf/task"
)

var (
	echoEndpoint = flag.String("echo_endpoint", "localhost:50052", "endpoint of YourService")
)

func run() error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}

	var err error
	err = gwecho.RegisterEchoServiceHandlerFromEndpoint(ctx, mux, *echoEndpoint, opts)
	if err != nil {
		return err
	}
	err = gwtask.RegisterTaskServiceHandlerFromEndpoint(ctx, mux, *echoEndpoint, opts)
	if err != nil {
		return err
	}

	return http.ListenAndServe(":8081", mux)
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := run(); err != nil {
		glog.Fatal(err)
	}
}
