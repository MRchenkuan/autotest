package com.gigold.pay.autotest.controller;

import javax.servlet.http.HttpSession;

import com.gigold.pay.autotest.bo.UserInfo;
import com.gigold.pay.autotest.reqDto.UserIReqDto;
import com.gigold.pay.autotest.rspDto.UserResDto;
import com.gigold.pay.autotest.service.UserInfoService;
import com.gigold.pay.framework.web.constant.SessionConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.autotest.util.Constant;


@Controller
@RequestMapping("/")
public class UserController extends BaseController {
    @Autowired
    UserInfoService userInfoService;

    /**
     * @return the userInfoService
     */
    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    /**
     * @param userInfoService the userInfoService to set
     */
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @RequestMapping(value = "/login.do")
    public @ResponseBody UserResDto login(@RequestBody UserIReqDto rdto, HttpSession sessin) {
        UserResDto dto = new UserResDto();
        // 验证请求参数合法性
        String code = rdto.validation();
        // 没有通过则返回对应的返回码
        if (!"00000".equals(code)) {
            dto.setRspCd(code);
            return dto;
        }
        UserInfo user = userInfoService.login(rdto.getUserInfo());
        if (user == null) {
            dto.setRspCd(CodeItem.IF_FAILURE);
        } else {
            sessin.setAttribute(Constant.LOGIN_KEY, user);
            // 新版本登录
            sessin.setAttribute(SessionConstant.GIGOLD_AUTH, Constant.SESSION_VALUE_GIGOLD_AUTH);
            sessin.setAttribute(SessionConstant.GIGOLD_USR_NO, user.getId());

            dto.setUserInfo(user);
            dto.setRspCd(SysCode.SUCCESS);
        }

        return dto;
    }

}
