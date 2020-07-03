package forward.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class LqHttpUtil {

    public static String Post(String url, Map<String, String> headers, Map<String, Object> params) throws Exception{

        JSONObject jsonObject = JSONUtil.parseObj(params);
        return HttpRequest.post(url).addHeaders(headers).body(jsonObject).timeout(5000).execute().body();

    }

    public static Map<String, String> HttpHeaders(){
        Map<String, String> headers = new HashMap<String, String>();
        String contentType = PropertyUtil.getProperty("Content-Type");
        String source = PropertyUtil.getProperty("source");
        String token = PropertyUtil.getProperty("token");
        headers.put("Content-Type", contentType);
        headers.put("source", source);
        headers.put("token", token);
        return headers;
    }
}
