package org.slackwareer.utils;

/**
 *
 */
public class XunleiMain {
    public static void main(String[] args) throws InterruptedException {
        Account at = Account.getInstance();
        at.login();
        at.printList();
        //启动后台守护进程
        new Thread(new MainThread()).start();
        Thread.sleep(1000000);
    }
}
