package org.slackwareer.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class Account {//管理离线下载列表的类,单例
    private        HashMap<String, Item> list     = new HashMap<String, Item>();//
    private static Account               _object  = new Account();
    private        Session               con      = null;
    private final  String                FILENAME = "FileList.dat";

    private Account() {

    }

    public static Account getInstance() {
        return Account._object;
    }

    //獲取Cookie
    public Map<String, String> getCookie() {
        return this.con.getCookie();
    }

    //登陸
    public void login() {
        try {
            this.con = new Session("http://lixian.vip.xunlei.com/task.html");
            Document doc = this.con.get();

            String url = "http://login.xunlei.com/sec2login/";
            this.con.url("http://login.xunlei.com/check?u=sel0537504&cachetime=" + System.currentTimeMillis()).get();
            String vcode = this.con.getCookie("check_result").split("0:")[1].toUpperCase();
            Settings settings = Settings.getInstance();
            doc = this.con.url(url).data("u", settings.get("user"))
                    .data("p", md5(md5(md5(settings.get("password"))) + vcode))
                    .data("verifycode", vcode).data("login_enable", "1")
                    .data("login_hour", "720").post();

            if (this.load()) {
                this.refresh();
                Iterator<String> it = this.list.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    Item value = this.list.get(key);
                    if (value.getStatus() == Item.STATUS_DOWNLOADING) {
                        TheadManager.addThread(new DownloadThread(value, this.getCookie()));
                    }
                }
            } else {
                doc = this.con.url("http://dynamic.cloud.vip.xunlei.com/login?from=0").get();
                Elements rwlist = doc.select(".rw_list");
                for (int i = 0, max = rwlist.size(); i < max; i++) {
                    Element rw = rwlist.get(i);
                    if (rw.select(".w05 div em").html().equals("已经过期")) break;
                    if (rw.attr("openformat").equals("other")) continue;
                    String taskid = rw.attr("taskid");
                    String name = rw.select("#taskname" + taskid).val();
                    String size = rw.select("#size" + taskid).html();
                    String downURL = rw.select("#dl_url" + taskid).val();
                    this.list.put(taskid, new Item(taskid, name, size, downURL, Item.STATUS_STOP));
                }
                this.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //刷新離線下載列表
    public void refresh() {
        Document doc = null;
        try {
            doc = this.con.url("http://dynamic.cloud.vip.xunlei.com/login?from=0").get();
            Elements rwlist = doc.select(".rw_list");
            HashMap<String, Item> tmp = new HashMap<String, Item>();
            @SuppressWarnings("unchecked")
            HashMap<String, Item> tmpC = (HashMap<String, Item>) this.list.clone();
            for (int i = 0, max = rwlist.size(); i < max; i++) {
                Element rw = rwlist.get(i);
                if (rw.select(".w05 div em").html().equals("已经过期")) break;
                if (rw.attr("openformat").equals("other")) continue;
                String taskid = rw.attr("taskid");
                String name = rw.select("#taskname" + taskid).val();
                String size = rw.select("#size" + taskid).html();
                String downURL = rw.select("#dl_url" + taskid).val();
                if (this.list.containsKey(taskid)) {
                    tmp.put(taskid, new Item(taskid, name, size, downURL, this.list.get(taskid).getStatus()));
                    tmpC.remove(taskid);
                } else {
                    this.list.put(taskid, new Item(taskid, name, size, downURL, Item.STATUS_READY));
                }
            }
            Iterator<String> it = tmpC.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                this.stop(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //打印離線下載列表
    public void printList() {
        Iterator<String> it = this.list.keySet().iterator();
        System.out.println(String.format("%s\t %s\t %s \t %s \t %s", "id", "name", "size", "status", "URL"));
        while (it.hasNext()) {
            String id = it.next();
            System.out.println(this.list.get(id).toString());
        }
    }

    //獲取列表
    public HashMap<String, Item> getList() {
        return this.list;
    }

    //md5函數
    private static String md5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    public boolean load() {
        File f = new File("./" + this.FILENAME);
        if (f.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                this.list = (HashMap<String, Item>) ois.readObject();
                ois.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void save() {
        try {
            File f = new File("./" + this.FILENAME);
            if (!f.exists()) f.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(this.list);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //停止下載,id為迅雷離線下載id
    public void stop(String id) {
        this.list.get(id).setStatus(Item.STATUS_DELETE);
    }

    //暫停下載
    public void pause(String id) {
        this.list.get(id).setStatus(Item.STATUS_PAUSE);
    }
}
