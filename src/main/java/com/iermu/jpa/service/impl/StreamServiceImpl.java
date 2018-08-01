package com.iermu.jpa.service.impl;

import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.tsdb.model.*;
import com.baidubce.services.tsdb.TsdbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.iermu.jpa.service.StreamService;

//import com.google.gson.JsonArray;
//import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//import com.google.gson.JsonSyntaxException;

@Service(value = "streamService")
public class StreamServiceImpl implements StreamService{

    private static String ACCESS_KEY_ID = "1c1e5dc78f06489aacd52475245ccc1b";     // 用户的Access Key ID
    private static String SECRET_ACCESS_KEY = "6c20c98d1fba41d8b806094a35888617"; // 用户的Secret Access Key
    private static String ENDPOINT = "swaintestiermu.tsdb-8xe9i5yc98t6.tsdb.iot.bj.baidubce.com";          // 用户的时序数据库域名，形式如databasename.tsdb.iot.gz.baidubce.com
//    private TsdbClient tsdbClient;

    private static Logger logger = LoggerFactory.getLogger(StreamServiceImpl.class);

    @Override
    public WriteDatapointsResponse SaveRecord(String stream, String payload){
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ENDPOINT);

        // 初始化一个TsdbClient
        TsdbClient tsdbClient = new TsdbClient(config);


        JsonParser parse =new JsonParser();  //创建json解析器
        JsonObject json=(JsonObject) parse.parse(payload);

        String alarming = json.get("alarming").getAsString();
        String bucket = json.get("bucket").getAsString();
        String key = json.get("key").getAsString();
        String format = json.get("format").getAsString();
        Long start_time = json.get("start_time").getAsLong();
        Long end_time = json.get("end_time").getAsLong();

        JsonObject obj = new JsonObject();
        //addProperty是添加属性（数值、数组等）；add是添加json对象
        obj.addProperty("key", key);
        obj.addProperty("bucket", bucket);
        obj.addProperty("alarming", alarming);
        String filestr = obj.toString();


        logger.info("---param--"+obj.toString());

        String tagKey = "stream";     // 标签名称
        String tagValue = stream;  // 标签值

        String field1 = "start_time";  // 域
        // 添加Long类型数据点
        Datapoint datapoint1 = new Datapoint()
                .withMetric("metric_video_record")    // 设置Metric
                .withField(field1)     // 设置数据点域，可选，不填使用默认域名 value
                .addTag(tagKey, tagValue)    // 设置Tag
                .addLongValue(start_time, start_time); //添加long类型数据

        String field2 = "end_time";  // 域
        Datapoint datapoint2 = new Datapoint()
                .withMetric("metric_video_record")
                .withField(field2)
                .addTag(tagKey, tagValue)
                .addLongValue(start_time, end_time);

        // 添加String类型数据点
        String field3 = "file_info";  // 域
        Datapoint datapoint3 = new Datapoint()
                .withMetric("metric_video_record")
                .withField(field3)
                .addTag(tagKey, tagValue)
                .addStringValue(start_time, filestr);

        return tsdbClient.writeDatapoints(Arrays.asList(datapoint1, datapoint2, datapoint3));
    }

    @Override
    public WriteDatapointsResponse SaveThumbnail(String stream,String payload){
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ENDPOINT);

        // 初始化一个TsdbClient
        TsdbClient tsdbClient = new TsdbClient(config);


        JsonParser parse =new JsonParser();  //创建json解析器
        JsonObject json=(JsonObject) parse.parse(payload);

        String bucket = json.get("bucket").getAsString();
        String key = json.get("key").getAsString();
        String format = json.get("format").getAsString();
        Long time = json.get("time").getAsLong();

        JsonObject obj = new JsonObject();
        //addProperty是添加属性（数值、数组等）；add是添加json对象
        obj.addProperty("key", key);
        obj.addProperty("bucket", bucket);
        obj.addProperty("format", format);
        String filestr = obj.toString();

        logger.info("---alarm---param--"+time+"---"+filestr);

        String tagKey = "stream";     // 标签名称
        String tagValue = stream;  // 标签值
        String field = "file_info";  // 域
        // 添加Long类型数据点
        Datapoint datapoint = new Datapoint()
                .withMetric("metric_video_record")    // 设置Metric
                .withField(field)     // 设置数据点域，可选，不填使用默认域名 value
                .addTag(tagKey, tagValue)    // 设置Tag
                .addStringValue(time, filestr); //添加string类型数据

        return tsdbClient.writeDatapoints(Arrays.asList(datapoint));
    }

    @Override
    public void SaveAlarm(String stream,String payload){

    }

    @Override
    public void SaveMetadata(String stream,String payload){

    }

    @Override
    public Long[][] PlayList(String stream, Long st, Long et){
        // 创建配置
        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY))
                .withEndpoint(ENDPOINT);

        // 初始化一个TsdbClient
        TsdbClient tsdbClient = new TsdbClient(config);


        String metric = "metric_video_record";
        String field1 = "start_time";
        String field2 = "end_time";
//        String field3 = "file_info";
        String tag = "stream";

        // 构造查询对象
        FieldFilter stFilter = new FieldFilter("start_time", ValueFilter.createValueFilter(">=", st));
        FieldFilter etFilter = new FieldFilter("end_time", ValueFilter.createValueFilter("<=", et));
        List<String> fields = Arrays.asList(field1, field2);
        List<Query> queries = Arrays.asList(new Query()             // 创建Query对象
                .withMetric(metric)                                 // 设置metric
                .withFields(fields)                                 // 设置查询的域名列表，不设置表示查询默认域，和field冲突
                .withTags(Arrays.asList(tag))                       // 设置查询的Tag列表，不设置表示不查询，
                .withFilters(new Filters()                          // 创建Filters对象
                        .withRelativeStart("2 days ago")         // 设置相对的开始时间，这里设置为2小时前
                        .withRelativeEnd("1 second ago")        // 设置相对的结束时间，不设置则默认为到当前时间为止
                        .addTag(tag, stream)
                        .withFields(Arrays.asList(stFilter, etFilter)))
                .withLimit(100));


        // 查询数据，返回结果的顺序和请求的field顺序相同
        QueryDatapointsResponse response = tsdbClient.queryDatapoints(queries);

        Long [][] arr = new Long[4][2];
        // 打印结果
        for (Result result : response.getResults()) {
            for (Group group : result.getGroups()) {
                for (GroupInfo groupInfo : group.getGroupInfos()) {
                    System.out.println("\t\tGroupInfo:");
                    System.out.println("\t\t\tName:" + groupInfo.getName());
                }
                System.out.println("\t\tTimeAndValues:");

                try {
                    int a=0;
                    for (Group.TimeAndValue timeAndValue : group.getTimeAndValueList()) {
                        System.out.print("\t\t\t[" + timeAndValue.getTime());
                        for (int index = 0; index < fields.size(); index++) {
                            System.out.print(", ");
//                            ArrayList templist = new ArrayList();
//                            templist.clear();

                            if (!timeAndValue.isNull(index)) {
                                if (timeAndValue.isDouble(index)) {
                                    System.out.print(timeAndValue.getDoubleValue(index));
                                } else if (timeAndValue.isLong(index)) {
                                    System.out.print(timeAndValue.getLongValue(index));
                                    if(index==0 || index==1){
                                        arr[a][index] = timeAndValue.getLongValue(index);
                                    }

                                } else {
                                    System.out.print(timeAndValue.getStringValue(index));

                                }
                            } else {
                                System.out.print("null");
                            }
                        }
                        System.out.println("]");
                        a++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return arr;

    }

}
