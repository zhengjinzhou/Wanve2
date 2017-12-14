package com.zhou.wanve2.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhou on 2017/12/13.
 */

public class APIServer {
    private XGGServer server;

    private static APIServer apiServer;

    public static APIServer getInstence(){
        if (apiServer == null){
            synchronized ((APIServer.class)){
                if (apiServer == null){
                    apiServer = new APIServer();
                }
            }
        }
        return apiServer;
    }

    public XGGServer getServer(){
        if (server == null){
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            //client.addInterceptor(new BaseInterceptor());
            client.readTimeout(1, TimeUnit.MINUTES).connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(1,TimeUnit.MINUTES);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(XGGServer.BASE_URL)
                    .client(client.build())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            server = retrofit.create(XGGServer.class);
        }
        return server;
    }
}
