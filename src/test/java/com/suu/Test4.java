package com.suu;

import com.su.PlateFormApplication;
import com.su.mapper.TalkMapper;
import com.su.pojo.Talk;
import com.su.service.impl.TalkServiceImp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(classes = PlateFormApplication.class)
@RunWith(SpringRunner.class)
public class Test4 {

    @Autowired
    private TalkMapper talkMapper;

    @Test
    public void test1(){
        String[] title={"Maven","java","spring","nacos","springboot",
        "rabbitmq","JVM","springMVC","redis","mysql","python"};
        String[] t2={"怎么学","应该这样学","学不会","知识点讲解","精讲","源码分析"};
        Random random=new Random();
        for (int i = 0; i < 500; i++) {
            Talk talk = new Talk();
            talk.setTitle(title[random.nextInt(title.length)]+t2[random.nextInt(t2.length)]+random.nextInt(100000));
            talk.setContent("[{\"content\":\"14354555555555555555555555555555jav3443\",\"type\":\"text\"},{\"content\":\"http://43.139.206.205:8888/group1/M00/00/00/rBEAB2QBs1-AF7udAAFPNDsfH2E021.jpg\",\"type\":\"image\"}]");
            talk.setImgUrl("http://43.139.206.205:8888/group1/M00/00/00/rBEAB2QBs2uATEmdAARMvmRme5w673.png");
            talk.setViewNum(random.nextInt(30000));
            talk.setPraiseNum(random.nextInt(1000));
            talk.setType(2);
            talk.setCommnetNum(random.nextInt(10));
            talk.setSubjectId(random.nextInt(15)+4);
            talk.setUserId(4);
            talk.setVersion(1);
            talk.setCollectNum(0);
            talk.setCreateTime(new Date());
            talk.setUpdateTime(new Date());

            talkMapper.insert(talk);


        }

    }
}
