package com.su.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-03-13
 */
@TableName("paper")
@ApiModel(value = "Paper对象", description = "")
public class Paper implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("试卷难度,1.简单，2.中等，3.难")
    @TableField("difficulty")
    private Integer difficulty;

    @ApiModelProperty("试卷题目")
    @TableField("question")
    private String question;

    @ApiModelProperty("知识点数量")
    @TableField("kp_num")
    private Integer kpNum;

    @ApiModelProperty("1.未完成，2.已完成")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("我的测试分数")
    @TableField("scope")
    private Integer scope;

    @ApiModelProperty("我的答案，以逗号隔开")
    @TableField("answer")
    private String answer;

      @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getKpNum() {
        return kpNum;
    }

    public void setKpNum(Integer kpNum) {
        this.kpNum = kpNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Paper{" +
        "id=" + id +
        ", userId=" + userId +
        ", difficulty=" + difficulty +
        ", question=" + question +
        ", kpNum=" + kpNum +
        ", status=" + status +
        ", scope=" + scope +
        ", answer=" + answer +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
