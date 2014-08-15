package org.slackwareer.xunlei;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * 下载线程
 */
public class DownloadThread extends XunleiThread {
    private              String url       = null;
    private              String name      = "";
    private              String path      = null;
    private static final int    TIMEOUT   = 5000;//连接最大等待响应时间
    private static final int    BUFFSIZE  = 5000000;//下载缓冲,默认5M
    private              String cookieStr = "";
    private              String id        = "";
    private              Item   item      = null;


    public DownloadThread(Item item, Map<String, String> cookies) {
        Settings s = Settings.getInstance();
        this.path = s.get("path") + item.getName();
        this.url = item.getURL();
        this.id = item.getId();
        this.name = item.getName();
        this.item = item;
        Iterator<String> it = cookies.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            this.cookieStr += name + "=" + cookies.get(name) + (it.hasNext() ? "; " : "");
        }
    }

    public int getFileSize() {
        return this.item.filesize;
    }

    @Override
    public void runing() {
        this.print("Create new connection to Download " + this.name);
        RandomAccessFile file = null;
        HttpURLConnection conn = null;
        try {
            URL downloadUrl = new URL(url);
            conn = (HttpURLConnection) downloadUrl.openConnection();
            conn.setConnectTimeout(DownloadThread.TIMEOUT);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,fil;q=0.2,es;q=0.2");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/32.0.1700.107 Chrome/32.0.1700.107 Safari/537.36");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Cookie", this.cookieStr + ";lx_login_u=yewenjunjun@qq.com;gdriveid=C6D2168AEDC94E99FB86E0E5C7BDDDE3");
            conn.setInstanceFollowRedirects(true);
            this.item.filesize = conn.getContentLength();
            file = new RandomAccessFile(this.path, "rw");
            file.setLength(this.item.filesize);//获取文件大小
            if (!this.load()) this.save();//创建或加载下载文件信息
            file.seek(this.item.hasdown);//文件定位
            InputStream inStream = conn.getInputStream();
            inStream.skip(this.item.hasdown);//下载定位
            byte[] buffer = new byte[DownloadThread.BUFFSIZE];
            int hasRead = 0;
            // long time = 1;
            //下载开始
            while ((this.item.getStatus() == Item.STATUS_DOWNLOADING && this.item.hasdown < this.item.filesize) && ((hasRead = inStream.read(buffer)) != -1)) {
                file.write(buffer, 0, hasRead);
                this.item.hasdown += hasRead;
            }
            //下载结束,检查状态
            //检查时候下载完成
            if (this.item.filesize == this.item.hasdown) {
                this.item.setStatus(Item.STATUS_COMPLETE);
                this.print(this.name + " Download was complete " + this.name);
                this.delete();
            } else {
                if (this.item.getStatus() == Item.STATUS_DOWNLOADING) {
                    this.item.setStatus(Item.STATUS_ERROR);
                    this.print("error Download " + this.name);
                }
            }
            if (this.item.getStatus() == Item.STATUS_DELETE) {
                File f = new File(this.path);
                f.delete();
                this.delete();
                this.print(this.name + "Download was Delete");
            }

            if (this.item.getStatus() == Item.STATUS_PAUSE) {
                this.print(this.name + "Download was pause");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getHasDown() {
        return this.item.hasdown;
    }


    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }


    public void save() {
        try {
            File f = new File(this.path + ".info");
            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f);
            fw.write(this.item.hasdown + "\n");
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean load() {
        try {
            File f = new File(this.path + ".info");
            if (f.exists()) {
                BufferedReader fr = new BufferedReader(new FileReader(f));
                if (fr.ready()) this.item.hasdown = Integer.valueOf(fr.readLine());
                fr.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void delete() {
        File f = new File(this.path + ".info");
        if (f.exists()) f.delete();
    }

    public float getSpeed() {
        return this.item.speed;
    }
}
