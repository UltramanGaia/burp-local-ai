package cn.ultramangaia.burp.models.chat;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public interface ChatService {
    public ChatResult chat(List<ChatMessage> messages);
    public ChatResult chat(JSONObject body);
}
