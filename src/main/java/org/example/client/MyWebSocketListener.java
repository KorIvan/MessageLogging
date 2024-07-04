package org.example.client;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class MyWebSocketListener extends WebSocketListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketListener.class);
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private int id;
    private final Map<Integer, Future<?>> submited = new HashMap<>();

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        LOGGER.info("WebSocket opened: " + response);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        LOGGER.info("Received message: " + text);
        id = Integer.parseInt(text);
        LOGGER.info("Current id is [{}]", id);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        LOGGER.info("Received bytes: " + bytes.hex());
        var s = bytes.string(Charset.defaultCharset());
        LOGGER.info("Received id {}", s);
        var id = Integer.getInteger(s);
        if (submited.containsKey(id)) {
            submited.get(id).cancel(true);
        } else {
            LOGGER.warn("id {} not found", id);
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        LOGGER.info("Closing WebSocket: " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        LOGGER.error("Websocket connection failure, response: " + response, t);
    }

    public int getId() {
        return id;
    }

    public void addSubmitted(int objectId, Future<?> f) {
        this.submited.put(objectId, f);
    }
}