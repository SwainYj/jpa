package com.baidu.bce.tsdb.demo;

import com.baidubce.BceClientConfiguration;
import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.tsdb.TsdbClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * create tsdb client demo
 *
 * @author hq(haoqian @ baidu.com)
 */
public class TsdbClientApiDemo {

    private static String ACCESS_KEY_ID;     // 用户的Access Key ID
    private static String SECRET_ACCESS_KEY; // 用户的Secret Access Key
    private static String ENDPOINT;          // 用户的时序数据库域名，形式如databasename.tsdb.iot.gz.baidubce.com
    private TsdbClient tsdbClient;

    static {
        // read config from properties file
        Properties properties = new Properties();
        InputStream in = TsdbClientApiDemo.class.getClassLoader().getResourceAsStream("tsdb.properties");
        try {
            properties.load(in);
            ACCESS_KEY_ID = properties.getProperty("ACCESS_KEY_ID");
            SECRET_ACCESS_KEY = properties.getProperty("SECRET_ACCESS_KEY");
            ENDPOINT = properties.getProperty("ENDPOINT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TsdbClientApiDemo() {
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ENDPOINT);

        // 初始化一个TsdbClient
        this.tsdbClient = new TsdbClient(config);
    }

    /**
     * 获取一个http client
     *
     * @return
     */
    public TsdbClient getTsdbClient() {
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ENDPOINT);

        // 初始化一个TsdbClient
        TsdbClient tsdbClient = new TsdbClient(config);
        return tsdbClient;
    }

    /**
     * 获取一个https client
     *
     * @return
     */
    public TsdbClient getHttpsTsdbClient() {
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withProtocol(Protocol.HTTPS)
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ENDPOINT);

        // 初始化一个TsdbClient
        TsdbClient tsdbClient = new TsdbClient(config);
        return tsdbClient;
    }

}
