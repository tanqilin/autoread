package com.example.utils;
import android.util.Log;

import okhttp3.*;
import okhttp3.Response;

import java.io.IOException;

public class HttpUtils {
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    public static String httpGetAppInfoUrl = "http://39.104.203.40/Api/AppInfo";
    public static String httpRegUrl = "http://39.104.203.40/Api/AppApi/register";
    public static String httpUpdateUrl = "http://39.104.203.40/Api/AppApi/RegUpdate";
    public static String httpAnswerUrl = "http://39.104.203.40/Api/Answer/PostSubject";

    public static void post(String url, String json,Callback callback) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType=MediaType.Companion.parse("application/json;charset=utf-8");
                RequestBody stringBody=RequestBody.Companion.create(json,mediaType);

                Request request = new Request.Builder()
                        .url(url)
                        .post(stringBody)
                        .build();

                client.newCall(request).enqueue(callback);
            }
        }).start();
    }

    public static String get(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1:学什么都能new出一个对象来
                    OkHttpClient okHttpClient = new OkHttpClient();
                    //Request就是请求的类
                    Request request = new Request.Builder().url(url).build();
                    //发送请求newCall方法
                    Call call = okHttpClient.newCall(request);
                    //通过call去处理给你响应Response
                    Response response = call.execute();
                    //从相应体里面拿到数据
                    String string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }
}
