package com.su.ga.gene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("用户期望工具类")
@Getter
@Setter
public class PaperConfig {
    @ApiModelProperty("用户期待难度")
    private double expectDifficulty;

    @ApiModelProperty("进化的最大代数")
    private int generationMax = 1000;

    @ApiModelProperty("每10代变异率增高")
    private int  generation=10;

    @ApiModelProperty("种群个体数")
    private int paperNum;

    @ApiModelProperty("复制数")
    private int copyNum;

    @ApiModelProperty("交叉数")
    private int crossNum;

    @ApiModelProperty("突变量")
    private int mutationNum;

    @ApiModelProperty("突变概率")
    private double mutationRate;

    public PaperConfig(Integer paperNum, double copyRate){
        this.paperNum=paperNum;
        //种群复制数
        this.copyNum= (int) (paperNum*copyRate);
        //交叉数
        this.crossNum=this.paperNum-copyNum;
        //变异数
        this.mutationNum= (int) (this.paperNum*mutationRate);
    }

    //变异概率
    public PaperConfig mutationRate(double mutationRate){
        if (mutationRate>0.1){
            mutationRate=0.1;//突变概率小于10%
        }
        this.mutationRate=mutationRate;
        this.mutationNum= (int) (paperNum*mutationRate);
        return this;
    }

    //变异率提高
    public void mutationRateIncrease(){
        mutationRate(mutationRate*2);
    }

    //修改期望值
    public PaperConfig expectDifficulty(double expectDifficulty){
        this.expectDifficulty=expectDifficulty;
        return this;
    }

}
