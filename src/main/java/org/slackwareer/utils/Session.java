package org.slackwareer.utils;

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
    private Map<String, String> cookie    = null;
    private String              UserAgent = "";

    private static final int MAX_RETRY = 10;

    public Session(String url) {
        this.conn = Jsoup.connect(url);
        this.cookie = this.conn.response().cookies();
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
                this.doc = this.conn.cookies(this.cookie).userAgent(this.UserAgent).post();
                this.cookie.putAll(this.conn.response().cookies());
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

    //以get方式提交數據
    public Document get() throws IOException {
        int retry = 0;
        while (retry < Session.MAX_RETRY) {
            try {
                this.doc = this.conn.cookies(this.cookie).userAgent(this.UserAgent).get();
                this.cookie.putAll(this.conn.response().cookies());
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

    //返回網頁內容
    public Document getDoc() {
        return this.doc;
    }

    //返回cookie
    public Map<String, String> getCookie() {
        return this.cookie;
    }

    //獲取某個cookie項的值
    public String getCookie(String key) {
        return this.cookie.get(key);
    }
}
