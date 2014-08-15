package org.slackwareer.xunlei;

import org.jsoup.helper.StringUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * 迅雷账号配置信息
 *
 * @author junxian
 * @date 14-8-15
 */
public class XunleiConfiguration {
    @CONFIG("用户名")
    private String user      = null;
    @CONFIG("密码")
    private String password  = null;
    @CONFIG("工作目录")
    private String workspace = "./";//默认是当前目录
    @CONFIG("UserAgent")
    private String userAgent = null;

    public XunleiConfiguration() {

    }

    public XunleiConfiguration(String file) throws Exception {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            properties.load(new FileInputStream(file));
            Field[] fields = this.getClass().getDeclaredFields();

            for (Field field : fields) {
                Object o = properties.get(field.getName());
                if (o != null && !StringUtil.isBlank(String.valueOf(o))) {
                    field.setAccessible(true);
                    field.set(this, o);
                }
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            sb.append(field.getName());
            sb.append("=");
            sb.append(field.getAnnotation(CONFIG.class).value());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface CONFIG {
        public String value() default "";
    }
}
