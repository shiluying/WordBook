package com.shiluying.wordbook.Server;


import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranslateServer {
    private static final String YOUDAO_URL = "https://openapi.youdao.com/api";
    private static final String APP_KEY = "57b4b51e07c9fa46";
    private static final String APP_SECRET = "qaPvB3c3YaPuBq2SAQwKaHClPVRcgZdg";
    public String getURL(String word){
        Map<String,String> params = new HashMap<String,String>();
        String q = word;
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", "en");
        params.put("to", "zh-CHS");
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = APP_KEY + truncate(q) + salt + curtime + APP_SECRET;
        String sign = getDigest(signStr);
        params.put("appKey", APP_KEY);
        params.put("salt", salt);
        params.put("sign", sign);
        /** 处理结果 */
        Set<String> param = params.keySet();
        Iterator<String> iterator = param.iterator();
        String url = YOUDAO_URL+"?q="+q;
        while (iterator.hasNext()){
            String next = iterator.next();
            url=url+"&"+next+"="+params.get(next);
        }
        Log.i("TREANSLATE",url);
        return url;
    }
    public Map getData(String data) throws IOException {
        Log.i("GETDATA",data);
        Map<String, Object> dataMap = new HashMap<>(16);
        Map<String, Object> map = new HashMap<>(16);
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(data, map.getClass());
        String phonetic="",explains="",basic="";
        basic=map.get("basic").toString();
        Log.i("BASIC",basic);
        Map basicMap=(Map<String, Object>)map.get("basic");
        try{
            phonetic = basicMap.get("phonetic").toString();
        }catch (Exception e){
            phonetic="";
        }

        explains = basicMap.get("explains").toString();
        dataMap.put("errorCode",map.get("errorCode").toString());
        dataMap.put("phonetic",phonetic);
        dataMap.put("explains",explains);
        dataMap.put("word",map.get("query").toString());
        return dataMap;
    }
//    public static void requestForHttp(String url,Map<String,String> params) throws IOException {
//
//        /** 创建HttpClient */
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//
//        /** httpPost */
//        HttpPost httpPost = new HttpPost(url);
//        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
//        Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry<String,String> en = it.next();
//            String key = en.getKey();
//            String value = en.getValue();
//            paramsList.add(new BasicNameValuePair(key,value));
//        }
//        httpPost.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
//        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
//        try{
//            Header[] contentType = httpResponse.getHeaders("Content-Type");
//            logger.info("Content-Type:" + contentType[0].getValue());
//            if("audio/mp3".equals(contentType[0].getValue())){
//                //如果响应是wav
//                HttpEntity httpEntity = httpResponse.getEntity();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                httpResponse.getEntity().writeTo(baos);
//                byte[] result = baos.toByteArray();
//                EntityUtils.consume(httpEntity);
//                if(result != null){//合成成功
//                    String file = "合成的音频存储路径"+System.currentTimeMillis() + ".mp3";
//                    byte2File(result,file);
//                }
//            }else{
//                /** 响应不是音频流，直接显示结果 */
//                HttpEntity httpEntity = httpResponse.getEntity();
//                String json = EntityUtils.toString(httpEntity,"UTF-8");
//                EntityUtils.consume(httpEntity);
//                System.out.println(json);
//            }
//        }finally {
//            try{
//                if(httpResponse!=null){
//                    httpResponse.close();
//                }
//            }catch(IOException e){
//            }
//        }
//    }



    public static String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        MessageDigest mdInst = null;
        try {
            mdInst = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
    public static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}