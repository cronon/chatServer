package org.chat;

import java.util.ArrayList;

public class Protocol {
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
        } else if (input.matches("^nick .*")){
            nick(input.replaceFirst("^nick ", ""), client, clients);
        } else {
            client.send("server unsupported command " + input);
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
        broadcast("server disconnected " + client.getNickname(), clients);
    }
    static void nick(String nickname, Client client, ArrayList<Client> clients){
        if(getNicknames(clients).contains(nickname)){
            client.send("server nick is already in use");
        } else {
            String old = client.getNickname();
            client.setNickname(nickname);
            broadcast("server nick " + old + " " + client.getNickname(), clients);
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
        for(Client c: clients){
            result.add(c.getNickname());
        }
        return result;
    }
};