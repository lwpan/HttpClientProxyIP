package com.worker.mytest;


import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tianjinjin on 2016/11/7.
 */
public class ThreadPool {

    int defaultWorkNum=10;
    ExecutorService executors ;
    CountDownLatch countDownLatch;

    /**
     *
     * @param proxyDownloader 实现ip代理的下载器
     * @param request
     * @param task
     */
    public void startPool(ProxyDownloader proxyDownloader,Request request, Task task){
        executors =  Executors.newFixedThreadPool(defaultWorkNum);
        countDownLatch  =  new  CountDownLatch(defaultWorkNum);
        executors.submit(new DownloaderRunnable(proxyDownloader,countDownLatch,request,task));
    }


    /**
     *
     * @param maxPoolNum httpClient连接池最大连接数
     * @param defaultRouteNum 默认路由的连接数，即连接每个网站的最大数量。
     * @return
     */
    public static PoolingHttpClientConnectionManager getHttpConnectionPool(int maxPoolNum,int defaultRouteNum){
        ConnectionSocketFactory plainsf  =  PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf  =  SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry  =  RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http",  plainsf)
                .register("https",  sslsf)
                .build();
        PoolingHttpClientConnectionManager cm  =  new  PoolingHttpClientConnectionManager(registry);

        cm.setMaxTotal(maxPoolNum);

        cm.setDefaultMaxPerRoute(defaultRouteNum);

        return cm;
    }
}
