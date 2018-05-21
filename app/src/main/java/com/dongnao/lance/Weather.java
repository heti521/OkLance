package com.dongnao.lance;


import com.dongnao.lance.api.annotation.BaseUrl;
import com.dongnao.lance.api.annotation.GET;
import com.dongnao.lance.api.annotation.POST;
import com.dongnao.lance.api.annotation.Parameter;
import com.dongnao.lance.api.annotation.ParameterMap;

import java.util.Map;

import okhttp3.Call;

/**
 * @author Lance
 * @date 2018/5/20
 */
@BaseUrl("http://restapi.amap.com/")
public interface Weather{


    @GET("/v3/weather/weatherInfo")
    Call get(@Parameter("city") String city, @Parameter("key") String key);

    @POST("/v3/weather/weatherInfo")
    Call post(@Parameter("city") String city, @Parameter("key") String key);


    @GET("/v3/weather/weatherInfo")
    Call get2(@ParameterMap Map<String, String> map);

    @POST("/v3/weather/weatherInfo")
    Call post2(@ParameterMap Map<String, String> map);


}