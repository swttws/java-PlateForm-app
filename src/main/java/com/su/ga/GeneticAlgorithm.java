package com.su.ga;

import com.su.ga.factory.PaperFactory;
import com.su.ga.gene.PaperConfig;
import com.su.ga.gene.PaperIndividual;
import com.su.pojo.Question;

import java.util.*;

//进化核心类
public class GeneticAlgorithm {

    private PaperConfig paperConfig;//配置类

    private PaperFactory paperFactory;//产生个体工厂类

    public int generation;//当前代

    private PaperIndividual resultPaper;//最终结果

    private double bestFitness;//最佳适应度

    private int stableFitnessGen;//保持年代

    private final int fixOnGen=5;//超过10代未达到最佳适应度，修正（突变）

    private final int questionNum=14;//试卷题数

    private PaperIndividual[] paperGroup;//试卷种群

    public GeneticAlgorithm(PaperConfig paperConfig,PaperFactory paperFactory){
        this.paperConfig=paperConfig;
        this.paperFactory=paperFactory;
        paperGroup=new PaperIndividual[paperConfig.getPaperNum()];
    }

    //寻找最佳试卷
    public PaperIndividual searchBestPaper(){
        //初始化种群
        initPaperGroup();
        //进化
        evolution();
        return this.resultPaper;
    }

    //初始化试卷种群
    private void initPaperGroup() {
        Random random=new Random();
        //试卷 8道单选，2道多选，2道判断，2道大题
        for (int i = 0; i < paperGroup.length; i++) {
            //试卷题
            List<Question> questionList=new ArrayList<>();
            //记录类型数
            Set<Integer> set=new HashSet<>();
            //试题组合
            for (int j = 0; j < questionNum; j++) {
                questionList=paperFactory.initPaper(paperConfig);
                //添加类型，set去重
                set.add(questionList.get(j).getSubjectId());
            }
            paperGroup[i]=paperFactory.init(questionList,paperConfig,set.size());
        }
    }

    //重置数据
    public void reset(){
        this.generation=0;
        this.bestFitness=0;
        this.stableFitnessGen=0;
    }

    //试卷种群进化
    private void evolution() {
        try {
            generation=0;
            calculateBestFitness();
            //开始进化
            for (int i = 0; i < paperConfig.getGenerationMax(); i++) {
                //当前种群达到要求
                if (bestFitness>paperConfig.getExpectDifficulty()){
                    break;
                }
                //变异率升高
                if (generation%paperConfig.getGeneration()==0){
                    paperConfig.mutationRateIncrease();
                }
                //新种群
                PaperIndividual[] newPaperGroup=new PaperIndividual[paperConfig.getPaperNum()];
                //交叉
                cross(newPaperGroup);
                //变异
                mutation(newPaperGroup);
                //复制
                copy(newPaperGroup);
                //修正
                fix(newPaperGroup);
                //更新种群
                paperGroup=newPaperGroup;
                generation++;
                calculateBestFitness();
            }
        }catch (Exception e){
            //重新进化
            reset();
            initPaperGroup();
            evolution();
            e.printStackTrace();
        }
    }

    //计算当前种群个体最佳舒适度
    private void calculateBestFitness(){
        double currentFitness=0;
        double totalFitness=0;
        for (int i = 0; i < paperGroup.length; i++) {
            //获取个体舒适度
            double fitness = paperGroup[i].getFitness();
            //舒适度小于0paperGroup = {PaperIndividual[20]@9518}
            if (fitness<0){
                throw new RuntimeException("舒适度不能小于0");
            }
            if (fitness>currentFitness){
                currentFitness=fitness;
            }
            totalFitness+=fitness;
        }
        //总舒适度不能小于0
        if (totalFitness<0){
            throw new RuntimeException("总舒适度不能为0");
        }
        //计算每个个体舒适度在种群中的比例
        for (int i = 0; i < paperGroup.length; i++) {
            paperGroup[i].setEvolutionRate(paperGroup[i].getFitness()/totalFitness);
        }
        //当前种群退化
        if (currentFitness<bestFitness){
            stableFitnessGen++;
        }else {
            stableFitnessGen=0;
        }
        bestFitness=currentFitness;
    }

    //交叉
    private void cross(PaperIndividual[] newPaperGroup) {
        for (int i = 0; i < paperConfig.getCrossNum(); i++) {
            //随机选取父母进行交叉
            PaperIndividual father = paperGroup[wager()];
            PaperIndividual mother = paperGroup[wager()];
            //获取个体
            PaperIndividual children = paperFactory.crossPaper(mother, father, paperConfig);
            newPaperGroup[i]=children;
        }
    }

    //轮盘度算法
    private int wager(){
        double sum=0;
        double p = Math.random();
        for (int i = 0; i < paperGroup.length; i++) {
            sum+=paperGroup[i].getEvolutionRate();
            if (sum>p){
                return i;
            }
        }
        return (int) (paperGroup.length*Math.random());
    }

    //变异
    private void mutation(PaperIndividual[] newPaperGroup) {
        for (int i = 0; i < paperConfig.getMutationNum(); i++) {
            //变异得新个体
            PaperIndividual children = paperFactory.mutationPaper(paperGroup[i], paperConfig);
            newPaperGroup[i]=children;
        }
    }

    //复制
    private void copy(PaperIndividual[] newPaperGroup) {
        int copyNum=paperConfig.getCopyNum();
        //选取优异的品种进行复制
        PaperIndividual[] paperIndividuals = sort(copyNum);
        int index=paperConfig.getCrossNum();
        for (PaperIndividual paperIndividual : paperIndividuals) {
            //复制
            newPaperGroup[index]=paperFactory.copyPaper(paperIndividual);
            index++;
        }
    }

    //按个体舒适度从小到大排序,冒泡排序
    private PaperIndividual[] sort(int copyNum){
        for (int i = 0; i < copyNum; i++) {
            for (int j = 0; j < paperGroup.length - 1 - i; j++) {
                if (paperGroup[i].getFitness()<paperGroup[i].getFitness()){
                    PaperIndividual temp=paperGroup[i];
                    paperGroup[i]=paperGroup[i+1];
                    paperGroup[i+1]=temp;
                }
            }
        }
        return Arrays.copyOfRange(paperGroup,paperGroup.length-copyNum,paperGroup.length);
    }

    //修正
    private void fix(PaperIndividual[] newPaperGroup) {
        if (stableFitnessGen<fixOnGen){
            return;
        }
        //获取变异个数
        int num= (int) (paperConfig.getCopyNum()*Math.random());
        //变异
        for (int i = 0; i < num; i++) {
            int mutationIndex=(int) Math.random()*paperConfig.getCrossNum();
            //获取新种群个体
            PaperIndividual paperIndividual = newPaperGroup[newPaperGroup.length - 1 - i];
            //继续变异依次
            newPaperGroup[mutationIndex]=paperFactory
                    .mutationPaper(paperIndividual,paperConfig);
        }
    }

    //获取最优解
    public PaperIndividual getResultPaper(){
        if (resultPaper==null){
            resultPaper = getPaper();
        }
        return resultPaper;
    }

    private PaperIndividual getPaper() {
        double bestFitness=0;
        int bestIndex=0;
        for (int i = 0; i < paperGroup.length; i++) {
            if (paperGroup[i].getFitness()>bestFitness){
                bestFitness=paperGroup[i].getFitness();
                bestIndex=i;
            }
        }
        return paperGroup[bestIndex];
    }


}
