package model;

import java.util.HashMap;
import java.util.Map;

public class VectorClock {

    private final Map<String, Integer> clock = new HashMap<>();

    public synchronized void increment(String clientId) {
        clock.put(clientId, clock.getOrDefault(clientId, 0) + 1);
    }

    public synchronized Map<String, Integer> snapshot() {
        return new HashMap<>(clock);
    }

    public static boolean isConcurrent(Map<String, Integer> a, Map<String, Integer> b) {
        boolean aBefore = false;
        boolean bBefore = false;

        for (String key : a.keySet()) {
            int av = a.getOrDefault(key, 0);
            int bv = b.getOrDefault(key, 0);

            if (av < bv) aBefore = true;
            if (av > bv) bBefore = true;
        }
        return aBefore && bBefore;
    }
}
