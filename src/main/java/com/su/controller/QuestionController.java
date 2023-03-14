package com.su.controller;


import com.su.common.ResultBean;
import com.su.service.QuestionService;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-03-09
 */
@RestController
@RequestMapping("/question")
@CrossOrigin
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @ApiOperation("根据学科，难度组卷")
    @GetMapping("/getPaper/{subjectId}/{difficulty}/{userId}")
    public ResultBean getPaper(@PathVariable("subjectId") Integer subjectId,
                               @PathVariable("difficulty") Double difficulty,
                               @PathVariable("userId") Integer userId){
        return questionService.getPaper(subjectId,difficulty,userId);
    }

    @ApiOperation("获取试卷")
    @GetMapping("getPaperById/{paperId}")
    public ResultBean getPaperById(@PathVariable Integer paperId){
        return questionService.getPaperById(paperId);
    }

}











