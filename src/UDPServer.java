import java.io.*;
import java.net.*;

public class UDPServer {
   public static void main(String args[]) throws Exception {
       DatagramSocket serverSocket = new DatagramSocket(9876);
       byte[] receiveData = new byte[1024];
       byte[] sendData = new byte[1024];

       while(true) {
           DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
           serverSocket.receive(receivePacket);

           String sentence = new String(receivePacket.getData());
           ////////////// ADDED
           InetAddress IPAddress = receivePacket.getAddress();
           int port = receivePacket.getPort();
           System.out.println("received: " + sentence + " from user " + port);
           String[] splitsentence = sentence.split(" ");
           splitsentence[0] = splitsentence[0].trim();
           splitsentence[1] = splitsentence[1].trim();
           splitsentence[2] = splitsentence[2].trim();
           int num1 = Integer.parseInt(splitsentence[0]);
           String operation = splitsentence[1];
           int num2 = Integer.parseInt(splitsentence[2]);

           int result = 0;

           switch(operation) {
               case "+":
                   result = num1 + num2;
                   break;
               case "-":
                   result = num1 - num2;
                   break;
               case "*":
                   result = num1 * num2;
                   break;
               case "/":
                   result = num1/num2;
                   break;
               case "%":
                   result = num1 % num2;
                   break;
               default:
                   System.out.println("invalid");
                   break;
           }
           String answer = Integer.toString(result);

////////////////////


           //String caps = sentence.toUpperCase();
           sendData = answer.getBytes(); //modded

           DatagramPacket sendpacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
           System.out.println("sending " + answer + " to port num " + port);

           serverSocket.send(sendpacket);

       }
   }


}
