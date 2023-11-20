import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
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
        String messageType;
        System.out.println("Please enter your name: ");
        String name = inFromUser.readLine().trim();
        String joinMessage = "join|" + name;
        byte[] joinData = joinMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(joinData, joinData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        byte[] receiveAck = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveAck, receiveAck.length);
        clientSocket.receive(receivePacket);
        String ack = new String(receivePacket.getData()).trim();
        String[] splitAck = ack.split("\\|");
        System.out.println("Server response: " + splitAck[1]);
        //System.out.println(name);
        //messageType = "join|";
        //String whatToSend = messageType + name;
        //byte [] sending = whatToSend.getBytes();
        //DatagramPacket sendPacket = new DatagramPacket(sending, sending.length, IPAddress, 9876);
        //clientSocket.send(sendPacket);
        //byte[] receiveAck = new byte[1024];
        // DatagramPacket receivePacket = new DatagramPacket(receiveAck, receiveAck.length);
        //clientSocket.receive(receivePacket);
        //String ack = new String(receivePacket.getData());

        //String [] splitAck = ack.split("\\|");
        //splitAck[1] = splitAck[1].trim();
        //System.out.println(splitAck[1]);


        while (runAgain){
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            messageType = "math|";
            System.out.println("Enter a simple math problem in the format 'a + b'");
            String sentence = inFromUser.readLine();
            String addheader = messageType + sentence;
            sendData = addheader.getBytes();
            DatagramPacket sendMathPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendMathPacket);
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String answerFormat = new String(receivePacket.getData());
            answerFormat = answerFormat.trim();
            System.out.println("FROM SERVER: " + answerFormat);

            System.out.println("Would you like to ask another question? Enter 1 for yes or 2 for no");
            int again = Integer.parseInt(inFromUser.readLine());
            System.out.println("you entered: " + again);
            if (again == 2){
                runAgain = false;
            }
        }
        String endMessage = "end|";
        byte[] endData = endMessage.getBytes();
        DatagramPacket endPacket = new DatagramPacket(endData, endData.length, IPAddress,9876);
        clientSocket.send(endPacket);
        clientSocket.close();
    }


}
