package com.suu;

import com.alibaba.fastjson.JSON;
import com.su.PlateFormApplication;
import com.su.pojo.Talk;
import com.su.pojo.view.ContentView;
import com.su.service.TalkService;
import com.su.utils.TrimUtils;

import org.elasticsearch.action.search.SearchResponse;

import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggester;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest(classes = PlateFormApplication.class)
@RunWith(SpringRunner.class)
public class Test3 {
    @Test
    public void test(){
        Calendar calendar=Calendar.getInstance();
        Calendar calendar2=Calendar.getInstance();
        Date date = new Date();
        Date date1 = new Date();
        calendar2.setTime(date1);
        calendar.setTime(date);
        System.out.println(calendar.getTimeInMillis()-calendar2.getTimeInMillis());

    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void teste(){
        Boolean aBoolean =
                redisTemplate.
                        opsForValue().setIfAbsent("b", "lock", 200, TimeUnit.SECONDS);
        System.out.println(aBoolean);
    }

    @Autowired
    private TrimUtils trimUtils;
    @Test
    public void test44(){
        String s = trimUtils.filterWord("傻逼，上43有，病");
        System.out.println(s);
    }


    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    private TalkService talkService;



    @Test
    public void test5() throws IOException {
        String key="java";

        //构造补全对象
        CompletionSuggestionBuilder completionSuggestionBuilder =SuggestBuilders
                .completionSuggestion("suggest")
                .text(key)
                .skipDuplicates(true)
                .size(8);

        SuggestBuilder suggestBuilder=new SuggestBuilder();
        suggestBuilder.addSuggestion("my-suggest",completionSuggestionBuilder);

        //查询提示词
        SearchResponse suggestResponse =
                elasticsearchTemplate.suggest(suggestBuilder, Talk.class);
        //获取suggest提示词
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<?
                extends Suggest.Suggestion.Entry.Option>> suggestions =
                suggestResponse.getSuggest().getSuggestion("my-suggest");
        //结果返回
        List<String> suggestList = suggestions.getEntries().stream()
                //获取options属性中的text属性
                .map(suggests -> suggests.getOptions().stream().
                        map(option -> option.getText().toString()).collect(Collectors.toList()))
                .findFirst()
                .get();
        suggestList.forEach(System.out::println);
    }

    @Test
    public void test6(){
        elasticsearchTemplate.createIndex(Talk.class);
        elasticsearchTemplate.putMapping(Talk.class);
//        elasticsearchTemplate.deleteIndex(Talk.class);
//        talkService.updateElasticSearch();
    }

    @Test
    public void test7(){
        talkService.updateElasticSearch();
    }
}
