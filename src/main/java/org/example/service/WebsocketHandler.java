package org.example.service;

import com.hazelcast.core.HazelcastInstance;
import org.example.data.GroupData;
import org.example.data.GroupHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebsocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketHandler.class);
    private static final String GROUP = "group";

    private final HazelcastInstance hazelcastInstance;
    private final GroupHandler groupHandler;

    public WebsocketHandler(HazelcastInstance hazelcastInstance, GroupHandler groupHandler) {
        this.hazelcastInstance = hazelcastInstance;
        this.groupHandler = groupHandler;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] info = message.getPayload().split("@");
        var name = info[0];
        var groupName = info[1];
        var sessionId = session.getId();
        LOGGER.info("associate session [{}] with client [{}]", sessionId, name);
        var groupMap = hazelcastInstance.getMap(groupName);
        groupMap.lock(groupName);
        if (groupMap.isEmpty()) {
            var newGroup = new GroupData();
            int assignedId = groupHandler.addClient(sessionId, newGroup.getMap(), newGroup.getCounter());
            groupMap.put(groupName, newGroup);
            session.sendMessage(new TextMessage(Integer.toString(assignedId)));
            session.getAttributes().put(GROUP, groupName);
            LOGGER.info("Created group {} and assigned id [{}] to [{}] for session [{}]", groupName, assignedId, name, sessionId);
        } else {
            var existingGroup = (GroupData) groupMap.get(groupName);
            int assignedId = groupHandler.addClient(sessionId, existingGroup.getMap(), existingGroup.getCounter());
            groupMap.put(groupName, existingGroup);
            session.getAttributes().put(GROUP, groupName);
            session.sendMessage(new TextMessage(Integer.toString(assignedId)));
            LOGGER.info("Assigned id [{}] to [{}] for session [{}]", assignedId, name, sessionId);
        }
        groupMap.unlock(groupName);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var groupName= (String) session.getAttributes().get(GROUP);
        var groupMap = hazelcastInstance.getMap(groupName);
        groupMap.lock(groupName);
        var existingGroup = (GroupData) groupMap.get(groupName);
        groupHandler.removeClient(session.getId(), existingGroup.getMap(), existingGroup.getCounter());
        groupMap.unlock(groupName);
        LOGGER.info("Removed client [{}] from [{}] on closing connection", session.getId(), groupName);
    }

}