package com.su.service;

import com.su.common.ResultBean;
import com.su.pojo.Question;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-03-09
 */
public interface QuestionService extends IService<Question> {
    //获取试卷
    ResultBean getPaper(Integer subjectId,Double difficulty,Integer userId);

    ResultBean getPaperById(Integer paperId);
}
