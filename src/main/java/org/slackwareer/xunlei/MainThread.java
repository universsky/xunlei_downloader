package org.slackwareer.xunlei;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 */
public class MainThread extends XunleiThread {
    private static final int time  = 10000;
    private              int times = 0;

    public void runing() {
        while (true) {
            Account account = Account.getInstance();
            if (this.times++ > 6) {//每六倍的刷新时间,刷新一下迅雷离线下载列表,并保存程序状态
                account.refresh();
                TheadManager.save();
                this.times = 0;
            }
            HashMap<String, Item> atlist = account.getList();
            Iterator<String> it = atlist.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Item next = atlist.get(key);
                if (next.getStatus() == Item.STATUS_READY) {
                    TheadManager.addThread(new DownloadThread(next, account.getCookie()));
                    next.setStatus(Item.STATUS_DOWNLOADING);
                }
                if (next.getStatus() == Item.STATUS_DOWNLOADING) {
                    next.speed = (next.hasdown - next.lasthasdown) / (MainThread.time / 1000);
                    next.lasthasdown = next.hasdown;
                }
            }
            TheadManager.clean();
            TheadManager.printThreadStatus();
            try {
                Thread.sleep(MainThread.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
