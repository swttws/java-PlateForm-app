package com.su.pojo.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneTitleView implements Serializable {
    private String text;
    private Integer value;
    //二级标题
    private List<TwoTitleView> children;
}
