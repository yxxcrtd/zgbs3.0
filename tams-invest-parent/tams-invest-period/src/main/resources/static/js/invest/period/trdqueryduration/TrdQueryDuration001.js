/**
 * 组合信息查看页面
 */
var showPtlManagement=function(portfolioId){
    Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"),
        basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
        "95%", "90%",{
            pageModel:PAGE_MODEL_REFERENCE,
            portfolioId:portfolioId,
        });
};
//不同资产的查看页面
var showDetailByFinprodId=function(finprodId,finprodType2){
    var label='';
    var pagePath='';
    //债券标的信息查看
    if(finprodType2 == PINAN_ASSET_TYPE_F01){
        // label='FinBondTargetAsset002.title';
        // pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
        // Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
        //     "95%", "90%",{
        //         pageModel:PAGE_MODEL_REFERENCE,
        //         finprodId:finprodId
        //     });//基金类标的信息查看
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
    }else if(finprodType2 == PINAN_ASSET_TYPE_F02){
        label='FinFundTarget002.detail';
        pagePath = "invest/asset/fund/FinFundTarget002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });//计划类利率型标的信息查看
    }else if(finprodType2 == PINAN_ASSET_TYPE_F03){
        label='FinPlanProfitAsset002.detail';
        pagePath = "invest/asset/plan/planProfit/FinPlanProfitAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });//计划类净值型标的信息查看
    }else if(finprodType2 == PINAN_ASSET_TYPE_F04){
        label='FinPlanNetAsset002.detail';
        pagePath = "invest/asset/plan/planNet/FinPlanNetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });//存款标的信息查看
    }else if(finprodType2 == PINAN_ASSET_TYPE_F05){
        label='FinDepositTarget002.title';
        pagePath = "invest/asset/depo/FinDepositTarget002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });//项目类资产标的信息查看
    }else if(finprodType2 == PINAN_ASSET_TYPE_F06){
        label='FinProjectTargetAsset002.title';
        pagePath = "invest/asset/project/FinProjectTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });//回购标的信息查看
    }else if(finprodType2 == PINAN_ASSET_TYPE_F07){
        label='';
        pagePath = "";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });//拆借标的信息查看
    }else if(finprodType2 == PINAN_ASSET_TYPE_F08){
        label='';
        pagePath = "";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });
    }else if(finprodType2 == PINAN_ASSET_TYPE_F10){
        label='FinBondTargetAsset002.title';
        pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });
    }else if(finprodType2 == PINAN_ASSET_TYPE_F09){
        label='FinPlanAssetC002.title';
        pagePath = "invest/asset/plan/plancur/FinPlanAssetC002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });
    }else if(finprodType2 == PINAN_ASSET_TYPE_F11){
        label='FinStockInfo002.title';
        pagePath = "invest/asset/stock/FinStockInfo002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });
    }else if(finprodType2 == PINAN_ASSET_TYPE_F18){
        label='TrdOptionTrade002.reference';
        pagePath = "invest/trade/option/TrdOptionTrade002/init";
        Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
            "95%", "90%",{
                pageModel:PAGE_MODEL_REFERENCE,
                finprodId:finprodId
            });
    }
};

