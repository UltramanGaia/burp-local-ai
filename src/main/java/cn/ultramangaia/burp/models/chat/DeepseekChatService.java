package cn.ultramangaia.burp.models.chat;

import cn.ultramangaia.burp.models.request.HttpTool;
import com.alibaba.fastjson2.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepseekChatService implements ChatService{
    private String host;
    private String url;
    private String model;
    private String apiKey;

    public DeepseekChatService(String host, String url, String model, String apiKey) {
        this.host = host;
        this.url = url;
        this.model = model;
        this.apiKey = apiKey;
    }
    @Override
    public ChatResult chat(List<ChatMessage> messages) {
        Map<String, String> headers = new HashMap<>();
        if(!apiKey.isEmpty()){
            headers.put("Authorization", "Bearer " + apiKey);
        }
        JSONObject reqBody = new JSONObject();
        reqBody.put("messages", messages);
        reqBody.put("model", model);
        reqBody.put("stream", false);

        HttpTool httpTool = new HttpTool();
        JSONObject response = httpTool.post(host+url, headers, reqBody);
        if(response.getString("error")!=null){
            return new ChatResult(response.getString("error"));
        }else{
            return new ChatResult(response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"));
        }
    }
}
