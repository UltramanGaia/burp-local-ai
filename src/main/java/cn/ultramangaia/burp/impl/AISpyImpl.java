package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.Message;
import burp.api.montoya.ai.chat.PromptOptions;
import cn.ultramangaia.burp.models.AiServer;
import cn.ultramangaia.burp.models.chat.ChatMessage;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatResult;

import java.gaia.AbstractSpy;
import java.util.ArrayList;
import java.util.List;

import static burp.BurpLocalAIExtension.api;

public class AISpyImpl extends AbstractSpy {

    private static AISpyImpl INSTANCE;

    public PromptImpl promptImpl;

    private AISpyImpl() {
        promptImpl = new PromptImpl();
    }

    public static AISpyImpl getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AISpyImpl();
        }
        return INSTANCE;
    }

    @Override
    public Object handle(String methodInfo, Object target, Object[] args) {
        api.logging().logToOutput("calling: " + methodInfo);
        switch(methodInfo) {
            case "execute([Ljava/lang/String;)":
                return promptImpl.execute((String[]) args);
            case "execute(Lburp/api/montoya/ai/post/PromptOptions;[Ljava/lang/String;)":
                return promptImpl.execute((PromptOptions) args[0], (String[]) args[1]);
            case "execute([Lburp/api/montoya/ai/post/Message;)":
                return promptImpl.execute((Message[]) args);
            case "execute(Lburp/api/montoya/ai/post/PromptOptions;[Lburp/api/montoya/ai/post/Message;)":
                return promptImpl.execute((PromptOptions) args[0], (Message[]) args[1]);
            case "prompt()":
                return promptImpl;
            case "promptOptions()":
                return new PromptOptionsImpl();
            case "systemMessage(Ljava/lang/String;)":
                return new MessageImpl("system", (String)((Object[]) args[2])[0]);
            case "userMessage(Ljava/lang/String;)":
                return new MessageImpl("user", (String)((Object[]) args[2])[0]);
            case "assistantMessage(Ljava/lang/String;)":
                return new MessageImpl("assistant", (String)((Object[]) args[2])[0]);
        }
        return null;
    }

    public String chat(Message... messages){
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (Message message : messages) {
            if(message instanceof MessageImpl){
                MessageImpl messageImpl = (MessageImpl) message;
                chatMessages.add(new ChatMessage(messageImpl.getRole(), messageImpl.getContent()));
            }
        }
        return AiServer.getInstance().chat(chatMessages);
    }
}
