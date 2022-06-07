(function () {

    var getPageParams = function() {
        var object = $.serializeToObject('#searchForm', true);

        if(object.applyNo) {
            object.applyNo = object.applyNo.join(",")
        }

        if(object.tradeDate) {
            object.tradeDateBegin = $("#tradeDate").startDate();
            object.tradeDateEnd = $("#tradeDate").endDate();
        }
        return object;
    };

    var getDealPageParams = function() {
        var object = $.serializeToObject('#searchForm_deal', true);

        if(object.portfolioId) {
            object.portfolioId = object.portfolioId.join(",")
        }

        if(object.finprodType2) {
            object.finprodType2 = object.finprodType2.join(",")
        }

        if(object.tradeDate) {
            object.tradeDateBegin = $("#deaalTradeDate").startDate();
            object.tradeDateEnd = $("#deaalTradeDate").endDate();
        }
        return object;
    };

    var getTransMatchInfoPageParams = function () {
        var object = $.serializeToObject('#searchForm_TransMacthInfo', true);
        if(object.tradeDate) {
            object.tradeDateBegin = $("#trdMatchInfo_tradeDate").startDate();
            object.tradeDateEnd = $("#trdMatchInfo_tradeDate").endDate();
        }

        // if(object.dealId) {
        //     object.dealId = object.dealId.join(",")
        // }

        if(object.portfolioId) {
            object.portfolioId = object.portfolioId.join(",")
        }
        return object;
    }

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getPageParams();
            }
        },
        searchOnInit: true,
        cols:[
            {field:'matchStatusStr', sortField:'matchStatus',width:200, title: 'ExchangeTransMatch001.matchStatus'},
            {field:'matchProgressStr', sortField:'matchProgress',width:200, title: 'ExchangeTransMatch001.matchProgress'},
            {field:'tradeMarketStr', sortField:'tradeMarket',width:200, title: 'ExchangeTransMatch001.tradeMarket'},
            {field:'applyTradeId', sortField:'applyTradeId',width:200, title: 'ExchangeTransMatch001.applyNo'},
            {field:'secAcctName', sortField:'secAcctId',width:200, title: 'ExchangeTransMatch001.secAcctNo'},
            {field:'finprodType2Str', sortField:'finprodType2',width:200, title: 'ExchangeTransMatch001.finprodType2'},
            {field:'finprodMarketId', sortField:'finprodMarketId',width:200, title: 'ExchangeTransMatch001.finprodId'},
            {field:'finprodAbbr', sortField:'finprodAbbr',width:200, title: 'ExchangeTransMatch001.finprodAbbr'},
            {field:'seatNumber', sortField:'seatNumber',width:200, title: 'ExchangeTransMatch001.seatNo'},
            // {field:'tradePlatStr', sortField:'tradePlat',width:200, title: 'ExchangeTransMatch001.tradePlat', visible:false},
            {field:'psStr', sortField:'ps',width:200, title: 'ExchangeTransMatch001.ps'},
            {field:'remainAmount', sortField:'remainAmount',width:200, title: 'ExchangeTransMatch001.remainAmount',type: 'money'},
            {field:'remainNum', sortField:'remainNum',width:200, title: 'ExchangeTransMatch001.remainNum',type: 'number'},
            {field:'unitFprice', sortField:'unitFprice',width:200, title: 'ExchangeTransMatch001.dealPrice',type: 'number',digit: 4},
            {field:'dealDate', sortField:'dealDate',width:200, title: 'ExchangeTransMatch001.dealDate',type: 'date'},
            {field:'acctName', sortField:'acctNo',width:200, title: 'ExchangeTransMatch001.fundAccount'},

            {field:'matchStatus', visible:false},
            {field:'matchProgress', visible:false},
            {field:'finprodType2', visible:false},
            {field:'finprodId', visible:false},
            {field:'secAcctId', visible:false},
            {field:'acctNo', visible:false},
            {field:'ps', visible:false},
            {field:'unitInt', visible:false},
            {field:'unitCprice', visible:false},
            {field:'tradeMarket', visible:false},
            {field:'seqId', visible:false},
            {field:'settleDate', visible:false},
            {field:'tradeMarketMode', visible:false},
            // {field:'tradePlat', visible:false},
            {field:'shareAmt', visible:false},
            {field:'fpriceAmt', visible:false},
            {field:'repoType', visible:false},
            {field:'conPrice', visible:false},
            {field:'conAmt', visible:false},
            {field:'rFinMarketId', visible:false},
            {field:'rFinprodId', visible:false},
            {field:'busiType', visible:false}
        ]

    });

    /**
     * 定义数据表格
     */
    var entrustDataTable=$("#entrustDataTable").joyintable({
        ajax: {
            url: 'findEntrustPage',
            type: 'POST',
            data: function() {
                return getDealPageParams();
            }
        },
        searchOnInit: true,
        dblclick:function(table, data) {
            data.operateType = "dblclick";
            data.pageModel=PAGE_MODEL_REFERENCE;
            //交易所（上交和深交）的指令
            var entrustType = data.entrustType;
            if(entrustType == ENTRUST_BUSS_TYPE_22 || entrustType == ENTRUST_BUSS_TYPE_21
                || entrustType == ENTRUST_BUSS_TYPE_20 || entrustType == ENTRUST_BUSS_TYPE_19
                || entrustType == ENTRUST_BUSS_TYPE_18 || entrustType == ENTRUST_BUSS_TYPE_17) {
                pagePath = "invest/entrust/etragrereptrade/EtrAgreRepTrade001/init";
                Joyin.dialog(JoyinUtil.getLabel('EtrAgreRepTrade001.detail'), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        entrustId: data.entrustId,
                    });//交易所协议回购指令详情
                return;
            }
            //债券申购进可转债页面
            if(data.assetType == F01 && data.ps == PS_T04 ){
                label='EtrConBond002.refrence';
                pagePath = '/invest/entrust/etrconbond/EtrConBond002/init'
            }else{
                label='EtrExchangeTrade001.detail';
                pagePath = "/invest/entrust/etrexchangetrade/EtrExchangeTrade001/init";
            }
            Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                "95%", "90%",data,
                function(){table.reload();});//交易所场内指令详情
            return;
        },
        cols:[
            // {field:'tradeTypeStr', sortField:'tradeType',width:200, title: 'ExchangeTransMatch001.entrustType'},
            {field:'portfolioId', sortField:'portfolioId',width:200, title: 'ExchangeTransMatch001.portfolioId'},
            {field:'portfolioName', sortField:'portfolioName',width:200, title: 'ExchangeTransMatch001.portfolioName'},
            {field:'finprodType2Str', sortField:'getFinprodType2Str',width:200, title: 'ExchangeTransMatch001.finprodType2'},
            {field:'finprodMarketId', sortField:'finprodMarketId',width:200, title: 'ExchangeTransMatch001.finprodId'},
            {field:'entrustDate', sortField:'entrustDate',width:200, title: 'ExchangeTransMatch001.entrustDate',type: 'date'},
            // {field:'tradeDate', sortField:'tradeDate',width:200, title: 'ExchangeTransMatch001.tradeDate',type: 'date'},
            {field:'psStr', sortField:'ps',width:200, title: 'ExchangeTransMatch001.ertPs'},
            {field:'tradeMarketStr', sortField:'tradeMarket',width:200, title: 'ExchangeTransMatch001.tradeMarket'},
            // {field:'tradePlatStr', sortField:'tradePlat',width:200, title: 'ExchangeTransMatch001.tradePlat', visible:false},
            {field:'entrustAmt', sortField:'entrustAmount',width:200, title: 'ExchangeTransMatch001.entrustAmount',type: 'number'},
            {field:'price', sortField:'entrustPrice',width:200, title: 'ExchangeTransMatch001.entrustPrice',type: 'number',digit: 4},
            {field:'entrustNum', sortField:'entrustNum',width:200, title: 'ExchangeTransMatch001.entrustNum',type: 'number'},
            {field:'secAcctName', sortField:'secAcctName',width:200, title: 'ExchangeTransMatch001.secAcctNo'},
            {field:'acctName', sortField:'acctName',width:200, title: 'ExchangeTransMatch001.fundAccount'},
            {field:'entrustId', sortField:'entrustId',width:200, title: 'ExchangeTransMatch001.bussCode'},
            {field:'tradeType', visible:false},
            {field:'finprodType2', visible:false},
            {field:'secAcctId', visible:false},
            {field:'acctNo', visible:false},
            {field:'ps', visible:false},
            {field:'tradeMarket', visible:false}
            // {field:'tradePlat', visible:false}
        ]

    });

    /**
     * 定义数据表格
     */
    var transMatchInfoTable=$("#exchageTransMatchInfo").joyintable({
        ajax: {
            url: 'findTransMatchInfoPage',
            type: 'POST',
            data: function() {
                return getTransMatchInfoPageParams();
            }
        },
        cols:[
            {field:'seqId',visible:false},
            {field:'matchTypeStr', sortField:'matchType',width:200, title: 'ExchangeTransMatch001.matchType'},
            {field:'busiCode', sortField:'busiCode',width:200, title: 'ExchangeTransMatch001.busiCode'},
            {field:'tradeMarketStr', sortField:'tradeMarket',width:200, title: 'ExchangeTransMatch001.tradeMarket'},
            {field:'applyTradeId', sortField:'applyTradeId',width:200, title: 'ExchangeTransMatch001.applyNo'},
            {field:'portfolioId', sortField:'portfolioId',width:200, title: 'ExchangeTransMatch001.portfolioId'},
            {field:'portfolioName', sortField:'portfolioName',width:200, title: 'ExchangeTransMatch001.portfolioName'},
            {field:'finprodType2Str', sortField:'finprodType2',width:200, title: 'ExchangeTransMatch001.finprodType2'},
            {field:'finprodMarketId', sortField:'finprodMarketId',width:200, title: 'ExchangeTransMatch001.finprodId'},
            {field:'psStr', sortField:'ps',width:200, title: 'ExchangeTransMatch001.ps'},
            {field:'tradeAmt', sortField:'tradeAmt',width:200, title: 'ExchangeTransMatch001.tradeAmout',type: 'number'},
            {field:'tradeNum', sortField:'tradeNum',width:200, title: 'ExchangeTransMatch001.tradeNum',type: 'number'},
            {field:'tradePrice', sortField:'tradePrice',width:200, title: 'ExchangeTransMatch001.tradePrice',type: 'number',digit: 4},
            {field:'tradeDate', sortField:'tradeDate',width:200, title: 'ExchangeTransMatch001.tradeDate',type: 'date'},
            {field:'ccyStr', sortField:'ccy',width:200, title: 'ExchangeTransMatch001.ccy'},
            {field:'matchType', visible:false},
            {field:'tradeMarket', visible:false},
            {field:'finprodType2', visible:false},
            {field:'ps', visible:false},
            {field:'ccy', visible:false},
            {field:'oprtation', title:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', type:'action', fixed: 'right',
                buttons:[{text:'&#xe642;',clas:'edit',title:'查看'}] }
        ]

    });

    /**
     * 绑定编辑事件
     */
    transMatchInfoTable.bind('click', '.edit', function(data, index) {
        Joyin.dialog(JoyinUtil.getLabel("匹配明细查看"),
            basePath + "invest/trade/exchangetransmatch/ExchangeTransMatch002/init",
            "98%", "95%", {seqNo:data.seqId},function(){
                transMatchInfoTable.reload();
            });
    });


    /**
     * 导出事件
     */

    var exportExcelQueDing = function() {
        var paramObj = getPageParams();
        Joyin.exportExcel('exportExcelQueDing', paramObj);
    };

    /**
     * 画面加载完成
     */
    $(function() {
        // TISHKS-828:修改查询重置按钮隐藏方式,占据页面空间 modify by fuyongsheng 20220505 start
        // $('#ExchangeTransMatch001_btn_reset').hide();
        // $('#ExchangeTransMatch001_btn_search').hide();
        document.getElementById("ExchangeTransMatch001_btn_reset").style.visibility = "hidden";
        document.getElementById("ExchangeTransMatch001_btn_search").style.visibility = "hidden";
        // TISHKS-828:修改查询重置按钮隐藏方式,占据页面空间 modify by fuyongsheng 20220505 end

        $('#ExchangeTransMatch001_btn_export').click(function() {
            exportExcelQueDing();
        });

        //成交数据查询
        $('#ExchangeTransMatch001_deal_btn_search').click(function () {
            table.table?table.reload():table.load();
        });

        //委托交易数据查询
        $('#ExchangeTransMatch001_entrust_btn_search').click(function () {
            entrustDataTable.table?entrustDataTable.reload():entrustDataTable.load();
        });

        //匹配信息查询
        $('#ExchangeTransMatch001_trdMatchInfo_btn_search').click(function () {
            transMatchInfoTable.table?transMatchInfoTable.reload():transMatchInfoTable.load();
        });

        //成交数据-匹配查询
        $('#ExchangeTransMatch001_deal_btn_match_search').click(function () {
            if(table) {
                var selArr = table.getAllCheckedRows();
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("TRDFUND_E_XXT_001"));
                    return;
                }else if(!selArr || selArr.length > 1){
                    Joyin.error(JoyinUtil.getMessage("REPORT_COMMON_E_0012"));
                    return;
                }

                var item = selArr[0];
                var object = getDealPageParams();
                object.finprodId = item.finprodMarketId;
                object.finprodType1 = item.finprodType2;
                object.ps = item.ps;
                // object.tradePlat = item.tradePlat;
                object.tradeMarket = item.tradeMarket;
                object.dealDate = new Date(item.dealDate).Format("yyyy-MM-dd");
                object.secAccount = item.secAcctId;
                object.repoType = item.repoType;
                object.remainNum = item.remainNum;
                // GNS-847:增加期限品种条件 modify by fuyongsheng 20220215 start
                object.termType = item.termType;
                // GNS-847:增加期限品种条件 modify by fuyongsheng 20220215 end
                if(entrustDataTable.table) {
                    entrustDataTable.clear();
                }
                Joyin.ajaxJson(JoyinUtil.getRealPath('invest/trade/exchangetransmatch/ExchangeTransMatch001/matchSearch'),object,function (result) {
                    if(result) {
                        if(result && result.root.length >0){
                            if(entrustDataTable.table) {

                                entrustDataTable.addRow(result.root);
                            }

                        }
                    }
                });
            }

        });

        //成交数据-成交匹配
        $('#ExchangeTransMatch001_deal_btn_transaction_macth').click(function () {

            var object = {};

            if(table) {
                var dealArr = table.getAllCheckedRows();
                if (!dealArr || dealArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("TRDFUND_E_XXT_001"));
                    return;
                }else if(!dealArr || dealArr.length > 1){
                    Joyin.error(JoyinUtil.getMessage("REPORT_COMMON_E_0012"));
                    return;
                }
                object = dealArr[0];
            }

            if(entrustDataTable) {
                var entrustArr = entrustDataTable.getAllCheckedRows();
                if (!entrustArr || entrustArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("TRDFUND_E_XXT_001"));
                    return;
                }
                object.entrustList = entrustArr;
            }

            object.elementId = "transaction_macth";
            Joyin.ajaxJson(basePath + 'invest/trade/exchangetransmatch/ExchangeTransMatch001/check', object, function(result) {
                Joyin.confirm("请确认是否进行成交匹配?", function() {
                    Joyin.ajaxJson(JoyinUtil.getRealPath('invest/trade/exchangetransmatch/ExchangeTransMatch001/transactionMacth'),object,function (result) {
                        table.table?table.reload():table.load();
                        entrustDataTable.table?entrustDataTable.reload():entrustDataTable.load();
                    });
                });
            });
        });

        //成交数据-成交录单
        $('#ExchangeTransMatch001_deal_btn_transaction').click(function () {
            var object = {};
            if(table) {
                var selArr = table.getAllCheckedRows();
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("TRDFUND_E_XXT_001"));
                    return;
                }else if(!selArr || selArr.length > 1){
                    Joyin.error(JoyinUtil.getMessage("REPORT_COMMON_E_0012"));
                    return;
                }

                var item = selArr[0];
                var checkObject = getDealPageParams();
                object.finprodId = item.finprodMarketId;
                object.finprodType1 = item.finprodType2;
                object.ps = item.ps;
                object.tradePlat = item.tradePlat;
                object.dealDate = new Date(item.dealDate).format('yyyy-MM-dd');
                object.secAccount = item.secAcctId;
                object.repoType = item.repoType;
                object.remainNum = item.remainNum;

                Joyin.ajaxJson(JoyinUtil.getRealPath('invest/trade/exchangetransmatch/ExchangeTransMatch001/matchSearch'),object,function (result) {
                    if(result) {
                        if(result && result.root.length > 0){
                            Joyin.error("存在可匹配信息，请先进行成交匹配！");
                            return;
                        }
                        else {

                            object = item;
                            object.elementId = "transaction_record";
                            Joyin.ajaxJson(basePath + 'invest/trade/exchangetransmatch/ExchangeTransMatch001/check', object, function(result) {
                                Joyin.confirm("请确认是否进行交易录单?", function() {
                                    Joyin.ajaxJson(JoyinUtil.getRealPath('invest/trade/exchangetransmatch/ExchangeTransMatch001/genTransactionRecord'),object,function (result) {
                                        result=JSON.stringify(result);
                                        if(result) {
                                            var lable = "";
                                            var url = "";
                                            //债券
                                            if(object.finprodType2 == FINPROD_TYPE_TG_F01) {
                                                //买入，卖出，行权回售，配债
                                                if(object.ps == PS_T01 || object.ps == PS_T02 || object.ps == PS_T03 || object.ps == PS_T97) {
                                                    lable = "TrdBondInvestTrade002";
                                                    url = "invest/trade/bond/TrdBondInvestTrade002/init";
                                                }
                                                //可转债申购
                                                else if(object.ps == PS_T04){
                                                    lable = "TrdConBond002";
                                                    url = "invest/trade/conbond/TrdConBond002/init";
                                                }
                                                //可转债申购确认
                                                else if(object.ps == PS_T05) {
                                                    lable = "TrdConBond002";
                                                    url = "invest/trade/conbond/TrdConBond003/init";
                                                }
                                                //债转股
                                                else if(object.ps == PS_T49) {
                                                    lable = "TrdBondToStock002.new";
                                                    url = "invest/trade/bondtostock/TrdBondToStock002/init";
                                                }
                                            }
                                            //质押式回购
                                            else if(object.finprodType2 == FINPROD_TYPE_TG_F02 || object.finprodType2 == FINPROD_TYPE_TG_F03) {
                                                lable = "TrdRepoTrade002.new";
                                                url = "invest/trade/trdrepotrade/TrdRepoTrade002/init";
                                            }
                                            //基金
                                            else if(object.finprodType2 == FINPROD_TYPE_TG_F06) {
                                                lable = "TrdFundInvestTrade002";
                                                url = "invest/trade/fund/TrdFundInvestTrade002/init";
                                            }
                                            //股票
                                            else if(object.finprodType2 == FINPROD_TYPE_TG_F10) {
                                                if(object.ps == PS_T01 || object.ps == PS_T02 || object.ps == PS_T46 || object.ps == PS_T47|| object.ps == PS_T48 ) {
                                                    lable = "TrdStock002";
                                                    url = "invest/trade/stockdeal/TrdStock002/init";
                                                }
                                            }

                                            Joyin.dialog(JoyinUtil.getLabel(lable), basePath
                                                + url, "99%", "99%", {
                                                pageModel : PAGE_MODEL_NEW,
                                                tranRecord : result
                                            }, function() {
                                                table.table?table.reload():table.load();
                                                entrustDataTable.table?entrustDataTable.reload():entrustDataTable.load();
                                            });
                                        }
                                    });
                                });
                            });
                        }
                    }
                });


            }
        });

        // //委托交易数据-匹配查询
        // $('#ExchangeTransMatch001_entrust_btn_match_search').click(function () {
        //
        // });
        //
        // //委托交易数据-成交匹配
        // $('#ExchangeTransMatch001_entrust_btn_transaction_macth').click(function () {
        //
        // });

        // //匹配查询-提交
        // $('#TrdMatchInfo_btn_submit').click(function () {
        // });

        //匹配查询-取消匹配
        $('#TrdMatchInfo_btn_quit').click(function () {
            var object = {};
            if(transMatchInfoTable) {
                var selArr = transMatchInfoTable.getAllCheckedRows();
                if (!selArr || selArr.length == 0) {
                    Joyin.error(JoyinUtil.getMessage("TRDFUND_E_XXT_001"));
                    return;
                }

                for(var i=0;i<selArr.length;i++) {
                    var item = selArr[i];
                    if(item.matchType == TRD_MATCH_TYPE_0) {
                        Joyin.error("自动匹配的记录不可以取消匹配！");
                        return;
                    }
                }
                object.selectItem = selArr;
                Joyin.confirm("请确认是否取消成交匹配?", function() {
                    Joyin.ajaxJson(JoyinUtil.getRealPath('invest/trade/exchangetransmatch/ExchangeTransMatch001/revokeTransactionMacth'),object,function (result) {
                        transMatchInfoTable.table?transMatchInfoTable.reload():transMatchInfoTable.load();
                    });
                });

            }

        });

        //成交重置按钮
        $('#ExchangeTransMatch001_deal_btn_clean').click(function () {
            $('#tradeMarket').val('').trigger('change');
            $('#tradePlat').val('').trigger('change');
            $('#ps').val('').trigger('change');
            $('#secAcctNo').val('').trigger('change');
            $('#applyNo').val('').trigger('change');
            $('#finprodId').val('').trigger('change');
            $('#tradeDate').val('').trigger('change');
            $('#matchStatus').val('').trigger('change');
            $('#tradeAmt1').val('').trigger('change');
            $('#tradeAmt2').val('').trigger('change');
        });

        //委托交易重置按钮
        $('#ExchangeTransMatch001_entrust_btn_clean').click(function () {
            $('#portfolioId').val('').trigger('change');
            $('#dealFinprodType2').val('').trigger('change');
            $('#dealFinprodId').val('').trigger('change');
            $('#dealPs').val('').trigger('change');
            $('#dealTradePlat').val('').trigger('change');
            $('#deaalTradeDate').val('').trigger('change');
        });

        //委托交易重置按钮
        $('#ExchangeTransMatch001_trdMatchInfo_btn_reset').click(function () {
            $('#trdMatchInfo_matchType').val('').trigger('change');
            $('#trdMatchInfo_dealId').val('').trigger('change');
            $('#trdMatchInfo_tradeMarket').val('').trigger('change');
            $('#trdMatchInfo_portfolioId').val('').trigger('change');
            $('#trdMatchInfo_busiCode').val('').trigger('change');
            $('#trdMatchInfo_secAcctNo').val('').trigger('change');
            $('#trdMatchInfo_ps').val('').trigger('change');
            $('#trdMatchInfo_tradeDate').val('').trigger('change');
            $('#trdMatchInfo_finprodType2').val('').trigger('change');
            $('#trdMatchInfo_finprodId').val('').trigger('change');

        });

        //更多按钮
        $('#ExchangeTransMatch001_deal_btn_more').click(function () {
            if($('#ExchangeTransMatch001_deal_btn_more').find('span').html()=="更多"){
                $('#ExchangeTransMatch001_more_div').show();
                // $('#ExchangeTransMatch001_more_div2').show();
                $('#ExchangeTransMatch001_deal_btn_more').find('span').html("取消更多");
            }else{
                $('#ExchangeTransMatch001_more_div').hide();
                // $('#ExchangeTransMatch001_more_div2').hide();
                $('#ExchangeTransMatch001_deal_btn_more').find('span').html("更多");
            }
        });
    });

}());