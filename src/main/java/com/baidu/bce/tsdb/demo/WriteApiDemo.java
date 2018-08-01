package com.baidu.bce.tsdb.demo;

import com.baidubce.services.tsdb.TsdbClient;
import com.baidubce.services.tsdb.model.Datapoint;
import com.baidubce.services.tsdb.model.WriteDatapointsRequest;
import com.baidubce.services.tsdb.model.WriteDatapointsResponse;

import java.util.Arrays;
import java.util.List;

/**
 * write datapoint  demo
 *
 * @author hq(haoqian @ baidu.com)
 */
public class WriteApiDemo {
    private TsdbClient tsdbClient;

    public WriteApiDemo() {
        this.tsdbClient = new TsdbClientApiDemo().getTsdbClient();
    }

    /**
     * 写入单域数据点
     * 注意：当写入的metric、field、tags、timestamp都相同时，后写入的value会覆盖先写入的value。
     */
    public WriteDatapointsResponse writeSingleFieldDatapoints() {
        String metric = "wind";    // metric
        String tagKey = "city";     // 标签名称
        String tagValue = "ShangHai";  // 标签值
        String field = "direction";  // 域

        // 创建数据点
        List<Datapoint> datapoints = Arrays.asList(new Datapoint()
                .withMetric(metric)    // 设置Metric
                .withField(field)      // 设置数据点域，可选，不填使用默认域名 value
                .addTag(tagKey, tagValue)   // 设置Tag
                .addDoubleValue(System.currentTimeMillis(), 0.1)); // 添加一个数据点

        return tsdbClient.writeDatapoints(datapoints);
    }

    /**
     * 一个Datapoint对象可以同时添加多个数据点，这些数据点使用相同的metric、标签和域。
     * 多个相同metric和标签的数据放入同一个Datapoint对象，可以减少payload。
     *
     * @return
     */
    public WriteDatapointsResponse addMultipleDatapoint() {
        String metric = "wind";    // metric
        String tagKey = "city";     // 标签名称
        String tagValue = "ShangHai";  // 标签值
        String field = "direction";  // 域
        Datapoint datapoint = new Datapoint()
                .withMetric(metric)  // 设置Metric
                .withField(field)    // 设置数据点域，可选，不填使用默认域名 value
                .addTag(tagKey, tagValue)  // 设置Tag
                .addDoubleValue(System.currentTimeMillis(), 0.1) // 添加一个数据点
                .addDoubleValue(System.currentTimeMillis() + 1, 0.2);  // 再添加一个数据点

        return tsdbClient.writeDatapoints(Arrays.asList(datapoint));
    }

    /**
     * Datapoint对象可以添加double，long和String类型的数据点。
     * 对于同一个field，如果写入了某个数据类型的value之后，相同的field不允许写入其他数据类型。
     *
     * @return
     */
    public WriteDatapointsResponse addMultipleTypeDatapoint() {
        String tagKey = "city";     // 标签名称
        String tagValue = "ShangHai";  // 标签值
        String field = "direction";  // 域
        // 添加Double类型数据点
        Datapoint datapoint1 = new Datapoint()
                .withMetric("wind")    // 设置Metric
                .withField(field)     // 设置数据点域，可选，不填使用默认域名 value
                .addTag(tagKey, tagValue)    // 设置Tag
                .addDoubleValue(System.currentTimeMillis(), 0.1); // 添加Double类型数据点

        // 添加Long类型数据点
        Datapoint datapoint2 = new Datapoint()
                .withMetric("memory_usage")
                .addTag(tagKey, tagValue)
                .addLongValue(System.currentTimeMillis(), 10L);

        // 添加String类型数据点
        Datapoint datapoint3 = new Datapoint()
                .withMetric("error_message")
                .addTag(tagKey, tagValue)
                .addStringValue(System.currentTimeMillis(), "string");

        return tsdbClient.writeDatapoints(Arrays.asList(datapoint1, datapoint2, datapoint3));
    }

    /**
     * 写入多域数据点
     * 不同的域并不需要同时写入，可以认为不同的域都是独立的。
     * 但如果查询时要用一条语句查出来，需要保证metric、所有的tag、时间戳都是一致的。
     */
    public WriteDatapointsResponse writeMultipleFieldDatapoints() {
        String metric = "wind";   // metric
        String tagKey = "city";   // 标签名称
        String tagValue = "ShangHai";  // 标签值
        String field1 = "direction";   // 域1
        String field2 = "speed";     // 域2
        long TIME = System.currentTimeMillis();    // 时间

        // 添加field_1的数据点
        Datapoint datapoint1 = new Datapoint()
                .withMetric(metric)  // 设置Metric
                .withField(field1)  // 设置域1
                .addTag(tagKey, tagValue)  // 设置Tag
                .addDoubleValue(TIME, 0.1);   // 指定时间写入Double类型数据点

        // 添加field_2的数据点
        Datapoint datapoint2 = new Datapoint()
                .withMetric(metric)  // 设置Metric,需要和field1的一样
                .withField(field2)  // 设置域2
                .addTag(tagKey, tagValue)  // 设置Tag，需要和field1的一样
                .addLongValue(TIME, 10L);  // 指定时间添加Long类型数据点，时间需要和field_1的一样

        return tsdbClient.writeDatapoints(Arrays.asList(datapoint1, datapoint2));
    }

    /**
     * 写入数据点的gzip压缩说明
     * v0.10.10版本的sdk中，写入数据点默认开启gzip压缩。
     * 如果不需要使用gzip压缩，可参考如下代码：
     */
    public void writeNotUseGzip() {
        String metric = "wind";                                          // metric
        String tagKey = "city";                                           // 标签名称
        String tagValue = "ShangHai";                                        // 标签值

        // 创建数据点
        List<Datapoint> datapoints = Arrays.asList(new Datapoint()
                .withMetric(metric)                                  // 设置Metric
                .addTag(tagKey, tagValue)                          // 设置Tag
                .addDoubleValue(System.currentTimeMillis(), 0.1));   // 添加一个数据点
        boolean useGzip = false;                                                // 不使用gzip

        tsdbClient.writeDatapoints(new WriteDatapointsRequest().withDatapoints(datapoints), useGzip);
    }

}
