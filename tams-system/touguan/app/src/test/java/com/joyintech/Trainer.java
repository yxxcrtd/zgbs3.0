package com.joyintech;

import com.joyintech.tams.valuation.analysis.ValuationTableTrain;

public class Trainer {

    public static void main(String[] args) throws Exception {
        String segModelPath = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\seg.m";
        String headerTrainDataDir = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\估值表识别\\表头";
        String bayesModelPath = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\估值模型\\bayes-valuation-table-header-model.gz";

        ValuationTableTrain train = new ValuationTableTrain(segModelPath, headerTrainDataDir, bayesModelPath);
        train.train();
    }

}
