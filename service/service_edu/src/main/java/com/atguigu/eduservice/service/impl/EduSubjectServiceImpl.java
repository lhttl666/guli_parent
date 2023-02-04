package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.baseservice.handler.GuliException;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.ExcelSubjectData;
import com.atguigu.eduservice.entity.subject.OneSubjectVo;
import com.atguigu.eduservice.entity.subject.TwoSubjectVo;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author lhl
 * @since 2023-01-22
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //批量导入课程分类
    @Override
    public void addSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, ExcelSubjectData.class,
                    new SubjectExcelListener(subjectService)).sheet().doRead();

        } catch (IOException e) {
            e.printStackTrace();
            throw new GuliException(20001, "导入课程分类失败");
        }
    }

    @Override
    public List<OneSubjectVo> getAllSubject() {
        QueryWrapper<EduSubject> oneSubjectVo = new QueryWrapper<>();
        oneSubjectVo.eq("parent_id", '0');
        List<EduSubject> oneSubjectlist = baseMapper.selectList(oneSubjectVo);

        QueryWrapper<EduSubject> twoSubjectVo = new QueryWrapper<>();
        twoSubjectVo.ne("parent_id", '0');
        List<EduSubject> twoSubjectlist = baseMapper.selectList(twoSubjectVo);

        List<OneSubjectVo> allList = new ArrayList<>();
        for (int i = 0; i < oneSubjectlist.size(); i++) {
            OneSubjectVo oneSubjectVo1 = new OneSubjectVo();
            BeanUtils.copyProperties(oneSubjectlist.get(i), oneSubjectVo1);
            allList.add(oneSubjectVo1);

            List<TwoSubjectVo> lists = new ArrayList<>();

            for (int j = 0; j < twoSubjectlist.size(); j++) {
//                if (twoSubjectlist.get(j).getParentId().equals(oneSubjectlist.get(i).getId())) {
                if (oneSubjectlist.get(i).getId().equals(twoSubjectlist.get(j).getParentId())) {
                    TwoSubjectVo twoSubjectVo1 = new TwoSubjectVo();
                    BeanUtils.copyProperties(twoSubjectlist.get(j), twoSubjectVo1);
                    lists.add(twoSubjectVo1);
                }
            }
            oneSubjectVo1.setChildren(lists);
        }

        return allList;
    }
}
