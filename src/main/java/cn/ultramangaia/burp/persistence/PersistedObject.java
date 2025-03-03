package cn.ultramangaia.burp.persistence;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.File;
import java.nio.file.Files;

public class PersistedObject {

    private static PersistedObject instance;

    private JSONObject cfg;

    private PersistedObject() {
        this.cfg = new JSONObject();
        load();
    }

    public void load(){
        try{
            String home = System.getProperty("user.home");
            if(home != null){
                String path = home + "/.burp-local-ai/cfg.json";
                File file = new File(path);
                if(file.exists()){
                    this.cfg = JSONObject.parseObject(Files.readString(file.toPath()));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void save(){
        try{
            String home = System.getProperty("user.home");
            if(home != null){
                String path = home + "/.burp-local-ai/cfg.json";
                File file = new File(path);
                if(!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                Files.writeString(file.toPath(), this.cfg.toJSONString(JSONWriter.Feature.PrettyFormat));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static PersistedObject getInstance() {
        if (instance == null) {
            instance = new PersistedObject();
        }
        return instance;
    }


    public Boolean getBoolean(String key) {
        return cfg.getBoolean(key);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean result = cfg.getBoolean(key);
        return result == null ? defaultValue : result;
    }

    public String getString(String key) {
        return cfg.getString(key);
    }

    public String getString(String key, String defaultValue) {
        String result = cfg.getString(key);
        return result == null ? defaultValue : result;
    }

    public String getOrSetString(String key, String defaultValue) {
        String result = cfg.getString(key);
        if(result == null){
            cfg.put(key, defaultValue);
            return defaultValue;
        }
        return result;
    }

    public void setString(String key, String value) {
        cfg.put(key, value);
    }

    public void setBoolean(String key, Boolean value) {
        cfg.put(key, value);
    }
}
