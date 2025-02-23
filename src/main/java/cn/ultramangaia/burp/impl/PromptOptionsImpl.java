package cn.ultramangaia.burp.impl;

import burp.api.montoya.ai.chat.PromptOptions;

public class PromptOptionsImpl implements PromptOptions {
    private double temperature = 0.7;
    @Override
    public PromptOptions withTemperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    public double getTemperature() {
        return temperature;
    }
}
