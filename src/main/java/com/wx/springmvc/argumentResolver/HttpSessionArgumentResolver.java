package com.wx.springmvc.argumentResolver;

import com.wx.springmvc.annotation.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

@Service("httpSessionArgumentResolver")
public class HttpSessionArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type, int paramIndex, Method method) {
        return HttpSession.class.isAssignableFrom(type);
    }

    @Override
    public Object argumentResolver(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Class<?> type,
                                   int paramIndex,
                                   Method method) {
        return request.getSession();
    }
}
