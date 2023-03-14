package com.su.service;

import com.su.common.ResultBean;
import com.su.pojo.Talk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.su.pojo.view.TalkView;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
public interface TalkService extends IService<Talk> {

    ResultBean upload(MultipartFile file);

    ResultBean saveTitleAndContent(TalkView talkView);

    ResultBean deleteImg(String imgUrl);

    ResultBean getTalkById(Integer textId);

    ResultBean updateTalk(TalkView talkView, Integer textId);

    ResultBean sendTalk(TalkView talk);

    ResultBean getTalkList(String subjectName);

    ResultBean filterText(String text);

    ResultBean insertElasticSearch();

    ResultBean updateElasticSearch();
}
