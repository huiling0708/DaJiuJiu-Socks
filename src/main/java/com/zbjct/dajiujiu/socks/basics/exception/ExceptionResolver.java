package com.zbjct.dajiujiu.socks.basics.exception;

import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import com.zbjct.dajiujiu.socks.basics.validate.Effective;
import com.zbjct.dajiujiu.socks.basics.validate.Unique;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class ExceptionResolver {

    private final static Set<String> FILTER_VALIDATE_ANN;

    static {
        FILTER_VALIDATE_ANN = new HashSet<>(Arrays.asList(
                Effective.class.getSimpleName(),
                Unique.class.getSimpleName()
//                CheckUser.class.getSimpleName(),
//                CheckCompany.class.getSimpleName()
        ));
    }

    /**
     * 主要拦截 使用@Valid注解校验后的字段的统一处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVo handleException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder builder = null;
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (builder == null) {
                builder = new StringBuilder();
            } else {
                builder.append(",");
            }
            String annName = fieldError.getCodes()[3];
            if (!FILTER_VALIDATE_ANN.contains(annName)) {
                String fieldDescribe = ClassUtils.getFieldDescribe(bindingResult.getTarget().getClass(),
                        fieldError.getField());
                builder.append("[");
                builder.append(fieldDescribe);
                builder.append("]");
            }
            builder.append(fieldError.getDefaultMessage());
        }
        return ResultVo.error(ResultCode.C00001, builder.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVo handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String message = exception.getMessage();
        log.error("参数格式错误:{}", message, exception);
        return ResultVo.error(ResultCode.C00001);
    }

}
