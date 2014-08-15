package org.slackwareer.xunlei;

import java.io.IOException;

/**
 * 迅雷主入口
 *
 * @author slackwareer
 */
public class XunleiMain {
    public static void main(String[] args) throws Exception {
        Account at = Account.getInstance();
        at.login();
        at.printList();
        new Thread(new MainThread()).start();
        Thread.sleep(1000000);
    }

    private static void help() throws IOException {
        System.out.println("Usage:");
        System.out.println("java -cp xunlei_downloader.jar " + XunleiMain.class.getName() + " CofigFile");
        System.out.println("ConfigFile Sample:");
        System.out.println(new XunleiConfiguration().toString());
    }
}
