package cn.ultramangaia.burp.models;

import cn.ultramangaia.burp.models.chat.*;
import cn.ultramangaia.burp.persistence.PersistedObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.ultramangaia.burp.util.Globals.*;

public class AiServer {

    private static AiServer INSTANCE;
    public String engineName;

    public Map<String, ChatService> chatServices;

    private AiServer() {
        engineName = OLLAMA;
        chatServices = new HashMap<>();
        chatServices.put(OLLAMA, new OllamaChatService("http://127.0.0.1:11434", "/api/chat", "deepseek-r1:32b", ""));
    }

    public static AiServer getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AiServer();
        }
        return INSTANCE;
    }

    public String chat(List<ChatMessage> messages) {
        ChatService chatService = chatServices.get(engineName);
        if (chatService == null) {
            return "unknown engine";
        }
        return chatService.chat(messages).content;
    }


    public void updateCfg(String engineName) {
        PersistedObject po = PersistedObject.getInstance();
        this.engineName = engineName;
        switch (engineName){
            case OLLAMA:
                chatServices.put(engineName, new OllamaChatService(po.getString("ollama.host"), po.getString("ollama.url"), po.getString("ollama.model"), po.getString("ollama.apikey")));
                break;
            case OPENAI:
                chatServices.put(engineName, new OpenaiChatService(po.getString("openai.host"), po.getString("openai.url"), po.getString("openai.model"), po.getString("openai.apikey")));
                break;
            case DEEPSEEK:
                chatServices.put(engineName, new DeepseekChatService(po.getString("deepseek.host"), po.getString("deepseek.url"), po.getString("deepseek.model"), po.getString("deepseek.apikey")));
                break;
            default:
                break;
        }
    }
}
