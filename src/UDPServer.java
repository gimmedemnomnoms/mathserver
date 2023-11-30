import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private static final String LOG_FILENAME = "serverlog.txt";
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
    private static List<Connection> clients = new ArrayList<>();
    private static List<logEntry> log = new ArrayList<>();
    private static DatagramSocket serverSocket;
    private static int connectionCounter;
    public static void main(String[] args) throws Exception {


        createServerSocket();
        while (true){
            DatagramPacket receivePacket = receivePacket();
            unpackPacket(receivePacket);
            if (connectionCounter == 0){
                System.out.println("\n\nPRINTING SERVER LOG\n-------------------");
                for (int i = 0; i < log.size(); i++){
                    System.out.println(log.get(i));
                }
            }
        }
    }
    private static void createServerSocket() throws SocketException{
        serverSocket = new DatagramSocket(8888);
    }
    private static DatagramPacket receivePacket() throws IOException {
        byte [] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        return receivePacket;
    }
    private static void unpackPacket(DatagramPacket packet){
        LocalDateTime receiveTimeStamp = LocalDateTime.now();
        String sentence = new String(packet.getData());
        String[] splitType = sentence.split("\\|");
        String messageType = splitType[0].trim();
        String replyMessage;
        switch (messageType){
            case "join":
                replyMessage = join(splitType, packet.getPort(),receiveTimeStamp);
                break;
            case "math":
                replyMessage = math(splitType, packet.getPort(), receiveTimeStamp);
                break;
            case "end":
                replyMessage = end(packet.getPort(), receiveTimeStamp);
                break;
            default:
                replyMessage = "you done goofed";
        }
        sendResponse(replyMessage, packet.getAddress(), packet.getPort());

    }
    private static String join(String[] splitType, int port, LocalDateTime timeStamp) {
        String user = splitType[1].trim();
        Connection newClient = new Connection(timeStamp);
        newClient.username = user;
        newClient.portNumber = port;
        clients.add(newClient);
        logEntry currentEntry = new logEntry(newClient, "user started session", timeStamp);
        log.add(currentEntry);
        System.out.println("New user: " + user);
        connectionCounter++;
        return "Welcome, " + user;
    }
    private static String math(String[] splitType, int port, LocalDateTime timeStamp){
        Connection currentUser = findClient(port);
        String user = currentUser.getUsername();

        try {
            String [] splitMath = splitType[1].split(" ");
            int num1 = Integer.parseInt(splitMath[0].trim());
            String operation = splitMath[1].trim();
            int num2 = Integer.parseInt(splitMath[2].trim());
            int result = calculateMath(num1, operation, num2);
            System.out.println("From " + user + "\t \t" + num1 + operation + num2);
            logEntry currentEntry = new logEntry(currentUser, num1 + operation + num2, timeStamp);
            log.add(currentEntry);
            //System.out.println(currentEntry);
            return num1 + operation + num2 + "=" + result;
        }
        catch (Exception e) {
            return "Error";
        }
    }
    private static String end(int port, LocalDateTime timeStamp){
        Connection currentUser = findClient(port);
        currentUser.endTime = timeStamp;
        log.add(new logEntry(currentUser, "user terminated session", timeStamp));
        System.out.println("Ending session with " + currentUser.username + "\t" + currentUser.endTime.format(formatter));
        connectionCounter--;
        return "end|terminating session";
    }
    private static void sendResponse(String message, InetAddress IPAddress, int port){
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, port);
            serverSocket.send(sendPacket);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static int calculateMath(int num1, String operation, int num2) throws Exception {
        switch (operation) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                return num1 / num2;
            case "%":
                return num1 % num2;
            default:
                throw new Exception("Invalid");
        }
    }
    private static Connection findClient(int port){
        for (int i = 0; i < clients.size(); i++){
            if (clients.get(i).getPortNumber() == port) {
                return clients.get(i);
            }
        }
        return null;
    }
    public static class Connection {
        int portNumber;
        String username;
        LocalDateTime startTime;
        LocalDateTime endTime;


        public Connection(LocalDateTime start) {
            this.startTime = start;
        }

        public int getPortNumber() {
            return portNumber;
        }
        public String getUsername(){
            return username;
        }
    }
    public static class logEntry{
        Connection client;
        String message;
        LocalDateTime timeStamp;

        @Override
        public String toString() {
            return String.format("%-10s%-25s%-40s%-30s", "logEntry", "user: " + client.username, "message: " + message, "time: " + timeStamp);

        }

        public logEntry(Connection client, String message, LocalDateTime timeStamp) {
            this.client = client;
            this.message = message;
            this.timeStamp = timeStamp;
            writeToLog();
        }
        private void writeToLog() {
            BufferedWriter writer = null;
            try {
                // Create the file if it doesn't exist
                FileWriter fileWriter = new FileWriter(LOG_FILENAME, true);
                writer = new BufferedWriter(fileWriter);

                // Write log entry details to the file
                writer.write(this.toString());
                writer.newLine();  // Add a newline for better readability between entries
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the writer in a finally block to ensure it gets closed
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
