package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.Message;
import burp.api.montoya.ai.chat.PromptOptions;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatResult;

import java.gaia.AbstractSpy;
import java.util.ArrayList;
import java.util.List;

import static cn.ultramangaia.burp.BurpLocalAIExtension.api;

public class AISpyImpl extends AbstractSpy {

    private static AISpyImpl INSTANCE;

    public PromptImpl promptImpl;
    public OllamaAPI ollamaAPI;
    public String engineName;
    public String modelName;

    private AISpyImpl() {
        promptImpl = new PromptImpl();
        ollamaAPI = new OllamaAPI();
        ollamaAPI.setRequestTimeoutSeconds(100);
        ollamaAPI.setVerbose(true);
        engineName = "ollama";
        modelName = "deepseek-r1:32b";
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
            case "execute(Lburp/api/montoya/ai/chat/PromptOptions;[Ljava/lang/String;)":
                return promptImpl.execute((PromptOptions) args[0], (String[]) args[1]);
            case "execute([Lburp/api/montoya/ai/chat/Message;)":
                return promptImpl.execute((Message[]) args);
            case "execute(Lburp/api/montoya/ai/chat/PromptOptions;[Lburp/api/montoya/ai/chat/Message;)":
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
        OllamaChatResult ollamaResult;
        List<OllamaChatMessage> chatMessages = convertMessages(messages);
        try {
            ollamaResult = ollamaAPI.chat(modelName, chatMessages);
        } catch (Throwable throwable) {
            return throwable.toString();
        }
        return ollamaResult.getResponse();
    }

    private List<OllamaChatMessage> convertMessages(Message... messages) {
        List<OllamaChatMessage> chatMessages = new ArrayList<>();
        for (Message message : messages) {
            if(message instanceof MessageImpl){
                MessageImpl messageImpl = (MessageImpl) message;
                OllamaChatMessageRole role;
                try {
                    role = OllamaChatMessageRole.getRole(messageImpl.getRole());
                }catch(RoleNotFoundException roleNotFoundException){
                    role = OllamaChatMessageRole.USER;
                }
                String content = messageImpl.getContent();
                chatMessages.add(new OllamaChatMessage(role, content));
            }
        }
        return chatMessages;
    }

    public void updateCfg(String engineName, String url, String model, String apiKey) {
        ollamaAPI = new OllamaAPI(url);
        ollamaAPI.setRequestTimeoutSeconds(100);
        ollamaAPI.setVerbose(true);
        this.engineName = engineName;
        this.modelName = model;
        if(apiKey.contains(":")){
            String[] parts = apiKey.split(":");
            ollamaAPI.setBasicAuth(parts[0], parts[1]);
        }
    }
}
