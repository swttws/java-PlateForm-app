package com.su.listener;

import com.su.config.RabbitmqConfig;

import com.su.mapper.SubjectMapper;
import com.su.mapper.TalkMapper;
import com.su.pojo.Subject;
import com.su.pojo.Talk;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

//监听消息，消息延迟队列中的消息
@Component
public class ArticleConsumer {

    @Autowired
    private TalkMapper talkMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //文章发布定时任务
    @RabbitListener(queues = RabbitmqConfig.DELAY_QUEUE)
    public void articleSend(Integer id){
        //查询文章id
        Talk talk = talkMapper.selectById(id);
        //将version修改为1
        talk.setVersion(1);
        talkMapper.updateById(talk);
        //获取标题名称
        Subject subject = subjectMapper.selectById(talk.getSubjectId());
        //获取父标题
        Subject subjectParent = subjectMapper.selectById(subject.getParentId());
        //获取redis中的数据
        List<Talk> talkList = (List<Talk>) redisTemplate.opsForValue().get(subjectParent.getTitle());
        //缓存删除
        if (talkList!=null){
            redisTemplate.delete(subjectParent.getTitle());
        }
    }

}
