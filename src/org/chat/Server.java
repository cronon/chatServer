package org.chat;
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    int nextId = 0;
    ArrayList<Client> clients = new ArrayList<>();
    Server(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                ClientThread newThread = new ClientThread(serverSocket.accept(), this.nextId++);
                newThread.start();
                this.clients.add(newThread);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    synchronized void processInput(ClientThread client, String input){
        System.out.println(client.nickname+ " " + input);
        Protocol.processInput(input, client, this.clients);
    }

    class ClientThread extends Thread implements Client{
        Socket socket;
        String nickname;
        PrintWriter out;
        boolean connected = true;
        public ClientThread(Socket socket, int id){
            this.socket = socket;
            this.nickname = "Anonymous" + String.valueOf(id);
        }
        public void run(){
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine, outputLine;
                while(this.connected){
                    inputLine = in.readLine();
                    processInput(this, inputLine);
                }
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        public void disconnect(){
            this.connected = false;
        }
        public void send(String text){
            this.out.println(text);
        }
        public String getNickname(){
            return this.nickname;
        }
        public void setNickname(String nickname){
            this.nickname = nickname;
        }
    }

    public static void main(String[] args) {
        int port;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        } else {
            port = 7777;
        }
        new Server(port);
    }
}
