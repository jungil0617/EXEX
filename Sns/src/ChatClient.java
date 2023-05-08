import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private Socket socket;

    public ChatClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        System.out.println("Connected to chat server on " + host + ":" + port);
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        messageArea = new JTextArea(20, 40);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        messageField = new JTextField(40);
        sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        JPanel messagePanel = new JPanel();
        messagePanel.add(messageField);
        messagePanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.getContentPane().add(BorderLayout.SOUTH, messagePanel);
        frame.pack();
        frame.setVisible(true);
        new Thread(new ReceiveThread()).start();
    }

    public void sendMessage(String message) throws IOException {
        Utils.sendMessage(socket.getOutputStream(), message);
    }

    public void addMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
        });
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String message = messageField.getText();
                sendMessage(message);
                messageField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error sending message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = Utils.receiveMessage(socket.getInputStream());
                    addMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 9090);
    }
}
