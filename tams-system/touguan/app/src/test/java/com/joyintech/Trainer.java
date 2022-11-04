package com.joyintech;

import com.joyintech.tams.valuation.analysis.ValuationTableTrain;

/**
 * 训练的目的是生成：bayes-valuation-table-header-model.gz
 */
public class Trainer {

    public static void main(String[] args) throws Exception {
        String segModelPath = "/home/model/seg.m";
        String headerTrainDataDir = "/home/model/估值表识别/表头";
        String bayesModelPath = "/home/model/估值模型/bayes-valuation-table-header-model.gz";

        ValuationTableTrain train = new ValuationTableTrain(segModelPath, headerTrainDataDir, bayesModelPath);
        train.train();
    }

}
