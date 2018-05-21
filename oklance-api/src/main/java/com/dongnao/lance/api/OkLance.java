package com.dongnao.lance.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author Lance
 * @date 2018/5/20
 */

public class OkLance {

    private static volatile OkLance instance;

    private OkHttpClient client;

    public static void init(OkHttpClient client) {
        if (null == instance) {
            synchronized (OkLance.class) {
                if (null == instance) {
                    instance = new OkLance(client);
                }
            }
        }
    }

    private OkLance(OkHttpClient client) {
        this.client = client;
    }

    public static OkLance getInstance() {

        return instance;
    }


    public Call get(HttpUrl httpUrl, Map<String, String> params) {
        StringBuffer p = new StringBuffer("?");
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            p.append(entry.getKey());
            p.append("=");
            p.append(entry.getValue());
            p.append("&");
        }
        p.deleteCharAt(p.length() - 1);

        httpUrl = httpUrl.newBuilder(p.toString()).build();
        Request request = new Request.Builder().url(httpUrl).get().build();
        Call call = client.newCall(request);
        return call;
    }


    public Call post(HttpUrl httpUrl, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(httpUrl).post(builder.build()).build();
        Call call = client.newCall(request);
        return call;
    }


    private Map<String, Object> serviceCache = new HashMap<>();
    private static final String IMPL = "Impl";

    public <T> T create(Class<T> service) {
        synchronized (this) {
            String serviceName = service.getName();
            if (!serviceName.endsWith(IMPL)) {
                serviceName += IMPL;
            }
            Object object = serviceCache.get(serviceName);
            if (null == object) {
                try {
                    object = Class.forName(serviceName).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            serviceCache.put(serviceName, object);
            return (T) object;
        }
    }

}
