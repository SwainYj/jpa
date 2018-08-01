package com.iermu.jpa.controller;

import com.baidubce.services.tsdb.model.WriteDatapointsResponse;
import com.iermu.jpa.service.StreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * user : swain
 */
@RestController
@RequestMapping(value = "stream")
public class StreamController {

    private static Logger logger = LoggerFactory.getLogger(StreamController.class);

    @Autowired
    private StreamService streamService;

    @ResponseBody
    @RequestMapping(value = "camEventHandler", method = RequestMethod.POST)
    public WriteDatapointsResponse camEvent(HttpServletRequest req){
        Map<String, String[]> parameterMap = req.getParameterMap();
        String type = req.getParameter("type");
        String stream = req.getParameter("stream");
        String payload = req.getParameter("payload");

        switch(type){
            case "Record":
                return streamService.SaveRecord(stream,payload);
//                break;
            case "Thumbnail":
                return streamService.SaveThumbnail(stream,payload);
//                break;
//            case "Alarm":
//                return streamService.SaveRecord(stream,payload);
////                break;
//            case "MetaData":
//                return streamService.SaveRecord(stream,payload);
////                break;
                default:
                    return streamService.SaveRecord(stream,payload);
        }


//        logger.info("---user---param--"+type);
//        Object result = new CamEventResult();
//        return result;

    }

    @ResponseBody
    @RequestMapping(value = "playlist", method = RequestMethod.GET)
    public Long[][] PlayList(HttpServletRequest req){
        Map<String, String[]> parameterMap = req.getParameterMap();
        String stream = req.getParameter("stream");
        String st = req.getParameter("st");
        String et = req.getParameter("et");

        Long start_time = Long.parseLong(st);
        Long end_time = Long.parseLong(et);

        Long[][] result = streamService.PlayList(stream,start_time,end_time);
        return result;

    }

}

//class CamEventResult{
//    public int request_id=1;
//    public int code=0;
//    public String msg="success";
//}
