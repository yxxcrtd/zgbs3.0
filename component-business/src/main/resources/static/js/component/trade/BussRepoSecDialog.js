(function () {
    if(entrustAmtUnit == '02' ){
        $(".unitNum").text('万元');
    }
    var avaPledgeBondDetailNeedInit = true;
    var pledgeBondDetailTblNeedInit = true;
    var getElementById = function (id) {
        return $("#" + entityName + "_" + id);
    };
    
    //产品化_逆回购时左侧可质押债券不预加载（即置空）add by zhangpei 20201117 start
    var avaPledgeBondDetailNeedInitFlag = true;
    if(getElementById("ps").val() == BACK_LEND_DIRECTION_04){
    	avaPledgeBondDetailNeedInitFlag=false;
    }
    //产品化_逆回购时左侧可质押债券不预加载（即置空）add by zhangpei 20201117 end

    /**
     * 定义可质押券列表（Detail）
     */
    var avaPledgeBondDetailTBL = getElementById("avaPledgeBondDetailList").joyintable({
        ajax: {
            url: 'selectAvaPledgeBondDetailList',
            type: 'POST',
            height:350,
            data: function () {
                var param = {};

                param.portfolioId = getElementById("portfolioId").val();
                param.ps = getElementById("ps").val();
                param.entrustId = getElementById("entrustId").val();
                param.chlId = getElementById("chlId").val();
                param.secAcctId = getElementById("secAcctId").val()? getElementById("secAcctId").val(): getElementById("securitiesCust").val();
                param.bondType = getElementById("bondType").val() ? getElementById("bondType").val().join(",") : '';
                var bondCodeSelObject = getElementById("bondCode").select2('data')[0] == null ?'':getElementById("bondCode").select2('data')[0].object;
                param.bondCode = bondCodeSelObject == null ? '' : bondCodeSelObject.finprodMarketId;
                param.repRegistOrg = getElementById("repRegistOrg").val();
                param.tradeMarket = typeof(tradeMarket)=="undefined"? '':tradeMarket=="undefined"?'':tradeMarket;
                // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 start
                param.endDays1Start = getElementById("endDays1Start").val();
                param.endDays1End = getElementById("endDays1End").val();
                // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 end
                
                //产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201125 start
                param.pageFromTz = getElementById("pageFromTz").val();
                //产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201125 end
                
                // 将质押列表数据传入后台，用于过滤已经全额质押的债券
                param.pledgeList = JSON.stringify(pledgeBondDetailTbl.getAllRows());
                // 获取请求参数
                
                // add by etliu 20210303 银行间上下行 start
                param.ignoreFlowTradeId = ignoreFlowTradeId;
                // add by etliu 20210303 银行间上下行 end
                param.isDzh = isDzh;
                // add by meifaxin 20210929 银行间 start
                param.tradePlace = tradePlace;
                // add by meifaxin 20210929 银行间 end
                param.mdate = mdate;
                return param;
            }
        },
        //产品化_逆回购时左侧可质押债券不预加载（即置空）add by zhangpei 20201117 start
        //searchOnInit: true,
        searchOnInit: avaPledgeBondDetailNeedInitFlag,
        //产品化_逆回购时左侧可质押债券不预加载（即置空）add by zhangpei 20201117 end
        //page: false,
        //loadonce: true,
        cols: [
            //add by zhangnian 20191126 start
            // 组合代码
            {
                field: 'portfolioId',
                width: 150,
                title: '组合代码',
                sortable: false,
                visible:(isDzh=='Y'&&getElementById('ps').val()=='03')?true:false
            }
            // 组合名称
            , {
                field: 'portfolioName',
                width: 150,
                title: '组合名称',
                sortable: false,
                visible:(isDzh=='Y'&&getElementById('ps').val()=='03')?true:false
            }
            , {
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
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.bondCode',
                sortable: false
            }
            // 债券简称
            , {
                field: 'bondAbbrName',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.bondAbbrName',
                sortable: false
            }
            // 券面总额
            , {
                field: 'prin',
                width: 150,
                // title: isDzh=='Y'?'券面总额(万元)':'BussRepoSec.avaPledgeBondDetailTBL.colmodel.prin',
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.prin1',
                sortable: false,
                type: 'money',
                digit: 4,
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            }
            // 已质押券面总额
            , {
                field: 'pledgePrin',
                width: 150,
                // title: isDzh=='Y'?'已质押券面总额(万元)':'BussRepoSec.avaPledgeBondDetailTBL.colmodel.pledgePrin',
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.pledgePrin1',
                sortable: false,
                type: 'money',
                digit: 4,
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            }
            // 已质押券面总额（初始）
            , {
                field: 'pledgePrinInit',
                width: 150,
                sortable: false,
                type: 'money',
                digit: 2,
                visible: false
            }
            // 可质押券面总额
            , {
                field: 'avaPledgePrin',
                width: 150,
                // title: isDzh=='Y'?'可质押券面总额(万元)':'BussRepoSec.avaPledgeBondDetailTBL.colmodel.avaPledgePrin',
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.avaPledgePrin1',
                sortable: false, type: 'money',
                digit: 4,
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            }
            // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 start
            // 债项评级
            , {
                field: 'gradeResultStr',
                width: 100,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.gradeResult',
                sortable: false,
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            }
            // 到期日
            , {
                field: 'mdate',
                width: 100,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.mdate',
                sortable: false, type: 'date',
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            }
            // 登记托管机构
            , {
                field: 'repRegistOrgStr',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.repRegistOrg',
                sortable: false,
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            }
            // 剩余期限
            , {
                field: 'endDays1',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.endDays1',
                sortable: false, type: 'number',
                digit: 2,
                visible: getElementById("ps").val() != BACK_LEND_DIRECTION_04
            },
            //add by chenjiaxing 质押式回购指令，选择质押券，首次净价为空 20210914 start
            {
                field: 'firstNetPrice',
                width: 150,
                title: '首次净价',
                sortable: false, type: 'number',
                digit: 2,
                visible: false
            },
            //add by chenjiaxing 质押式回购指令，选择质押券，首次净价为空 20210914 end

            // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 end
        ],
        done:function () {
            if(avaPledgeBondDetailNeedInit){
                avaPledgeBondDetailNeedInit = false;
                var ps = getElementById("ps").val();
                if(BACK_LEND_DIRECTION_04 == ps){
                    avaPledgeBondDetailTBL.showColumns([{field:'prin',visible:false},{field:'pledgePrin',visible:false},{field:'pledgePrinInit',visible:false},{field:'avaPledgePrin',visible:false}
                    // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 start
                    ,{field:'gradeResultStr',visible:false},{field:'mdate',visible:false},{field:'repRegistOrgStr',visible:false},{field:'endDays1',visible:false}]);
                    // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 end
                }
            }
        }
    });

    var tempPledgePrin;

    /**
     * 定义质押券列表（Detail）
     */
    var pledgeBondDetailTbl = getElementById("pledgeBondDetailList").joyintable({
        data: (entity && entity.pledgeBondList) ? entity.pledgeBondList : [],
        page: false,
        loadonce: true,
        height:350,
        cols: [
            //add by zhangnian 20191126 start
            // 组合代码
            {
                field: 'portfolioId',
                width: 150,
                title: '组合代码',
                sortable: false,
                visible:(isDzh=='Y'&&getElementById('ps').val()=='03')?true:false
            }
            // 组合名称
            , {
                field: 'portfolioName',
                width: 150,
                title: '组合名称',
                sortable: false,
                visible:(isDzh=='Y'&&getElementById('ps').val()=='03')?true:false
            }
            , {
                field: 'finprodId',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.bondCode',
                sortable: false,
                visible:false
            }
            //add by zhangnian 20191126 end
            // 债券代码
            ,{field: 'bondCode', width: 150, title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.bondCode', sortable: false}
            // 债券简称
            , {
                field: 'bondAbbrName',
                width: 150,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.bondAbbrName',
                sortable: false
            }
            // 可质押券面总额
            , {
                field: 'avaPledgePrin',
                width: 150,
                // title: isDzh=='Y'?'可质押券面总额(万元)':'BussRepoSec.pledgeBondDetailTbl.colmodel.avaPledgePrin',
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.avaPledgePrin1',
                type: 'money',
                digit: 4,
                sortable: false
            }
            // 入库券面总额
            , {
                field: 'pledgePrin',
                width: 150,
                // title: isDzh=='Y'?'质押券面总额(万元)':'BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrin',
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrin1',
                type: 'money',
                digit: 4,
                edit: true,
                sortable: false
            }
            // 入库券面总额（初始）
            , {
                field: 'pledgePrinInit',
                width: 150,
                type: 'money',
                digit: 2,
                sortable: false,
                visible: false
            }
            // 登记托管机构
            , {
                field: 'repRegistOrgStr',
                width: 150,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.repRegistOrg',
                sortable: false,
            }
            // 首次净价
            , {
                field: 'firstNetPrice',
                width: 100,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.firstNetPrice',
                sortable: false
            }
            // 质押比例
            , {
                field: 'pledgeRatio',
                width: 100,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.pledgeRatio',
                type: 'number',
                digit: 2,
                edit: true,
                sortable: false
            }
        ],
        beforeEditCell: function (preVal, field, rowData) {
            if (field === "pledgePrin") {
                tempPledgePrin = preVal.replace(/\,/g, '');
            }
            return true;
        },
        editCell: function (value, data, field, cell, column) {
            if (field === "pledgePrin" && getElementById("ps").val() == BACK_LEND_DIRECTION_03 && data.pledgePrin * 1 > data.avaPledgePrin * 1) {
                //CFET质押式支持软硬空
                if(getElementById("pageFromTz").val() == 'CfetsRepoDealQuotedPrice002'&&ibuFlag_repoOverAvaCanContinue=='02'){
                    Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_E_0003",
                        [JoyinUtil.getLabel("BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrin"),
                            JoyinUtil.getLabel("BussRepoSec.pledgeBondDetailTbl.colmodel.avaPledgePrin")]), function () {},'',function () {
                        var datas = pledgeBondDetailTbl.getAllRows();
                        datas[data.LAY_TABLE_INDEX].pledgePrin = tempPledgePrin;
                        pledgeBondDetailTbl.reload({data: datas});
                    })
                }else{
                    var datas = pledgeBondDetailTbl.getAllRows();
                    datas[data.LAY_TABLE_INDEX].pledgePrin = tempPledgePrin;
                    pledgeBondDetailTbl.reload({data: datas});
                    Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0003",
                        [JoyinUtil.getLabel("BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrin"),
                            JoyinUtil.getLabel("BussRepoSec.pledgeBondDetailTbl.colmodel.avaPledgePrin")]));
                }
            }
            // 汇总券面总额字段增加 add by renping 20190920 start
            refreshTotalPrinAmt();
            // 汇总券面总额字段增加 add by renping 20190920 end
            window.CfetsPledgedBond.refreshCfetsPldegeBond(pledgeBondDetailTbl.getAllRows());
        },
        done:function () {
            if(pledgeBondDetailTblNeedInit){
                pledgeBondDetailTblNeedInit = false;
                var ps = getElementById("ps").val();
                if(BACK_LEND_DIRECTION_04 == ps){
                    if(isDzh == 'Y'){
                        pledgeBondDetailTbl.showColumns([{field:'avaPledgePrin',visible:false}]);
                    }else{
                        pledgeBondDetailTbl.showColumns([{field:'avaPledgePrin',visible:false}
                            ,{field:'pledgePrin',visible:true,title:'BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrinTotle'}]);
                    }
                }
            }
            // 汇总券面总额字段增加 add by renping 20190920 start
            refreshTotalPrinAmt();
            // 汇总券面总额字段增加 add by renping 20190920 end
        }
    });

    // 汇总券面总额字段增加 add by renping 20190920 start
    /**
     * 计算汇总券面总额处理
     */
    var refreshTotalPrinAmt = function () {
        var allRows = pledgeBondDetailTbl.getAllRows();
        var totalPrinAmt = Number(0);
        for (var i in allRows) {
            var pledgeRatioVal = 100;
            if (allRows[i].pledgeRatio) {
                pledgeRatioVal = allRows[i].pledgeRatio;
            }
            var pledgePrinVal = 0;
            if (allRows[i].pledgePrin) {
                pledgePrinVal = allRows[i].pledgePrin;
            }
            // if (isDzh == 'Y') {
                //多组合以万元为单位
            if (entrustAmtUnit == '01') {
                totalPrinAmt += pledgePrinVal * pledgeRatioVal * 100;
            } else {
                totalPrinAmt += pledgePrinVal * pledgeRatioVal / 100;
            }
        }
        getElementById("totalPrinAmt").val(totalPrinAmt).trigger('change').trigger('blur');
    };
    // 汇总券面总额字段增加 add by renping 20190920 end

    /**
     * 校验行数据中登记托管机构是否不一致
     * @param array
     * @returns {boolean}
     */
    var checkIsDifferent = function (array) {
        var isDifferent = false;
        if (array != null && array.length > 1) {
            for (var index in array) {
                if (index < array.length - 1
                    && array[index].repRegistOrg != array[index * 1 + 1].repRegistOrg) {
                    isDifferent = true;
                    break;
                }
            }
        }
        return isDifferent;
    };

    /**
     * 弹出债券新建画面
     * @returns
     */
    function showSecCreateDialog() {
        var url = "home.html?a=a&openFromOldPage=Y&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a";
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "98%", "98%",
            param);

        // Joyin.dialog(JoyinUtil.getLabel('FinBondTargetAsset002.newTitle'), '/invest/asset/bond/FinBondTargetAsset002/init', '98%', '98%');
    }

    /**
     * 加入按钮按下
     * @param array
     * @returns {boolean}
     */
    var joyinProcess = function () {
        var avaSelectedRows = avaPledgeBondDetailTBL.getAllCheckedRows();
        var pledgeAllRows = pledgeBondDetailTbl.getAllRows();
        if (avaSelectedRows != null && avaSelectedRows.length > 0) {
            // 若添加的质押券不只一个，且登记托管机构不全相同，则保存质押券时提示质押券登记托管机构不同，不允许保存；
            // 如果 选择的待质押劵 和质押劵列表中债券托管机构不一致 则 提示债券托管机构不一致，不能添加！
            var isDuplicated = false;
            var mergeArray = new Array();
            mergeArray = _.concat(mergeArray, avaSelectedRows);
            mergeArray = _.concat(mergeArray, pledgeAllRows);
            if (checkIsDifferent(mergeArray)) {
                Joyin.error(JoyinUtil.getMessage("TRD_E_0007"));
                return false;
            }

            // 如果 选择的待质押劵 在质押劵列表中存在  则 提示 是否覆盖  质押劵列表中债券
            var isConfirm = false;
            var confirmMessageArray = new Array();

            // 倒序删除，正序会存在问题
            for (var i = avaSelectedRows.length - 1; i >= 0; i--) {
                for (var j in pledgeAllRows) {
                    // modify by etliu 20210326 银行间上下行多组合 start
                    if (isDzh == 'Y') {
                        if (pledgeAllRows[j].bondCode == avaSelectedRows[i].bondCode
                                && pledgeAllRows[j].portfolioId == avaSelectedRows[i].portfolioId) {
                            isConfirm = true;
                            confirmMessageArray.push("【债券代码：" + avaSelectedRows[i].bondCode + " 组合代码：" + avaSelectedRows[i].portfolioId + "】");
                            break;
                        }
                    }
                    else if (pledgeAllRows[j].bondCode == avaSelectedRows[i].bondCode) {
                        isConfirm = true;
                        confirmMessageArray.push(avaSelectedRows[i].bondCode);
                        break;
                    }
                    // modify by etliu 20210326 银行间上下行多组合 end
                }
            }
            if (isConfirm) {
                Joyin.confirm(JoyinUtil.getMessage("TRD_C_0003", confirmMessageArray.join("，")), function () {
                    //逐个删除未删除干净，获取全部集合，过滤掉已勾选的重新加载 edit by daizhiwei 20200222
                    var allAvaRows = avaPledgeBondDetailTBL.getAllRows();
                    // 倒序删除，正序会存在问题
                    for (var i = avaSelectedRows.length - 1; i >= 0; i--) {
                        allAvaRows = allAvaRows.filter(function(item){
                            return item.bondCode != avaSelectedRows[i].bondCode
                        });
                        for (var j in pledgeAllRows) {
                            // 覆盖质押券列表中重复记录
                            // modify by etliu 20210326 银行间上下行多组合 start
                            if (isDzh == 'Y') {
                                if (pledgeAllRows[j].bondCode == avaSelectedRows[i].bondCode
                                    && pledgeAllRows[j].portfolioId == avaSelectedRows[i].portfolioId) {
                                    pledgeBondDetailTbl.removeRow(pledgeAllRows[j]);
                                }
                            }
                            else if (pledgeAllRows[j].bondCode == avaSelectedRows[i].bondCode) {
                                pledgeBondDetailTbl.removeRow(pledgeAllRows[j]);
                            }
                            // modify by etliu 20210326 银行间上下行多组合 end
                        }
//                        avaPledgeBondDetailTBL.removeRow(avaSelectedRows[i]);
                        avaSelectedRows[i].pledgePrin = avaSelectedRows[i].avaPledgePrin;
                        if(avaSelectedRows[i].firstNetPrice != null && avaSelectedRows[i].firstNetPrice != '')
                        {
                            avaSelectedRows[i].pledgeRatio = (Number(avaSelectedRows[i].firstNetPrice) / 100 * 100).toFixed(2);
                        }
                        if(avaSelectedRows[i].pledgeRatio == null || avaSelectedRows[i].pledgeRatio == ''){
                            avaSelectedRows[i].pledgeRatio = 100;
                        }
                        pledgeBondDetailTbl.addRow(avaSelectedRows[i]);
                    }
                    avaPledgeBondDetailTBL.reload({data:allAvaRows});
                });
            } else {
                var allAvaRows = avaPledgeBondDetailTBL.getAllRows();
                // 倒序删除，正序会存在问题
                for (var i = avaSelectedRows.length - 1; i >= 0; i--) {
                    allAvaRows = allAvaRows.filter(function(item){
                        return item.bondCode != avaSelectedRows[i].bondCode
                    });
//                    avaPledgeBondDetailTBL.removeRow(avaSelectedRows[i]);
                    avaSelectedRows[i].pledgePrin = avaSelectedRows[i].avaPledgePrin;
                    if(avaSelectedRows[i].firstNetPrice != null && avaSelectedRows[i].firstNetPrice != '') {
                        avaSelectedRows[i].pledgeRatio = (Number(avaSelectedRows[i].firstNetPrice) / 100 * 100).toFixed(2);
                    }
                    if (avaSelectedRows[i].pledgeRatio == null || avaSelectedRows[i].pledgeRatio == ''){
                        avaSelectedRows[i].pledgeRatio = 100;
                    }
                    pledgeBondDetailTbl.addRow(avaSelectedRows[i]);
                }
                avaPledgeBondDetailTBL.reload({data:allAvaRows});
            }
        }
        // 汇总券面总额字段增加 add by renping 20190920 start
        refreshTotalPrinAmt();
        // 汇总券面总额字段增加 add by renping 20190920 end
    };

    /**
     * 移除按钮按下
     * @param array
     * @returns {boolean}
     */
    var removeProcess = function () {
        var avaRows = avaPledgeBondDetailTBL.getAllRows();
        var pledgeAllRows = pledgeBondDetailTbl.getAllCheckedRows();
        //质押债券列表出去移除的数据，重新加载表格  add by xueying 20200728 start
        var  allRowNo=pledgeBondDetailTbl.getAllRows();
        //质押债券列表出去移除的数据，重新加载表格  add by xueying 20200728 end
        if (pledgeAllRows != null && pledgeAllRows.length > 0) {
            // 倒序删除，正序会存在问题
            for (var i = pledgeAllRows.length - 1; i >= 0; i--) {
                var isAvaHave = false;
                // 删除可用质押券列表中相同记录
                for (var j in avaRows) {
                    if (avaRows[j].bondCode == pledgeAllRows[i].bondCode) {
                        isAvaHave = true;
                        break;
                    }
                }
                if(!pledgeAllRows[i]["pledgePrin"])
                {
                    if (pledgeAllRows[i]["pledgePrin"] != 0){
                        pledgeAllRows[i]["pledgePrin"] = null;
                    }
                }
                pledgeBondDetailTbl.removeRow(pledgeAllRows[i]);
                if (!isAvaHave) {
                    pledgeAllRows[i].pledgePrin = pledgeAllRows[i].pledgePrinInit;
                    avaPledgeBondDetailTBL.addRow(pledgeAllRows[i]);
                }
                //质押债券列表出去移除的数据，重新加载表格  add by xueying 20200728 start
                allRowNo = allRowNo.filter(function(item){
                    return item.bondCode != pledgeAllRows[i].bondCode
                });
                pledgeBondDetailTbl.reload({data:allRowNo});
                //pledgeBondDetailTbl.removeRow(pledgeAllRows[i]);
                //质押债券列表出去移除的数据，重新加载表格  add by xueying 20200728 start
            }
        }
        // 汇总券面总额字段增加 add by renping 20190920 start
        refreshTotalPrinAmt();
        // 汇总券面总额字段增加 add by renping 20190920 end
    };

    // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 start
    /**
     * 剩余期限修改联动
     */
    var endDays1Change = function () {
    	var startVal= getElementById("endDays1Start").val();
    	var endVal= getElementById("endDays1End").val();
    	if(startVal && endVal && Number(startVal) > Number(endVal)){
	        getElementById("endDays1End").val(startVal).trigger('blur');
	        getElementById("endDays1Start").val(endVal).trigger('blur');
    	}
    }
    // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 end
    /**
     * 保存按钮按下
     *
     * @param array
     * @returns {boolean}
     */
    var addPledgeBond = function () {
        var pledgeAllRows = pledgeBondDetailTbl.getAllRows();
        if (checkIsDifferent(pledgeAllRows)) {
            Joyin.error(JoyinUtil.getMessage("TRD_E_0007"));
            return false;
        }
        window.parent.BussRepoSec[entityName].addPledgeBond(pledgeAllRows);
        var index = window.parent.layer.getFrameIndex(window.name);
        window.parent.layer.close(index);
    };

    /**
     * 债券代码change事件
     */
    var bongCodeChange = function(){
        if(getElementById("bondCode").val() != null && getElementById("bondCode").val() != ''){
            var object = {};object.finprodId = getElementById("bondCode").val();
            Joyin.ajaxJson(basePath + 'invest/asset/bond/FinBondTargetAsset002/getBasicInfo',object, function(
                result) {
                getElementById("repRegistOrg").val(result.registOrg).attr('disabled',true).trigger('change');
            })
        }else{
        	getElementById("repRegistOrg").val('').attr('disabled',false).trigger('change');
        }
    }

    window.componentLoadCallback = function (isDone, pagePath, params) {
        if(isDone){
            //查询click事件被off
            getElementById("btn_search").click(function () {
                // 检索可质押债券列表
                avaPledgeBondDetailTBL.table ? avaPledgeBondDetailTBL.reload() : avaPledgeBondDetailTBL.load();
            });
        }
    }
    /**
     * 画面加载完成
     */
    $(function () {
        if(getElementById("ps").val() == BACK_LEND_DIRECTION_04){
            getElementById("portfolioId").parent().parent().hide();
            getElementById("endDays1Start").parent().parent().parent().hide();
        }else if(getElementById("ps").val() == BACK_LEND_DIRECTION_03){
            getElementById("portfolioId").parent().parent().show();
            getElementById("endDays1Start").parent().parent().parent().show();
        }

        //Modify by gaopeng 2019/10/12 start
    	//判断是否存在委托代码，存在即隐藏添加质押券按钮
    	if(getElementById("entrustId").val()!=null&&getElementById("entrustId").val()!=""&&pageModel==2){
    		getElementById("btn_joyin").hide();
    	}

    	// GZYHTZJY-510:银行间债券借贷交易,新建页面借贷方向为融出时,质押券信息，添加质押卷界面应没有【新建债券】按钮 fuyongsheng 20211122 edit start
        if(isBondLendTrade && getElementById("ps").val() == BACK_LEND_DIRECTION_06){
            getElementById("btn_add").hide();
        }
        // GZYHTZJY-510:银行间债券借贷交易,新建页面借贷方向为融出时,质押券信息，添加质押卷界面应没有【新建债券】按钮 fuyongsheng 20211122 edit end

    	//Modify by gaopeng 2019/10/12 end
        // 初始化时，直接加载质押券列表
        pledgeBondDetailTbl.load();
        // 添加检索事件
        getElementById("btn_search").click(function () {
            // 检索可质押债券列表
            avaPledgeBondDetailTBL.table ? avaPledgeBondDetailTBL.reload() : avaPledgeBondDetailTBL.load();
        });
        // 债券新建按钮
        getElementById("btn_add").click(showSecCreateDialog);
        // 加入按钮事件
        getElementById("btn_joyin").click(joyinProcess);
        // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 start
        // 剩余期限change
        getElementById("endDays1Start").change(endDays1Change);
        getElementById("endDays1End").change(endDays1Change);
        // 可质押列表增加“债项评级”、“到期日”、“登记托管机构”、“剩余期限”字段 add by renping 20191012 end
        // 移除按钮事件
        getElementById("btn_remove").click(removeProcess);
        // 保存按钮事件
        getElementById("save").click(addPledgeBond);
        //增加债券代码change事件
        getElementById("bondCode").change(function(){
            bongCodeChange();
        })
        if(isDzh == 'Y'){
            $('#portfolioId_div').hide();
        }
    	setTimeout(function () {
    	    if (window.CfetsPledgedBond) {
                window.CfetsPledgedBond.refreshCfetsPldegeBond((entity && entity.pledgeBondList) ? entity.pledgeBondList : []);
            }
        },1000);


    });
}());