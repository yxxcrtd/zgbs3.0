(function() {
    var init = function () {
        //初始化，产生树结构
        var preUrl=getAllRelationUrl+"?";
        $("#treePorfolio").attr('url',preUrl+
            'mdate='+$("#AssetsTransfer001_mdate").val());
        searchTreeSingle();
    };

    function searchTreeSingle() {
        $('#treePorfolio').joyinMixTree({
            onTreeLoadSuccess: function () {
                $("div.mixtree-div").append('<a class="refreshAicon" ><i class="iconfont icon-refresh"></i>&nbsp;刷新</a>');
                $.fn.zTree.getZTreeObj("treePorfolio_tree").setting.callback.onClick=function(event, treeId, treeNode){
                    event.stopPropagation();
                    if(treeNode.level==1){
                        // var bd = $("div.condition-area");
                        // bd.find('#portfolioCode').val(treeNode.id);
                        // bd.find('#portfolioName').val(treeNode.name);
                        $('#portfolioCode').val(treeNode.id);
                        $('#portfolioName').val(treeNode.name);
                        $('#AssetsTransfer001_btn_search').click();
                    }
                };//触发函数
                //绑定组合刷新事件
                $("div.mixtree-div").find('a.refreshAicon').click(function(){
                    event.stopPropagation();
                    $('#AssetsTransfer001_btn_portofioSearch').click();
                    $('#portfolioCode').val('');
                    $('#portfolioName').val('');
                });
            }
        });
    }

    /**
     * 获取待转仓列表查询参数
     */
    var getUnTransSearchParams = function() {
        var obj = $.serializeToObject('#searchForm');
        obj.portfolioCode = $('#portfolioCode').val();
        if($("#AssetsTransfer001_assetType").val()){
            obj.assetType = $("#AssetsTransfer001_assetType").val().join(",");
        };
        if($("#AssetsTransfer001_assetId").val()){
            obj.assetId = $("#AssetsTransfer001_assetId").val().join(",");
        };
        return obj;
    };

    /**
     * 获取已转仓列表查询参数
     */
    var getTransSearchParams = function() {
        var obj = {
           assetType : $('#assetType').val() ? $('#assetType').val().join(",") : "",
           tradeDate : $('#tradeDate').val(),
           tradeId : $('#tradeId').val(),
           status : $('#status').val()
       };
       if($("#portfolioId").val()){
           obj.portfolioId = $("#portfolioId").val().join(",");
       }
       if($('#finprodId').select2('data')){
           var finprodId = [];
           $.each($('#finprodId').select2('data'),function (idx,value) {
               finprodId.push(value.object);
           });
           obj.finprodId = finprodId.join(",");
       }
       return obj;
    };
    /**
     * 定义待转仓数据表格
     */
    var unTransferTable = $("#unTransferTable").joyintable({
        ajax: {
            url:  basePath + "invest/trade/transfer/assetstransfer/AssetsTransfer001/findUnTransPage",
            type: 'POST',
            data: function() {
                return getUnTransSearchParams();
            }
        },

        cols:[
            {field:'tradeId', width:150 , title:"业务编号"}
            ,{field:'portfolioId', width:150 , title:"组合代码",
                render : function(data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">'+row.portfolioId+'</a>';
                }}
            ,{field:'portfolioName', width:150 , title: '组合名称'}
            ,{field:'assetTypeVal', width:150 , title: '资产类型',sortField:'assetType'}
            ,{field:'finprodMarketId', title: '资产代码/标的资产代码',
                render : function(data, type, row, meta) {
                    if(row.finprodMarketId !=undefined){
                        return "<a onclick=\"showDetailByFinprodId('"+row.finprodId+"','"+row.finprodMarketId+"','"+row.assetType+"')\" + style='cursor: pointer;'>"+row.finprodMarketId+"</a>";
                    }
                }
                ,width:150}
            ,{field:'finprodName', width:150 , title: '资产名称/标的资产简称'}
            // 通道隐藏  by gaoshuqi 20210721
           /* ,{field:'chlName', width:150 , title: '通道'}*/
            ,{field:'secAcctName', width:150 , title: '证券账户'}
            ,{field:'invAimVal', width:150 , title: '投资目的',sortField:'invAim'}
            ,{field:'vdate', width:150 , title: '起息日',type:'date'}
            ,{field:'mdate', width:150 , title: '到期日',type:'date'}
            ,{field:'backLendDirectionVal', width:150 , title: '交易方向',sortField:'backLendDirection'}
            ,{field:'days', width:150 , title: '剩余天数',type:'number'}
            ,{field:'yield', width:150 , title: '利率/收益率（%）',type:'number', digit: 4}
            ,{field:'amount', width:150 , title: '券面总额',type:'number'}
            ,{field:'printAmt', width:150 , title: '本金金额',type:'number'}
            ,{field:'posAmt', width:150 , title: '持仓数量/持仓份额',type:'number'}
            ,{field:'chlId', visible : false}
            ,{field:'secAcctId', visible : false}
            ,{field:'finprodType2', visible : false}
            ,{field:'finprodId', visible : false}
            ,{field:'profitType', visible : false}
            ,{field:'assetType', visible : false}
            ,{field:'calendarId', visible : false}
            ,{field:'ccy', visible : false}
            ,{field:'invAim', visible : false}
            ,{field:'ps', visible : false}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit']
                            ,render: function (data, type, row, meta) {
                                return '<i class="btn iconfont edit icon-circle-correct_fill" title="确认"></i>';}
                        }
        ],
        done: function() {
            //列表刷新 选中值可能被更新，刷新下拉框
            var self = this;
            //监听表格复选框选择
            self.layTable.on('checkbox('+ 'unTransferTable'+')', function (obj) {
                afterUnTransferTableCheckRow();
            })
        }
    });

    /**
     * 绑定转仓事件
     */
    unTransferTable.bind('click', '.edit', function(data, index) {
        var confirmTrade = function (data, PAGE_MODEL_EDIT) {
            data.pageModel = PAGE_MODEL_EDIT;
            if(data.assetType == PINAN_ASSET_TYPE_F03) {
                Joyin.dialog("计划类资产（利率型）交易转仓",
                    basePath + "/invest/trade/transfer/planratetransfer/TrdPlanRateInvestTradeTransfer001/init",
                    "98%", "95%", data, function () {
                	unTransferTable.reload();
                    });
            }
            else if(data.assetType == PINAN_ASSET_TYPE_F05) {
                Joyin.dialog("存款类交易转仓",
                    basePath + "/invest/trade/transfer/deposittransfere/DepositTransfere001/init",
                    "98%", "95%", data, function () {
                	unTransferTable.reload();
                    });
            }
            //净值型计划转仓
            else if(data.assetType == PINAN_ASSET_TYPE_F04){
                var object = data;
                object.undistributedAmount = data.posAmt;
                object.finprodAbbr = data.finprodName;
                Joyin.dialog("净值型计划转仓",'/invest/trade/transfer/netplantransfer/NetPlanTransfer001/init',
                    "98%", "95%", object, function () {});
            }else if(data.assetType == PINAN_ASSET_TYPE_F06) {
            	Joyin.dialog("项目类资产交易转仓",
    					basePath + "/invest/trade/transfer/trdassetsProject/TrdAssetsProject001/init",
    					"98%", "95%", data, function () {
            		unTransferTable.reload();
    					});
            }else if(data.assetType == PINAN_ASSET_TYPE_F07) {
            	Joyin.dialog("质押式回购交易转仓",
    					basePath + "/invest/trade/transfer/trdrolloverpledgedrepo/TrdRolloverPledgedRepo001/init",
    					"98%", "95%", data, function () {
            		unTransferTable.reload();
    					});
            }else if(data.assetType == PINAN_ASSET_TYPE_F02) {
            	Joyin.dialog("基金类资产交易转仓",
    					basePath + "/invest/trade/transfer/fund/FundAssetTransfer001/init",
    					"98%", "95%", data, function () {
    					});
            }else if(data.assetType == PINAN_ASSET_TYPE_F01) {
            	Joyin.dialog("债券类资产交易转仓",
    					basePath + "/invest/trade/transfer/sectransfer/SecTransfer001/init",
    					"98%", "95%", data, function () {
            		unTransferTable.reload();
    					});
            }
            else if(data.assetType == PINAN_ASSET_TYPE_F10) {
                Joyin.dialog("买断式回购转仓",
                    basePath + "/invest/trade/transfer/buyoutrepo/BuyOutRepoTransfer001/init",
                    "98%", "95%", data, function () {
                	unTransferTable.reload();
                    });
            }
            else if(data.assetType == PINAN_ASSET_TYPE_F08) {
                Joyin.dialog("拆借资产转仓",
                    basePath + "/invest/trade/transfer/lendtransfer/LendTransfer001/init",
                    "98%", "95%", data, function () {
                	unTransferTable.reload();
                    });
            }
        };
        Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/checkTrd', data, function(result1) {
        	confirmTrade(data, PAGE_MODEL_NEW);
        });
    });

    /**
     * 列表复选框选中事件
     */
    var afterUnTransferTableCheckRow = function(){
        var checkRows = unTransferTable.getAllCheckedRows();
        if(checkRows && checkRows.length >0){
            var list = [];
            // key容器 防止重复key放入
            var keyMap = new Map();
            //循环选中的数据 处理关键字段入map
            $.each(checkRows,function (i,v) {
                keyMap.set(v.finprodMarketId,v.finprodName);
            });
            //获取所有的map key 处理成目标待选项
            var keyList = keyMap.keys();
            // 处理待选项 并赋值到控件
            for(let k of keyList){
                list.push(k);
            }
            $("#finprodId").val(list).trigger("change");
        }
        else{
        //下拉框清空
            $("#finprodId").val("").trigger("change");
        }
    }
    /**
     * 绑定列表复选框
     */
    // unTransferTable.bind("click",".laytable-cell-checkbox",afterUnTransferTableCheckRow);

    /**
     * 定义已转仓数据表格
     */
    var transferTable = $("#transferTable").joyintable({
        ajax: {
            url:  basePath + "invest/trade/transfer/assetstransfer/AssetsTransfer001/findTransPage",
            type: 'POST',
            data: function() {
                return getTransSearchParams();
            }
        },

        cols:[
            {field:'statusVal', width:150 , title:"转仓状态",sortField:'status'}
            ,{field:'tradeId', width:150 , title:"业务编号"}
            ,{field:'totleTradeId', visible : false}
            ,{field:'portfolioId', width:150 , title:"组合代码",
                render : function(data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">'+row.portfolioId+'</a>';
                }}
            ,{field:'portfolioName', width:150 , title: '组合名称'}
            ,{field:'assetTypeVal', width:150 , title: '资产类型',sortField:'assetType'}
            ,{field:'finprodMarketId', width:150 , title: '资产代码/标的资产代码',
                render : function(data, type, row, meta) {
                    if(row.finprodMarketId !=undefined){
                        return "<a onclick=\"showDetailByFinprodId('"+row.finprodId+"','"+row.finprodMarketId+"','"+row.assetType+"')\" + style='cursor: pointer;'>"+row.finprodMarketId+"</a>";
                    }
                }}
            ,{field:'finprodName', width:150 , title: '资产名称/标的资产简称'}
            // 通道隐藏  by gaoshuqi 20210721
           /* ,{field:'chlName', width:150 , title: '通道'}*/
            ,{field:'tradeDate', width:150 , title: '交易日期',type:'date'}
            ,{field:'psVal', width:150 , title: '交易方向',sortField:'ps'}
            ,{field:'tradeMarketVal', width:150 , title: '交易场所',sortField:'tradeMarket'}
            ,{field:'ccyVal', width:150 , title: '币种',sortField:'ccy'}
            ,{field:'netPrice', width:150 , title: '净价/单位净值/交易价格',type:'number',
            	render : function(data, type, row, meta) {
                    if(row.netPrice !=undefined){
                    	if(row.assetTypeVal == '债券'){
                    		return Number(row.netPrice).toFixed(6);
                    	}else if(row.assetTypeVal == '基金' || row.assetTypeVal == '计划类资产(净值型)'){
                    		return Number(row.netPrice).toFixed(4);
                    	}
                    }
                }}
            ,{field:'yield', width:150 , title: '利率/收益率（%）',type:'number'}
            ,{field:'amount', width:150 , title: '券面总额',type:'number'}
            ,{field:'printAmt', width:150 , title: '本金金额',type:'number'}
            ,{field:'tradeAmt', width:150 , title: '交易数量/交易份额',type:'number'}
            ,{field:'chlId', visible : false}
            ,{field:'finprodId', visible : false}
            ,{field:'ccy', visible : false}
            ,{field:'status', visible : false}
            ,{field:'ps', visible : false}
            ,{field:'assetType', visible : false}
            ,{field:'type6', visible : false}
            ,{field:'backLendDirection', visible : false}
            //add by liuwei for huaxing 20200310 start
            ,{field:'finprodType2', visible : false}
            //add by liuwei for huaxing 20200310 end
        ]

    });

    /**
     * 撤销
     */
    function revoke() {
        if (transferTable != null && transferTable != '') {
            var selArr = transferTable.getAllCheckedRows();
        }
        // 列表中未勾选
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        // 列表中勾选对象1条以上时
        if (selArr.length > 1) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
            return;
        } else if (selArr[0].status != TRADE_STATUS_02) {
            Joyin.error("转仓状态为有效的才能撤销！");
            return;
        }
        Joyin.ajaxJson(basePath + 'invest/trade/transfer/assetstransfer/AssetsTransfer001/revokeCheck', selArr[0],function (result) {
            if(result && result.statuCode == 200){
                //存款类资产撤销
                if (selArr[0].assetType == PINAN_ASSET_TYPE_F05) {
                    var object = {selectedItem: selArr, operateType: "delete"};//请求参数
                    object.selectedItem[0].elementId = "TrdDepositInvestTrade001_btn_revoke";
                    object.finprodId = selArr[0].finprodId;
                    object.tradeDate = selArr[0].tradeDate;
                    object.portfolioId= selArr[0].portfolioId;
                    Joyin.ajaxJson(basePath + 'invest/trade/trddepositinvesttrade/TrdDepositInvestTrade001/check', object, function (result) {//数据验证
                        Joyin.prompt(JoyinUtil.getLabel('TrdDepositInvestTrade001.cancelText'), 0, function (result) {
                            for (var select in object.selectedItem) {
                                object.selectedItem[select].cancelRemark = result;
                            }
                            WorkFlow.doRevokeList(basePath + 'invest/trade/trddepositinvesttrade/TrdDepositInvestTrade001/delete', object, "TrdDepositInvestTrade001_btn_revoke", "TrdDepositInvestTrade002");//删除
                        });
                    });
                }
                //债券类资产撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F01) {
                    var checkObj = {};
                    checkObj.tradeId = selArr[0].tradeId;
                    checkObj.totleTradeId = selArr[0].totleTradeId;
                    checkObj.systemFinprodId = selArr[0].finprodId;
                    checkObj.tradeDate = selArr[0].tradeDate;
                    checkObj.operateType = "revoke";
                    Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade001/checkRevoke', checkObj, function (result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                            checkObj.revokeReason = result;
                            WorkFlow.doRevokeList(basePath + 'invest/trade/bond/TrdBondInvestTrade001/doRevoke', checkObj, "TrdBondInvestTrade001_btn_revoke", "TrdBondInvestTrade002");
                        });
                    });
                }
                //基金类资产撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F02) {
                    var object = {selectedItem: selArr, operateType: "revoke"};
                    object.selectedItem[0].elementId = "TrdFundInvestTrade001_btn_revoke";
                    var obj = object;
                    if (object.selectedItem[0].ps == ps_T19 || object.selectedItem[0].ps == ps_T05) {
                        obj.selectedItem[0].bTradeId = object.selectedItem[0].tradeId;
                        Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade001/check2', obj, function (result1) {
                            Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade001/check', object, function (result1) {
                                Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.revoke")), 0, function (result2) {
                                    object.cancelRemark = result2;
                                    WorkFlow.doRevokeList(basePath + 'invest/trade/fund/TrdFundInvestTrade001/revoke', object, "TrdFundInvestTrade001_btn_revoke", "TrdFundInvestTrade003");
                                });
                            });
                        });
                    } else {
                        Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade001/check', object, function (result1) {
                            Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.revoke")), 0, function (result2) {
                                object.cancelRemark = result2;
                                WorkFlow.doRevokeList(basePath + 'invest/trade/fund/TrdFundInvestTrade001/revoke', object, "TrdFundInvestTrade001_btn_revoke", "TrdFundInvestTrade002");
                            });
                        });
                    }
                }
                //计划类净值型资产撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F04) {
                    //净值型
                    var object = {selectedItem: selArr, operateType: "revoke"};
                    object.selectedItem[0].elementId = "TrdPlanNetInvestTrade001_btn_revoke";
                    var obj = object;
                    if (object.selectedItem[0].ps == ps_T19 || object.selectedItem[0].ps == ps_T05) {
                        obj.selectedItem[0].bTradeId = object.selectedItem[0].tradeId;
                        obj.selectedItem[0].busiType = BUS_TYPE;
                        Joyin.ajaxJson(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade001/check2', obj, function (result1) {
//                    Joyin.ajaxJson(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade001/check', object, function(result1) {
                            Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.revoke")), 0, function (result2) {
                                object.cancelRemark = result2;
                                WorkFlow.doRevokeList(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade001/revoke', object, "TrdPlanNetInvestTrade001_btn_revoke", "TrdPlanNetInvestTrade003");
                            });
//                    });
                        });
                    } else {
                        Joyin.ajaxJson(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade001/check', object, function (result1) {
                            Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.revoke")), 0, function (result2) {
                                object.cancelRemark = result2;
                                WorkFlow.doRevokeList(basePath + 'invest/trade/plannet/TrdPlanNetInvestTrade001/revoke', object, "TrdPlanNetInvestTrade001_btn_revoke", "TrdPlanNetInvestTrade002");
                            });
                        });
                    }
                }
                //计划类利率型资产撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F03) {
                    var object = {selectedItem: selArr, operateType: "delete"};//请求参数
                    object.selectedItem[0].elementId = "TrdPlanRateInvestTrade001_btn_delete";
                    Joyin.ajaxJson(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade001/check', object, function (result) {//数据验证
//                Joyin.confirm(JoyinUtil.getMessage('COMMON_C_0001',JoyinUtil.getMessage(JoyinUtil.getLabel("SysCommon.delete"))), function(){
                        Joyin.prompt(JoyinUtil.getLabel('TrdPlanRateInvestTrade001.cancelText'), 0, function (result) {
                            for (var select in object.selectedItem) {
                                object.selectedItem[select].cancelRemark = result;
                            }
                            WorkFlow.doRevokeList(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade001/delete', object, "TrdPlanRateInvestTrade001_btn_delete", "TrdPlanRateInvestTrade002");//删除
//                    WorkFlow.doDeleteList(basePath + 'invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade001/delete', object, "TrdPlanRateInvestTrade001_btn_delete", "TrdPlanRateInvestTrade002");//删除
                        });
                    });
                }
                //计划类货币型资产撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F09) {
                    var object = {selectedItem: selArr, operateType: "revoke"};
                    object.selectedItem[0].elementId = "TrdPlanCcyInvestTrade001_btn_revoke";
                    var obj = object;
                    if (object.selectedItem[0].ps == ps_T19 || object.selectedItem[0].ps == ps_T05) {
                        obj.selectedItem[0].bTradeId = object.selectedItem[0].tradeId;
                        Joyin.ajaxJson(basePath + 'invest/trade/planccy/TrdPlanCcyInvestTrade001/check2', obj, function (result1) {
                            Joyin.ajaxJson(basePath + 'invest/trade/planccy/TrdPlanCcyInvestTrade001/check', object, function (result1) {
                                Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.revoke")), 0, function (result2) {
                                    object.cancelRemark = result2;
                                    WorkFlow.doRevokeList(basePath + 'invest/trade/planccy/TrdPlanCcyInvestTrade001/revoke', object, "TrdPlanCcyInvestTrade001_btn_revoke", "TrdPlanCcyInvestTrade002");
                                });
                            });
                        });
                    } else {
                        Joyin.ajaxJson(basePath + 'invest/trade/planccy/TrdPlanCcyInvestTrade001/check', object, function (result1) {
                            Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.revoke")), 0, function (result2) {
                                object.cancelRemark = result2;
                                WorkFlow.doRevokeList(basePath + 'invest/trade/planccy/TrdPlanCcyInvestTrade001/revoke', object, "TrdPlanCcyInvestTrade001_btn_revoke", "TrdPlanCcyInvestTrade002");
                            });
                        });
                    }
                }
                //项目类类资产撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F06) {
                    var object = selArr[0];
                    object.operateType = "revoke";
                    object.isTransfer = true;
                    object.bizCode = selArr[0].totleTradeId;
                    object.statusValid = "02";//有效状态为有效
                    object.elementId = "TrdProjectInvestTrade001_btn_revoke";
                    Joyin.ajax(basePath + 'invest/trade/project/TrdProjectInvestTrade001/revokeCheck', object, function (result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                            object.revokeReason = result;
                            WorkFlow.doRevoke(basePath + 'invest/trade/project/TrdProjectInvestTrade001/revoke', object, "TrdProjectInvestTrade001_btn_revoke", "TrdProjectInvestTrade002");
                        });
                    });
                }
                //质押式回购撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F07) {
                    var checkObj = {};
                    checkObj.tradeId = selArr[0].tradeId;
                    checkObj.ps = selArr[0].ps;
                    checkObj.repoType = selArr[0].type6;
                    checkObj.operateType = "revoke";
                    checkObj.portfolioId = selArr[0].portfolioId;
                    checkObj.tradeDate = selArr[0].tradeDate;
                    checkObj.finprodType2 = selArr[0].finprodType2;//add by liuwei for huaxing 20200310
                    Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade001/checkRevoke', checkObj, function (result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                            checkObj.revokeReason = result;
                            WorkFlow.doRevokeList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade001/doRevoke', checkObj, "TrdRepoTrade001_btn_revoke", "TrdRepoTrade002");
                        });
                    });
                }
                //买断式回购撤销
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F10) {
                    var checkObj = {};
                    checkObj.tradeId = selArr[0].tradeId;
                    checkObj.operateType = "revoke";
                    Joyin.ajaxJson(basePath + 'invest/trade/buyoutrepo/TrdOutRightRepoTrade001/checkRevoke', checkObj, function (result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                            checkObj.revokeReason = result;
                            checkObj.ps = selArr[0].backLendDirection;
                            checkObj.tradeDate = selArr[0].tradeDate;
                            checkObj.finprodId = selArr[0].finprodId;
                            checkObj.portfolioId = selArr[0].portfolioId;
                            checkObj.secTotalPrin = selArr[0].amount;
                            WorkFlow.doRevokeList(basePath + 'invest/trade/buyoutrepo/TrdOutRightRepoTrade001/doRevoke', checkObj, "TrdOutRightRepoTrade001_btn_revoke", "TrdOutRightRepoTrade002");
                        });
                    });
                }
                //拆借撤回
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F08) {
                    var checkObj = selArr[0];
                    checkObj.tradeId = selArr[0].tradeId;
                    checkObj.tradeDate = selArr[0].tradeDate;
                    checkObj.paBussType = selArr[0].type6;
                    checkObj.operateType = "revoke";
                    checkObj.elementId = "TrdLendInvestTrade001_btn_revoke";
                    Joyin.ajaxJson(basePath + 'invest/trade/lend/TrdLendInvestTrade001/checkRevoke', checkObj, function(result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                            checkObj.revokeReason = result;
                            WorkFlow.doRevokeList(basePath + 'invest/trade/lend/TrdLendInvestTrade001/doRevoke', checkObj, "TrdLendInvestTrade001_btn_revoke", "TrdLendInvestTrade002");
                        });
                    });
                }
                //股票撤回
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F11) {
                    var checkObj = {};
                    checkObj.tradeId = selArr[0].tradeId;
                    checkObj.finprodName = selArr[0].finprodName;
                    checkObj.tradeDate = selArr[0].tradeDate;
                    checkObj.operateType = "revoke";
                    checkObj.ps = selArr[0].ps;
                    Joyin.ajaxJson(basePath+ 'invest/trade/stockdeal/TrdStock001/checkRevoke',checkObj,function(result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"),0,function(result) {
                            checkObj.revokeReason = result;
                            WorkFlow.doRevokeList(basePath+ 'invest/trade/stockdeal/TrdStock001/doRevoke',checkObj,"TrdStock001_btn_revoke","TrdStock002");
                        });
                    });
                }
                //债券借贷撤回
                else if (selArr[0].assetType == PINAN_ASSET_TYPE_F12) {
                    var checkObj = {};
                    checkObj.tradeId = selArr[0].tradeId;
                    checkObj.finprodName = selArr[0].finprodName;
                    checkObj.tradeDate = selArr[0].tradeDate;
                    checkObj.operateType = "revoke";
                    checkObj.ps = selArr[0].ps;
                    Joyin.ajaxJson(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade001/checkRevoke', checkObj, function(result) {
                        Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                            checkObj.revokeReason = result;
                            WorkFlow.doRevokeList(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade001/doRevoke', checkObj, "TrdBondLendInvestTrade001_btn_revoke", "TrdBondLendInvestTrade002");
                        });
                    });
                }
            }
        })
    }

    /**
     * 画面加载完成
     */
    $(function() {
        //初始化
        init();

        //组合树点查询事件
        $("#AssetsTransfer001_btn_portofioSearch").click(function () {
            $("#treePorfolio").css('height', '470px');
            var preUrl = getAllRelationUrl + "?";
            if ($("#AssetsTransfer001_portfolioId").val()) {
                var portfolioId = $("#AssetsTransfer001_portfolioId").val().join(",");
                $("#treePorfolio").attr('url', preUrl + "portfolioId=" + portfolioId +
                    '&mdate=' + $("#AssetsTransfer001_mdate").val());
            } else {
                $("#treePorfolio").attr('url', preUrl +
                    'mdate=' + $("#AssetsTransfer001_mdate").val());
            }
            searchTreeSingle();
        });

        // 未转仓交易查询按钮事件
        $('#AssetsTransfer001_btn_search').click(function () {
            unTransferTable.load();
        });

        // 已转仓交易查询按钮事件
        $('#AssetsTransfer001_btn_transferSearch').click(function () {
            if(!$('#tradeDate').val()){
                Joyin.error("请输入交易日期!");
                return;
            }
            transferTable.load();
        });

        /**
         * 撤销
         */
        $('#AssetsTransfer001_btn_revoke').click(function () {
            revoke();
        });
    })
}());
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
    var showDetailByFinprodId = function(finprodId,finprodMarketId,assetType){
        //债券标的信息查看
        if(assetType == PINAN_ASSET_TYPE_F01){
            Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"),
                basePath +"home.html?a=a&finprodMarketId=" + finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:finprodId  //金融产品代码
                });
        }
        else if(assetType == PINAN_ASSET_TYPE_F10){
            Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"),
                basePath + "invest/asset/bond/FinBondTargetAsset002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:finprodId  //金融产品代码
                });
        }
        //基金信息查看
        else if(assetType == PINAN_ASSET_TYPE_F02){
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/asset/fund/FinFundTarget002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,finprodId:finprodId});
        }
        //存款类资产信息查看
        else if(assetType == PINAN_ASSET_TYPE_F05){
            Joyin.dialog(JoyinUtil.getLabel("FinDepositTarget002.title"),
                basePath + "invest/asset/depo/FinDepositTarget002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:finprodId  //金融产品代码
                });
        }
        //计划类净值型资产查看
        else if(assetType == PINAN_ASSET_TYPE_F04){
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/asset/plan/planNet/FinPlanNetAsset002/init",
                "98%", "98%", {
                    pageModel: PAGE_MODEL_REFERENCE,
                    finprodId: finprodId
                });
        }
        //计划类利率型资产查看
        else if(assetType == PINAN_ASSET_TYPE_F03){
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/asset/plan/planProfit/FinPlanProfitAsset002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:finprodId});
        }
        //计划类货币型资产查看
        else if(assetType == PINAN_ASSET_TYPE_F09){
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/asset/plan/plancur/FinPlanAssetC002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:finprodId});
        }
        //项目类资产查看
        else if(assetType == PINAN_ASSET_TYPE_F06){
            Joyin.dialog(JoyinUtil.getLabel("FinProjectTargetAsset002.title"),
                basePath + "invest/asset/project/FinProjectTargetAsset002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,
                    finprodId:finprodId
                });
        }
        //股票资产查看
        else if(assetType == PINAN_ASSET_TYPE_F11){
            Joyin.dialog(JoyinUtil.getLabel("FinStockInfo002.title"),
                basePath + "invest/asset/stock/FinStockInfo002/init",
                "99%", "99%", {
                    pageModel: PAGE_MODEL_REFERENCE,
                    finprodId: finprodId,
                    finprodMarketId: finprodMarketId//市场代码
                });
        }
    }
