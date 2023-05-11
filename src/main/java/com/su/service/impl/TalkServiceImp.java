package com.su.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.imageaudit20191230.Client;
import com.aliyun.imageaudit20191230.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.su.common.Errors;
import com.su.common.ResultBean;
import com.su.config.RabbitmqConfig;
import com.su.es.TalkRepository;
import com.su.pojo.Subject;
import com.su.pojo.Talk;
import com.su.mapper.TalkMapper;
import com.su.pojo.view.Aliyvn;
import com.su.pojo.view.ContentView;
import com.su.pojo.view.TalkView;
import com.su.service.SubjectService;
import com.su.service.TalkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.CommonValueUtils;
import com.su.utils.TrimUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
@Service
@Slf4j
public class TalkServiceImp extends ServiceImpl<TalkMapper, Talk> implements TalkService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private TalkRepository talkRepository;


    //文件上传
    @Override
    public ResultBean upload(MultipartFile file) {
        //校验参数
        if (StringUtils.isEmpty(file)){
            throw new Errors("500","请选择文件上传");
        }
        //检查文件后缀是否符合
        String[] fileName = file.getOriginalFilename().split("\\.");
        String suffix = fileName[fileName.length - 1];
        if (!suffix.equals("jpg")&&
        !suffix.equals("jpeg")&&
        !suffix.equals("png")){
            throw new Errors("500","文件格式只能为jpg，jpeg，png格式");
        }
        //上传文件，返回文件url地址
        StorePath path=null;
        try {
            path=fastFileStorageClient.uploadFile(file.getInputStream(),file.getSize(),
                    suffix,null);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new Errors("500","文件上传失败");
        }
        //检查文件是否上传成功
        if (StringUtils.isEmpty(path)){
            throw new Errors("500","文件上传失败");
        }
        return ResultBean.success().data(
                CommonValueUtils.imgPath+path.getFullPath());
    }

    //保存文章标题和内容，初始化数据
    @Override
    public ResultBean saveTitleAndContent(TalkView talkView) {
        //验证数据合法性
        String title = talkView.getTitle();
        List<ContentView> contentViewList = talkView.getContentViewList();
        if (contentViewList==null||contentViewList.size()==0||
        StringUtils.isEmpty(title)){
            throw new Errors("500","内容或标题不能为空");
        }
        //开启异步执行
        //文章数据内容检验
        CompletableFuture<Boolean> contextWork= null;
        CompletableFuture<Boolean> imageWork=null;
        try {
            contextWork = CompletableFuture
                    .supplyAsync(()->scanContent(contentViewList))
                    .exceptionally((e)->{
                        log.info("文章内容检验异常");
                        throw new RuntimeException("异常");
                    });
            //图片内容检测
            imageWork=CompletableFuture
                    .supplyAsync(()->scanImage(contentViewList))
                    .exceptionally(e->{
                        log.info("图片检测异常");
                        throw new Errors("500","异常");
                    });
        } catch (Exception e) {
            throw new Errors("500","服务器异常");
        }
        //获取返回结果
        boolean scanContext = false;
        boolean scanImage = false;
        try {
            scanContext = contextWork.get();
            scanImage = imageWork.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //检测内容不通过，通知用户修改
        if ((!scanContext)||(!scanImage)){
            throw new Errors("500","内容非法或图片非法，请修改文章");
        }
        //文章保存
        Talk talk=new Talk();
        //内容数据处理
        String content = JSON.toJSONString(contentViewList);
        talk.setContent(content);
        talk.setTitle(title);
        baseMapper.insert(talk);
        return ResultBean.success().data(talk.getId());
    }

    //文章内容审核
    @Autowired
    private Aliyvn aliyvn;
    public boolean scanContent(List<ContentView> contentViewList){
        try {
            //创建客户端
            Config config=new Config().setAccessKeyId(aliyvn.getAccesskeyId())
                    .setAccessKeySecret(aliyvn.getAccesskeySecret())
                    .setEndpoint(aliyvn.getEndpoint());
            Client client=new Client(config);
            //设置检验规范
            ScanTextRequest.ScanTextRequestLabels labels=
                    new ScanTextRequest.ScanTextRequestLabels()
                    .setLabel("spam").setLabel("ad").setLabel("abuse")
                    .setLabel("terrorism").setLabel("porn");
            //刷选文本
            String text = contentViewList.stream()
                    .filter(view -> view.getType().equals("text"))
                    .map(ContentView::getContent)
                    .collect(Collectors.joining());
            //设置检测文本
            ScanTextRequest.ScanTextRequestTasks tasks = new ScanTextRequest.ScanTextRequestTasks()
                    .setContent(text);
            //添加任务进行检查
            ScanTextRequest scanTextRequest=new ScanTextRequest()
                    .setLabels(Arrays.asList(labels))
                    .setTasks(Arrays.asList(tasks));
            //检测返回结果
            ScanTextResponse response = client.scanTextWithOptions(scanTextRequest, new RuntimeOptions());
            //获取检测数据
            List<ScanTextResponseBody.ScanTextResponseBodyDataElements> elements =
                    response.getBody().getData().getElements();
            //true通过检测，false不通过检测
            boolean flag=true;
            for (ScanTextResponseBody.ScanTextResponseBodyDataElementsResults result :
                    elements.get(0).getResults()) {
                if (!result.getSuggestion().equals("pass")){
                    flag=false;
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Errors("500","检测服务异常");
        }
    }

    //图片检测
    public boolean scanImage(List<ContentView> contentViewList){
        try {
            //刷选图片url
            List<String> imgList = contentViewList.stream()
                    .filter(view -> view.getType().equals("image"))
                    .map(ContentView::getContent)
                    .collect(Collectors.toList());
            //没有图片，无需检查
            if (imgList.size()==0){
                return true;
            }
            //创建客户端
            Config config=new Config().setAccessKeyId(aliyvn.getAccesskeyId())
                    .setAccessKeySecret(aliyvn.getAccesskeySecret())
                    .setEndpoint(aliyvn.getEndpoint());
            Client client=new Client(config);
            //创建检测图片任务
            List<ScanImageRequest.ScanImageRequestTask> taskList=new ArrayList<>();
            imgList.forEach(img->{
                taskList.add(new ScanImageRequest.ScanImageRequestTask()
                        .setImageTimeMillisecond(1L)
                        .setInterval(-1)
                        .setImageURL(img));
            });
            //开始图片检测
            ScanImageRequest scanImageRequest = new ScanImageRequest()
                    .setTask(taskList)
                    .setScene(Arrays.asList(
                            "porn","ad","terrorism","live"
                    ));
            //返回检测结果
            boolean flag=true;
            ScanImageResponse scanImageResponse = client.scanImageWithOptions(scanImageRequest, new RuntimeOptions());
            List<ScanImageResponseBody.ScanImageResponseBodyDataResults> results = scanImageResponse.getBody().getData().getResults();
            //图片检测不和格，返回false
            for (ScanImageResponseBody.ScanImageResponseBodyDataResults result : results) {
                if (!result.getSubResults().get(0).getSuggestion().equals("pass")){
                    flag=false;
                    break;
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Errors("500","检测服务异常");
        }
    }

    //删除图片内容
    @Override
    public ResultBean deleteImg(String imgUrl) {
        String url = imgUrl.substring(27);
        fastFileStorageClient.deleteFile(CommonValueUtils.group,url);
        return ResultBean.success();
    }

    //根据id获取文章内容
    @Override
    public ResultBean getTalkById(Integer textId){
        Talk talk = baseMapper.selectById(textId);
        //内容数据处理
        String content = talk.getContent();
        List<ContentView> contentViews = JSONArray.parseArray(content, ContentView.class);
        //封装数据
        TalkView talkView=new TalkView();
        talkView.setTitle(talk.getTitle());
        talkView.setContentViewList(contentViews);
        return ResultBean.success().data(talkView);
    }

    //修改文章内容和标题
    @Override
    public ResultBean updateTalk(TalkView talkView, Integer textId) {
        //查询数据
        Talk talk = baseMapper.selectById(textId);
        List<ContentView> contentViewList = talkView.getContentViewList();
        String title = talkView.getTitle();
        //参数校验
        if (StringUtils.isEmpty(contentViewList)||StringUtils.isEmpty(talk)){
            throw new Errors("500","文章内容不能为空");
        }
        //list转换为json字符串
        String s = JSON.toJSONString(contentViewList);
        talk.setTitle(title);
        talk.setContent(s);
        baseMapper.updateById(talk);
        return ResultBean.success();
    }

    //文章发布，定时发送功能
    @Override
    public ResultBean sendTalk(TalkView talk) {
        //校验参数
        if (StringUtils.isEmpty(talk.getImgUrl())||StringUtils.isEmpty(talk.getSubjectId())
        ||StringUtils.isEmpty(talk.getType())||StringUtils.isEmpty(talk.getSendTime())){
            throw new Errors("500","请将信息填写完整");
        }
        //获取时间差，单位为ms
        Date sendTime = talk.getSendTime();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(sendTime);
        int time= (int) (calendar.getTimeInMillis()-System.currentTimeMillis());
        //向延迟交换机发送延迟消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.DELAY_EXCHANGE, RabbitmqConfig.DELAY_KEY,
                talk.getId(),message -> {
            //设置延迟发送时间
            message.getMessageProperties().setHeader("x-delay",time);
            return message;
        });
        //根据id获取数据库信息
        Talk talkInfo = baseMapper.selectById(talk.getId());
        //数据封装
        talkInfo.setImgUrl(talk.getImgUrl());
        talkInfo.setSubjectId(talk.getSubjectId());
        talkInfo.setUserId(talk.getUserId());
        talkInfo.setType(talk.getType());
        //文章保存数据库（version号在被消费者消费时修改）
        baseMapper.updateById(talkInfo);
        return ResultBean.success();
    }

    //首页文章缓存
    @Override
    public ResultBean getTalkList(String subjectName) {
        //查询redis缓存中是否存在文章缓存
        List<Talk> talkList = (List<Talk>) redisTemplate.opsForValue().get(subjectName);
        //缓存存在
        if (talkList!=null){
            return ResultBean.success().data(talkList);
        }
        //缓存不存在    redis加锁
        //生成UUID标识锁
        String uuid=UUID.randomUUID().toString();
        //加锁
        if (redisTemplate.opsForValue().setIfAbsent(CommonValueUtils.redisLock,uuid,
                10,TimeUnit.SECONDS)){
            try {
                //查询所有一级标题
                List<Subject> oneSubject = subjectService.getAllOneSubject();
                //每个一级标题数据库查询5篇最新讨论
                oneSubject.forEach(subject -> {
                    //关联查询
                    List<Talk> talks=baseMapper.selectTalk(subject.getId());
                    //缓存到redis中
                    Random random=new Random();
                    //设置过期时间不同
                    Integer time=5*60*60+random.nextInt(10000);
                    redisTemplate.opsForValue()
                            .set(subject.getTitle(),talks,time,TimeUnit.SECONDS);
                });
            }finally {
                //判断锁是否还是本线程持有
                if (uuid.equals(redisTemplate.opsForValue().get(CommonValueUtils.redisLock))){
                    //锁释放
                    redisTemplate.delete(CommonValueUtils.redisLock);
                }
            }
        }else{
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //其他请求自旋本方法
        return getTalkList(subjectName);
    }


    @Autowired
    private TrimUtils trimUtils;

    //敏感词过滤，搜索栏,将试用*代替敏感词
    @Override
    public ResultBean filterText(String text) {
        //敏感词过滤
        String newWord = trimUtils.filterWord(text);
        //搜索提示词获取
        List<String> textList = getText(text);
        //数据封装
        Map<String,Object> map=new HashMap<>();
        map.put("newWord",newWord);
        map.put("textList",textList);
        return ResultBean.success().data(map);
    }

    //获取搜索提示词
    public List<String> getText(String text){
        //构造补全对象
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders
                .completionSuggestion("suggest")
                .text(text)
                .skipDuplicates(true)
                .size(8);

        SuggestBuilder suggestBuilder=new SuggestBuilder();
        suggestBuilder.addSuggestion("my-suggest",completionSuggestionBuilder);

        //查询提示词
        SearchResponse suggestResponse =
                elasticsearchRestTemplate.suggest(suggestBuilder, Talk.class);
        //获取suggest提示词
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<?
                extends Suggest.Suggestion.Entry.Option>> suggestions =
                suggestResponse.getSuggest().getSuggestion("my-suggest");
        //结果返回
        return suggestions.getEntries().stream()
                //获取options属性中的text属性
                .map(suggests -> suggests.getOptions().stream().
                        map(option -> option.getText().toString()).collect(Collectors.toList()))
                .findFirst()
                .get();
    }


    //数据库数据插入ElasticSearch
    @Override
    public ResultBean insertElasticSearch() {
        elasticsearchRestTemplate.createIndex(Talk.class);
        elasticsearchRestTemplate.putMapping(Talk.class);
        //查询数据库文章数据
        List<Talk> talkList = baseMapper.selectList(null);
        talkList.forEach(talk -> {
            //为completion添加数据，将tittle数据添加
            Completion completion=new Completion(new String[]{talk.getTitle()});
            talk.setSuggest(completion);
        });
        talkRepository.saveAll(talkList);
        return ResultBean.success();
    }

    @Override
    public ResultBean updateElasticSearch(){
        try {
            //清除es中的旧数据
            elasticsearchRestTemplate.deleteIndex(Talk.class);
            //重新插入数据
            insertElasticSearch();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Errors("500","插入异常");
        }
        return ResultBean.success();
    }


}
