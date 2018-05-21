package com.dongnao.lance;


import com.dongnao.lance.api.OkLance;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public interface RetrofitWeather {

        @retrofit2.http.GET("/v3/weather/weatherInfo")
        retrofit2.Call<ResponseBody> get(@Query("city") String city, @Query("key") String key);

        @FormUrlEncoded
        @retrofit2.http.POST("/v3/weather/weatherInfo")
        retrofit2.Call<ResponseBody> post(@Field("city") String city, @Field("key") String key);
    }

    public class RetrofitWeatherA implements RetrofitWeather {


        @Override
        public Call<ResponseBody> get(String city, String key) {
            return null;
        }

        @Override
        public Call<ResponseBody> post(String city, String key) {
            return null;
        }
    }

    @Test
    public void proxy() throws Exception {
        RetrofitWeatherA a = new RetrofitWeatherA();
        RetrofitWeather weather = (RetrofitWeather) Proxy.newProxyInstance(getClass()
                        .getClassLoader(), new
                        Class[]{RetrofitWeather.class},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws
                            Throwable {
//                        GET get = method.getAnnotation(GET.class);
//                        String value = get.value();
//                        System.out.println("请求:" + value);
//                        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
//                        for (int i = 0; i < parameterAnnotations.length; i++) {
//                            Query query = (Query) parameterAnnotations[i][0];
//                            System.out.println("参数:" + query.value() + "=" + args[i]);
//                        }
//                        System.out.println(proxy);
                        System.out.println("====");
                        return null;
                    }
                });
        weather.get("1", "2");
        Map<String,Object> map = new HashMap<>();
        map.put("1",weather);


    }

    @Test
    public void testRetrofit() throws Exception {
        ConnectionPool connectionPool = new ConnectionPool();
        OkHttpClient httpClient = new OkHttpClient.Builder().connectionPool(connectionPool).build();
        httpClient.newCall(new Request.Builder().url("http://restapi.amap.com/").build()).execute();

        long start = System.currentTimeMillis();
        Retrofit retrofit = new Retrofit.Builder().client(httpClient).baseUrl("http://restapi" +
                ".amap.com/").build();

        RetrofitWeather weather = retrofit.create(RetrofitWeather.class);

        retrofit2.Response<ResponseBody> get = weather.get("长沙",
                "13cb58f5884f9749287abbead9c658f2").execute();
        System.out.println(get.body().string());
        get.body().close();

        retrofit2.Response<ResponseBody> post = weather.post("长沙",
                "13cb58f5884f9749287abbead9c658f2")
                .execute();
        System.out.println(post.body().string());
        post.body().close();

        System.out.println(System.currentTimeMillis() - start);
    }


    @Test
    public void testOkLance() throws Exception {
        ConnectionPool connectionPool = new ConnectionPool();
        OkHttpClient httpClient = new OkHttpClient.Builder().connectionPool(connectionPool).build();
        httpClient.newCall(new Request.Builder().url("http://restapi.amap.com/").build()).execute();

        long start = System.currentTimeMillis();
//        OkLance.init(new OkHttpClient());
        OkLance.init(httpClient);

        Weather weather = OkLance.getInstance().create(Weather.class);
        Response get = weather.get("长沙", "13cb58f5884f9749287abbead9c658f2").execute();
        System.out.println(get.body().string());
        get.close();


        Response post = weather.post("长沙", "13cb58f5884f9749287abbead9c658f2").execute();
        System.out.println(post.body().string());
        post.close();

        System.out.println(System.currentTimeMillis() - start);

//        WeatherImpl weatherImpl = new WeatherImpl();

//        Map<String, String> params = new HashMap<>();
//        params.put("city", "长沙");
//        params.put("key", "13cb58f5884f9749287abbead9c658f2");
//        Response get2 = weather.get2(params).execute();
//        System.out.println(get2.body().string());
//        get2.close();
//
//        Response post2 = weather.post2(params).execute();
//        System.out.println(post2.body().string());
//        post2.close();

    }
}