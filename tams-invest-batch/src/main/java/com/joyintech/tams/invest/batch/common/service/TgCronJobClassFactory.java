/*
 * 文件名：ValCronJobClassFactory.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2019年2月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
package com.joyintech.tams.invest.batch.common.service;

import com.joyintech.batch.core.DimensionDetailTask;
import com.joyintech.batch.core.JoyinBatchClassFactory;
import com.joyintech.batch.core.JoyinTask;
import org.quartz.Job;
import org.springframework.stereotype.Service;


/**
 * 实现批框架的类工厂
 *
 * @author 王波
 * @version 1.0 2019年2月13日
 * @see TgCronJobClassFactory
 * @since 1.0
 */
@Service
public class TgCronJobClassFactory implements JoyinBatchClassFactory {

    @Override
    public Class<? extends Job> defineJobClass() {
        // 返回自定义的任务类
        return TgCronJob.class;
    }

	@Override
	public Class<? extends JoyinTask> defineDimensionTaskClass() {
		return DimensionDetailTask.class;
	}

	@Override
	public Class<? extends JoyinTask> defineTaskClass() {
		return null;
	}
}
