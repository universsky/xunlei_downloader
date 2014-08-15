package org.slackwareer.xunlei;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理类
 *
 * @author slackwareer
 * @date 2014-08-15
 */
public class TheadManager {
    private static final int                             maxThtead       = 1;//并发线程数
    private static       ExecutorService                 executorService = Executors.newFixedThreadPool(TheadManager.maxThtead);
    private static       HashMap<String, DownloadThread> threadMap       = new HashMap<String, DownloadThread>();

    public static void addThread(DownloadThread th) {
        TheadManager.executorService.submit(th);
        TheadManager.threadMap.put(th.getId(), th);
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

    //打印线程状态
    public static void printThreadStatus() {
        Iterator<String> it = TheadManager.threadMap.keySet().iterator();
        System.out.println(String.format("%s\t %s \t %s \t %s \t %s \t", "Id", "Name", "file size", "has download", "speed"));
        while (it.hasNext()) {
            String key = it.next();
            DownloadThread thread = TheadManager.threadMap.get(key);
            System.out.println(String.format("%s\t %s \t %d \t %d \t %f \t",
                    thread.getId(), thread.getName(), thread.getFileSize(),
                    thread.getHasDown(), thread.getSpeed()));
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
