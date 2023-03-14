package com.su.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

//字典树算法（敏感词过滤工具类）
@Component
public class TrimUtils {

    private static final String REPLACE_WORD="**";

    //根节点
    private  Trim rootTrim=new Trim();

    //替换敏感词
    public String filterWord(String word){
        if (StringUtils.isBlank(word)){
            return "";
        }
        //指针1指向字典树根节点
        Trim beginTrim=rootTrim;
        //替换的字符串
        StringBuilder stringBuilder=new StringBuilder();
        //两个指针指向字符串区间
        int begin=0;
        int end=0;
        while (end<word.length()){
            char c = word.charAt(end);
            //为标点符号
            if (isSymbol(c)){
                //begin对应的字符不在字典树中
                if (beginTrim==rootTrim){
                    stringBuilder.append(c);//符号追加
                    begin++;
                }
                end++;
                continue;
            }
            //获取子节点
            beginTrim = beginTrim.getSubTrim(c);
            //字符不存在字典树中
            if (beginTrim==null){
                //begin字符不是敏感词
                stringBuilder.append(word.charAt(begin));
                end=++begin;
                beginTrim=rootTrim;
            }
            //字符存在字典树中，且为结尾
            else if (beginTrim.getIsEnd()){
                //采用*替代字符串
                stringBuilder.append(REPLACE_WORD);
                begin=++end;
                //字典树回到根节点
                beginTrim=rootTrim;
            }
            //字符在字典树
            else {
                //继续查找下一个字符
                end++;
            }
        }
        //添加剩余字符串
        stringBuilder.append(word.substring(begin));
        return stringBuilder.toString();
    }

    //加载敏感词文件
    @PostConstruct
    private void initTrim(){
        InputStream resourceAsStream=null;
        BufferedReader bufferedReader=null;
        //获取文件流
        try {
            resourceAsStream =
                    this.getClass().getClassLoader().getResourceAsStream("sensitiveword.txt");
            bufferedReader=new BufferedReader(new InputStreamReader(resourceAsStream));
            String keyword;
            while ((keyword=bufferedReader.readLine())!=null){
                this.addTrimNode(keyword);//添加字典树
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                resourceAsStream.close();
                bufferedReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //将敏感词添加到字典树
    private void addTrimNode(String  word){
        Trim templateTrim=rootTrim;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            //字符为标点符号
            if (isSymbol(c)){
                continue;
            }
            Trim subTrim = templateTrim.getSubTrim(c);
            //为空，字典树添加
            if (subTrim==null){
                subTrim=new Trim();
                templateTrim.addTrim(c,subTrim);
            }
            //遍历到子节点
            templateTrim=subTrim;
            //标识最后一个字符
            if (i==word.length()-1){
                templateTrim.setEnd();
            }
        }
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //字典树
    private class Trim{
        //标记是否为最后一个字符
        private boolean isEnd=false;

        //子节点,(k:子字符，v:子节点)
        private Map<Character,Trim> map=new HashMap<>();

        //子节点添加
        public void addTrim(Character word,Trim subTrim){
            map.put(word,subTrim);
        }

        //获取子节点
        public Trim getSubTrim(Character word){
            return map.get(word);
        }

        //设置结束标志
        public void setEnd(){
            this.isEnd=true;
        }

        //返回结束标志
        public boolean getIsEnd(){
            return this.isEnd;
        }
    }
}
