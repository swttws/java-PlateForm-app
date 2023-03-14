package com.su.ga.gene;

import com.su.pojo.Question;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApiModel(value = "试卷类（个体），单选8题，判断2题，多选2题，面试题2题")
@Setter
@Getter
@ToString
public class PaperIndividual implements Serializable {
    //题集合(即组合为个体的元素)
    private List<Question> questionList;
    //试卷适应度
    private double fitness;
    //试卷适应度在种群中比例
    private double evolutionRate;
    //试卷难度系数
    private double difficulty;
    //知识点覆盖率
    private double kpCoverage;
    //知识点数量
    private int kpNum;
    //总分
    private final double totalScore=100;
    //知识点权重
    private final double kp=0.2;
    //难度权重
    private final double dp=0.8;
    //题的数量
    private final double questionNum=14;

    public PaperIndividual(List<Question> questionList){
        this.questionList=questionList;
    }

    //计算个体知识点覆盖率
    public PaperIndividual kpCoverage(int kpNum){
        this.kpNum=kpNum;
        //计算知识点数量
        Set<Integer> set=new HashSet<>();
        for (Question question : this.questionList) {
            set.add(question.getSubjectId());
        }
        this.kpNum=set.size();
        this.kpCoverage=this.kpNum/questionNum;
        return this;
    }

    //个体的难度
    public PaperIndividual difficulty(){
        double total=0;
        for (Question question : questionList) {
            total+=question.getDifficulty()*question.getScope();
        }
        this.difficulty=total/totalScore;
        return this;
    }

    //计算个体适应度 最佳适应度为2.6
    public PaperIndividual fitness(PaperConfig paperConfig){
        this.fitness=this.kpCoverage*kp+this.difficulty*dp;
        return this;
    }



}
