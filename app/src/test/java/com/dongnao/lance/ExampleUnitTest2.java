package com.dongnao.lance;


import com.dongnao.lance.api.OkLance;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest2 {

    public interface RetrofitWeather {

        @GET("/v3/weather/weatherInfo")
        Call<ResponseBody> get(@Query("city") String city, @Query("key") String key);

        @FormUrlEncoded
        @retrofit2.http.POST("/v3/weather/weatherInfo")
        Call<ResponseBody> post(@Field("city") String city, @Field("key") String key);
    }

    public class RetrofitWeatherImpl implements RetrofitWeather {


        @Override
        public Call<ResponseBody> get(String city, String key) {
            return null;
        }

        @Override
        public Call<ResponseBody> post(String city, String key) {
            return null;
        }

        @Override
        public String toString() {
            return "111222";
        }
    }

    class MyInvocationHandler implements InvocationHandler {

//        private final RetrofitWeatherImpl impl;
//
//        public MyInvocationHandler(RetrofitWeatherImpl impl) {
//            this.impl = impl;
//        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(impl,args);
        }
    }
    RetrofitWeatherImpl impl = new RetrofitWeatherImpl();
    @Test
    public void proxy() throws Exception {

        RetrofitWeather weather = (RetrofitWeather) Proxy.newProxyInstance(getClass()
                        .getClassLoader(), new
                        Class[]{RetrofitWeather.class},
                new MyInvocationHandler());
        Map<String, Object> map = new HashMap<>();
        map.put("1", weather);


    }

}