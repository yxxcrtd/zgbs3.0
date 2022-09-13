package com.joyintech.tams.position.constant;

import com.joyintech.framework.util.StringUtil;

public enum PzxMessageCode {
    //第三发系统根据业务情况决定
	DS0000, //头寸规则计算异常
	DS0001, //没有找到决策配置信息
	
	//失败
	FL0001, //没有找到头寸流水配置信息
	FL0002, //头寸流水计算方式不支持
	FL0003, //头寸流水节点类型不支持
	FL0004, //头寸流水处理方式不支持
	FL0005, //头寸流水处理异常
	FL0006, //头寸流水簿记异常
	FL0007, //余额检查不通过
	FL0008, //获取锁异常
	FL0009, //获取锁异常超时
	FL0010, //超过等待的次数
	
	CT0001   //控制类型错误
	;


	
}
