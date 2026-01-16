package model;

import java.util.Map;

public class Operation {
    public String clientId;
    public int position;
    public String text;
    public Map<String, Integer> vectorClock;

    public Operation() {}

    public Operation(String clientId, int position, String text, Map<String, Integer> vectorClock) {
        this.clientId = clientId;
        this.position = position;
        this.text = text;
        this.vectorClock = vectorClock;
    }
}
