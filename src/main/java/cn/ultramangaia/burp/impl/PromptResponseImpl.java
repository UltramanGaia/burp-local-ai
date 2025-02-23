package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.PromptResponse;

public class PromptResponseImpl implements PromptResponse {
    private String content;

    public PromptResponseImpl(String content) {
        this.content = content;
    }

    @Override
    public String content() {
        return this.content;
    }
}
