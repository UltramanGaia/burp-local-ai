package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.Message;
import burp.api.montoya.ai.chat.PromptOptions;
import io.github.ollama4j.OllamaAPI;

import java.gaia.AbstractSpy;

import static cn.ultramangaia.burp.BurpLocalAIExtension.api;

public class AISpyImpl extends AbstractSpy {

    public static PromptImpl promptImpl;
    public static OllamaAPI ollamaAPI;
    public static String modelName;

    public AISpyImpl() {
        promptImpl = new PromptImpl();
        ollamaAPI = new OllamaAPI();
        ollamaAPI.setRequestTimeoutSeconds(100);
        ollamaAPI.setVerbose(true);
        modelName = "deepseek-r1:32b";
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



}
