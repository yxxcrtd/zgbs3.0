var initFlag = false;
$(function() {

    getoutDataTable();
    $('#TrdRepoTrade004_btn_save').click(function () {
        saveOrSubmit('save');
    });
    $('#TrdRepoTrade004_btn_submit').click(function () {
        saveOrSubmit('submit');
    });

    $('#TrdRepoTrade004_btn_ok').click(function () {
        outOk();
    });
    isShowDiv($('#ps').val());

});
window.componentLoadCallback = function (isDone, pagePath, params) {
    if (isDone)
    {
        var workflowId=$('#workflowId').val();
        if(workflowId!=='')
        {
        	initFlag = true;
            outOk();
            initFlag = false;
        }
        else {
            var oldpledgeBondList = null;//定义换入质押券列表值没发生变化前的记录
            var oldOutBondList = null;//定义换出债券列表值没发生变化前的记录
            var intertime = setInterval(function () {
                var object = $.serializeToObject('#submitForm');
                if (object.bussRepoSec !== undefined
                    && ((object.bussRepoSec.pledgeBondList != null
                            && object.bussRepoSec.pledgeBondList.length > 0
                            && oldpledgeBondList !== JSON.stringify(object.bussRepoSec.pledgeBondList))
                        || (object.outDataTable != null
                            && object.outDataTable.length > 0
                            && oldOutBondList !== JSON.stringify(object.outDataTable))
                    )
                ) {
                    oldpledgeBondList = JSON.stringify(object.bussRepoSec.pledgeBondList);
                    oldOutBondList = JSON.stringify(object.outDataTable);
                    outOk();
                }
                if ((object.bussRepoSec.pledgeBondList.length === 0
                    && oldpledgeBondList !== JSON.stringify(object.bussRepoSec.pledgeBondList))
                    || (object.outDataTable.length === 0
                        && oldpledgeBondList !== JSON.stringify(object.outDataTable))) {
                    oldpledgeBondList = JSON.stringify(object.bussRepoSec.pledgeBondList);
                    oldOutBondList = JSON.stringify(object.outDataTable);
                    outOk();
                }
            }, 2000);
        }
        setBussRepoSecInput();
    }
};
/**
 * 获取换出质押券查询参数
 */
var getPageParams = function() {
    var pageParam = {
        branch:$('#branch').val(),
        finprodId: $('#tradeId').val()
    };
    return pageParam;
};
    /**
     * 定义换出质押券数据表格
     */
    var outTable;
    function  getoutDataTable() {

     outTable=$("#outDataTable").joyintable({
         data:entity_outBussRepoSec? entity_outBussRepoSec: [],
         cols:[
            {field:'bondCode',sortField:'bondCode', width:100, title: '债券代码'}
            ,{field:'bondAbbrName', sortField:'bondAbbrName',width:200, title: '债券简称'}
            ,{field:'pledgePrin',sortField:'pledgePrin', width:200, title: '质押券面总额（万元）',type:'money'}
            ,{field:'prin',sortField:'prin', width:200, title: '换出面额（万元）',edit: true,type:'money'}
            ,{field:'pledgeRatio', sortField:'pledgeRatio',width:140, title: '折算比例%',type:'money'}

        ],
         // 编辑单元格
         editCell : function(value, data, field, cell, column) {
             if(field=='prin'){

                     if(JoyinMath.doMinus(data.prin,data.pledgePrin)>0)
                     {
                         Joyin.error("换出面额不能大于券面质押总额");
                         return false;
                     }
                     //换出质押券换出债券汇总券面总额计算值sum(换出面额)
                     if (outTable.getAllRows() != null && outTable.getAllRows().length > 0) {

                         var outSumAmtValue = 0;
                         for (var i = outTable.getAllRows().length - 1; i >= 0; i--) {
                             outSumAmtValue = JoyinMath.doAdd(outSumAmtValue, outTable.getAllRows()[i].prin * outTable.getAllRows()[i].pledgeRatio/100);
                         }
                         $("#outSumAmt").val(outSumAmtValue);//换出质押券换出债券汇总券面总额计算值
                     }
                 outOk();//计算最终质押券列表中数据
             }
         },
        searchOnInit: true,
        //显示选择框 add by yzg
        //checkbox:false
    });

    }


