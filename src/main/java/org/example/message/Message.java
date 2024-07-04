package org.example.message;

import java.io.Serializable;

public class Message implements Serializable {
    private final String from;
    private final String group;
    private final MessageType type;

    public Message(String from, String group, MessageType type) {
        this.from = from;
        this.group = group;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public String getGroup() {
        return group;
    }

    public MessageType getType() {
        return type;
    }

}
