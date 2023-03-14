package com.su.service;

import com.su.common.ResultBean;
import com.su.pojo.Subject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
public interface SubjectService extends IService<Subject> {

    ResultBean getOneAndTwo();

    List<Subject> getAllOneSubject();
}
