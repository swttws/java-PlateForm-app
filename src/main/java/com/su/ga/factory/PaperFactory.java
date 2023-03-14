package com.su.ga.factory;


import com.su.ga.gene.PaperConfig;
import com.su.ga.gene.PaperIndividual;
import com.su.pojo.Question;
import lombok.Getter;
import lombok.Setter;

import java.awt.print.Paper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//生成试卷种群工厂类
@Getter
public class PaperFactory {

    //单选题题库
    private  List<Question> singleSelectList=new ArrayList<>();

    //多选题题库
    private  List<Question> multipleSelectList=new ArrayList<>();

    //判断题题库
    private  List<Question> decideList=new ArrayList<>();

    //面试题题库
    private  List<Question> auditionList=new ArrayList<>();

    private final int geno=14;

    //单选题下标0-7
    private final int singSelectIndex=7;

    //多选题下标8-9
    private final int multipleIndex=9;

    //判断题下标 10-11
    private final int decideIndex=11;

    //大题下标  12-13
    private final int auditionIndex=13;

    //题库获取
    public PaperFactory(List<Question> singleSelectList,List<Question> multipleSelectList,
                        List<Question> auditionList,List<Question> decideList){
        this.singleSelectList=singleSelectList;
        this.multipleSelectList=multipleSelectList;
        this.auditionList=auditionList;
        this.decideList=decideList;
    }

    //初始化试题
    public List<Question> initPaper(PaperConfig paperConfig){
        double expect = paperConfig.getExpectDifficulty();
        List<Question> questionList=new ArrayList<>();
        Random random=new Random();
        for (int i = 0; i < 14; i++) {
            //获取题库
            List<Question> examList = getQuestionLIstByIndex(i);
            List<Question> list=null;
            Question question=null;
            if (expect<=1.1){
                //获取难度为1的试题
                list= examList.stream().
                                filter(exam -> exam.getDifficulty() == 1).
                                collect(Collectors.toList());
            }else if (expect<=1.5){
                //获取难度为去1，2的题库
                list = examList.stream()
                        .filter(exam -> exam.getDifficulty() <= 2)
                        .collect(Collectors.toList());

            }else {
                //获取难度为2,3的题库
                list=examList.stream()
                        .filter(exam->exam.getDifficulty()<=3&&exam.getDifficulty()>=2)
                        .collect(Collectors.toList());
            }
            question=list.get(random.nextInt(list.size()));
            //防止重复获取相同题目
            examList.remove(question);
            //试卷添加试题
            questionList.add(question);
        }
        //原题库中删除的题目添加回去
        for (int i = 0; i < questionList.size(); i++) {
            List<Question> examList = getQuestionLIstByIndex(i);
            examList.add(questionList.get(i));
        }
        return questionList;
    }

    //根据下标获取题库
    private List<Question> getQuestionLIstByIndex(int index){
        List<Question> result=new ArrayList<>();
        if (index<=singSelectIndex){
            result=singleSelectList;
        }else if (index<=multipleIndex){
            result=singleSelectList;
        }else if (index<=decideIndex){
            result=decideList;
        }else {
            result=auditionList;
        }
        return result;
    }

    //产生个体,kpNUm 题知识点的数量
    public PaperIndividual init(List<Question> questionList, PaperConfig paperConfig, int kpNum){
        PaperIndividual paperIndividual=new PaperIndividual(questionList)
                .difficulty()
                .kpCoverage(kpNum)
                .fitness(paperConfig);
        return paperIndividual;
    }

    //复制得个体
    public PaperIndividual copyPaper(PaperIndividual mother){
        PaperIndividual paperIndividual = new PaperIndividual(mother.getQuestionList())
                .difficulty()
                .kpCoverage(mother.getKpNum());
        paperIndividual.setFitness(mother.getFitness());
        return paperIndividual;
    }

    //交叉得个体
    public PaperIndividual crossPaper(PaperIndividual mother,PaperIndividual father,
                                             PaperConfig paperConfig){
        //随机获取父母交换的试题的分界线
        int boundary=(int) (Math.random()*geno);
        //新子代题目
        List<Question> children=new ArrayList<>();
        //父母题目
        Question parent=null;
        for (int i = 0; i < mother.getQuestionList().size(); i++) {
            parent=i<=boundary?mother.getQuestionList().get(i):
                    father.getQuestionList().get(i);
            //试题重复,题库重新获取相同题型试题
            if (children.contains(parent)){
                selectQuestion(children,i,parent.getDifficulty());
                continue;
            }
            children.add(parent);
        }
        return new PaperIndividual(children)
                .difficulty()
                .kpCoverage(mother.getKpNum())
                .fitness(paperConfig);
    }

    //重新获取题目
    public void selectQuestion(List<Question> children,int index,Integer difficulty){
        Question question=null;
        Random random=new Random();
        //题目重复，题目位空，或者题目难度不一样，重新查找
        while (question==null||children.contains(question)){
            //获取题库
            List<Question> questionList = getQuestionLIstByIndex(index);
            question=questionList.get(random.nextInt(questionList.size()));
        }
        children.add(index,question);
    }

    //变异得个体
    public PaperIndividual mutationPaper(PaperIndividual mother,PaperConfig paperConfig){
        //变异下标
        int mutationIndex=(int) (Math.random()*geno);
        List<Question> children = new ArrayList<>(mother.getQuestionList());
        selectQuestion(children,mutationIndex,
                mother.getQuestionList().get(mutationIndex).getDifficulty());
        //创建新个体
        return new PaperIndividual(children)
                .difficulty()
                .kpCoverage(mother.getKpNum())
                .fitness(paperConfig);
    }

}
