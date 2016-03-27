package org.chat;

import java.util.ArrayList;
import java.util.Date;

public class Protocol {
    public static void newClient(String input, Client client, ArrayList<Client> clients){
        if(input.matches("^connect .*")){
            String nickname = input.replaceFirst("^connect ", "");
            if(!getNicknames(clients).contains(nickname)){
                client.setNickname(nickname);
            } else {
                client.send("server error nickname" + nickname + " is already in use");
                setToAnonymous(client);
            }
        } else {
            client.send("server error expected 'connect <nickname>' but got" + input);
            setToAnonymous(client);
        }
        broadcast("server connect " + client.getNickname(), clients);
    }
    static void setToAnonymous(Client client){
        long timestamp = (new Date()).getTime();
        client.setNickname("Anonymous" + String.valueOf(timestamp));
    }

    public static void processInput(String input, Client client, ArrayList<Client> clients){
        if(input.matches("^server .*")){
            server(input.replaceFirst("^server ", ""), client, clients);
        } else {
            if(input.matches("^chat .*")){
                input = input.replaceFirst("^chat ", "");
            }
            chat(input, client, clients);
        }
    }

    static void server(String input, Client client, ArrayList<Client> clients){
        if(input.matches("^nicknames")){
            nicknames(client, clients);
        } else if(input.matches("^disconnect")) {
            disconnect(client, clients);
        } else if (input.matches("^nickname .*")){
            nickname(input.replaceFirst("^nickname ", ""), client, clients);
        } else {
            client.send("server error unsupported command " + input);
        }
    }
    static void nicknames(Client client, ArrayList<Client> clients){
        ArrayList<String> names = getNicknames(clients);
        String output = "server nicknames " + String.join(" ", names);
        client.send(output);
    }
    static void disconnect(Client client, ArrayList<Client> clients){
        client.disconnect();
        clients.remove(client);
        broadcast("server disconnect " + client.getNickname(), clients);
    }
    static void nickname(String nickname, Client client, ArrayList<Client> clients){
        if(getNicknames(clients).contains(nickname)){
            client.send("server nickname " + nickname + " is already in use");
        } else {
            String old = client.getNickname();
            client.setNickname(nickname);
            broadcast("server nickname " + old + " " + client.getNickname(), clients);
        }
    }
    static void chat(String input, Client client, ArrayList<Client> clients){
        String output = "chat " + client.getNickname() + " " + input;
        broadcast(output, clients);
    }
    static void broadcast(String text, ArrayList<Client> clients){
        clients.forEach(c -> c.send(text));
    }
    static ArrayList<String> getNicknames(ArrayList<Client> clients){
        ArrayList<String> result = new ArrayList<>();
        clients.forEach(c -> result.add(c.getNickname()));
        return result;
    }
}