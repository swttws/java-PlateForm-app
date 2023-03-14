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
 * @since 2023-02-20
 */
@TableName("user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("头像url")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty("加密算法用的")
    @TableField("salt")
    private String salt;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("大学名称")
    @TableField("college")
    private String college;

    @ApiModelProperty("学历，1.大专，2本科，3研究生")
    @TableField("educational")
    private Integer educational;

    @ApiModelProperty("毕业年份")
    @TableField("graudate_time")
    private LocalDateTime graudateTime;

    @ApiModelProperty("文章数")
    @TableField("talk_num")
    private Integer talkNum;

    @ApiModelProperty("测试数")
    @TableField("test_num")
    private Integer testNum;

    @ApiModelProperty("解题数")
    @TableField("solving_num")
    private Integer solvingNum;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Integer getEducational() {
        return educational;
    }

    public void setEducational(Integer educational) {
        this.educational = educational;
    }

    public LocalDateTime getGraudateTime() {
        return graudateTime;
    }

    public void setGraudateTime(LocalDateTime graudateTime) {
        this.graudateTime = graudateTime;
    }

    public Integer getTalkNum() {
        return talkNum;
    }

    public void setTalkNum(Integer talkNum) {
        this.talkNum = talkNum;
    }

    public Integer getTestNum() {
        return testNum;
    }

    public void setTestNum(Integer testNum) {
        this.testNum = testNum;
    }

    public Integer getSolvingNum() {
        return solvingNum;
    }

    public void setSolvingNum(Integer solvingNum) {
        this.solvingNum = solvingNum;
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
        return "User{" +
        "id=" + id +
        ", userName=" + userName +
        ", password=" + password +
        ", imgUrl=" + imgUrl +
        ", salt=" + salt +
        ", email=" + email +
        ", college=" + college +
        ", educational=" + educational +
        ", graudateTime=" + graudateTime +
        ", talkNum=" + talkNum +
        ", testNum=" + testNum +
        ", solvingNum=" + solvingNum +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
