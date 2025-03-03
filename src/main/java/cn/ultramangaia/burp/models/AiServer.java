package cn.ultramangaia.burp.models;

import cn.ultramangaia.burp.gui.MainForm;
import cn.ultramangaia.burp.models.chat.*;
import cn.ultramangaia.burp.persistence.PersistedObject;
import com.alibaba.fastjson2.JSONObject;

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
        ChatResult chatResult = chatService.chat(messages);
        MainForm.getInstance().addAiLog(chatResult);
        return chatResult.content;
    }

    public String chat(String engine, JSONObject messages) {
        ChatService chatService = chatServices.get(engine);
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
