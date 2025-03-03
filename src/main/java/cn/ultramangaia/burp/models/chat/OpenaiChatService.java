package cn.ultramangaia.burp.models.chat;

import cn.ultramangaia.burp.models.request.HttpTool;
import cn.ultramangaia.burp.util.Globals;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return chat(reqBody);
    }

    @Override
    public ChatResult chat(JSONObject body) {
        ChatResult result = new ChatResult();
        result.engine = Globals.OPENAI;
        result.reqBody = body;
        Map<String, String> headers = new HashMap<>();
        if(!apiKey.isEmpty()){
            headers.put("Authorization", "Bearer " + apiKey);
        }
        HttpTool httpTool = new HttpTool();
        JSONObject response = httpTool.post(host+url, headers, body);
        result.resBody = response;
        if(response.getString("error")!=null){
            result.content = response.getString("error");
        }else{
            result.content = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        }
        return result;
    }
}
