package com.wx.springmvc.handlerAdapter;

import com.wx.springmvc.annotation.Service;
import com.wx.springmvc.argumentResolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service("wxHandlerAdapter")
public class wxHandlerAdapter implements HandlerAdapter{

    @Override
    public Object[] hand(HttpServletRequest request,
                         HttpServletResponse response,
                         Method method,
                         Map<String, Object> beans) {

        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];

        Map<String,Object> argumentResolvers = getBeansOfType(beans,
                ArgumentResolver.class);
        int paramIndex = 0;
        int i = 0;
        for(Class<?> paramClazz : paramClazzs){
            for(Map.Entry<String,Object> entry : argumentResolvers.entrySet()){
                ArgumentResolver ar = (ArgumentResolver) entry.getValue();
                if(ar.support(paramClazz,paramIndex,method)){
                    args[i++] = ar.argumentResolver(request,
                            response,
                            paramClazz,
                            paramIndex,
                            method);
                }
            }
            paramIndex++;
        }
        return args;
    }

    private Map<String, Object> getBeansOfType(Map<String, Object> beans,
                                               Class<?> intfType) {
        Map<String,Object> resultBeans = new HashMap<>();

        for(Map.Entry<String,Object> entry : beans.entrySet()){
            Class<?>[] intfs =
                    entry.getValue().getClass().getInterfaces();
            //O（n）遍历找到对应注解
            if(intfs != null && intfs.length > 0){
                for (Class<?> intf : intfs){
                    if(intf.isAssignableFrom(intfType)){
                        resultBeans.put(entry.getKey(),entry.getValue());
                    }
                }
            }
        }
        return resultBeans;
    }
}
