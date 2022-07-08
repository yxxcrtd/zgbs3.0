/**
 * 所有投资查询
 */
(function(){
    /**
     * 获取查询参数
     */
    var searchform =function(){
        var data = $.serializeToObject('#searchForm');
        if($('#finprodType2').val()){
            data.finprodType2 = $('#finprodType2').val().join(',');
        }
        if($('#portfolioId').val()){
            data.portfolioId = $('#portfolioId').val().join(',');
        }
        if($('#entrustStatus').val()){
            data.entrustStatus = $('#entrustStatus').val().join(',');
        }
        //利率互换交易方向  后台转换
        if($('#ps').val() && $('#ps').select2('data')[0].col ==IRS_TYPE ){
            data.ps= IRS_TYPE+data.ps
        }
        return data;
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return searchform();
            }
        },
        dblclick:function(table, data) {
            data.operateType = "dblclick";
            data.pageModel=PAGE_MODEL_REFERENCE;
            tradeMarket = data.tradeMarket;
            entrustCreateSource = data.entrustCreateSource;
            //交易所（上交和深交）的指令
            if(tradeMarket == TRADE_PLACE_03 || tradeMarket == TRADE_PLACE_04) {
               // ENTRUST_CLASS=多组合单资产指令 且 资产类型 IN（债券、股票、基金、回购）且 交易场所IN（上交所、深交所）打开场内交易指令画面对应交易平台的多组合指令画面
                if(data.entrustClass==ENTRUST_CLASS_02&&(data.assetType == F01 || data.assetType == F02||data.assetType == F07||data.assetType == F11 )){
                    pagePath = "invest/entrust/etrexchangetrade/EtrExchangeTradeMulti001/init";
                    Joyin.dialog(JoyinUtil.getLabel('EtrAgreRepTrade001.detail'), basePath + pagePath,
                        "95%", "90%", {
                            pageModel: PAGE_MODEL_REFERENCE,
                            entrustId: data.entrustId,
                        });//交易所协议回购指令详情
                    return;
                }
            //     if (entrustCreateSource != null && entrustCreateSource == ENTRUST_CREATE_SOURCE_02) {
            //         var entrustType = data.entrustType;
            //         if(entrustType == ENTRUST_BUSS_TYPE_22 || entrustType == ENTRUST_BUSS_TYPE_21
            //             || entrustType == ENTRUST_BUSS_TYPE_20 || entrustType == ENTRUST_BUSS_TYPE_19
            //             || entrustType == ENTRUST_BUSS_TYPE_18 || entrustType == ENTRUST_BUSS_TYPE_17) {
            //             pagePath = "invest/entrust/etragrereptrade/EtrAgreRepTrade001/init";
            //             Joyin.dialog(JoyinUtil.getLabel('EtrAgreRepTrade001.detail'), basePath + pagePath,
            //                 "95%", "90%", {
            //                     pageModel: PAGE_MODEL_REFERENCE,
            //                     entrustId: data.entrustId,
            //                 });//交易所协议回购指令详情
            //             return;
            //         }
            //         //债券申购进可转债页面
            //         if(data.assetType == F01 && data.ps == PS_T04 ){
            //             label='EtrConBond002.refrence';
            //             pagePath = '/invest/entrust/etrconbond/EtrConBond002/init'
            //         }else{
            //             label='EtrExchangeTrade001.detail';
            //             pagePath = "/invest/entrust/etrexchangetrade/EtrExchangeTrade001/init";
            //         }
            //         Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            //             "95%", "90%",data,
            //             function(){table.reload();});//交易所场内指令详情
            //         return;
            //     }
            }
            /*//交易所（上交和深交）的指令
            if((tradeMarket == TRADE_PLACE_03 || tradeMarket == TRADE_PLACE_04) && data.assetType != F17) {
                var entrustType = data.entrustType;
                if(entrustType == ENTRUST_BUSS_TYPE_22 || entrustType == ENTRUST_BUSS_TYPE_21
                    || entrustType == ENTRUST_BUSS_TYPE_20 || entrustType == ENTRUST_BUSS_TYPE_19
                    || entrustType == ENTRUST_BUSS_TYPE_18 || entrustType == ENTRUST_BUSS_TYPE_17) {
                    pagePath = "invest/entrust/etragrereptrade/EtrAgreRepTrade001/init";
                    Joyin.dialog(JoyinUtil.getLabel('EtrAgreRepTrade001.detail'), basePath + pagePath,
                        "95%", "90%", {
                            pageModel: PAGE_MODEL_REFERENCE,
                            entrustId: data.entrustId,
                        });//交易所指令详情
                    return;
                }
            	//债券申购进可转债页面
            	else if(data.assetType == F01 && data.ps == PS_T04 ){
            		label='EtrConBond002.refrence';
            		pagePath = '/invest/entrust/etrconbond/EtrConBond002/init'
            	}else{
                	label='EtrExchangeTrade001.detail';
                	pagePath = "/invest/entrust/etrexchangetrade/EtrExchangeTrade001/init";
            	}
                Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "95%", "90%",data,
                    function(){table.reload();});//交易所指令详情
                
            }*/
            console.log(data, '...data')
            //银行间质押式回购
            if(data.tradeMarket == '01'&& data.assetType == F07){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F07
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F11 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //银行间债券买卖
            else if(data.tradeMarket == '01'&& data.assetType == F01 && (data.ps == PS_T01 || data.ps == PS_T02)){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F01
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F11 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //场内交易股票买卖
            else if((data.assetType == F11 && data.ps == PS_T01) ||
                (data.assetType == F11 && data.ps == PS_T02)){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F11
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F11 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //新股申购 - 非交易
            else if(data.assetType == F11 && data.ps == PS_T04 && data.busiTypePosition == 'EX009'){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F11
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F11 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";
                console.log(url, '...url')
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //新股申购 - 交易
            else if(data.assetType == F11 && data.ps == PS_T04 && data.busiTypePosition != 'EX009'){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F11
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F11 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";
                console.log(url, '...url')
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //通用质押式回购
            else if(data.assetType == F07&&data.busiTypePosition!=null){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F07
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F07 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";
                console.log(url, '...url')
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //债券买卖
            else if(data.assetType == F01 && data.busiTypePosition == 'EX001' && (data.ps == PS_T01 || data.ps == PS_T02) ){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F01
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F01 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //基金买卖
            else if(data.assetType == F02 && data.busiTypePosition == 'EX001' && (data.ps == PS_T01 || data.ps == PS_T02) ){
                var param = {
                    "busiCode": data.entrustId,
                    "finprodType2": F02
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId + "&finprodType2=" + F02 +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //大宗交易、协议现券 、债转股 、质押券出入库、配债、配股、新股申购、可转债申购、场内债券
            else if(data.busiTypePosition == 'EX002' || data.busiTypePosition == 'EX016'
                || data.busiTypePosition == 'EX004' || data.busiTypePosition == 'EX005'
                || data.busiTypePosition == 'EX006' || data.busiTypePosition == 'EX008'
                || data.busiTypePosition == 'EX009' || data.busiTypePosition == 'EX007'
                || data.busiTypePosition == 'EX017'){
                var param = {
                    "busiCode": data.entrustId
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //债转股
            else if(data.assetType == F01 && data.ps == "T49" ){
                var param = {
                    "busiCode": data.entrustId
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //债回售
            else if(data.assetType == F01 && data.ps == PS_T03 ){
                var param = {
                    "busiCode": data.entrustId
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //信用保护凭证
            else if(data.assetType == F37 ){
                var param = {
                    "busiCode": data.entrustId
                };
                var url = "home.html?a=a&busiCode=" + data.entrustId +
                    "&pageModel=1&editable=false&isTodoList=true#/instructionsSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            else if(data.assetType == F01 && data.ps != PS_T04 && data.ps != PS_T49){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrBond"),
                    basePath
                    + "invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F07){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrBack"),
                    basePath
                    + "invest/entrust/etrrepo/EtrRepo002/init",
                    "95%", "90%", data,
                    function(){table.reload();});
                // add by zhangpuyu 2910/10/18 修复委托查询买断式回购双击跳转失败start
            }else if(data.assetType == F10) {
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrBack"),
                    basePath
                    + "invest/entrust/etrboutrepo/EtrBoutRepo002/init",
                    "95%", "80%", data,
                    function (){table.reload();});
            }else if(data.assetType == F02){
                // add by zhangpuyu 2910/10/18 修复委托查询买断式回购双击跳转失败 end
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrFund"),
                    basePath
                    + "invest/entrust/etrfund/EtrFund002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F05){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrBrow"),
                    basePath
                    + "invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F08){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrLend"),
                    basePath
                    + "invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F04){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrPlanN"),
                    basePath
                    + "invest/entrust/etrplanassetn/EtrPlanAssetN002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F03){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrPlanR"),
                    basePath
                    + "invest/entrust/etrplanassetr/EtrPlanAssetR002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F06){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrProject"),
                    basePath
                    + "invest/entrust/etrproject/EtrProject002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F09){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrPlanC"),
                    basePath
                    + "invest/entrust/etrplanassetc/EtrPlanAssetC002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F11){
                Joyin.dialog(JoyinUtil.getLabel("EtrStock002.refence"),
                    basePath
                    + "invest/entrust/etrstock/EtrStock002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }else if(data.assetType == F12){
                Joyin.dialog(JoyinUtil.getLabel("EtrSecLend.refrence"),
                    basePath
                    + "invest/entrust/etrseclend/EtrSecLend002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
            }else if(data.assetType == F13){
                Joyin.dialog(JoyinUtil.getLabel("EtrWtSpotex.dblclick"),
                    basePath
                    + "invest/entrust/etrwtspotex/EtrWtSpotex002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
            }else if(data.assetType == F14){
                Joyin.dialog(JoyinUtil.getLabel("EtrForwex002.refrence"),
                    basePath
                    + "invest/entrust/etrforwex/EtrForwex002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
            }else if(data.assetType == F15){
                Joyin.dialog(JoyinUtil.getLabel("EtrWtFxswap002.refrence"),
                    basePath
                    + "invest/entrust/etrwtfxswap/EtrWtFxswap002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
            }else if(data.assetType == F16){
                Joyin.dialog(JoyinUtil.getLabel("EtrIRS.dblclick"),
                    basePath
                    + "invest/entrust/etrirs/EtrIRS002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
            }else if(data.assetType == F17){
                Joyin.dialog(JoyinUtil.getLabel("EtrBondFirstLBidding002.refrence"),
                    basePath
                    + "invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        entrustStatus:data.entrustStatus,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
                //ADD  BY GAOPENG 20191123 增加可转债转股委托 start 
            }else if(data.assetType == F01 && data.ps == PS_T49){
                Joyin.dialog(JoyinUtil.getLabel("EtrBondToStock002.dblclick"),
                    basePath
                    + "invest/entrust/etrbondtostock/EtrBondToStock002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
                //ADD  BY GAOPENG 20191123 增加可转债转股委托 end
            }else if(data.assetType == F01 && data.ps == PS_T04){
                Joyin.dialog(JoyinUtil.getLabel("EtrConBond002.dblclick"),
                    basePath
                    + "invest/entrust/etrconbond/EtrConBond002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId,
                        targetFinprodId:data.finprodId,
                        portfolioId:data.portfolioId
                    },
                    function(){table.reload();});
            }else if(data.assetType == F36 ){
                Joyin.dialog(JoyinUtil.getLabel("EtrInstruction002.dblclick"),
                    basePath
                    + "invest/entrust/etrinstruction/EtrInstruction002/init",
                    "95%", "80%", {
                        pageModel:PAGE_MODEL_REFERENCE,
                        entrustId:data.entrustId
                    },
                    function(){table.reload();});
            } else if(data.assetType == F19){
                Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.etrProjectnet"),
                    basePath
                    + "invest/entrust/etrprojectnet/EtrProjectNet002/init",
                    "95%", "80%", data,
                    function(){table.reload();});
            }
        },
        cols:[
            {field:'entrustId',visible:false}
            ,{field:'entrustStatus', visible:false}
            ,{field:'entrustType',visible:false}
            ,{field:'entrustStatusStr', title: 'EtrInvestAllInfo001.entrustStatus', width:'200',sortField:"entrustStatus"}
            ,{field:'portfolioId', title: 'EtrInvestAllInfo001.portfolioId', width:'200',
                render : function(data, type, row, meta) {
                    // if(row.portfolioId&&row.entrustClass==ENTRUST_CLASS_01){
                        return "<a  class='pro-link portfolioIdLink' style='cursor: pointer;'>"+row.portfolioId+"</ a>";
                    // }else{
                    //     return '';
                    // }
                } ,sortField:"portfolioId"
            }
            ,{field:'portfolioName', title: 'EtrInvestAllInfo001.portfolioName', width:'200',sortField:"portfolioName"}
            // add by dubaojun 2020-08-12 上农商个性化-增加证券账户名称的显示 start
            ,{field:'secAcctName', title: 'EtrInvestAllInfo001.secAcctName', width:'200',sortField:"secAcctName"}
            // add by dubaojun 2020-08-12 上农商个性化-增加证券账户名称的显示 end
            ,{field:'assetType',visible:false}
            ,{field:'assetTypeStr', title: 'EtrInvestAllInfo001.finprodType2', width:'200',sortField:"assetType"}
            ,{field:'finprodId',visible:false}
            ,{field:'finprodMarketId', title: 'EtrInvestAllInfo001.finprodId', width:'200',
                render : function(data, type, row, meta) {
                    if(row.assetType != F07 && row.assetType != F37){
                        return "<a  class='pro-link finprodIdLink' style='cursor: pointer;'>"+data+"</ a>";
                    }
                },sortField:"finprodMarketId"
            }
            ,{field:'finprodName', title: 'EtrInvestAllInfo001.finprodName', width:'200',sortField:"finprodName"}
            ,{field:'ps', visible:false}
            ,{field:'psStr', title: 'EtrInvestAllInfo001.ps', width:'10%'}
            ,{field:'tradeMarket', visible:false}
            ,{field:'tradeMarketStr', title: 'EtrInvestAllInfo001.tradeMarket', width:'200',sortField:"tradeMarket"}
            ,{field:'tradePlat', visible:false}
            ,{field:'tradePlatStr', title: 'EtrInvestAllInfo001.tradePlat', width:'200',sortField:"settleSpreed"}
            ,{field:'entrustAmt',type:'number', title: 'EtrInvestAllInfo001.entrustAmt', width:'200', type:'money',sortField:"entrustAmt"}
            ,{field:'ccy',visible:false}
            ,{field:'ccyStr', title: 'EtrInvestAllInfo001.ccy', width:'200',sortField:"ccy"}
            ,{field:'vdateStr', title: 'EtrInvestAllInfo001.vdate', width:'200' ,type:'date',sortField:"vdate"}
            ,{field:'mdateStr', title: 'EtrInvestAllInfo001.mdate', width:'200' ,type:'date',sortField:"mdate"}
            ,{field:'invManagerId', title: 'EtrInvestAllInfo001.invManagerId', width:'200',sortField:"invManagerId"}
            ,{field:'remark', title: 'EtrInvestAllInfo001.remark', width:'200',sortable:false}
            ,{field:'nextUser', title: 'EtrCommon.nextUser', width:'10%',sortable:false}
            ,{field:'pbExportstatus', visible:false}
            ,{field:'busiTypePosition', visible:false}
            ,{field:'pbExportstatusStr', title: 'EtrInvestAllInfo001.pbExportstatus', width:'150',sortField:"pbExportstatus", visible:false}
            ,{field:'entrustCreateSource', visible:false}
        ],
    });

    /**
     * 组合代码链接
     */
    table.bind('click','.portfolioIdLink', function(row) {
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
            "98%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                portfolioId:row.portfolioId});//主键组合代码
    });
    /**
     * 资产代码链接
     */
    table.bind('click','.finprodIdLink', function(row) {
        //在新建界面进行显示单行的详细信息
        if(row.assetType == F01){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astBond"),
                basePath
                + "invest/asset/bond/FinBondTargetAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F02){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astFund"),
                basePath
                + "invest/asset/fund/FinFundTarget002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F05){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astBrow"),
                basePath
                + "invest/asset/depo/FinDepositTarget002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F04){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astPlanN"),
                basePath
                + "invest/asset/plan/planNet/FinPlanNetAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F03){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astPlanR"),
                basePath
                + "invest/asset/plan/planProfit/FinPlanProfitAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F06){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astProject"),
                basePath
                + "invest/asset/project/FinProjectTargetAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F09){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astPlanC"),
                basePath
                + "invest/asset/plan/plancur/FinPlanAssetC002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F12){
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath
                + "invest/asset/bond/FinBondTargetAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //债券借贷委托查看
                });
        }else if(row.assetType == F17){
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath
                + "invest/asset/tenderbond/FinTenderBondTargetAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //债券一级债券委托查看
                });
        }else if(row.assetType == F19){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astProjectnet"),
                basePath
                + "invest/asset/projectnet/FinProjectNetTargetAsset002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }else if(row.assetType == F11){
            Joyin.dialog(JoyinUtil.getLabel("EtrInvestAllInfo001.astStock"),
                basePath
                + "/invest/asset/stock/FinStockInfo002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:row.finprodId,  //金融产品代码
                });
        }
    });
    /**
     * 导出
     */
    var exportExcel = function(){
        //勾选导出
        var paramObj = searchform();
        var selectedItem = [];
        $.each(table.getAllCheckedRows() ,function (i, item) {
            selectedItem.push(item.entrustId);
        });
        paramObj.selectedItem = selectedItem.join(',');
        Joyin.exportExcel(basePath+"invest/entrust/etrinvestallinfo/EtrInvestAllInfo001/export", paramObj);
    };
    /**
     * PB导出
     */
    var exportPBExcel = function(){
        //勾选导出
        var paramObj = searchform();
        var selectedItem = [];
        var isTrue = true;
        var checkMsg="";
        $.each(table.getAllCheckedRows() ,function (i, item) {
            selectedItem.push(item.entrustId);
            if(!(item.entrustStatus=='02'||item.entrustStatus=='03')){
                checkMsg+="选中的第"+(i+1)+"行，只允许导出指令有效状态为“待接收”、“已接收”的指令！</br>";
                isTrue = false;
            }else if(!(item.assetType==F01||item.assetType==F07)){
                checkMsg+="选中的第"+(i+1)+"行，只允许导出交易类型为债券买卖、债券质押式竞价回购的指令！</br>";
                isTrue = false;
            }else {
                if(item.assetType==F07){
                    Joyin.ajaxSync('/invest/entrust/etrinvestallinfo/EtrInvestAllInfo001/checkPBRepoType',{entrustId:item.entrustId},
                        function(result) {
                            if(!(result&&result.assetType1=='H02')){
                                checkMsg+="选中的第"+(i+1)+"行，只允许导出交易类型为债券买卖、债券质押式竞价回购的指令！</br>";
                                isTrue = false;
                            }
                        });
                }
                if(!(item.tradeMarket=='03'||item.tradeMarket=='04')){
                    checkMsg+="选中的第"+(i+1)+"行，只允许导出交易场所为“上海证券交易所”、“深圳证券交易所”的指令！</br>";
                    isTrue = false;
                }
                if(item.assetType==F01){
                    if(!(item.ps=='T01'||item.ps=='T02')){
                        checkMsg+="选中的第"+(i+1)+"行，只允许导出交易类型为债券买卖、债券质押式竞价回购的指令！</br>";
                        isTrue = false;
                    }
                    if(!(item.tradePlat=='02'||item.tradePlat=='03'||item.tradePlat=='04')){
                        checkMsg+="选中的第"+(i+1)+"行，只允许导出交易平台为“固收收益平台”、“大宗交易平台”、“竞价撮合平台”的指令！</br>";
                        isTrue = false;
                    }
                }

            }
        });
        paramObj.selectedItem = selectedItem.join(',');
        if(paramObj.selectedItem.length==0){
            Joyin.error(JoyinUtil
                .getMessage("请至少选择一条委托数据！"));
            return;
        }
        if(!isTrue){
            Joyin.warn(JoyinUtil
                .getMessage(checkMsg));
            return;
        }
        Joyin.ajaxSync('/invest/entrust/etrinvestallinfo/EtrInvestAllInfo001/checkPBExportType',paramObj,
            function(result) {
                if(result){
                    Joyin.confirm("委托编号："+result+"已导出，是否确认重复导出？",function() {
                        Joyin.exportExcel(basePath+"invest/entrust/etrinvestallinfo/EtrInvestAllInfo001/exportPB", paramObj);
                    });
                }else{
                    Joyin.exportExcel(basePath+"invest/entrust/etrinvestallinfo/EtrInvestAllInfo001/exportPB", paramObj);
                }
                table.reload();
            });
    };

    /**
     * 导出pdf add by zengchengfa 20210730
     */
    var exportPdf = function(){
        if(table!=null && table!=''){
            var selArr = table.getAllCheckedRows();
            // 列表中未勾选
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            //勾选导出
            var param = searchform();
            var selectedItem = [];
            var assetType = '';
            var isError = false;
            $.each(table.getAllCheckedRows() ,function (i, item) {
                if(assetType == ''){
                    assetType = item.assetType;
                }else {
                    if(item.assetType != assetType){
                        isError = true;
                        return;
                    }
                }
                selectedItem.push(item.entrustId);
            });
            if(isError){
                JoyinUtil.showMessage("选择的指令非同一资产类型，无法打印，请重新选择！","error");
                return;
            }
            param.selectedItem = selectedItem.join(',');
            param.assetType = assetType;
            Joyin.exportExcel(basePath+"invest/entrust/etrinvestallinfo/EtrInvestAllInfo001/exportPdf", param);
        }else{
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
        }
    };

    /**
     * 编辑按钮事件
     */
    $(function () {
        /**
         * 查询按钮
         */
        $('#EtrInvestAllInfo001_btn_search').click(function() {
            table.load();
        });
        /**
         * 导出按钮
         */
        $('#EtrInvestAllInfo001_btn_export').click(function() {
            exportExcel();
        });
        /**
         * 导出按钮
         */
        $('#EtrInvestAllInfo001_btn_pb_export').click(function() {
            exportPBExcel();
        });

        /**
         * 导出pdf按钮 add by zengchengfa 20210730
         */
        $('#EtrInvestAllInfo001_btn_pdf_export').click(function() {
            exportPdf();
        });
    });
}());