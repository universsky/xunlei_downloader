package org.slackwareer.xunlei;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * 迅雷session信息
 *
 * @author junxian
 * @date 14-8-15
 */
public class XunleiSession {
    private Connection          conn          = null;
    private Document            doc           = null;
    private Map<String, String> cookie        = null;
    private String              UserAgent     = "";
    private XunleiConfiguration configuration = null;


}
