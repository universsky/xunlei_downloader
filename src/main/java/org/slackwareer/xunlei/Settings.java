package org.slackwareer.xunlei;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 设置
 */
public class Settings {
    private static       Settings                _object  = new Settings();
    private static final String                  FILENAME = "org/slackwareer/utils/xunlei/xunlei/setting.ini";
    private              HashMap<String, String> list     = new HashMap<String, String>();

    private Settings() {
        if (!this.load()) {
            this.initList();
            this.save();
        }
    }

    public static Settings getInstance() {
        return Settings._object;
    }

    //读取
    public String get(String key) {
        return this.list.get(key);
    }

    //保存
    public void save() {
        try {
            File f = new File("./" + Settings.FILENAME);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f);
            Iterator<String> it = this.list.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = this.list.get(key);
                fw.write(String.format("%s=%s\n", key, value));
            }
            fw.close();
            System.out.println("Create a new Settings on ./" + Settings.FILENAME);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化
    public void initList() {
        this.list.put("cookie", "");
        this.list.put("path", "./");
        this.list.put("user", "");
        this.list.put("password", "");
        this.list.put("UserAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11");
    }

    //读取文件加载
    public boolean load() {
        try {
            InputStream is = Settings.class.getClassLoader().getResourceAsStream("org/slackwareer/xunlei/xunlei/setting.ini");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (reader.ready()) {
                String in = reader.readLine();
                if (in.isEmpty() || in.startsWith("#")) {
                    continue;
                } else {
                    String[] in_a = in.split("=", 2);
                    this.list.put(in_a[0].trim(), in_a[1].trim());
                }
            }
            reader.close();
            if (!(this.list.containsKey("user") && this.list.containsKey("password") && this.list.containsKey("cookie")
                    && !this.list.get("user").isEmpty() && !this.list.get("password").isEmpty() && !this.list.get("cookie").isEmpty())) {
                System.out.println("Settings error,please check the ./" + Settings.FILENAME + " file!!");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
