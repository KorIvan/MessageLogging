package org.example.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientManager {

    public static final String URL = "ws://localhost:8080/ws";
    private final Map<String, List<WebSocketClient>> groupClients = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void createGroup(String group) {
        int n = 10;
        Random r = new Random();
        Collection<WebSocketClient> clients = new ArrayList<>();
        for (int i = 0; i < n; i++) {
//            clients.add(new WebSocketClient("0" + i, group, r.nextInt(100)));
        }

    }

    public void generateClients(String group, int n) {
        var client = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();
        var listner = new MyWebSocketListener();
        WebSocket webSocket = client.newWebSocket(request, listner);
        for (int i = 0; i < n; i++) {
            groupClients.get(group).add(new WebSocketClient("0" + i, group, client, webSocket, listner));
        }
    }

    public void stopNClients(String group, int n) {
        var clients = groupClients.get(group);
        while (n >= 0) {
            int c = clients.size() - n;
            var temp = clients.get(c);
            temp.webSocket().close(1000, "Close client");
            clients.remove(clients.size() - 1 - n--);
        }
    }

    public void stopAllClient(String group) {
        stopNClients(group, 10);
    }
}
