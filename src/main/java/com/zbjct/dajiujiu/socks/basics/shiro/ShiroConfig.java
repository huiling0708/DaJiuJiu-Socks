package com.zbjct.dajiujiu.socks.basics.shiro;

import com.zbjct.dajiujiu.socks.basics.query.processor.QueryLoadProcessor;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Set;

/**
 * shiro 配置
 */
@Configuration
public class ShiroConfig {


    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        chainDefinition.addPathDefinition("/user/logout", "logout");
        chainDefinition.addPathDefinition("/user/login", "loginUrl");
        chainDefinition.addPathDefinition("/user/login", "anon");
        chainDefinition.addPathDefinition("/user/logout", "anon");
        chainDefinition.addPathDefinition("/user/reg/company", "anon");

        chainDefinition.addPathDefinition("/hello", "anon");
        chainDefinition.addPathDefinition("/query/page/**", "anon");
        chainDefinition.addPathDefinition("/query/list/**", "anon");
        chainDefinition.addPathDefinition("/query/single/**", "anon");


        chainDefinition.addPathDefinition("/doc.html**", "anon");
        chainDefinition.addPathDefinition("/swagger-ui.html**", "anon");
        chainDefinition.addPathDefinition("/v2/api-docs", "anon");
        chainDefinition.addPathDefinition("/swagger-resources/**", "anon");
        chainDefinition.addPathDefinition("/webjars/**", "anon");

        chainDefinition.addPathDefinition("/**", "user");
//        chainDefinition.addPathDefinition("/**", "authc");
//        chainDefinition.addPathDefinition("/**", "anon");

        return chainDefinition;
    }


    /**
     * 凭证匹配
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法MD5
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数
        return hashedCredentialsMatcher;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        return securityManager;
    }

    /**
     * 自定义权限与校验
     *
     * @return
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return shiroRealm;
    }

    @Bean
    public static FormAuthenticationFilter shiroFormAuthenticationFilter() {
        return new ShiroAuthenticationFilter();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    @ConditionalOnMissingBean
    public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
}
