//@ sourceURL=BussTrdTransferOrder.js
/**
 * 划款指令共通组件
 */
(function () {
    var entityName = ENTITY_NAME;
    var entity = ENTITY;
    var entityList = ENTITY_LIST;
    var getElementById = function (id) {
        return $('#' + entityName + '_' + id);
    };
    var table;
    // 生成指令参数保存
    var calcConditionObjBackUp = null;
    //表格初始化n
    var init = function () {
        if (pageModel != PAGE_MODEL_NEW && entity) {
//            getElementById('payAcctNo').val(JSON.stringify(entity.payAcctNoList));
//            getElementById('recAcctNo').val(JSON.stringify(entity.recAcctNoList));
        }
        /**
         * 定义数据表格
         *
         */
        var option = {
            searchOnInit: true,
            checkbox: false,
            cols: [
                {
                    field: 'isNeedSend', width: 140, title: 'BussTrdTransferOrder.isNeedSend', edit: true,
                    editOption: {type: 'select', multiple: false, searchable: false, showField: 'text', result: YES_NO}, sortable: false
                }
                , {field: 'payTypeText', width: 130, title: 'BussTrdTransferOrder.payType', sortable: false}
                , {field: 'settleDateText', width: 130, title: 'BussTrdTransferOrder.settleDate', sortable: false}
                , {field: 'settleAmt', width: 130, title: 'BussTrdTransferOrder.settleAmt', type: 'money', sortable: false}
                , {field: 'ccy', width: 130, title: 'BussTrdTransferOrder.ccy', sortable: false}
                , {
                    field: 'payAcctNo', width: 130, title: 'BussTrdTransferOrder.payAcctNo', edit: true,
                    editOption: {
                        type: 'select', multiple: false, searchable: true, parentIds: entityName + '_payAcctNo,ccy_select', showField: 'id',
                        url: JoyinUtil.getRealPath("/component/trade/BussTrdTransferOrder/select")
                    }, sortable: false
                }
                , {field: 'payAcctName', width: 150, title: 'BussTrdTransferOrder.payAcctName', sortable: false}
                , {
                    field: 'recAcctNo', width: 130, title: 'BussTrdTransferOrder.recAcctNo', edit: true,
                    editOption: {
                        type: 'select', multiple: false, searchable: true, parentIds: entityName + '_recAcctNo,ccy_select', showField: 'id',
                        url: JoyinUtil.getRealPath("/component/trade/BussTrdTransferOrder/select")
                    }, sortable: false
                }
                , {field: 'recAcctName', width: 150, title: 'BussTrdTransferOrder.recAcctName', sortable: false}
                , {field: 'recAcctPoint', width: 150, title: 'BussTrdTransferOrder.recAcctPoint', sortable: false}
                , {field: 'purpose', width: 130, title: 'BussTrdTransferOrder.purpose', sortable: false, edit: orderPageId != 'OrderNewBuild002'}
                , {field: 'remark', width: 130, title: 'BussTrdTransferOrder.remark', sortable: false, edit: orderPageId != 'OrderNewBuild002'}
                , {field: 'portfolioId', width: 130, title: 'BussTrdTransferOrder.portfolioId', sortable: false}
                , {field: 'portfolioName', width: 150, title: 'BussTrdTransferOrder.portfolioName', sortable: false}
                , {field: 'inOutBank', width: 130, title: 'BussTrdTransferOrder.inOutBank', sortable: false}
                , {field: 'orderId', visible: false}
                , {field: 'payType', visible: false}
                , {field: 'ps', visible: false}
                , {
                    field: 'ccy', visible: false, edit: false, width: 0,
                    editOption: {
                        type: 'select', id: 'ccy_select', childrenIds: 'recAcctNo,payAcctNo'
                    }
                }
                , {field: 'settleStatus', visible: false}
                , {field: 'isTotalTrade', visible: false}
                , {field: 'settleDate', visible: false}
            ],
            page: false,
            height: 200,
            data: [],
            done: function () {
                $("[data-field='ccy'][data-edit='select']").hide();
            },
            //add by yzg 20191108 start
            beforeEditCell : function(preVal, field, rowData){
            	if(calcConditionObjBackUp!=null && calcConditionObjBackUp.repPageId == 'OrderNewBuild002'){
            		return false;
            	}
            	return true;
            },
            //add by yzg 20191108 end
            editCell: function (value, row, field, cell, column) {
                if (column.field == 'payAcctNo') {
                    if (typeof value != "undefined" && value) {
                        row.payAcctName = $(cell).data('sd')[0].text;
                    }
                    if (typeof value == "undefined" || value == null || value == "") {
                        row.payAcctName = "";
                    }

                    var yesNo = JSON.parse(YES_NO);
                    $.each(yesNo, function (i, v) {
                        if (v.id == row.isNeedSend) {
                            row.isNeedSend = v.text;
                        }
                    });
                    // 选择付款账户带出贷款账户名称
                    if (typeof value != "undefined" && value) {
                        Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                            ccy: row.ccy,
                            acctNo: value
                        }, function (result) {
                            // 网点
//                        row.recAcctPoint = result.largePayno;
                            //add by wangmin 2019-0-06 begin
                            if (!result) {
                                Joyin.error("该币种下，无此账户。");
                                row.recAcctNo = null;
                                table.renderRow(row.LAY_TABLE_INDEX, row);
                                return;
                            }
                            row.payAcctNo=result.acctNo;
                            row.payAcctName = result.acctName;
                            //add by wangmin 2019-0-06 end
                            table.renderRow(row.LAY_TABLE_INDEX, row);
                        })
                    } else {
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                    }

                } else if (column.field == 'recAcctNo') {
                    if (typeof value != "undefined" && value) {
                        row.recAcctName = $(cell).data('sd')[0].text;
                    }
                    if (typeof value == "undefined" || value == null || value == "") {
                        row.recAcctName = "";
                    }

                    if (typeof value != "undefined" && value) {
                        // 选择收款账户带出行内/行外、网点
                        Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                            ccy: row.ccy,
                            acctNo: value
                        }, function (result) {
                        	if (!result) {
                                Joyin.error("该币种下，无此账户。");
                                row.recAcctNo = null;
                                table.renderRow(row.LAY_TABLE_INDEX, row);
                                return;
                            }
                            var assetSource = JSON.parse(ASSET_SOURCE);
                            $.each(assetSource, function (i, v) {
                                if (v.id == result.inOutBank) {
                                    row.inOutBank = v.text;
                                }
                            });

                            // var yesNo = JSON.parse(YES_NO);
                            // $.each(yesNo, function (i, v) {
                            //     if (v.id == row.isNeedSend) {
                            //         row.isNeedSend = v.text;
                            //     }
                            // });

                            // 网点
                            row.recAcctNo = result.acctNo;
                            row.recAcctPoint = result.largePayno;
                            //add by wangmin 2019-0-06 begin
                            row.recAcctName = result.acctName;
                            //add by wangmin 2019-0-06 end
                            table.renderRow(row.LAY_TABLE_INDEX, row);
                        })
                    } else {
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                    }
                }
            }
        }

        // 不改变原有逻辑的基础上，新增处理逻辑，展示多条指令用  add by renping 2019-07-10 start
        if (entityList) {
            entityList.forEach(function (value) {
                option.data.push(value);
            });
        } else if (entity) {

            // 不改变原有逻辑的基础上，新增处理逻辑，展示多条指令用  add by renping 2019-07-10 end
            option.data.push(entity);
        }
        table = getElementById("table").joyintable(option);



        $("#" + entityName + "_btn_generate").on("click", function () {generateOrders();});
    };

    function extractedDuoZuHe(listdataMulti, index, callback) {
        if (calcConditionObjBackUp && calcConditionObjBackUp.deal.length > 0) {
            table.clear(function () {
                for (var dataindex in calcConditionObjBackUp.deal) {
                    var data = calcConditionObjBackUp.deal[dataindex];
                    if (data) {
                        listdataMulti.deal = data;
                        listdataMulti.portfolioId = data.portfolioId;
                    }
                    if (calcConditionObjBackUp.jiaoyiType == 'dzh') {
                        listdataMulti.isDzh = "Y";
                    }
                    Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/check'), listdataMulti, function (result) {
                        Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/generateOrder'), result.data, function (ret) {
                            var order = ret.data;
                            // DVP资金调拨管理,直接使用画面上选择的收付款账户
                            if (data.isUsePageAcct) {
                                order.payAcctNoList = data.payAcctNoList;
                                order.recAcctNoList = data.recAcctNoList;

                                if (data.payAcctNoList && data.payAcctNoList.length > 0) {
                                    order.payAcctNo = data.payAcctNoList[0].id;
                                    order.payAcctName = data.payAcctNoList[0].text;
                                }
                                if (data.recAcctNoList && data.recAcctNoList.length > 0) {
                                    order.recAcctNo = data.recAcctNoList[0].id;
                                    order.recAcctName = data.recAcctNoList[0].text;
                                }

                                // 根据收款账户带出行内/行外、网点
                                Joyin.ajaxSync(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                                    ccy: order.ccy,
                                    acctNo: order.recAcctNo
                                }, function (result) {
                                    var assetSource = JSON.parse(ASSET_SOURCE);
                                    $.each(assetSource, function (i, v) {
                                        if (v.id == result.inOutBank) {
                                            order.inOutBank = v.text;
                                        }
                                    });

                                    // 网点
                                    order.recAcctPoint = result.largePayno;
                                    //add by wangmin 2019-0-06 begin
                                    order.recAcctName = result.acctName;
                                    //add by wangmin 2019-0-06 end
                                })
                            }

                            if (checkMyAccount(order)) {
                                Joyin.error("本方账户为空，请检查账户配置。");
                                return;
                            }
                            table.addRow(order);
                            layui.layer.close(index);
                            getElementById('payAcctNo').val(JSON.stringify(order.payAcctNoList));
                            getElementById('recAcctNo').val(JSON.stringify(order.recAcctNoList));
                            if (callback && typeof callback === 'function') {
                                callback();
                            }
                            // 异步请求改为同步请求 modify by renping start
                        }, false);
                    }, false);
                    // 异步请求改为同步请求 modify by renping end
                }
            });
        }
    }


    function extractedDanZuHe(calcConditionObjBackUp,index, callback) {
        //指令新建生成划款信息 yzg 20191101 start
        if(calcConditionObjBackUp.repPageId == 'OrderNewBuild002'){
            Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/generatorOrderNewBuild'), calcConditionObjBackUp.deal, function (ret) {
                table.clear(function (){
                    var rowData = calcConditionObjBackUp.deal;
                    rowData.orderId = ret.data.orderId;
                    rowData.settleDateText = Date.ofDate(rowData.settleDate).format('yyyy-MM-dd');
                    //划款类型
                    if (calcConditionObjBackUp.payTypeList && calcConditionObjBackUp.payTypeList.length > 0) {
                        rowData.payType = calcConditionObjBackUp.payTypeList[0].id;
                        rowData.payTypeText = calcConditionObjBackUp.payTypeList[0].text;
                    }
                    //付款账号
                    if (calcConditionObjBackUp.payAcctNoList && calcConditionObjBackUp.payAcctNoList.length > 0) {
                        rowData.payAcctNo = calcConditionObjBackUp.payAcctNoList[0].id;
                        rowData.payAcctName = calcConditionObjBackUp.payAcctNoList[0].text;
                    }
                    //收款账号
                    if (calcConditionObjBackUp.recAcctNoList && calcConditionObjBackUp.recAcctNoList.length > 0) {
                        rowData.recAcctNo = calcConditionObjBackUp.recAcctNoList[0].id;
                        rowData.recAcctName = calcConditionObjBackUp.recAcctNoList[0].text;
                        rowData.recAcctPoint = calcConditionObjBackUp.recAcctNoList[0].object;
                    }

                    if (checkMyAccount(rowData)) {
                        Joyin.error("本方账户为空，请检查账户配置。");
                        return;
                    }
                    table.addRow(rowData);
                    layui.layer.close(index);
                    if (callback && typeof callback === 'function') {
                        callback();
                    }
                })
            })
            return;
        }
        //指令新建生成划款信息 yzg 20191101 end
        Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/check'), calcConditionObjBackUp, function () {
            Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/generateOrder'), calcConditionObjBackUp, function (ret) {
                var order = ret.data;
                table.clear(function () {
                    // DVP资金调拨管理,直接使用画面上选择的收付款账户
                    if (calcConditionObjBackUp.isUsePageAcct) {
                        order.payAcctNoList = calcConditionObjBackUp.payAcctNoList;
                        order.recAcctNoList = calcConditionObjBackUp.recAcctNoList;

                        if (calcConditionObjBackUp.payAcctNoList && calcConditionObjBackUp.payAcctNoList.length > 0) {
                            order.payAcctNo = calcConditionObjBackUp.payAcctNoList[0].id;
                            order.payAcctName = calcConditionObjBackUp.payAcctNoList[0].text;
                        }
                        if (calcConditionObjBackUp.recAcctNoList && calcConditionObjBackUp.recAcctNoList.length > 0) {
                            order.recAcctNo = calcConditionObjBackUp.recAcctNoList[0].id;
                            order.recAcctName = calcConditionObjBackUp.recAcctNoList[0].text;
                        }

                        // 根据收款账户带出行内/行外、网点
                        Joyin.ajaxSync(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                            ccy: order.ccy,
                            acctNo: order.recAcctNo
                        }, function (result) {
                            var assetSource = JSON.parse(ASSET_SOURCE);
                            $.each(assetSource, function (i, v) {
                                if (v.id == result.inOutBank) {
                                    order.inOutBank = v.text;
                                }
                            });

                            // 网点
                            order.recAcctPoint = result.largePayno;
                            //add by wangmin 2019-0-06 begin
                            order.recAcctName = result.acctName;
                            //add by wangmin 2019-0-06 end
                        })
                    }

                    //是否轧差清算 yzg 20191108 start
                    if(calcConditionObjBackUp.isLiquidation == true){
                        if(calcConditionObjBackUp.deal.liquidationYesno == YES){
                            order.isNeedSend = NO;
                        }
                    }
                    //是否轧差清算 yzg 20191108 end

                    getElementById('payAcctNo').val(JSON.stringify(order.payAcctNoList));
                    getElementById('recAcctNo').val(JSON.stringify(order.recAcctNoList));
                    if (checkMyAccount(order)) {
                        Joyin.error("本方账户为空，请检查账户配置。");
                        return;
                    }
                    table.addRow(order);
                    layui.layer.close(index);
                    if (callback && typeof callback === 'function') {
                        callback();
                    }
                    layui.layer.close(index);
                });
            },false);
        },false);
    }

    var generateOrders = function (callback) {
        if (stlCommandInput != null) {
            var index = layer.open({
                type: 1,
                icon: 16,
                area: ['200px', '100px'],
                content: '数据加载中',
                closeBtn: 0
            });
            var listdataMulti = stlCommandInput();
            //债券质押回购多组合，张庚林，20190723 update start
            if (!listdataMulti || ((!listdataMulti.deal || listdataMulti.deal.length == 0) && (!listdataMulti.deal || listdataMulti.deal.length == 0))) {
                layui.layer.close(index);
                return;
            }
            //债券质押回购多组合，张庚林，20190723 end
            // 保存生成指令参数
            calcConditionObjBackUp = stlCommandInput();
            /* calcConditionObjBackUp.repPageId = calledPageId;*/
            if (isMultitemp) {//债券多组合
                extractedDuoZuHe(listdataMulti, index, callback);
            } else {//质押式多组合
                if (calcConditionObjBackUp) {
                    //债券质押回购多组合，张庚林，20190723 update start
                    if (calcConditionObjBackUp.deal != null&&calcConditionObjBackUp.jiaoyiType=='dzh') {
                        extractedDuoZuHe(listdataMulti, index, callback);
                    } else {//单组合
                        extractedDanZuHe(calcConditionObjBackUp,index, callback);
                    }
                    //债券质押回购多组合，张庚林，20190723 update start
                }
            }
        }
        layui.layer.close(index);
    }

    var generateClick = function () {
        $("#" + entityName + "_btn_generate").on("click", function () {generateOrdersPrivate();});
        $("#" + entityName + "_btn_generate").click();
    }

    var generateOrdersPrivate = function (callback) {
            if (stlCommandInput != null) {
                var index = layer.open({
                    type: 1,
                    icon: 16,
                    area: ['200px', '100px'],
                    content: '数据加载中', closeBtn: 0
                });
                var data = stlCommandInput();
                // 保存生成指令参数
                calcConditionObjBackUp = data;

                if (data && data.skipPrivate!=true) {
                    Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/check'), data, function () {
                        Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/generateOrder'), data, function (ret) {
                            var order = ret.data;
                            table.clear(function () {
                                if (checkMyAccount(ret.data)) {
                                    Joyin.error("本方账户为空，请检查账户配置。");
                                    return;
                                }
                                table.addRow(order);
                                layui.layer.close(index);
                                getElementById('payAcctNo').val(JSON.stringify(order.payAcctNoList));
                                getElementById('recAcctNo').val(JSON.stringify(order.recAcctNoList));
                                if (callback && typeof callback === 'function') {
                                    callback();
                                }
                            });
                        }, true);
                    });
                }
                layui.layer.close(index);
            }
    }

    var checkMyAccount = function (order) {
        var datas = [];
        if(_.isArray(order)) {
            datas = _.concat(datas, order);
        }
        else {
            datas.push(order);
        }
        $.each(datas, function (i, v) {
            if (v && ((v.ps == '01' && !v.recAcctNo) || (v.ps == '02' && !v.payAcctNo))) {
                return true;
            }
        })
        return false;
    }

    var stlCommandInput;
    var isMultitemp = false;
    var setStlCommandInput = function (func, isMulti) {
        stlCommandInput = func;
        isMultitemp = isMulti;
    };

    /**
     * 初期化处理保存生成指令参数
     */
    var saveCalcCondition = function () {
        // 初期化处理时，表格中是否有数据

        if (stlCommandInput != null) {
            var data = stlCommandInput();
            // 保存生成指令参数
            calcConditionObjBackUp = data;
        }
    };

    function duozuheCheck(currentCalcConditionList,calcConditionObjBackUp){
        var isTrue = false;
        if (calcConditionObjBackUp&&calcConditionObjBackUp.deal&&
                calcConditionObjBackUp.deal.length && calcConditionObjBackUp.deal.length > 0) {
            $.each(calcConditionObjBackUp.deal,function (index,element) {
                for (var key in element) {
                    if (key == "createTime" || key == "updateTime"||key=='index') {
                        continue;
                    }
                    var currentDealObj = currentCalcConditionList.deal[index][key];
                    var backupDealObj = element[key];

                    if (typeof currentDealObj != "object"
                        && currentDealObj != backupDealObj) {
                        isTrue = true;
                        break;
                    } else if (typeof currentDealObj == "object") {
                        for (var i in currentDealObj) {
                            if (i == "createTime" || i == "updateTime"||i=='index') {
                                continue;
                            }
                            if (typeof currentDealObj[i] != "object"
                                && backupDealObj[i] != currentDealObj[i]) {
                                isTrue = true;
                                break;
                            }
                        }
                    }
                }
            })
        }
        return isTrue;
    }

    // 检查生成指令参数是否发生变化
    var isCalcConditionChanged = function () {
        // 参照模式，不做check
        if (pageModel === PAGE_MODEL_REFERENCE) {
            return false;
        }
        if (!getElementById("div").is(":visible")) {
            return false;
        }
        if (calcConditionObjBackUp == null) {
            return false;
        }

        if (stlCommandInput != null) {
            var currentCalcConditionList = stlCommandInput();
            //债券质押回购多组合，张庚林，20190723 update start
            if (currentCalcConditionList.portfolioId != "dzh") {
                //债券质押回购多组合，张庚林，20190723 update end
                if (!currentCalcConditionList.deal || calcConditionObjBackUp.deal.length != currentCalcConditionList.deal.length) {
                    Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                        ["指令生成要素", "生成划款指令"]));
                    return true;
                }
                //债券质押回购多组合，张庚林，20190723 update start
            } else {
                if (!currentCalcConditionList.deal || calcConditionObjBackUp.deal.length != currentCalcConditionList.deal.length) {
                    Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                        ["指令生成要素", "生成划款指令"]));
                    return true;
                }
            }
            //债券质押回购多组合，张庚林，20190723 update end

            // var currentCalcConditionObj = stlCommandInput();
            // 影响计划生成的所有项目与计划生成时记录的值比较，值变更时
            for (var key in currentCalcConditionList) {
                //债券质押回购多组合，张庚林，20190723 update start 
                if (currentCalcConditionList.portfolioId != "dzh") {
                    //债券质押回购多组合，张庚林，20190723 update end
                    if (typeof currentCalcConditionList[key] != "object"
                        && currentCalcConditionList[key] != calcConditionObjBackUp[key]) {
                        //                        var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key);
                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                            ["指令生成要素", "生成划款指令"]));
                        return true;
                    }
                    //债券质押回购多组合，张庚林，20190723 update start
                } else {
                    if (typeof currentCalcConditionList[key] != "object"
                        && duozuheCheck(currentCalcConditionList,calcConditionObjBackUp)) {
                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                            ["指令生成要素", "生成划款指令"]));
                        return true;
                    }
                }
                //债券质押回购多组合，张庚林，20190723 update end
            }

            // 支持多条划款指令生成时check add by renping 2019-07-12 start
            if ((calcConditionObjBackUp.deal.length && calcConditionObjBackUp.deal.length > 1)||
                currentCalcConditionList.portfolioId == "dzh") {
                for (var i = 0; i < calcConditionObjBackUp.deal.length; i++) {
                    var dealDataObj = currentCalcConditionList.deal[i];
                    var dealDataUp = calcConditionObjBackUp.deal[i];
                    for (var key in dealDataObj) {
                        if (key == "createTime" || key == "updateTime"||key=='index') {
                            continue;
                        }
                        var currentDealObj = dealDataObj[key];
                        var backupDealObj = dealDataUp[key];

                        if (typeof currentDealObj != "object"
                            && currentDealObj != backupDealObj) {
                            Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                                ["指令生成要素", "生成划款指令"]));
                            return true;
                        } else if (typeof currentDealObj == "object") {
                            for (var j in currentDealObj) {
                                if (j == "createTime" || j == "updateTime") {
                                    continue;
                                }
                                if (typeof currentDealObj[j] != "object"
                                    && backupDealObj[j] != currentDealObj[j]) {
                                    Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                                        ["指令生成要素", "生成划款指令"]));
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else {
                for (var key in currentCalcConditionList.deal) {
                    if (key == "createTime" || key == "updateTime") {
                        continue;
                    }
                    // 支持多条划款指令生成时check add by renping 2019-07-12 end
                    var currentDealObj = currentCalcConditionList.deal[key];
                    var backupDealObj = calcConditionObjBackUp.deal[key];

                    if (typeof currentDealObj != "object"
                        && currentDealObj != backupDealObj) {
                        //                        var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key);
                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                            ["指令生成要素", "生成划款指令"]));
                        return true;
                    } else if (typeof currentDealObj == "object") {
                        for (var i in currentDealObj) {
                            if (i == "createTime" || i == "updateTime") {
                                continue;
                            }
                            if (typeof currentDealObj[i] != "object"
                                && backupDealObj[i] != currentDealObj[i]) {
                                Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                                    ["指令生成要素", "生成划款指令"]));
                                return true;
                            }
                        }
                    }
                }
            }
        }
    };

    // 划款指令共通组件公开接口
    if (window.BussTrdTransferOrder == undefined) {
        window.BussTrdTransferOrder = {};
    }
    window.BussTrdTransferOrder[entityName] = {
        /**
         * 输入函数范例，需提供是否汇总交易，拆借类型，组合代码，金融产品交易Entity：
         *   function {
         *       return {'isTotalTrade':'02','bankLendingType':null,'portfolioId':'123456','deal':getDealEntity()}
         *   }
         */
        setStlCommandInput: setStlCommandInput,
        init: init,
        isCalcConditionChanged: isCalcConditionChanged,
        saveCalcCondition: saveCalcCondition,
        generateClick: generateClick,
        generateOrders: generateOrders,
        "bussTrdTransferOrderTable":table
    };
    init();
}());