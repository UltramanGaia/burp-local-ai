package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.*;

import static burp.BurpLocalAIExtension.api;

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
//        OptionsBuilder builder = new OptionsBuilder();
//        if(options instanceof PromptOptionsImpl){
//            builder.setTemperature((float)((PromptOptionsImpl) options).getTemperature());
//        }
        String requestStr = convertMessagesToString(messages).trim();
        api.logging().logToOutput("Request: ------");
        api.logging().logToOutput(requestStr);
        api.logging().logToOutput("------");

        String responseStr = AISpyImpl.getInstance().chat(messages);
        api.logging().logToOutput("Response: ------");
        api.logging().logToOutput(responseStr);
        api.logging().logToOutput("------");
        int thinkEnd = responseStr.indexOf("</think>");
        if(thinkEnd != -1){
            responseStr = responseStr.substring(thinkEnd+"</think>".length());
        }
        return new PromptResponseImpl(responseStr.trim());
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
