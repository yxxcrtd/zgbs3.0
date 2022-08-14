/**
 * PtlRepoPledgeSearch001 债券质押查询
 */
$(function() {
    /**
     * 获取债券质押查询参数
     */
    var getSearchParams = function() {
        var counterId = $('#counterId').val();
        var finprodMarketId = $('#finprodMarketId').val();
        var type4 = $('#type4').val();
        var type5 = $('#type5').val();
        var prodManager = $('#prodManager').val();
        // add by swwan 【重庆银行】 增加 业务类型，交易对手  2021年7月22日 start
        var busiType = $('#busiType').val();
        var counter = $('#counter').val();
        // add by swwan 【重庆银行】 增加 业务类型，交易对手  2021年7月22日 end
        return {
            counterId : counterId ? counterId.join(','):'',
    		// add by wangyang1 【吉林银行】 增加 产品经理  2019年9月29日 start
            prodManager: prodManager,
			// add by wangyang1 【吉林银行】 增加 产品经理  end
            finprodMarketId : finprodMarketId,
            type4 : type4,
            type5 : type5,
			// add by swwan 【重庆银行】 增加 业务类型，交易对手  2021年7月22日 start
			busiType : busiType ? busiType.join(','):'',
            counter : counter ? counter.join(','):''
			// add by swwan 【重庆银行】 增加 业务类型，交易对手  2021年7月22日 end
        }
    };
    /**
     * 获取质押券置换查询参数
     */
    var getSubstitutionSearchParams  = function() {
        var substitutionPortfolioId = $('#substitutionPortfolioId').val();//组合代码
        var substitutionTradeType = $('#substitutionTradeType').val(); // 交易类型
        var substitutionTradePs = $('#substitutionTradePs').val(); // 交易方向
        var substitutionTradeStartDate = $('#substitutionTradeDate').startDate(); // 交易日期开始日期
        var substitutionTradeEndDate = $('#substitutionTradeDate').endDate(); // 交易日期结束日期
        var substitutionStartDate = $('#substitutionDate').startDate(); // 置换日期开始日期
        var substitutionEndDate = $('#substitutionDate').endDate(); // 置换日期结束日期
        return {
            portfolioId : substitutionPortfolioId ? substitutionPortfolioId.join(','):'',
            tradeType: substitutionTradeType,
            tradePs : substitutionTradePs,
            substitutionTradeStartDate : substitutionTradeStartDate,
            substitutionTradeEndDate : substitutionTradeEndDate,
            substitutionStartDate : substitutionStartDate,
            substitutionEndDate : substitutionEndDate
        }
    };

    /**
     * 获取查询参数
     */
    var getPledgeSummarySearchParams = function() {
        var object = $.serializeToObject('#pledgeSummarySearchForm');
        if(object.pledgeSummaryPortfolioId){
            object.portfolioId=object.pledgeSummaryPortfolioId.join(",");
        }
        return {
            portfolioId:object.portfolioId,
            searchDate:$('#pledgeSummaryTradeDate').val()
        }
    };
    
    /**
     * 定义债券质押数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        cols:[
            {field:'counterId',width:150,title: 'PtlRepoPledgeSearch001.counterId'}
            ,{field:'finprodType2Name',width:150,title: 'PtlRepoPledgeSearch001.finprodType2',sortField:'finprod_type2'}
            ,{field:'type4Abbr',width:150,title: 'PtlRepoPledgeSearch001.type4',sortField:'type_4'}
            ,{field:'type5Abbr',width:150,title: 'PtlRepoPledgeSearch001.type5',sortField:'type_5'}
            ,{field:'finprodMarketId',width:150,title: 'PtlRepoPledgeSearch001.finprodMarketId',sortField:'FINPROD_MARKET_ID'}
            ,{field:'finprodAbbr',width:150,title: 'PtlRepoPledgeSearch001.finprodAbbr'}
            ,{field:'companyNature',width:150,title: 'PtlRepoPledgeSearch001.companyNature'}
            ,{field:'isSecBond',width:150,title: 'PtlRepoPledgeSearch001.isSecBond'}
            ,{field:'pledgeAmt',width:150,type:'money',title: 'PtlRepoPledgeSearch001.pledgeAmt',sortField:'PLEDGE_AMT'}
            ,{field:'intRate',width:150,type:'number',title: 'PtlRepoPledgeSearch001.intRate'}
            ,{field:'term',width:150,title: 'PtlRepoPledgeSearch001.term'}
            ,{field:'vdate',width:150,title: 'PtlRepoPledgeSearch001.vdate',type:'date'}
            ,{field:'mdate',width:150,title: 'PtlRepoPledgeSearch001.mdate',type:'date'}
            ,{field:'pledgeRatio',width:150,type:'number',title: 'PtlRepoPledgeSearch001.pledgeRatio'}
            ,{field:'pledgeFaceValue',width:150,type:'money',title: 'PtlRepoPledgeSearch001.pledgeFaceValue'}
            ,{field:'gradeResult',width:150,title: 'PtlRepoPledgeSearch001.gradeResult',sortField:'GRADE_RESULT'}
            ,{field:'registOrgAbbr',width:150,title: 'PtlRepoPledgeSearch001.registOrg',sortField:'REGIST_ORG'}
            ,{field:'counter',width:150,title: 'PtlRepoPledgeSearch001.counter',sortField:'REGIST_ORG'} //交易对手
        ]
    });

    /**
     * 定义质押券置换数据表格
     */
    var substitutionDataTable=$("#substitutionDataTable").joyintable({
        ajax: {
            url: basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch002/substitutionFindPage',
            type: 'POST',
            data: function() {
                return getSubstitutionSearchParams();
            }
        },
        cols:[
            {field:'businessCode',width:150,title: 'PledgeSubstitution001.businessCode',sort:false,
                render: function (data, type, row, meta) {
                    return "<a class='businessCode-link' style='cursor: pointer;'>" + row.businessCode + "</a>";
                }}
            ,{field:'tradeTypeName',width:150,title: 'PledgeSubstitution001.tradeType',sort:false,sortField: 'tradeType'}
            ,{field:'sourceBusinessCode',width:150,title: 'PledgeSubstitution001.sourceBusinessCode',sort:false,
                render: function (data, type, row, meta) {
                    return "<a class='sourceBusinessCode-link' style='cursor: pointer;'>" + row.sourceBusinessCode + "</a>";
                }}
            ,{field:'entrustCode',width:150,title: 'PledgeSubstitution001.entrustCode',sort:false}
            ,{field:'portfolioId',width:150,title: 'PledgeSubstitution001.portfolioId',sort:false,
                render: function (data, type, row, meta) {
                    return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                }}
            ,{field:'portfolioName',width:150,title: 'PledgeSubstitution001.portfolioName',sort:false}
            ,{field:'finprodId',width:150,title: 'PledgeSubstitution001.finprodId',sort:false,
                render: function (data, type, row, meta) {
                if(row.finprodId!=undefined)
                    return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodId+"</a>";
                }}
            ,{field:'finprodName',width:150,title: 'PledgeSubstitution001.finprodName',sort:false}
            ,{field:'tradePsName',width:150,title: 'PledgeSubstitution001.tradePs',sort:false}
            ,{field:'tradeMarketName',width:150,title: 'PledgeSubstitution001.tradeMarket',sort:false}
            ,{field:'tradeAmt',width:150,type:'number',title: 'PledgeSubstitution001.tradeAmt',sort:false}
            ,{field:'tradeDate',width:150,title: 'PledgeSubstitution001.tradeDate',type:'date',sort:false}
            ,{field:'vPayDate',width:150,title: 'PledgeSubstitution001.vPayDate',type:'date',sort:false}
            ,{field:'mPayDate',width:150,type:'date',title: 'PledgeSubstitution001.mPayDate',sort:false}
            ,{field:'termType',width:150,title: 'PledgeSubstitution001.termType',sort:false}
            ,{field:'actCapDays',width:150,title: 'PledgeSubstitution001.actCapDays',sort:false}
            ,{field:'pledgeSubstitutionDate',width:150,title: 'PledgeSubstitution001.pledgeSubstitutionDate',type:'date',sort:false}
            ,{field:'ccyName',width:150,title: 'PledgeSubstitution001.ccy',sort:false}
            ,{field:'remark',width:150,title: 'PledgeSubstitution001.remark',sort:false}
        ]
    });

    /**
     * 定义债券质押汇总查询数据表格
     */
    var pledgeSummaryDataTable=$("#pledgeSummaryDataTable").joyintable({
        ajax: {
            url: basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch004/pledgeSummaryFindPage',
            type: 'POST',
            data: function() {
                return getPledgeSummarySearchParams();
            }
        },
        cols:[
            {field:'portfolioId',width:150,title: '组合代码',sort:false}
            ,{field:'portfolioName',width:220,title: '组合名称',sort:false}
            ,{field:'secid',width:150,title: '债券代码',sort:false}
            ,{field:'secName',width:150,title: '债券名称',sort:false}
            ,{field:'canpledgeAmt',width:150,type:'money',title: '持仓券面(元)',sort:false}
            ,{field:'remainAmt',width:150,type:'money',title: '可质押券面(元)',sort:false}
            // ,{field:'canpledgeAmt',width:150,type:'money',title: '可质押券面',sort:false}
            ,{field:'pledgeAmt',width:150,type:'money',title: '已质押券面(元)',sort:false}
            // ,{field:'gradeTypeNamezt',width:110,title: '主体评级',sort:false}
            // ,{field:'gradeTypeNamezx',width:110,title: '债项评级',sort:false}
            // ,{field:'registOrgName',width:280,title: '机构名称',sort:false}
        ]
    });
    
    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();
        Joyin.ajaxJson(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch001/exportCheck', paramObj, function(result) {
            if (result && result.statuCode == '200') {

                if (table != null && table != '') {
                    var selArr = table.getAllCheckedRows();
                    var datas = JSON.stringify(selArr);
                }
                if (!selArr || selArr.length == 0) {
                    var paramObj = getSearchParams();
                    Joyin.exportExcel('export', paramObj);
                } else {
                    Joyin.exportExcel('export', {
                        datas : datas
                    });
                }

            }
        });

    };
    /**
     * 点击链接跳转到该组合信息查看页面
     */
    substitutionDataTable.bind('click', '.portfolioId-link', function (row) {
        Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"),
            basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
            "95%", "90%",
            {
                pageModel: PAGE_MODEL_REFERENCE,
                portfolioId: row.portfolioId,
                portfolioName: row.portfolioName
            });
    });

    /**
     * 点击业务编号链接跳转到置换明细业务查看页面
     */
    substitutionDataTable.bind('click', '.businessCode-link', function (data) {

        data.pageModel = PAGE_MODEL_REFERENCE;
        Joyin.dialog(JoyinUtil.getLabel("PtlRepoPledgeSearch003"),
            JoyinUtil.getRealPath("invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch003/init"),
            "99%", "99%", data

        );

    });

    /**
     * 点击原始业务编号链接跳转到业务查看页面
     */
    substitutionDataTable.bind('click', '.sourceBusinessCode-link', function (data) {

        data.pageModel=PAGE_MODEL_REFERENCE;
        data.tradeId = data.sourceBusinessCode;
        if(data.tradeTypeName=='债券借贷交易')
        {
            Joyin.dialog(JoyinUtil.getLabel("TrdBondLendInvestTrade002.reference"),
                JoyinUtil.getRealPath("invest/trade/bondlend/TrdBondLendInvestTrade002/init"),
                "99%", "99%", data);
        }
        else {
            Joyin.dialog(JoyinUtil.getLabel("TrdRepoTrade002.reference"),
                JoyinUtil.getRealPath("invest/trade/trdrepotrade/TrdRepoTrade002/init"),
                "99%", "99%", data
            );
        }

    });
    /**
     * 点击标的链接跳转到该标的查看页面
     */
    substitutionDataTable.bind('click', '.finprodId-link', function (row) {

        var url = "home.html?a=a&finprodMarketId=" + row.finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a"; // 默认债券标的
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
            finprodId: row.finprodId
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "98%", "98%",
            param);

        // Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002"),
        //     basePath + "invest/asset/bond/FinBondTargetAsset002/init",
        //     "95%", "90%",
        //     { pageModel:PAGE_MODEL_REFERENCE,
        //         finprodId: row.finprodId,
        //         finprodName: row.finprodName });
    });
    /**
     * 绑定导出事件->质押券置换查询导出
     */
    var exportSubstitutionExcel = function() {
//        var paramObj = getSubstitutionSearchParams();
//        Joyin.ajaxJson(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch002/exportCheck', paramObj, function(result) {
//            if (result && result.statuCode == '200') {
//                Joyin.exportExcel(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch002/export', paramObj);

                if (substitutionDataTable != null && substitutionDataTable != '') {
                    var selArr = substitutionDataTable.getAllCheckedRows();
                    var datas = JSON.stringify(selArr);
                }
                if (!selArr || selArr.length == 0) {
                    var paramObj = getSubstitutionSearchParams();
                    Joyin.exportExcel(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch002/export', paramObj);
                } else {
                    Joyin.exportExcel(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch002/export', {
                        datas : datas
                    });
                }

//            }
//        });
    };

    /**
     * 绑定导出事件->质押券汇总查询导出
     */
    var exportPledgeSummaryExcel = function() {

//        var paramObj = getPledgeSummarySearchParams();
//        Joyin.ajaxJson(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch004/exportCheck', paramObj, function(result) {
//            if (result && result.statuCode == '200') {
//                Joyin.exportExcel(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch004/export', paramObj);

                if (pledgeSummaryDataTable != null && pledgeSummaryDataTable != '') {
                    var selArr = pledgeSummaryDataTable.getAllCheckedRows();
                    var datas = JSON.stringify(selArr);
                }
                if (!selArr || selArr.length == 0) {
                    if ($('#pledgeSummaryPortfolioId').val() == null || $('#pledgeSummaryPortfolioId').val() == ''){
                        Joyin.error('请选择组合代码!');
                        return;
                    }
                    if ($('#pledgeSummaryTradeDate').val() == null || $('#pledgeSummaryTradeDate').val() == ''){
                        Joyin.error('请选择交易日期!');
                        return;
                    }
                    var paramObj = getPledgeSummarySearchParams();
                    Joyin.exportExcel(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch004/export', paramObj);
                } else {
                    Joyin.exportExcel(basePath + 'invest/portfolio/portfolioanalysis/ptlrepopledgesearch/PtlRepoPledgeSearch004/export', {
                        datas : datas
                    });
                }

//            }
//        });
    };
    
    /**
     * 画面加载完成
     */
    $(function() {
        $('#PtlRepoPledgeSearch001_btn_search').click(function() {

            table.load();
        });
        // 绑定按钮事件
        $('#PtlRepoPledgeSearch001_btn_export').click(function() {
            exportExcel();
        });

        $('.condition-area-substitution_1 #PtlRepoPledgeSearch001_btn_search').click(function() {
            substitutionDataTable.load();
        });
        
        $('.condition-area-substitution_2 #PtlRepoPledgeSearch001_btn_search').click(function() {
            if ($('#pledgeSummaryPortfolioId').val() == null || $('#pledgeSummaryPortfolioId').val() == ''){
                Joyin.error('请选择组合代码!');
                return;
            }
            if ($('#pledgeSummaryTradeDate').val() == null || $('#pledgeSummaryTradeDate').val() == ''){
                Joyin.error('请选择交易日期!');
                return;
            }
            pledgeSummaryDataTable.load();
        });
        // 绑定按钮事件
        $('.substitutionSearchDiv #PtlRepoPledgeSearch001_btn_export').click(function() {
            exportSubstitutionExcel();
        });

        $('.pledgeSummarySearchDiv #PtlRepoPledgeSearch001_btn_export').click(function() {
            exportPledgeSummaryExcel();
        });
        
        //重置
   	 $('.pledgeSummarySearchDiv #PtlRepoPledgeSearch001_btn_reset').click(function(){
   		window.location.reload();
   	 });

    });
}());