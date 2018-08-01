package com.baidu.bce.tsdb.demo;

import com.baidubce.services.tsdb.TsdbClient;
import com.baidubce.services.tsdb.TsdbConstants;
import com.baidubce.services.tsdb.model.Aggregator;
import com.baidubce.services.tsdb.model.FieldFilter;
import com.baidubce.services.tsdb.model.Fill;
import com.baidubce.services.tsdb.model.Filters;
import com.baidubce.services.tsdb.model.GetFieldsResponse;
import com.baidubce.services.tsdb.model.GetMetricsResponse;
import com.baidubce.services.tsdb.model.GetTagsResponse;
import com.baidubce.services.tsdb.model.Group;
import com.baidubce.services.tsdb.model.GroupInfo;
import com.baidubce.services.tsdb.model.Query;
import com.baidubce.services.tsdb.model.QueryDatapointsResponse;
import com.baidubce.services.tsdb.model.Result;
import com.baidubce.services.tsdb.model.TagFilter;
import com.baidubce.services.tsdb.model.ValueFilter;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * query data demo
 *
 * @author hq(haoqian @ baidu.com)
 */
public class QueryApiDemo {
    private TsdbClient tsdbClient;

    public QueryApiDemo() {
        this.tsdbClient = new TsdbClientApiDemo().getTsdbClient();
    }

    /**
     * 获取metric列表
     */
    public void getMetrics() {
        // 获取Metric
        GetMetricsResponse response = tsdbClient.getMetrics();

        // 打印结果
        for (String metric : response.getMetrics()) {
            System.out.println(metric);
        }
    }

    /**
     * 获取field列表
     */
    public void getFields() {
        String metric = "wind";

        // 获取Field
        GetFieldsResponse response = tsdbClient.getFields(metric);

        // 打印结果
        for (Map.Entry<String, GetFieldsResponse.FieldInfo> entry : response.getFields().entrySet()) {
            System.out.println(entry.getKey() + ":");
            System.out.println("\t" + entry.getValue().getType());
        }

    }

