package com.atguigu.eduservice.service.impl;

import com.atguigu.baseservice.handler.GuliException;
import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.CourseInfoForm;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.entity.vo.CourseWebVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author lhl
 * @since 2023-01-24
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;

    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private EduVideoService videoService;
    @Autowired
    private VodClient vodClient;


    //添加课程信息

    @Override
    public String addCourseInfo(CourseInfoForm courseInfoForm) {
        //1添加课程信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm, eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if (insert == 0) {
            throw new GuliException(20001, "创建课程失败");
        }
        //2获取课程id
        String courseId = eduCourse.getId();
        //3添加课程描述信息
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseId);
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescriptionService.save(courseDescription);

        return courseId;
    }

    //根据id课程信息
    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        //1根据id查询课程信息
        EduCourse eduCourse = baseMapper.selectById(id);
        //2封装课程信息
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(eduCourse, courseInfoForm);
        //3根据id查询课程描述信息
        EduCourseDescription courseDescription = courseDescriptionService.getById(id);
        //4封装课程描述
        courseInfoForm.setDescription(courseDescription.getDescription());
        return courseInfoForm;
    }

    @Override
    public void updateCourseInfo(CourseInfoForm courseInfoForm) {
        //1复制课程数据
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm, eduCourse);
        //2更新课程数据
        int update = baseMapper.updateById(eduCourse);
        //3判断是否成功
        if (update == 0) {
            throw new GuliException(20001, "修改课程失败");
        }
        //4更新课程描述
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfoForm.getId());
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    //根据课程id查询课程发布信息

    @Override
    public CoursePublishVo getCoursePublishById(String id) {
        CoursePublishVo coursePublishVo =
                baseMapper.getCoursePublishById(id);
        return coursePublishVo;
    }

    //根据id删除课程相关信息
    @Override
    public void delCourseInfo(String id) {
        //1 删除视频
        //1.1查询相关小节
        QueryWrapper<EduVideo> videoIdWrapper = new QueryWrapper<>();
        videoIdWrapper.eq("course_id", id);
        List<EduVideo> list = videoService.list(videoIdWrapper);
        //1.2遍历获取视频id
        List<String> videoIdList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            EduVideo eduVideo = list.get(i);
            videoIdList.add(eduVideo.getVideoSourceId());
        }
        //1.3判断，调接口
        if (videoIdList.size() > 0) {
            vodClient.delVideoList(videoIdList);
        }
//        //2删除小节
//        QueryWrapper<EduVideo> videoWrapper = new QueryWrapper<>();
//        videoWrapper.eq("course_id", id);
//        videoService.remove(videoWrapper);
//        // 3删除章节
//        QueryWrapper<EduChapter> chapterWrapper = new QueryWrapper<>();
//        chapterWrapper.eq("course_id", id);
//        chapterService.remove(chapterWrapper);
//        // 4删除课程描述
//        courseDescriptionService.removeById(id);
//        // 5删除课程
//        int delete = baseMapper.deleteById(id);
//        if (delete == 0) {
//            throw new GuliException(20001, "删除课程失败");
//        }
    }

    //带条件分页查询课程列表
    @Override
    public Map<String, Object> getCourseApiPageVo(Page<EduCourse> pageParam, CourseQueryVo courseQueryVo) {
        //1 取出查询条件
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();
        String buyCountSort = courseQueryVo.getBuyCountSort();
        String gmtCreateSort = courseQueryVo.getGmtCreateSort();
        String priceSort = courseQueryVo.getPriceSort();
        //2 验空，不为空拼写到查询条件
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("subject_parent_id", subjectParentId);
        }
        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("subject_id", subjectId);
        }
        if (!StringUtils.isEmpty(buyCountSort)) {
            queryWrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(gmtCreateSort)) {
            queryWrapper.orderByDesc("gmt_create");
        }
        if (!StringUtils.isEmpty(priceSort)) {
            queryWrapper.orderByDesc("price");
        }
        queryWrapper.eq("status", "Normal");

        //3 分页查询
        baseMapper.selectPage(pageParam, queryWrapper);
        //4 封装数据
        List<EduCourse> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    //根据课程id查询课程相关信息
    @Override
    public CourseWebVo getCourseWebVo(String id) {
        CourseWebVo courseWebVo = baseMapper.getCourseWebVo(id);
        return courseWebVo;
    }


}
