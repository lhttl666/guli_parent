package com.atguigu.orderservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.orderservice.service.TPayLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author lhl
 * @since 2023-02-03
 */
@Api(description = "支付管理")
@RestController
@RequestMapping("/orderservice/paylog")
@CrossOrigin
public class TPayLogController {
    @Autowired
    private TPayLogService payLogService;

    @ApiOperation(value = "根据订单编号生成支付二维码")
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo) {
        Map<String, Object> map = payLogService.createNative(orderNo);
        return R.ok().data(map);
    }

    @ApiOperation(value = "根据订单编号查询支付状态")
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo){
        //1调用微信接口查询支付状态
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        //2判断支付状态
        if(map==null){
            return R.error().message("支付出错");
        }
        if("SUCCESS".equals(map.get("trade_state"))){
            //3 支付成功后，更新订单状态，记录支付日志
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中");

    }

}


