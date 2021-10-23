package com.zbjct.dajiujiu.socks.basics.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 全局异常处理器
 */
@Slf4j
public class ExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception e) {
        ModelAndView mv = new ModelAndView();
        FastJsonJsonView view = new FastJsonJsonView();
        Attributes attributes = new Attributes();
        log.error("ExceptionHandler拦截:", e);
        if (e instanceof PlatformException) {
            attributes.error(((PlatformException) e).getResultCode().name(), e.getMessage());
        } else {
            attributes.error(ResultCode.E99999);
        }
        view.setAttributesMap(attributes);
        mv.setView(view);
        return mv;
    }

    private class Attributes extends HashMap<String, String> {
        private void error(ResultCode resultCode) {
            this.error(resultCode.name(), resultCode.getMessage());
        }

        private void error(String code, String message) {
            this.put("code", code);
            this.put("message", message);
        }
    }
}
