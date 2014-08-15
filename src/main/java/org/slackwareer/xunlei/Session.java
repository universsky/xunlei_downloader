package org.slackwareer.xunlei;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class Session {
    private Connection          conn      = null;
    private Document            doc       = null;
    private Map<String, String> cookieMap = null;
    private String              UserAgent = "";

    private static final int MAX_RETRY = 10;

    public Session(String url) {
        this.conn = Jsoup.connect(url);
        this.cookieMap = this.conn.response().cookies();
        Settings s = Settings.getInstance();
        this.UserAgent = s.get("UserAgent");
    }

    public Session url(String url) {
        this.conn.url(url);
        return this;
    }

    public Document post() throws IOException {
        int retry = 0;
        while (retry < Session.MAX_RETRY) {
            try {
                this.doc = this.conn.cookies(this.cookieMap).userAgent(this.UserAgent).post();
                Map<String, String> cookieMap = this.conn.response().cookies();
                if (cookieMap.containsKey("gdriveid")) {
                    System.out.println("Fuck>>>" + cookieMap);
                }
                this.cookieMap.putAll(cookieMap);
                break;
            } catch (IOException e) {
                retry++;
                if (retry < Session.MAX_RETRY) {
                    throw e;
                }
            }
        }
        return this.doc;
    }

    public Session data(String key, String value) {
        this.conn.data(key, value);
        return this;
    }

    public Document get() throws IOException {
        int retry = 0;
        while (retry < Session.MAX_RETRY) {
            try {
                this.doc = this.conn.cookies(this.cookieMap).userAgent(this.UserAgent).get();
                Map<String, String> cookieMap = this.conn.response().cookies();
                if (cookieMap.containsKey("gdriveid")) {
                    System.out.println("Fuck>>>" + cookieMap);
                }
                this.cookieMap.putAll(cookieMap);
                break;
            } catch (IOException e) {
                retry++;
                if (retry < Session.MAX_RETRY) {
                    throw e;
                }
            }
        }
        return this.doc;
    }

    //返回cookie
    public Map<String, String> getCookieMap() {
        return this.cookieMap;
    }

    //獲取某個cookie項的值
    public String getCookie(String key) {
        return this.cookieMap.get(key);
    }
}
