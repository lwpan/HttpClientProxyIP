package com.httpclientproxy.mycode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Tianjinjin on 2016/11/2.
 */
public class GetRunnable implements  Runnable {
    private CountDownLatch countDownLatch;
    private  final CloseableHttpClient httpClient;
    private  final HttpGet httpget;

    private PoolingHttpClientConnectionManager cm;
    String url;

    public  GetRunnable(CloseableHttpClient  httpClient,  HttpGet  httpget,  String url, PoolingHttpClientConnectionManager cm,CountDownLatch  countDownLatch){
        this.httpClient  =  httpClient;
        this.httpget  =  httpget;
        this.countDownLatch  =  countDownLatch;

        this.cm=cm;
        this.url=url;
    }
    public void run() {
        CloseableHttpResponse response  =  null;
        try  {
            System.out.println(Thread.currentThread().getName() + "线程被调用了。");
            HttpHost localhost  =  new  HttpHost(url,80);
            HttpRoute httpRoute = new HttpRoute(localhost);
            PoolStats poolStats = cm.getStats(httpRoute);
            System.out.println(poolStats.getAvailable());


            response  =  httpClient.execute(httpget, HttpClientContext.create());
            HttpEntity entity  =  response.getEntity();
            System.out.println(EntityUtils.toString(entity, "utf-8"))  ;
            EntityUtils.consume(entity);
        }  catch  (IOException e)  {
            e.printStackTrace();
        }  finally  {
            httpget.releaseConnection();
            countDownLatch.countDown();
            try  {
                if(response  !=  null)
                    response.close();

            }  catch  (IOException  e)  {
                e.printStackTrace();
            }
        }
    }
}
