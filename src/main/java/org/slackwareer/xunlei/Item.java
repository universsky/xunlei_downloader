package org.slackwareer.xunlei;

import java.io.Serializable;

/**
 *
 */
public class Item implements Serializable {
    private String name        = "";
    private String size        = "";
    private String URL         = "";
    private String id          = "";
    private int    status      = 0;
    public  int    filesize    = 0;
    public  int    hasdown     = 0;
    public  int    lasthasdown = 0;
    public  float  speed       = 0;

    public static final int STATUS_STOP        = 0;
    public static final int STATUS_READY       = 1;
    public static final int STATUS_DOWNLOADING = 2;
    public static final int STATUS_PAUSE       = 3;
    public static final int STATUS_COMPLETE    = 4;
    public static final int STATUS_ERROR       = 5;
    public static final int STATUS_DELETE      = 6;

    public Item(String id, String name, String size, String URL, int status) {
        this.name = name;
        this.size = size;
        this.URL = URL;
        this.status = status;
        this.id = id;
    }

    public String toString() {
        return String.format("%s\t %s\t %s \t %s \t %s", this.id, this.name, this.status, this.size, this.URL);
    }

    public int getStatus() {
        return this.status;
    }

    public String getURL() {
        return this.URL;
    }

    public String getName() {
        return this.name;
    }

    public void setStatus(int b) {
        this.status = b;
    }

    public String getId() {
        return this.id;
    }

}
