package com.zbjct.dajiujiu.socks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
public class DaJiuJiuSocksApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaJiuJiuSocksApplication.class, args);
    }

}
