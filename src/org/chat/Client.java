package org.chat;

public interface Client {
    void send(String text);
    String getNickname();
    void disconnect();
    void setNickname(String nickname);
}
