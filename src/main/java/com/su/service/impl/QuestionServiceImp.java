package com.su.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.ResultBean;
import com.su.ga.GeneticAlgorithm;
import com.su.ga.factory.PaperFactory;
import com.su.ga.gene.PaperConfig;
import com.su.ga.gene.PaperIndividual;
import com.su.mapper.PaperMapper;
import com.su.mapper.SubjectMapper;
import com.su.pojo.Paper;
import com.su.pojo.Question;
import com.su.mapper.QuestionMapper;
import com.su.pojo.Subject;
import com.su.service.QuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.CommonValueUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-03-09
 */
@Service
public class QuestionServiceImp extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    //组卷
    @Override
    public ResultBean getPaper(Integer subjectId, Double difficulty,Integer userId) {
        //根据subjectId获取所有二级标题
        List<Subject> subjectList = subjectMapper.
                selectList(Wrappers.<Subject>lambdaQuery().eq(Subject::getParentId, subjectId));
        //处理subjectList，保留id字段
        List<Integer> subjectIdList = subjectList.stream().
                map(Subject::getId).
                collect(Collectors.toList());
        //查询数据库题库
        //单选题
        List<Question> singleList= getQuestionList(1,subjectIdList);
        //多选题
        List<Question> multipleList = getQuestionList(2,subjectIdList);
        //判断题题库
        List<Question> decideList = getQuestionList(3,subjectIdList);
        //面试题
        List<Question> auditionList = getQuestionList(4, subjectIdList);

        //进化算法配置类，工具类
        PaperConfig paperConfig=new PaperConfig(20,0.2);
        paperConfig.setExpectDifficulty(difficulty);//设置用户期望值
        PaperFactory paperFactory=new
                PaperFactory(singleList,multipleList,decideList,auditionList);

        //试卷不断获取最有解
        GeneticAlgorithm geneticAlgorithm=new GeneticAlgorithm(paperConfig,paperFactory);
        geneticAlgorithm.searchBestPaper();
        //返回最优解
        PaperIndividual resultPaper = geneticAlgorithm.getResultPaper();

        //试卷保存到数据库
        Paper paper = new Paper();
        paper.setUserId(userId);
        paper.setKpNum(resultPaper.getKpNum());
        paper.setStatus(1);
        int d=difficulty<1.5?1:2;
        if (difficulty>=2.1){
            d=3;
        }
        paper.setDifficulty(d);
        paper.setQuestion(JSON.toJSONString(resultPaper.getQuestionList()));
        paperMapper.insert(paper);
        //试卷缓存到redis
        redisTemplate.opsForValue().set(CommonValueUtils.paper+paper.getId(),paper,
                3*60, TimeUnit.SECONDS);
        return ResultBean.success().data(paper.getId());
    }

    //获取各类型题解
    public List<Question> getQuestionList(int type,List<Integer> subjectIdList){
        return baseMapper.selectList(Wrappers.<Question>lambdaQuery()
        .eq(Question::getType,type).in(Question::getSubjectId,subjectIdList));
    }

    //获取试卷
    @Override
    public ResultBean getPaperById(Integer paperId) {
        String key=CommonValueUtils.paper+paperId;
        //缓存中查找试卷
        Paper paper = (Paper) redisTemplate.opsForValue().get(key);
        if (paper==null){
            paper= paperMapper.selectById(paperId);
        }
        //数据处理
        List<Question> questionList = JSONArray.parseArray(paper.getQuestion(), Question.class);
        questionList.forEach(question -> {
            if (question.getType()!=4){
                String[] split = question.getSelectOptions().split(",");
                question.setOptions(split);
            }
        });
        Map<String,Object> map=new HashMap<>();
        map.put("paper",paper);
        map.put("questionList",questionList);
        return ResultBean.success().data(map);
    }

}
