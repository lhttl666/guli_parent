package com.atguigu.orderservice.service;

import com.atguigu.orderservice.entity.TOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author lhl
 * @since 2023-02-03
 */
public interface TOrderService extends IService<TOrder> {

    String createOrder(String courseId, String memberId);
}
