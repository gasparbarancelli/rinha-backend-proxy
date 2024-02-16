package io.gasparbarancelli;

import io.undertow.Undertow;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;

import java.net.URI;
import java.net.URISyntaxException;

public class Proxy {

    public static void main(String[] args) throws URISyntaxException {
        LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient()
                .addHost(new URI("http://localhost:8081"))
                .addHost(new URI("http://localhost:8082"))
                .setConnectionsPerThread(20);

        Undertow reverseProxy = Undertow.builder()
                .addHttpListener(9999, "localhost")
                .setIoThreads(4)
                .setHandler(ProxyHandler.builder().setProxyClient(loadBalancer).setMaxRequestTime( 30000).build())
                .build();
        reverseProxy.start();
    }
}
