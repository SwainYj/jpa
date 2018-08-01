package com.baidu.bce.tsdb.demo;


import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.tsdb.TsdbAdminClient;
import com.baidubce.services.tsdb.model.CreateDatabaseRequest;
import com.baidubce.services.tsdb.model.CreateDatabaseResponse;
import com.baidubce.services.tsdb.model.GetDatabaseResponse;
import com.baidubce.services.tsdb.model.ListDatabaseResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * manage tsdb api demo
 *
 * @author hq(haoqian @ baidu.com)
 */
public class AdminApiDemo {
    private static String ACCESS_KEY_ID;     // 用户的Access Key ID
    private static String SECRET_ACCESS_KEY; // 用户的Secret Access Key
    private static String ADMIN_ENDPOINT;    // 注意：与新建TsdbClient时使用的endpoint不同
    private TsdbAdminClient tsdbAdminClient;

    static {
        // read config from properties file
        Properties properties = new Properties();
        InputStream in = TsdbClientApiDemo.class.getClassLoader().getResourceAsStream("tsdb.properties");
        try {
            properties.load(in);
            ACCESS_KEY_ID = properties.getProperty("ACCESS_KEY_ID");
            SECRET_ACCESS_KEY = properties.getProperty("SECRET_ACCESS_KEY");
            ADMIN_ENDPOINT = properties.getProperty("ADMIN_ENDPOINT");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public AdminApiDemo() {
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ADMIN_ENDPOINT);

        // 初始化一个TsdbAdminClient
        this.tsdbAdminClient = new TsdbAdminClient(config);
    }

    /**
     * 新建TsdbAdminClient
     *
     * @return
     */
    public TsdbAdminClient getTsdbAdminClient() {
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ADMIN_ENDPOINT);

        // 初始化一个TsdbAdminClient
        TsdbAdminClient tsdbAdminClient = new TsdbAdminClient(config);
        return tsdbAdminClient;
    }

    /**
     * 创建时序数据库实例
     */
    public void createDatabase() {
        String databaseName = "databasename";                            // 实例的名字
        String description = "description";                              // 实例描述，可不填写
        int ingestDataPointsMonthly = 1;                                 // 写入额度，单位：百万点/月
        int purchaseLength = 1;                                          // 购买时长，单位：月
        String couponName = "<your-coupon-name>";                                          // 代金券号，可不填写

        CreateDatabaseRequest request = new CreateDatabaseRequest()
                .withDatabaseName(databaseName)
                .withDescription(description)
                .withIngestDataPointsMonthly(ingestDataPointsMonthly)
                .withPurchaseLength(purchaseLength)
                .withCouponName(couponName);

        String clientToken = "<your-client-token>";                        // ClientToken, 用于保证幂等性，重试发送创建请求时，使用同一个clientToken。
        CreateDatabaseResponse response = tsdbAdminClient.createDatabase(request, clientToken);
    }

    /**
     * 删除时序数据库实例
     * 注意，只允许删除到期的时序数据库实例，否则将报错。
     */
    public void deleteDatabase() {
        String databaseId = "<your-database-id>";                             // 实例的ID
        tsdbAdminClient.deleteDatabase(databaseId);

    }

    /**
     * 获取时序数据库实例
     *
     * @return
     */
    public GetDatabaseResponse getDatabase() {
        String databaseId = "<your-database-id>";  // 实例的ID
        GetDatabaseResponse database = tsdbAdminClient.getDatabase(databaseId);
        return database;
    }

    /**
     * 获取时序数据库实例列表
     *
     * @return
     */
    public ListDatabaseResponse getDatabaseList() {
        ListDatabaseResponse database = tsdbAdminClient.listDatabase();
        return database;
    }
}