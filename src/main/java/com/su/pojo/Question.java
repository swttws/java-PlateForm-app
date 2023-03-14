package com.su.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-03-09
 */
@TableName("question")
@ApiModel(value = "Question对象", description = "")
@Data
@ToString
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("题目")
    @TableField("title")
    private String title;

    @ApiModelProperty("选项，以逗号隔开")
    @TableField("select_options")
    private String selectOptions;

    @ApiModelProperty("答案")
    @TableField("answer")
    private String answer;

    @ApiModelProperty("解析")
    @TableField("analysis")
    private String analysis;

    @ApiModelProperty("分数")
    @TableField("scope")
    private Integer scope;

    @ApiModelProperty("类型")
    @TableField("subject_id")
    private Integer subjectId;

    @ApiModelProperty("题型，1单旋，2.多选，3.判断,4面试题，5.填空题")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("难度，三个等级")
    @TableField("difficulty")
    private Integer difficulty;

    @TableField(exist = false)
    @ApiModelProperty("选项")
    private String[] options;



}