/**
 * 设置添加质押券共通参数
 */
var setBussRepoSecInput = function () {
    var data = {
        portfolioId: $('#portfolioId').val(),
        ps: $("#ps").val(),
        secAcctId: $('#secAcctId').val(),
        repoType: $('#repoType').val(),
        chlId: $('#chlId').val(),
        tradeMarket:$('#tradeMarket').val(),
        substitution:'yes',
        mdate:$('#mdate').val()
    };
    var object = {
        portfolioId: ['portfolioId', 'TrdRepoTrade002.portfolioId'],
        ps: ['ps', 'TrdRepoTrade002.ps'],
        secAcctId: ['secAcctId', 'TrdRepoTrade002.secAcctId'],
        chlId: ['chlId', 'TrdRepoTrade002.chlId'],
        repoType: ['repoType', 'TrdRepoTrade002.repoType'],
//        mdate: ['mdate', 'TrdRepoTrade002.mdate'],
        tradeMarket: ['tradeMarket', 'TrdRepoTrade002.tradeMarket'],
        data: data
    };
    window.BussRepoSec["bussRepoSec"].setInputParam(object);
};

    /**
     * 定义最终质押券数据表格
     */
    var resultTable=$("#bussResultRepoSec").joyintable({
        searchOnInit: true,
        checkbox: true,
        page: true,
        data:bussResultRepoSecMsg? bussResultRepoSecMsg: [],
        cols:[
            {field:'bondCode',sortField:'bondCode', width:100, title: '债券代码'}
            ,{field:'bondAbbrName', sortField:'bondAbbrName',width:200, title: '债券简称'}
            ,{field:'pledgePrin',sortField:'avaPledgePrin', width:200, title: '质押券面总额（万元）',type:'money'}
            ,{field:'firstNetPrice',sortField:'firstNetPrice', width:150, title: '首次净价',type:'money'}
            ,{field:'pledgeRatio', sortField:'pledgeRatio',width:140, title: '折算比例%',type:'money'}

        ]
    });

/**
 * 根据交易方向值来判断相关值显示
 * @param array
 * @returns {boolean}
 */
    function  isShowDiv(ps) {

        if(ps==TRADE_PS_03||ps==TRADE_PS_04)
        {
            //质押式回购
            $("#bondLendBasisDiv").hide();
            $("#bondLendMoneyDiv").hide();
            $("#actCapDaysDiv").show();
            $("#fTradeIdDiv").show();
            $("#repoDateDiv").show();
            $("#repoBasisDiv").show();
            $("#repoMoneyDiv").show();
        }
        if(ps==TRADE_PS_05||ps==TRADE_PS_06)
        {
            // 债券借贷
            $("#bondLendBasisDiv").show();
            $("#bondLendMoneyDiv").show();
            $("#actCapDaysDiv").hide();
            $("#fTradeIdDiv").hide();
            $("#repoDateDiv").hide();
            $("#repoBasisDiv").hide();
            $("#repoMoneyDiv").hide();
            //置换费用if 交易方向 = 融入时 500 else 0 end if
            var workflowId=$('#workflowId').val();
            if(workflowId=='') {
                if (ps == TRADE_PS_05) {
                    $("#substitutionAmt").val(500);
                }
                else {
                    $("#substitutionAmt").val(0);
                }
            }
        }

    }
/**
 * 选择换出债券确认按钮 -》最终质押券信息汇总
 * @param array
 * @returns {boolean}
 */
