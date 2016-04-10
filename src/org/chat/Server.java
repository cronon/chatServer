package org.chat;
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    ArrayList<Client> clients = new ArrayList<>();
    Server(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                ClientThread newThread = new ClientThread(serverSocket.accept());
                newThread.start();
                this.clients.add(newThread);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    synchronized void processInput(ClientThread client, String input){
        System.out.println(client.nickname + " " + input);
        Protocol.processInput(input, client, this.clients);
    }
    synchronized void newClient(ClientThread client, String input){
        System.out.println(input);
        Protocol.newClient(input, client, this.clients);
    }
    class ClientThread extends Thread implements Client{
        Socket socket;
        String nickname;
        PrintWriter out;
        boolean connected = true;
        public ClientThread(Socket socket){
            this.socket = socket;
        }
        public void run(){
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                newClient(this, in.readLine());
                while(this.connected){
                    processInput(this, in.readLine());
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
