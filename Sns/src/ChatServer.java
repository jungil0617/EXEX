import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private List<ClientHandler> clients = new ArrayList<>();

    public ChatServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Chat server started on port " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostName());
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = Utils.receiveMessage(socket.getInputStream());
                    if (username == null) {
                        username = message;
                        broadcast(username + " has joined the chat.");
                    } else {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clients.remove(this);
                broadcast(username + " has left the chat.");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage(message);
                }
            }
        }

        private void sendMessage(String message) {
            try {
                Utils.sendMessage(socket.getOutputStream(), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // 이 부분에 중괄호 추가

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(9090);
    }
}
