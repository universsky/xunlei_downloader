package org.slackwareer.xunlei;

import org.jsoup.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * 迅雷session信息
 *
 * @author junxian
 * @date 14-8-15
 */
public class XunleiSession {
    private Connection          conn    = null;
    private Map<String, String> cookies = new HashMap<String, String>();
    private XunleiConfiguration conf    = null;

    public XunleiSession(XunleiConfiguration conf) {
        this.conf = conf;
    }

    public void init() {

    }

    public String getCookies() {
        return null;
    }
}
