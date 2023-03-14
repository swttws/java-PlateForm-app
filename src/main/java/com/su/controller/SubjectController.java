package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.Subject;
import com.su.service.SubjectService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import jdk.internal.util.xml.impl.ReaderUTF8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
@RestController
@RequestMapping("/subject")
@CrossOrigin
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @ApiModelProperty("查询一级标题一级对应的二级标题")
    @GetMapping("getOneAndTwo")
    public ResultBean getOneAndTwo(){
        return subjectService.getOneAndTwo();
    }

    @ApiOperation("获取所有一级标题")
    @GetMapping("getAllOneSubject")
    public ResultBean getAllOneSubject(){
       List<Subject> subjects=subjectService.getAllOneSubject();
       return ResultBean.success().data(subjects);
    }
}

