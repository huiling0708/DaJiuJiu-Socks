package com.zbjct.dajiujiu.socks.basics.shiro;

import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;
import com.zbjct.dajiujiu.socks.basics.dict.Enable;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


public class ShiroRealm extends AuthorizingRealm {


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();
        //验证角色、权限
        return authorizationInfo;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * 账户校验
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户的输入的账号.
        String phone = (String) token.getPrincipal();
        //获取用户
        UserInfo userInfo = JpaWrapper.create(UserInfo.class)
                .where(UserInfo::getPhone, phone)
                .doGetOne();
        if (userInfo == null) {
            throw new UnknownAccountException();//用户不存在
        }
        if (!Enable.ENABLE.equals(userInfo.getState())) {
            throw new LockedAccountException(); //用户状态非启用
        }
        return new SimpleAuthenticationInfo(
                userInfo, //用户
                userInfo.getPassword(), //密码
                new CustomByteSource(userInfo.getCredentialsSalt()),//盐=username+salt
                getName()
        );
    }
}
