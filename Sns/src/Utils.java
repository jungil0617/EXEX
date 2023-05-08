import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void sendMessage(OutputStream outputStream, String message) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String receiveMessage(InputStream inputStream) {
        String message = "";
        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            message = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}


