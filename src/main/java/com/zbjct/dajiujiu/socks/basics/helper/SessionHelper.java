package com.zbjct.dajiujiu.socks.basics.helper;


import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Session帮助
 */
@Slf4j
public abstract class SessionHelper {


    /**
     * 获取当前用户
     *
     * @return
     */
    public static UserInfo getUser() {
        UserInfo user = null;
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null && subject.isAuthenticated()) {
                user = (UserInfo) subject.getSession().getAttribute(UserInfo.class.getSimpleName());
            }
        } catch (Exception e) {
            log.info("获取用户信息失败", e);
            user = null;
        }
        if (user == null) {
            throw new PlatformException(ResultCode.U00001);
        }
        return user;
    }

    /**
     * 获取用户id
     *
     * @return
     */
    public static String getUserId() {
        return getUser().getUserId();
    }


    /**
     * 获取用户名称
     *
     * @return
     */
    public static String getUserName() {
        return getUser().getName();
    }

    /**
     * 获取当前登录公司
     *
     * @return
     */
    public static String getCompanyId() {
        return getUser().getCompanyId();
    }

    /**
     * 获取用户id 异常或空时 返回空（该方法只在特定情况下使用）
     *
     * @return
     */
    public static String getUserIdNotCheck() {
        UserInfo user;
        try {
            user = getUser();
        } catch (Exception e) {
            return null;
        }
        if (user == null) {
            return null;
        }
        return user.getUserId();
    }
}