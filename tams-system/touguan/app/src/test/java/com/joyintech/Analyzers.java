package com.joyintech;

import java.util.List;

import com.joyintech.tams.valuation.analysis.ValuationTableAnalysis;
import com.joyintech.tams.valuation.analysis.info.ValuationTableInfo;
import com.joyintech.tams.valuation.analysis.info.ValuationTableRow;

public class Analyzers {

	public static void main(String[] args) throws Exception {
		String timeModelPath = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\time.m";
		String headerTrainDataDir = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\估值表识别\\表头";
		String aggregateTrainDataDir = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\估值表识别\\汇总";
		String bayesModelPath = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\估值模型\\bayes-valuation-table-header-model.gz";
		String excel = "D:\\workspace\\zgbs-3.0\\tams-system\\touguan\\app\\src\\test\\java\\com\\joyintech\\model\\样表汇总\\中金华裕1号集合资产管理计划估值表-债券（应计利息和成本在一级科目下）、股票（应收利息和成本不在一级科目下）.xls";
		ValuationTableAnalysis analysis = new ValuationTableAnalysis(timeModelPath, bayesModelPath, headerTrainDataDir, aggregateTrainDataDir);
		ValuationTableInfo info = analysis.execute(excel);
		
		System.out.println("估值表名称               : "+info.getName());
		System.out.println("估值日期                   : "+info.getHeader().getDate());
		System.out.println("明细头信息(从0开始) : "+info.getListHeaderIndexes()+"\r\n");
		printList("",info.getList());
		System.out.println("\r\n"+info.getAggregateInfo());
	}

	public static void printList(String pre,List<ValuationTableRow> list) {
		for (ValuationTableRow row : list) {
			System.out.println(pre + row);
			List<ValuationTableRow> child = row.getChild();
			if (child.size()>0) {
				printList(pre+"  ",child);
			}
		}
	}

}
