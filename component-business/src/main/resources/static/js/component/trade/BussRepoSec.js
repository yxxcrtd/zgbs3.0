//@ sourceURL=BussRepoSec.js
(function () {

    var param = {};

    /**
     * 设置初始参数
     * @param paramObject
     */
    if(entrustAmtUnit == '02' ){
        $(".unitNum").text('万元');
    }
    var setInputParam = function (paramObject) {
        param = paramObject;
        getElementById("ps").val(param.data.ps);
        //逆回购隐藏可质押券面总额
        if(param.data.ps=='04'){
            pledgeBondTotalTbl.showColumns([{field:'avaPledgePrin',visible: false}]);
        }else{
            if(isDzh == 'Y'){
                pledgeBondTotalTbl.showColumns([{field:'portfolioId', visible: true},
                    {field:'portfolioName',visible: true},
                    {field:'avaPledgePrin',visible: true}]);
            }else{
                pledgeBondTotalTbl.showColumns([{field:'avaPledgePrin',visible: true}]);
            }
        }
    }

    var getElementById = function (id) {
        return $("#" + entityNameBussRepoSec + "_" + id);
    };

    var tempPledgePrin;

    /**
     * 定义质押券信息列表（Total）
     */
    var pledgeBondTotalTbl = getElementById("pledgeBondTotalList").joyintable({
        page: false,
        data: (entity && entity.pledgeBondList) ? entity.pledgeBondList : [],
        height: 200,
        /**
         * 定义表头
         */
        cols: [
            //add by zhangnian 20191126 start
            {
                field: 'finprodId',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.bondCode',
                sortable: false,
                visible:false
            }
            //add by zhangnian 20191126 end
            // 债券代码
            ,{
                field: 'bondCode',
                width: 150, title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.bondCode', sortable: false
            }
            // 债券简称
            , {
                field: 'bondAbbrName',
                width: 150,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.bondAbbrName',
                sortable: false
            }
            // 组合代码
            , {
                field: 'portfolioId',
                width: 150,
                title: '组合代码',
                sortable: false,
                visible:(isDzh=='Y'&&$('#CfetsRepoDealQuotedPrice002_ps').val()=='03')?true:false
            }
            // 组合名称
            , {
                field: 'portfolioName',
                width: 150,
                title: '组合名称',
                sortable: false,
                visible:(isDzh=='Y'&&$('#CfetsRepoDealQuotedPrice002_ps').val()=='03')?true:false
            }
            // 可质押券面总额
            , {
                field: 'avaPledgePrin',
                width: 250,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.avaPledgePrin1',
                type: 'money',
                digit: 4,
                sortable: false
            }
            // 质押券面额
            , {
                field: 'pledgePrin',
                width: 200,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.pledgePrin1',
                type: 'money',
                digit: 4,
                edit: true,
                sortable: false
            }
            // 首次净价
            , {
                field: 'firstNetPrice',
                width: 100,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.firstNetPrice1',
                sortable: false
            }
            // 折算比例%
            , {
                field: 'pledgeRatio',
                width: 100,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.pledgeRatio',
                type: 'number',
                digit: 2,
                edit: true,
                sortable: false
            }
            , {
                field: 'repRegistOrg',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.repRegistOrg',
                sortable: false,
                visible:false
            }
            , {
                field: 'repRegistOrgStr',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.repRegistOrg',
                sortable: false,
                visible:false
            }
        ],
        beforeEditCell: function (preVal, field, rowData) {
            if (field === "pledgePrin"||field === 'pledgeRatio') {
                //CFET质押式回购  回复操作  不可编辑
                if(param.data.quotaPageModel&&param.data.quotaPageModel == 'dialogueResponse'){
                    return false;
                }
                //交易画面选择成交编号 table不可编辑
                if(param.data.isFromCstp){
                    return false;
                }
                tempPledgePrin = preVal.replace(/\,/g, '');
            }
            return true;
        },
        editCell: function (value, data, field, cell, column) {
            if (field === "pledgePrin" && param.data.ps == BACK_LEND_DIRECTION_03 && data.pledgePrin * 1 > data.avaPledgePrin * 1) {
                //CFET质押式支持软硬空
                if((getElementById("pageFrom").val() == 'CfetsRepoDealQuotedPrice002'||getElementById("pageFrom").val() == 'CfetsRepoDealQuotedPrice003')
                &&ibuFlag_repoOverAvaCanContinue=='02'){
                    Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_E_0003",
                        [JoyinUtil.getLabel("BussRepoSec.pledgeBondTotalTbl.colmodel.pledgePrin"),
                            JoyinUtil.getLabel("BussRepoSec.pledgeBondTotalTbl.colmodel.avaPledgePrin")]), function () {},'',function () {
                        var datas = pledgeBondTotalTbl.getAllRows();
                        datas[data.LAY_TABLE_INDEX].pledgePrin = tempPledgePrin;
                        pledgeBondTotalTbl.reload({data: datas});
                    })
                }else{
                    var datas = pledgeBondTotalTbl.getAllRows();
                    datas[data.LAY_TABLE_INDEX].pledgePrin = tempPledgePrin;
                    pledgeBondTotalTbl.reload({data: datas});
                    Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0003",
                        [JoyinUtil.getLabel("BussRepoSec.pledgeBondTotalTbl.colmodel.pledgePrin"),
                            JoyinUtil.getLabel("BussRepoSec.pledgeBondTotalTbl.colmodel.avaPledgePrin")]));
                }
            }
            refreshTotalPrinAmt();
            //刷新对手方质押券信息

            window.CfetsPledgedBond.refreshCfetsPldegeBond(pledgeBondTotalTbl.getAllRows());
        },done:function(data1,data2,data3){
        	var pledgeBondTotalTblDone = window.BussRepoSec[entityNameBussRepoSec].pledgeBondTotalTblDone;
        	if(typeof pledgeBondTotalTblDone != "undefined" &&$.isFunction(pledgeBondTotalTblDone)){
        		pledgeBondTotalTblDone(data1,data2,data3);
        	}
            if(isDzh=='Y'){
                window.CfetsPledgedBond.refreshCfetsPldegeBond(pledgeBondTotalTbl.getAllRows());
            }
        }
    });

    /**
     * 添加按钮按下处理
     */
    var addProcess = function () {
        entrustId =  getElementById("entrustId").val();
        //CFET质押式回购check
        if(getElementById("pageFrom").val() == 'CfetsRepoDealQuotedPrice002'){
            var data = $.serializeToObject('#CfetsRepoDealQuotedPrice002_submitForm');
            if(!data.ps){
                Joyin.error(JoyinUtil.getMessage("CFETS_COMMON_E_024"));
                return;
            }
            if(!data.portfolioId){
                Joyin.error(JoyinUtil.getMessage("CFETS_COMMON_E_025"));
                return;
            }
            if(!data.mySide){
                Joyin.error(JoyinUtil.getMessage("CFETS_COMMON_E_026"));
                return;
            }
            if(!data.term){
                Joyin.error(JoyinUtil.getMessage("CFETS_COMMON_E_027"));
                return;
            }
            if(!data.price){
                Joyin.error(JoyinUtil.getMessage("CFETS_COMMON_E_028"));
                return;
            }
            if (!entrustId && data.entrustId){
                entrustId = data.entrustId;
            }
        }
        Joyin.ajaxJson(basePath + 'component/trade/BussRepoSec/check',
            param, function () {
                if (entity == null) {
                    entity = {};
                }
                entity.pledgeBondList = pledgeBondTotalTbl.getAllRows();
                if(!param.data.portfolioId){
                	param.data.portfolioId="";
                }
                Joyin.dialog(JoyinUtil.getLabel('BussRepoSec.addPledgeInfo'),
                    '/component/trade/BussRepoSecDialog/init', '98%', '98%',
                    {
                        //多组合没有组合代码页面显示undefined修改  update by xueying 20200721 start
                        portfolioId: param.data.portfolioId==undefined?'':param.data.portfolioId,
                        //多组合没有组合代码页面显示undefined修改  update by xueying 20200721 end
                        ps: param.data.ps,
                        secAcctId: param.data.secAcctId? param.data.secAcctId: param.data.securitiesCust,
                        chlId: param.data.chlId,
                        securitiesCust: param.data.securitiesCust,
                        repoType: param.data.repoType,
                        tradeMarket: $("#"+tradeMarketId).val()==undefined?tradeMarketId: $("#"+tradeMarketId).val(),
                        //add by gaopeng 2019/10/12 start
                        entrustId:entrustId==''?param.data.entrustId: entrustId,
                        //add by gaopeng 2019/10/12 end
                        entity: JSON.stringify(entity),
                        entityName: entityNameBussRepoSec,
                        statusValid: statusValid,
                        bizCode: bizCode,
                        /*isTodoList: isTodoList,*/
                        pageModel: pageModel,
                        operationType: operationType,
                        workflowId: workflowId
                        //产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201125 start
                        ,pageFromTz:(getElementById("pageFrom").val()=='TrdRepoTrade002'||getElementById("pageFrom").val()=='CfetsRepoDealQuotedPrice002')?getElementById("pageFrom").val():''
                        //产品化_质押式回购交易界面,正回购时：点击添加后的|可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201125 end
                        // add by etliu 20210303 银行间上下行 start
                        ,ignoreFlowTradeId: param.data.ignoreFlowTradeId
                        // add by etliu 20210303 银行间上下行 end
                        ,pageFromTz:(getElementById("pageFrom").val()=='TrdRepoTrade002'||getElementById("pageFrom").val()=='CfetsRepoDealQuotedPrice002'||getElementById("pageFrom").val()=='CfetsRepoDealQuotedPrice003'
                        ||getElementById("pageFrom").val()=='EtrSecLend002'||getElementById("pageFrom").val()=='TrdBondLendTrade002')?getElementById("pageFrom").val():''
                        //产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201125 end
                        ,isDzh:isDzh
                        ,quoteId:quoteId
                        ,tradePlace:tradePlace
                        ,parentPageId:parentPageId
                        ,mdate:param.data.mdate
                        ,isBondLendTrade:isBondLendTrade
                    });
            });
    };

    /**
     * 删除按钮按下处理
     */
    var deleteProcess = function () {
        var selArr;
        if (pledgeBondTotalTbl != null && pledgeBondTotalTbl != '') {
            selArr = pledgeBondTotalTbl.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        Joyin.confirm('是否确认删除所选质押劵？',function () {
            // 倒序删除，正序会存在问题
            for (var index = selArr.length - 1; index >= 0; index--) {
//                if(!selArr[index]["pledgePrin"])
//                {
//                    selArr[index]["pledgePrin"] = undefined;
//                }
                pledgeBondTotalTbl.removeRow(selArr[index]);
            }
            refreshTotalPrinAmt();
            //add by jiangpeng on 20200731 start 修改TISHKBS-844导致 添加判断质押券置换
            if(getElementById('pageFrom').val()!="EtrRepo002"){
	            if(getElementById('pageFrom').val()!="TrdRepoTrade004"){
	                window.calFee();
	            }else{
	                calFee();
	            }
            }
            //add by jiangpeng on 20200731 end
        });
    };

    /**
     * 删除按钮按下处理
     */
    var refreshTotalPrinAmt = function () {
        var allRows = pledgeBondTotalTbl.getAllRows();
        var totalPrinAmt = Number(0);
        var zzdPrinAmt = Number(0);
        var sqsPrinAmt = Number(0);
        //券面总额合计  不计算折算率
        var totalPrinAmtSum = Number(0);
        for (var i in allRows) {
            var pledgePrin = allRows[i].pledgePrin;
            // if(isDzh == 'Y'){
            if(entrustAmtUnit == '01'){     //传过来的数据就是以万元为单位
                pledgePrin = JoyinMath.doMul(pledgePrin,10000,2,JoyinMath.ROUND_HALF_UP);
                totalPrinAmtSum += pledgePrin / 10000;
            }else{
                totalPrinAmtSum += pledgePrin;      //以万元为单位验证
            }
            totalPrinAmt += pledgePrin * allRows[i].pledgeRatio / 100;
            if(allRows[i].repRegistOrg=='01'){
                zzdPrinAmt+=pledgePrin * allRows[i].pledgeRatio / 100;
            }
            if(allRows[i].repRegistOrg=='02'){
                sqsPrinAmt+=pledgePrin * allRows[i].pledgeRatio / 100;
            }
        }
        getElementById("totalPrinAmt").val(totalPrinAmt).trigger('change').trigger('blur');
        getElementById("zzdTotalPrinAmt").val(zzdPrinAmt).trigger('change').trigger('blur');
        getElementById("sqsTotalPrinAmt").val(sqsPrinAmt).trigger('change').trigger('blur');
        $("#zzdPrinAmt").val(zzdPrinAmt).trigger('change').trigger('blur');
        $("#sqsPrinAmt").val(sqsPrinAmt).trigger('change').trigger('blur');
        refreshParentFace({totalPrinAmt:totalPrinAmtSum,shareCtAmt:zzdPrinAmt+sqsPrinAmt});
    };

    /**
     * 保存按钮按下，添加子画面传回的质押券信息
     */
    var addPledgeBond = function (pledgeRows) {
        // 列表中未勾选
        if (!pledgeRows || pledgeRows.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        } else {
            pledgeBondTotalTbl.clear(function () {
                var totalPrinAmt = Number(0);
                var zzdPrinAmt = Number(0);
                var sqsPrinAmt = Number(0);
                //券面总额合计  不计算折算率
                var totalPrinAmtSum = Number(0);
                for (var i in pledgeRows) {
                    var obj = pledgeRows[i];
                    totalPrinAmt += obj.pledgePrin * obj.pledgeRatio / 100;
                    // totalPrinAmtSum += obj.pledgePrin / 10000; //以万元为单位
                    totalPrinAmtSum += obj.pledgePrin; //显示就是万元，不需要转换
                    if(obj.repRegistOrg=='01'){
                        zzdPrinAmt+=obj.pledgePrin * obj.pledgeRatio / 100;
                    }
                    if(obj.repRegistOrg=='02'){
                        sqsPrinAmt+=obj.pledgePrin * obj.pledgeRatio / 100;
                    }
                    pledgeBondTotalTbl.addRow(obj);
                }
                //多组合  可质押券面额和质押券面额 以万元为单位
                // if(isDzh == 'Y'){
                if (entrustAmtUnit == '01'){
                    totalPrinAmtSum = totalPrinAmtSum*10000;
                    totalPrinAmt = totalPrinAmt*10000;
                    zzdPrinAmt = zzdPrinAmt*10000;
                    sqsPrinAmt = sqsPrinAmt*10000;
                }
                getElementById("totalPrinAmt").val(totalPrinAmt).trigger('change').trigger('blur');
                getElementById("zzdTotalPrinAmt").val(zzdPrinAmt).trigger('change').trigger('blur');
                getElementById("sqsTotalPrinAmt").val(sqsPrinAmt).trigger('change').trigger('blur');
                $("#zzdPrinAmt").val(zzdPrinAmt).trigger('change').trigger('blur');
                $("#sqsPrinAmt").val(sqsPrinAmt).trigger('change').trigger('blur');
                //更新CFETS质押式回购报价要素信息
                refreshParentFace({totalPrinAmt:totalPrinAmtSum,shareCtAmt:zzdPrinAmt+sqsPrinAmt});
            });
        }
    };

    /**
     * 更新CFETS质押式回购报价要素信息
     */
    var refreshParentFace = function (result) {
        //单组合
    	if(getElementById("pageFrom").val() == 'CfetsRepoDealQuotedPrice002'){
    		window.CfetsRepoDealQuotedPr002.calculateAmt(pledgeBondTotalTbl);
/*
            $("#CfetsRepoDealQuotedPrice002_pledgePrinTotal").val(result.totalPrinAmt).trigger('blur');
*/
/*            $("#CfetsRepoDealQuotedPrice002_shareCtAmt").val(result.shareCtAmt).trigger('blur');*/
        }
    	//多组合
    	if(getElementById("pageFrom").val() == 'CfetsRepoDealQuotedPrice003'){
            window.CfetsRepoDealQuotedPr003.calculateAmt(pledgeBondTotalTbl);
/*
            $("#CfetsRepoDealQuotedPrice002_pledgePrinTotal").val(result.totalPrinAmt).trigger('blur');
*/
            /*$("#CfetsRepoDealQuotedPrice002_shareCtAmt").val(result.shareCtAmt).trigger('blur');*/
        }
    }
    /**
     * 清除数据并隐藏质押券信息
     */
    var clearHideRepo = function () {
        pledgeBondTotalTbl.clear(function () {
            getElementById("totalPrinAmt").val(0).trigger('change').trigger('blur');
        });
        getElementById("main").hide();
    };

    /**
     * 显示质押券信息
     */
    var showRepo = function () {
        getElementById("main").show();
    };
    /**
     * 重新加载质押券信息
     */
    var reloadPledgeBond = function(repoAssetList){
        pledgeBondTotalTbl.table ? pledgeBondTotalTbl.reload({data:repoAssetList, url:null}) : null;
    };
    
    /**
     * 自动导入质押券按钮按下处理
     */
    var autoPledge = function () {
    	if(getElementById('pageFrom').val()=="EtrIbRepoinfo002"&&repo_pledge_model=='02'){
    		Joyin.ajaxJson(basePath + 'component/trade/BussRepoSec/checkAutoPledgeOfEtrIbRepoinfo',param, function (result) {
        	    //获取质押券列表原数据
        		var oldData =  pledgeBondTotalTbl.getAllRows();
        		var newData = [];
                //add by chenjiaxing ZGZGST-2567 质押式回购指令，指令新建自动导入质押券信息错误 202100914 start
                for(var i = 0 ; i < result.data.length ; i++){
                    if(result.data[i].avaPledgePrin){
                        result.data[i].avaPledgePrin = JoyinMath.doDiv(result.data[i].avaPledgePrin, "10000", 4);
                    }
                    if(result.data[i].pledgePrin){
                        result.data[i].pledgePrin = JoyinMath.doDiv(result.data[i].pledgePrin, "10000", 4);
                    }
                }
                //add by chenjiaxing ZGZGST-2567 质押式回购指令，指令新建自动导入质押券信息错误 202100914 start
        		if(oldData != undefined){
        		    var map = new Map();
        		    var resultData = oldData.concat(result.data);
        		    resultData.forEach(item => {
        		        if(map.has(item.bondCode)){
                            map.set(item.bondCode,true);
        		        }else{
        		            map.set(item.bondCode,true);
        		            newData.push(item);
        		        }
        		    });
        		}else{
        		    newData = result.data;
        		}
        		// 加载质押券数据
        		pledgeBondTotalTbl.reload({data: newData});
        		// 汇总券面总额 赋值
        		refreshTotalPrinAmt() ;
                        
        	});
    	}else{
    		Joyin.ajaxJson(basePath + 'component/trade/BussRepoSec/checkAutoPledge',param, function (result) {
        	    //获取质押券列表原数据
        		var oldData =  pledgeBondTotalTbl.getAllRows();
        		var newData = [];
        		if(oldData != undefined){
        		    var map = new Map();
        		    var resultData = oldData.concat(result.data);
        		    resultData.forEach(item => {
        		        if(map.has(item.bondCode)){
                            map.set(item.bondCode,true);
        		        }else{
        		            map.set(item.bondCode,true);
        		            newData.push(item);
        		        }
        		    });
        		}else{
        		    newData = result.data;
        		}
        		// 加载质押券数据
        		pledgeBondTotalTbl.reload({data: newData});
        		// 汇总券面总额 赋值
        		refreshTotalPrinAmt() ;
                        
        	});
    	}
    };
    
    /**
     * 计算费用
     */
    var calFee = function(){
        if(!$("#tradeDate").val()){
            return;
        }
        if(!$("#secAcctId").val()){
            return;
        }
        if(!$("#ps").val()){
            return;
        }
        if(!$("#prinAmt").val()){
            return;
        }
        if($("#tradeMarket").val() == '01' && !$("#termTypeReback").val()){ //TRADE_PLACE_01
            return;
        }
        if (pageModel == PAGE_MODEL_REFERENCE){
            // 审批人审批或者生效的交易不再重新计算
            return;
        }

        var object = $.serializeToObject('#submitForm', true);
        Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/calFee', object, function (result) {
            if (result) {
                var tradeFeeList = result.tradeFeeList;
                if(tradeFeeList && tradeFeeList.length>0){
                    // 交易手续费条数
                    var cntP = 0;
                    // 结算费条数
                    var cntS = 0;
                    // 佣金条数
                    var cntC = 0;
                    $.each(tradeFeeList,function(i,v){
                        // 佣金设置
                        if(v.feeType==FEE_TYPE_05&&v.feeAmt){
                            cntC++;
                            isPayWithTrade = v.isPayWithTrade;
                            $("#isPayWithTrade").val(isPayWithTrade);
                            if(pageModel==PAGE_MODEL_NEW){
                                $("#commissionAmt").val(v.feeAmt).trigger("blur");
                            }
                            // 交易方向为逆回购并且随交易支付，交易金额 = 本金金额 + 随交易支付的费用
                            changeTradeAmtAndFee();
                        }
                        if(v.feeType==FEE_TYPE_06){
                            cntP++;
                            if (v.feeAmt) {
                                $("#procedureAmt").val(v.feeAmt).trigger("blur");
                            }
                            else {
                                $("#procedureAmt").val(0).trigger("blur");
                            }
                            // $("#tradeAmtAndFee").val($("#prinAmt").val()).trigger("blur");
                        }
                        else if(v.feeType==FEE_TYPE_08){
                            cntS++;
                            if (v.feeAmt) {
                                $("#settleAmt").val(v.feeAmt).trigger("blur");
                            }
                            else {
                                $("#settleAmt").val(0).trigger("blur");
                            }
                            // $("#tradeAmtAndFee").val($("#prinAmt").val()).trigger("blur");
                        }


                    });
                    // 按照最新费用规则计算相关费用无结果则清空相关费用
                    if(cntP == 0){
                        if(pageModel==PAGE_MODEL_NEW){
                            $("#procedureAmt").val("").trigger("blur");
                        }
                    }
                    if(cntS == 0){
                        if(pageModel==PAGE_MODEL_NEW){
                            $("#settleAmt").val("").trigger("blur");
                        }
                    }
                    if(cntC == 0){
                        if(pageModel==PAGE_MODEL_NEW) {
                            $("#commissionAmt").val("").trigger("blur");
                        }
                    }
                }else{
                    if(pageModel==PAGE_MODEL_NEW) {
                        $("#commissionAmt").val('').trigger("blur");
                        $("#procedureAmt").val('').trigger("blur");
                        $("#settleAmt").val('').trigger("blur");
                    }

                }
            }
        },false);
    };
    /**
     * 画面加载完成
     */
    $(function () {
    	// 质押式回购指令新建时，显示自动导入质押券按钮
    	if(getElementById('pageFrom').val()=="EtrRepo002"){
    		$('#EtrRepo002_auto_pledge_div').show();
    	}
    	
    	if(getElementById('pageFrom').val()=="EtrIbRepoinfo002"&&repo_pledge_model=='02'){
    		$('#EtrIbRepoinfo002_auto_pledge_div').show();
    	}
    	
        if(isBondLendTrade){
            $('#isBondLendTrade').show();
        }
        // 加载质押券信息列表
        pledgeBondTotalTbl.load();
        // 添加按钮事件
        getElementById("btn_add").click(addProcess);
        // 删除按钮事件
        getElementById("btn_delete").click(deleteProcess);
        // 自动导入质押券
        getElementById("auto_pledge").click(autoPledge);
    });

    // 组合现金、持仓查询业务共通公开接口
    if (window.BussRepoSec == undefined) {
        window.BussRepoSec = {};
    }
    window.BussRepoSec[entityNameBussRepoSec] = {
        setInputParam: setInputParam,
        clearHideRepo: clearHideRepo,
        showRepo: showRepo,
        addPledgeBond: addPledgeBond,
        reloadPledgeBond: reloadPledgeBond,
        "pledgeBondTotalTbl": pledgeBondTotalTbl
    };
    if(getElementById("pageFrom").val() == 'CfetsRepoDealQuotedPrice002'){
        pledgeBondTotalTbl.showColumns([{field:'repRegistOrgStr',visible: true}]);
    }
    //多组合
    // if(isDzh == 'Y'){
    //     pledgeBondTotalTbl.showColumns([{field:'avaPledgePrin',visible:true,title: '可质押券面总额(万元)'}]);
    //     pledgeBondTotalTbl.showColumns([{field:'pledgePrin',visible:true,title: '质押券面额(万元)'}]);
    // }
}());