package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 86178
 */
@Component
public class VodFileDegradeFeignClient implements VodClient {
    @Override
    public R delVideo(String videoId) {
        return R.ok().message("删除失败，兜底结果");
    }

    @Override
    public R delVideoList(List<String> videoIdList) {
        return R.ok().message("删除失败，兜底结果");
    }
}
