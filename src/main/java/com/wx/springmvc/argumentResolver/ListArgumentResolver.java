package com.wx.springmvc.argumentResolver;

import com.wx.springmvc.annotation.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("listArgumentResolver")
public class ListArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type,
                           int paramIndex,
                           Method method) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type, int paramIndex, Method method) {
        List list = new ArrayList();
        Map<String,String[]> paramMap = request.getParameterMap();
        for(Map.Entry<String,String[]> entry : paramMap.entrySet()){
            list.add(entry.getValue());
        }
        return list;
    }
}
