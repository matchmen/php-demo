package com.bn.phpdemo;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.BASE64Decoder;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class PhpDemoApplicationTests {

    private static String URL = "https://b2b-uat.bnretail.cn/open-api/user/query";

    private static String CHARSET = "GB18030";//ISO-8859-1 GB18030 UTF-8 UTF-16 GBK
    @Test
    void contextLoads() {
    }


    @Test
    public void HttpURLConnectionTest1() throws Exception {

        URL url = new URL(URL);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod(RequestMethod.POST.name());

        connection.setRequestProperty("Content-type","application/json;charset="+CHARSET);

        InputStream inputStream = connection.getInputStream();

        byte[] b = new byte[1024];

        StringBuffer sb = new StringBuffer();

        int num;

        while ((num = inputStream.read(b)) != -1){
            sb.append(new String(b,0,num, CHARSET));
        }

        System.out.println(sb.toString());



    }

    @Test
    public void HttpClientPostTest1() throws Exception {

        //创建客户端
        CloseableHttpClient httpClient = HttpClients.custom()
                .evictExpiredConnections()
                .evictIdleConnections(30, TimeUnit.SECONDS)//退出空闲时间
                .setMaxConnTotal(10)//最大连接数（最大并发数）
                .setMaxConnPerRoute(5)//各大
                .build();

        System.out.println("请求地址：" + URL);
        // 创建Get请求
        HttpPost httpPost = new HttpPost(URL);
        //配置设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000) //服务器连接超时时间
                .setSocketTimeout(2000)  //服务器响应超时时间
                .build();
        httpPost.setConfig(requestConfig);
        //请求参数配置
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appKey","111"));
        params.add(new BasicNameValuePair("sign","111"));
        params.add(new BasicNameValuePair("version","111"));
        params.add(new BasicNameValuePair("format","111"));
        params.add(new BasicNameValuePair("timestamp","111"));
        params.add(new BasicNameValuePair("body","111"));
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(params,CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e);
        }
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // 响应实体(请求结束关闭)
        try(CloseableHttpResponse response = httpClient.execute(httpPost);) {
            // 获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态:" + response.getStatusLine());
            String responseContentStr = EntityUtils.toString(responseEntity,CHARSET);
            System.out.println("响应内容:"+responseContentStr);
            //BASE64解码
//            BASE64Decoder decoder = new BASE64Decoder();
//            System.out.println(new String(decoder.decodeBuffer(responseContentStr)));
        } catch (IOException e) {
            //do something
            e.printStackTrace();
        }finally {
            //释放连接
            httpPost.releaseConnection();
        }

    }

}
