# **CLI Collaborative Editor (WebSocket + Java + Docker)**

This project is a lightweight real-time collaborative text editor built using Java, WebSockets, and Docker, designed to run entirely in a command-line interface without any graphical UI dependencies. Multiple clients can connect simultaneously to a central WebSocket server and collaboratively update a shared document in real time. The server manages active client sessions, broadcasts updates instantly, and maintains the in-memory document state, enabling low-latency communication and seamless collaboration. The entire application is containerized, making it fully portable and easy to run on any machine without requiring local Java installation.

  The project demonstrates key backend and distributed systems concepts such as asynchronous messaging, concurrent session handling, event-driven communication, and containerized deployment using multi-stage Docker builds. It serves as a practical foundation for understanding how real-time systems like collaborative editors, chat platforms, and live dashboards operate under the hood. The architecture is intentionally minimal yet extensible, allowing future enhancements such as conflict resolution using CRDTs or vector clocks, persistent storage, user identity management, and scalability improvements.

## 1. **Prerequisites:**
**Install:**
- Docker
- Docker Compose

## 2. **Start Server + Client**

From the project root:
  ```docker compose up --build```


You should see:
- Collab Server running on ws://localhost:8025/doc
- Connected. Type text and press Enter:

## 3. **Open Additional Clients**
In a new terminal:
  ```docker compose run collab-client```

- Type messages in any client window — all connected clients will receive updates instantly.

## Recent Enhancements

- Added WebSocket-based real-time collaboration using Jakarta WebSocket (Tyrus), enabling multiple clients to connect and edit a shared document concurrently.
- Implemented a lightweight Operational Transformation (OT) mechanism on the server to automatically resolve concurrent edits by adjusting insertion positions.
- Introduced Vector Clocks to detect concurrent operations and preserve causal ordering between edits across distributed clients.
- Enhanced document handling to support line-based input, ensuring each client entry is appended as a new line for better readability and realistic editor behavior.
- Maintained an in-memory operation history to enable transformation logic and ensure consistency across all connected clients.
- Implemented JSON-based message exchange using Jackson for structured operation serialization and deserialization.
- Packaged the application using multi-stage Docker builds, allowing the system to run without requiring Java or Maven installed locally.
- Enabled multi-container orchestration using Docker Compose for seamless startup of server and multiple clients.
- Enabled horizontal scaling of the WebSocket server using Docker Compose by running multiple container replicas without host port binding.
- Leveraged Docker’s internal service networking and load balancing to distribute client connections across multiple server instances.
