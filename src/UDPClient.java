import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        //get port number this client is connected to and print
        int portNum = clientSocket.getLocalPort();
        System.out.println(portNum);
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        boolean runAgain = true;
        while (runAgain){byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            System.out.println("Enter a simple math problem in the format 'a + b'");

            String sentence = inFromUser.readLine();
            sendData = sentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String answerFormat = new String(receivePacket.getData());
            System.out.println("FROM SERVER: " + answerFormat);

            System.out.println("Would you like to ask another question? Enter 1 for yes or 2 for no");
            int again = Integer.parseInt(inFromUser.readLine());
            System.out.println("you entered: " + again);
            if (again == 2){
                runAgain = false;
            }
        }


        clientSocket.close();
    }
}