var outOk = function () {
    //确认按钮前清空最终质押券列表中数据
    var workflowId=$('#workflowId').val();
debugger;
    if(workflowId!='')
    {

        //流程中换出质押券换出债券汇总券面总额计算值sum(换出面额)
        if (entity_outBussRepoSec!= null && entity_outBussRepoSec.length > 0) {

            var outSumAmtValue = 0;
            for (var i = entity_outBussRepoSec.length - 1; i >= 0; i--) {
                outSumAmtValue = JoyinMath.doAdd(outSumAmtValue, entity_outBussRepoSec[i].prin * entity_outBussRepoSec[i].pledgeRatio/100 );
            }
            $("#outSumAmt").val(outSumAmtValue);//换出质押券换出债券汇总券面总额计算值
        }
        if(!initFlag){
            //清空最终质押券列表
            resultTable.clear(function () {
            });
        }
    }
    else
    { //清空最终质押券列表
        resultTable.clear(function () {
        });
    }


    //业务逻辑：最终质押券信息数据=选出质押券列表中未选中的数据+选入质押券列表中的所有数据
    var object = $.serializeToObject('#submitForm');
    var outSelectedCheckdRows =[];//选择换出质押券列表中选中的数据(prin >0)
    var outSelectedAllRows = outTable.getAllRows();//选择换出的质押券列表中所有的数据
    var outSelectedNoCheckdRows = [];//选择换出的质押券列表中未选择的数据
    var inSelectedAllRows = object.bussRepoSec.pledgeBondList;// 选择选入的质押券列表中的数据
    var resultRows = resultTable.getAllRows();// 最终质押券信息列表

    // 获取换出的质押券列表中已选择的数据和未选择的数据，换出金额》0则表示已选择否则为未选择
    if (outSelectedAllRows != null && outSelectedAllRows.length > 0) {

        for (var i = outSelectedAllRows.length - 1; i >= 0; i--) {

            if(outSelectedAllRows[i].prin==undefined ||outSelectedAllRows[i].prin==0)
            {
                outSelectedNoCheckdRows.push(outSelectedAllRows[i]);
            }
            else
            {
                outSelectedCheckdRows.push(outSelectedAllRows[i]);
            }

        }
    }


    /*//从选出质押券列表中 将未选中的列表数据加入到最终质押券列表中
    if (outSelectedNoCheckdRows != null && outSelectedNoCheckdRows.length > 0) {

        for (var i = outSelectedNoCheckdRows.length - 1; i >= 0; i--) {

            resultTable.addRow(outSelectedNoCheckdRows[i]);

            }
        }*/

    //从选出质押券列表中 将表数据加入到最终质押券列表中
    if (outSelectedCheckdRows != null && outSelectedCheckdRows.length > 0) {

    	if(!initFlag){
            for (var i = outSelectedCheckdRows.length - 1; i >= 0; i--) {
                resultTable.addRow(outSelectedCheckdRows[i]);
            }	
    	}
    }
        //2将选入质押券列表中 数据加入到最终质押券列表中，存在则不添加
    if (inSelectedAllRows != null && inSelectedAllRows.length > 0 ) {

        //先将选入列表中的数据先添加到最终列表中
        var resultArrlist = [];//最终质押列表中bondCode list

        for (var k = resultTable.getAllRows().length - 1; k >= 0; k--) {
            resultArrlist.push(resultTable.getAllRows()[k].bondCode);
        }
        for (var k = inSelectedAllRows.length - 1; k >= 0; k--) {
            //如果已存在最终质押券列表中则不添加

            if ($.inArray(inSelectedAllRows[k].bondCode, resultArrlist) == -1) {
                var newdate = inSelectedAllRows[k];
                resultTable.addRow(newdate);
                resultArrlist.push(newdate.bondCode);
            }

        }
    }
        //3重新计算最终质押券列表中的数据

        for (var k = resultTable.getAllRows().length - 1; k >= 0; k--) {

            //循环最终质押券列表中数据和选出列表的数据，如果最终质押券列表中和选出列表数据重合，则重新计算最终质押券列表中质押券面额=选出列表的质押券面总额-换出面额+换入列表中质押券面额
            if (outTable.getAllRows() != null && outTable.getAllRows().length > 0) {
                for (var iii = outTable.getAllRows().length - 1; iii >= 0; iii--) {

                    if (resultTable.getAllRows()[k].bondCode == outTable.getAllRows()[iii].bondCode) {
                        //重新计算质押券面总额
                        var avaPledgePrinValue = JoyinMath.doMinus(outTable.getAllRows()[iii].pledgePrin, outTable.getAllRows()[iii].prin);
                        /*  $('body').on('change','#avaPledgePrin',function(event,data){
                                     resultTable.renderRow(j, {avaPledgePrin:avaPledgePrinValue});
                                 });*/
                        //如果当前数据在换入列表中也存在，则加上 换入列表中对应的质押券面额
                        if(inSelectedAllRows!=null && inSelectedAllRows.length>0 ) {
                            for (var kk = inSelectedAllRows.length - 1; kk >= 0; kk--) {
                                if (inSelectedAllRows[kk].bondCode == resultTable.getAllRows()[k].bondCode) {
                                    avaPledgePrinValue=JoyinMath.doAdd(avaPledgePrinValue,inSelectedAllRows[kk].pledgePrin);
                                }
                            }
                        }
                        var newdate = resultTable.getAllRows()[k];
                        newdate.pledgePrin = avaPledgePrinValue;
                        resultTable.deleteRow(k);
                        resultTable.addRow(newdate);
                    }
                }

        }
    }

    //最终质押券汇总券面总额
    if (resultTable.getAllRows() != null && resultTable.getAllRows().length > 0) {
        var sumAmtValue = 0;
        for (var i = resultTable.getAllRows().length - 1; i >= 0; i--) {
            sumAmtValue = JoyinMath.doAdd(sumAmtValue, resultTable.getAllRows()[i].pledgePrin * resultTable.getAllRows()[i].pledgeRatio/100);

        }
        $("#sumAmt").val(sumAmtValue);

    }
    else {
        $("#sumAmt").val('');
    }

    if(workflowId!='')
    {

        //流程中换出质押券换出债券汇总券面总额计算值sum(换出面额)
        if (bussResultRepoSecMsg!= null && bussResultRepoSecMsg.length > 0) {

            var sumAmtValue = 0;
            for (var i = bussResultRepoSecMsg.length - 1; i >= 0; i--) {
                sumAmtValue = JoyinMath.doAdd(sumAmtValue, bussResultRepoSecMsg[i].pledgePrin*bussResultRepoSecMsg[i].pledgeRatio/100);
            }
            $("#sumAmt").val(sumAmtValue);//换出质押券换出债券汇总券面总额计算值
        }
    }

};

