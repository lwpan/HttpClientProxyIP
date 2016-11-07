package com.httpclientproxy.mycode;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tianjinjin on 2016/11/3.
 */
public class ConnectiontionPoolTest {

    public  static  void  main(String[]  args) throws InterruptedException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

        cm.setMaxTotal(25);//设置最大连接数200
        cm.setDefaultMaxPerRoute(30);//设置每个路由默认连接数
        HttpHost host = new HttpHost("http://blog.csdn.net/gaolu");//针对的主机
        HttpRoute httpRoute = new HttpRoute(host);

        cm.setMaxPerRoute(httpRoute, 30);//每个路由器对每个服务器允许最大30个并发访问




        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        // URIs to perform GETs on
        /*String[] urisToGet = {
                "http://www.baidu.com/",
                "http://www.sina.com/",
                "http://www.qq.com/",
                "http://www.3dmgame.com/"
        };*/

        String[]  urisToGet  =  {
                "http://blog.csdn.net/gaolu/article/details/48466059",
                "http://blog.csdn.net/gaolu/article/details/48243103",
                "http://blog.csdn.net/gaolu/article/details/47656987",
                "http://blog.csdn.net/gaolu/article/details/47055029",

                "http://blog.csdn.net/gaolu/article/details/46400883",
                "http://blog.csdn.net/gaolu/article/details/46359127",
                "http://blog.csdn.net/gaolu/article/details/46224821",
                "http://blog.csdn.net/gaolu/article/details/45305769",

                "http://blog.csdn.net/gaolu/article/details/43701763",
                "http://blog.csdn.net/gaolu/article/details/43195449",
                "http://blog.csdn.net/gaolu/article/details/42915521",
                "http://blog.csdn.net/gaolu/article/details/41802319",

                "http://blog.csdn.net/gaolu/article/details/41045233",
                "http://blog.csdn.net/gaolu/article/details/40395425",
                "http://blog.csdn.net/gaolu/article/details/40047065",
                "http://blog.csdn.net/gaolu/article/details/39891877",

                "http://blog.csdn.net/gaolu/article/details/39499073",
                "http://blog.csdn.net/gaolu/article/details/39314327",
                "http://blog.csdn.net/gaolu/article/details/38820809",
                "http://blog.csdn.net/gaolu/article/details/38439375",
        };

        // create a thread for each URI
        GetThread[] threads = new GetThread[urisToGet.length];



        for (int i = 0; i < threads.length; i++) {
            HttpGet httpget = new HttpGet(urisToGet[i]);
            threads[i] = new GetThread(httpClient, httpget, cm, httpRoute);
        }

        // start the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
            System.out.println("<——第" + j + "个线程启动——>");
            Set<HttpRoute> httpRoutes=cm.getRoutes();
            for(HttpRoute route : httpRoutes){
                PoolStats poolStats = cm.getStats(route);
                System.out.println(poolStats.toString());
                System.out.println("1.租用数" + poolStats.getLeased()+"2.等待数" + poolStats.getPending()+"3.可用连接数" + poolStats.getAvailable()+"4.最大连接数" + poolStats.getMax());
        }

        }



        // join the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].join();
        }
    }


    static class GetThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;


        private final PoolingHttpClientConnectionManager cm;
        private final HttpRoute httpRoute;
        public GetThread(CloseableHttpClient httpClient, HttpGet httpget ,PoolingHttpClientConnectionManager cm,HttpRoute httpRoute) {
            this.httpClient = httpClient;
            this.context = HttpClientContext.create();
            this.httpget = httpget;
            this.cm = cm;
            this.httpRoute= httpRoute;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + "线程被调用了。");
                CloseableHttpResponse response = httpClient.execute(
                        httpget, context);

                Thread.sleep(2 * 1000);


                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());

                    if(entity!=null){
                        System.out.println("test");
                    }
                    //System.out.println(EntityUtils.toString(entity, "utf-8"))  ;
                } finally {
                    response.close();
                }
            } catch (ClientProtocolException ex) {
                // Handle protocol errors
            } catch (IOException ex) {
                // Handle I/O errors
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
