package com.zbjct.dajiujiu.socks.basics.shiro;

import com.alibaba.fastjson.JSONObject;
import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 权限过滤
 */
public class ShiroAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ResultVo resultVO = ResultVo.error(ResultCode.U00001);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/html;charset=utf-8");
        httpServletResponse.setStatus(401);
        PrintWriter out = response.getWriter();
        out.print(JSONObject.toJSONString(resultVO));
        out.flush();
        out.close();
        return false;
    }


    public static boolean isAjax(ServletRequest request) {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