    /**
     * 获取tag标签和tag值
     */
    public void getTags() {
        String metric = "wind";   // 设置需要获取tag的metric

        // 获取标签
        GetTagsResponse response = tsdbClient.getTags(metric);

        // 打印结果
        for (Map.Entry<String, List<String>> entry : response.getTags().entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String tagValue : entry.getValue()) {
                System.out.println("\t" + tagValue);
            }
        }
    }

    /**
     * 查询数据点
     */
    public void queryData() {
        String metric = "wind";
        String field = "direction";

        // 构造查询对象
        List<Query> queries = Arrays.asList(new Query()  // 创建Query对象
                .withMetric(metric)          // 设置metric
                .withField(field)           // 设置查询的域名，不设置表示查询默认域
                .withFilters(new Filters()     // 创建Filters对象
                        .withRelativeStart("2 hours ago"))  // 设置相对的开始时间，这里设置为2小时前
                .withLimit(100)   // 设置返回数据点数目限制
                .addAggregator(new Aggregator()   // 创建Aggregator对象
                        .withName(TsdbConstants.AGGREGATOR_NAME_SUM)  // 设置聚合函数为Sum
                        .withSampling("1 second")));  // 设置采样

        // 查询数据
        QueryDatapointsResponse response = tsdbClient.queryDatapoints(queries);

        // 打印结果
        for (Result result : response.getResults()) {
            System.out.println("Result:");
            for (Group group : result.getGroups()) {
                System.out.println("\tGroup:");

                for (GroupInfo groupInfo : group.getGroupInfos()) {
                    System.out.println("\t\tGroupInfo:");
                    System.out.println("\t\t\tName:" + groupInfo.getName());
                }
                System.out.println("\t\tTimeAndValue:");
                try {
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        if (timeAndValue.isDouble()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getDoubleValue() + "]");
                        } else if (timeAndValue.isLong()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getLongValue() + "]");
                        } else {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getStringValue() + "]");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查询多域数据点
     */
    public void queryMultipleFieldsData() {
        String metric = "wind";
        String field1 = "direction";
        String field2 = "speed";
        String tag = "city";

        // 构造查询对象
        List<String> fields = Arrays.asList(field1, field2);
        List<Query> queries = Arrays.asList(new Query()             // 创建Query对象
                .withMetric(metric)                                 // 设置metric
                .withFields(fields)                                 // 设置查询的域名列表，不设置表示查询默认域，和field冲突
                .withTags(Arrays.asList(tag))                       // 设置查询的Tag列表，不设置表示不查询，
                .withFilters(new Filters()                          // 创建Filters对象
                        .withRelativeStart("2 hours ago")         // 设置相对的开始时间，这里设置为2小时前
                        .withRelativeEnd("1 second ago"))           // 设置相对的结束时间，不设置则默认为到当前时间为止
                .withLimit(100));

        // 查询数据，返回结果的顺序和请求的field顺序相同
        QueryDatapointsResponse response = tsdbClient.queryDatapoints(queries);

        // 打印结果
        for (Result result : response.getResults()) {
            System.out.println("Result:");
            for (Group group : result.getGroups()) {
                System.out.println("\tGroup:");

                for (GroupInfo groupInfo : group.getGroupInfos()) {
                    System.out.println("\t\tGroupInfo:");
                    System.out.println("\t\t\tName:" + groupInfo.getName());
                }
                System.out.println("\t\tTimeAndValues:");
                try {
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        System.out.print("\t\t\t[" + timeAndValue.getTime());
                        for (int index = 0; index < fields.size(); index++) {
                            System.out.print(", ");
                            if (!timeAndValue.isNull(index)) {
                                if (timeAndValue.isDouble(index)) {
                                    System.out.print(timeAndValue.getDoubleValue(index));
                                } else if (timeAndValue.isLong(index)) {
                                    System.out.print(timeAndValue.getLongValue(index));
                                } else {
                                    System.out.print(timeAndValue.getStringValue(index));

                                }
                            } else {
                                System.out.print("null");
                            }
                        }
                        System.out.println("]");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查询数据点的同时返回对应的tags信息
     */
    public void queryDatapointAndTag() {
        String metric = "wind";
        String field = "direction";
        String tagKey1 = "city";
        String tagKey2 = "province"; // 构造查询对象
        List<Query> queries = Arrays.asList(new Query()                          // 创建Query对象
                .withMetric(metric)                                              // 设置metric
                .withField(field)                                               // 设置查询的域名，不设置表示查询默认域
                .withTags(Arrays.asList(tagKey1, tagKey2))                     // 设置需要同时返回的tag key列表
                .withFilters(new Filters()                                       // 创建Filters对象
                        .withRelativeStart("2 hours ago"))                     // 设置相对的开始时间
                .withLimit(100));                                                   // 设置返回数据点数目限制


        // 查询数据
        QueryDatapointsResponse response = tsdbClient.queryDatapoints(queries);

        // 打印结果
        for (Result result : response.getResults()) {
            System.out.println("Result:");
            for (Group group : result.getGroups()) {
                System.out.println("\tGroup:");

                for (GroupInfo groupInfo : group.getGroupInfos()) {
                    System.out.println("\t\tGroupInfo:");
                    System.out.println("\t\t\tName:" + groupInfo.getName());
                }
                System.out.println("\t\tTimeAndValue:");
                try {
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        // 打印的格式为 [时间,FIELD_VALUE,TAG_1_VALUE,TAG_2_VALUE]
                        if (timeAndValue.isDouble()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getDoubleValue(0) + "," + timeAndValue.getStringValue(1) + "," + timeAndValue.getStringValue(2) + "]");
                        } else if (timeAndValue.isLong()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getLongValue(0) + "," + timeAndValue.getStringValue(1) + "," + timeAndValue.getStringValue(2) + "]");
                        } else {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getStringValue(0) + "," + timeAndValue.getStringValue(1) + "," + timeAndValue.getStringValue(2) + "]");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 使用插值方式查询数据点
     */
    public void queryFill() {
        String metric = "wind";
        String field = "direction";

        // 构造查询对象
        List<Query> queries = Arrays.asList(new Query()                          // 创建Query对象
                .withMetric(metric)                                              // 设置metric
                .withField(field)                                                // 设置查询的域名，不设置表示查询默认域
                .withFilters(new Filters()                                       // 创建Filters对象
                        .withRelativeStart("2 hours ago")                      // 设置相对的开始时间
                        .withRelativeEnd("1 second ago"))                        // 设置相对的结束时间，不设置则默认为到当前时间为止
                .withFill(new Fill()
                        .withType(TsdbConstants.FILL_TYPE_LINEAR)                // 设置插值类型，这里设置成线性插值
                        .withInterval("5 minutes")                               // 设置插值间隔
                        .withMaxWriteInterval("10 minutes")));                   // 设置最大写入间隔

        // 查询数据
        QueryDatapointsResponse response = tsdbClient.queryDatapoints(queries);

        // 打印结果
        for (Result result : response.getResults()) {
            System.out.println("Result:");
            for (Group group : result.getGroups()) {
                System.out.println("\tGroup:");

                for (GroupInfo groupInfo : group.getGroupInfos()) {
                    System.out.println("\t\tGroupInfo:");
                    System.out.println("\t\t\tName:" + groupInfo.getName());
                }
                System.out.println("\t\tTimeAndValue:");
                try {
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        if (timeAndValue.isDouble()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getDoubleValue() + "]");
                        } else if (timeAndValue.isLong()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getLongValue() + "]");
                        } else {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getStringValue() + "]");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 分页查询原始数据点
     */
    public void queryPage() {
        String metric = "wind";
        String field = "direction";

        // 构造查询对象
        Query query = new Query()                                                // 创建Query对象
                .withMetric(metric)                                              // 设置metric
                .withField(field)                                                // 设置查询的域名，不设置表示查询默认域
                .withFilters(new Filters()                                       // 创建Filters对象
                        .withRelativeStart("1 week ago")                         // 设置相对的开始时间，这里设置为1周前
                        .withRelativeEnd("1 second ago"));                       // 设置相对的结束时间，不设置则默认为到当前时间为止

        while (true) {
            // 查询数据
            QueryDatapointsResponse response = tsdbClient.queryDatapoints(Arrays.asList(query));
            // 打印结果
            Result result = response.getResults().get(0);
            System.out.println("Result:");
            for (Group group : result.getGroups()) {
                System.out.println("\tGroup:");
                for (GroupInfo groupInfo : group.getGroupInfos()) {
                    System.out.println("\t\tGroupInfo:");
                    System.out.println("\t\t\tName:" + groupInfo.getName());
                }
                System.out.println("\t\tTimeAndValue:");
                try {
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        if (timeAndValue.isDouble()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getDoubleValue() + "]");
                        } else if (timeAndValue.isLong()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getLongValue() + "]");
                        } else {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getStringValue() + "]");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 如果没有下一页数据，则退出循环
            if (!result.isTruncated()) {
                break;
            }
            // 设置下一页数据的marker
            query.setMarker(result.getNextMarker());
        }
    }

    /**
     * 查询数据时使用标签过滤
     */
    public void queryFilterWithTag() {
        String metric = "wind";
        String field = "direction";

        // 构造查询对象
        List<Query> queries = Arrays.asList(new Query()                          // 创建Query对象
                .withMetric(metric)                                              // 设置metric
                .withField(field)                                                // 设置查询的域名，不设置表示查询默认域
                .withFilters(new Filters()                                       // 创建Filters对象
                        .withRelativeStart("2 hours ago")                      // 设置相对的开始时间，这里设置为2小时前
                        .withRelativeEnd("1 second ago")                         // 设置相对的结束时间，不设置则默认为到当前时间为止
                        .addTagFilter(new TagFilter()                            // 创建TagFilter对象
                                .withTag("tag1")                                 // 设置要过滤的tagKey
                                .addNotIn("value2"))));                          // 设置不允许出现的tagValue。还可以设置允许出现的tagValue，或者设置like匹配。
        // 查询数据
        QueryDatapointsResponse response = tsdbClient.queryDatapoints(queries);

        // 打印结果
        for (Result result : response.getResults()) {
            System.out.println("Result:");
            for (Group group : result.getGroups()) {
                System.out.println("\tGroup:");
                System.out.println("\t\tTimeAndValue:");
                try {
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        if (timeAndValue.isDouble()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getDoubleValue() + "]");
                        } else if (timeAndValue.isLong()) {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getLongValue() + "]");
                        } else {
                            System.out.println("\t\t\t[" + timeAndValue.getTime() + "," + timeAndValue.getStringValue() + "]");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Filters对象设置示例
     */
    public void filterConfig() {
        // 使用绝对时间
        Filters filters1 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000);

        // 添加Tags：逐个添加
        Filters filters2 = new Filters()
                .withRelativeStart("5 minutes ago")
                .withRelativeEnd("2 minutes ago")
                .addTag("tagKey1", "tagValue1", "tagValue2")
                .addTag("tagKey2", "tagValue1");

        // 添加Tags：通过map添加
        Map<String, List<String>> tags = new HashMap<String, List<String>>();      // 创建tags
        tags.put("tagKey1", Arrays.asList("tagValue1", "tagValue2"));              // 添加tag
        tags.put("tagKey2", Arrays.asList("tagValue1"));                           // 添加tag
        Filters filters3 = new Filters()
                .withRelativeStart("5 minutes ago")
                .withRelativeEnd("2 minutes ago")
                .withTags(tags);

    }

    /**
     * 按值过滤的设置示例
     */
    public void filterConfigByValue() {
        // 对long型数据进行过滤，支持的比较符为 >，<，>=，<=，= 和 !=
        Filters filters1 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000)
                .withValue(ValueFilter.createValueFilter(ValueFilter.LESS_OR_EQUAL, 100L));         // 过滤条件为 "<= 100"

        // 对double型数据进行过滤，支持的比较符为 >，<，>=，<=，= 和 !=
        Filters filters2 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000)
                .withValue(ValueFilter.createValueFilter(ValueFilter.GREATER, 99.9));          // 过滤条件为 "> 99.9"

        // 对String型数据进行过滤，支持的比较符为 >，<，>=，<=，= 和 !=
        Filters filters3 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000)
                .withValue(ValueFilter.createValueFilter(ValueFilter.EQUAL, "stringvalue"));   // 过滤条件为 "= 'stringvalue'"

        // 将数据与标签tagKey1的值进行比较过滤，支持的比较符为 >，<，>=，<=，= 和 !=
        Filters filters4 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000)
                .withValue(ValueFilter.createValueFilterOfTag(ValueFilter.EQUAL, "tagKey1"));   // 过滤条件为 "= tagKey1"

        // 对所有多域数据使用统一value进行过滤，支持的比较符为 >，<，>=，<=，= 和 !=
        Filters filters5 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000)
                .withValue(ValueFilter.createValueFilter(ValueFilter.LESS_OR_EQUAL, 100L));         // 过滤条件为 "<= 100"

        // 对多域数据使用不同的value进行过滤，支持的比较符为 >，<，>=，<=，= 和 !=
        FieldFilter fieldFilter1 = new FieldFilter("field1", ValueFilter.createValueFilter("<", 100L));
        FieldFilter fieldFilter2 = new FieldFilter("field2", ValueFilter.createValueFilter(">", 200L));
        Filters filters6 = new Filters()
                .withAbsoluteStart(System.currentTimeMillis() - 5000)
                .withAbsoluteEnd(System.currentTimeMillis() - 1000)
                .withFields(Arrays.asList(fieldFilter1, fieldFilter2));         // 过滤条件为 "field1< 100 && field2 > 200"

    }

    /**
     * 生成查询数据点的预签名URL
     *
     * @return
     */
    public URL genenateUrl() {
        String metric = "wind";                                            // 设置需要获取tag的metric
        String field = "direction";

        // 构造查询对象
        List<Query> queries = Arrays.asList(new Query()                          // 创建Query对象
                .withMetric(metric)                                              // 设置metric
                .withField(field)                                                // 设置域，不设置表示查询默认域
                .withFilters(new Filters()                                       // 创建Filters对象
                        .withRelativeStart("2 seconds ago")                      // 设置相对的开始时间，这里设置为5秒前
                        .withRelativeEnd("1 second ago"))                        // 设置相对的结束时间，不设置则默认为到当前时间为止
                .withLimit(100)                                                  // 设置返回数据点数目限制
                .addAggregator(new Aggregator()                                  // 创建Aggregator对象
                        .withName(TsdbConstants.AGGREGATOR_NAME_SUM)             // 设置聚合函数为Sum
                        .withSampling("1 second")));                             // 设置采样

        // 获取预签名URL
        URL url = tsdbClient.generatePresignedUrlForQueryDatapoints(queries, 120); // 设置签名超时时间为120s

        return url;
    }

}
