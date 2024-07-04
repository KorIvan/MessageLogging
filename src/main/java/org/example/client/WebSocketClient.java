package org.example.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public record WebSocketClient(String name, String group, OkHttpClient okHttpClient, WebSocket webSocket,
                              MyWebSocketListener listener) {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    public boolean register() {
        return webSocket.send(name + "@" + group);
    }

    public void onReceiveEvent(int objectId) {
        int assignedId = listener.getId();
        if (objectId % assignedId == 0) {
            provessEvent(objectId);
        } else {
            Future<?> f = executor.schedule(() -> processAfterDelay(objectId), objectId % assignedId, TimeUnit.SECONDS);
            listener.addSubmitted(objectId, f);
        }
    }

    private void provessEvent(int objectId) {
        LOGGER.info("[{}] Started on id {}", name, objectId);
        webSocket.send(String.valueOf(objectId));
        LOGGER.info("[{}] Processed id {}", name, objectId);
    }

    public void processAfterDelay(int objectId) {
        Request request = new Request.Builder()
                .url(ClientManager.URL)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            var r = response.body() != null ? response.body().string() : "";
            if (r.isBlank()) {
                provessEvent(objectId);
            } else {
                LOGGER.info("[{}] Skipping event {} due to taken to work by {}", name, objectId, r);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
