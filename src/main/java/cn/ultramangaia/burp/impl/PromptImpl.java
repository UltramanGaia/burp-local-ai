package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.*;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;

import static cn.ultramangaia.burp.BurpLocalAIExtension.api;

public class PromptImpl implements Prompt {
    @Override
    public PromptResponse execute(String... messages) throws PromptException {
        return execute(new PromptOptionsImpl(), messages);
    }

    @Override
    public PromptResponse execute(PromptOptions options, String... messages) throws PromptException {
        Message[] messagesArray = new Message[messages.length];
        for (int i = 0; i < messages.length; i++) {
            messagesArray[i] = new MessageImpl("user", messages[i]);
        }
        return execute(options, messagesArray);
    }

    @Override
    public PromptResponse execute(Message... messages) throws PromptException {
        return execute(new PromptOptionsImpl(), messages);
    }

    @Override
    public PromptResponse execute(PromptOptions options, Message... messages) throws PromptException {
        OllamaResult ollamaResult;
        OptionsBuilder builder = new OptionsBuilder();
        if(options instanceof PromptOptionsImpl){
            builder.setTemperature((float)((PromptOptionsImpl) options).getTemperature());
        }
        api.logging().logToOutput("Request: ------");
        api.logging().logToOutput(convertMessagesToString(messages));
        api.logging().logToOutput("------");
        List<OllamaChatMessage> chatMessages = convertMessages(messages);
        try {
            ollamaResult = AISpyImpl.ollamaAPI.chat(AISpyImpl.modelName, chatMessages);
        } catch (Throwable throwable) {
            return new PromptResponseImpl(throwable.toString());
        }
        String content = ollamaResult.getResponse();
        api.logging().logToOutput("Response: ------");
        api.logging().logToOutput(content);
        api.logging().logToOutput("------");
        int thinkEnd = content.indexOf("</think>");
        if(thinkEnd != -1){
            content = content.substring(thinkEnd+"</think>".length());
        }
        return new PromptResponseImpl(content.trim());
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

    private String convertMessagesToString(Message... messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Message message : messages) {
            if(message instanceof MessageImpl){
                MessageImpl messageImpl = (MessageImpl) message;
                stringBuilder.append(messageImpl.getRole()).append(": ").append(messageImpl.getContent()).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
