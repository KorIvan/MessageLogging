package org.example.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {

    public static void main(String[] args) throws InterruptedException {
        String groupA = "groupA";
        int n = 10;
        Random r = new Random();
        Collection<Client> clients = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            clients.add(new Client("0" + i, groupA, r.nextInt(100)));
        }
        ExecutorService executor = Executors.newFixedThreadPool(n);
        executor.invokeAll(clients, 100, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(200, TimeUnit.SECONDS);
    }

    private static class Client implements Callable<Integer> {
        String name;
        String group;
        int ttlSecs;
        WebSocket webSocket;

        public Client(String name, String group, int ttlSecs) {
            this.name = name;
            this.group = group;
            this.ttlSecs = ttlSecs;
        }

        @Override
        public Integer call() {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("ws://localhost:8080/ws").build();
            MyWebSocketListener listener = new MyWebSocketListener();
            WebSocket webSocket = client.newWebSocket(request, listener);
            webSocket.send(name + "@" + group);
            return 0;
        }
    }
}