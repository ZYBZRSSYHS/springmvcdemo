package com.wx.springmvc.service.impl;

import com.wx.springmvc.annotation.Service;
import com.wx.springmvc.service.wxService;

@Service("wxServiceImpl")
public class wxServiceImpl implements wxService {
    @Override
    public String query(String param) {
        return this.getClass().getName() + " query";
    }

    @Override
    public String insert(String param) {
        return this.getClass().getName() + " insert";
    }

    @Override
    public String update(String param) {
        return this.getClass().getName() + " update";
    }
}
