package cn.ultramangaia.burp.models.chat;

import java.util.List;

public interface ChatService {
    public ChatResult chat(List<ChatMessage> messages);
}
