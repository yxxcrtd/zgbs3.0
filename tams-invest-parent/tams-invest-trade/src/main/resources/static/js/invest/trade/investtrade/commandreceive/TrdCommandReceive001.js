/**
 * 组合信息查看页面
 */
var showPtlManagement = function (portfolioId) {
    Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"), basePath
        + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
        "95%", "90%", {
            pageModel: PAGE_MODEL_REFERENCE,
            portfolioId: portfolioId,
        });
};
//不同资产的查看页面
var showDetailByFinprodId = function (finprodId, finprodType2) {
    var label = '';
    var pagePath = '';
    debugger
    //债券标的信息查看
    if (finprodType2 == PINAN_ASSET_TYPE_F01
        || finprodType2 == PINAN_ASSET_TYPE_F10) {
        // label = 'FinBondTargetAsset002.title';
        // pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
        // Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
        //     "90%", {
        //         pageModel: PAGE_MODEL_REFERENCE,
        //         finprodId: finprodId
        //     });
        var url = "home.html?a=a&finprodMarketId=" + finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a"; // 默认债券标的
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
            finprodId: finprodId
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "98%", "98%",
            param);
        //基金类标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F02) {
        label = 'FinFundTarget002.detail';
        pagePath = "invest/asset/fund/FinFundTarget002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//计划类利率型标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F03) {
        label = 'FinPlanProfitAsset002.detail';
        pagePath = "invest/asset/plan/planProfit/FinPlanProfitAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//计划类净值型标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F04) {
        label = 'FinPlanNetAsset002.detail';
        pagePath = "invest/asset/plan/planNet/FinPlanNetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//存款标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F05) {
        label = 'FinDepositTarget002.title';
        pagePath = "invest/asset/depo/FinDepositTarget002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//项目类资产标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F06) {
        label = 'FinProjectTargetAsset002.title';
        pagePath = "invest/asset/project/FinProjectTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//回购标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F07) {
        label = '';
        pagePath = "";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//拆借标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F08) {
        label = '';
        pagePath = "";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//计划类货币
    } else if (finprodType2 == PINAN_ASSET_TYPE_F09) {
        label = 'SysCommon.refrence';
        pagePath = "invest/asset/plan/plancur/FinPlanAssetC002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    } else if (finprodType2 == PINAN_ASSET_TYPE_F11) {
        label = 'FinStockInfo002.refrence';
        pagePath = "invest/asset/stock/FinStockInfo002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    } else if (finprodType2 == PINAN_ASSET_TYPE_F12) {
        label = 'SysCommon.refrence';
        pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//债券借贷委托查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F16) {
        label = 'SysCommon.refrence';
        pagePath = "invest/entrust/etrirs/EtrIRS002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//一级投标债信息
    } else if (finprodType2 == PINAN_ASSET_TYPE_F17) {
        label = 'FinTenderBondTargetAsset002.title';
        pagePath = "invest/asset/tenderbond/FinTenderBondTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    } else if (finprodType2 == PINAN_ASSET_TYPE_F18) {
        label = 'TrdOptionTrade002.reference';
        pagePath = "invest/trade/option/TrdOptionTrade002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    }
    // add by etliu 20210421 权益类资产相关代码合并 start
    else if (finprodType2 == PINAN_ASSET_TYPE_F19) {
        label = 'FinProjectNetTargetAsset002.title';
        pagePath = "invest/asset/projectnet/FinProjectNetTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath, "95%",
            "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//权益类资产标的信息查看
    }
    // add by etliu 20210421 权益类资产相关代码合并 end
};

/**
 * 投资委托查询
 */
/*
 * var showEtrInvest = function (entrustId){
 * TrdCommandReceive001.entrustId=entrustId;
 * $('#detailTable_Div').attr("hidden",false);
 * TrdCommandReceive001.detailTable.table ?
 * TrdCommandReceive001.detailTable.reload() :
 * TrdCommandReceive001.detailTable.load(); }
 */
