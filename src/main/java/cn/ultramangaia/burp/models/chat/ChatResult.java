package cn.ultramangaia.burp.models.chat;

import com.alibaba.fastjson2.JSONObject;

public class ChatResult {
    public String think;
    public String content;

    public String engine;
    public JSONObject reqBody;
    public JSONObject resBody;

    public ChatResult() {
    }

    public ChatResult(String content) {
        this.content = content;
    }

    public ChatResult(String think, String content) {
        this.think = think;
        this.content = content;
    }
}
