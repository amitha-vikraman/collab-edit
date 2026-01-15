import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/doc")
public class CollabServer {
//Conflict resolution (OT/CRDT)
//User presence
//Version history
    private static final Set<Session> sessions =
            Collections.synchronizedSet(new HashSet<>());

    private static final StringBuilder document = new StringBuilder();

    @OnOpen
    public void onOpen(Session session) throws Exception {
        sessions.add(session);
        session.getAsyncRemote().sendText(
                "Connected.\nCurrent Document:\n" + document.toString()
        );
        System.out.println("Client connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session sender) {
        synchronized (document) {
            document.append(message).append("\n");
        }
        broadcast("[" + sender.getId() + "] " + message);
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
