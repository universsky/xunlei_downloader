package org.slackwareer.xunlei;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class Account {
    private        HashMap<String, Item> list    = new HashMap<String, Item>();//
    private static Account               _object = new Account();
    private        Session               session = null;

    private Account() {

    }

    public static Account getInstance() {
        return Account._object;
    }

    public Map<String, String> getCookie() {
        return this.session.getCookieMap();
    }

    public void login() {
        try {
            Settings settings = Settings.getInstance();
            this.session = new Session("http://lixian.vip.xunlei.com/task.html");
            this.session.get();
            this.session.url("http://login.xunlei.com/check?u=" + settings.get("user") + "&cachetime=" + System.currentTimeMillis()).get();
            String vcode = this.session.getCookie("check_result").split("0:")[1].toUpperCase();
            this.session.url("http://login.xunlei.com/sec2login/").data("u", settings.get("user"))
                    .data("p", md5(md5(md5(settings.get("password"))) + vcode))
                    .data("verifycode", vcode).data("login_enable", "1")
                    .data("login_hour", "720").post();
            Document doc = this.session.url("http://dynamic.lixian.vip.xunlei.com/login?cachetime=" + System.currentTimeMillis() + "&from=0").get();
            this.session.url("http://vip.xunlei.com/").get();
            Elements rwlist = doc.select(".rw_list");
            for (int i = 0, max = rwlist.size(); i < max; i++) {
                Element rw = rwlist.get(i);
                if (rw.select(".w05 div em").html().equals("已经过期")) {
                    break;
                }
                if (rw.attr("openformat").equals("other")) {
                    continue;
                }
                String taskid = rw.attr("taskid");
                String name = rw.select("#taskname" + taskid).val();
                String size = rw.select("#size" + taskid).html();

                String downURL = rw.select("#dl_url" + taskid).val();
                this.list.put(taskid, new Item(taskid, name, size, downURL, Item.STATUS_READY));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        Document doc = null;
        try {
            doc = this.session.url("http://dynamic.cloud.vip.xunlei.com/login?from=0").get();
            Elements rwlist = doc.select(".rw_list");
            HashMap<String, Item> tmp = new HashMap<String, Item>();
            HashMap<String, Item> tmpC = (HashMap<String, Item>) this.list.clone();
            for (int i = 0, max = rwlist.size(); i < max; i++) {
                Element rw = rwlist.get(i);
                if (rw.select(".w05 div em").html().equals("已经过期")) {
                    break;
                }
                if (rw.attr("openformat").equals("other")) {
                    continue;
                }
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
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString();
    }

    public void stop(String id) {
        this.list.get(id).setStatus(Item.STATUS_DELETE);
    }
}
