package com.su.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.lang.annotation.Documented;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.completion.Completion;


/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
@TableName("talk")
@ApiModel(value = "Talk对象", description = "")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "talk")
public class Talk implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Field(type = FieldType.Text)
    @ApiModelProperty("文章标题")
    @TableField("title")
    private String title;

    @Field(type = FieldType.Text,store = true)
    @ApiModelProperty("文章内容,image为图片，content为内容，以逗号隔开")
    @TableField("content")
    private String content;

    @ApiModelProperty("标题图片url，只有一个图片")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty("浏览数")
    @TableField("view_num")
    private Integer viewNum;

    @ApiModelProperty("点赞数")
    @TableField("praise_num")
    private Integer praiseNum;

    @ApiModelProperty("1.自己可看 2.全部人可看")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("评论数")
    @TableField("commnet_num")
    private Integer commnetNum;

    @ApiModelProperty("收藏数量")
    @TableField("collect_num")
    private Integer collectNum;

    @ApiModelProperty("文章类型id")
    @TableField("subject_id")
    private Integer subjectId;

    @ApiModelProperty("文章作者id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("数据版本控制")
    @TableField("version")
    private Integer version;

    @Field(type = FieldType.Date)
    @ApiModelProperty("最初发布日期")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @Field(type = FieldType.Date)
    @ApiModelProperty("最近更新发布日期")
      @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    //搜索词提示
    @CompletionField( maxInputLength = 400)
    @TableField(exist = false)
    private Completion suggest;

    @Transient
    @ApiModelProperty("用户名")
    @TableField(exist = false)
    private String userName;

    @Transient
    @ApiModelProperty("类型名称")
    @TableField(exist = false)
    private String subjectName;

    @Transient
    @ApiModelProperty("大学·")
    @TableField(exist = false)
    private String college;


}
