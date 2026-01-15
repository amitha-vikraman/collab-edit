import jakarta.websocket.*;
import org.glassfish.tyrus.client.ClientManager;

import java.net.URI;
import java.util.Scanner;

@ClientEndpoint
public class CollabClient {

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
            session.getAsyncRemote().sendText(line);
        }
    }
}
