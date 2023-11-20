import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UDPServer {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static connection findClient(int port){
        for (int i = 0; i < clients.size(); i++){
            if (clients.get(i).getPortNumber() == port){
                return clients.get(i);
            }
        }
        return null;
    }
    public static class connection{
        public int getPortNumber() {
            return portNumber;
        }

        public String getUsername() {
            return username;
        }

        int portNumber;
        String username;
        LocalDateTime startTime;
        LocalDateTime endTime;
        public connection(LocalDateTime start) {
            this.startTime = start;
        }

    }
    private static List<connection> clients = new ArrayList<>();

    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String user;

        while(true) {
            System.out.println(LocalDateTime.now());
            DatagramPacket receiveMathPacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receiveMathPacket);
            InetAddress IPAddress = receiveMathPacket.getAddress();
            int port = receiveMathPacket.getPort();
            String sentence = new String(receiveMathPacket.getData());

            String [] splitType = sentence.split("\\|");
            String messageType = splitType[0];
            messageType = messageType.trim();
            //System.out.println(messageType);
            String replyMessage = null;
            switch (messageType){
                case "join":
                    connection newClient = new connection(LocalDateTime.now());
                    //clients.add(new connection(LocalDateTime.now()));
                    user = splitType[1];
                    user = user.trim();
                    newClient.username = user;
                    newClient.portNumber = port;
                    clients.add(newClient);
                    System.out.println(user + " has joined \t" + LocalDateTime.now().format(formatter));
                    replyMessage = messageType + "|" + "Welcome, " + user;
                    break;
                case "math":
                    String operation;
                    connection currentUser = findClient(port);
                    user = currentUser.getUsername();
                    try{
                        //System.out.println(LocalDateTime.now());
                        // System.out.println("in the try");
                        String [] splitMath = splitType[1].split(" ");
                        // System.out.println("did the spliteroony");
                        splitMath[0] = splitMath[0].trim();
                        // System.out.println("trim 0: " + splitMath[0]);
                        splitMath[1] = splitMath[1].trim();
                        // System.out.println("trim 1: " + splitMath[1]);
                        splitMath[2] = splitMath[2].trim();
                        // System.out.println("trim 2: " + splitMath[2]);
                        int num1 = Integer.parseInt(splitMath[0]);
                        //  System.out.println("parsed num1");
                        operation = splitMath[1];
                        // System.out.println("set op");
                        int num2 = Integer.parseInt(splitMath[2]);
                        // System.out.println("parsed num2");
                        int result = 0;
                        // System.out.println("into the switch");

                        switch (operation) {
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
                                result = num1 / num2;
                                break;
                            case "%":
                                result = num1 % num2;
                                break;
                            default:
                                throw new Exception("You done goofed");
                        }
                        System.out.println("From: " + user + "\n" + num1 + operation + num2 + "\t" + LocalDateTime.now().format(formatter));
                        String answer = Integer.toString(result);
                        replyMessage = num1 + operation + num2 + "=" + answer;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "end":
                    replyMessage = messageType + "|" + "terminating session";
                    currentUser = findClient(port);
                    System.out.println("ending session with " + currentUser.username + "\t" + LocalDateTime.now().format(formatter));
                    currentUser.endTime = LocalDateTime.now();
            }
            sendData = replyMessage.getBytes();
            DatagramPacket sendpacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            //System.out.println("sending " + answer + " to port num " + port);
            serverSocket.send(sendpacket);
        }
    }
}
