package com.zbjct.dajiujiu.socks.basics.config;

import com.zbjct.dajiujiu.socks.basics.dict.base.DictHelper;
import com.zbjct.dajiujiu.socks.basics.exception.ExceptionHandler;
import com.zbjct.dajiujiu.socks.basics.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ApplicationRunner implements CommandLineRunner,ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.setApplicationContext(applicationContext);
    }

    @Override
    public void run(String... args) {
        DictHelper.init();//初始化字典
        log.info(MESSAGE);
    }

    @Bean
    public ExceptionHandler exceptionHandler() {
        return new ExceptionHandler();
    }

    private final static String MESSAGE="\n" +
            " __    __   _______  __       __        ______      ____    __    ____  ______   .______       __       _______   __  \n" +
            "|  |  |  | |   ____||  |     |  |      /  __  \\     \\   \\  /  \\  /   / /  __  \\  |   _  \\     |  |     |       \\ |  | \n" +
            "|  |__|  | |  |__   |  |     |  |     |  |  |  |     \\   \\/    \\/   / |  |  |  | |  |_)  |    |  |     |  .--.  ||  | \n" +
            "|   __   | |   __|  |  |     |  |     |  |  |  |      \\            /  |  |  |  | |      /     |  |     |  |  |  ||  | \n" +
            "|  |  |  | |  |____ |  `----.|  `----.|  `--'  |       \\    /\\    /   |  `--'  | |  |\\  \\----.|  `----.|  '--'  ||__| \n" +
            "|__|  |__| |_______||_______||_______| \\______/         \\__/  \\__/     \\______/  | _| `._____||_______||_______/ (__)";
}
