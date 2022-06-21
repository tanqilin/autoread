package com.example.models;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 题目信息
 */
public class SubjectInfo {
    public String Subject;     // 题干
    public String Answer;      // 待选答案
    public String Type;        // 题目类型 单选/多选/填空
    public String Correct;     // 正确答案

    public String getAnswer() {
        return Answer;
    }

    public String getSubject() {
        return Subject;
    }

    public String getCorrect() {
        if(Correct == null) return "数据库还没有此题目的答案！";
        return Correct;
    }

    public String getType() {
        return Type;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public void setType(String type) {
        Type = type;
    }

    /**
     * 发送用户信息到服务器
     */
    public void postSubjectInfo(Callback callback){
        try{
            String jsonString = new Gson().toJson(this);
            HttpUtils.post(HttpUtils.httpAnswerUrl, jsonString,callback);
        }catch (IOException err){
            err.printStackTrace();
        }
    }
}
