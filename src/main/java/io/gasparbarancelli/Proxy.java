package io.gasparbarancelli;

import io.undertow.Undertow;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Proxy {

    public static void main(String[] args) throws URISyntaxException {
        var uris = Optional.ofNullable(System.getenv("uris"));
        if (uris.isPresent()) {
            var connectionsPerThread = Optional.ofNullable(System.getenv("connections-per-thread"))
                    .map(Integer::parseInt).orElse(20);

            LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient()
                    .setConnectionsPerThread(connectionsPerThread);

            for (String uri : uris.get().split(",")) {
                loadBalancer.addHost(new URI(uri));
            }

            var host = Optional.ofNullable(System.getenv("host"))
                    .orElse("localhost");

            var port = Optional.ofNullable(System.getenv("port"))
                    .map(Integer::parseInt).orElse(9999);

            var ioThreads = Optional.ofNullable(System.getenv("io-threads"))
                    .map(Integer::parseInt).orElse(4);

            Undertow reverseProxy = Undertow.builder()
                    .addHttpListener(port, host)
                    .setIoThreads(ioThreads)
                    .setHandler(ProxyHandler.builder().setProxyClient(loadBalancer).setMaxRequestTime(30000).build())
                    .build();
            reverseProxy.start();
        }
    }
}
