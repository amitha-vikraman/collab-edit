package server;

import redis.clients.jedis.Jedis;
import java.util.List;

public class RedisStore {

    private static final Jedis jedis = new Jedis("redis", 6379);

    private static final String DOC_KEY = "doc";
    private static final String HISTORY_KEY = "history";

    public static synchronized String getDocument() {
        return jedis.get(DOC_KEY) == null ? "" : jedis.get(DOC_KEY);
    }

    public static synchronized void saveDocument(String doc) {
        jedis.set(DOC_KEY, doc);
    }

    public static synchronized List<String> getHistory() {
        return jedis.lrange(HISTORY_KEY, 0, -1);
    }

    public static synchronized void appendHistory(String opJson) {
        jedis.rpush(HISTORY_KEY, opJson);
    }
}
