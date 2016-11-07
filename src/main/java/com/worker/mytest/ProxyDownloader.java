package com.worker.mytest;


import com.httpclientproxy.mycode.ProxyIp;
import com.mapper.ProxyIpMapper;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Tianjinjin on 2016/11/4.
 */
@Component
public class ProxyDownloader implements Downloader{

    HttpHost proxyHttpPost;

    @Autowired
    private ProxyIpMapper proxyIpMapper;

    public Page download(Request request, Task task) {
        PoolingHttpClientConnectionManager cm = ThreadPool.getHttpConnectionPool(200,15);
        CloseableHttpClient client= HttpClients.custom().setConnectionManager(cm).build();
        HttpGet get=new HttpGet(request.getUrl());
        HttpResponse response= null;
        try {
            response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            while(status==403){
                resetProxy(get);
                response = client.execute(get);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String content= null;
        try {
            content = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Page page=new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }




    public void setThread(int threadNum) {

    }


    /**
     * 设置代理
     * @param httpRequestBase
     */
    public void resetProxy(HttpRequestBase httpRequestBase){

        proxyHttpPost= getHttpPost();
        RequestConfig requestConfig  =  RequestConfig.custom()
                .setConnectionRequestTimeout(100000)
                .setConnectTimeout(100000)
                .setSocketTimeout(100000)
                .setProxy(proxyHttpPost)
                .build();

        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取一个HttpPost对象，用于设置代理
     * 先从数据库得到一组代理ip对象，然后用一个范围在[0,list.size()]的随机数i，用于
     * 从list里面取出一个ip对象，进行代理，每次取出一个对象，就出list中将其移除
     */
    public HttpHost getHttpPost(){
        List<ProxyIp> proxyIpList =proxyIpMapper.getProxyIpList();

        int i = Math.abs(new Random().nextInt())%proxyIpList.size();
        proxyIpList.remove(i);
        ProxyIp proxyIp = proxyIpList.get(i);
        HttpHost proxyHttpPost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
        return proxyHttpPost;
    }
}
