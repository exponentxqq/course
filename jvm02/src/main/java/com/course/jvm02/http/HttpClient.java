package com.course.jvm02.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpClient {
  private static final String NETTY_SERVER_URL = "http://127.0.0.1:8020/test";

  public static void main(String[] args) throws IOException {
    requestByOkHttp();
    requestByHttpClient();
  }

  private static void requestByOkHttp() throws IOException {
    final OkHttpClient client =
        new Builder().callTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
    final Request reqeust = new Request.Builder().url(NETTY_SERVER_URL).get().build();
    final Response response = client.newCall(reqeust).execute();
    System.out.println(response.body().string());
  }

  private static void requestByHttpClient() throws IOException {
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet httpGet = new HttpGet(NETTY_SERVER_URL);
    final CloseableHttpResponse response = httpClient.execute(httpGet);
    System.out.println(EntityUtils.toString(response.getEntity()));
  }
}
