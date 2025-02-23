package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.Message;

public class MessageImpl implements Message {
    private String role;
    private String content;

    public MessageImpl(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return role + ":" + content;
    }
}
