package main

import (
	"flag"
	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	gw "github.com/soushin/spring5-kotlin-application/grpc-gateway/grpc/gen/soushin/spring5-kotlin-application/task"
	"golang.org/x/net/context"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
	"log"
	"net/http"
	"os"
	"strings"
)

var (
	certPath         = flag.String("cert_path", os.Getenv("CERT_PATH"), "ca file path")
	currencyEndpoint = flag.String("api_endpoint", os.Getenv("ENDPOINT"), "endpoint of Api")
)

func newGateway(ctx context.Context, opts ...runtime.ServeMuxOption) (http.Handler, error) {
	mux := runtime.NewServeMux(opts...)

	var dialOpts []grpc.DialOption
	if len(*certPath) == 0 {
		dialOpts = []grpc.DialOption{grpc.WithInsecure()}
	} else {
		creds, credentialsErr := credentials.NewClientTLSFromFile(*certPath, "")
		if credentialsErr != nil {
			log.Fatalf("Failed to create TLS credentials %v", credentialsErr)
		}
		dialOpts = append(dialOpts, grpc.WithTransportCredentials(creds))
	}

	err := gw.RegisterTaskServiceHandlerFromEndpoint(ctx, mux, *currencyEndpoint, dialOpts)
	if err != nil {
		return nil, err
	}

	return mux, nil
}

func Run(address string, opts ...runtime.ServeMuxOption) error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := http.NewServeMux()

	gw, err := newGateway(ctx, opts...)
	if err != nil {
		return err
	}
	mux.Handle("/", gw)

	return http.ListenAndServe(address, allowCORS(mux))
}

// allowCORS allows Cross Origin Resoruce Sharing from any origin.
// Don't do this without consideration in production systems.
func allowCORS(h http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if origin := r.Header.Get("Origin"); origin != "" {
			w.Header().Set("Access-Control-Allow-Origin", origin)
			if r.Method == "OPTIONS" && r.Header.Get("Access-Control-Request-Method") != "" {
				preflightHandler(w, r)
				return
			}
		}
		h.ServeHTTP(w, r)
	})
}

func preflightHandler(w http.ResponseWriter, r *http.Request) {
	headers := []string{"Content-Type", "Accept"}
	w.Header().Set("Access-Control-Allow-Headers", strings.Join(headers, ","))
	methods := []string{"GET", "HEAD", "POST", "PUT", "DELETE"}
	w.Header().Set("Access-Control-Allow-Methods", strings.Join(methods, ","))
	glog.Infof("preflight request for %s", r.URL.Path)
	return
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := Run(":8081"); err != nil {
		glog.Fatal(err)
	}
}