var TrdCommandReceive001 = function () {
    //委托指令
    var entrustId;
    /**
     * 获取查询参数
     */
    var getPageParams = function () {
        var obj = $.serializeToObject('#TrdCommandReceive001_searchForm');
        obj.beginDate = $('#TrdCommandReceive001_createTime').startDate();
        obj.endDate = $('#TrdCommandReceive001_createTime').endDate();
        if (!$("#TrdCommandReceive001_finprodType2").val() == '') {
            obj.finprodType2 = $("#TrdCommandReceive001_finprodType2").val()
                .join(",");
        }
        //利率互换交易方向特殊处理  后台转换
        if ($("#TrdCommandReceive001_ps").val() && $('#TrdCommandReceive001_ps').select2('data')[0].col ==IRS_TYPE) {
        	obj.ps = IRS_TYPE+obj.ps;
        }
        return obj;
    };

    /*
     * var getPageParams = function() { var
     * obj=$.serializeToObject('#TrdCommandReceive001_searchForm');
     * obj.beginDate=$('#TrdCommandReceive001_createTime').startDate();
     * obj.endDate=$('#TrdCommandReceive001_createTime').endDate();
     * if(!$("#TrdCommandReceive001_finprodType2").val() == ''){
     * obj.finprodType2=$("#TrdCommandReceive001_finprodType2").val().join(","); }
     * return obj; };
     */

    /**
     * 定义已接收的数据表格
     */
    var table = $("#dataTable")
        .joyintable(
            {
                ajax: {
                    url: 'findPage',
                    type: 'POST',
                    data: function () {
                        return getPageParams();
                    }
                },
                dblclick: function (table, data) {
                    var entrustId = data.entrustId;
                    //债券委托详情
                    if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps != PS_T04 && data.ps != PS_T49) {
                        label = 'EtrInvestBondInfo001.detail';
                        pagePath = "invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//基金委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F02) {
                        label = 'EtrFund002.detail';
                        pagePath = "invest/entrust/etrfund/EtrFund002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//计划类利率委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F03) {
                        label = 'EtrPlanAssetR002.detail';
                        pagePath = "invest/entrust/etrplanassetr/EtrPlanAssetR002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//计划类净值委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F04) {
                        label = 'EtrPlanAssetN002.detail';
                        pagePath = "invest/entrust/etrplanassetn/EtrPlanAssetN002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//存款委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F05) {
                        label = 'EtrBrowDepoInfo002.detail';
                        pagePath = "invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//项目类委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F06) {
                        label = 'EtrProject002.detail';
                        pagePath = "invest/entrust/etrproject/EtrProject002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//回购委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F07) {
                        label = 'EtrRepo002.detail';
                        pagePath = "invest/entrust/etrrepo/EtrRepo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//拆借查看页面
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F08) {
                        label = 'EtrLendSpecInfo002.dblclick';
                        pagePath = "invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F09) {
                        label = 'EtrPlanAssetC002.refence';
                        pagePath = "invest/entrust/etrplanassetc/EtrPlanAssetC002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//计划类货币委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F10) {
                        label = 'EtrBoutRepo002.refrence';
                        pagePath = "invest/entrust/etrboutrepo/EtrBoutRepo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });// 买断式回购委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F11) {
                        label = 'EtrStock002.refence';
                        pagePath = "invest/entrust/etrstock/EtrStock002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });// 股票交易委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F13) {
                        label = 'EtrWtSpotex.dblclick';
                        pagePath = "invest/entrust/etrwtspotex/EtrWtSpotex002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//外汇即期委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F14) {
                        label = 'EtrForwex002.refrence';
                        pagePath = "invest/entrust/etrforwex/EtrForwex002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//外汇远期委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F15) {
                        label = 'EtrWtFxswap002.refrence';
                        pagePath = "invest/entrust/etrwtfxswap/EtrWtFxswap002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//外汇掉期委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F12) {
                        label = 'EtrSecLend.refrence';
                        pagePath = "invest/entrust/etrseclend/EtrSecLend002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                                targetFinprodId: data.finprodId,
                                portfolioId: data.portfolioId
                            });//债券借贷委托查看
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F16) {
                        label = 'EtrIRS.dblclick';
                        pagePath = "invest/entrust/etrirs/EtrIRS002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                                targetFinprodId: data.finprodId,
                                portfolioId: data.portfolioId
                            });//add by gejiaao start  一级投标债查看页面
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F17) {
                        label = "EtrBondFirstLBidding002.dblclick";
                        pagePath = "invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            })
                    } //add by gaopeng 20191123 start  
                    else if(data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps == PS_T49){
                        label='EtrBondToStock002.dblclick';
                        pagePath = "invest/entrust/etrbondtostock/EtrBondToStock002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                            "95%", "90%",{
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:entrustId
                            });
                    }//add by gaopeng 20191123 end
                    //可转债查看页面
                    else if(data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps == PS_T04){
                        label='EtrConBond002.dblclick';
                        pagePath = "invest/entrust/etrconbond/EtrConBond002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                            "95%", "90%",{
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:entrustId
                            });
                    }else  if (data.finprodType2 == PINAN_ASSET_TYPE_F36 ){//add by gejiaao end
                        Joyin.dialog(JoyinUtil.getLabel("EtrInstruction002.dblclick"),
                            basePath
                            + "invest/entrust/etrinstruction/EtrInstruction002/init",
                            "95%", "80%", {
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:data.entrustId
                            },
                            function(){table.reload();});
                    }else  if (data.finprodType2 == PINAN_ASSET_TYPE_F18 ){
                        Joyin.dialog(JoyinUtil.getLabel("TrdOptionTrade002.reference"),
                            basePath
                            + "invest/trade/option/TrdOptionTrade002/init",
                            "95%", "80%", {
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:data.entrustId
                            },
                            function(){table.reload();});
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    else if (data.finprodType2 == PINAN_ASSET_TYPE_F19) {
                        label = 'EtrProjectNet002.detail';
                        pagePath = "invest/entrust/etrprojectnet/EtrProjectNet002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//权益类指令详情
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 end

                },
//        height:'300px',
                cols: [
                    {
                        field: 'otherEntrustStatus',
                        title: 'TrdCommandReceive001.entrustStatus',
                        width: 100,
                        sortField: 'entrustStatus'
                    },
                    {
                        field: 'createTimeValStr',
                        title: 'TrdCommandReceive001.createTime',
                        width: 150,
                        type: 'date',
                        sortField: 'createTimeVal'
                    },
                    {
                        field: 'portfolioId',
                        title: 'TrdCommandReceive001.portfolioId',
                        render: function (data, type, row, meta) {
                            var portfolioId = row.portfolioId;
                            if (portfolioId) {
                                portfolioId = '<a onclick="showPtlManagement(\''
                                    + row.portfolioId
                                    + '\')" class="pro-link"  style="cursor: pointer;">'
                                    + row.portfolioId + '</a>';
                            }else {
                                portfolioId = null;
                            }
                            return portfolioId;
                        },
                        width: 130
                    },
                    {
                        field: 'vdateStr',
                        title: 'TrdCommandReceive001.vdate',
                        width: 150,
                        sortField: 'vdate'
                    },
                    {
                        field: 'mdateStr',
                        title: 'TrdCommandReceive001.mdate',
                        width: 150,
                        sortField: 'mdate'
                    },
                    {
                        field: 'portfolioName',
                        title: 'TrdCommandReceive001.portfolioName',
                        width: 150
                    },
                    {
                        field: 'otherFinprodType2',
                        title: 'TrdCommandReceive001.finprodType2',
                        width: 100,
                        sortField: 'finprodType2'
                    },
                    {
                        field: 'finprodMarketId',
                        title: 'TrdCommandReceive001.finprodMarketId',
                        render: function (data, type, row, meta) {
                            if (row.finprodMarketId != undefined) {
                                '"+A+"', '"+B+"';
                                // return '<a
                                // onclick="showDetailByFinprodId(\''
                                // + row.finprodId
                                // + '\')" class="pro-link"
                                // style="cursor:
                                // pointer;">'+row.finprodMarketId+'</a>';
                                return "<a onclick=\"showDetailByFinprodId('"
                                    + row.finprodId
                                    + "','"
                                    + row.finprodType2
                                    + "','"
                                    + row.portfolioId
                                    + "','"
                                    + row.entrustId
                                    + "','"
                                    + row.tradeId
                                    + "')\" + style='cursor: pointer;'>"
                                    + row.finprodMarketId
                                    + "</a>";
                            }
                        },
                        width: 150
                    },
                    {
                        field: 'finprodAbbr',
                        title: 'TrdCommandReceive001.finprodAbbr',
                        width: 160
                    },
                    {
                        field: 'otherPs',
                        title: 'TrdCommandReceive001.ps',
                        width: 100,
                        sortField: 'ps'
                    },
                    {
                        field: 'otherTradeMarket',
                        title: 'TrdCommandReceive001.tradeMarket',
                        width: 130,
                        sortField: 'tradeMarket'
                    },
                    {
                        field: 'entrustAmt',
                        title: 'TrdCommandReceive001.entrustAmt',
                        type: 'number',
                        width: 140
                    },
                    //add by  gejiaao satrt 列表项中委托额度后增加合同签署结果、合同号
                    /*{
                        field: 'contractResult',
                        title: 'TrdCommandReceive001.contractResult',
                        width: 130,
                        sortField: 'contractResult'
                    },*/
                    {
                        field: 'contractNo',
                        title: 'TrdCommandReceive001.contractNo',
                        width: 130,
                        sortField: 'contractNo'
                    },
                    //add by  gejiaao end

                    //add by  gejiaao satrt 列表项中委托额度后增加字段投标标位
                    {field: 'bidPrice', title: 'TrdCommandDispatcher001.bidPrice', width: 140,sortable:false},
                    {field: 'bidId', width: 140, title: 'PrimaryBiddingManage001.bidid', visible: false},
                    //add by  gejiaao end
                    {
                        field: 'proAvailableCash',
                        title: 'TrdCommandReceive001.proAvailableCash',
                        type: 'number',
                        width: 100
                    },
                    {
                        field: 'finshAmt',
                        title: 'TrdCommandReceive001.finishAmt',
                        type: 'number',
                        width: 140
                    },
                    {
                        field: 'dealPercent',
                        title: 'TrdCommandReceive001.dealPercent',
                        width: 100,
                        render: function (data, type, row, meta) {
                            return "<div class='layui-progress layui-progress-big' style='margin-top:5px;'><div class='layui-progress-bar layui-bg-green' style='width:"
                                + (row.dealPercent)
                                + "%'><span class='layui-progress-text'>"
                                + (row.dealPercent)
                                + "%</span></div></div>";
                        },
                        width: 100,
                        sortField: 'percent'
                    }, {
                        field: 'otherCcy',
                        title: 'TrdCommandReceive001.ccy',
                        width: 120,
                        sortField: 'ccy'
                    }, {
                        field: 'trader',
                        title: 'TrdCommandReceive001.trader',
                        width: 100
                    }, {
                        field: 'remark',
                        title: 'TrdCommandReceive001.remark',
                        width: 150
                    }, {
                        field: 'entrustId',
                        visible: false
                    // add by zhangrui1 增加交易对手字段 start
                    }, {
                        field: 'counterId',
                        visible: false
                    // add by zhangrui1 增加交易对手字段 end
                    }, {
                        field: 'finprodId',
                        visible: false
                    }, {
                        field: 'days',
                        visible: false
                    }, {
                        field: 'entrustStatus',
                        visible: false
                    }, {
                        field: 'finprodType2',
                        visible: false
                    }, {
                        field: 'tradeId',
                        visible: false
                    }, {
                        field: 'ps',
                        visible: false
                    },{
                        field:'chlId',
                        visible: false
                    },{
                        field:'secAcctId',
                        visible: false
                    },{
                        field:'termType',
                        visible: false
                    },{
                        field:'assetType1',
                        visible: false
                    },
                    {
                        field:'settleSpeed',
                        visible: false
                    }],
                done: function (tables, allData) {
                    $.each(tables, function (index, obj) {
                        //[相隔天数]在3天之内且[交易完成度]<100%，则列表行用红色标识
                        if (tables[index].days <= 3
                            && tables[index].percent < 1) {
                            // table.$element.next().find(".layui-table-body
                            // table.layui-table
                            // tr[data-index='"+index+"']").css({'background-color':'#FF5722','color':'white'})
                        }

                    });

                },
                click: function (table, data) {
                    entrustId = data.entrustId;
                    $('#detailTable_Div').attr("hidden", false);
                    detailTable ? detailTable.reload() : detailTable
                        .load();
                    // detailTable.table ? detailTable.reload() :
                    // detailTable.load();
//        	showEtrInvest(data.entrustId);
                    // table.$element.next().find(".layui-table-content
                    // ").css({height:'200px'})
                    detailTable.$element.next().find(
                        ".layui-tab-content").css({
                        height: '200px'
                    });
                }
            });

    /**
     * 定义未接收的数据表格
     */
    var otherTable = $("#otherDataTable")
        .joyintable(
            {
                ajax: {
                    url: 'otherFindPage',
                    type: 'POST',
                    data: function () {
                        return getPageParams();
                    }
                },
                dblclick: function (table, data) {
                    var entrustId = data.entrustId;
                    //债券委托详情
                    if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps != PS_T04 && data.ps != PS_T49) {
                        label = 'EtrInvestBondInfo001.detail';
                        pagePath = "invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//基金委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F02) {
                        label = 'EtrFund002.detail';
                        pagePath = "invest/entrust/etrfund/EtrFund002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//计划类利率委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F03) {
                        label = 'EtrPlanAssetR002.detail';
                        pagePath = "invest/entrust/etrplanassetr/EtrPlanAssetR002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//计划类净值委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F04) {
                        label = 'EtrPlanAssetN002.detail';
                        pagePath = "invest/entrust/etrplanassetn/EtrPlanAssetN002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//存款委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F05) {
                        label = 'EtrBrowDepoInfo002.detail';
                        pagePath = "invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//项目类委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F06) {
                        label = 'EtrProject002.detail';
                        pagePath = "invest/entrust/etrproject/EtrProject002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//回购委托详情
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F07) {
                        label = 'EtrRepo002.detail';
                        pagePath = "invest/entrust/etrrepo/EtrRepo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId,
                            });//拆借查看页面
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F08) {
                        label = 'EtrLendSpecInfo002.dblclick';
                        pagePath = "invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F09) {
                        label = 'EtrPlanAssetC002.refence';
                        pagePath = "invest/entrust/etrplanassetc/EtrPlanAssetC002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F10) {
                        label = 'EtrBoutRepo002.refrence';
                        pagePath = "invest/entrust/etrboutrepo/EtrBoutRepo002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });// 买断式回购委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F11) {
                        label = 'EtrStock002.refrence';
                        pagePath = "invest/entrust/etrstock/EtrStock002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });// 股票交易委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F13) {
                        label = 'EtrWtSpotex.dblclick';
                        pagePath = "invest/entrust/etrwtspotex/EtrWtSpotex002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//外汇即期委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F14) {
                        label = 'EtrForwex002.refrence';
                        pagePath = "invest/entrust/etrforwex/EtrForwex002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//外汇远期委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F15) {
                        label = 'EtrWtFxswap002.refrence';
                        pagePath = "invest/entrust/etrwtfxswap/EtrWtFxswap002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//外汇掉期委托
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F16) {
                        label = 'EtrIRS.dblclick';
                        pagePath = "invest/entrust/etrirs/EtrIRS002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//add by yedengqun start  一级投标债查看页面
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F17) {
                        label = "EtrBondFirstLBidding002.dblclick";
                        pagePath = "invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//ADD BY GAOPENG 20191125 start
                    }else if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps == PS_T49) {
                        label = "EtrBondToStock002.dblclick";
                        pagePath = "invest/entrust/etrbondtostock/EtrBondToStock002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//ADD BY GAOPENG 20191125 end
                    } //可转债查看页面
                    else if(data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps == PS_T04){
                        label='EtrConBond002.dblclick';
                        pagePath = "invest/entrust/etrconbond/EtrConBond002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                            "95%", "90%",{
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:entrustId
                            });
                    }else  if (data.finprodType2 == PINAN_ASSET_TYPE_F36 ){//add by gejiaao end
                        Joyin.dialog(JoyinUtil.getLabel("EtrInstruction002.dblclick"),
                            basePath
                            + "invest/entrust/etrinstruction/EtrInstruction002/init",
                            "95%", "80%", {
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:data.entrustId
                            },
                            function(){table.reload();});
                    }else  if (data.finprodType2 == PINAN_ASSET_TYPE_F18 ){
                        Joyin.dialog(JoyinUtil.getLabel("TrdOptionTrade002.reference"),
                            basePath
                            + "invest/trade/option/TrdOptionTrade002/init",
                            "95%", "80%", {
                                pageModel:PAGE_MODEL_REFERENCE,
                                entrustId:data.entrustId
                            },
                            function(){table.reload();});
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    else if (data.finprodType2 == PINAN_ASSET_TYPE_F19){
                        label = 'EtrProjectNet002.detail';
                        pagePath = "invest/entrust/etrprojectnet/EtrProjectNet002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });//权益类指令详情
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 end

                },
                searchOnInit: true,
                cols: [
                    {
                        field: 'createTimeValStr',
                        title: 'TrdCommandReceive001.createTime',
                        width: 150,
                        type: 'date',
                        sortField: 'createTimeVal'
                    },
                    {
                        field: 'portfolioId',
                        title: 'TrdCommandReceive001.portfolioId',
                        render: function (data, type, row, meta) {
                            var portfolioId = row.portfolioId;
                            if (portfolioId){
                                portfolioId = '<a onclick="showPtlManagement(\''
                                + row.portfolioId
                                + '\')" class="pro-link"  style="cursor: pointer;">'
                                + row.portfolioId + '</a>'
                            } else {
                                portfolioId = null;
                            }
                            return portfolioId;
                        },
                        width: 130
                    },
                    {
                        field: 'portfolioName',
                        title: 'TrdCommandReceive001.portfolioName',
                        width: 150
                    },
                    {
                        field: 'otherFinprodType2',
                        title: 'TrdCommandReceive001.finprodType2',
                        width: 100,
                        sortField: 'finprodType2'
                    },
                    {
                        field: 'finprodMarketId',
                        title: 'TrdCommandReceive001.finprodMarketId',
                        render: function (data, type, row, meta) {
                            if (row.finprodMarketId != undefined) {
                                return "<a onclick=\"showDetailByFinprodId('"
                                    + row.finprodId
                                    + "','"
                                    + row.finprodType2
                                    + "')\" + style='cursor: pointer;'>"
                                    + row.finprodMarketId
                                    + "</a>";
                            }
                        },
                        width: 180
                    },
                    {
                        field: 'finprodAbbr',
                        title: 'TrdCommandReceive001.finprodAbbr',
                        width: 160
                    },
                    {
                        field: 'otherPs',
                        title: 'TrdCommandReceive001.ps',
                        width: 100,
                        sortField: 'ps'
                    },
                    {
                        field: 'otherTradeMarket',
                        title: 'TrdCommandReceive001.tradeMarket',
                        width: 130,
                        sortField: 'tradeMarket'
                    },
                    {
                        field: 'entrustAmt',
                        title: 'TrdCommandReceive001.entrustAmt',
                        type: 'number',
                        width: 140
                    },
                    //add by  gejiaao satrt 列表项中委托额度后增加字段投标标位
                    {field: 'bidPrice', title: 'TrdCommandDispatcher001.bidPrice', width: 140,sortable:false},
                    {field: 'bidId', width: 140, title: 'PrimaryBiddingManage001.bidid', visible: false},
                    //add by  gejiaao end
                    {
                        field: 'proAvailableCash',
                        title: 'TrdCommandReceive001.proAvailableCash',
                        type: 'number',
                        width: 100
                    },
                    {
                        field: 'finshAmt',
                        title: 'TrdCommandReceive001.finishAmt',
                        type: 'number',
                        width: 140
                    },
                    {
                        field: 'dealPercent',
                        title: 'TrdCommandReceive001.dealPercent',
                        render: function (data, type, row, meta) {
                            return "<div class='layui-progress layui-progress-big' style='margin-top:5px;'><div class='layui-progress-bar layui-bg-green' style='width:"
                                + (row.dealPercent)
                                + "%'><span class='layui-progress-text'>"
                                + (row.dealPercent)
                                + "%</span></div></div>";
                        },
                        width: 100,
                        sortField: 'percent'
                    }, {
                        field: 'otherCcy',
                        title: 'TrdCommandReceive001.ccy',
                        width: 120,
                        sortField: 'ccy'
                    }, {
                        field: 'trader',
                        title: 'TrdCommandReceive001.trader',
                        width: 100
                    }, {
                        field: 'remark',
                        title: 'TrdCommandReceive001.remark',
                        width: 150
                    }, {
                        field: 'entrustId',
                        visible: false
                    }, {
                        field: 'finprodId',
                        visible: false
                    }, {
                        field: 'days',
                        visible: false
                    }, {
                        field: 'entrustStatus',
                        visible: false
                    }, {
                        field: 'tradeId',
                        visible: false
                    }],
                done: function (tables, allData) {
                    $.each(tables, function (index, obj) {
                        //[相隔天数]在3天之内且[交易完成度]<100%，则列表行用红色标识
                        if (tables[index].days <= 3
                            && tables[index].percent < 1) {
                            // table.$element.next().find(".layui-table-body
                            // table.layui-table
                            // tr[data-index='"+index+"']").css({'background-color':'#FF5722','color':'white'})
                        }
                    });
                }
            });
    //明细表格
    var detailTable = $("#detailTable")
        .joyintable(
            {
                ajax: {
                    url: 'selectEtrInvest',
                    type: 'POST',
                    data: function () {
                        return {
                            entrustId: entrustId,
                            pageId: "TrdCommandReceive001"
                        };
                    }
                },
                height: '200px',
                checkbox: false,
                loadonce: true,
                rowno: true,
//        page:false,
                dblclick: function (table, data) {
                    //存款投资交易查看页面
                    if (data.finprodType2 == PINAN_ASSET_TYPE_F05) {
                        label = 'TrdDepositInvestTrade001.reference';
                        pagePath = "invest/trade/trddepositinvesttrade/TrdDepositInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });//拆借投资交易查看页面
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F08) {
                        label = 'TrdLendInvestTrade002.reference';
                        pagePath = "invest/trade/lend/TrdLendInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F01) {//债券
                        label = 'TrdBondInvestTrade002';
                        pagePath = "invest/trade/bond/TrdBondInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F02) {//基金
                        label = 'TrdFundInvestTrade002';
                        pagePath = "invest/trade/fund/TrdFundInvestTrade002/init";
                        pagePath1 = "invest/trade/fund/TrdFundInvestTrade003/init";
                        if (data.ps != 'T19' && data.ps != 'T05') {
                            Joyin
                                .dialog(
                                    JoyinUtil.getLabel(label),
                                    basePath + pagePath,
                                    "95%",
                                    "90%",
                                    {
                                        pageModel: PAGE_MODEL_REFERENCE,
                                        tradeId: data.tradeId
                                    });
                        } else {
                            Joyin
                                .dialog(
                                    JoyinUtil.getLabel(label),
                                    basePath + pagePath1,
                                    "95%",
                                    "90%",
                                    {
                                        pageModel: PAGE_MODEL_REFERENCE,
                                        tradeId: data.tradeId,
                                        ps: data.ps
                                    });
                        }
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F03) {//计划类利率
                        label = 'TrdPlanRateInvestTrade002';
                        pagePath = "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F04) {//计划类净值
                        label = 'TrdPlanNetInvestTrade002';
                        pagePath = "invest/trade/plannet/TrdPlanNetInvestTrade002/init";
                        pagePath1 = "invest/trade/plannet/TrdPlanNetInvestTrade003/init";
                        if (data.ps != 'T19' && data.ps != 'T05') {
                            Joyin
                                .dialog(
                                    JoyinUtil.getLabel(label),
                                    basePath + pagePath,
                                    "95%",
                                    "90%",
                                    {
                                        pageModel: PAGE_MODEL_REFERENCE,
                                        tradeId: data.tradeId
                                    });
                        } else {
                            Joyin
                                .dialog(
                                    JoyinUtil.getLabel(label),
                                    basePath + pagePath1,
                                    "95%",
                                    "90%",
                                    {
                                        pageModel: PAGE_MODEL_REFERENCE,
                                        tradeId: data.tradeId,
                                        ps: data.ps
                                    });
                        }
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F06) {//项目类资产
                        label = 'TrdProjectInvestTrade002';
                        pagePath = "invest/trade/project/TrdProjectInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                bizCode: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F07) {//回购
                        label = 'TrdRepoTrade002';
                        pagePath = "invest/trade/trdrepotrade/TrdRepoTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F09) {//计划类货币型
                        label = 'TrdPlanCcyInvestTrade002.reference';
                        pagePath = "invest/trade/planccy/TrdPlanCcyInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F10) {//买断式回购交易
                        label = 'TrdOutRightRepoTrade002.reference';
                        pagePath = "invest/trade/buyoutrepo/TrdOutRightRepoTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F11) {// 股票投资交易
                        label = 'TrdStock002';
                        pagePath = "invest/trade/stockdeal/TrdStock002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                tradeId: data.tradeId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F13) {//外汇即期交易
                        label = 'TrdSpotexInvestTrade002.refrence';
                        pagePath = "invest/trade/spotex/TrdSpotexInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F14) {//外汇远期委托
                        label = 'TrdForwexInvestTrade002.refrence';
                        pagePath = "invest/trade/forwex/TrdForwexInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F15) {//外汇掉期交易
                        label = 'TrdFxswapInvestTrade002.reference';
                        pagePath = "invest/trade/trdfxswapinvesttrade/TrdFxswapInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    } else if (data.finprodType2 == PINAN_ASSET_TYPE_F16) {//利率互换交易
                        label = 'TrdIRSTrade002.refrence';
                        pagePath = "invest/trade/irs/TrdIRSTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                            "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                entrustId: entrustId
                            });
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    else if (data.finprodType2 == PINAN_ASSET_TYPE_F19) {//项目类资产
                        label = 'TrdProjectNetInvestTrade002';
                        pagePath = "invest/trade/projectnet/TrdProjectNetInvestTrade002/init";
                        Joyin.dialog(JoyinUtil.getLabel(label),
                            basePath + pagePath, "95%", "90%", {
                                pageModel: PAGE_MODEL_REFERENCE,
                                bizCode: data.tradeId
                            });
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 end
                },
                cols: [{
                    field: 'tradeId',
                    width: 170,
                    title: 'TrdCommandReceive001.dealNumber'
                }, {
                    field: 'tradeDate',
                    width: 120,
                    title: 'TrdCommandReceive001.dealDate',
                    type: 'date'
                }, {
                    field: 'createTimeVal',
                    width: 170,
                    title: 'TrdCommandReceive001.dealTime',
                    type: 'datetime'
                }, {
                    field: 'trader',
                    width: 120,
                    title: 'TrdCommandReceive001.dealTrader'
                }, {
                    field: 'tradeAmt',
                    width: 120,
                    title: 'TrdCommandReceive001.dealAmt',
                    type: 'number'
                }, {
                    field: 'remark',
                    title: 'TrdCommandReceive001.remark'
                }, {
                    field: 'finprodType2',
                    visible: false
                }, {
                    field: 'ps',
                    visible: false
                }, {
                    field: 'tradeStatus',
                    visible: false
                }]
            });
    /**
     * 导出事件
     */
    var exportExcel = function (index) {
        var paramObj = getPageParams();
        paramObj.tab = index;
        Joyin.exportExcel('export', paramObj);
    };

    /*
     * $('#await').click(function(){ otherTable.reload();
     * Joyin.ajax(JoyinUtil.getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/otherSelectList'),
     * getPageParams(), function(result) { $('#number').html(result); }) })
     */

    /**
     * 画面加载完成
     */
    $(function () {
        $('#number').html(NUMBER);
        $('#numberreceive').html(NUMBERRECEIVE);
        //初始隐藏接收按钮
        $('#TrdCommandReceive001_btn_receive').attr("style", "display:none");
        $('#detailTable_Div').attr("hidden", false);
        detailTable.load();
        detailTable.$element.next().find(".layui-tab-content").css({
            height: '200px'
        });
        $('#detailTable_Div').attr("hidden", true);
        //定义选项卡是哪一个
        var element;
        var index = '0';//tab卡初始状态
        table.load();//默认初始刷新
        layui
            .use(
                'element',
                function () {
                    element = layui.element;
                    element
                        .on(
                            'tab(demo)',
                            function (data) {
                                //改变指令状态搜索框状态
                                index = data.index;
                                if (index == '1') {
                                    $('#detailTable_Div').attr(
                                        "hidden", true);// 明细table隐藏
                                    $(
                                        '#TrdCommandReceive001_btn_transaction')
                                        .attr("style",
                                            "display:none");// 交易录单按钮
                                    $('#TrdCommandReceive001_btn_preTrade').hide();// 交易预录入按钮
                                    $('#TrdCommandReceive001_btn_contract').hide();
                                    $(
                                        '#TrdCommandReceive001_btn_refuce')
                                        .attr("style",
                                            "display:none");// 拒绝按钮
                                    $(
                                        '#TrdCommandReceive001_btn_receive')
                                        .attr("style",
                                            "display:false");// 接收按钮
                                    //$('#TrdCommandReceive001_entrustStatus').val('').trigger('change');//完成状态
                                    //$('#TrdCommandReceive001_entrustStatus').attr("disabled",true);
                                    $('#entrustStatus').hide();
                                    $('#secondaryMarketTradeDiv').hide();
                                    otherTable.reload();
                                    Joyin
                                        .ajax(
                                            JoyinUtil
                                                .getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/otherSelectList'),
                                            getPageParams(),
                                            function (
                                                result) {
                                                $(
                                                    '#number')
                                                    .html(
                                                        result);
                                            })
                                } else {
                                    if (detailTable.table) {
                                        //明细table存在就隐藏
                                        $('#detailTable_Div')
                                            .attr("hidden",
                                                false);
                                    }
                                    $('#entrustStatus').show();
                                    $('#secondaryMarketTradeDiv').show();
                                    $(
                                        '#TrdCommandReceive001_btn_transaction')
                                        .attr("style",
                                            "display:false");
                                    //根据SYS_ELEMENT_CONTROL里的is_show参数来判断是否切换tab时展示 edit by yeming 20210918 start
                                    if(!ElementsControl.includes("TrdCommandReceive001_btn_preTrade")){
                                        $('#TrdCommandReceive001_btn_preTrade').show();// 交易预录入按钮
                                    }
                                    if(!ElementsControl.includes("TrdCommandReceive001_btn_contract")){
                                        $('#TrdCommandReceive001_btn_contract').show();
                                    }
                                    //根据SYS_ELEMENT_CONTROL里的is_show参数来判断是否切换tab时展示 edit by yeming 20210918 end
                                    $(
                                        '#TrdCommandReceive001_btn_refuce')
                                        .attr("style",
                                            "display:false");
                                    $(
                                        '#TrdCommandReceive001_btn_receive')
                                        .attr("style",
                                            "display:none");
                                }
                            });
                });
        //搜索
        $('#TrdCommandReceive001_btn_search')
            .click(
                function () {
                    if (index == '1') {//未接收加载
                        otherTable.table ? otherTable.reload()
                            : otherTable.load();
//                        Joyin
//                            .ajax(
//                                JoyinUtil
//                                    .getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/otherSelectList'),
//                                getPageParams(), function (
//                                    result) {
//                                    $('#number').html(result);
//                                })
                    } else {//已接收加载
                        table.table ? table.reload() : table.load();
                    }
                    
                    /**add by chendijun 点击查询时已接收和未接收显示正确的条数 **/
            		Joyin.ajax(JoyinUtil.getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/otherSelectList'), getPageParams(),
            			function(result) {
            			$('#number').html(result);
            		});
            		
            		Joyin.ajax(JoyinUtil.getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/SelectList'), getPageParams(),
            				function(result) {
            				$('#numberreceive').html(result);
            	   });
            	   /**add by chendijun 点击查询时已接收和未接收显示正确的条数 **/
                });
        //指令接收
        $("#TrdCommandReceive001_btn_receive")
            .click(
                function () {
                    //对table选中行做判断
                    if (otherTable != null && otherTable != '') {
                        var tableData = otherTable.getAllCheckedRows();
                    }
                    if (!tableData || tableData.length == 0) {
                        Joyin.error(JoyinUtil
                            .getMessage("BUSS_COM_E_0026"));
                        return;
                    }
                    //拼接所有选中的委托代码
                    var entrustIds = "";
                    for (var i = 0; i < tableData.length; i++) {
                        entrustIds += tableData[i].entrustId + ",";
                    }
                    //去掉拼接最后一位逗号
                    entrustIds = (entrustIds
                        .substring(entrustIds.length - 1) == ',') ? entrustIds
                            .substring(0, entrustIds.length - 1)
                        : entrustIds;
                    //调用接收委托业务组件
                    Joyin
                        .ajax(
                            JoyinUtil
                                .getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/receiveEntrust'),
                            {
                                entrustIds: entrustIds
                            },
                            function (result) {
                                //成功后刷新table
                                otherTable.table ? otherTable
                                    .reload() : otherTable
                                    .load();
                                table.table ? table.reload()
                                    : table.load();
                                Joyin
                                    .ajax(
                                        JoyinUtil
                                            .getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/otherSelectList'),
                                        getPageParams(),
                                        function (result) {
                                            $('#number')
                                                .html(
                                                    result);
                                        })
                            });

                });

        //指令拒收
        $('#TrdCommandReceive001_btn_refuce')
            .click(
                function () {
                    //对table选中行做判断
                    if (table != null && table != '') {
                        var tableData = table.getAllCheckedRows();
                    }
                    if (!tableData || tableData.length == 0) {
                        Joyin.error(JoyinUtil
                            .getMessage("BUSS_COM_E_0026"));
                        return;
                    }
                    //对选中的table做确认校验
                    for (var i = 0; i < tableData.length; i++) {
                        if (tableData[i].bidId){
                            Joyin.error(JoyinUtil
                                .getMessage("TRD_J_0001"));
                            return;
                        }
                    }

                    //校验是否已经做了交易录单，流程中是否存在数据
                    var object = tableData[0];
                    object.operateType = 'refuce';
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check', object, function (result) {
                        Joyin.prompt('退回原因', 0, function (result) {
                            if(!result){
                                Joyin.error('请填写退回原因！');
                                return;
                            }
                            var entrustStatus = "";
                            for (var i = 0; i < tableData.length; i++) {
                                entrustStatus += tableData[i].entrustStatus
                                    + ",";
                            }
                            //拼接所有选中的委托代码
                            var entrustIds = "";
                            for (var i = 0; i < tableData.length; i++) {
                                entrustIds += tableData[i].entrustId + ",";
                            }
                            //去掉拼接最后一位逗号
                            entrustIds = (entrustIds
                                .substring(entrustIds.length - 1) == ',') ? entrustIds
                                    .substring(0, entrustIds.length - 1)
                                : entrustIds;
                            entrustStatus = (entrustStatus
                                .substring(entrustStatus.length - 1) == ',') ? entrustStatus
                                    .substring(0, entrustStatus.length - 1)
                                : entrustStatus;

                             // add by zhangrui1 增加交易对手字段 start
                             var counterIds = "";
                             var psList = "";
                             for (var i = 0; i < tableData.length; i++) {
                                 counterIds += tableData[i].entrustId + ":" + tableData[i].counterId + ",";
                                 psList += tableData[i].entrustId + ":" + tableData[i].ps + ",";
                             }
                             counterIds = (counterIds
                                     .substring(counterIds.length - 1) == ',') ? counterIds
                                         .substring(0, counterIds.length - 1)
                                     : counterIds;
                             psList = (psList.substring(psList.length - 1) == ',') ? psList.substring(0, psList.length - 1)
                                      : psList;
                             // add by zhangrui1 增加交易对手字段 end
                            //调用退回委托业务组件
                            Joyin.ajax(JoyinUtil.getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/refuseEntrust'),
                                {
                                    entrustIds: entrustIds,
                                    entrustStatus: entrustStatus,
                                    entrustBackReason:result
                                    // add by zhangrui1 增加交易对手字段 start
                                    ,counterIds: counterIds
                                    ,psList: psList
                                    // add by zhangrui1 增加交易对手字段 end
                                }, function (result) {
                                    //成功后刷新table
                                    otherTable.table ? otherTable.reload() : otherTable.load();
                                    table.table ? table.reload() : table.load();
                                });
                        },false);
                    },false);
                });
        //交易录单
        $('#TrdCommandReceive001_btn_transaction')
            .click(
                function () {
                    if (table != null && table != '') {
                        var tableData = table.getAllCheckedRows();
                    }
                    if (!tableData || tableData.length == 0) {
                        Joyin.error(JoyinUtil
                            .getMessage("BUSS_COM_E_0026"));
                        return;
                    }
                    var entrustIdList=[];
                    if (tableData.length > 1) {
                        if(!ISTRADERECOMMANDrECEIVE){
                            Joyin.error(JoyinUtil
                                .getMessage("BUSS_COM_E_0046"));
                            return;
                        }
                        //交易类型不一致的话
                        var isSomeType = tableData.some(function (value, index) {
                            return value.finprodType2 !== tableData[0].finprodType2;
                        });
                        if(isSomeType){
                            return Joyin.error(JoyinUtil.getMessage("TRD_PEP_003"));
                        }
                        //对于非债券及质押式回购 进行check
                            var isOth = false;
                            //key容器
                            var keyMap;
                            if(tableData[0].finprodType2 ==PINAN_ASSET_TYPE_F01){
                                //对于债券，在资产标的、交易方向、交易场所、交易平台、通道相同时，交易员可勾选多笔合并交易录单，某个字段为空时可认为是与其他相同；
                                keyMap ={
                                    //金融产品类型
                                    "finprodType2":"",
                                    //金融产品代码
                                    "finprodId":"",
                                    //交易方向
                                    "ps":"",
                                    //交易场所
                                    "tradeMarket":"",
                                    //    交易平台
                                    "tradePlat":"",
                                    //通道代码
                                    "chlId":"",
                                    //证券托管户
                                    "secManageAcctId":""
                                };
                            }
                            else if(tableData[0].finprodType2 ==PINAN_ASSET_TYPE_F07){
                                // 5.	对于正/逆回购，期限品种、回购类型、交易场所、交易方向相同时，也可以分别勾选多笔合并交易录单；
                                keyMap ={
                                    "finprodType2":"",
                                    //交易方向
                                    "ps":"",
                                    // 期限品种
                                    "termType":"",
                                    //回购类型
                                    "assetType1":"",
                                    //交易场所
                                    "tradeMarket":"",
                                    //价格模式
                                    "priceModel":"",
                                    //证券托管户
                                    "securitiesCust":""
                                };
                            }
                            else{
                                isOth = true;
                            }
                            if(!isOth){
                                var font="";
                                $.each(tableData,function(i,v){
                                    if(font){
                                        return;
                                    }
                                    entrustIdList.push(v["entrustId"]);
                                    $.each(keyMap,function(key,val){
                                        if(val){
                                            if(v[key] && val != v[key]){
                                                font = key;
                                                isOth = true;

                                            }
                                        }
                                        else{
                                            keyMap[key] = v[key];
                                        }
                                    });
                                });
                                if(font && font!="finprodType2"){
                                    //债券
                                    if(tableData[0].finprodType2 ==PINAN_ASSET_TYPE_F01){
                                        return Joyin.error(JoyinUtil.getMessage("TRD_PEP_001",JoyinUtil.getMessage("invest.channelId")));
                                    }else if(tableData[0].finprodType2 ==PINAN_ASSET_TYPE_F07){
                                        return Joyin.error(JoyinUtil.getMessage("TRD_PEP_002"));
                                    }
                                }

                            }
                        //存在非债券及质押式回购 直接提示单选
                        if(isOth){
                            return Joyin.error(JoyinUtil.getMessage("TRD_PEP_004"));
                        }
                    }
                 /*   if (tableData.length > 1) {
                        Joyin.error(JoyinUtil
                            .getMessage("BUSS_COM_E_0046"));
                        return;
                    }*/
                    if (tableData[0].finprodType2==PINAN_ASSET_TYPE_F17) {
                        Joyin.error(JoyinUtil.getMessage("TRD_J_0002"));
                        return;
                    }
                    // 平台日期小于委托开始日报错
                    if (platDate < tableData[0].vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }

                    var param = {
                        entrustId: tableData[0].entrustId,
                        proavailablecash: tableData[0].proAvailableCash,
                        pageModel: PAGE_MODEL_NEW,
                        entrustId: tableData[0].entrustId,
                        portfolioId: tableData[0].portfolioId,
                        targetFinprodId: tableData[0].finprodId,
                        finprodAbbr: tableData[0].finprodAbbr,
                        otherTradeMarket: tableData[0].otherTradeMarket,
                        otherPs: tableData[0].otherPs,
                        otherCcy: tableData[0].otherCcy,
                        pageModel: PAGE_MODEL_NEW,
                        finprodMarketId: tableData[0].finprodMarketId,
                        entrustIds:entrustIdList.join(",")
                    };
                    
                    //转到相应的投资交易录入界面
                    var pagePath = "";
                    var label = '';
                    var finprodType2 = tableData[0].finprodType2;
                    // 根据资产类型判断对应投资交易画面url
                    if (finprodType2 == PINAN_ASSET_TYPE_F08) {//拆借
                        label = 'TrdLendInvestTrade002.new';
                        pagePath = "invest/trade/lend/TrdLendInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F05) {//存款
                        label = 'TrdDepositInvestTrade002';
                        pagePath = "invest/trade/trddepositinvesttrade/TrdDepositInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F01&&tableData.length ==1&& tableData[0].ps==PS_T04) {//可转债
                        label = 'TrdConBond002';
                        pagePath = "invest/trade/conbond/TrdConBond002/init";
                    }
                    else if (finprodType2 == PINAN_ASSET_TYPE_F01&&tableData.length ==1&& tableData[0].ps==PS_T49) {//债转股
                        label = 'TrdBondToStock002';
                        pagePath = "invest/trade/bondtostock/TrdBondToStock002/init";
                    }
                    else if (finprodType2 == PINAN_ASSET_TYPE_F01&&tableData.length ==1) {//债券
                        label = 'TrdBondInvestTrade002';
                        pagePath = "invest/trade/bond/TrdBondInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F02) {//基金
                        label = 'TrdFundInvestTrade002';
                        pagePath = "invest/trade/fund/TrdFundInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F03) {//计划类利率
                        label = 'TrdPlanRateInvestTrade002';
                        pagePath = "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F04) {//计划类净值
                        label = 'TrdPlanNetInvestTrade002';
                        pagePath = "invest/trade/plannet/TrdPlanNetInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F06) {//项目类资产
                        label = 'TrdProjectInvestTrade002';
                        pagePath = "invest/trade/project/TrdProjectInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F07&&tableData.length == 1) {//质押式回购
                        label = 'TrdRepoTrade002';
                        pagePath = "invest/trade/trdrepotrade/TrdRepoTrade002/init";
                        param.portfolioId = tableData[0].portfolioId;
                        param.ps = tableData[0].ps;
                    }else if(finprodType2 == PINAN_ASSET_TYPE_F07&&tableData.length > 1){
                        label = 'TrdRepoTrade003';
                        pagePath = "invest/trade/trdrepotrade/TrdRepoTrade003/init";
                        var entrustIds = '';
                        var portfolioId = '';
                        for(var i=0;i<tableData.length;i++){
                            if(i==tableData.length-1){
                                entrustIds =entrustIds + tableData[i].entrustId;
                                portfolioId =portfolioId + tableData[i].portfolioId;
                            }else{
                                entrustIds =entrustIds + tableData[i].entrustId+",";
                                portfolioId =portfolioId + tableData[i].portfolioId+",";
                            }
                        }
                        param.entrustId=entrustIds;
                        param.portfolioId = portfolioId;
                        param.ps = tableData[0].ps;
                    } else if(finprodType2 == PINAN_ASSET_TYPE_F01&&tableData.length > 1){//债券交易
                        label = '多组合债券交易新建';
                        pagePath = "invest/trade/bond/TrdBondInvestTrade003/init";
                        var entrustIds = '';
                        var portfolioId = '';
                        for(var i=0;i<tableData.length;i++){
                            if(i==tableData.length-1){
                                entrustIds =entrustIds + tableData[i].entrustId;
                                portfolioId =portfolioId + tableData[i].portfolioId;
                            }else{
                                entrustIds =entrustIds + tableData[i].entrustId+",";
                                portfolioId =portfolioId + tableData[i].portfolioId+",";
                            }
                        }
                        param.entrustId=entrustIds;
                        param.portfolioId = portfolioId;
                        param.ps = tableData[0].ps;
                    }
                    else if (finprodType2 == PINAN_ASSET_TYPE_F10) {//买断式回购
                        label = 'TrdOutRightRepoTrade002.new';
                        pagePath = "invest/trade/buyoutrepo/TrdOutRightRepoTrade002/init";
                        param.portfolioId = tableData[0].portfolioId;
                        param.ps = tableData[0].ps;
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F09) {//计划类货币
                        label = 'TrdPlanCcyInvestTrade002.reference';
                        pagePath = "invest/trade/planccy/TrdPlanCcyInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F11) {// 股票交易
                        label = 'TrdStock002';
                        pagePath = "invest/trade/stockdeal/TrdStock002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F13) { //外汇即期
                        label = 'TrdSpotexInvestTrade002.refrence';
                        pagePath = "invest/trade/spotex/TrdSpotexInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F14) { //外汇远期
                        label = 'TrdForwexInvestTrade002.reference';
                        pagePath = "invest/trade/forwex/TrdForwexInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F15) { //外汇掉期
                        label = 'TrdFxswapInvestTrade002.reference';
                        pagePath = "invest/trade/trdfxswapinvesttrade/TrdFxswapInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F12) { //债券借贷委托
                        label = 'TrdBondLendInvestTrade002';
                        pagePath = "invest/trade/bondlend/TrdBondLendInvestTrade002/init";
                    } else if (finprodType2 == PINAN_ASSET_TYPE_F16) { //利率互换
                        label = 'TrdIRSTrade002.reference';
                        pagePath = "invest/trade/irs/TrdIRSTrade002/init";
                    }else if (finprodType2 == PINAN_ASSET_TYPE_F18) { //期权交易
                        label = 'TrdOptionTrade002';
                        pagePath = "invest/trade/option/TrdOptionTrade002/init";
                    }else if(finprodType2 == 'F36'){
                        Joyin.error("立项指令暂不支持交易录单");
                        return;
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    else if (finprodType2 == PINAN_ASSET_TYPE_F19) { //权益类资产交易
                        label = 'TrdProjectNetInvestTrade002';
                        pagePath = "invest/trade/projectnet/TrdProjectNetInvestTrade002/init";
                    }
                    // add by etliu 20210421 权益类资产相关代码合并 end

                    pagePath = basePath + pagePath;
                    // Joyin.ajax(pagePath,
                    // {entrustId:tableData[0].entrustId},function(result)
                    // {
//        		table.reload();
//            });
                    // Joyin.dialog(JoyinUtil.getLabel(label), pagePath,
                    // "99%", "99%", {
                    // entrustId :
                    // tableData[0].entrustId,pageModel:PAGE_MODEL_NEW,
//            });
                    var canOpen = [];
                    //交易录单的check
                    $.each(tableData,function (index,data) {
                        Joyin.ajaxJson(
                            JoyinUtil
                                .getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/check'),
                            {
                                entrustStatus: data.entrustStatus,
                                operateType: 'trdTrade',
                                //存在流程中数据，不可以进入交易录单页面（回购，债券可以多次录入（提交会有check其流程中与已完成额度））
                                entrustId: data.entrustId,
                                finprodType2 : data.finprodType2
                            }, function (result) {
                                canOpen.push(true);
                            },false);

                    });

                    if(canOpen.length==tableData.length){
                        Joyin.dialog(JoyinUtil
                                .getLabel(label),
                            pagePath, "99%", "99%",
                            param);
                    }
                });

        //交易预录入
        $('#TrdCommandReceive001_btn_preTrade').click(function(){
            if (table != null && table != '') {
                var tableData = table.getAllCheckedRows();
            }
            if (!tableData || tableData.length == 0) {
                Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0026"));
                return;
            }
            var entrustIdList=[];
            var finprodType2 = tableData[0].finprodType2;
            var basicData = tableData[0];
            $.each(tableData,function(i,v){
                entrustIdList.push(v.entrustId);
            });
            var pagePath = "";
            var label = '';
            if(finprodType2 == PINAN_ASSET_TYPE_F07){
                label = "PreTrdRepoTrade002";
                pagePath = "invest/trade/pretrdrepotrade/PreTrdRepoTrade002/init"
            }else if(finprodType2 == PINAN_ASSET_TYPE_F01){
            	label = "PreTrdBondInvestTrade002";
                pagePath = "invest/trade/pretrdbondtrade/PreTrdBondInvestTrade002/init"
            }
            var param = {
                entrustId: entrustIdList.join(",")
            };
            Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/entrustCheck', param, function (result) {
                if(result.errorList.length == 0){
                    pagePath = basePath + pagePath;
                    Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
                }
            },false);
        });
        //导出
        $('#TrdCommandReceive001_btn_export').click(function () {
            exportExcel(index);
        });

        //合同签署结果
        $('#TrdCommandReceive001_btn_contract').click(function () {
            //进入合同签署界面
            if (table != null && table != '') {
                var tableData = table.getAllCheckedRows();
            }
            if (!tableData || tableData.length == 0) {
                Joyin.error(JoyinUtil
                    .getMessage("BUSS_COM_E_0026"));
                return;
            }
            var entrustIdList=[];
            if ( tableData.length ==0 ||tableData.length > 1  ) {
                Joyin.error("请选择一条数据");

            }else  {
               if(tableData[0].finprodType2 == 'F36'){
                   tableData[0].pageModel ==PAGE_MODEL_EDIT;
                   Joyin.ajaxJson(basePath + 'invest/entrust/etrinstruction/EtrInstruction001/check', tableData[0], function (result) {
                       Joyin.ajaxJson(basePath + 'invest/entrust/etrinstruction/EtrInstruction001/editCheck', tableData[0], function (editResult) {
                   Joyin.dialog(JoyinUtil.getLabel("EtrInstruction002.htqswh"),
                       basePath
                       + "invest/entrust/etrinstruction/EtrInstruction003/init",
                       "95%", "80%",tableData[0] ,
                       function(){table.reload();});
                       });
                   });
            }else {
                   Joyin.error("只能对立项指令进行合同签署结果操作");

               }

            }
        });


        
      //平安需求变更-指令接收定时自动刷新 sjj add 2019/04/03 start
        function myrefresh()
        {
    		otherTable.reload();
    		Joyin.ajax(JoyinUtil.getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/otherSelectList'), getPageParams(),
    			function(result) {
    			$('#number').html(result);
    		});
    		table.reload();
    		Joyin.ajax(JoyinUtil.getRealPath('invest/trade/investtrade/commandreceive/TrdCommandReceive001/SelectList'), getPageParams(),
    				function(result) {
    				$('#numberreceive').html(result);
    			});
    		$('#detailTable_Div').attr("hidden",true);
        }
        setInterval(function(){myrefresh()},30000); 
      //平安需求变更-指令接收定时自动刷新 sjj add 2019/04/03 end

    });


}();
