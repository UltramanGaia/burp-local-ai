package cn.ultramangaia.burp.models.request;

import cn.ultramangaia.burp.gui.MainForm;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpTool {
    public JSONObject post(String url, Map<String, String> headers, JSONObject body) {
        String originJsonBody = body.toJSONString();
        String modifiedJsonBody = MainForm.getInstance().modifyRequestBody(originJsonBody);

        JSONObject responseBodyJson = null;
        URI uri = URI.create(url);
        // Create Request
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        builder.header("Content-Type", "application/json");
        for(Map.Entry<String, String> entry : headers.entrySet()){
            builder.header(entry.getKey(), entry.getValue());
        }
        builder.timeout(Duration.ofSeconds(300));
        builder.POST(HttpRequest.BodyPublishers.ofString(modifiedJsonBody));
        HttpRequest request = builder.build();
        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int statusCode = response.statusCode();
            if(statusCode == 200){
                InputStream responseBodyStream = response.body();
                responseBodyJson = JSON.parseObject(responseBodyStream);
            }
        }catch(Exception e){
            e.printStackTrace();
            responseBodyJson = new JSONObject();
            responseBodyJson.put("error", e.getMessage());
        }
        return responseBodyJson;
    }
}


