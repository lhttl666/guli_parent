package com.atguigu.eduservice.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 86178
 */
@Data
public class ChapterVo {

    private String id;

    private String title;

    private List<VideoVo> children =new ArrayList<>();

}
