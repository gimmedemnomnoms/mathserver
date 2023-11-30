import java.io.*;
import java.net.*;

public class UDPClient {
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private int serverPort = 8888;

    public static void main(String[] args) throws Exception {
        UDPClient client = new UDPClient();
        client.createClientSocket();
        client.joinServer();
        client.mathQuestions();
        client.endSession();
    }
    private void createClientSocket() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("127.0.0.1");
    }

    private void joinServer() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter your name: ");
        String name = inFromUser.readLine().trim();
        String joinMessage = "join|" + name;
        sendPacket(joinMessage);
        String serverResponse = receiveServerResponse();
        System.out.println(serverResponse);
    }

    private void mathQuestions() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        boolean runAgain = true;

        while (runAgain){
            System.out.println("Enter a simple math problem in the format 'a + b'");
            String sentence = inFromUser.readLine();
            String message = "math|" + sentence;
            sendPacket(message);
            String response = receiveServerResponse();
            System.out.println("From server: " + response);
            System.out.println("Would you like to ask another question? Enter 1 for yes or 2 for no");
            int again = Integer.parseInt(inFromUser.readLine());
            if (again == 2){
                runAgain = false;
            }
        }
    }
    private void endSession() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press any key to terminate program.");
        inFromUser.readLine();
        sendPacket("end|");
        clientSocket.close();
    }

    private void sendPacket(String message) throws IOException {
        byte [] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
        clientSocket.send(sendPacket);
    }
    private String receiveServerResponse() throws IOException {
        byte [] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        return new String(receivePacket.getData()).trim();
    }
}
