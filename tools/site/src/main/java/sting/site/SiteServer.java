package sting.site;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

final class SiteServer {
    private SiteServer() {}

    static void serve(final Path directory) throws IOException {
        final var root = directory.toRealPath();
        final var server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 3000), 0);
        final var types = Map.of(
                "html",
                "text/html; charset=utf-8",
                "css",
                "text/css; charset=utf-8",
                "js",
                "text/javascript; charset=utf-8",
                "svg",
                "image/svg+xml",
                "png",
                "image/png",
                "ico",
                "image/x-icon",
                "json",
                "application/json",
                "xml",
                "application/xml");
        server.createContext("/", exchange -> {
            try (exchange) {
                final var method = exchange.getRequestMethod();
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    exchange.getResponseHeaders().set("Allow", "GET, HEAD");
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }
                var file = root.resolve(exchange.getRequestURI().getPath().substring(1))
                        .normalize();
                if (!file.startsWith(root)) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                if (Files.isDirectory(file)) {
                    if (!exchange.getRequestURI().getPath().endsWith("/")) {
                        exchange.getResponseHeaders()
                                .set("Location", exchange.getRequestURI().getRawPath() + "/");
                        exchange.sendResponseHeaders(301, -1);
                        return;
                    }
                    file = file.resolve("index.html");
                }
                if (!Files.isRegularFile(file) || !file.toRealPath().startsWith(root)) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                final var name = file.getFileName().toString();
                final var extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
                exchange.getResponseHeaders()
                        .set("Content-Type", types.getOrDefault(extension, "application/octet-stream"));
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                if (method.equals("HEAD")) {
                    exchange.getResponseHeaders().set("Content-Length", Long.toString(Files.size(file)));
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    exchange.sendResponseHeaders(200, Files.size(file));
                    Files.copy(file, exchange.getResponseBody());
                }
            }
        });
        server.start();
        System.out.println("Serving " + root
                + " at http://localhost:3000; rebuild with tools/site.sh build and refresh the browser");
    }
}
