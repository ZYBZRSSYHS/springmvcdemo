package com.wx.springmvc.controller;

import com.wx.springmvc.annotation.Controller;
import com.wx.springmvc.annotation.Qualifier;
import com.wx.springmvc.annotation.RequestMapping;
import com.wx.springmvc.annotation.RequestParam;
import com.wx.springmvc.service.wxService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wx")
public class wxController {

    @Qualifier("wxServiceImpl")
    private wxService wxService;

    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      HttpSession session, Map map,
                      @RequestParam("name") String userName, List list){
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = wxService.query(null);
        assert pw != null;
        pw.write(result);
    }


    @RequestMapping("/insert")
    public void insert(HttpServletRequest request, HttpServletResponse response,
                       HttpSession session,String param){
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = wxService.insert(param);
        pw.write(result);
    }

    @RequestMapping("/update")
    public void update(HttpServletRequest request, HttpServletResponse response,
                       String param){
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = wxService.update(param);
        pw.write(result);
    }

}
