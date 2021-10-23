package com.zbjct.dajiujiu.socks.business.user;

import com.zbjct.dajiujiu.socks.basics.dict.Enable;
import com.zbjct.dajiujiu.socks.business.user.entity.CompanyInfo;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import com.zbjct.dajiujiu.socks.business.user.param.RegCompanyParam;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司服务
 */
@Service
public class CompanyService {

    /**
     * 公司注册
     *
     * @param param
     */
    @Transactional
    public void regCompany(RegCompanyParam param) {
        //保存
        CompanyInfo company = new CompanyInfo();
        company.setName(param.getCompanyName());
        company.createWrapper().doSave();

        UserInfo user = new UserInfo();
        user.setName(param.getUserName());
        user.setCompanyId(company.getCompanyId());
        user.setState(Enable.ENABLE);
        user.setPhone(param.getPhone());
        user.setSalt(new SecureRandomNumberGenerator().nextBytes().toHex());
        //密码处理
        String newPassword = new SimpleHash("md5", param.getPassword(),
                ByteSource.Util.bytes(user.getCredentialsSalt()), 2).toHex();
        user.setPassword(newPassword);
        user.createWrapper().doSave();
    }
}
