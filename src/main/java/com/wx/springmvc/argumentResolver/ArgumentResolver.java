package com.wx.springmvc.argumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface ArgumentResolver {

    boolean support(Class<?> type, int paramIndex, Method method);

    Object argumentResolver(HttpServletRequest request,
                            HttpServletResponse response,
                            Class<?> type,
                            int paramIndex,
                            Method method);
}
