package com.su.pojo.view;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TalkView implements Serializable {

    //image为图片，text为内容
    private List<ContentView> contentViewList;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("文章标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("1.自己可看 2.全部人可看")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("标题图片url，只有一个图片")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty("文章作者id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("文章类型id")
    @TableField("subject_id")
    private Integer subjectId;

    //发布时间
    private Date sendTime;
}
