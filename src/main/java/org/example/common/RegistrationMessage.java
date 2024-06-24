package org.example.common;

import org.springframework.web.socket.AbstractWebSocketMessage;

import java.io.Serial;
import java.io.Serializable;

public class RegistrationMessage implements Serializable {

    private static final long serialVersionUID = -123456789L;
    private final int assignedId;
    private final String group;

    public RegistrationMessage(int assignedId, String group) {
        this.assignedId = assignedId;
        this.group = group;
    }

    public int getAssignedId() {
        return assignedId;
    }

    public String getGroup() {
        return group;
    }
}
