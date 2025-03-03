package cn.ultramangaia.burp.models.chat;

public class ChatResult {
    public String think;
    public String content;

    public ChatResult(String content) {
        this.content = content;
    }

    public ChatResult(String think, String content) {
        this.think = think;
        this.content = content;
    }
}
