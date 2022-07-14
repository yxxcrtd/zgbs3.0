/**
 * 组合信息查看页面
 */
var showPtlManagement = function (portfolioId) {
    Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"),
        basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
        "95%", "90%", {
            pageModel: PAGE_MODEL_REFERENCE,
            portfolioId: portfolioId,
        });
};
//不同资产的查看页面
var showDetailByFinprodId = function (finprodId, finprodType2) {
    var label = '';
    var pagePath = '';
    //债券标的信息查看
    if (finprodType2 == PINAN_ASSET_TYPE_F01) {
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
        // label = 'FinBondTargetAsset002.title';
        // pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
        // Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
        //     "95%", "90%", {
        //         pageModel: PAGE_MODEL_REFERENCE,
        //         finprodId: finprodId
        //     });
        //基金类标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F02) {
        label = 'FinFundTarget002.detail';
        pagePath = "invest/asset/fund/FinFundTarget002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//计划类利率型标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F03) {
        label = 'FinPlanProfitAsset002.detail';
        pagePath = "invest/asset/plan/planProfit/FinPlanProfitAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//计划类净值型标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F04) {
        label = 'FinPlanNetAsset002.detail';
        pagePath = "invest/asset/plan/planNet/FinPlanNetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//存款标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F05) {
        label = 'FinDepositTarget002.title';
        pagePath = "invest/asset/depo/FinDepositTarget002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//项目类资产标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F06) {
        label = 'FinProjectTargetAsset002.title';
        pagePath = "invest/asset/project/FinProjectTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//回购标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F07) {
        label = '';
        pagePath = "";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });//拆借标的信息查看
    } else if (finprodType2 == PINAN_ASSET_TYPE_F08) {
        label = '';
        pagePath = "";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    } else if (finprodType2 == PINAN_ASSET_TYPE_F10) {
        label = 'FinBondTargetAsset002.title';
        pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    } else if (finprodType2 == PINAN_ASSET_TYPE_F09) {
        label = 'FinPlanAssetC002.title';
        pagePath = "invest/asset/plan/plancur/FinPlanAssetC002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    } else if (finprodType2 == PINAN_ASSET_TYPE_F11) {
        label = 'FinStockInfo002.title';
        pagePath = "invest/asset/stock/FinStockInfo002/init";
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
    } else if (finprodType2 == PINAN_ASSET_TYPE_F19) {
        label = 'FinProjectNetTargetAsset002.reference';
        pagePath = "invest/asset/projectnet/FinProjectNetTargetAsset002/init";
        Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            "95%", "90%", {
                pageModel: PAGE_MODEL_REFERENCE,
                finprodId: finprodId
            });
    }
    //信用保护凭证
    else if(finprodType2 == PINAN_ASSET_TYPE_F37){
        var url = "home.html?a=a&finprodMarketId=" + finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/newCreditProject?a=a";
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
            finprodId: finprodId
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "98%", "98%",
            param);
    }

};

