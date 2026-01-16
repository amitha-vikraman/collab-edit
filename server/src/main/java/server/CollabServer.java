package server;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Operation;
import model.VectorClock;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@ServerEndpoint("/doc")
public class CollabServer {

    private static final Set<Session> sessions =
            Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) throws Exception {
        sessions.add(session);
        session.getAsyncRemote().sendText(
                "Connected."
        );
        System.out.println("Client connected: " + session.getId());
    }

    @OnMessage
    public synchronized void onMessage(String message, Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Operation incoming = mapper.readValue(message, Operation.class);

            // Load shared state from Redis
            String document = RedisStore.getDocument();
            List<String> historyJson = RedisStore.getHistory();

            List<Operation> history = new ArrayList<>();
            for (String h : historyJson) {
                history.add(mapper.readValue(h, Operation.class));
            }

            // --- OT Transform ---
            for (Operation past : history) {
                if (VectorClock.isConcurrent(incoming.vectorClock, past.vectorClock)) {
                    if (past.position <= incoming.position) {
                        incoming.position += past.text.length() + 1; // +1 for newline
                    }
                }
            }

            // Apply operation
            String textWithNewline = incoming.text + "\n";
            StringBuilder updatedDoc = new StringBuilder(document);
            updatedDoc.insert(incoming.position, textWithNewline);

            // Persist back to Redis
            RedisStore.saveDocument(updatedDoc.toString());
            RedisStore.appendHistory(message);

            // Broadcast to connected clients
            broadcast(">" + updatedDoc.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    private void broadcast(String msg) {
        synchronized (sessions) {
            for (Session s : sessions) {
                try {
                    s.getAsyncRemote().sendText(msg);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server("0.0.0.0", 8025, "/", null, CollabServer.class);
        server.start();
        System.out.println("🚀 Collab Server running on ws://localhost:8025/doc");
        Thread.currentThread().join();
    }
}
//Conflict resolution (OT/CRDT)
//User presence
//Version history
