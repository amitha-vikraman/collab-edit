package client;

import jakarta.websocket.*;
import model.Operation;
import org.glassfish.tyrus.client.ClientManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.VectorClock;

import java.net.URI;
import java.util.Scanner;
import java.util.UUID;

@ClientEndpoint
public class CollabClient {

    static VectorClock clock = new VectorClock();
    static String clientId = UUID.randomUUID().toString();
    static StringBuilder localDocument = new StringBuilder();

    @OnMessage
    public void onMessage(String msg) {
        System.out.println("\n>> " + msg);
    }

    public static void main(String[] args) throws Exception {
        ClientManager client = ClientManager.createClient();

        Session session = client.connectToServer(
                CollabClient.class,
                URI.create("ws://collab-server:8025/doc")
        );

        System.out.println("Connected. Type text and press Enter:");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            clock.increment(clientId);

            Operation op = new Operation(
                    clientId,
                    localDocument.length(),   // append for now (simple)
                    line,
                    clock.snapshot()
            );

            String json = new ObjectMapper().writeValueAsString(op);
            session.getAsyncRemote().sendText(json);
        }
    }
}
