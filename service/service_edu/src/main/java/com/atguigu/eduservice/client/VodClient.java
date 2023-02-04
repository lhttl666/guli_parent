package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 86178
 */
@Component
@FeignClient(name = "service-vod", fallback = VodFileDegradeFeignClient.class)
public interface VodClient {

    @DeleteMapping("/eduvod/video/delVideo/{videoId}")
    public R delVideo(@PathVariable("videoId") String videoId);


    @ApiOperation(value = "批量删除视频")
    @DeleteMapping("/eduvod/video/delVideoList")
    public R delVideoList(@RequestParam("videoIdList") List<String> videoIdList);


}
