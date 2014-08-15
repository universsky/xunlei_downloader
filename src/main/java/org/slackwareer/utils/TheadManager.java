package org.slackwareer.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理
 */
public class TheadManager {
    private static final int                             maxThtead       = 1;//并发线程数
    private static       ExecutorService                 executorService = Executors.newFixedThreadPool(TheadManager.maxThtead);
    private static       HashMap<String, DownloadThread> threadMap       = new HashMap<String, DownloadThread>();

    public static void addThread(DownloadThread th) {
        TheadManager.executorService.submit(th);
        TheadManager.threadMap.put(th.getId(), th);
    }

    //停止线程添加,并等待线程结束
    public static void shutdown() {
        TheadManager.executorService.shutdown();
    }

    public static HashMap<String, DownloadThread> getList() {
        return TheadManager.threadMap;
    }

    //清理已完成线程
    public static boolean clean() {
        boolean out = false;
        Iterator<String> it = TheadManager.threadMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            DownloadThread next = TheadManager.threadMap.get(key);
            if (next.isDone()) {
                it.remove();
                out = true;
            }
        }
        return out;
    }

    //获取线程大小
    public static int size() {
        TheadManager.clean();
        return TheadManager.threadMap.size();
    }

    //打印线程状态
    public static void printThreadStatus() {
        Iterator<String> it = TheadManager.threadMap.keySet().iterator();
        System.out.println(String.format("%s\t %s \t %s \t %s \t %s \t", "Id", "Name", "file size", "has download", "speed"));
        while (it.hasNext()) {
            String key = it.next();
            DownloadThread next = TheadManager.threadMap.get(key);
            System.out.println(String.format("%s\t %s \t %d \t %d \t %f \t",
                    next.getId(), next.getName(), next.getFileSize(),
                    next.getHasDown(), next.getSpeed()));
        }
        System.out.println();
    }

    //保存线程状态
    public static void save() {
        Iterator<String> it = TheadManager.threadMap.keySet().iterator();
        Account at = Account.getInstance();
        while (it.hasNext()) {
            String key = it.next();
            if (at.getList().get(key).getStatus() == Item.STATUS_DOWNLOADING) {
                TheadManager.threadMap.get(key).save();
            }
        }
    }
}
