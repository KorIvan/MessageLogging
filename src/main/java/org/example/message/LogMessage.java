package org.example.message;

public class LogMessage extends Message {
    private int eventId;

    public LogMessage(String name, String group, int eventId) {
        super(name, group, MessageType.LOG);
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }
}