$(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
    	var result=$.serializeToObject('#TrdQueryDuration001_searchForm');
    	if(!$("#TrdQueryDuration001_finprodType2").val() == ''){
            result.finprodType2=$("#TrdQueryDuration001_finprodType2").val().join(",");
    	}
        if ($("#TrdQueryDuration001_portfolioId").val()) {
            result.portfolioId = $("#TrdQueryDuration001_portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }
        if ($("#TrdQueryDuration001_tradeDate").val()) {
            result.tradeDateStart = $("#TrdQueryDuration001_tradeDate").startDate();
            result.tradeDateEnd = $("#TrdQueryDuration001_tradeDate").endDate();
        }
    	return result;
    };
    
    /**
     * 定义已接收的数据表格
     */
    
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getPageParams();
            }
        },
        dblclick:function(table, data) {
            var tradeId = data.tradeId;
            var workflowId = '';
            if(data.workflowId){
                workflowId = data.workflowId;
            }
            //债券详情
            if(data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.paBussType != 137  && data.paBussType != 138){
                let pagePath = "invest/period/trdbondduration/TrdBondDuration002/init";
                let title = "债券还本付息信息查看";
                if (data.paBussType == 11) {
                    pagePath = "invest/period/trdbondduration/TrdBondDuration003/init";
                    title = "债券行权交易信息查看";
                }
                data.pageModel = PAGE_MODEL_REFERENCE;
                data.totleTradeId = data.busiCode;
                Joyin.dialog(title,basePath+pagePath,
                    "98%", "95%", data, function(){});
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F02){
                if(FUND_DEAL_TYPE == '02'){
                    label='TrdFundDuration001.reference';
                    pagePath = "invest/period/trdfunddurationnew/TrdFundDurationNew002/init";
                    data.pageModel = PAGE_MODEL_REFERENCE;
                    if(data.statusValid == CHANCLE_STATUS){
                        data.pTradeId = data.busiCode;
                    }
                    data.totleTradeId = data.busiCode;
                    Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                        "98%", "95%", data, function(){});
                }else{
                    label='TrdFundDuration001.reference';
                    pagePath = "invest/period/trdfundduration/TrdFundDuration002/init";
                    data.pageModel = PAGE_MODEL_REFERENCE;
                    //如果是撤销，需要去除末尾的R start
                    if(data.statusValid == CHANCLE_STATUS){
                        data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                        data.pTradeId = data.busiCode;
                    }
                    //如果是撤销，需要去除末尾的R start
                    data.totleTradeId = data.busiCode;
                    Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                        "98%", "95%", data, function(){});
                    if(data.statusValid == CHANCLE_STATUS){
                        data.busiCode = data.busiCode+"R";
                    }
                }
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F03){
                label='TrdPlanInterestDuration002.edit';
                pagePath = "invest/period/trdplanintduration/TrdPlanInterestDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                }
                data.totleTradeId = data.busiCode;
                Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "98%", "95%", data, function(){});//计划类净值委托详情
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode+"R";
                }
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F04){
                label='TrdPlanNetDuration001.reference';
                pagePath = "invest/period/trdplannetduration/TrdPlanNetDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                //如果是撤销，需要去除末尾的R start
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                	data.pTradeId = data.busiCode;
                }
                //如果是撤销，需要去除末尾的R start
                data.totleTradeId = data.busiCode;
                Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "98%", "95%", data, function(){});
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode+"R";
                }
              //存款委托详情
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F05){
                label='TrdDepoDuration001.reference';
                data.adjustDate = Date.ofDate(data.adjustDate).format();
                //1.通过get或取存款的type6
                $.get(basePath+"/invest/period/trdqueryduration/TrdQueryDuration001/getType6?finprodId="+data.finprodId,function(result){
                    var pagePath = "invest/period/trddepoduration/TrdDepoDuration002/init";
                    //2.判断最后的url
                    if(result ==DEPO_ASSET_TYPE_03){
                        var pagePath = "invest/period/trddepoduration/TrdDepoDuration004/init";
                    }
                    else{
                        if(data.paBussType != 41){
                            pagePath = "invest/period/trddepoduration/TrdDepoDuration003/init";
                        }
                    }
                    data.pageModel = PAGE_MODEL_REFERENCE;
                    //如果是撤销，需要去除末尾的R start
                    if(data.statusValid == CHANCLE_STATUS){
                    	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                    	data.pTradeId = data.busiCode;
                    }
                    //如果是撤销，需要去除末尾的R start
                    data.totleTradeId = data.busiCode;
                    Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                        "98%", "95%", data, function(){});
                    if(data.statusValid == CHANCLE_STATUS){
                    	data.busiCode = data.busiCode+"R";
                    }
                });    
            //项目类委托详情
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F06){
                label='项目类资产还本付息-查看';
                pagePath = "invest/period/trdprojduration/TrdProjectDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                }
                data.totleTradeId = data.busiCode;
                Joyin.dialog(label,basePath+pagePath,
                    "98%", "95%", data, function(){});
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode+"R";
                }
            //回购委托详情    
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F07){
                label='回购存续期交易查看';
                pagePath = "invest/period/trdrepoduration/TrdRepoDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                data.totleTradeId = data.busiCode;
                Joyin.dialog(label,basePath+pagePath,
                    "98%", "95%", data, function(){});//拆借查看页面
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F08){
                label='TrdLendDuration001.reference';
                pagePath = "invest/period/trdlendduration/TrdLendDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                //如果是撤销，需要去除末尾的R start
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                	data.pTradeId = data.busiCode;
                }
                //如果是撤销，需要去除末尾的R start
                data.totleTradeId = data.busiCode;
                Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "98%", "95%", data, function(){});
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode+"R";
                }
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F10){
                label='买断式回购存续期交易查看';
                pagePath = "invest/period/trdbuyoutduration/TrdRepoBuyoutDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                //如果是撤销，需要去除末尾的R start
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                }
                //如果是撤销，需要去除末尾的R start
                data.totleTradeId = data.busiCode;
                Joyin.dialog(label,basePath+pagePath,
                    "98%", "95%", data, function(){});
                //恢复
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode+"R";
                }
            }else if(data.finprodType2 == PINAN_ASSET_TYPE_F09){ //
                label='TrdPlanCurrDuration001.reference';
                pagePath = "invest/period/trdplancurrduration/TrdPlanCurrDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                //如果是撤销，需要去除末尾的R start
                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode.substring(0,data.busiCode.length - 1);
                	data.pTradeId = data.busiCode;
                }
                //如果是撤销，需要去除末尾的R start
                data.totleTradeId = data.busiCode;
                Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "98%", "95%", data, function(){});

                if(data.statusValid == CHANCLE_STATUS){
                	data.busiCode = data.busiCode+"R";
                }
             }else if(data.finprodType2 == PINAN_ASSET_TYPE_F11){
                 label='TrdStockBonus001.reference';
                 pagePath = "invest/period/trdstockbonus/TrdStockBonus002/init";
                 data.pageModel = PAGE_MODEL_REFERENCE;
                data.totleTradeId = data.busiCode;
                 Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "98%", "95%", data, function(){});
             }else if(data.finprodType2 == PINAN_ASSET_TYPE_F12){
				label='TrdSecLendDuration002.reference';
				pagePath = "invest/period/trdseclendduration/TrdSecLendDuration002/init";
                data.pageModel = PAGE_MODEL_REFERENCE;
                data.totleTradeId = data.busiCode;
                Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
                    "98%", "95%", data, function(){});
			}else if(data.finprodType2 == PINAN_ASSET_TYPE_F16){
                if(data.paBussType ==123) {
                    label = '利率互换提前终止交易查看';
                    pagePath = "invest/period/trdirsduration/TrdIRSDuration003/init";
                }else{
                    label = '利率互换收付息交易查看';
                    pagePath = "invest/period/trdirsduration/TrdIRSDuration002/init";
                }
                data.totleTradeId = data.busiCode;
                data.pageModel = PAGE_MODEL_REFERENCE;
                Joyin.dialog(label,basePath+pagePath,
                    "98%", "95%", data, function(){});
			}else if(data.finprodType2 == PINAN_ASSET_TYPE_F18){
                if (data.paBussType == 147){
                    label='期权存续期查看';
                    pagePath = "invest/period/trdoptionduration/TrdOptionDuration003/init";
                }else{
                    label='期权存续期查看';
                    pagePath = "invest/period/trdoptionduration/TrdOptionDuration002/init";
                }
                data.totleTradeId = data.busiCode;
                data.pageModel = PAGE_MODEL_REFERENCE;
                Joyin.dialog(label,basePath+pagePath,
                    "98%", "95%", data, function(){});
			}else if(data.paBussType == 137  || data.paBussType == 138){
                data.pageModel = PAGE_MODEL_REFERENCE;
                var title = '融出债券收息还本（收）查看';
                if (data.tradeType == '137') {
                    title = '融入债券付息还本（付）查看'
                }
                data.totleTradeId = data.busiCode;
                Joyin.dialog(title,
                    basePath + "invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration002/init",
                    "98%", "95%", data, function () {
                    });
            }
        },
        cols:[
             {field:'statusValidString',title:'TrdQueryDuration001.statusValid',width:100,sortField:'statusValid'}
            ,{field:'busiCode', title: 'TrdQueryDuration001.tradeId',width:170, sortField:"busiCode"}
            ,{field:'finprodType2String', title:'TrdQueryDuration001.finprodType2',width:170, sortField:"finprodType2"}
            ,{field:'paBussTypeString', title: 'TrdQueryDuration001.paBussTypeString',width:170, sortField:"paBussType"}
        	,{field:'portfolioId', title: 'TrdQueryDuration001.portfolioId',
                render : function(data, type, row, meta) {
                    if(row.portfolioId !=undefined) {
                        return '<a onclick="showPtlManagement(\'' + row.portfolioId
                            + '\')" class="pro-link"  style="cursor: pointer;">' + row.portfolioId + '</a>';
                    }
                }
                ,width:130, sortField:"portfolioId"
            }
        	,{field:'portfolioName',title: 'TrdQueryDuration001.portfolioName',width:140, sortField:"portfolioName"}
            ,{field:'secAcctName', width:130, title: function(){return JoyinUtil.getLabel("TrdQueryDuration001.secAcctId")+'/'+JoyinUtil.getLabel("TrdQueryDuration001.channelId");}(), sortField:'secAcctName'}
        	,{field:'finprodMarketId', title: 'TrdQueryDuration001.finprodMarketId',
                render : function(data, type, row, meta) {
                    if(row.finprodMarketId !=undefined){
                        return "<a onclick=\"showDetailByFinprodId('"+row.finprodId+"','"+row.finprodType2+"')\" + style='cursor: pointer;'>"+row.finprodMarketId+"</a>";
                    }
                }
                ,width:180
            }
        	,{field:'finprodAbbr',title: 'TrdQueryDuration001.finprodAbbr',width:160, sortField:"finprodAbbr"}
            ,{field:'vDate', width:120, title: 'TrdQueryDuration001.vDate',sortField:'vDate'}
            ,{field:'mDate', width:120, title: 'TrdQueryDuration001.mDate',sortField:'mDate'}
        	,{field:'tradeAmt2String', title: 'TrdQueryDuration001.amt',width:150,type:'number', sortField:"tradeAmt",sort: false}
            ,{field:'shareAmt', title: 'TrdQueryDuration001.shareAmt',width:150,type:'number',sortField:"shareAmt"}
        	,{field:'ccyVal', title: 'TrdQueryDuration001.ccy',width:120,sortField:'ccy',sortField:"ccy"}
        	,{field:'tradeDate', title: 'TrdQueryDuration001.tradeDate',width:100,sortField:'tradeDate'}
        	,{field:'remark', title: 'TrdQueryDuration001.remark',width:100,sortField:"remark"}
            ,{field:'ps', visible:false}
            ,{field:'paBussType', visible:false}
            ,{field:'totleTradeId', visible:false}
        ]
    });
    
    
    /**
     * 导出事件
     */
    var exportExcel = function() {
        var paramObj = getPageParams();
        //勾选导出
        var selectedItem = [];
        $.each(table.getAllCheckedRows() ,function (i, item) {
            selectedItem.push(item.busiCode);
        });
        paramObj.selectedItem = selectedItem.join(',');
        Joyin.exportExcel('export', paramObj);
    };

    /**
     * 查询
     */
    var demand = function() {
        var paramObj = getPageParams();//获取参数
        if(paramObj.tradeDate == null || paramObj.tradeDate == ''){
            Joyin.error("资金到账日不允许为空")
            return;
        }else{
            table.load();
        }
    };

    /**
     * 画面加载完成
     */
    $(function() {
    	//搜索
    	$('#TrdQueryDuration001_btn_search').click(function(){
            demand();
    	});
        //导出
        $('#TrdQueryDuration001_btn_export').click(function() {
            exportExcel();
        });
    });
}());