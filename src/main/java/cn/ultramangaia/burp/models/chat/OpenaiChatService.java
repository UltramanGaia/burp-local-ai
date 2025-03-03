package cn.ultramangaia.burp.models.chat;

import cn.ultramangaia.burp.models.request.HttpTool;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public class OpenaiChatService implements ChatService{
    private String host;
    private String url;
    private String model;
    private String apiKey;

    public OpenaiChatService(String host, String url, String model, String apiKey) {
        this.host = host;
        this.url = url;
        this.model = model;
        this.apiKey = apiKey;
    }
    @Override
    public ChatResult chat(List<ChatMessage> messages) {
        JSONObject reqBody = new JSONObject();
        reqBody.put("messages", messages);
        reqBody.put("model", model);
        reqBody.put("stream", false);

        HttpTool httpTool = new HttpTool();
        JSONObject response = httpTool.post(host+url, reqBody);
        if(response.getString("error")!=null){
            return new ChatResult(response.getString("error"));
        }else{
            return new ChatResult(response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"));
        }
    }
}
