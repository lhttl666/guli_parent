package com.atguigu.staservice.service;

import com.atguigu.staservice.entity.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author lhl
 * @since 2023-02-03
 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {

    void createStaDaily(String day);

    Map<String, Object> getStaDaily(String type, String begin, String end);
}
