package com.zbjct.dajiujiu.socks.business.user;

import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import com.zbjct.dajiujiu.socks.business.user.param.LoginParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class UserService {

    /**
     * 登陆
     *
     * @param loginParam
     * @return
     */
    public UserInfo login(LoginParam loginParam) {

        AuthenticationToken token =
                new UsernamePasswordToken(loginParam.getPhone(), loginParam.getPassword());

        //获取shiro Subject
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            throw new PlatformException(ResultCode.U90001);
        } catch (UnknownAccountException e) {
            throw new PlatformException(ResultCode.U90002);
        } catch (LockedAccountException e) {
            throw new PlatformException(ResultCode.U90003);
        } catch (Exception e) {
            throw new PlatformException(ResultCode.U00000, "登陆失败：" + e.getMessage());
        }
        UserInfo userInfo = (UserInfo) subject.getPrincipal();
        subject.getSession().setAttribute(UserInfo.class.getSimpleName(), userInfo);
        return userInfo;
    }
}
