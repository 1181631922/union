package com.education.union.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.education.union.dao.LoginMapper;
import com.education.union.service.LoginService;
import com.education.union.util.Base64Util;
import com.education.union.util.CommonUtil;
import com.education.union.util.MD5Transfer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author： fanyafeng
 * Data： 2019-06-18 18:59
 * Email: fanyafeng@live.cn
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private LoginMapper loginMapper;

    /**
     * 登录表单提交
     *
     * @param jsonObject
     * @return
     */
    @Override
    public JSONObject authLogin(JSONObject jsonObject) {
        String mobile = jsonObject.getString("mobile");
        String originPassword = jsonObject.getString("password");
        String password= MD5Transfer.MD5(originPassword);
        JSONObject data = new JSONObject();
        Subject currentUser = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(mobile, password);
        try {
            currentUser.login(token);
            data.put("result", "success");
            JSONObject token1 = loginMapper.getToken(mobile, password);
            Integer userId = token1.getIntValue("userId");
            if (userId < 10) {
                token1.put("userId", Base64Util.encode("0" + userId.toString()));
            } else {
                token1.put("userId", Base64Util.encode(userId.toString()));
            }
            token1.put("userToken", Base64Util.encodeToken(token1.getString("userToken")));
            data.put("userInfo", token1);

//            data.put("token", currentUser.getSession().getId());
//            Session session = currentUser.getSession();
//            session.setTimeout(25000);
//            session.setAttribute("currentUser", mobile);
        } catch (AuthenticationException e) {
//            e.printStackTrace();
            data.put("result", "fail");
        }
        return CommonUtil.successJson(data);
    }

    /**
     * 根据手机号和密码获取对应用户
     *
     * @param mobile
     * @param password
     */
    @Override
    public JSONObject getUser(String mobile, String password) {
        return loginMapper.getUser(mobile, password);
    }

    /**
     * 退出登录
     */
    @Override
    public JSONObject logout() {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.logout();
        } catch (Exception e) {
        }
        return CommonUtil.successJson();
    }


}
