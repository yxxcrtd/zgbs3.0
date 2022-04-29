(function () {
    // 报价修改时组合信息只可以添加和修改额度，不允许删除(初始化的组合信息不允许删除，手工添加的组合信息可以删除)
    var initEntrustId = [];
    if (entity) {
        for (var index in entity) {
            initEntrustId.push(entity[index].entrustId);
        }
    }

    var tempAvaNum ;
    /**
     * 登记组合分配列表
     * @type {*[]}
     */
    var colsReg = [
        {field:'entrustId', width:180, title: '指令代码',sortable: false,disabled:true}
        ,{field:'portfolioId', width:150,  title: '组合代码',edit:false,sortable: false,
            editOption:{type:'select', id:'portfolioId', multiple: false, showField:'text',searchable: true,
                url: JoyinUtil.getRealPath('/business/common/CommonSelect/selectPortfolioId?act=true&&auth=false')
            }
        }
        ,{field:'portfolioName', width:160,  title: '组合名称',edit:false,sortable: false,
        }
        ,{field:'avaNum', width:160, title: '剩余额度（万元）',type:'number',sortable: false}
        ,{field:'shareAmt', width:150, title: '券面总额（万元）',sortable: false,type:'number', digit:2}
        ,{field:'ourEntNum', width:150, title: '本方券券面总额（万元）',sortable: false,type:'number', digit:2,visible:sellPara=="02"&&ps!='T03'?true:false}
        ,{field:'lendinEntNum', width:150, title: '融入券券面总额（万元）',sortable: false,type:'number', digit:2,visible:sellPara=="02"&&ps=='T02'?true:false}
        ,{field:'sellAbleNum', width:150, title: '卖空购回券面总额（万元）',sortable: false,type:'number', digit:2,visible:sellPara=="02"&&ps=='T01'?true:false}
        ,{field:'dealFeeAmt', width:150, title: '交易手续费（元）',sortable: false,type:'number', digit:2}
        ,{field:'settFeeAmt', width:150, title: '结算服务费（元）',sortable: false,type:'number', digit:2}
        ,{field:'cpriceAmt', width:150, title: '净价总额（元）',sortable: false,type:'number', digit:2,visible:ps!='T03'?true:false}
        ,{field:'intAmt', width:150, title: '应计利息总额（元）',sortable: false,type:'number', digit:2,visible:ps!='T03'?true:false}
        ,{field:'fpriceAmt', width:150, title: '全价总额（元）',sortable: false,type:'number', digit:2,visible:ps!='T03'?true:false}
        ,{field:'businessModel', width:100, title: '业务模式', sortable: false
            ,editOption:{type:'select',id:"businessModel",formatoption:true,result:BUSINESS_MODEL}
            // , visible:SHOW_INVAIM_OR_BUSSINSSMODEL=='02' && ps!='T03'?true:false }
            , visible: ps!='T03'?true:false }
        ,{field:'priceModel', width:100, title: '价格模式', sortable: false, edit:true
            ,editOption:{type:'select',id:"priceModel",formatoption:true,result:BUSINESS_MODEL}
            // , visible:SHOW_INVAIM_OR_BUSSINSSMODEL=='02'?true:false }
            , visible:true }
        ,{field:'secAcctName', width:150, title: '证券账户',sortable: false}
        ,{field:'price', width:120, title: '净价(元)',sortable: false,type:'number', digit:2}
        ,{field:'unitFprice', width:120, title: '全价(元)',sortable: false,type:'number', digit:2,visible:ps=='T03'?true:false}
        ,{field:'matureYield', width:120, title: '到期收益率%',edit:false,sortable: false,visible: true,type:'number', digit:4,visible:ps!='T03'?true:false}
        ,{field:'exerYield', width:120, title: '行权收益率%',sortable: false,type:'number', digit:4,visible:ps!='T03'?true:false}
        ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt'],
            render(data, type, row, meta) {
                // 报价修改时组合信息只可以添加和修改额度，不允许删除(初始化的组合信息不允许删除，手工添加的组合信息可以删除)
                if (quotaPageModel && quotaPageModel == 'edit' && initEntrustId.indexOf(row.entrustId) > -1) {
                    return "";
                }else if(quotaPageModel && quotaPageModel == 'new'){
                    return "";
                }
                else {
                    return '<i class="btn layui-icon delt" title="删除">&#xe640;</i>';
                }
            }
        }//编辑和删除事件
    ];
    /**
     * 对话和请求组合列表
     * @type {*[]}
     */
    var colsFreg = [
        {field:'entrustId', width:180, title: '指令代码',sortable: false,disabled:true}
        ,{field:'portfolioId', width:150,  title: '组合代码',edit:false,sortable: false,
            editOption:{type:'select', id:'portfolioId', multiple: false, showField:'text',searchable: true,
                url: JoyinUtil.getRealPath('/business/common/CommonSelect/selectPortfolioId?act=true&&auth=false')
            }
        }
        ,{field:'portfolioName', width:160,  title: '组合名称',edit:false,sortable: false,
        }
        /*,{field:'businessModel', width:100, title: '业务模式', sortable: false, edit:true
            ,editOption:{type:'select',id:"businessModel",formatoption:true,result:BUSINESS_MODEL}
            , visible:SHOW_INVAIM_OR_BUSSINSSMODEL=='02'?true:false }*/
        ,{field:'entrustAvaNum', width:160, title: '剩余额度（万元）',type:'number',sortable: false}
        ,{field:'avaNum', width:150, title: '券面总额（万元）',edit:true,sortable: false,type:'number', digit:2}
        ,{field:'price', width:120, title: '净价(元)',sortable: false,type:'number', digit:4}
        ,{field:'matureYield', width:120, title: '到期收益率%',edit:false,sortable: false,visible: true,type:'number', digit:4}
        ,{field:'exerYield', width:120, title: '行权收益率%',sortable: false,type:'number', digit:4}
        ,{field:'dealFeeAmt', width:150, title: '交易手续费（元）',edit:true,sortable: false,type:'number', digit:2}
        ,{field:'settFeeAmt', width:150, title: '结算服务费（元）',edit:true,sortable: false,type:'number', digit:2}
        // ,{field:'settleAmt', width:150, title: '结算金额',sortable: false,type:'number', digit:2}
        , {
            field: 'proAvailableCash',
            width: 120,
            title: '组合T日可用现金(元)',
            sortable: false,
            type: 'number',
            digit: 2
        }, {
            field: 'secManageAvailablePosition',
            width: 120,
            title: '组合T日可用持仓(元)',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: true
        }
        ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt'],
            render(data, type, row, meta) {
                // 报价修改时组合信息只可以添加和修改额度，不允许删除(初始化的组合信息不允许删除，手工添加的组合信息可以删除)
                if (quotaPageModel && quotaPageModel == 'edit' && initEntrustId.indexOf(row.entrustId) > -1) {
                    return "";
                }else if(quotaPageModel && quotaPageModel == 'new'){
                    return "";
                }
                else {
                    return '<i class="btn layui-icon delt" title="删除">&#xe640;</i>';
                }
            }
        }//编辑和删除事件
    ];
    var table=$("#cfetsQuoTedCommonTable").joyintable({
        data:entity==null?[]:entity,
        searchOnInit: true,
        loadonce: true,
        page: false,
        addbtn: false,
        height : 200,
        cols:quoteType=="04"?colsReg:colsFreg,
        editCell: function(value, data, field, cell, column){
            if(Number(value) > Number(data.entrustAvaNum)){
                table.renderRow(data.LAY_TABLE_INDEX, {"avaNum": tempAvaNum});
                Joyin.error("券面总额不得大于剩余额度!");
            }
            if (field == "portfolioId") {
                var rowData = {};
                if (value != null && value !== '') {

                    var datas = {
                        portfolioId:data.portfolioId,
                        ccy:$('#CfetsBondDealQuotedPrice005_settleCcy').val()?$('#CfetsBondDealQuotedPrice005_settleCcy').val():$('#CfetsBondDealQuotedPriceMuti002_settleCcy').val(),
                        tradeDate:$('#CfetsBondDealQuotedPrice005_tradeDate').val()?$('#CfetsBondDealQuotedPrice005_tradeDate').val():$('#CfetsBondDealQuotedPriceMuti002_tradeDate').val(),
                        calendarId:"",
                        acctTypeList: "A01,A07",
                        finprodId:$('#CfetsBondDealQuotedPrice005_hidden_finprodId').val()?$('#CfetsBondDealQuotedPrice005_hidden_finprodId').val():$('#CfetsBondDealQuotedPriceMuti002_hidden_finprodId').val()
                    };
                    if(quoteType == '01'){
                        datas = {
                            portfolioId:data.portfolioId,
                            ccy:$('#CfetsBondDealQuotedPrice004_settleCcy').val()?$('#CfetsBondDealQuotedPrice004_settleCcy').val():$('#CfetsBondDealQuotedPriceMuti001_settleCcy').val(),
                            tradeDate:$('#CfetsBondDealQuotedPrice004_tradeDate').val()?$('#CfetsBondDealQuotedPrice004_tradeDate').val():$('#CfetsBondDealQuotedPriceMuti001_tradeDate').val(),
                            calendarId:"",
                            acctTypeList: "A01,A07",
                            finprodId:$('#CfetsBondDealQuotedPrice004_hidden_finprodId').val()?$('#CfetsBondDealQuotedPrice004_hidden_finprodId').val():$('#CfetsBondDealQuotedPriceMuti001_hidden_finprodId').val()
                        };
                    }
                    datas.multiParam = 100;
                    datas.secAcctId =obj.secManageAcctId;
                    Joyin.ajaxJson(JoyinUtil.getRealPath( '/component/trade/BussTrdAvailableCashQuery/getAvailableCash'), datas, function (result) {
                        if (result) {
                            if (result) {
                                rowData.proAvailableCash = result.T_0;
                                rowData.secManageAvailablePosition = result.position;
                            }
                            table.renderRow(data.LAY_TABLE_INDEX, rowData);
                        }
                    });
                }else{
                    rowData.proAvailableCash = ' ';
                    rowData.secManageAvailablePosition = ' ';
                    table.renderRow(data.LAY_TABLE_INDEX, rowData);
                }
            }
        },
        beforeEditCell: function (preVal, field, rowData) {
            if (field === "avaNum") {
                tempAvaNum = preVal.replace(/\,/g, '');
            }
            return true;
        },
        done: function () {
            if(quoteType == '01'){
                if(window.CfetsBondDealQuotedPrice004){
                    window.CfetsBondDealQuotedPrice004.disabledElementByDzh(table.getAllRows());
                }
                if(window.CfetsBondDealQuotedPriceMuti001){
                    window.CfetsBondDealQuotedPriceMuti001.disabledElementByDzh(table.getAllRows());
                }
            }else{
                if(window.CfetsBondDealQuotedPrice005){
                    window.CfetsBondDealQuotedPrice005.disabledElementByDzh(table.getAllRows());
                }
                if(window.CfetsBondDealQuotedPriceMuti002){
                    window.CfetsBondDealQuotedPriceMuti002.disabledElementByDzh(table.getAllRows());
                }
            }
        }
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        table.removeRow(data);
    });

    /**
     *  组合分配列表添加操作，确认后 重新刷新列表
     * @param result
     */
    var reloadPtlAllocation = function(result){
        for (var i in result) {
            var obj = result[i];
            if (obj != null && obj !== '') {
                var datas = {
                    portfolioId:obj.portfolioId,
                    ccy:$('#CfetsBondDealQuotedPrice005_settleCcy').val()?$('#CfetsBondDealQuotedPrice005_settleCcy').val():$('#CfetsBondDealQuotedPriceMuti002_settleCcy').val(),
                    tradeDate:$('#CfetsBondDealQuotedPrice005_tradeDate').val()?$('#CfetsBondDealQuotedPrice005_tradeDate').val():$('#CfetsBondDealQuotedPriceMuti002_tradeDate').val(),
                    calendarId:"",
                    acctTypeList: "A01,A07",
                    finprodId:$('#CfetsBondDealQuotedPrice005_hidden_finprodId').val()?$('#CfetsBondDealQuotedPrice005_hidden_finprodId').val():$('#CfetsBondDealQuotedPriceMuti002_hidden_finprodId').val()
                };
                if(quoteType == '01'){
                    datas = {
                        portfolioId:obj.portfolioId,
                        ccy:$('#CfetsBondDealQuotedPrice004_settleCcy').val()?$('#CfetsBondDealQuotedPrice004_settleCcy').val():$('#CfetsBondDealQuotedPriceMuti001_settleCcy').val(),
                        tradeDate:$('#CfetsBondDealQuotedPrice004_tradeDate').val()?$('#CfetsBondDealQuotedPrice004_tradeDate').val():$('#CfetsBondDealQuotedPriceMuti001_tradeDate').val(),
                        calendarId:"",
                        acctTypeList: "A01,A07",
                        finprodId:$('#CfetsBondDealQuotedPrice004_hidden_finprodId').val()?$('#CfetsBondDealQuotedPrice004_hidden_finprodId').val():$('#CfetsBondDealQuotedPriceMuti001_hidden_finprodId').val()
                    };
                }
                datas.multiParam = 100;
                datas.secAcctId =obj.secManageAcctId;
                Joyin.ajaxJson(JoyinUtil.getRealPath( '/component/trade/BussTrdAvailableCashQuery/getAvailableCash'), datas, function (result) {
                    if (result) {
                        obj.proAvailableCash = result.T_0;
                        obj.secManageAvailablePosition = result.position;
                    }
                    table.addRow(obj);
                },false);

            }else{
                obj.proAvailableCash = ' ';
                obj.secManageAvailablePosition = ' ';
                table.addRow(obj);
            }
        }
    }

    var clearTable = function(){
        table.clear();
    }

    window.CfetsQuotedCommon001 = {
        reloadPtlAllocation: reloadPtlAllocation,
        clearTable: clearTable
    };


    /**
     * 画面加载完成
     */
    $(function () {
        // if(quotaPageModel&&quotaPageModel=="new"){
        //     $('#CfetsQuotedCommon001_dzh_add_btn').hide();
        // }
        $("#cfetsQuoTedCommon_btn_autoAllocation").click(function(){
            if(!$("#cfetsQuoTedCommon_distribution").val()){
                Joyin.error("请选择分配方式!");
                return ;
            }
            if(!getElementValue("avaNum") ){
                Joyin.error("请填券面总额!");
                return;
            }
            if(table.getAllRows().length==0){
                Joyin.error("组合分配列表为空，请添加!");
                return;
            }

            // 检查券面总额是否超过组合分配中所有指令的剩余额度汇总
            var entrustAvaNumTotal = 0;
            $.each(table.getAllRows(),function (i,e) {
                    entrustAvaNumTotal = JoyinMath.doAdd(entrustAvaNumTotal, e.entrustAvaNum);
                }
            );
            if (Number(getElementValue("avaNum")) > entrustAvaNumTotal) {
                Joyin.error("券面总额超过组合分配列表中所有指令的剩余额度汇总，请修改券面总额!");
                return;
            }

            if ($("#cfetsQuoTedCommon_distribution").val()=="139"){//平均分配
                avgMethod();

            }else if ($("#cfetsQuoTedCommon_distribution").val()=="143"){//比例分配
                proportionalMethod();
            }
        });

        /**
         * 组合分配 添加
         */
        $('#CfetsQuotedCommon001_dzh_add_btn').click(function(){
            //添加后列表 初始化页面 排除已经存在的分配
            var entrustIds = "";
            var tableResult = table.getAllRows();
            for(var i in tableResult){
                entrustIds += tableResult[i].entrustId+",";
            }
            var ps = $('#CfetsBondDealQuotedPrice005_ps').val()?$('#CfetsBondDealQuotedPrice005_ps').val():$('#CfetsBondDealQuotedPriceMuti002_ps').val();
            var mySide = $('#CfetsBondDealQuotedPrice005_mySide').val()?$('#CfetsBondDealQuotedPrice005_mySide').val():$('#CfetsBondDealQuotedPriceMuti002_mySide').val();
            var settleSpreed = $('#CfetsBondDealQuotedPrice005_settleSpreed').val()?$('#CfetsBondDealQuotedPrice005_settleSpreed').val():$('#CfetsBondDealQuotedPriceMuti002_settleSpreed').val();
            var finprodId = $('#CfetsBondDealQuotedPrice005_hidden_finprodId').val()?$('#CfetsBondDealQuotedPrice005_hidden_finprodId').val():$('#CfetsBondDealQuotedPriceMuti002_hidden_finprodId').val();
            if(quoteType == '01'){
                ps = $('#CfetsBondDealQuotedPrice004_ps').val()?$('#CfetsBondDealQuotedPrice004_ps').val():$('#CfetsBondDealQuotedPriceMuti001_ps').val();
                mySide = $('#CfetsBondDealQuotedPrice004_mySide').val()?$('#CfetsBondDealQuotedPrice004_mySide').val():$('#CfetsBondDealQuotedPriceMuti001_mySide').val();
                settleSpreed = $('#CfetsBondDealQuotedPrice004_settleSpreed').val()?$('#CfetsBondDealQuotedPrice004_settleSpreed').val():$('#CfetsBondDealQuotedPriceMuti001_settleSpreed').val();
                finprodId = $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val()?$('#CfetsBondDealQuotedPrice004_hidden_finprodId').val():$('#CfetsBondDealQuotedPriceMuti001_hidden_finprodId').val();
            }
            Joyin.dialog(JoyinUtil.getLabel("CfetsRepoDealQuotedPrice005"),
                basePath + "invest/bankuplink/bond/CfetsBondDealQuotedPrice006/init", "99%", "80%", {
                    pageModel : PAGE_MODEL_NEW,
                    ps : ps,
                    /*mySide : mySide,*/
                    settleSpreed : settleSpreed,
                    finprodId : finprodId,
                    isDzh:"Y",
                    entrustIds:entrustIds
                })
        });

        var getElementValue=function (id){
            id = pageId+"_"+id;
            return $('#'+id).val();
        }

        var proportionalMethod =function (){
            var dealAmt = getElementValue("avaNum") ;
            //组合分配列表
            var ptlAllocationList = table.getAllRows();
            //升序排序
            ptlAllocationList = ptlAllocationList.sort(function (a,b) {
                return a.entrustAvaNum - b.entrustAvaNum;
            })
            //比例分配
            //分配汇总
            var avaTotal = 0;
            //剩余额度汇总
            var entrustAvaNumTotal = 0;
            $.each(ptlAllocationList,function (i,e) {
                entrustAvaNumTotal = JoyinMath.doAdd(entrustAvaNumTotal,e.entrustAvaNum);
            });
            $.each(ptlAllocationList,function (i,e) {
//                e.avaNum = JoyinMath.doMul(dealAmt,JoyinMath.doDiv(e.entrustAvaNum,entrustAvaNumTotal,6,1),2,1);
                e.avaNum = JoyinMath.doDiv(JoyinMath.doMul(dealAmt,e.entrustAvaNum,2,1),entrustAvaNumTotal,2,1);
                avaTotal = JoyinMath.doAdd(avaTotal,e.avaNum);
            });
            //尾差总数
            var tailDifference = JoyinMath.doMinus(dealAmt,avaTotal);
            handleTailDifference(tailDifference,ptlAllocationList,100);
            //刷新列表
            $.each(ptlAllocationList,function (i,e) {
                table.renderRow(e.LAY_TABLE_INDEX, {"avaNum": e.avaNum});
            });

        }

        var avgMethod =function (){

            var dealAmt = getElementValue("avaNum") ;
            //组合分配列表
            var ptlAllocationList = table.getAllRows();
            //升序排序
            ptlAllocationList = ptlAllocationList.sort(function (a,b) {
                return a.entrustAvaNum - b.entrustAvaNum;
            })
            //平均分配
//            if(distribution == '139'){
            var size = ptlAllocationList.length;
            //平均值
            var avg = JoyinMath.doDiv(dealAmt,size,2,1);
            //分配汇总
            var avaTotal = 0;
            $.each(ptlAllocationList,function (i,e) {
                    if (Number(e.entrustAvaNum) >= Number(avg)) {
                        e.avaNum = avg;
                    } else {
                        e.avaNum = e.entrustAvaNum;
                    }
                    avaTotal = JoyinMath.doAdd(avaTotal,e.avaNum);
                }
            );
            //尾差总数
            var tailDifference = JoyinMath.doMinus(dealAmt,avaTotal);
            handleTailDifference(tailDifference,ptlAllocationList,100);
            //刷新列表
            $.each(ptlAllocationList,function (i,e) {
                table.renderRow(e.LAY_TABLE_INDEX, {"avaNum": e.avaNum});
            });

        }

        /**
         * 尾差处理
         * @param tailDifference 尾差总数
         * @param list
         * @param unit 尾差单位
         */
        var  handleTailDifference = function(tailDifference,ptlAllocationList,unit){
            //尾差比例
            var entrustIdArr = [];
            var tailDifferencePre = JoyinMath.doDiv(1,unit,unit.length-1);
            var length = ptlAllocationList.length;
            while(Number(tailDifference)>0&&length>0){
                if(Number(tailDifference)>=1){
                    //尾差大于1 始终按委托条数平均拆分尾差
                    tailDifferencePre = JoyinMath.doDiv(tailDifference,length,unit.length-1);
                }
                $.each(ptlAllocationList,function (i,e) {
                    if(Number(e.avaNum) == Number(e.entrustAvaNum)||Number(tailDifference)<=Number(0)) {
                        if(entrustIdArr.indexOf(e.entrustId)<0){
                            entrustIdArr.push(e.entrustId);
                            length--;
                        }
                        return;
                    }else{
                        e.avaNum = JoyinMath.doAdd(e.avaNum,tailDifferencePre);
                        tailDifference = JoyinMath.doMinus(tailDifference,tailDifferencePre);
                    }
                });
            }
        }

    });

}());