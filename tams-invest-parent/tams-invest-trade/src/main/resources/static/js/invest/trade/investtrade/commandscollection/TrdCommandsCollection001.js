$(function() {
    /**
     * 当前所选大类
     * */
    var assetType = PINAN_ASSET_TYPE_F01;
    var assetTypeFormer = PINAN_ASSET_TYPE_F01;
    // 第一次加载 的判断标志
    var F02flag = F03flag = F04flag = F05flag = F06flag = F07flag = F08flag =
        F09flag = F10flag = F11flag = F17flag = Con1flag = Con2flag = true;
    $("li").click(function() {
        assetType = $(this).attr('value');
        if(assetType != assetTypeFormer){
            $('#TrdCommandsCollection001_' + assetTypeFormer + '_searchForm').hide();
            $('#TrdCommandsCollection001_' + assetType + '_searchForm').show();
            $('#TrdCommandsCollection001_' + assetType + '_searchForm').children().show();
            $('#table' + assetTypeFormer + 'Form').hide();
            $('#table' + assetType + 'Form').show();
            $('#table' + assetType + 'Form').children().show();
            $('#entrustTable' + assetTypeFormer + 'Form').hide();
            $('#entrustTable' + assetType + 'Form').show();
            $('#entrustTable' + assetType + 'Form').children().show();
            
            assetTypeFormer = assetType;
        }
        // if(assetType == PINAN_ASSET_TYPE_F01 || assetType == PINAN_ASSET_TYPE_F02
        //         || assetType == PINAN_ASSET_TYPE_F07 || assetType == PINAN_ASSET_TYPE_F11
        //         || assetType == 'Con1'){
        //     $('#TrdCommandsCollection001_btn_reset').show();
        // }
        // else{
        //     $('#TrdCommandsCollection001_btn_reset').hide();
        // }

        if(assetType == PINAN_ASSET_TYPE_F07){
            $('#pledgeList_div').show();
            $('#pledgeList_div').children().show();
        }
        else{
            $('#pledgeList_div').hide();
        }

        //基金、股票、拆借、可转债申购、可转债转股暂时隐藏交易录单按钮，功能完成后放出按钮
        if(assetType == PINAN_ASSET_TYPE_F02 ||assetType == PINAN_ASSET_TYPE_F08||assetType == PINAN_ASSET_TYPE_F11 ||assetType == 'Con1' ||assetType == 'Con2'  ) {
            $('#TrdCommandsCollection001_btn_transaction').hide();
        }
        else
        {
            $('#TrdCommandsCollection001_btn_transaction').show();
        }
        /*
         * 第一次进入模块时加载表格
         * */
        switch(assetType){
        case "Con1":
            if(Con1flag){
                Con1flag = false;
                tableCon1 =$("#tableCon1").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList',
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableCon1.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'15%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdFundInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'purchModleStr', width:'10%', title: 'TrdConBond001.purchModle',sortField:"purchModle"}
                        ,{field:'purchTypeStr', width:'10%', title: 'TrdConBond001.purchType',sortField:"purchType"}
                        ,{field:'tradeMarketStr', width:'10%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'unfinishedAmt', width:200, title: 'TrdCommandsCollection002.unfinishedAmtF01', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                        ,{field:'secAcctName', title: 'TrdStock002.secAcctId', sortField: "secAcctId"}
                        ,{field:'chlName', width:'10%', title: 'invest.channelId', visible:false,sortField:"chlId"}
                    ],
                });

                /**
                 * 可转债申购资产代码链接跳转
                 */
                tableCon1.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"),
                            basePath + "invest/asset/bond/FinBondTargetAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });

                entrustTableCon1 =$("#entrustTableCon1").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'invAimStr', width:'10%', title: 'TrdBondInvestTrade002.invAim',sortable:false}
                        ,{field:'purchModleStr', width:'10%', title: 'TrdConBond001.purchModle',sortField:"purchModle"}
                        ,{field:'purchTypeStr', width:'10%', title: 'TrdConBond001.purchType',sortField:"purchType"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdBondToStock001.entrustAmt', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF01', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF01', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'secAcctName', width:'10%', title: 'TrdStock002.secAcctId',sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });

                /**
                 * 绑定可转债委托明细交易录单事件
                 */
                entrustTableCon1.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F01
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdConBond002"),
                                        basePath + "invest/trade/conbond/TrdConBond002/init",
                                        "99%", "99%", data);
                    });
                });

            }

            break;
        case "Con2":
            if(Con2flag){
                Con2flag = false;
                tableCon2 =$("#tableCon2").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList',
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableCon2.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'10%', title: 'TrdBondToStock001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'10%', title: 'TrdBondToStock001.finprodName',sortField:"finprodName"}
                        ,{field:'tradeMarketStr', width:'10%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'shareId', width:'10%', title: 'TrdCommandsCollection001.shareId',sortField:"shareId"}
                        ,{field:'shareName', width:'10%', title: 'TrdCommandsCollection001.shareName',sortField:"shareName"}
                        ,{field:'conShareId', width:'10%', title: 'TrdBondToStock001.conShareId',sortField:"conShareId"}
                        ,{field:'stocketrPriceTotal', width: 200, title: 'TrdCommandsCollection001.stocketrPriceTotal', type: 'number', sortField:"stocketrPriceTotal"}
                        ,{field:'expConValue', width: 200, title: 'TrdCommandsCollection001.expConValue', type: 'number', sortField:"expConValue"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF01', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                        ,{field:'secAcctName', width:'10%', title: 'TrdStock002.secAcctId', sortField: "secAcctId"}
                        ,{field:'chlName', width:'10%', title: 'invest.channelId', visible:false,sortField:"chlId"}
                    ],
                });

                /**
                 * 可转债转股资产代码链接跳转
                 */
                tableCon2.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"),
                            basePath + "invest/asset/bond/FinBondTargetAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });

                entrustTableCon2 =$("#entrustTableCon2").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'invAimStr', width:'10%', title: 'TrdBondInvestTrade002.invAim',sortable:false}
                        ,{field:'holdetrPriceTotal', width:'10%', title: 'TrdBondToStock001.holdetrPriceTotal', type: 'number',sortField:"holdetrPriceTotal"}
                        ,{field:'conetrPriceTotal', width:'10%', title: 'TrdBondToStock001.conetrPriceTotal', type: 'number',sortField:"conetrPriceTotal"}
                        ,{field:'stocketrPriceTotal', width:'10%', title: 'TrdCommandsCollection001.stocketrPriceTotal', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF01', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF01', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'secAcctName', width:'10%', title: 'TrdStock002.secAcctId',sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });

                /**
                 * 绑定债转股委托明细交易录单事件
                 */
                entrustTableCon2.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F01
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdBondToStock001"),
                                        basePath + "invest/trade/bondtostock/TrdBondToStock002/init",
                                        "99%", "99%", data);
                    });
                });
            }

            break;
        case PINAN_ASSET_TYPE_F02:
            if(F02flag){
                F02flag = false;
                tableF02 =$("#tableF02").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF02.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'10%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'10%', title: 'TrdFundInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                        ,{field:'tradeMarketStr', width:'10%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'fundTypeStr', width:'10%', title: 'TrdFundInvestTrade001.type6',sortField:"type6"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'secAcctName', title: 'TrdStock002.secAcctId', sortField: "secAcctId"}
                        ,{field:'chlName', width:'10%', title: 'invest.channelId',visible:false,sortField:"chlId"}
                    ],
                });
                
                /**
                 * 基金资产代码链接跳转
                 */
                tableF02.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinFundTarget002.detail"),
                            basePath + "invest/asset/fund/FinFundTarget002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF02 =$("#entrustTableF02").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        // ,{field:'settleSpeedStr', width:'10%', title: 'TrdRepoTrade002.settleSpeed', sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF02', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'secAcctName', title: 'TrdStock002.secAcctId',sortable:false,visible:true}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });

                /**
                 * 绑定基金委托明细交易录单事件
                 */
                entrustTableF02.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F02
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdFundInvestTrade002"),
                                        basePath + "invest/trade/fund/TrdFundInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
                
            }
            break;
        case PINAN_ASSET_TYPE_F03:
            if(F03flag){
                F03flag = false;
                tableF03 =$("#tableF03").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF03.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'10%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdPlanNetInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'planTypeStr', width:'10%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'chlName', width:'10%', title: 'invest.channelId',visible:false,sortField:"chlId"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                    ],
                });

                /**
                 * 计划类利率型资产代码链接跳转
                 */
                tableF03.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinPlanProfitAsset002.detail"),
                            basePath + "invest/asset/plan/planProfit/FinPlanProfitAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF03 =$("#entrustTableF03").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF02', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定计划类利率型委托明细交易录单事件
                 */
                entrustTableF03.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F03
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdPlanRateInvestTrade002"),
                                        basePath + "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
                
            }
            
            break;
        case PINAN_ASSET_TYPE_F04:
            if(F04flag){
                F04flag = false;
                tableF04 =$("#tableF04").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF04.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'10%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdPlanNetInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'planTypeStr', width:'10%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                    ],
                });
                
                /**
                 * 计划类净值型资产代码链接跳转
                 */
                tableF04.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinPlanNetAsset002.detail"),
                            basePath + "invest/asset/plan/planNet/FinPlanNetAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF04 = $("#entrustTableF04").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF02', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定计划类净值型委托明细交易录单事件
                 */
                entrustTableF04.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F04
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdPlanNetInvestTrade002"),
                                        basePath + "invest/trade/plannet/TrdPlanNetInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F05:
            if(F05flag){
                F05flag = false;
                tableF05 =$("#tableF05").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF05.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'15%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdPlanNetInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'depoTypeStr', width:'10%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'chlName', title: 'invest.channelId',visible:false,sortField:"chlId"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                        ,{field:'ps', visible:false}
                    ],
                });
                
                /**
                 * 存款类资产代码链接跳转
                 */
                tableF05.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinDepositTarget002.title"),
                            basePath + "invest/asset/depo/FinDepositTarget002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF05 = $("#entrustTableF05").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'price', width:'10%', title: 'TrdDepositInvestTrade002.loadRate', type: 'number', digit: 4, sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF07', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortable:false}
                        //,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection001.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定存款委托明细交易录单事件
                 */
                entrustTableF05.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F05
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdDepositInvestTrade002"),
                                        basePath + "invest/trade/trddepositinvesttrade/TrdDepositInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F06:
            if(F06flag){
                F06flag = false;
                tableF06 =$("#tableF06").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF06.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'10%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdPlanNetInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'projectTypeStr', width:'10%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'chlName', width:'10%', title: 'invest.channelId',visible:false,sortField:"chlId"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                    ],
                });
                
                /**
                 * 项目类资产代码链接跳转
                 */
                tableF06.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinProjectTargetAsset002.title"),
                            basePath + "invest/asset/project/FinProjectTargetAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF06 = $("#entrustTableF06").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF07', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection001.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定项目类委托明细交易录单事件
                 */
                entrustTableF06.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F06
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdProjectInvestTrade002"),
                                        basePath + "invest/trade/project/TrdProjectInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F07:
            if(F07flag){
                F07flag = false;
                tableF07 =$("#tableF07").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF07.reload({data: result});
                        });
                        if(data.pledgeFinprod == '01'){
                            pledgeListTable.reload({data: []});
                        }
                        else{
                            Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectPledgeList', 
                                    {entity: JSON.stringify(data)}, function(result) {
                                pledgeListTable.reload({data: result});
                            });
                        }
                    },
                    height:150,
                    cols:[
                        {field:'pledgeFinprodStr', width:'15%', title: 'TrdOutRightRepoTrade002.finprodMarketId', sortField:"pledgeFinprod"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps', sortField:"ps"}
                        ,{field:'tradeMarketStr', width:'15%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'repoTypeStr', width:'15%', title: 'TrdRepoTrade001.repoType', sortField:"type6"}
                        ,{field:'termDays', width:'10%', title: 'TrdCommandsCollection002.repoTerm', sortField:"termDays"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'settleSpeedStr', width:'10%', title: 'TrdRepoTrade002.settleSpeed', sortField:"settleSpeed"}
                        ,{field:'ccyStr', title: 'TrdFundInvestTrade001.ccy', sortField:"ccy"}
                    ]
                });
                
                entrustTableF07 = $("#entrustTableF07").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'priceModelStr',  width:'10%', title: 'TrdCommandsCollection002.priceMode',sortable:false}
                        ,{field:'price', width:'10%', title: 'TrdLendInvestTrade002.intRate', type: 'number', digit: 4, sortable:false}
                        ,{field:'ps', visible:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF07', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortable:false}
                        ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection001.lowestDealNum', sortable:false}
                        ,{field:'secAcctName', width:'10%', title: 'TrdStock002.secAcctId',sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定质押式回购委托明细交易录单事件
                 */
                entrustTableF07.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F07
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdRepoTrade002.new"),
                                        basePath + "invest/trade/trdrepotrade/TrdRepoTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F08:
            if(F08flag){
                F08flag = false;
                tableF08 =$("#tableF08").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF08.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'lendTypeStr', width:'15%', title: 'EtrLendSpecInfo001.type6',sortField:"type6"}
                        ,{field:'calendarId', title: 'TrdLendInvestTrade002.calendarId', visible : false,sortable : false }
                        ,{field:'psStr',  width:'15%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'termDays', width:'15%', title: 'TrdCommandsCollection002.repoTerm', sortField:"termDays"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', width:'15%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                        ,{field:'calendarName', title: 'TrdLendInvestTrade002.calendarId',sortField:"calendarId"}
                    ],
                });
                
                entrustTableF08 = $("#entrustTableF08").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'price', width:'10%', title: 'TrdLendInvestTrade002.intRate', type: 'number', digit: 4, sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF07', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF07', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF07', type: 'number', sortable:false}
                        ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection001.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定拆借委托明细交易录单事件
                 */
                entrustTableF08.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F08
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdLendInvestTrade002"),
                                        basePath + "invest/trade/lend/TrdLendInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F09:
            if(F09flag){
                F09flag = false;
                tableF09 =$("#tableF09").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF09.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'10%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdPlanNetInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'planTypeStr', width:'10%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'ccyStr', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                    ],
                });
                
                /**
                 * 计划类货币型资产代码链接跳转
                 */
                tableF09.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinPlanAssetC002.title"),
                            basePath + "invest/asset/plan/plancur/FinPlanAssetC002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF09 = $("#entrustTableF09").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF02', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF02', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF02', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定计划类货币型委托明细交易录单事件
                 */
                entrustTableF09.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F09
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdPlanCcyInvestTrade002.reference"),
                                        basePath + "invest/trade/planccy/TrdPlanCcyInvestTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F10:
            if(F10flag){
                F10flag = false;
                tableF10 =$("#tableF10").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF10.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'20%', title: 'TrdOutRightRepoTrade002.finprodMarketId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'tradeMarketStr', width:'20%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'termDays', width:'10%', title: 'TrdCommandsCollection002.repoTerm',sortField:"termDays"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdOutRightRepoTrade002.amtInfo.bondTotalAmt', type: 'number', sortField:"entrustAmt"}
                        ,{field:'settleSpeedStr', width:'15%', title: 'TrdRepoTrade002.settleSpeed',sortField:"settleSpeed"}
                        ,{field:'ccyStr', width:'15%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                    ]
                });

                /**
                 * 买断式回购质押标的代码链接跳转
                 */
                tableF10.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"),
                            basePath + "invest/asset/bond/FinBondTargetAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF10 = $("#entrustTableF10").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'priceModelStr',  width:'10%', title: 'TrdCommandsCollection002.priceMode',sortable:false}
                        ,{field:'price', width:'10%', title: 'TrdOutRightRepoTrade002.amtInfo.repoRate', type: 'number', digit: 4, sortable:false}
                        ,{field:'firstPrice', width:'10%', title: 'TrdOutRightRepoTrade002.amtInfo.firstCleanPrice', sortable:false,
                            render : function(data, type, row, meta) {
                                if(row.firstPrice != null && row.firstPrice2 != null){
                                    return row.firstPrice + " - " + row.firstPrice2;
                                }
                                else{
                                    return '';
                                }
                            }
                        }
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF11', type: 'number', sortable:false}
                        ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定买断式回购委托明细交易录单事件
                 */
                entrustTableF10.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F10
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdOutRightRepoTrade002.new"),
                                        basePath + "invest/trade/buyoutrepo/TrdOutRightRepoTrade002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F11:
            if(F11flag){
                F11flag = false;
                tableF11 =$("#tableF11").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    click: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF11.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'15%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'10%', title: 'TrdPlanNetInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                        ,{field:'tradeMarketStr', width:'10%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'boardStr', width:'10%', title: 'TrdStock002.board',sortField:"board"}
                        ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF11', type: 'number', sortField:"entrustAmt"}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF11', type: 'number', sortField:"unfinishedAmt"}
                        ,{field:'secAcctName', width:'15%', title: 'TrdStock002.secAcctId', sortField: "secAcctId"}
                    ]
                });

                /**
                 * 股票资产代码链接跳转
                 */
                tableF11.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinStockInfo002.title"),
                            basePath + "invest/asset/stock/FinStockInfo002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF11 = $("#entrustTableF11").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustIdF11',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdateF11', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'priceModelStr',  width:'10%', title: 'TrdCommandsCollection002.priceMode',sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF11', type: 'number', sortable:false}
                        ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF11', type: 'number', sortable:false}
                        ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF11', type: 'number', sortable:false}
                        // ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                        ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                                title: '交易录单',
                                clas: 'transaction',
                                text: '<a class="trans">交易录单</a>'
                            }]//操作：交易录单
                        }
                    ]
                });
                
                /**
                 * 绑定股票委托明细交易录单事件
                 */
                entrustTableF11.bind('click', '.transaction', function(data, index) {
                    // 平台日期小于委托开始日报错
                    if (platDate < data.vdateStr) {
                        Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
                        return;
                    }
                    Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                            {
                                entrustStatus: ENTRUST_STATUS_03,
                                operateType: 'trdTrade',
                                entrustId: data.entrustId,
                                finprodType2 : PINAN_ASSET_TYPE_F11
                            }, function (result) {
                                Joyin.dialog(JoyinUtil.getLabel("TrdStock002"),
                                        basePath + "invest/trade/stockdeal/TrdStock002/init",
                                        "99%", "99%", data);
                    });
                });
            }
            
            break;
        case PINAN_ASSET_TYPE_F17:
            if(F17flag){
                F17flag = false;
                tableF17 =$("#tableF17").joyintable({
                    ajax: {
                        url: 'findPage',
                        type: 'POST',
                        data: function() {
                            return getPageParams(assetType);
                        }
                    },
                    dblclick: function (table, data) {
                        Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                                {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                            entrustTableF17.reload({data: result});
                        });
                    },
                    height:150,
                    cols:[
                        {field:'finprodId', visible: false}
                        ,{field:'finprodMarketId', width:'15%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                            render : function(data, type, row, meta) {
                                return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                            }
                        }
                        ,{field:'finprodName', width:'15%', title: 'TrdFundInvestTrade001.finprodName',sortField:"finprodName"}
                        ,{field:'type6Str', width:'15%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
                        ,{field:'tradeMarketStr', width:'15%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
                        ,{field:'counterName',  width:'15%', title: 'TrdIRSTrade.counterId',sortField:"counterId"}
                        ,{field:'entrustAmt', width: 200, title: 'TrdCommandsCollection002.entrustAmtF17', type: 'number', sortField:"entrustAmt"}
                        ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
                    ]
                });

                /**
                 * 债券一级投标资产代码链接跳转
                 */
                tableF17.bind('click','.finprodId-link', function(row) {
                    Joyin.dialog(JoyinUtil.getLabel("FinTenderBondTargetAsset002.title"),
                            basePath + "invest/asset/tenderbond/FinTenderBondTargetAsset002/init",
                            "95%", "90%",
                            { pageModel:PAGE_MODEL_REFERENCE,
                            finprodId: row.finprodId,
                            finprodName: row.finprodName });
                });
                
                entrustTableF17 = $("#entrustTableF17").joyintable({
                    checkbox: false,
                    contextmenu: false,
                    rowno: true,
                    page: false,
                    searchOnInit: true,
                    height:150,
                    cols:[
                        {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
                        ,{field:'vdate', visible:false}
                        ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
                        ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
                        ,{field:'invAimStr', width:'10%', title: 'TrdBondInvestTrade002.invAim',sortable:false}
                        ,{field:'bidPrice', width:'10%', title: 'PrimaryBiddingManage002.bidprice', type: 'number',sortable:false}
                        ,{field:'entrustAmt', width: 200, title: 'TrdBondToStock001.entrustAmt', type: 'number', sortable:false}
                        ,{field:'lowestDealNum', width:'10%', title: 'TrdCommandsCollection002.lowestDealNum', sortable:false}
                        ,{field:'secAcctName', width:'10%', title: 'TrdStock002.secAcctId',sortable:false}
                        ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
                        ,{field:'remark', width:'10%', title: 'TrdCommandReceive001.remark',sortable:false}
                        ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                            render : function(data, type, row, meta) {
                                var htmlStr = "";
                                for(var i in row.files){
                                    htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                                }
                                return htmlStr;
                            }
                        }
                    ]
                });
            }
            
            break;
        }
    });
    
    /**
     * 自动刷新参数
     * */
    var refresh;
    
    /**
     * 获取查询条件
     * */
    var getPageParams = function(type){
        var param = $.serializeToObject('#TrdCommandsCollection001_' + type + '_searchForm');
        param.assetType = type;
        switch(type){
        case PINAN_ASSET_TYPE_F01:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            if (param.type7) {
                param.type7 = param.type7.join(",");
            }
            break;
        case PINAN_ASSET_TYPE_F02:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            break;
// add by zoujiamin ZGBSS-190  2021/1/22
        case PINAN_ASSET_TYPE_F03:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            break;
        case PINAN_ASSET_TYPE_F04:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            break;
        case PINAN_ASSET_TYPE_F05:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            break;
        case PINAN_ASSET_TYPE_F06:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            break;
        case PINAN_ASSET_TYPE_F09:
            if (param.type6) {
                param.type6 = param.type6.join(",");
            }
            break;
        }
        return param;
    }
    
    /**
     * 债券指令汇总数据表格
     */
    var tableF01 =$("#tableF01").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getPageParams(assetType);
            }
        },
        dblclick: function (table, data) {
            Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                    {assetType: assetType, entity: JSON.stringify(data)}, function(result) {
                entrustTableF01.reload({data: result});
            });
        },
        height:150,
        cols:[
            {field:'finprodId', visible: false}
            ,{field:'finprodMarketId', width:'15%', title: 'TrdFundInvestTrade001.finprodId',sortField:"finprodId",
                render : function(data, type, row, meta) {
                    return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                }
            }
            ,{field:'finprodName', width:'10%', title: 'TrdFundInvestTrade001.finprodName',sortField:"finprodName"}
            ,{field:'type6Str', width:'10%', title: 'TrdCommandsCollection002.type6',sortField:"type6"}
            ,{field:'type7Str', width:'10%', title: 'TrdCommandsCollection002.type7',sortField:"type7"}
            ,{field:'psStr',  width:'10%', title: 'TrdFundInvestTrade001.ps',sortField:"ps"}
            ,{field:'tradeMarketStr', width:'10%', title: 'TrdFundInvestTrade001.tradeMarket',sortField:"tradeMarket"}
            ,{field:'tradePlatStr', width:'10%', title: 'EtrInvestBondInfo001.tradePlat',sortField:"tradePlat"}
            ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF01', type: 'money', sortField:"unfinishedAmt"}
            ,{field:'settleSpeedStr', width:'10%', title: 'TrdRepoTrade002.settleSpeed',sortField:"settleSpeed"}
            ,{field:'ccyStr', width:'10%', title: 'TrdFundInvestTrade001.ccy',sortField:"ccy"}
        ],
    });
    
    /**
     * 可转债申购指令汇总数据表格
     */
    var tableCon1;
    
    /**
     * 可转债转股指令汇总数据表格
     */
    var tableCon2;

    /**
     * 基金指令汇总数据表格
     */
    var tableF02;

    /**
     *计划类利率型指令汇总数据表格
     */
    var tableF03;

    /**
     *计划类净值型指令汇总数据表格
     */
    var tableF04;

    /**
     *存款指令汇总数据表格
     */
    var tableF05;

    /**
     *项目类指令汇总数据表格
     */
    var tableF06;

    /**
     * 质押式回购指令汇总数据表格
     */
    var tableF07;

    /**
     * 拆借指令汇总数据表格
     */
    var tableF08;

    /**
     *计划类货币型指令汇总数据表格
     */
    var tableF09;

    /**
     * 买断式回购指令汇总数据表格
     */
    var tableF10;
    
    /**
     * 股票指令汇总数据表格
     */
    var tableF11;
    
    /**
     * 债券一级投标指令汇总数据表格
     */
    var tableF17;
    
    /**
     * 债券资产代码链接跳转
     */
    tableF01.bind('click','.finprodId-link', function(row) {
        Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"),
                basePath + "invest/asset/bond/FinBondTargetAsset002/init",
                "95%", "90%",
                { pageModel:PAGE_MODEL_REFERENCE,
                finprodId: row.finprodId,
                finprodName: row.finprodName });
    });
    
    /**
     * 下拉框选项初始化
     * */
    var init = function(){
        $('#TrdCommandsCollection001_btn_reset').hide();
        var PS_SELECTIONS = JSON.parse(PS);
        var TRADE_PLACE_SELECTIONS = JSON.parse(TRADE_PLACE);
        var DEAL_MODE_SELECTIONS = JSON.parse(DEAL_MODE);
        var BACK_LEND_SELECTIONS = JSON.parse(BACK_LEND_DIRECTION);
        var psF01Selections = [];
        var psF02Selections = [];
        var psF03Selections = [];
        var psF04Selections = [];
        var psF05Selections = [];
        var psF06Selections = [];
        var psF08Selections = [];
        var psF09Selections = [];
        var psF10Selections = [];
        var psF11Selections = [];
        var tradeMarket12Selections = [];
        var tradeMarket124Selections = [];
        var tradeMarket012Selections = [];
        var tradeMarket02345Selections = [];
        var dealMode0123Selections = [];

        for (var i =0 ;i< PINAN_ASSET_TYPE_F01_PS.length;i++){
            psF01Selections.push(PINAN_ASSET_TYPE_F01_PS[i].dictItemVal);
        }
        for (var i =0 ;i< PINAN_ASSET_TYPE_F03_PS.length;i++){
            psF03Selections.push(PINAN_ASSET_TYPE_F03_PS[i].dictItemVal);
        }
        for (var i =0 ;i< PINAN_ASSET_TYPE_F04_PS.length;i++){
            psF04Selections.push(PINAN_ASSET_TYPE_F04_PS[i].dictItemVal);
        }
        for (var i =0 ;i< PINAN_ASSET_TYPE_F05_PS.length;i++){
            psF05Selections.push(PINAN_ASSET_TYPE_F05_PS[i].dictItemVal);
        }
        for (var i =0 ;i< PINAN_ASSET_TYPE_F06_PS.length;i++){
            psF06Selections.push(PINAN_ASSET_TYPE_F06_PS[i].dictItemVal);
        }
        for (var i =0 ;i< PINAN_ASSET_TYPE_F09_PS.length;i++){
            psF09Selections.push(PINAN_ASSET_TYPE_F09_PS[i].dictItemVal);
        }
        for (var i =0 ;i< PINAN_ASSET_TYPE_F11_PS.length;i++){
            psF11Selections.push(PINAN_ASSET_TYPE_F11_PS[i].dictItemVal);
        }

        // ps01Selections.push(PS_SELECTIONS[0].id);
        // ps01Selections.push(PS_SELECTIONS[3].id);
        // psF01Selections.push(PS_SELECTIONS[0].id);
        // psF01Selections.push(PS_SELECTIONS[3].id);
        // modify by wanshiwei ZGZGST-2586 基金tab交易方向枚举值应该为申购/赎回/买入/卖出/产品违约赎回 20210912 start
        psF02Selections.push(PS_SELECTIONS[6].id);
        psF02Selections.push(PS_SELECTIONS[11].id);
        psF02Selections.push(PS_SELECTIONS[1].id);
        psF02Selections.push(PS_SELECTIONS[3].id);
        psF02Selections.push(PS_SELECTIONS[91].id);
        // modify by wanshiwei ZGZGST-2586 基金tab交易方向枚举值应该为申购/赎回/买入/卖出/产品违约赎回 20210912 end
        // psF04Selections.push(PS_SELECTIONS[0].id);
        // psF04Selections.push(PS_SELECTIONS[3].id);
        // psF04Selections.push(PS_SELECTIONS[6].id);
        // psF04Selections.push(PS_SELECTIONS[11].id);
        psF08Selections.push(BACK_LEND_SELECTIONS[0].id);
        psF08Selections.push(BACK_LEND_SELECTIONS[1].id);
        psF10Selections.push(BACK_LEND_SELECTIONS[2].id);
        psF10Selections.push(BACK_LEND_SELECTIONS[3].id);
        // psF11Selections.push(PS_SELECTIONS[0].id);
        // psF11Selections.push(PS_SELECTIONS[3].id);
        // psF11Selections.push(PS_SELECTIONS[103].id);
        // psF11Selections.push(PS_SELECTIONS[105].id);
        // psF11Selections.push(PS_SELECTIONS[104].id);
        tradeMarket12Selections.push(TRADE_PLACE_SELECTIONS[2].id);
        tradeMarket12Selections.push(TRADE_PLACE_SELECTIONS[1].id);
        // 修改屏蔽 基金为（上交所、深交所） add wsz 20220704  3.0.7 7.13版本
        // tradeMarket124Selections.push(TRADE_PLACE_SELECTIONS[1].id);
        // tradeMarket124Selections.push(TRADE_PLACE_SELECTIONS[2].id);
        tradeMarket124Selections.push(TRADE_PLACE_SELECTIONS[4].id);
        // 修改屏蔽 基金为（上交所、深交所） add wsz 20220704  3.0.7 7.13版本
        tradeMarket012Selections.push(TRADE_PLACE_SELECTIONS[0].id);
        tradeMarket012Selections.push(TRADE_PLACE_SELECTIONS[2].id);
        tradeMarket012Selections.push(TRADE_PLACE_SELECTIONS[1].id);
        tradeMarket02345Selections.push(TRADE_PLACE_SELECTIONS[0].id);
        tradeMarket02345Selections.push(TRADE_PLACE_SELECTIONS[2].id);
        // tradeMarket02345Selections.push(TRADE_PLACE_SELECTIONS[3].id);
        tradeMarket02345Selections.push(TRADE_PLACE_SELECTIONS[1].id);
        // tradeMarket02345Selections.push(TRADE_PLACE_SELECTIONS[5].id);
        dealMode0123Selections.push(DEAL_MODE_SELECTIONS[0].id);
        dealMode0123Selections.push(DEAL_MODE_SELECTIONS[1].id);
        dealMode0123Selections.push(DEAL_MODE_SELECTIONS[2].id);
        dealMode0123Selections.push(DEAL_MODE_SELECTIONS[3].id);
        $('#psF01').updateChosenArr({
            item: psF01Selections
        });
        $('#psF02').updateChosenArr({
            item: psF02Selections
        });
        $('#psF03').updateChosenArr({
            item: psF03Selections
        });
        $('#psF04').updateChosenArr({
            item: psF04Selections
        });
        $('#psF05').updateChosenArr({
            item: psF05Selections
        });
        $('#psF06').updateChosenArr({
            item: psF06Selections
        });
        $('#psF07').updateChosenArr({
            item: psF10Selections
        });
        $('#psF08').updateChosenArr({
            item: psF08Selections
        });
        $('#psF09').updateChosenArr({
            item: psF09Selections
        });
        $('#psF10').updateChosenArr({
            item: psF10Selections
        });
        $('#psF11').updateChosenArr({
            item: psF11Selections
        });
        $('#tradeMarketF01').updateChosenArr({
            item: tradeMarket02345Selections
        });
        // modify by wanshiwei ZGZGST-2586 基金tab交易场所枚举值为上海证券交易所/深圳证券交易所 20210912 start
        $('#tradeMarketF02').updateChosenArr({
            // modify by etliu 20211206 start
            // TISHKS-509: 【交易管理】【交易管理】【指令汇总】基金tab页，交易场所下拉框枚举值错误
            // item: tradeMarket12Selections
            item: tradeMarket124Selections
            // modify by etliu 20211206 end
        });
        // modify by wanshiwei ZGZGST-2586 基金tab交易场所枚举值为上海证券交易所/深圳证券交易所 20210912 end
        $('#tradeMarketF07').updateChosenArr({
            item: tradeMarket012Selections
        });
        $('#tradeMarketF11').updateChosenArr({
            item: tradeMarket12Selections
        });
        $('#dealModeF11').updateChosenArr({
            item: dealMode0123Selections
        });
    }

    /**
     * 自动刷新开关控制
     * */
    $("#refreshSwitch").change(function () {
        if($("#refreshSwitch").is(':checked')){
            var refreshCircle = parseInt($('#refreshCircle option:selected') .val());
            clearInterval(refresh);
            refresh = setInterval(function(){
                $('#TrdCommandsCollection001_btn_search').trigger("click")
                }, refreshCircle); 
        }else{
            clearInterval(refresh);
        }
    });

    /**
     * 自动刷新周期控制
     * */
    $("#refreshCircle").change(function () {
        if($("#refreshSwitch").is(':checked')){
            var refreshCircle = parseInt($('#refreshCircle option:selected') .val());
            clearInterval(refresh);
            refresh = setInterval(function(){
                $('#TrdCommandsCollection001_btn_search').trigger("click")
                }, refreshCircle); 
        }else{
            clearInterval(refresh);
        }
    });
    
    /**
     * 导出事件
     */
    var exportExcel = function () {
        var paramObj = getPageParams(assetType);
        Joyin.exportExcel('export', paramObj);
    };

    /**
     * 债券数据表格
     */
    var entrustTableF01 =$("#entrustTableF01").joyintable({
        checkbox: false,
        contextmenu: false,
        rowno: true,
        page: false,
        searchOnInit: true,
        height:150,
        cols:[
            {field:'entrustId', width:'13%', title: 'TrdBondInvestTrade001.entrustId',sortable:false}
            ,{field:'vdate', visible:false}
            ,{field:'mdateStr', width:'10%', title: 'TrdCommandReceive001.mdate', type: 'date', sortable:false}
            ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId',sortable:false}
            ,{field:'invAimStr', width:'10%', title: 'TrdBondInvestTrade002.invAim',sortable:false}
            ,{field:'priceModelStr',  width:'10%', title: 'TrdCommandsCollection002.priceMode',sortable:false}
            ,{field:'price', width:'10%', title: 'TrdBondInvestTrade001.unitCprice', type: 'number', digit: 4, sortable:false}
            ,{field:'fpriceAmt', width:'10%', title: '全价', type: 'number', digit: 4 ,sortable:false}
            ,{field:'matureYield', width:'10%', title: 'TrdBondInvestTrade002.yield', type: 'number', digit: 4, sortable:false}
            ,{field:'entrustAmt', width: 200, title: 'TrdBondToStock001.entrustAmt', type: 'number', sortable:false}
            ,{field:'finishedAmt', width: 200, title: 'TrdCommandsCollection002.finishedAmtF01', type: 'number', sortable:false}
            ,{field:'unfinishedAmt', width: 200, title: 'TrdCommandsCollection002.unfinishedAmtF01', type: 'number', sortable:false}
            ,{field:'lowestDealNum', width:'10%', title: '最低成交额', sortable:false}
            ,{field:'secAcctName', width:'10%', title: 'TrdStock002.secAcctId',sortable:false}
            ,{field:'invManagerId', width:'10%', title: 'TrdCommandDispatcher001.invManagerId',sortable:false}
            ,{field:'files', width:'10%', title: 'TrdFundInvestTrade002.file',sortable:false,
                render : function(data, type, row, meta) {
                    var htmlStr = "";
                    for(var i in row.files){
                        htmlStr += "<a style='cursor: pointer;' href='" + basePath + "framework/download/" + row.files[i].fileNo + "'>" + row.files[i].fileName + "</a>"
                    }
                    return htmlStr;
                }
            }
            ,{field:'operation', title: 'SysCommon.operation', type: 'action', width: 20, buttons: [{
                    title: '交易录单',
                    clas: 'transaction',
                    text: '<a class="trans">交易录单</a>'
                }]//操作：交易录单
            }
        ]
    });
    
    /**
     * 可转债申购数据表格
     */
    var entrustTableCon1;
    
    /**
     * 可转债转股数据表格
     */
    var entrustTableCon2;

    /**
     * 基金数据表格
     */
    var entrustTableF02;
    
    /**
     * 计划类利率型委托明细数据表格
     */
    var entrustTableF03;
    
    /**
     * 计划类净值型委托明细数据表格
     */
    var entrustTableF04;
    
    /**
     * 存款类委托明细数据表格
     */
    var entrustTableF05;
    
    /**
     * 项目类委托明细数据表格
     */
    var entrustTableF06;


    /**
     * 质押式回购数据表格
     */
    var entrustTableF07;
    
    /**
     * 拆借类委托明细数据表格
     */
    var entrustTableF08;
    
    /**
     * 计划类货币型委托明细数据表格
     */
    var entrustTableF09;

    /**
     * 买断式回购数据表格
     */
    var entrustTableF10;

    /**
     * 股票数据表格
     */
    var entrustTableF11;
    
    /**
     * 债券一级投标数据表格
     */
    var entrustTableF17;

    /**
     * 委托质押券详情列表
     */
    var pledgeListTable =$("#pledgeListTable").joyintable({
        checkbox: false,
        contextmenu: false,
        rowno: true,
        page: false,
        searchOnInit: true,
        cols:[
            {field:'finprodId', width:'10%', title: 'TrdFundInvestTrade001.finprodId',sortable:false}
            ,{field:'finprodName', title: 'TrdFundInvestTrade001.finprodName',sortable:false}
            ,{field:'type6Str', width:'14%', title: 'TrdCommandsCollection002.type6', sortable:false}
            ,{field:'registOrgStr', width:'20%', title: 'TrdBondInvestTrade001.registOrg', sortable:false}
            ,{field:'portfolioId', width:'10%', title: 'TrdCommandReceive001.portfolioId', sortable:false}
            ,{field:'portfolioName', width:'14%', title: 'TrdCommandReceive001.portfolioName', sortable:false}
            ,{field:'pledgeAmt', width: 200, title: 'TrdCommandsCollection002.pledgeAmt', type: 'number', sortable:false}
            ,{field:'pledgeRatio', width: 150, title: 'TrdCommandsCollection002.pledgeRatio', type: 'number', sortable:false}
        ]
    });
    
    /**
     * 绑定债券委托明细交易录单事件
     */
    entrustTableF01.bind('click', '.transaction', function(data, index) {
        // 平台日期小于委托开始日报错
        if (platDate < data.vdateStr) {
            Joyin.error(JoyinUtil.getMessage("TRD_E_0003"));
            return;
        }
        Joyin.ajaxJson(basePath + 'invest/trade/investtrade/commandreceive/TrdCommandReceive001/check',
                {
                    entrustStatus: ENTRUST_STATUS_03,
                    operateType: 'trdTrade',
                    entrustId: data.entrustId,
                    finprodType2 : PINAN_ASSET_TYPE_F01
                }, function (result) {
                    Joyin.dialog(JoyinUtil.getLabel("TrdBondInvestTrade002"),
                            basePath + "invest/trade/bond/TrdBondInvestTrade002/init",
                            "99%", "99%", data);
        });
    });
    
    /**
     * 交易录单
     */
    var transactionSheet = function(){
        var tableData=null;
        if(assetType == PINAN_ASSET_TYPE_F10){//买断式回购
            if (tableF10 != null && tableF10 != '') {
                tableData = tableF10.getAllCheckedRows();
            }
        }else if(assetType == PINAN_ASSET_TYPE_F01){
            if (tableF01 != null && tableF01 != '') {
                tableData = tableF01.getAllCheckedRows();
            }
        }else if(assetType == PINAN_ASSET_TYPE_F05){//存款
            if (tableF05 != null && tableF05 != '') {
                var tableData = tableF05.getAllCheckedRows();
            }
        }else if(assetType == PINAN_ASSET_TYPE_F06){///项目类资产
        	if(tableF06 != null && tableF06 != ''){
        		tableData = tableF06.getAllCheckedRows();
        	}
        }else if(assetType == PINAN_ASSET_TYPE_F09){//计划类货币型
        	if(tableF09 != null && tableF09 != ''){
        		tableData = tableF09.getAllCheckedRows();
        	}
        }else if(assetType == PINAN_ASSET_TYPE_F07){//存款
            if (tableF07 != null && tableF07 != '') {
                var tableData = tableF07.getAllCheckedRows();
            }
        }else if(assetType == PINAN_ASSET_TYPE_F04){//计划类净值型
            if (tableF04 != null && tableF04 != '') {
                var tableData = tableF04.getAllCheckedRows();
            }
        }else if(assetType == PINAN_ASSET_TYPE_F03){//计划类利率型
            if (tableF03 != null && tableF03 != '') {
                var tableData = tableF03.getAllCheckedRows();
            }
        }
        
        if (!tableData || tableData.length == 0) {
            Joyin.error(JoyinUtil
                .getMessage("BUSS_COM_E_0026"));
            return;
        }
        
        // 列表中勾选对象1条以上时
        if (tableData.length > 1) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
            return;
        }
        
        var param = null;
        var label = "";
        var pagePath = "";
        Joyin.ajax(JoyinUtil.getRealPath("/invest/trade/investtrade/commandscollection/TrdCommandsCollection002/initData"),
          {
            assetType: assetType,
            entity: JSON.stringify(tableData[0])
          }, function (result) {
              if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F10){//买断式回购
                  var entrustIdList=[];
                  var portfolioIdList=[];
                  var entrustAmtList=[];
                  
                  var entrustList = result.entrustList;
                  var endDate=new Date(entrustList[0].mdate).format("yyyy-MM-dd");
                  
                  for(var i=0;i < entrustList.length;i++){
                      var endDate2=new Date(entrustList[i].mdate).format("yyyy-MM-dd");
                      if(endDate2<endDate){
                          endDate=endDate2;
                      }
                      if(i==result.entrustList.length-1){
                          entrustIdList = entrustIdList + entrustList[i].entrustId;
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId;
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt;
                      }else{
                          entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt+",";
                      }
                  }
                  
                  param = {
                          portfolioId: portfolioIdList,
                          entrustAmt: entrustAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt,
                          price:(typeof result.entrustList[0].price == 'undefined'?"":result.entrustList[0].price),
                          endDate:endDate
                      };
                  
                  label = 'TrdOutRightRepoTrade003.new';
                  pagePath = basePath + "invest/trade/buyoutrepo/TrdOutRightRepoTrade003/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F05){//存款
                  var entrustIdList=[];
                  var portfolioIdList=[];
                  var unfinishedAmtList=[];
                  
                  var entrustList = result.entrustList;
                  var tableData_F05 = [];
                  tableData_F05 = tableF05.getAllCheckedRows();
                  for(var i=0;i < entrustList.length;i++){
                      if(i==result.entrustList.length-1){
                          entrustIdList = entrustIdList + entrustList[i].entrustId;
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId;
                          unfinishedAmtList = unfinishedAmtList + entrustList[i].unfinishedAmt;
                      }else{
                          entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          unfinishedAmtList = unfinishedAmtList + entrustList[i].unfinishedAmt+",";
                      }
                  }
                  
                  param = {
                          portfolioId: portfolioIdList,
                          unfinishedAmt: unfinishedAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt,
                          finprodMarketId: tableData_F05[0].finprodMarketId,
                          ps:tableData_F05[0].ps
                      };
                  
                  label = 'TrdDepositInvestTrade003';
                  pagePath = basePath + "invest/trade/trddepositinvesttrade/TrdDepositInvestTrade003/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }
              else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F01){//债券
                  var entrustIdList=[];
                  var portfolioIdList=[];
                  var entrustAmtList=[];
                  
                  var entrustList = result.entrustList;
                  for(var i=0;i < entrustList.length;i++){
                      if(i==result.entrustList.length-1){
                          entrustIdList = entrustIdList + entrustList[i].entrustId;
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId;
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt;
                      }else{
                          entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt+",";
                      }
                  }
                  
                  param = {
                          portfolioId: portfolioIdList,
                          entrustAmt: entrustAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt
                      };
                  
                  label = 'TrdBondInvestTrade003';
                  pagePath = basePath + "invest/trade/bond/TrdBondInvestTrade003/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F09){//计划类货币型
            	  var entrustIdList=[];
                  var portfolioIdList=[];
                  var entrustAmtList=[];
                  
                  var entrustList = result.entrustList;
                  for(var i=0;i < entrustList.length;i++){
                      if(i==result.entrustList.length-1){
                          entrustIdList = entrustIdList + entrustList[i].entrustId;
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId;
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt;
                      }else{
                          entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt+",";
                      }
                  }
                  
                  param = {
                          portfolioId: portfolioIdList,
                          entrustAmt: entrustAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt
                      };
                  
                  label = 'TrdPlanCcyInvestTrade004';
                  pagePath = basePath + "invest/trade/planccy/TrdPlanCcyInvestTrade004/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F06){//项目类
            	  var entrustIdList=[];
                  var portfolioIdList=[];
                  var entrustAmtList=[];
                  
                  var entrustList = result.entrustList;
                  for(var i=0;i < entrustList.length;i++){
                      if(i==result.entrustList.length-1){
                          entrustIdList = entrustIdList + entrustList[i].entrustId;
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId;
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt;
                      }else{
                          entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt+",";
                      }
                  }
                  
                  param = {
                          portfolioId: portfolioIdList,
                          entrustAmt: entrustAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt
                      };
                  
                  label = 'TrdProjectInvestTrade003';
                  pagePath = basePath + "invest/trade/project/TrdProjectInvestTrade003/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }
              else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F07){//质押式回购
                  var entrustIdList=[];
                  
                  var entrustList = result.entrustList;
                  if (entrustList && entrustList.length > 0) {
                      for(var i=0;i < entrustList.length;i++){
                          if(i==result.entrustList.length-1){
                              entrustIdList = entrustIdList + entrustList[i].entrustId;
                          }else{
                              entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          }
                      }

                      param = {
                              entrustId:entrustIdList,
                                ps:entrustList[0].ps
                          };

                      label = 'TrdRepoTrade003.new';
                      pagePath = basePath + "invest/trade/trdrepotrade/TrdRepoTrade003/init";
                      Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
                  }
              }
              else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F04){//计划类净值型
            	  var entrustIdList=[];
                  var portfolioIdList=[];
                  var entrustAmtList=[];
                  
                  var entrustList = result.entrustList;
                  for(var i=0;i < entrustList.length;i++){
                      if(i==result.entrustList.length-1){
                          entrustIdList = entrustIdList + entrustList[i].entrustId;
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId;
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt;
                      }else{
                          entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          entrustAmtList = entrustAmtList + entrustList[i].entrustAmt+",";
                      }
                  }
                  
                  param = {
                          portfolioId: portfolioIdList,
                          entrustAmt: entrustAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt
                      };
                  
                  label = 'TrdPlanNetInvestTrade004.title';
                  pagePath = basePath + "invest/trade/plannet/TrdPlanNetInvestTrade004/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }
              else if(result&&result.entrustList&&assetType == PINAN_ASSET_TYPE_F03){//计划类利率型
            	  var entrustIdList=[];
                  var portfolioIdList=[];
                  var unfinishedAmtList=[];
                  
                  var entrustList = result.entrustList;
                  
                  var tableData_F03 = [];
                  tableData_F03 = tableF03.getAllCheckedRows();
                  
                  for(var i=0;i < entrustList.length;i++){
                      if(i==result.entrustList.length-1){
                    	  entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          unfinishedAmtList = unfinishedAmtList + entrustList[i].unfinishedAmt;
                      }else{
                    	  entrustIdList = entrustIdList + entrustList[i].entrustId+",";
                          portfolioIdList = portfolioIdList + entrustList[i].portfolioId+",";
                          unfinishedAmtList = unfinishedAmtList + entrustList[i].unfinishedAmt+",";
                      }
                  }
                  
                  param = {                       
                          portfolioId: portfolioIdList,
                          unfinishedAmt: unfinishedAmtList,
                          entrustId:entrustIdList,
                          entrustAmtTotle:result.entrustAmt,
                          finprodMarketId: tableData_F03[0].finprodMarketId,
                          ps:tableData_F03[0].ps
                      };
                  
                  label = 'TrdPlanRateInvestTrade003';
                  pagePath = basePath + "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade003/init";
                  Joyin.dialog(JoyinUtil.getLabel(label),pagePath, "99%", "99%",param);
              }
            }
        );
        
    }
    
    /**
     * 股票模块：交易场所-上市板 选择联动
     * （这里试过在数据字典关联表中配置了，但是没用，只能这样干了，如果你有更好的方法请告诉我，谢谢！！）
     * */
    $("#tradeMarketF11").change(function () {
        var BOARD_SELECTIONS = JSON.parse(BOARD);
        var boardSelections = [];
        if($("#tradeMarketF11").val() == '03'){
            boardSelections.push(BOARD_SELECTIONS[0].id);
            boardSelections.push(BOARD_SELECTIONS[2].id);
        }
        else if($("#tradeMarketF11").val() == '04') {
            boardSelections.push(BOARD_SELECTIONS[0].id);
            boardSelections.push(BOARD_SELECTIONS[1].id);
            boardSelections.push(BOARD_SELECTIONS[3].id);
        }
        else{
            boardSelections.push(BOARD_SELECTIONS[0].id);
            boardSelections.push(BOARD_SELECTIONS[1].id);
            boardSelections.push(BOARD_SELECTIONS[2].id);
            boardSelections.push(BOARD_SELECTIONS[3].id);
        }
        $('#boardF11').updateChosenArr({
            item: boardSelections
        });
    });

    /**
     * 画面加载完成
     */
    $(function () {
        init();
        
        /**
         * 重置按钮边上的下拉箭头不能自动隐藏，这里就只好手动隐藏了
         */
        $('#TrdCommandsCollection001_btn_reset').next().hide();
        
        /**
         * 查询事件
         */
        $("#TrdCommandsCollection001_btn_search").click(function() {
            switch(assetType){
            case PINAN_ASSET_TYPE_F01:
                if(tableF01.table){
                    tableF01.reload();
                }
                else {
                    tableF01.load();
                }
                entrustTableF01.reload({data:[]});
                break;
            case "Con1":
                if(tableCon1.table){
                    tableCon1.reload();
                }
                else {
                    tableCon1.load();
                }
                entrustTableCon1.reload({data:[]});
                break;
            case "Con2":
                if(tableCon2.table){
                    tableCon2.reload();
                }
                else {
                    tableCon2.load();
                }
                entrustTableCon2.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F02:
                if(tableF02.table){
                    tableF02.reload(); 
                }
                else {
                    tableF02.load();
                }
                entrustTableF02.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F03:
                if(tableF03.table){
                    tableF03.reload(); 
                }
                else {
                    tableF03.load();
                }
                entrustTableF03.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F04:
                if(tableF04.table){
                    tableF04.reload(); 
                }
                else {
                    tableF04.load();
                }
                entrustTableF04.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F05:
                if(tableF05.table){
                    tableF05.reload(); 
                }
                else {
                    tableF05.load();
                }
                entrustTableF05.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F06:
                if(tableF06.table){
                    tableF06.reload(); 
                }
                else {
                    tableF06.load();
                }
                entrustTableF06.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F07:
                if(tableF07.table){
                    tableF07.reload(); 
                }
                else {
                    tableF07.load();
                }
                entrustTableF07.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F08:
                if(tableF08.table){
                    tableF08.reload(); 
                }
                else {
                    tableF08.load();
                }
                entrustTableF08.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F09:
                if(tableF09.table){
                    tableF09.reload(); 
                }
                else {
                    tableF09.load();
                }
                entrustTableF09.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F10:
                if(tableF10.table){
                    tableF10.reload(); 
                }
                else {
                    tableF10.load();
                }
                entrustTableF10.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F11:
                if(tableF11.table){
                    tableF11.reload(); 
                }
                else {
                    tableF11.load();
                }
                entrustTableF11.reload({data:[]});
                break;
            case PINAN_ASSET_TYPE_F17:
                if(tableF17.table){
                    tableF17.reload(); 
                }
                else {
                    tableF17.load();
                }
                entrustTableF17.reload({data:[]});
                break;
            }
        });
        
        /**
         * 导出事件
         */
        $('#TrdCommandsCollection001_btn_export').click(function () {
            exportExcel();
        });

        /**
         * 重置事件
         */
        $('#TrdCommandsCollection001_btn_clickReset').click(function () {
            $('#TrdCommandsCollection001_btn_reset').trigger("click");
        });
        
        /**
         * 交易录单
         */
        $('#TrdCommandsCollection001_btn_transaction').click(function() {
            transactionSheet();
        });
        
        /**
         * 查看明细
         */
        $('#TrdCommandsCollection001_btn_detail').click(function () {
            var selArr = [];
            switch(assetType){
            //债券
            case PINAN_ASSET_TYPE_F01:
                selArr = tableF01.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList',
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF01.reload({data: result});
                });
                break;
            //可转债申购
            case "Con1":
                selArr = tableCon1.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList',
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableCon1.reload({data: result});
                });
                break;
            //可转债转股
            case "Con2":
                selArr = tableCon2.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList',
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableCon2.reload({data: result});
                });
                break;
            //基金
            case PINAN_ASSET_TYPE_F02:
                selArr = tableF02.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF02.reload({data: result});
                });
                break;
            //计划类利率型
            case PINAN_ASSET_TYPE_F03:
                selArr = tableF03.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF03.reload({data: result});
                });
                break;
            //计划类净值型
            case PINAN_ASSET_TYPE_F04:
                selArr = tableF04.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF04.reload({data: result});
                });
                break;
            //存款
            case PINAN_ASSET_TYPE_F05:
                selArr = tableF05.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF05.reload({data: result});
                });
                break;
            //项目类
            case PINAN_ASSET_TYPE_F06:
                selArr = tableF06.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF06.reload({data: result});
                });
                break;
            //质押式回购
            case PINAN_ASSET_TYPE_F07:
                selArr = tableF07.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF07.reload({data: result});
                });
                break;
            //拆借
            case PINAN_ASSET_TYPE_F08:
                selArr = tableF08.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF08.reload({data: result});
                });
                break;
            //计划类货币型
            case PINAN_ASSET_TYPE_F09:
                selArr = tableF09.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF09.reload({data: result});
                });
                break;
            //买断式回购
            case PINAN_ASSET_TYPE_F10:
                selArr = tableF10.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF10.reload({data: result});
                });
                break;
            //股票
            case PINAN_ASSET_TYPE_F11:
                selArr = tableF11.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF11.reload({data: result});
                });
                break;
            //债券一级投标
            case PINAN_ASSET_TYPE_F17:
                selArr = tableF17.getAllCheckedRows();
                // 列表中未勾选
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                    return;
                }
                // 列表中勾选对象1条以上时
                if (selArr.length > 1) {
                    Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                    return;
                }
                Joyin.ajax(basePath + 'invest/trade/investtrade/commandscollection/TrdCommandsCollection002/selectEntrustList', 
                        {assetType: assetType, entity: JSON.stringify(selArr[0])}, function(result) {
                    entrustTableF17.reload({data: result});
                });
                break;
            }
        });
    });
}());