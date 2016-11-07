package com.worker.mytest;



import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Tianjinjin on 2016/11/7.
 */
public class DownloaderRunnable implements Runnable{

    /**
     *倒计时计数器
     *
     */
    private CountDownLatch countDownLatch;

    private ProxyDownloader proxyDownloader;

    private Request request;

    private Task task;


    public DownloaderRunnable(ProxyDownloader pageDownloader, CountDownLatch countDownLatch, Request request, Task task){
        this.countDownLatch = countDownLatch;
        this.proxyDownloader = pageDownloader;
        this.request=request;
        this.task=task;
    }

    public void run() {
        proxyDownloader.download(request, task);
    }
}
