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

    private static final StringBuilder document = new StringBuilder();
    private static final List<Operation> history = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) throws Exception {
        sessions.add(session);
        session.getAsyncRemote().sendText(
                "Connected.\nCurrent Document:\n" + document.toString()
        );
        System.out.println("Client connected: " + session.getId());
    }

    @OnMessage
    public synchronized void onMessage(String message, Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Operation incoming = mapper.readValue(message, Operation.class);

            // Simple OT: shift position if concurrent ops happened before
            for (Operation past : history) {
                if (VectorClock.isConcurrent(incoming.vectorClock, past.vectorClock)) {
                    if (past.position <= incoming.position) {
                        incoming.position += past.text.length();
                    }
                }
            }

            String textWithNewline = incoming.text + "\n";
            document.insert(incoming.position, textWithNewline);
            history.add(incoming);

            broadcast(">" + document.toString());

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
