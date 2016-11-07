package com.httpclientproxy.mycode;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tianjinjin on 2016/11/2.
 */
public class HttpConnectionPool {
    private  static  void  config(HttpRequestBase httpRequestBase)  {
        httpRequestBase.setHeader("User-Agent",  "Mozilla/5.0");
        httpRequestBase.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Language",  "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");//"en-US,en;q=0.5");
        httpRequestBase.setHeader("Accept-Charset", "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        //  配置请求的超时设置
        RequestConfig requestConfig  =  RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }

    public  static  void  main(String[]  args)  {
        ConnectionSocketFactory plainsf  =  PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf  =  SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry  =  RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http",  plainsf)
                .register("https",  sslsf)
                .build();
        PoolingHttpClientConnectionManager cm  =  new  PoolingHttpClientConnectionManager(registry);




        CloseableHttpClient httpClient  =  HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        //HttpHost localhost  =  new  HttpHost("http://blog.csdn.net/gaolu",80);

        //  URL列表数组
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

        List<ProxyIp> list = new ArrayList();
        list.add(new ProxyIp("123.234.143.121",81));
        list.add(new ProxyIp("122.72.32.73",80));
        list.add(new ProxyIp("122.96.59.107", 843));
        list.add(new ProxyIp("122.96.59.98", 80));
        list.add(new ProxyIp("218.25.13.23", 80));
        list.add(new ProxyIp("119.53.131.6", 8118));
        list.add(new ProxyIp("122.96.59.99", 80));
        list.add(new ProxyIp("112.195.69.157", 8118));
        list.add(new ProxyIp("202.171.253.72", 80));
        list.add(new ProxyIp("175.42.45.167", 8888));
        list.add(new ProxyIp("218.106.205.145", 8080));
        list.add(new ProxyIp("175.17.225.86", 8888));
        list.add(new ProxyIp("183.129.178.14", 8080));
        list.add(new ProxyIp("122.228.179.178", 80));
        list.add(new ProxyIp("117.28.255.84", 80));
        list.add(new ProxyIp("120.76.243.40", 80));
        list.add(new ProxyIp("122.5.233.250", 8888));
        list.add(new ProxyIp("124.88.67.20", 80));
        list.add(new ProxyIp("106.75.176.4", 80));
        list.add(new ProxyIp("123.56.74.13", 8080));

        long  start  =  System.currentTimeMillis();

        try  {
            int  pagecount  =  list.size();
            ExecutorService executors  =  Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch  =  new  CountDownLatch(pagecount);
            for(int  i  =  0;  i<  pagecount;i++){
                HttpGet httpget  =  new  HttpGet(urisToGet[i]);
               //HttpHost proxy = new HttpHost(list.get(i).getIp(), list.get(i).getPort());
                config(httpget);
                //启动线程抓取
                executors.submit(new GetRunnable(httpClient, httpget,urisToGet[i],cm,countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        }  catch  (InterruptedException  e)  {
            e.printStackTrace();
        }  finally  {
            System.out.println("线程"  +  Thread.currentThread().getName()  +  ","  +  System.currentTimeMillis()  +  ",  所有线程已完成，开始进入下一步！");
        }

        long  end  =  System.currentTimeMillis();
        System.out.println("consume  ->  "  +  (end  -  start));
    }
}