$(function () {
    /**
     * 获取查询参数
     */
    var getPageParams = function () {
        var obj = $.serializeToObject('#TrdInvestDealQuery001_searchForm');
        obj.beginDate = $('#TrdInvestDealQuery001_settleDate').startDate();
        obj.endDate = $('#TrdInvestDealQuery001_settleDate').endDate();
        if (!$("#TrdInvestDealQuery001_finprodType2").val() == '') {
            obj.finprodType2 = $("#TrdInvestDealQuery001_finprodType2").val().join(",");
        }
        if ($("#chlFinprodId").val()) {
            obj.chlFinprodId = $("#chlFinprodId").val().join(",");
        }
        obj.portfolioId = $("#portfolioId").val();
        if (obj.portfolioId) {
            obj.portfolioId = obj.portfolioId.join(",");
        }
        var data = $('#TrdInvestDealQuery001_ps').data().result;
        obj.prodPs = '';
        if (data.length > 0) {
            $.each(data, function (i, v) {
                obj.prodPs += v.id;
            });
        }
        //如果为利率互换交易方向 则转换 后台会转回真实交易方向
        if (obj.ps && $('#TrdInvestDealQuery001_ps').val() && $('#TrdInvestDealQuery001_ps').select2('data')[0].col == IRS_TYPE) {
            obj.ps = IRS_TYPE + obj.ps
        }
        obj.queryType = IS_QUERY_ASSETS_LOSS;
        return obj;
    };

    /**
     * 定义已接收的数据表格
     */

    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function () {
                return getPageParams();
            }
        },
        dblclick: function (table, data) {
            var tradeId = data.tradeId;
            var workflowId = '';
            if (data.workflowId) {
                workflowId = data.workflowId;
            }

            if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && data.ps == PS_T04) {
                label = 'TrdConBond002';
                pagePath = "invest/trade/conbond/TrdConBond002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        busiCode: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && (data.ps == PS_T05 || data.ps == PS_T58)) {
                label = 'TrdConBond002';
                pagePath = "invest/trade/conbond/TrdConBond003/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        busiCode: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && (data.ps == PS_T49)) {
                label = 'TrdBondToStock001.refrence';
                pagePath = "/invest/trade/bondtostock/TrdBondToStock002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        busiCode: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            }
            //债券委托详情 银行间/商业银行柜台
            else if (data.finprodType2 == PINAN_ASSET_TYPE_F01 && (data.tradeMarket == '01' || data.tradeMarket == '02')) {
                console.log(data.tradeMarket);
                var param = {
                    "busiCode": tradeId,
                    "finprodType2": PINAN_ASSET_TYPE_F01
                };
                var url = "home.html?a=a&busiCode=" + tradeId + "&finprodType2=" + PINAN_ASSET_TYPE_F01 +
                    "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //债券委托详情
            // else if (data.finprodType2 == PINAN_ASSET_TYPE_F01) {
            //     console.log("-----------债券委托详情----------------")
            //     label = 'TrdBondInvestTrade002';
            //     pagePath = "invest/trade/bond/TrdBondInvestTrade002/init";
            //     Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
            //         "95%", "90%", {
            //             pageModel: PAGE_MODEL_REFERENCE,
            //             tradeId: tradeId,
            //             workflowId: workflowId,
            //             workflowStatus: data.validStatus
            //         });
            //
            // }
            //基金委托详情
            else if (data.finprodType2 == PINAN_ASSET_TYPE_F02) {
                label = 'TrdFundInvestTrade002';
                /* update by zhangpuyu 2019/12/10 start */
                if (data.ps == PS_T01 || data.ps == PS_T02 || data.ps == PS_T04 || data.ps == PS_T06 || data.ps == PS_T52) {
                    /* update by zhangpuyu 2019/12/10 end */
                    pagePath = "invest/trade/fund/TrdFundInvestTrade002/init";
                } else if (data.ps == PS_T05 || data.ps == PS_T19 || data.ps == PS_T24 || data.ps == PS_T18) {
                    pagePath = "invest/trade/fund/TrdFundInvestTrade003/init";
                }
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: data.tradeId,
                        ps: data.ps,
                        workflowId: workflowId,
                        validStatus: data.validStatus
                    });//计划类利率委托详情
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F03) {
                label = 'TrdPlanRateInvestTrade001';
                pagePath = "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        totleTradeId: tradeId,
                        portfolioId: data.portfolioId
                    });//计划类净值委托详情
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F04) {
                label = 'TrdPlanNetInvestTrade001';
                if (data.ps == PS_T04 || data.ps == PS_T06) {
                    pagePath = "invest/trade/plannet/TrdPlanNetInvestTrade002/init";
                } else {
                    pagePath = "invest/trade/plannet/TrdPlanNetInvestTrade003/init";
                }

                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: data.tradeId,
                        ps: data.ps,
                        workflowId: workflowId,
                        validStatus: data.validStatus
                    });//存款委托详情
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F05) {
                label = 'TrdDepositInvestTrade001';
                pagePath = "invest/trade/trddepositinvesttrade/TrdDepositInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        portfolioId: data.portfolioId
                    });//项目类委托详情
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F06) {
                label = 'TrdProjectInvestTrade002.view';
                pagePath = "invest/trade/project/TrdProjectInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        bizCode: tradeId,
                        statusValid: data.validStatus,
                        workflowId: workflowId
                    });
            }
            //回购委托详情 银行间/商业银行柜台
            else if (data.finprodType2 == PINAN_ASSET_TYPE_F07 && (data.tradeMarket == '01' || data.tradeMarket == '02')) {
                console.log("----------PINAN_ASSET_TYPE_F07-------------");
                console.log(data.tradeMarket);
                var param = {
                    "busiCode": tradeId,
                    "finprodType2": PINAN_ASSET_TYPE_F07
                };
                var url = "home.html?a=a&busiCode=" + tradeId + "&finprodType2=" + PINAN_ASSET_TYPE_F07 +
                    "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //回购委托详情
            else if (data.finprodType2 == PINAN_ASSET_TYPE_F07) {
                label = 'TrdRepoTrade002';
                pagePath = "invest/trade/trdrepotrade/TrdRepoTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });//拆借查看页面
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F08) {
                label = 'TrdLendInvestTrade002';
                pagePath = "invest/trade/lend/TrdLendInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F10) {
                label = 'TrdOutRightRepoTrade002.reference';
                pagePath = "invest/trade/buyoutrepo/TrdOutRightRepoTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F09) {
                label = 'TrdPlanCcyInvestTrade001.reference';
                if (data.ps == PS_T04 || data.ps == PS_T06) {
                    pagePath = "invest/trade/planccy/TrdPlanCcyInvestTrade002/init";
                } else {
                    pagePath = "invest/trade/planccy/TrdPlanCcyInvestTrade003/init";
                }
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: data.tradeId,
                        ps: data.ps,
                        workflowId: workflowId,
                        validStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F11) {
                label = 'TrdStock002.reference';
                pagePath = "invest/trade/stockdeal/TrdStock002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F12) {
                label = 'TrdBondLendInvestTrade002.reference';
                pagePath = "invest/trade/bondlend/TrdBondLendInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F13) {
                label = 'SysCommon.refrence';
                pagePath = "invest/trade/spotex/TrdSpotexInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F14) {
                label = 'TrdForwexInvestTrade002.reference';
                pagePath = "invest/trade/forwex/TrdForwexInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F15) {
                label = 'SysCommon.refrence';
                pagePath = "invest/trade/trdfxswapinvesttrade/TrdFxswapInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus,
                        tradeStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F16) {
                label = 'TrdIRSTrade002.reference';
                pagePath = "invest/trade/irs/TrdIRSTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            }
            //一级投标债券 银行间/商业银行柜台
            else if (data.finprodType2 == PINAN_ASSET_TYPE_F17 && (data.tradeMarket == '01' || data.tradeMarket == '02')) {
                console.log("----------PINAN_ASSET_TYPE_F17-------------");
                console.log(data.tradeMarket);
                var param = {
                    "busiCode": tradeId,
                    "finprodType2": PINAN_ASSET_TYPE_F17
                };
                var url = "home.html?a=a&busiCode=" + tradeId + "&finprodType2=" + PINAN_ASSET_TYPE_F17 +
                    "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + url,
                    "95%", "90%",
                    param);
            }
            //一级投标债券
            else if (data.finprodType2 == PINAN_ASSET_TYPE_F17) {
                label = 'SysCommon.refrence';
                pagePath = "invest/trade/bond/TrdBondInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F18) {
                label = 'TrdOptionTrade002';
                pagePath = "invest/trade/option/TrdOptionTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            } else if (data.finprodType2 == PINAN_ASSET_TYPE_F19) {
                label = 'TrdProjectNetInvestTrade002.reference';
                pagePath = "invest/trade/projectnet/TrdProjectNetInvestTrade002/init";
                Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
                    "95%", "90%", {
                        pageModel: PAGE_MODEL_REFERENCE,
                        tradeId: tradeId,
                        workflowId: workflowId,
                        workflowStatus: data.validStatus
                    });
            }

        },
        cols: [
            {field: 'validStatusVal', title: 'TrdInvestDealQuery001.statusValid', width: 100, sortField: 'validStatus'}
            , {field: 'tradeId', title: 'TrdInvestDealQuery001.tradeId', width: 170}
            , {field: 'entrustId', title: 'TrdInvestDealQuery001.entrustId', width: 170}
            , {
                field: 'portfolioId', title: 'TrdInvestDealQuery001.portfolioId',
                render: function (data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">' + row.portfolioId + '</a>';
                }
                , width: 130
            }
            , {field: 'portfolioName', title: 'TrdInvestDealQuery001.portfolioName', width: 140}
            , {
                field: 'finprodType2Val',
                title: 'TrdInvestDealQuery001.finprodType2',
                width: 140,
                sortField: 'finprodType2'
            }
            , {
                field: 'finprodMarketId', title: 'TrdInvestDealQuery001.finprodMarketId',
                render: function (data, type, row, meta) {
                    if (row.finprodMarketId != undefined && row.finprodType2 != 'F07') {
                        return "<a onclick=\"showDetailByFinprodId('" + row.finprodId + "','" + row.finprodType2 + "')\" + style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                    }
                }
                , width: 180
            }
            , {field: 'finprodAbbr', title: 'TrdInvestDealQuery001.finprodAbbr', width: 160}
            , {field: 'chlFinprodId', title: 'TrdInvestDealQuery001.chlFinprodId', visible: false, width: 160}
            , {field: 'psVal', title: 'TrdInvestDealQuery001.ps', width: 100, sortField: 'ps'}
            , {
                field: 'tradeMarketVal',
                title: 'TrdInvestDealQuery001.tradeMarket',
                width: 130,
                sortField: 'tradeMarket'
            }
            , {
                field: 'tradeMarketModeVal',
                title: 'TrdInvestDealQuery001.tradeMarketMode',
                width: 100,
                sortField: 'tradeMarketMode'
            }
            , {field: 'tradeAmt', title: 'TrdInvestDealQuery001.amt', width: 150, type: 'number'}
            , {
                field: 'tradePrice',
                title: 'TrdInvestDealQuery001.tradePrice',
                width: 150,
                type: 'number',
                sortField: 'unitCprice'
            }
            , {field: 'rate', title: 'TrdInvestDealQuery001.rate', width: 150, type: 'number'}
            , {field: 'ccyVal', title: 'TrdInvestDealQuery001.ccy', width: 120, sortField: 'ccy'}
            , {field: 'tradeDateVal', title: 'TrdInvestDealQuery001.tradeDate', width: 100, sortField: 'tradeDate'}
            , {field: 'settleDateVal', title: 'TrdInvestDealQuery001.settleDate', width: 100, sortField: 'settleDate'}
            , {field: 'remark', title: 'TrdInvestDealQuery001.remark', width: 100}
            , {field: 'counterName', title: '交易对手', width: 100}
            , {field: 'isOutTradeStr', sortField: 'isOutTrade', title: '是否转仓', width: 100}
            , {field: 'fTradeId', sortField: 'fTradeId', width: 180, title: '银行间成交编号'}
            , {field: 'finprodType2', visible: false}
            , {field: 'finprodId', visible: false}
        ],
        initSort: {field: 'tradeDate', type: 'desc'}
    });


    /**
     * 导出事件
     */
    var exportExcel = function () {
        var paramObj = getPageParams();
        //勾选导出
        var selectedItem = [];
        $.each(table.getAllCheckedRows(), function (i, item) {
            selectedItem.push(item.tradeId);
        });
        paramObj.selectedItem = selectedItem.join(',');
        Joyin.exportExcel('export', paramObj);
    };


    var newSingleExportExcel = function () {
        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }

