CLI Collaborative Editor (WebSocket + Java + Docker)

  This project is a lightweight real-time collaborative text editor built using Java, WebSockets, and Docker, designed to run entirely in a command-line interface without any graphical UI dependencies. Multiple clients can connect simultaneously to a central WebSocket server and collaboratively update a shared document in real time. The server manages active client sessions, broadcasts updates instantly, and maintains the in-memory document state, enabling low-latency communication and seamless collaboration. The entire application is containerized, making it fully portable and easy to run on any machine without requiring local Java installation.

  The project demonstrates key backend and distributed systems concepts such as asynchronous messaging, concurrent session handling, event-driven communication, and containerized deployment using multi-stage Docker builds. It serves as a practical foundation for understanding how real-time systems like collaborative editors, chat platforms, and live dashboards operate under the hood. The architecture is intentionally minimal yet extensible, allowing future enhancements such as conflict resolution using CRDTs or vector clocks, persistent storage, user identity management, and scalability improvements — making it an excellent portfolio project for senior backend and platform engineering roles.

- Prerequisites
Install:
Docker
Docker Compose

- Start Server + Client

From the project root:
  docker compose up --build


You should see:
- Collab Server running on ws://localhost:8025/doc
- Connected. Type text and press Enter:

- Open Additional Clients
In a new terminal:
  docker compose run collab-client

- Type messages in any client window — all connected clients will receive updates instantly.