/**
 * 提交和保存
 */
function saveOrSubmit(operateType) {
    var object = $.serializeToObject('#submitForm');
    object.balanceCheck ='Y';
    var inSelectedAllRows=object.bussRepoSec.pledgeBondList;
    var isCommit=false;//是否提交
    var ps= $('#ps').val();
    //换入债券的质押券面额小于换出面额时，提交时应该提醒"换入债券小于换出债券，是否确认？"
    if (outTable.getAllRows() != null && outTable.getAllRows().length > 0) {
        isCommit = false;
        //获取换入质押券汇总面额
        var inSumPledgePrin = 0;
        if (inSelectedAllRows != null && inSelectedAllRows.length > 0) {
            for (var kk = inSelectedAllRows.length - 1; kk >= 0; kk--) {
                inSumPledgePrin = JoyinMath.doAdd(inSumPledgePrin, inSelectedAllRows[kk].pledgePrin);
                //债券借贷融入交易做置换，换入债券的质押券面大于可质押券面时要弹出提示：质押券面额不能大于可质押券面总额
                if(ps==TRADE_PS_05)
                {
                    if(JoyinMath.doMinus(inSelectedAllRows[kk].pledgePrin,inSelectedAllRows[kk].avaPledgePrin)>0)
                    {
                        Joyin.error("质押券面额不能大于可质押券面总额");
                        isCommit=true;
                        return false;
                    }
                }
            }
        }
        if(JoyinMath.doMinus(inSumPledgePrin,$("#outSumAmt").val())<0 && inSelectedAllRows.length > 0)
        {
            Joyin.confirm("换入债券小于换出债券，是否确认？",function() {
                if(object.bussRepoSec){
                    if( object.bussRepoSec.totalPrinAmt){
                        object.bussRepoSec.totalPrinAmt =object.bussRepoSec.totalPrinAmt*10000;
                    }
                    if(object.bussRepoSec.pledgeBondList){
                        var index = object.bussRepoSec.pledgeBondList.length;
                        for(var i = 0 ;i<index;i++){
                            if(object.bussRepoSec.pledgeBondList[i].avaPledgePrin){
                                object.bussRepoSec.pledgeBondList[i].avaPledgePrin = object.bussRepoSec.pledgeBondList[i].avaPledgePrin*10000;
                            }
                            object.bussRepoSec.pledgeBondList[i].pledgePrin = object.bussRepoSec.pledgeBondList[i].pledgePrin*10000
                            object.bussRepoSec.pledgeBondList[i].prin = object.bussRepoSec.pledgeBondList[i].prin*10000
                        }
                    }
                }
                if(object.outDataTable){
                    var index = object.outDataTable.length;
                    for(var i = 0 ;i<index;i++){
                        object.outDataTable[i].avaPledgePrin = object.outDataTable[i].avaPledgePrin*10000;
                        object.outDataTable[i].pledgePrin = object.outDataTable[i].pledgePrin*10000
                        object.outDataTable[i].prin = object.outDataTable[i].prin*10000
                    }
                }

                //提交前计算最终质押券列表中的值
                outOk();
                object.operateType = operateType;
                if (FileCommon) {
                    object.uploadFileList = FileCommon.getUploadFileInfo();
                }
                Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade004/check', object, function (result) {
                    Joyin.confirm(result.data, function () {

                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade004/save', object, "TrdRepoTrade004_btn_save", "TrdRepoTrade004", function () {

                            });
                        }
                        else {
                            WorkFlow.doSubmitList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade004/save', object, "TrdRepoTrade004_btn_submit", "TrdRepoTrade004", function () {

                            });
                        }
                    });
                });

            });
        }
        else
        {
            isCommit=true;
        }
    }
    else
    {
        isCommit=true;
    }
    if(isCommit)
    {
        //提交前计算最终质押券列表中的值
        outOk();
        object.operateType = operateType;
        if (FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo();
        }
        Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade004/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                if(object.bussRepoSec){
                    if( object.bussRepoSec.totalPrinAmt){
                        object.bussRepoSec.totalPrinAmt =object.bussRepoSec.totalPrinAmt*10000;
                    }
                    if(object.bussRepoSec.pledgeBondList){
                        var index = object.bussRepoSec.pledgeBondList.length;
                        for(var i = 0 ;i<index;i++){
                            if(object.bussRepoSec.pledgeBondList[i].avaPledgePrin){
                                object.bussRepoSec.pledgeBondList[i].avaPledgePrin = object.bussRepoSec.pledgeBondList[i].avaPledgePrin*10000;
                            }
                            object.bussRepoSec.pledgeBondList[i].pledgePrin = object.bussRepoSec.pledgeBondList[i].pledgePrin*10000
                            object.bussRepoSec.pledgeBondList[i].prin = object.bussRepoSec.pledgeBondList[i].prin*10000
                        }
                    }
                }
                if(object.outDataTable){
                    var index = object.outDataTable.length;
                    for(var i = 0 ;i<index;i++){
                        object.outDataTable[i].avaPledgePrin = object.outDataTable[i].avaPledgePrin*10000;
                        object.outDataTable[i].pledgePrin = object.outDataTable[i].pledgePrin*10000
                        object.outDataTable[i].prin = object.outDataTable[i].prin*10000
                    }
                }

                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade004/save', object, "TrdRepoTrade004_btn_save", "TrdRepoTrade004", function () {

                    });
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade004/save', object, "TrdRepoTrade004_btn_submit", "TrdRepoTrade004", function () {

                    });
                }
            });
        });
    }

}