//        var paramObj = {};
//        //勾选导出
//        var selectedItem = [];
//        $.each(table.getAllCheckedRows() ,function (i, item) {
//            selectedItem.push(item.tradeId);
//        });
//        paramObj.tradeIds = selectedItem.join(',');


        var param = {selectedItem: selArr, operateType: "singleExport"};
        Joyin.ajaxJson(basePath + 'invest/trade/investtrade/investdealquery/TrdInvestDealQuery001/check', param, function (result) {
            Joyin.exportExcel('newSingleExportExcel', {dtoStr: JSON.stringify(param)});
        });

    };

    var newBatchExportExcel = function () {

        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }

//    	var paramObj = {};
//        //勾选导出
//        var selectedItem = [];
//        $.each(table.getAllCheckedRows() ,function (i, item) {
//            selectedItem.push(item.tradeId);
//        });
//        paramObj.tradeIds = selectedItem.join(',');

        var param = {selectedItem: selArr, operateType: "batchExport"};

        Joyin.ajaxJson(basePath + 'invest/trade/investtrade/investdealquery/TrdInvestDealQuery001/check', param, function (result) {
            Joyin.exportExcel('newBatchExportExcel', {dtoStr: JSON.stringify(param)});
        });

    };

    /**
     * 【吉林银行个性化代码】
     * add by wangyang1
     * 2019年9月23日
     * 导出单笔导出转移定价审批表
     */
    var singleExportExcel = function () {
        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        var param = {selectedItem: selArr, operateType: "singleExport"};
        Joyin.ajaxJson(basePath + 'invest/trade/investtrade/investdealquery/TrdInvestDealQuery001/check', param, function (result) {
            Joyin.exportExcel(basePath + "invest/trade/investtrade/investdealquery/TrdInvestDealQuery001/singleExport", {dtoStr: JSON.stringify(param)});
        });
    };

    /**
     * 【吉林银行个性化代码】
     * add by wangyang1
     * 2019年9月23日
     * 导出单笔导出转移定价审批表
     */
    var batchExportExcel = function () {
        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        var param = {selectedItem: selArr, operateType: "batchExport"};
        Joyin.ajaxJson(basePath + 'invest/trade/investtrade/investdealquery/TrdInvestDealQuery001/check', param, function (result) {
            Joyin.exportExcel(basePath + "invest/trade/investtrade/investdealquery/TrdInvestDealQuery001/batchExportExcel", {dtoStr: JSON.stringify(param)});
        });
    };

    /**
     * 导出pdf add by zengchengfa 20210803
     */
    var exportPdf = function () {
        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
            // 列表中未勾选
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var paramObj = getPageParams();
            //勾选导出
            var selectedItem = [];
            $.each(table.getAllCheckedRows(), function (i, item) {
                selectedItem.push(item.tradeId);
            });
            paramObj.selectedItem = selectedItem.join(',');
            Joyin.exportExcel('exportPdf', paramObj);
        } else {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
        }
    };
    /**
     * 画面加载完成
     */
    $(function () {
        //搜索
        $('#TrdInvestDealQuery001_btn_search').click(function () {
            table.load();
        });
        //导出
        $('#TrdInvestDealQuery001_btn_export').click(function () {
            exportExcel();
        });
        /**
         * 【吉林银行个性化代码】
         * add by wangyang1
         * 2019年9月23日
         */
        // $('#TrdInvestDealQuery001_btn_singleExport').click(function() {
        // 	newSingleExportExcel();
        // });
        /**
         * 【吉林银行个性化代码】
         * add by wangyang1
         * 2019年9月23日
         */
        // $('#TrdInvestDealQuery001_btn_batchExport').click(function() {
        //     newBatchExportExcel();
        // });

        //add by zengchengfa 20210803 pdf导出
        // 	$('#TrdInvestDealQuery001_btn_pdfExport').click(function() {
        // 		exportPdf();
        // 	});
    });

}());