package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.Talk;
import com.su.pojo.view.ContentView;
import com.su.pojo.view.TalkView;
import com.su.service.TalkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
@RestController
@RequestMapping("/talk")
@CrossOrigin
public class TalkController {

    @Autowired
    private TalkService talkService;

    @ApiModelProperty("图片上传服务器")
    @PostMapping("/upload")
    public ResultBean upload(MultipartFile file){
        return talkService.upload(file);
    }

    @ApiModelProperty("保存文章内容和标题")
    @PostMapping("/saveTitleAndContent")
    public ResultBean saveTitleAndContent(@RequestBody TalkView talkView){
        return talkService.saveTitleAndContent(talkView);
    }

    @ApiModelProperty("图片删除")
    @PostMapping("deleteImg")
    public ResultBean deleteImg(@RequestBody Map<String,String> map){
        String imgUrl=map.get("imgUrl");
        return talkService.deleteImg(imgUrl);
    }

    @ApiModelProperty("根据id查询文章标题和内容")
    @GetMapping("/getById")
    public ResultBean getById(@RequestParam("textId") Integer textId){
        return talkService.getTalkById(textId);
    }

    @ApiModelProperty("修改文章的内容和标题")
    @PostMapping("updateTalk/{textId}")
    public ResultBean updateTalk(@RequestBody TalkView talkView,
                                 @PathVariable("textId") Integer textId) {
        return talkService.updateTalk(talkView,textId);
    }

    @ApiOperation("文章发布")
    @PostMapping("sendTalk")
    public ResultBean sendTalk(@RequestBody TalkView talk){
        return talkService.sendTalk(talk);
    }

    @ApiOperation("根据文章id查询文章信息")
    @GetMapping("getTalkById/{id}")
    public ResultBean getTalkById(@PathVariable("id")Integer id){
        Talk talk = talkService.getById(id);
        return ResultBean.success().data(talk);
    }

    @ApiOperation("首页文章缓存")
    @GetMapping("getTalkList/{subjectName}")
    public ResultBean getTalkList(@PathVariable("subjectName") String subjectName){
        return talkService.getTalkList(subjectName);
    }

    @ApiOperation("敏感词过滤,搜索词提示查询")
    @GetMapping("filterText")
    public ResultBean filterText(@RequestParam("text") String text ){
        return talkService.filterText(text);
    }



}

