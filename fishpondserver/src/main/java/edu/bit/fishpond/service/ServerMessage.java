package edu.bit.fishpond.service;

public class ServerMessage {

    private final int targetId;
    private final String message;

    public ServerMessage(int targetId, String messageHead, String messageBody){
        this.targetId = targetId;

        message = messageHead + "|" + messageBody;
    }

    public int getTargetId() {
        return targetId;
    }

    public String getMessage() {
        return message;
    }
}
