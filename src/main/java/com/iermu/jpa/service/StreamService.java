package com.iermu.jpa.service;

import com.baidubce.services.tsdb.model.WriteDatapointsResponse;
import org.springframework.stereotype.Service;

@Service
public interface StreamService {
    WriteDatapointsResponse SaveRecord(String stream, String payload);
    WriteDatapointsResponse SaveThumbnail(String stream,String payload);
    void SaveAlarm(String stream,String payload);
    void SaveMetadata(String stream,String payload);

    Long[][] PlayList(String stream, Long st, Long et);
}
