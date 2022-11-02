package cn.itcast.baoxiu.util;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.itcast.baoxiu.entity.Admin;
import cn.itcast.baoxiu.entity.Repair;
import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MyRequest {
    public static String getString(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUser(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONObject.parseObject(response.body().string(),User.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getUsers(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONArray.parseArray(response.body().string(),User.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Admin getAdmin(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONObject.parseObject(response.body().string(), Admin.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Admin> getAdmins(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONArray.parseArray(response.body().string(),Admin.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Support getSupport(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONObject.parseObject(response.body().string(),Support.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Support> getSupports(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONArray.parseArray(response.body().string(),Support.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Repair getRepair(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONObject.parseObject(response.body().string(),Repair.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Repair> getRepairs(String url){
        try {
            //创建http客户端
            OkHttpClient client = new OkHttpClient();
            //创建http请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            //执行发送的指令
            Response response = client.newCall(request).execute();
            //获取后端数据
            assert response.body() != null;
            return JSONArray.parseArray(response.body().string(),Repair.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url,JSONObject json){
        try {
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .post(requestBody)
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
