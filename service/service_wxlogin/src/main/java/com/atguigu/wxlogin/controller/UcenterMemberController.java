package com.atguigu.wxlogin.controller;


import com.atguigu.baseservice.handler.GuliException;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.wxlogin.entity.UcenterMember;
import com.atguigu.wxlogin.service.UcenterMemberService;
import com.atguigu.wxlogin.utils.ConstantPropertiesUtil;
import com.atguigu.wxlogin.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaWsdlMappingType;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2021-03-19
 */
@Api(description="微信登录")
@Controller
@RequestMapping("/api/ucenter/wx")
@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    @GetMapping("login")
    public String wxlogin(){
        //方式1   https://open.weixin.qq.com/connect/qrconnect?
        // appid=APPID&redirect_uri=REDIRECT_URI&
        // response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        //方式2
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址
        String redirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new GuliException(20001, e.getMessage());
        }

        String qrcodeUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUrl,
                "atguiguwxlogin");

        return "redirect:" + qrcodeUrl;

    }

    @GetMapping("callback")
    public String callback(String code, String state){
        //1获取参数code，临时票据
        System.out.println("code="+code);
        System.out.println("state="+state);

        //2拿code，换取access_token、openid
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        String result = null;
        try {
            result =  HttpClientUtils.get(accessTokenUrl);
            System.out.println("result="+result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //2.1解析json
        Gson gson = new Gson();
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String)map.get("access_token");
        String openid = (String)map.get("openid");

        //3 换取用户信息
        //访问微信的资源服务器，获取用户信息
        String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
        "?access_token=%s" +
        "&openid=%s";
        String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
        String resultUserInfo = null;
        try {
            resultUserInfo =  HttpClientUtils.get(userInfoUrl);
            System.out.println("resultUserInfo="+resultUserInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.1解析json
        HashMap userMap = gson.fromJson(resultUserInfo, HashMap.class);
        String nickname = (String)userMap.get("nickname");
        String headimgurl = (String)userMap.get("headimgurl");

        //4根据openid查询用户
        QueryWrapper<UcenterMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        UcenterMember member = memberService.getOne(queryWrapper);
        //5判断用户是否存在，用户不存在，走注册
        if(member==null){
            member = new UcenterMember();
            member.setNickname(nickname);
            member.setAvatar(headimgurl);
            member.setOpenid(openid);
            memberService.save(member);
        }
        String token = JwtUtils.getJwtToken(member.getId(),member.getNickname());

        return "redirect:http://localhost:3000?token="+token;
    }








}

