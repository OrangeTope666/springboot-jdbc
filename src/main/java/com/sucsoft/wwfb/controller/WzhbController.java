package com.sucsoft.wwfb.controller;

import com.sucsoft.wwfb.model.Xzqy;
import com.sucsoft.wwfb.service.NewWwfbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/wzhb")
public class WzhbController {

    @Autowired
    private NewWwfbService wwfbService;


    @RequestMapping(value = "/getXzqy", method = RequestMethod.GET)
    //(value = "行政区域", notes = "获取行政区域数据")
    public List<Xzqy> getXzqy() {
        return wwfbService.getXzqy();
    }

    @RequestMapping(value="/getPageList",method = RequestMethod.GET)
    //(value = "获取所有站点 记录分页列表", notes = "获取所有站点 记录分页列表 notes")
    public Map<String, Object> getPageList(
            @RequestParam(defaultValue = "1") String wrlx,
            @RequestParam(defaultValue = "") String xzqy,
            @RequestParam(defaultValue = "") String time,
            @RequestParam(defaultValue = "") String qymc,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ){

        return wwfbService.getList(wrlx,xzqy,time,qymc,pageNo, pageSize);
    }
}
