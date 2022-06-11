//@ sourceURL=EtrExchangeTradeCommon004.js
/**
 * 行情和交易信息
 */
(function () {
    //申报价格
    var declarePriceObj = {};
    //是否需要格式化指令价格(申报信息get出来不需要格式化)
    var declarePriceFlag = true;
    //初始化标志
    var initFlag = false;
    var availableNum = "";
    var riskFlag = 1;
    // 默认画面为“净价交易平台”
    // var currentTab = currentTab;
    //试算结果
    var riskResultdata;
    var cluflag = true;
    //默认新增指令 指令操作类型  NEW 是新增指令  EDIT 修改指令
    var optType = ENTRUST_RECORD_NEW;
    //默认清空表示为NULL值,点击清空时，置为true
    var resetFlag = null;
    //判断竞价平台资产二级分类;
    var type9 = '';
    //证券代码关联的对象;
    secIdObjRelative = '';
    //默认指令价格小数点后位数
    var scale = 3;
    //双击后在录入页面加载联动的开关，防止计算价格和收益率的方法重复调用,提高速度
    var setPageItemsFlag = false;
    //市价指令默认0价
    var zeroPrice = '0.00';
    //指令真实价格
    var factPrice = zeroPrice;

    var rskStatus = 0;

    function query(ele) {
        return $("#EtrExchangeTradeCommon004_bodydiv " + ele);
    }

    /**
     * 返回表单数据
     * @returns
     */
    function getSearchParams() {
        var object = $.serializeToObject('#sumbitForm_' + currentTab);
        //根据指令方向设置质押式回购的金融产品类型
        if (object.finprodType2 && object.finprodType2 == FINPROD_TYPE_TG_ZYSHG) {
            if (object.ps == PS_03) {
                object.finprodType2 = FINPROD_TYPE_TG_F02;
            } else {
                object.finprodType2 = FINPROD_TYPE_TG_F03;
            }
        }
        if (object.ps == PS_T68 || object.ps == PS_T69 || object.ps == PS_T49 || object.ps == PS_T97 || object.ps == PS_T46 || object.ps == PS_T03 || object.ps == PS_T04) {
            object.priceModel = "PRM01";
        }

        // 隐藏项需要手动获取
        object.finprodId = $("#finprodId_" + currentTab).val();
        object.tradeMarket = $("#tradeMarket_" + currentTab).val();
        object.entrustId = $("#entrustId_" + currentTab).val();
        object.entrustStatus = $("#entrustStatus_" + currentTab).val();
        //object.availableNum = parent.EtrExchangeTradeJjHq.getJJMktPrice("#availableNum_jj").replace(/,/g, "");
        object.tradePlat = currentTab;
        if (currentTab == 'jj') {//竞价
            object.tradePlat = SHARES_TRADE_PLATFORM_02;
        } else if (currentTab == 'dz') {//大宗
            object.tradePlat = SHARES_TRADE_PLATFORM_04;
        } else if (currentTab == 'gd') {//固定
            object.tradePlat = SHARES_TRADE_PLATFORM_03;
        }
        if (object.secId) {//证券代码
            object.stockId = object.secId;
        }

        // 计算模式需要特殊处理
        object.calculateField = object['calculateField_' + currentTab];
        //指令价格为空或者债券时 交易方向非配债、行权回售、可转债申购
        if (object.finprodType2 && object.finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(object.ps)) {
            object.entrustPrice = object.price;
        }
        ;
        if (object.priceModel == PRICE_MODEL_PRM03) {
            object.entrustPrice = $("#factPrice_" + currentTab).val();
            if (!object.entrustPrice) {
                object.entrustPrice = 0;
            }
        }
        //设置涨跌停参数
        //parent.EtrExchangeTradeJjHq.setLimitUpAndDown(object);
        object.pageModel = pageModel;
        object.optType = optType;

        object.fromEtrExchange = YES_OR_NO_Y;

        if (riskResultdata) {
            object.riskTrialRetDto = riskResultdata;
        }
        object.entrustPriceLabel = $('#entrustPrice_label_jj').text().replace("*", "");
        object.entrustNumLabel = $('#entrustNum_label_jj').text().replace("*", "");
        object.entrustAmtLabel = $('#entrustAmt_label_jj').text().replace("*", "");
        object.toShareAvailableNumLabel = $('#toShareAvailableNum_label_jj').text().replace("*", "");
        if (dateTable) {
            object.portfolioList = dateTable.getAllRows();
            for (var i = 0; i < object.portfolioList.length; i++) {
                if (!object.portfolioList[i].entrustAmt) {
                    object.portfolioList[i].entrustAmt = 0;
                }
            }
        }

        if (!object.ps) {
            object.priceModel = "PRM01";
            object.entrustPrice = 0;
        }
        return object;
    }

    /**
     * 返回大宗查询条件
     * @returns
     */
    function getDzSearchParams() {
        var object = new Object();
        object.secId = $("#secId_" + currentTab).val();
        return object;
    }

    /**
     * 获取风险试算数据
     */
    var getriskdata = function (object) {
        var obj = {}
        //债券
        if (object.finprodType2 == FINPROD_TYPE_TG_F01) {
            var tradeAmount = 0;
            if (object.ps == PS_T49) {
                tradeAmount = object.entrustNum;
            } else {
                tradeAmount = object.entrustAmt;
            }
            obj = {
                tradeDirection: object.ps,
                timePoint: MY_SUP_TIME_TYPE_02,
                valDate: iPlatformDate,
                portfolioCode: object.portfolioId,
                tradeAmount: tradeAmount,
                assetType: object.finprodType2,
                isEnabled: '01',
                assetCode: object.finprodId
            }

        }
        //基金
        else if (object.finprodType2 == FINPROD_TYPE_TG_F06) {
            //买入
            if (object.ps == PS_T01) {
                obj = {
                    tradeDirection: object.ps,
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: iPlatformDate,
                    portfolioCode: object.portfolioId,
                    tradeAmount: object.entrustAmt,
                    assetType: object.finprodType2,
                    isEnabled: '01',
                    assetCode: object.finprodId
                }
            }
            //卖出
            else if (object.ps == PS_T02) {
                obj = {
                    tradeDirection: object.ps,
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: iPlatformDate,
                    portfolioCode: object.portfolioId,
                    tradeAmount: object.entrustAmt,
                    assetType: object.finprodType2,
                    isEnabled: '01',
                    assetCode: object.finprodId,
                    amtType: '1'
                }
            }
        }
        //股票
        else if (object.finprodType2 == FINPROD_TYPE_TG_F10) {
            obj = {
                tradeDirection: object.ps,
                timePoint: MY_SUP_TIME_TYPE_02,
                valDate: iPlatformDate,
                portfolioCode: object.portfolioId,
                tradeAmount: object.entrustAmt,
                assetType: object.finprodType2,
                isEnabled: '01',
                assetCode: object.finprodId
            }
        }
        //质押式回购
        else if (object.finprodType2 == FINPROD_TYPE_TG_F02 || object.finprodType2 == FINPROD_TYPE_TG_F03) {
            obj = {
                tradeDirection: object.ps,
                timePoint: MY_SUP_TIME_TYPE_02,
                valDate: iPlatformDate,
                portfolioCode: object.portfolioId,
                tradeAmount: object.entrustAmt,
                assetType: object.finprodType2,
                isEnabled: '01',
                assetCode: object.finprodId
            }
        }

        //增加交易对手影响试算
        if (currentTab == 'dz' || currentTab == 'gd') {
            obj.counterId = object.counterId;
        }

        var riskdata = {
            tradeDirection: ['ps', 'EtrExchangeTrade001.ps'],
            portfolioCode: ['portfolioId', 'EtrExchangeTrade001.portfolioId'],
            tradeAmount: ['entrustNum', getRiskMathPrompt()],
            assetType: ['assetType1', 'EtrExchangeTrade003.finprodType2'],
            data: obj,
            // add by zhangrui1 交易对手影响风险试算 start
            counterId: ['counterId', 'EtrExchangeTrade001.counterId']
        }
        return riskdata;
    }

    /**
     * 重写更新下拉框选项的方法
     */
    $.fn.updateChosenArr = function updateChosenArr(obj) {
        var $this = $(this), arr = obj.dataResult || JSON.parse($this.attr('data-result') || '[]'), item = obj.item;
        $this.empty();
        $this.append($('<option value>'));
        for (var i in arr) {
            if (!arr.hasOwnProperty(i)) continue;
            if (obj.item && obj.item.length > 0) {
                if ($.inArray(arr[i].id, item) >= 0)
                    $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this);
            } else
                $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this)
        }
        if (obj.chosen)
            $this.val(obj.chosen);
        if (obj.disabled === true || obj.disabled === false)
            $this.attr('disabled', obj.disabled);
        if (obj.isNotTrigger) {
            $this.trigger("chosen:updated").trigger('change', obj.isNotTrigger);
        } else {
            $this.trigger("chosen:updated").trigger('change');
        }

        if (obj.callback)
            obj.callback($this);
        return $this;
    }
    /**
     * 根据传入的数据，设置画面元素
     */
        //add by chenjiaxing 持仓页面点击未带出净价 start
    var setEntrust001PageItems001 = function (param) {
            setPageItemsFlag = true;
            if (param) {
                if (param.entrustType) {
                    linkTradePlat(param.entrustType, false);
                }
                if (param.entrustId) {
                    $("#entrustId_" + currentTab).val(param.entrustId).trigger('change');
                }
                if (param.entrustStatus) {
                    $("#entrustStatus_" + currentTab).val(param.entrustStatus).trigger('change');
                }
                if (param.portfolioId) {
                    $("#portfolioId_" + currentTab).textSelect('setValue', param.portfolioId);
                }
                if (param.acctNo) {
                    $("#acctNo_" + currentTab).textSelect('setValue', param.acctNo);
                }
                if (param.secId) {
                    $("#secId_" + currentTab).textSelect('setValue', param.secId);
                }
                /*if(param.finprodType2) {
                    $("#finprodType2_" + currentTab).val(param.finprodType2).trigger('change');
                }*/
                if (param.ps) {
                    $("#ps_" + currentTab).textSelect('setValue', param.ps);
                }
                if (param.tradeMarket) {
                    $("#tradeMarket_" + currentTab).val(param.tradeMarket).trigger('change');
                }
                if (param.dataSource) {
                    $("#dataSource_" + currentTab).val(param.dataSource).trigger('change');
                }
                if (param.priceModel) {
                    $("#priceModel_" + currentTab).val(param.priceModel).trigger('change', true);
                }
                if (param.declaType) {
                    $("#declaType_" + currentTab).val(param.declaType).trigger('change', true);
                }
                if (param.calMode) {

                    $("input[name=calculateField_" + currentTab + "][value='" + param.calMode + "']").attr("checked", true);
                    layui.form.render(); //更新
                    calculateFieldChange();
                }
                // 需要根据金融产品类型设置不同的价格
                if (param.price) {
                    if ($("#finprodType2_" + currentTab).val() == FINPROD_TYPE_TG_F01 && isStandardPs(param.ps)) {
                        $("#price_" + currentTab).val(param.price).trigger('blur').trigger('change');
                    } else {
                        $("#entrustPrice_" + currentTab).val(param.price).trigger('blur').trigger('change');
                    }
                }
                if (param.calMode && param.calMode == SEC_CALC_MODE_02 && param.fprice) {
                    $("#fprice_" + currentTab).val(param.fprice).trigger('blur').trigger('change');
                }
                if (param.calMode && param.calMode == SEC_CALC_MODE_03 && param.yield) {
                    $("#yield_" + currentTab).val(param.yield).trigger('blur').trigger('change');
                }
                if (param.entrustPrice) {
                    if ($("#finprodType2_" + currentTab).val() == FINPROD_TYPE_TG_F01 && isStandardPs(param.ps)) {
                        $("#price_" + currentTab).val(param.entrustPrice).trigger('blur').trigger('change');
                    } else {
                        $("#entrustPrice_" + currentTab).val(param.entrustPrice).trigger('blur').trigger('change');
                    }
                }

                if (param.entrustAmt) {
                    $("#entrustAmt_" + currentTab).val(param.entrustAmt).trigger('blur').trigger('change');
                }

                //打开开关
                setPageItemsFlag = false;
                if (param.targetType) {
                    $("#targetType_" + currentTab).val(param.targetType).trigger('change');
                }

                if (param.entrustNum) {
                    $("#entrustNum_" + currentTab).val(param.entrustNum).trigger('blur').trigger('change');
                }
                if (param.invAim) {
                    $("#invAim_" + currentTab).val(param.invAim).trigger('change');
                }
                if (param.counterId) {
                    $("#counterId_" + currentTab).textSelect('setValue', param.counterId);
                }
                if (param.seatId) {
                    $("#seatId_" + currentTab).val(param.seatId).trigger('change');
                }
                if (param.agreeOnId) {
                    $("#agreeOnId_" + currentTab).val(param.agreeOnId).trigger('change');
                }
                if (param.counterOrg) {
                    $("#counterOrg_" + currentTab).val(param.counterOrg).trigger('change');
                }
                if (param.counterTrader) {
                    $("#counterTrader_" + currentTab).val(param.counterTrader).trigger('change');
                }
                if (param.trader) {
                    $("#trader_" + currentTab).val(param.trader).trigger('change');
                }
            }
            //计算价格和收益率
            calcPriceAndYield();
        }
    //add by chenjiaxing 持仓页面点击未带出净价 end
    /**
     * 根据传入的数据，设置画面元素
     */
    var setEntrust001PageItems = function (param) {
        setPageItemsFlag = true;
        if (param) {
            if (param.entrustType) {
                linkTradePlat(param.entrustType, false);
            }
            if (param.entrustId) {
                $("#entrustId_" + currentTab).val(param.entrustId).trigger('change');
            }
            if (param.entrustStatus) {
                $("#entrustStatus_" + currentTab).val(param.entrustStatus).trigger('change');
            }
            if (param.portfolioId) {
                $("#portfolioId_" + currentTab).textSelect('setValue', param.portfolioId);
            }
            if (param.acctNo) {
                $("#acctNo_" + currentTab).textSelect('setValue', param.acctNo);
            }
            if (param.secId) {
                $("#secId_" + currentTab).textSelect('setValue', param.secId);
            }
            /*if(param.finprodType2) {
                $("#finprodType2_" + currentTab).val(param.finprodType2).trigger('change');
            }*/
            if (param.ps) {
                $("#ps_" + currentTab).textSelect('setValue', param.ps);
            }
            if (param.tradeMarket) {
                $("#tradeMarket_" + currentTab).val(param.tradeMarket).trigger('change');
            }
            if (param.dataSource) {
                $("#dataSource_" + currentTab).val(param.dataSource).trigger('change');
            }
            if (param.priceModel) {
                $("#priceModel_" + currentTab).val(param.priceModel).trigger('change', true);
            }
            if (param.declaType) {
                $("#declaType_" + currentTab).val(param.declaType).trigger('change', true);
            }
            if (param.calMode) {
                //$("#declaType_" + currentTab).val(param.calMode).trigger('change');
                $("input[name=calculateField_" + currentTab + "][value='" + param.calMode + "']").attr("checked", true);
                layui.form.render(); //更新
                calculateFieldChange();
            }
            // 需要根据金融产品类型设置不同的价格
            if (param.price) {
                if ($("#finprodType2_" + currentTab).val() == FINPROD_TYPE_TG_F01 && isStandardPs(param.ps)) {
                    if (param.calMode && param.calMode == SEC_CALC_MODE_01) {
                        $("#price_" + currentTab).val(param.price).trigger('blur').trigger('change');
                    }
                } else {
                    $("#entrustPrice_" + currentTab).val(param.price).trigger('blur').trigger('change');
                }
            }
            if (param.calMode && param.calMode == SEC_CALC_MODE_02 && param.fprice) {
                $("#fprice_" + currentTab).val(param.fprice).trigger('blur').trigger('change');
            }
            if (param.calMode && param.calMode == SEC_CALC_MODE_03 && param.yield) {
                $("#yield_" + currentTab).val(param.yield).trigger('blur').trigger('change');
            }
            if (param.entrustPrice) {
                if ($("#finprodType2_" + currentTab).val() == FINPROD_TYPE_TG_F01 && isStandardPs(param.ps)) {
                    $("#price_" + currentTab).val(param.entrustPrice).trigger('blur').trigger('change');
                } else {
                    $("#entrustPrice_" + currentTab).val(param.entrustPrice).trigger('blur').trigger('change');
                }
            }

            if (param.entrustAmt) {
                $("#entrustAmt_" + currentTab).val(param.entrustAmt).trigger('blur').trigger('change');
            }

            //打开开关
            setPageItemsFlag = false;
            if (param.targetType) {
                $("#targetType_" + currentTab).val(param.targetType).trigger('change');
            }

            if (param.entrustNum) {
                $("#entrustNum_" + currentTab).val(param.entrustNum).trigger('blur').trigger('change');
            }
            if (param.invAim) {
                $("#invAim_" + currentTab).val(param.invAim).trigger('change');
            }
            if (param.counterId) {
                $("#counterId_" + currentTab).textSelect('setValue', param.counterId);
            }
            if (param.seatId) {
                $("#seatId_" + currentTab).val(param.seatId).trigger('change');
            }
            if (param.agreeOnId) {
                $("#agreeOnId_" + currentTab).val(param.agreeOnId).trigger('change');
            }
            if (param.counterOrg) {
                $("#counterOrg_" + currentTab).val(param.counterOrg).trigger('change');
            }
            if (param.counterTrader) {
                $("#counterTrader_" + currentTab).val(param.counterTrader).trigger('change');
            }
            if (param.trader) {
                $("#trader_" + currentTab).val(param.trader).trigger('change');
            }
        }
        //计算价格和收益率
        calcPriceAndYield();

    }
    /**
     * 取消,清空指令数据
     */
    var doReset = function (opt) {
        //设置指令记录操作类型
        optType = ENTRUST_RECORD_NEW;
        //清空操作开始
        resetFlag = true;
        $("#entrustId_" + currentTab).val('').trigger('change');
        $("#entrustStatus_" + currentTab).val('').trigger('change');
        if (!opt) {
            $("#portfolioId_" + currentTab).val("").trigger('change');
            $("#acctNo_" + currentTab).val("").trigger('change');
        }
        $("#secId_" + currentTab).val("").trigger('change');
        $("#ps_" + currentTab).val('').trigger('change');
        $("#priceModel_" + currentTab).val('').trigger('change', true);
        $("#declaType_" + currentTab).val('').trigger('change', true);
        $("#price_" + currentTab).val('').trigger('change');
        $("#fprice_" + currentTab).val('').trigger('change');
        $("#yield_" + currentTab).val('').trigger('change');
        $("#entrustPrice_" + currentTab).val('').trigger('change');
        $("#targetType_" + currentTab).val('').trigger('change');
        $("#entrustNum_" + currentTab).val('').trigger('change');
        $("#entrustAmt_" + currentTab).val('').trigger('change');
        $("#toShareAvailableNum_" + currentTab).val('').trigger('change');
//         $("#invAim_" + currentTab).val('').trigger('change');
        $("#counterId_" + currentTab).val('').trigger('change');
        $("#seatId_" + currentTab).val('').trigger('change');
        $("#agreeOnId_" + currentTab).val('').trigger('change');
        $("#counterOrg_" + currentTab).val('').trigger('change');
        $("#counterTrader_" + currentTab).val('').trigger('change');
        $("#trader_" + currentTab).val('').trigger('change');
        $("#settleSpeed_" + currentTab).val('').trigger('change');
        /***
         * 清空行情列表
         */
        if (currentTab == 'jj') {
            parent.EtrExchangeTradeJjHq.updateLevel2();
        }


        if (currentTab == 'dz') {
            parent.EtrExchangeTradeJjHq.clearDzHqTable();
        }
        //清空操作结束
        resetFlag = null;
        //清空风险试算结果
        riskResultdata = null;
        cluflag = true;

        controlModifyView(false);
    }
    /**
     * 获取当前tab页
     * @return
     */
    var getCurrentTab = function () {
        return currentTab;
    }
    /**
     * 风险试算URL
     * @returns
     */
    var getRiskInterfUrl = function () {

        var object = getSearchParams();
        var riskInterfUrl;
        if (object) {

            //债券
            if (object.finprodType2 == FINPROD_TYPE_TG_F01) {
                if (object.ps == PS_T49) {
                    riskInterfUrl = basePath + 'riskrds/interf/doInfData/0/RSK011/F01';
                } else {
                    riskInterfUrl = basePath + 'riskrds/interf/doInfData/0/RSK002/F01';
                }
            }
            //基金
            else if (object.finprodType2 == FINPROD_TYPE_TG_F06) {
                riskInterfUrl = basePath + 'riskrds/interf/doInfData/0/RSK002/F02';
            }
            //股票
            else if (object.finprodType2 == FINPROD_TYPE_TG_F10) {
                riskInterfUrl = basePath + 'riskrds/interf/doInfData/0/RSK002/F11';
            }
            //质押式回购
            else if (object.finprodType2 == FINPROD_TYPE_TG_F02 || object.finprodType2 == FINPROD_TYPE_TG_F03) {
                riskInterfUrl = basePath + 'riskrds/interf/doInfData/0/RSK002/F07';
            }

        }
        return riskInterfUrl;
    }

    function compare(arg) { //这是比较函数
        return function (m, n) {
            var a = m[arg];
            var b = n[arg];
            return a - b; //升序
        }
    }

    /**
     * 获取最小价差
     */
    var getMinPrice = function (obj) {
        if (!obj) {
            return null;
        }
        //设置默认值
        obj.minPrice = 0.001;
        obj.multiple = 1000;
        obj.scale = 3;
        if (!minPriceWhole) {
            return obj;
        } else {
            var obj = formatSecIdData(obj);
            var filter;
            if (obj.tradePlat && obj.tradeMarket && obj.assetType) {
                filter = minPriceWhole.filter(m => m.tradePlat == obj.tradePlat).filter(m => m.tradeMarket == obj.tradeMarket).filter(m => m.assetType == obj.assetType);
            }
            //资产分类配置规则:  1)资产一级分类,资产二级分类不细分;
            //             2)只有资产一级 分类 细分,资产二级分类不细分;
            //             3)资产一级分类 细分,资产二级分类细分;
            //同一交易平台,同一交易场所,同一资产大类【平安资产类型】过滤后仍然有多条(>1)记录
            //同一细则,仅可以配置不分档共1条记录，或者分档共2-n条记录；
            if (null != filter && filter.length > 1) {
                var exactFilter;
                var assetType1 = filter[0].assetType1;
                var assetType2 = filter[0].assetType2;
                if (assetType1 && obj.assetType1) {
                    exactFilter = filter.filter(m => m.assetType1 == obj.assetType1);
                    if (null != exactFilter && exactFilter.length > 0) {
                        filter = exactFilter;
                    }
                    if (assetType2 && obj.assetType2) {
                        exactFilter = filter.filter(m => m.assetType2 == obj.assetType2);
                        if (null != exactFilter && exactFilter.length > 0) {
                            filter = exactFilter;
                        }
                    }
                }
            }
            if (null != filter && filter.length == 1) {
                var minPrice = filter[0].minPrice;
                if (minPrice) {
                    obj.minPrice = minPrice;
                    obj.multiple = 1 / minPrice;
                    var precision = parseInt(obj.multiple) - 1;
                    obj.scale = precision <= 0 ? 0 : precision.toString().length;
                }
            } else if (null != filter && filter.length > 1) {
                var minPriceObj = null;
                filter = filter.sort(compare("gradeAmt"));
                for (var i in filter) {
                    var gradeAmt = filter[i].gradeAmt;
                    var gradeCondition = filter[i].gradeCondition;
                    var flag = gradeAmt - obj.price;
                    if (gradeCondition == CONTROL_TYPE_03) {
                        if (flag > 0) {
                            minPriceObj = filter[i];
                        }
                    } else if (gradeCondition == CONTROL_TYPE_04) {
                        if (flag >= 0) {
                            minPriceObj = filter[i];
                        }
                    } else if (gradeCondition == CONTROL_TYPE_01) {
                        if (flag < 0) {
                            minPriceObj = filter[i];
                        }
                    } else if (gradeCondition == CONTROL_TYPE_02) {
                        if (flag <= 0) {
                            minPriceObj = filter[i];
                        }
                    }else{
                        minPriceObj = filter[i];
                    }
                    if (minPriceObj) {
                        var minPrice = minPriceObj.minPrice;
                        obj.minPrice = minPrice;
                        obj.multiple = 1 / minPrice;
                        var precision = parseInt(obj.multiple) - 1;
                        obj.scale = precision <= 0 ? 0 : precision.toString().length;
                        return obj;
                    }
                }
            }
            setScale(obj);
            return obj;
        }
    }

    /**
     * 格式化证券代码关联的对象
     */
    var formatSecIdData = function (obj) {
        var tradePlat = currentTab;
        var tradeMarket = obj.tradeMarket;
        if (tradePlat == "jj") {
            tradePlat = SHARES_TRADE_PLATFORM_02;
        } else if (tradePlat == "dz") {
            if (tradeMarket && tradeMarket == TRADE_PLACE_03) {
                tradePlat = SHARES_TRADE_PLATFORM_04;
            } else if (tradeMarket && tradeMarket == TRADE_PLACE_04) {
                tradePlat = SHARES_TRADE_PLATFORM_05;
            }
        } else if (tradePlat == "gd") {
            tradePlat = SHARES_TRADE_PLATFORM_03;
        }
        obj.tradePlat = tradePlat;
        obj.assetType = '';
        if (obj.finprodType2) {
            if (obj.finprodType2 == FINPROD_TYPE_TG_F01) {
                obj.assetType = FINPROD_TYPE_TG_F01;
                if (obj.tradeMarket == TRADE_PLACE_03) {
                    obj.assetType1 = null;
                    obj.assetType2 = null;
                }
            } else {
                obj.assetType1 = null;
                obj.assetType2 = null;
                if (obj.finprodType2 == FINPROD_TYPE_TG_ZYSHG || obj.finprodType2 == FINPROD_TYPE_TG_F02 || obj.finprodType2 == FINPROD_TYPE_TG_F03) {
                    obj.assetType = 'F07';
                } else if (obj.finprodType2 == FINPROD_TYPE_TG_F06) {
                    obj.assetType = 'F02';
                } else if (obj.finprodType2 == FINPROD_TYPE_TG_F10) {
                    obj.assetType = 'F11';
                }
            }
        }
        return obj;
    }

    /**
     * 格式化指令价格
     */
    var formatPrice = function ($price, $price2) {
        var $secIdObj = $("#secIdObjRelative_" + currentTab).val();
        if ($secIdObj) {
            var $obj = JSON.parse($secIdObj);
            $.extend($obj, {
                price: $price
            });
            var precision = getMinPrice($obj).scale;
            if (precision >= 0) {
                setScale($obj);
                if ($price2) {
                    return (Math.round($price2 / $obj.minPrice) * $obj.minPrice).toFixed(precision);
                } else
                    return (Math.round($price / $obj.minPrice) * $obj.minPrice).toFixed(precision);
            }
        }
        return $price;
    }

    /**
     * 获取行情中的价格
     */
    var getMktPrice = function () {
        if (currentTab == "jj") {
            var secId = $("#secId_" + currentTab).val();
            var secName = $("#secName_" + currentTab).val();
            var finprodType2 = $("#finprodType2_" + currentTab).val();
            var jJHQXX = queryJJHQXX(secId, secName, finprodType2);
            var ps = $("#ps_" + currentTab).val();
            console.log(finprodType2);
            if (finprodType2 == FINPROD_TYPE_TG_F06 || finprodType2 == FINPROD_TYPE_TG_F10) {
                /*if (ps == PS_T01) {
                    //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#limitUp_jj");
                    return jJHQXX.ceiling.toFixed(2);
                } else if (ps == PS_T02) {
                    //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#limitDown_jj");
                    return jJHQXX.ceiling.toFixed(2);
                } else {
                    //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#latestPrice_jj");
                    return jJHQXX.ceiling.toFixed(2);
                }*/
                return jJHQXX.ceiling.toFixed(2);
            } else {
                // rent.EtrExchangeTradeJjHq.getJJMktPrice("#latestPrice_jj");
                return jJHQXX.latest.toFixed(2);
            }
        } else if (currentTab == "dz") {
            var secId = $("#secId_" + currentTab).val();
            var secName = $("#secName_" + currentTab).val();
            var finprodType2 = $("#finprodType2_" + currentTab).val();
            var jJHQXX = queryJJHQXX(secId, secName, finprodType2);

            var ps = $("#ps_" + currentTab).val();
            if (finprodType2 == FINPROD_TYPE_TG_F06 || finprodType2 == FINPROD_TYPE_TG_F10) {
               /* if (ps == PS_T01) {
                    //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#limitUp_jj");
                    return jJHQXX.ceiling.toFixed(2);
                } else if (ps == PS_T02) {
                    //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#limitDown_jj");
                    return jJHQXX.ceiling.toFixed(2);
                } else {
                    //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#latestPrice_jj");
                    return jJHQXX.ceiling.toFixed(2);
                }*/
                return jJHQXX.ceiling.toFixed(2);
            } else {
                //return parent.EtrExchangeTradeJjHq.getJJMktPrice("#latestPrice_jj");
                return jJHQXX.latest.toFixed(2);
            }
        } else if (currentTab == "gd") {
            return $("#price_gd").get(0).value;
        }
        return '';
    };
    /**根据市场代码，查询行情信息，展示出来*/
    var queryJJHQXX = function (secId, secName, finprodType) {
        var jJHQXX = {};
        $.ajax({
            /**临时用这个地址，后面调整！！！！**/
            //url: "http://192.168.90.105:8080/tams/joyin/quote/tick?stock_code="+secId,
            url: basePath + "joyin/quote/tick?stock_code=" + secId,
            async: false,
            type: "GET",
            dataType: "json",
            success: function (info) {
                var result = info.data;
                if (result != null && result != '') {
                    var xtradeTickData = new Object();
                    xtradeTickData.stockCode = secId;
                    xtradeTickData.stockName = secName;
                    xtradeTickData.finprodType = finprodType;
                    //    this.stockName = stock_name;
                    //   this.updateTime = tick[0] * 1000;
                    xtradeTickData.open = result.openPrice;
                    xtradeTickData.highest = result.highPrice;
                    xtradeTickData.lowest = result.lowPrice;
                    xtradeTickData.latest = result.lastPrice;
                    xtradeTickData.preclose = result.preClosePrice;
                    xtradeTickData.tradeVolume = result.volume || 0;
                    xtradeTickData.tradeAmount = result.tradeBalance || 0;
                    xtradeTickData.ceiling = result.upperLimitPrice;
                    xtradeTickData.floor = result.lowerLimitPrice;
                    xtradeTickData.change = xtradeTickData.latest - xtradeTickData.preclose;
                    xtradeTickData.changePercent = xtradeTickData.change / (xtradeTickData.preclose > 0 ? xtradeTickData.preclose : 1);

                    /**
                     * 卖出数量，顺序，1档 ~ 10档
                     */
                    xtradeTickData.sellVolumes = result.askVolume instanceof Array ? result.askVolume : [];

                    /**
                     * 卖出报价，顺序，1档 ~ 10档
                     */
                    xtradeTickData.sells = result.askPrice instanceof Array ? result.askPrice : [];

                    /**
                     * 买入数量，顺序，10档 ~ 1档
                     */
                    xtradeTickData.buyVolumes = result.bidVolume instanceof Array ? result.bidVolume : [];

                    /**
                     * 买入报价，顺序，10档 ~ 1档
                     */
                    xtradeTickData.buys = result.bidPrice instanceof Array ? result.bidPrice : [];
                    jJHQXX = xtradeTickData;
                } else {
                }
            }
        });
        return jJHQXX;

    }
    /**
     * 组合代码变更时，联动其他字段
     * 组合代码变更时会联动资金账户，此处会自动调用资金账户的联动事件。
     */
    var portfolioIdChange = function (init) {
        linkAcctNo(init);
        linkZqzyPos(init);
    };

    /**
     * 证券代码变更时，联动其他字段
     * 联动字段：证券名称、金融产品类型、交易场所、金融产品代码
     * 此处联动金融产品类型，再通过金融产品类型联动委托方向
     */
    var secIdChange = function () {
        setRiskFlag(1);
        // 证券代码改变，清空数据来源
        $("#dataSource_" + currentTab).val('').trigger('change');
        var secId = $("#secId_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        if (secId) {
            var object = getSecIdSelectObjectData(secId);
            $("#secIdObjRelative_" + currentTab).val(JSON.stringify(object)).trigger('change');
            if (currentTab == 'gd') {
                // if (object) {//固定收益平台 设置清算速度
                //     setSettleSpeed(object.isNetSett);
                // }
                let parms = {
                    finprodId: secId
                }
                Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/IsSpeedTOneOrZero', parms,
                    function (result) {
                        setSettleSpeed(result.data);
                    })
            }
        }
        // 证券代码选择后联动【行情信息】
        if (currentTab == 'jj') {
            // if(parent.EtrExchangeTradeJjHq){
            //     parent.EtrExchangeTradeJjHq.showFiveLevel();
            // }
            if (secId) {
                var object = $("#secIdObjRelative_" + currentTab).val();
                if (object) {
                    object = JSON.parse(object);
                    type9 = object.assetType1;
                    if (object.othCodeType && (object.othCodeType == FINPROD_OTH_CODE_TYPE_06 || object.othCodeType == FINPROD_OTH_CODE_TYPE_05 || object.othCodeType == FINPROD_OTH_CODE_TYPE_07)) {
                        secId = object.marketId;
                        // if(parent.EtrExchangeTradeJjHq) {
                        //     parent.EtrExchangeTradeJjHq.hideFiveLevel();
                        // }
                        $("#portfolioId_div_" + currentTab).show();
                    }
                    if (ps == PS_T04) {
                        $("#portfolioId_div_" + currentTab).show();
                    }
                    //if (secId) {
                        // if(parent.EtrExchangeTradeJjHq) {
                        //     if (useInterfQuoteFlag) {
                        //         parent.EtrExchangeTradeJjHq.queryJJHQXXByWS(secId, object.finprodName, object.finprodType2);
                        //     } else {
                        //         parent.EtrExchangeTradeJjHq.queryJJHQXX(secId, object.finprodName, object.finprodType2);
                        //     }
                        // }
                        /*if(ps == PS_T68 || ps == PS_T69){
                        	linkToConverseRatio();
                        }else if(ps == PS_T03){
                        	linkToExercisePrice();
                        }else if(ps == PS_T49){
                        	linkToConverseSharePrice();
                        }else if(ps == PS_T04){
                        	linkToBondSubscribe();
                        }   */
                  //  } else {
                        // if(parent.EtrExchangeTradeJjHq) {
                        //     parent.EtrExchangeTradeJjHq.updateLevel2();
                        // }
                   // }
                }
            }
        } else if (currentTab == 'dz') {
            if (secId) {
                if (parent.EtrExchangeTradeJjHq) {
                    parent.EtrExchangeTradeJjHq.loadDzHqTable();
                }
            } else {
                //清空行情列表
                if (parent.EtrExchangeTradeJjHq) {
                    parent.EtrExchangeTradeJjHq.clearDzHqTable();
                }
            }
        } else if (currentTab == 'gd') {
            if (secId) {
                if (parent.EtrExchangeTradeJjHq) {
                    parent.EtrExchangeTradeJjHq.loadGdHqTable();
                }
            } else {
                //清空行情列表
                if (parent.EtrExchangeTradeJjHq) {
                    parent.EtrExchangeTradeJjHq.clearGdHqTable();
                }
            }
        }
        if (secId) {
            var object = $("#secIdObjRelative_" + currentTab).val();
            if (object) {
                object = JSON.parse(object);
                $("#finprodId_" + currentTab).val(object.finprodId).trigger('change');
                $("#finprodName_" + currentTab).val(object.finprodName).trigger('change');
                $("#tradeMarket_" + currentTab).val(object.tradeMarket).trigger('change');
                $("#finprodType2_" + currentTab).val(object.finprodType2 || FINPROD_TYPE_TG_ZYSHG).trigger('change');
                $("#board_" + currentTab).val(object.board).trigger('change');
                /*if(currentTab == 'dz' || currentTab == "gd"){
                	queryJJHQPrice({secId: secId, finprodType2: object.finprodType2,ps: ps});
                }*/
            } else {
                $("#finprodId_" + currentTab).val('').trigger('change');
                $("#finprodName_" + currentTab).val('').trigger('change');
                $("#tradeMarket_" + currentTab).val('').trigger('change');
                $("#finprodType2_" + currentTab).val('').trigger('change');
                $("#board_" + currentTab).val('').trigger('change');
                if (type9) {
                    type9 = '';
                }
            }
            var param1 = {};
            var finprodId = $("#finprodId_" + currentTab).val();
            param1.secId = secId;
            Joyin.ajaxSync(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/IsBondAsset', param1, function(result) {
                if(result.data=='2') {     //2 返回2代表所选资产是债券且中债sppi通过
                    $("#businessModelDiv_"+ currentTab).show();
                    //$("#businessModel_" + currentTab).val("").trigger('change');
                    $("#businessModel_" + currentTab).prop("disabled", true);
                }else if(result.data=='3'){ //3 返回3代表所选资产是债券且中债sppi不通过
                    $("#businessModelDiv_"+ currentTab).show();
                    $("#businessModel_" + currentTab).val("FVTPL").trigger('change');
                    $("#businessModel_" + currentTab).prop("disabled", true);
                }else if(result.data=="4"){
                    Joyin.error("当前资产未进行SPPI测试，无法进行指令提交/保存。");
                    $("#secId_" + currentTab).val("").trigger('change');
                }else if(result.data=='1'){
                    $("#businessModelDiv_"+ currentTab).hide();
                }
            });
        }
        else {
            $("#finprodId_" + currentTab).val('').trigger('change');
            $("#finprodName_" + currentTab).val('').trigger('change');
            $("#tradeMarket_" + currentTab).val('').trigger('change');
            $("#finprodType2_" + currentTab).val('').trigger('change');
            $("#board_" + currentTab).val('').trigger('change');
            if (type9) {
                type9 = '';
            }
            ;
        }
    };

    var setScale = function (obj) {
        var ps = $("#ps_" + currentTab).val();
        if (obj.finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
            $("#price_" + currentTab).attr("data-textrange", obj.scale);
            $("#fprice_" + currentTab).attr("data-textrange", obj.scale);
        } else {
            $("#entrustPrice_" + currentTab).attr("data-textrange", obj.scale);
        }
    }
    /**
     * 交易场所变更时
     */
    var tradeMarketChange = function () {
        linkSecAcctId();
    };
    /**
     * 联动证券代码用交易场所变更时，联动证券代码
     */
    var acctTradeMarketChange = function () {
        // 判断当前选择的证券代码对应的交易场所是否与账户对应的交易场所一致，不一致则清空证券代码
        var acctTradeMarket = $("#acctTradeMarket_" + currentTab).val();
        var secId = $("#secId_" + currentTab).val();
        var object = $("#secIdObjRelative_" + currentTab).val();
        if (acctTradeMarket && secId) {
            if (object) {
                object = JSON.parse(object);
                if (object.tradeMarket != acctTradeMarket) {
                    $("#secId_" + currentTab).val('').trigger('change');
                }
            } else {
                $("#secId_" + currentTab).val('').trigger('change');
            }
        }
    };
    /**
     * 金融产品类型（投管）变更时
     */
    var finprodType2Change = function () {
        // 此处联动委托方向会带动价格模式和委托价格显示隐藏
        linkPs();
        linkEntrustNumUnit();
        //add by chenjiaxing 股票与基金时投资目的字段不显示 start
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        if ((currentTab == 'jj' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06)
            || (currentTab == 'dz' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06)
            || (currentTab == 'gd' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06)) {
            $("#invAim_div_" + currentTab).hide();
        } else if ((currentTab == 'jj' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10)
            || (currentTab == 'dz' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10)
            || (currentTab == 'gd' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10)) {
            $("#invAim_div_" + currentTab).hide();
        } else if ((currentTab == 'jj' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_ZYSHG)
            || (currentTab == 'dz' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_ZYSHG)
            || (currentTab == 'gd' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_ZYSHG)) {
            $("#invAim_div_" + currentTab).hide();
        } else {
            var ps = $("#ps_" + currentTab).val();
            if (ps == PS_T68 || ps == PS_T69 || (ps == PS_T04 && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F01)) {
                $("#invAim_div_" + currentTab).hide();
            } else {
                $("#invAim_div_" + currentTab).show();
            }
        }
        //add by chenjiaxing 股票与基金时投资目的字段不显示 end
    };
    /**
     * 资金账户选择之后的联动事件
     */
    var acctNoChange = function (init) {
        var acctNo = $("#acctNo_" + currentTab).val();
        setRiskFlag(1);
        if (acctNo) {
            var list = getAcctNoSelectList(null, acctNo);
            // 获取对应的交易场所和证券账户，如果只有一个，则设置对应的值，否则不设置
            if (list && list.length > 0 && list[0].object && list[0].object.length == 1) {
                $("#acctTradeMarket_" + currentTab).val(list[0].object[0].tradeMarket).trigger('change');
            } else {
                $("#acctTradeMarket_" + currentTab).val('').trigger('change');
            }
        } else {
            $("#acctTradeMarket_" + currentTab).val('').trigger('change');
        }
        var secAcctId = $("#secAcctId_" + currentTab).val();
        //if(!secAcctId){  修复bug场内指令推送到场内，资金账户不对，指令表中sec_Acct_id登录错误
        linkSecAcctId();
        //}
        // 资金账户联动可用现金
        linkAccAvailableCash();
    };
    /**
     * 证券托管户变更后需要查询可用持仓
     */
    var secAcctIdChange = function () {
        linkAvailableNum();
    };
    /**
     * 价格模式选择后的联动处理
     */
    var priceModelChange = function (init, isNotTrigger) {
        setRiskFlag(1);
        var priceModel = $("#priceModel_" + currentTab).val();
        var declaType = $("#declaType_" + currentTab).val();
        var finprodId = $("#finprodId_" + currentTab).val();
        // 十二.价格模式和指令价格的联动
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        var secId = $("#secId_" + currentTab).val();
        if (priceModel) {
            // if 画面.价格模式 = 市价指令  and 点击成交成报
            if (priceModel == PRICE_MODEL_PRM03 || (currentTab == "dz" && declaType == DECLA_TYPE_PRM08)) {
                if (priceModel == PRICE_MODEL_PRM03) {
                    var obj = {secId: secId, finprodType2: finprodType2, ps: ps};
                    queryJJHQPrice(obj);
                }
                if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                    $("#price_" + currentTab).attr("disabled", true);
                    /*if (priceModel == PRICE_MODEL_PRM03) {
                        $("#price_" + currentTab).val(zeroPrice).trigger('change');
                    }*/
                    $("input[name=calculateField_" + currentTab + "]").attr("disabled", true);
                    $("#price_div_" + currentTab).find(':radio[value="01"]').prop('checked', true);
                    layui.form.render();
                    // 单选按钮不会自动触发change事件，需要手动调用
                    calculateFieldChange();
                } else {
                    $("#entrustPrice_" + currentTab).attr("disabled", true);
                    $("#entrustPrice_" + currentTab).val(zeroPrice).trigger('change');
                }
            } else {
                // 默认隐藏可转股数量
                $("#toShareAvailableNum_div_" + currentTab).hide();
                if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                    //$("#price_" + currentTab).attr("disabled", false);
                    calculateFieldChange();
                    $("input[name=calculateField_" + currentTab + "]").attr("disabled", false);
                    $("input[name=calculateField_" + currentTab + "]").next().removeClass('layui-radio-disbaled layui-disabled');
                }
                // else if 画面.金融产品类型 = 债券 and 画面.交易方向=数据字典.交易方向.债转股
                else if (finprodType2 == FINPROD_TYPE_TG_F01 && ps == PS_T49) {
                    $("#entrustPrice_" + currentTab).attr("disabled", true);
                    // 画面.可转股数量 展示，不可编辑.
                    $("#toShareAvailableNum_div_" + currentTab).show();
                    // 联动可转股数量
                    linkToShareAvailableNum();
                }
                // else if 画面.金融产品类型 = 债券 and （画面.交易方向=数据字典.交易方向.质押入库 or 质押出库 or 行权-回售） 配债
                else if (finprodType2 == FINPROD_TYPE_TG_F01 && (ps == PS_T97 || ps == PS_T68 || ps == PS_T69 || ps == PS_T04 || (ps == PS_T03))) {
                    $("#entrustPrice_" + currentTab).attr("disabled", true);
                } else if (finprodType2 == FINPROD_TYPE_TG_F10 && ps == PS_T46) {//配股
                    $("#entrustPrice_" + currentTab).attr("disabled", true);
                } else {
                    $("#entrustPrice_" + currentTab).attr("disabled", false);
                }
            }
            if (!init && !isNotTrigger) {
                if (priceModel != PRICE_MODEL_PRM03) {
                    // 获取行情价
                    if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                        $("#price_" + currentTab).val(formatPrice(getMktPrice())).trigger('blur').trigger('change');
                    } else {
                        $("#entrustPrice_" + currentTab).val(formatPrice(getMktPrice())).trigger('blur').trigger('change');
                    }
                }
            }
        } else {
            if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                $("#price_" + currentTab).val('').trigger('blur').trigger('change');
            } else {
                $("#entrustPrice_" + currentTab).val('').trigger('blur').trigger('change');
            }
        }
        // 十五.申报类型和席位号、交易对手、约定号联动
        // 联动画面.席位号、交易对手、约定号
        if (currentTab == 'dz') {
            // 画面.价格模式 IN（意向申报，定价申报，收盘价固定价格申报，日均价固定价格申报）
            if (declaType == DECLA_TYPE_PRM06 || declaType == DECLA_TYPE_PRM07 || declaType == DECLA_TYPE_PRM10 || declaType == DECLA_TYPE_PRM11) {
                // 画面.席位号、交易对手、约定号 隐藏
                $("#counterId_" + currentTab).val('').trigger('change');
                $("#seatId_" + currentTab).val('').trigger('change');
                $("#agreeOnId_" + currentTab).val('').trigger('change');
                $("#counterId_div_" + currentTab).hide();
                $("#seatId_div_" + currentTab).hide();
                $("#agreeOnId_div_" + currentTab).hide();
            }
            // 画面.价格模式 IN（点击成交申报，成交申报，不指定）
            else if (declaType == DECLA_TYPE_PRM05 || declaType == DECLA_TYPE_PRM08 || declaType == DECLA_TYPE_PRM09) {
                // 画面.席位号、交易对手、约定号展示，可编辑
                $("#counterId_div_" + currentTab).show();
                $("#seatId_div_" + currentTab).show();
                $("#agreeOnId_div_" + currentTab).show();
            }
            if (declaType == DECLA_TYPE_PRM08) {
                $("#seatId_" + currentTab).attr("disabled", true);
                $("#agreeOnId_" + currentTab).attr("disabled", true);
            } else {
                $("#seatId_" + currentTab).attr("disabled", false);
                $("#agreeOnId_" + currentTab).attr("disabled", false);
            }
        }
    };

    /**
     * 申报类型的联动处理
     */
    var declaTypeChange = function (init, isNotTrigger) {
        declaTypeChange(init, isNotTrigger);
    };


    /**
     * 价格模式选择后的联动处理
     */
    var declaTypeChange = function (init, isNotTrigger) {
        setRiskFlag(1);
        var declaType = $("#declaType_" + currentTab).val();
        // 十五.申报类型和席位号、交易对手、约定号联动
        // 联动画面.席位号、交易对手、约定号
        if (currentTab == 'dz') {
            // 画面.价格模式 IN（意向申报，定价申报，收盘价固定价格申报，日均价固定价格申报）
            if (declaType == DECLA_TYPE_PRM06 || declaType == DECLA_TYPE_PRM07 || declaType == DECLA_TYPE_PRM10 || declaType == DECLA_TYPE_PRM11) {
                // 画面.席位号、交易对手、约定号 隐藏
                $("#counterId_" + currentTab).val('').trigger('change');
                $("#seatId_" + currentTab).val('').trigger('change');
                $("#agreeOnId_" + currentTab).val('').trigger('change');
                $("#counterId_div_" + currentTab).hide();
                $("#seatId_div_" + currentTab).hide();
                $("#agreeOnId_div_" + currentTab).hide();
            }
            // 画面.价格模式 IN（点击成交申报，成交申报，不指定）
            else if (declaType == DECLA_TYPE_PRM05 || declaType == DECLA_TYPE_PRM08 || declaType == DECLA_TYPE_PRM09) {
                // 画面.席位号、交易对手、约定号展示，可编辑
                $("#counterId_div_" + currentTab).show();
                $("#seatId_div_" + currentTab).show();
                $("#agreeOnId_div_" + currentTab).show();
            }
            if (declaType == DECLA_TYPE_PRM08) {
                $("#seatId_" + currentTab).attr("disabled", true);
                $("#agreeOnId_" + currentTab).attr("disabled", true);
            } else {
                $("#seatId_" + currentTab).attr("disabled", false);
                $("#agreeOnId_" + currentTab).attr("disabled", false);
            }
        }
    };






    /**
     * 委托价格变更时联动计算委托金额
     */
    var entrustPriceChange = function () {
        formatEntrustPrice(calcEntrustNumAndAmt);
    };

    /**
     * 目标类型变更时联动计算委托金额
     */
    var targetTypeChange = function (init) {
        setRiskFlag(1);
        var targetType = $("#targetType_" + currentTab).val();
        if (targetType && targetType == TARGET_CLASS_01) {
            $("#entrustNum_" + currentTab).prop("disabled", false);
            $("#entrustAmt_" + currentTab).prop("disabled", true);
        } else {
            $("#entrustAmt_" + currentTab).prop("disabled", false);
            $("#entrustNum_" + currentTab).prop("disabled", true);
        }
        if (ps == PS_T68 || ps == PS_T69 || ps == PS_T49 || ps == PS_T03) {
            $("#entrustNum_" + currentTab).prop("disabled", false);
            $("#entrustAmt_" + currentTab).prop("disabled", true);
        }
        calcEntrustNumAndAmt(init);
    };
    /**
     * 委托数量变更时联动计算委托金额
     */
    var entrustNumChange = function () {
        setRiskFlag(1);
        calcEntrustNumAndAmt();
        linkTableEntrustNumAmt();
    };
    /**
     * 委托金额更时联动计算委托数量
     */
    var entrustAmtChange = function () {
        calcEntrustNumAndAmt();
        linkTableEntrustNumAmt();
    };
    /**
     * 委托方向变更时，需要联动计算收益率等
     */
    var psChange = function (init) {
        setRiskFlag(1);
        linkPortfolio();
        linkDeclaType();
        controlEntrustPrice();
        calcPriceAndYield(init);
        linkTargetType();
        linkAvailableNum();

        var finprodType2 = $("#finprodType2_" + currentTab).val();
        if ((currentTab == 'jj' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06)
            || (currentTab == 'dz' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06)
            || (currentTab == 'gd' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06)) {
            $("#invAim_div_" + currentTab).hide();
        } else if ((currentTab == 'jj' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10)
            || (currentTab == 'dz' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10)
            || (currentTab == 'gd' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10)) {
            $("#invAim_div_" + currentTab).hide();
        } else if ((currentTab == 'jj' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_ZYSHG)
            || (currentTab == 'dz' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_ZYSHG)
            || (currentTab == 'gd' && finprodType2 && finprodType2 == FINPROD_TYPE_TG_ZYSHG)) {
            $("#invAim_div_" + currentTab).hide();
            //linkFindSecAmountInfo();
        } else {
            //质押券出入库不展示投资目的
            var ps = $("#ps_" + currentTab).val();
            if (ps == PS_T68 || ps == PS_T69 || ps == PS_T04 || ps == PS_T49 || ps == PS_T03) {
                //$("#invAim_div_" + currentTab).hide();
                $("#businessModelDiv_"+ currentTab).hide();
            } else {
                //$("#invAim_div_" + currentTab).show();
                $("#businessModelDiv_"+ currentTab).show();
                if(ps == PS_T02){
                    // $("#businessModel_" + currentTab).val("").trigger('change');
                    $("#businessModel_" + currentTab).prop("disabled", true);
                }
            }
            // linkFindSecAmountInfo();
        }
    };
    /**
     * 数据来源变更时
     */
    var dataSourceChange = function () {
        linkDeclaType();
    };
    /**
     * 计算方式改变时。
     * 决定采用净价、全价、到期收益率的哪一个计算另外两个
     */
    var calculateFieldChange = function (init) {
        setRiskFlag(1);
        var calculateField = $("[name=calculateField_" + currentTab + "]:radio").next(".layui-form-radioed").prev("input").val();
        // 选择的计算方式为“净价”时
        if (calculateField == SEC_CALC_MODE_01) {
            var priceModel = $("#priceModel_" + currentTab).val();
            var declaType = $("#declaType_" + currentTab).val();
            // if  画面.价格模式 = 市价指令 or 点击成交成报 or 收盘价固定价格申报 or 日均价固定价格申报
            if (priceModel == PRICE_MODEL_PRM03 || declaType == DECLA_TYPE_PRM08
                || declaType == DECLA_TYPE_PRM10 || declaType == DECLA_TYPE_PRM11) {
                $("#price_" + currentTab).attr("disabled", true);
            } else {
                $("#price_" + currentTab).attr("disabled", false);
            }
            $("#fprice_" + currentTab).attr("disabled", true);
            $("#yield_" + currentTab).attr("disabled", true);
        }
        // 选择的计算方式为“全价”时
        else if (calculateField == SEC_CALC_MODE_02) {
            $("#price_" + currentTab).attr("disabled", true);
            $("#fprice_" + currentTab).attr("disabled", false);
            $("#yield_" + currentTab).attr("disabled", true);
        }
        // 选择的计算方式为“到期收益率”时
        else if (calculateField == SEC_CALC_MODE_03) {
            $("#price_" + currentTab).attr("disabled", true);
            $("#fprice_" + currentTab).attr("disabled", true);
            $("#yield_" + currentTab).attr("disabled", false);
        }
        // 重新计算价格（包含净价和全价）和到期收益率
        calcPriceAndYield(init);
    };
    /**
     * 根据参数获取证券代码对应的Object属性
     * secId:证券代码
     */
    var getSecIdSelectObjectData = function (secId) {
        var tradePlat = SHARES_TRADE_PLATFORM_02;
        var entrustBussType = $("#entrustBussType_" + currentTab).val();
        var tradePlat_05 = null;
        if (currentTab == 'dz') {
            tradePlat = SHARES_TRADE_PLATFORM_04;
            tradePlat_05 = SHARES_TRADE_PLATFORM_05;
        } else if (currentTab == 'gd') {
            tradePlat = SHARES_TRADE_PLATFORM_03;
        }
        var result = {};
        var param = {secId: secId, entrustBussType: entrustBussType, tradePlat: tradePlat, tradePlat_05: tradePlat_05};
        Joyin.ajaxSync(basePath + 'etrexchange/common/EtrExchangeTradeCommon004/getFinprodMarketInfo', param, function (page) {
            if (page && page.root && page.root.length > 0) {
                result = page.root[0].object || {};

            }
        });

        return result;
    };
    /**
     * 根据参数获取资金账户下拉框的值
     * portfolioId:组合代码
     * acctNo:资金账户
     */
    var getAcctNoSelectList = function (portfolioId, acctNo) {
        var result = null;
        var param = {portfolioId: portfolioId, acctNo: acctNo};
        Joyin.ajaxSync(basePath + 'etrexchange/common/EtrExchangeTradeCommon004/getPtlSecAcctInfo', param, function (list) {
            result = list;
        });
        return result;
    };
    /**
     * 根据参数获取资金账户下拉框的值
     * portfolioId:组合代码
     * acctNo:资金账户
     */
    var getAcctNoSelectList = function (portfolioId, acctNo, tradeMarket) {
        var result = null;
        var param = {portfolioId: portfolioId, acctNo: acctNo, tradeMarket: tradeMarket};
        Joyin.ajaxSync(basePath + 'etrexchange/common/EtrExchangeTradeCommon004/getPtlSecAcctInfo', param, function (list) {
            result = list;
        });
        return result;
    };
    /**
     * 联动设置对应的tab
     * tradePlat：数据库中的交易平台
     * deleteOther：是否删除其他Tab
     */
    var linkTradePlat = function (entrustType, deleteOther) {
        var tradePlat;
        if (entrustType == ENTRUST_BUSS_TYPE_01 || entrustType == ENTRUST_BUSS_TYPE_02
            || entrustType == ENTRUST_BUSS_TYPE_07 || entrustType == ENTRUST_BUSS_TYPE_09
            || entrustType == ENTRUST_BUSS_TYPE_10 || entrustType == ENTRUST_BUSS_TYPE_11
            || entrustType == ENTRUST_BUSS_TYPE_12 || entrustType == ENTRUST_BUSS_TYPE_13) {
            tradePlat = SHARES_TRADE_PLATFORM_02;
        } else if (entrustType == ENTRUST_BUSS_TYPE_03 || entrustType == ENTRUST_BUSS_TYPE_04
            || entrustType == ENTRUST_BUSS_TYPE_05 || entrustType == ENTRUST_BUSS_TYPE_06) {
            tradePlat = SHARES_TRADE_PLATFORM_04;
        } else if (entrustType == ENTRUST_BUSS_TYPE_08) {
            tradePlat = SHARES_TRADE_PLATFORM_03;
        }
        // 默认竞价平台
        else {
            tradePlat = SHARES_TRADE_PLATFORM_02;
        }

        //绑定交易平台TAB切换事件
        if (parent.EtrExchangeTradeJjHq) {
            parent.EtrExchangeTradeJjHq.setTradePlatform(tradePlat, deleteOther);
        }

        if (tradePlat == SHARES_TRADE_PLATFORM_02) {
            $("#content_div_jj").show();
            $("#content_div_dz").hide();
            $("#content_div_gd").hide();
        } else if (tradePlat == SHARES_TRADE_PLATFORM_04) {
            $("#content_div_jj").hide();
            $("#content_div_dz").show();
            $("#content_div_gd").hide();
        } else if (tradePlat == SHARES_TRADE_PLATFORM_03) {
            $("#content_div_jj").hide();
            $("#content_div_dz").hide();
            $("#content_div_gd").show();
        }
    }

    var linkZqzyPos = function (init) {
        var portfolioId = $("#portfolioId_" + currentTab).val();
        if (!init) {
            window.parent.EtrExchangeTrade002.zqzyPositionRefresh(portfolioId);
        }
    }
    /**
     * 联动资金账户下拉框。
     */
    var linkAcctNo = function (init) {
        var portfolioId = $("#portfolioId_" + currentTab).val();
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        var param = {portfolioId: portfolioId/*, tradeMarket:tradeMarket*/};
        if (portfolioId) {
            var list = getAcctNoSelectList(portfolioId, null, tradeMarket);
            var acctNo = '';
            if (list && list.length > 0) {
                var acctNo = list[0].id;
                if (list && list.length == 1) {
                    $("#acctNo_div_" + currentTab).hide();
                    if (!init) {
                        $("#acctNo_" + currentTab).textSelect('setValue', acctNo);
                    }
                } else {
                    if (!init) {
                        $("#acctNo_" + currentTab).textSelect('setValue', acctNo);
                    }
                    $("#acctNo_div_" + currentTab).show();
                }
            } else {
                $("#acctNo_" + currentTab).val('').trigger('change');
                $("#acctNo_div_" + currentTab).show();
            }
        } else {
            $("#acctNo_" + currentTab).val('').trigger('change');
            $("#acctNo_div_" + currentTab).hide();
        }
        if (currentTab == 'gd' || ps == PS_T03 || ps == PS_T49 || ps == PS_T68 || ps == PS_T69) {
            $("#acctNo_div_" + currentTab).hide();
        }
    };
    /**
     * 交易场所+资金账户联动证券账户
     */
    var linkSecAcctId = function () {
        // modify by swwan GZYHTZJY-81 指令下单使用的证券账户不是绑定的那个 2021-10-28 start
        var portfolioId = $("#portfolioId_" + currentTab).val();
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        var acctNo = $("#acctNo_" + currentTab).val();
        if (tradeMarket && acctNo) {
            var list = getAcctNoSelectList(portfolioId, acctNo, tradeMarket);
            // modify by swwan GZYHTZJY-81 指令下单使用的证券账户不是绑定的那个 2021-10-28 end
            if (list && list.length > 0 && list[0].object) {
                var object = list[0].object;
                for (var index in object) {
                    if (object[index].tradeMarket == tradeMarket) {
                        $("#secAcctId_" + currentTab).textSelect('setValue', object[index].secAcctId);
                        return;
                    }
                }
                $("#secAcctId_" + currentTab).val('').trigger('change');
            } else {
                $("#secAcctId_" + currentTab).val('').trigger('change');
            }
        } else {
            $("#secAcctId_" + currentTab).val('').trigger('change');
        }

    }
    /**
     * 联动委托方向
     */
    var linkPs = function () {
        //var object = getSecIdSelectObjectData($("#secId_" + currentTab).val());
        var object = $("#secIdObjRelative_" + currentTab).val();
        if (object) {
            object = JSON.parse(object);
        }
        if (object && object.othCodeType == FINPROD_OTH_CODE_TYPE_06) {
            $("#ps_" + currentTab).updateChosenArr({
                item: [PS_T46],
                chosen: PS_T46,
                disabled: true
            });
            return;
        } else if (object && object.othCodeType == FINPROD_OTH_CODE_TYPE_05) {
            $("#ps_" + currentTab).updateChosenArr({
                item: [PS_T97],
                chosen: PS_T97,
                disabled: true
            });
            return;
        }
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        //add by chenjiaxing 资产代码清除是投资方向联动为空 start 20210826
        if (!finprodType2) {
            $("#ps_" + currentTab).val('').trigger('change');
        }
        //add by chenjiaxing 资产代码清除是投资方向联动为空 end 20210826
        // if  交易平台 = 数据字典.交易平台.大宗交易平台
        if (currentTab == 'dz') {
            // if 画面.金融产品类型（投管）= 数据字典.金融产品类型（投管）.债券质押式回购(不属于数据字典范围，仅仅判断用)
            if (finprodType2 == FINPROD_TYPE_TG_ZYSHG) {
                $("#ps_" + currentTab).updateChosenArr({
                    item: [PS_03, PS_04],
                    chosen: $("#ps_" + currentTab).val(),
                    disabled: false
                });
            } else if (!finprodType2) {
                $("#ps_" + currentTab).updateChosenArr({
                    item: [PS_T01, PS_T02, PS_03, PS_04],
                    chosen: $("#ps_" + currentTab).val(),
                    disabled: false
                });
            } else {
                $("#ps_" + currentTab).updateChosenArr({
                    item: [PS_T01, PS_T02],
                    chosen: $("#ps_" + currentTab).val(),
                    disabled: false
                });
            }
        } else if (currentTab == 'jj') {
            //  if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.基金
            if (finprodType2 && finprodType2 == FINPROD_TYPE_TG_F06) {
                $("#ps_" + currentTab).updateChosenArr({
                    item: [PS_T01, PS_T02],
                    chosen: $("#ps_" + currentTab).val(),
                    disabled: false
                });
            }
            // if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.股票
            else if (finprodType2 && finprodType2 == FINPROD_TYPE_TG_F10) {
                // modify by etliu 20211013 start
                // TISHKS-5 bug修复：在场内下指令时，资产选了万科之后，出现申购选项，但理论上不应该出现(万科很早就上市了)，我在申报信息维护中也没有看到万科的新股申报
                // $("#ps_" + currentTab).updateChosenArr({
                //     item: [PS_T01, PS_T02, PS_T04],
                //     chosen: $("#ps_" + currentTab).val(),
                //     disabled: false
                // });
                var tempParam = {
                    finprodId: $("#finprodId_" + currentTab).val()
                };
                Joyin.ajax(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/isXinGuShenGou', tempParam,
                    function (res) {
                        if (res == YES_OR_NO_Y) {
                            $("#ps_" + currentTab).updateChosenArr({
                                item: [PS_T04],
                                chosen: PS_T04,
                                disabled: true
                            });
                        } else {
                            $("#ps_" + currentTab).updateChosenArr({
                                item: [PS_T01, PS_T02],
                                chosen: $("#ps_" + currentTab).val(),
                                disabled: false
                            });
                        }
                    });
                // modify by etliu 20211013 end
            }
            // if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.债券
            else if (finprodType2 && finprodType2 == FINPROD_TYPE_TG_F01) {
                setExtPs();
            }
                // 画面.金融产品类型（投管）为空或者为质押式回购
            //add by chenjiaxing 更改金融产品为空时的枚举值 start
            else if (!finprodType2) {
                $("#ps_" + currentTab).updateChosenArr({
                    item: [PS_03, PS_04, PS_T01, PS_T02, , PS_T03, PS_T04, PS_T46, PS_T49],
                    chosen: $("#ps_" + currentTab).val(),
                    disabled: false
                });
            } else {
                $("#ps_" + currentTab).updateChosenArr({
                    item: [PS_03, PS_04],
                    chosen: $("#ps_" + currentTab).val(),
                    disabled: false
                });
            }
            //add by chenjiaxing 更改金融产品为空时的枚举值 end
        } else {
            $("#ps_" + currentTab).trigger('change');
        }
    };
    /**
     * 联动组合分配
     */
    var linkPortfolio = function () {
        var ps = $("#ps_" + currentTab).val();
        if (ps == PS_T04 || ps == PS_T46 || ps == PS_T97) {
            $("#portfolioId_div" + currentTab).show();
            $("#acctNo_div" + currentTab).show();
            $("#dateTableDiv").hide();
        } else {
            $("#portfolioId_div" + currentTab).hide();
            $("#acctNo_div" + currentTab).hide();
            $("#dateTableDiv").show();
        }

        if (currentTab == 'gd') {
            $("#acctNo_div_" + currentTab).hide()
        }

        if(ps == PS_T97){
            $("#businessModelDiv_"+ currentTab).hide();
        }

    };

    var formatMoney = function (s, n) {
        //区分正负数，且整数位%3==0的负数  显示异常
        var flag = "";
        n = n > 0 && n <= 20 ? n : 2;
        var s = parseFloat((s + '').replace(/[^\d\.-]/g, '')).toFixed(n) + '';
        if (s.indexOf("-") >= 0) {
            flag = "-";
            s = s.substring(1, s.length);
        }
        var l = s.split('.') [0].split('').reverse(),
            r = s.split('.') [1];
        var t = '';
        for (var i = 0; i < l.length; i++) {
            t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? ',' : '');
        }
        return (flag + t.split('').reverse().join('') + '.' + r).trim();

    };


    var setExtPs = function () {
        declarePriceFlag = true;
        //债券默认买入、卖出
        var item = [PS_T01, PS_T02];
        var finprodId = $("#finprodId_" + currentTab).val();
        var param = {
            finprodId: finprodId
        };
        Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectExtPs', param,
            function (res) {
                if (null != res) {
                    declarePriceObj = res;
                    for (var key in res) {//如果是申购、直接设置申购
                        if (key == PS_T04) {
                            $("#ps_" + currentTab).updateChosenArr({
                                item: [PS_T04],
                                chosen: PS_T04,
                                disabled: true
                            });
                            return;
                        } else {
                            item.push(key);
                        }
                    }
                    $("#ps_" + currentTab).updateChosenArr({
                        item: item, /*[PS_T01, PS_T02, PS_T03, PS_T49, PS_T04, PS_T68, PS_T69],*/
                        chosen: $("#ps_" + currentTab).val(),
                        disabled: false
                    });
                }
            });
    }

    /**
     * 删除千分符
     * @param num
     * @returns {string|*}
     */

    function moneyDelete(num) {
        if (num && num != undefined && num != null) {
            let _num = num;
            _num = _num.toString();
            _num = _num.replace(/,/gi, '');
            return _num
        } else {
            return num
        }
    }


    /**
     * 联动展示 持仓数量、可卖数量、成本价
     */
        // var linkFindSecAmountInfo = function() {
        //     var portfolioId = $("#portfolioId_" + currentTab).val(); // 组合名称
        //     var acctNo = $("#acctNo_" + currentTab).val(); // 资金账户
        //     var acctTradeMarket = $("#acctTradeMarket_" + currentTab).val(); // 资金账户
        //     var secAcctId = $("#secId_" + currentTab).val(); // 资产名称
        //     var finprodId = $("#finprodId_" + currentTab).val(); // 资产代码
        //     var ps = $("#ps_" + currentTab).val(); // 委托方向
        //     var vdate = $("#vdate_" + currentTab).val(); // 开始时间
        //
        //     var param = {
        //         "portfolioId":portfolioId,
        //         "vdate":vdate,
        //         "acctNo":acctNo,
        //         "secAcctId":secAcctId,
        //         "finprodId":finprodId,
        //         "ps":ps
        //     };
        //     if (portfolioId != null && portfolioId != '') {
        //         Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/findSecAmountInfo', param, function (result) {
        //             if (result) {
        //                 //add by chenjiaxing 大宗交易指令、固收平台指令创建面板，选择的资产为基金时，“持仓数量、可卖数量、可买数量”单位显示“份” 20210826 start
        //                 var finprodType2 = $("#finprodType2_" + currentTab).val();
        //                 if(finprodType2 == FINPROD_TYPE_TG_F06){
        //                     document.getElementById('positionAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.positionAmount, 2, 10000, 2)+'万份';
        //                     document.getElementById('canSellAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.canSellAmount, 2, 10000, 2)+'万份';
        //                     document.getElementById('costPrice_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.costPrice, 2, null, 2) ;
        //                 }
        //                 else if(finprodType2 == FINPROD_TYPE_TG_F10){
        //                     document.getElementById('positionAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.positionAmount, 2, 1, 2)+'股';
        //                     document.getElementById('canSellAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.canSellAmount, 2, 1, 2)+'股';
        //                     document.getElementById('costPrice_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.costPrice, 2, null, 2) ;
        //                 }
        //                 else{
        //                     //配债配股,质押券出入库不显示持仓数量，可卖数量，成本价
        //                     if(ps == PS_T46 || ps == PS_T97 || ps == PS_T68 || ps ==PS_T69 || ps ==PS_03 || ps ==PS_04) {
        //                         document.getElementById('positionAmount_' + currentTab).innerHTML = "";
        //                         document.getElementById('canSellAmount_' + currentTab).innerHTML = "";
        //                         document.getElementById('costPrice_' + currentTab).innerHTML = "";
        //                     }
        //                     else {
        //                         document.getElementById('positionAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.positionAmount, 2, 10000, 2)+'万元';
        //                         document.getElementById('canSellAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.canSellAmount, 2, 10000, 2)+'万元';
        //                         document.getElementById('costPrice_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.costPrice, 2, null, 2) ;
        //                     }
        //                 }
        //                 //add by chenjiaxing 大宗交易指令、固收平台指令创建面板，选择的资产为基金时，“持仓数量、可卖数量、可买数量”单位显示“份” 20210826 end
        //             }
        //         }, false);
        //     }
        //
        // };

    var linkInvAim = function () {
            var finprodId = $("#finprodId_" + currentTab).val();
            var secAcctId = $("#secAcctId_" + currentTab).val();
            var portfolioId = $("#portfolioId_" + currentTab).val();
            var finprodType2 = $("#finprodType2_" + currentTab).val();

            // 如果金融产品类型债券”,需要投资目的
            if (finprodId && secAcctId && portfolioId && finprodType2 == FINPROD_TYPE_TG_F01) {
                var param = {
                    secAcctId: secAcctId,
                    finprodId: finprodId,
                    portfolioId: portfolioId
                }
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/linkInvAim', param, function (result) {
                    if (result && result.data) {
                        $("#invAim_" + currentTab).textSelect('setValue', result.data);
                        $("#invAim_" + currentTab).prop("disabled", true);
                    } else {
                        $("#invAim_" + currentTab).prop("disabled", false);
                    }
                });
            }
        }

    /**
     * 联动展示可买数量
     */
    // var calCanBuyAmount = function() {
    //     document.getElementById('canBuyAmount_' + currentTab).innerHTML = '0.00万元';
    //     var portfolioId = $("#portfolioId_" + currentTab).val(); // 组合名称
    //     var acctNo = $("#acctNo_" + currentTab).val(); // 资金账户
    //     var secAcctId = $("#secId_" + currentTab).val(); // 资产名称
    //     var finprodId = $("#finprodId_" + currentTab).val(); // 资产代码
    //     var ps = $("#ps_" + currentTab).val(); // 委托方向
    //     var vdate = $("#vdate_" + currentTab).val(); // 开始时间
    //     var price = $("#price_" + currentTab).val(); // 竞价
    //     if (portfolioId != null && portfolioId != '') {
    //         var param = {
    //             portfolioId : portfolioId,
    //             acctNo : acctNo,
    //             acctId : acctNo,
    //             ccy : 'CNY',
    //             secAcctId : secAcctId,
    //             finprodId : finprodId,
    //             ps : ps,
    //             vdate : vdate,
    //             price:price
    //         };
    //         Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/calCanBuyAmount', param, function (result) {
    //             if (result) {
    //                 //add by chenjiaxing 大宗交易指令、固收平台指令创建面板，选择的资产为基金时，“持仓数量、可卖数量、可买数量”单位显示“份” 20210826 start
    //                 var finprodType2 = $("#finprodType2_" + currentTab).val();
    //                 if(finprodType2 == FINPROD_TYPE_TG_F06){
    //                     document.getElementById('canBuyAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.canBuyAmount, 2, 10000, 2)+'万份';
    //                 }
    //                 else if(finprodType2 == FINPROD_TYPE_TG_F10){
    //                     document.getElementById('canBuyAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.canBuyAmount, 2, 1, 2)+'股';
    //                 }
    //                 else{
    //                     if(ps == PS_T46 || ps == PS_T97 || ps == PS_T68 || ps == PS_T69) {
    //                         document.getElementById('canBuyAmount_' + currentTab).innerHTML ='0.00万元'
    //                     }else {
    //                         document.getElementById('canBuyAmount_' + currentTab).innerHTML = JoyinUtil.formatNumber(result.canBuyAmount, 2, 10000, 2)+'万元';
    //                     }
    //
    //                 }
    //                 //add by chenjiaxing 大宗交易指令、固收平台指令创建面板，选择的资产为基金时，“持仓数量、可卖数量、可买数量”单位显示“份” 20210826 end
    //             }
    //         }, false);
    //     }
    // }

    //add by chenjiaixng 20210907 债券净价交易添加偏离度展示 start
    /**
     * 净值偏离度
     */
    function calDeviationDegree() {
        var price = $("#price_" + currentTab).val();
        var finprodId = $("#finprodId_" + currentTab).val();
        var secId = $("#secId_" + currentTab).val();
        if (secId && price) {
            var paramObj = {};
            // TISHKS-137:计算偏离度 fuyongsheng 20211109 edit start
            paramObj.finprodId = finprodId;
            if (finprodId) {
                paramObj.finprodMarketId = secId
            }
            // TISHKS-137:计算偏离度 fuyongsheng 20211109 edit end
            paramObj.unitCprice = price;
            Joyin.ajax(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade003/calcDeviationDegree'), paramObj, function (data) {
                $("#deviationDegree").text(data);
            });
        } else {
            $("#deviationDegree").text("");
        }
    }

    //add by chenjiaixng 20210907 债券净价交易添加偏离度展示 end

    /**
     * 联动可用持仓信息
     */
    var linkAvailableNum = function () {
        if (currentTab == 'jj') {
            var portfolioId = $("#portfolioId_" + currentTab).val();
            var finprodId = $("#finprodId_" + currentTab).val();
            var secAcctId = $("#secAcctId_" + currentTab).val();
//            var invAim = $("#invAim_" + currentTab).val();
            var finprodType2 = $("#finprodType2_" + currentTab).val();
            var tradeMarket = $("#tradeMarket_" + currentTab).val();
            var acctNo = $("#acctNo_" + currentTab).val();
            var ps = $("#ps_" + currentTab).val();
            // 标准券可用数量
            if (ps == PS_03 || ps == PS_T69) {
                if (ps == PS_T69) {
                    $("#availableNum_label_" + currentTab).text("标准券可出库数量");
                } else if (ps == PS_03) {
                    $("#availableNum_label_" + currentTab).text("标准券可用数量");
                }
                finprodId = 'XN_PRODID999';
            } else {
                $("#availableNum_label_" + currentTab).text("可卖数量");
            }
            // 如果金融产品类型不是“债券”，则可以不需要投资目的
            if (portfolioId && secAcctId && finprodId/* && (invAim || finprodType2 != FINPROD_TYPE_TG_F01)*/) {
                var param = {
                    portfolioId: portfolioId,
                    finprodId: finprodId,
                    secAcctId: secAcctId,
                    tradeMarket: tradeMarket,
                    acctNo: acctNo,
                    ps: ps,
                };
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectAvailableNum', param, function (result) {
                    if (parent.EtrExchangeTradeJjHq) {
                        parent.EtrExchangeTradeJjHq.setAccAvailableCash(result, 'num');
                    }
                }, false);
            } else {
                //parent.EtrExchangeTradeJjHq.setCashOrNum('num');
            }
        }
    };
    /**
     * 联动可用现金信息
     */
    var linkAccAvailableCash = function () {
        if (currentTab == 'jj') {
            var portfolioId = $("#portfolioId_" + currentTab).val();
            var acctNo = $("#acctNo_" + currentTab).val();
            if (portfolioId && acctNo) {
                var list = getAcctNoSelectList(null, acctNo);
                if (list && list.length > 0 && list[0].object && list[0].object.length > 0) {
                    var param = {
                        portfolioId: portfolioId,
                        acctId: acctNo,
                        ccy: list[0].object[0].ccy
                    };
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectAccAvailableCash', param, function (result) {
                        if (parent.EtrExchangeTradeJjHq) {
                            parent.EtrExchangeTradeJjHq.setAccAvailableCash(result, 'cash');
                        }
                    }, false);
                } else {
                    if (parent.EtrExchangeTradeJjHq) {
                        //parent.EtrExchangeTradeJjHq.setCashOrNum('cash');
                    }
                }
            } else {
                if (parent.EtrExchangeTradeJjHq) {
                    // parent.EtrExchangeTradeJjHq.setCashOrNum('cash');
                }
            }
        }
    };
    /**
     * 联动可转股数量
     * 画面.可转股数量 = 画面.指令数量 *画面.单位面值 /画面.指令价格
     */
    var linkToShareAvailableNum = function () {
        var ps = $("#ps_" + currentTab).val();
        if (ps == PS_T49) {
            var finprodId = $("#finprodId_" + currentTab).val();
            var entrustNum = $("#entrustNum_" + currentTab).val()
            //债券交易数量单位 add by yeming 20210729 start
            if (bond_traded_unit == '02' && finprodType2 == FINPROD_TYPE_TG_F01) {
                entrustNum = entrustNum * 10;
            }
            //债券交易数量单位 add by yeming 20210729 end
            var entrustPrice = $("#entrustPrice_" + currentTab).val();
            // 如果金融产品类型不是“债券”，则可以不需要投资目的
            if (finprodId && entrustNum && entrustPrice && entrustPrice != 0) {
                var param = {
                    finprodId: finprodId
                };
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectSecFaceValue', param, function (result) {
                    var toShareAvailableNum = Math.floor(entrustNum * result / entrustPrice, 2);
                    $("#toShareAvailableNum_" + currentTab).val(toShareAvailableNum).trigger('blur');
                }, false);
            } else {
                $("#toShareAvailableNum_" + currentTab).val('').trigger('blur');
            }
        }
    };

    /**
     * 质押券出入库
     */
    var linkToConverseRatio = function () {
        scale = 0;
        var ps = $("#ps_" + currentTab).val();
        //设置默认小数位
        $("#entrustPrice_" + currentTab).attr("data-textrange", scale);
        if (ps == PS_T68 || ps == PS_T69) {
            var finprodId = $("#finprodId_" + currentTab).val();
            // 如果金融产品类型不是“债券”，则可以不需要投资目的
            if (finprodId) {
                var param = {finprodId: finprodId};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectStdsecConverseRatio', param, function (result) {
                    if (result) {
                        $("#entrustPrice_" + currentTab).val(result).trigger('blur');
                    } else {
                        $("#entrustPrice_" + currentTab).val('').trigger('blur');
                    }
                }, false);
            } else {
                $("#entrustPrice_" + currentTab).val('').trigger('blur');
            }
        }
    }

    /**
     * 取行权价 废弃方法
     */
    var linkToExercisePrice = function () {
        var ps = $("#ps_" + currentTab).val();
        //设置默认小数位
        $("#entrustPrice_" + currentTab).attr("data-textrange", scale);
        if (ps == PS_T03) {
            var finprodId = $("#finprodId_" + currentTab).val();
            var vdate = $("#vdate_" + currentTab).val();
            // 如果金融产品类型不是“债券”，则可以不需要投资目的
            if (finprodId) {
                var param = {finprodId: finprodId, vdate: vdate};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectExercisePriceById', param, function (result) {
                    if (result) {
                        $("#entrustPrice_" + currentTab).val(result).trigger('blur');
                    } else {
                        $("#entrustPrice_" + currentTab).val('').trigger('blur');
                    }
                }, false);
            } else {
                $("#entrustPrice_" + currentTab).val('').trigger('blur');
            }
        }
    }

    /**
     * 债转股 废弃方法
     */
    var linkToConverseSharePrice = function () {
        var ps = $("#ps_" + currentTab).val();
        //设置默认小数位
        $("#entrustPrice_" + currentTab).attr("data-textrange", scale);
        if (ps == PS_T49) {//债转股
            var finprodId = $("#finprodId_" + currentTab).val();
            // 如果金融产品类型不是“债券”，则可以不需要投资目的
            if (finprodId) {
                var param = {finprodId: finprodId};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectConverseSharePrice', param, function (result) {
                    if (result) {
                        $("#entrustPrice_" + currentTab).val(result).trigger('blur');
                    } else {
                        $("#entrustPrice_" + currentTab).val('').trigger('blur');
                    }
                }, false);
            } else {
                $("#entrustPrice_" + currentTab).val('').trigger('blur');
            }
        }
    }

    /**
     * 配债、配股价格
     */
    var linkToTrdOthPrice = function (secId) {
        var ps = $("#ps_" + currentTab).val();
        var othCodeType;
        if (ps == PS_T97) {
            othCodeType = FINPROD_OTH_CODE_TYPE_05;
        } else if (ps == PS_T46) {
            othCodeType = FINPROD_OTH_CODE_TYPE_06;
        } else if (ps == PS_T04) {
            othCodeType = FINPROD_OTH_CODE_TYPE_07;
        }
        if(ps == PS_T68 || ps == PS_T69){
            scale = 0;
        }
        //设置默认小数位
        $("#entrustPrice_" + currentTab).attr("data-textrange", scale);
        if (ps == PS_T46 || ps == PS_T97 || ps == PS_T04) {
            if (secId) {
                var param = {
                    finprodMarketId: secId,
                    //othCodeType: (ps == PS_T97) ? FINPROD_OTH_CODE_TYPE_05: FINPROD_OTH_CODE_TYPE_06
                    othCodeType: othCodeType
                };
                Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectTrdOthPrice', param,
                    function (result) {
                        if (result) {
                            $("#entrustPrice_" + currentTab).val(result);
                            $("#entrustPrice_" + currentTab).attr('disabled', 'disabled');
                        } else {
                            $("#entrustPrice_" + currentTab).val('').trigger('blur');
                        }
                    },
                    false);
            } else {
                $("#entrustPrice_" + currentTab).val('').trigger('blur');
            }
        }
    }
    /**
     * 联动目标类型
     */
    var linkTargetType = function () {
        setRiskFlag(1);
        var ps = $("#ps_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        //配债 T97 配股 T46 T03
        if (ps == PS_T49 || ps == PS_T68 || ps == PS_T69 || ps == PS_03 || ps == PS_04 || ps == PS_T97 || ps == PS_T46 || ps == PS_T03) {
            $("#targetType_" + currentTab).prop("disabled", true);
            $("#targetType_" + currentTab).val(TARGET_CLASS_01).trigger('change');
        } else if ((finprodType2 == FINPROD_TYPE_TG_F10 && ps == PS_T04) || (finprodType2 == FINPROD_TYPE_TG_F01 && ps == PS_T04 && type9 == WINDTYPEONE_B14)) {
            //新股申购
            $("#targetType_" + currentTab).prop("disabled", true);
            $("#targetType_" + currentTab).val(TARGET_CLASS_01).trigger('change');
        } else {
            $("#targetType_" + currentTab).prop("disabled", false);
        }
    };
    /**
     * 1、根据金融产品类型和委托方向控制委托价格是否展示
     * 2、设置价格字段的小数位数。
     */
    var controlEntrustPrice = function () {
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        var $star = $('<i class="must">*</i>');
        if (finprodType2) {
            // if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.债券 && 画面.委托方向 != 数据字典.交易方向.行权-回售 or 申购 or 配债 or 债转股 or 质押入库 or 质押出库
            if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                // 净价、到期收益率、全价显示，单选按钮，计算方式 = 数据字典.计算方式.
                $("#price_div_" + currentTab).show();
                $("#fprice_div_" + currentTab).show();
                $("#yield_div_" + currentTab).show();
                $("#entrustPrice_div_" + currentTab).hide();

                $("#entrustPrice_label_" + currentTab).text("指令价格").append($star.clone());
                $("#entrustNum_label_" + currentTab).text("指令数量").append($star.clone());
                $("#entrustAmt_label_" + currentTab).text("指令金额").append($star.clone());
                $("#entrustPriceUnit_" + currentTab).text("元");
                $("#entrustAmtUnit_" + currentTab).text("元");
            } else {
                $("#price_div_" + currentTab).hide();
                $("#fprice_div_" + currentTab).hide();
                $("#yield_div_" + currentTab).hide();
                $("#entrustPrice_div_" + currentTab).show();
                //  画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.债券 and （画面.委托方向=数据字典.交易方向.质押入库、质押出库）
                if (finprodType2 == FINPROD_TYPE_TG_F01 && (ps == PS_T68 || ps == PS_T69)) {
                    // 指令价格展示为折算率标签、指令金额展示为标准券数量
                    $("#entrustPrice_label_" + currentTab).text("折算率").append($star.clone());
                    $("#entrustNum_label_" + currentTab).text("指令数量").append($star.clone());
                    $("#entrustAmt_label_" + currentTab).text("标准券数量").append($star.clone());
                    $("#entrustPriceUnit_" + currentTab).text("%");
                    $("#entrustAmtUnit_" + currentTab).text("万元");
                    $("#entrustNumUnit_" + currentTab).text("万元");

                    // var entrustAmt = $("#standardBondVolume_" + currentTab).val();
                    // $("#entrustAmt_" + currentTab).val(Math.floor(new Number(entrustAmt)));
                }
                // if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.质押式回购
                else if (finprodType2 == FINPROD_TYPE_TG_ZYSHG) {
                    // 指令价格显示，标签联动为回购利率、指令数量标签联动为标准券数量、指令金额标签联动为回购金额
                    $("#entrustPrice_label_" + currentTab).text("回购利率").append($star.clone());
                    $("#entrustNum_label_" + currentTab).text("标准券数量").append($star.clone());
                    $("#entrustAmt_label_" + currentTab).text("回购金额").append($star.clone());
                    $("#entrustPriceUnit_" + currentTab).text("%");
                    $("#entrustAmtUnit_" + currentTab).text("元");

                    // var entrustAmt = $("#standardBondVolume_" + currentTab).val();
                    // $("#entrustAmt_" + currentTab).val(Math.floor(new Number(entrustAmt)));
                } else {
                    $("#targetType_label_" + currentTab).text("目标类型").append($star.clone());
                    $("#priceModel_label_" + currentTab).text("价格模式").append($star.clone());
                    $("#toShareAvailableNum_label_" + currentTab).text("可转股数量").append($star.clone());
                    $("#entrustDate_label_" + currentTab).text("指令日期").append($star.clone());
                    $("#ps_label_" + currentTab).text("委托方向").append($star.clone());
                    $("#secId_label_" + currentTab).text("资产名称").append($star.clone());
                    $("#acctNo_label_" + currentTab).text("资金账户").append($star.clone());
                    $("#portfolioId_label_" + currentTab).text("组合名称").append($star.clone());
                    $("#entrustPrice_label_" + currentTab).text("指令价格").append($star.clone());
                    $("#entrustNum_label_" + currentTab).text("指令数量").append($star.clone());
                    $("#entrustAmt_label_" + currentTab).text("指令金额").append($star.clone());
                    $("#entrustPriceUnit_" + currentTab).text("元");
                    $("#entrustAmtUnit_" + currentTab).text("元");
                }
            }
        }
    };
    /**
     * 联动价格模式，控制价格模式的下拉选项。
     */
    var linkDeclaType = function () {
        setRiskFlag(1);
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var dataSource = $("#dataSource_" + currentTab).val();
        if (dataSource && dataSource == '01') {
            // 画面.价格模式  = 数据字典.价格模式.点击成交申报
            $("#declaType_" + currentTab).updateChosenArr({
                item: [DECLA_TYPE_PRM08],
                chosen: DECLA_TYPE_PRM08,
                disabled: true,
                isNotTrigger: true
            });
        } else if (currentTab == 'dz') {
            var tradeMarket = $("#tradeMarket_" + currentTab).val();
            // 画面.交易场所 = 数据字典.交易场所.上海证券交易场所
            if (tradeMarket == TRADE_PLACE_03) {
                // 画面.价格模式  = 数据字典.意向申报，数据字典.成交申报，收盘价固定价格申报，不指定
                $("#declaType_" + currentTab).updateChosenArr({
                    item: [DECLA_TYPE_PRM06, DECLA_TYPE_PRM09, DECLA_TYPE_PRM10, DECLA_TYPE_PRM05],
                    chosen: $("#declaType_" + currentTab).val(),
                    disabled: false,
                    isNotTrigger: true
                });
            }
            // 画面.金融产品类型 IN（债券、股票、基金）
            else if (/*finprodType2 == FINPROD_TYPE_TG_F01 || */finprodType2 == FINPROD_TYPE_TG_F06
                || finprodType2 == FINPROD_TYPE_TG_F10) {
                // 画面.价格模式  = 数据字典.收盘价固定价格申报，数据字典.日均价固定价格申报，不指定
                $("#declaType_" + currentTab).updateChosenArr({
                    item: [DECLA_TYPE_PRM10, DECLA_TYPE_PRM11, DECLA_TYPE_PRM05],
                    chosen: $("#declaType_" + currentTab).val(),
                    disabled: false,
                    isNotTrigger: true
                });
            }
            //债券申报类型放开
            else if (finprodType2 == FINPROD_TYPE_TG_F01) {
                $("#declaType_" + currentTab).updateChosenArr({
                    item: [DECLA_TYPE_PRM06, DECLA_TYPE_PRM08, DECLA_TYPE_PRM09, DECLA_TYPE_PRM07, DECLA_TYPE_PRM10, DECLA_TYPE_PRM11, DECLA_TYPE_PRM05],
                    chosen: $("#declaType_" + currentTab).val(),
                    disabled: false,
                    isNotTrigger: true
                });
            } else if (finprodType2 == FINPROD_TYPE_TG_ZYSHG) {
                // 画面.价格模式  = 数据字典.意向申报，数据字典.成交申报，定价申报、数据字典.收盘价固定价格申报，数据字典.日均价固定价格申报、不指定
                $("#declaType_" + currentTab).updateChosenArr({
                    item: [DECLA_TYPE_PRM06, DECLA_TYPE_PRM09, DECLA_TYPE_PRM07, DECLA_TYPE_PRM10, DECLA_TYPE_PRM11, DECLA_TYPE_PRM05],
                    chosen: $("#declaType_" + currentTab).val(),
                    disabled: false,
                    isNotTrigger: true
                });
            }
        } else if (currentTab == 'jj') {
            var ps = $("#ps_" + currentTab).val();
            var board = $("#board_" + currentTab).val();
            // if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.债券
            if (finprodType2 == FINPROD_TYPE_TG_F01) {
                // 回售 or 申购 or 配债 or 债转股 or 质押入库 or 质押出库
                if (ps == PS_T03 || ps == PS_T04 || ps == PS_T97 || ps == PS_T49 || ps == PS_T68 || ps == PS_T69) {
                    // 画面.价格模式  = 数据字典.价格模式.指定价格 不可编辑
                    $("#priceModel_" + currentTab).updateChosenArr({
                        item: [PRICE_MODEL_PRM01],
                        chosen: PRICE_MODEL_PRM01,
                        disabled: true,
                        isNotTrigger: true
                    });
                } else {
                    // 画面.价格模式  = 数据字典.价格模式.限价指令，数据字典.价格模式.市价指令
                    $("#priceModel_" + currentTab).updateChosenArr({
                        item: [PRICE_MODEL_PRM02, PRICE_MODEL_PRM03],
                        chosen: $("#priceModel_" + currentTab).val(),
                        disabled: false,
                        isNotTrigger: true
                    });
                }
            }
            // else if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.股票 and （画面.委托方向=数据字典.交易方向.配股 or 数据字典.交易方向.申购）
            else if (finprodType2 == FINPROD_TYPE_TG_F10 && (ps == PS_T46 || ps == PS_T04)) {
                // 画面.价格模式  = 数据字典.价格模式.指定价格 不可编辑
                $("#priceModel_" + currentTab).updateChosenArr({
                    item: [PRICE_MODEL_PRM01],
                    chosen: PRICE_MODEL_PRM01,
                    disabled: true,
                    isNotTrigger: true
                });
                //add by chenjiaxing 更改资产为空时的价格模式枚举值 start
            } else if (!finprodType2) {
                // 画面.价格模式  = 数据字典.价格模式.限价指令，数据字典.价格模式.市价指令
                $("#priceModel_" + currentTab).updateChosenArr({
                    item: [PRICE_MODEL_PRM01, PRICE_MODEL_PRM02, PRICE_MODEL_PRM03],
                    chosen: $("#priceModel_" + currentTab).val(),
                    disabled: false,
                    isNotTrigger: true
                });
                //add by chenjiaxing 更改资产为空时的价格模式枚举值 end
            }
                // else if 画面.金融产品类型（投管） = 数据字典.金融产品类型（投管）.质押式回购
            /*            else if(finprodType2 == FINPROD_TYPE_TG_ZYSHG || finprodType2 == FINPROD_TYPE_TG_F02 || finprodType2 == FINPROD_TYPE_TG_F03) {
                            // 画面.价格模式  = 数据字典.价格模式.指定价格、限价指令
                            $("#priceModel_" + currentTab).updateChosenArr({
                                item: [PRICE_MODEL_PRM01, PRICE_MODEL_PRM02],
                                chosen: $("#priceModel_" + currentTab).val(),
                                disabled: false
                            });
                        }*/
            else if(finprodType2 == FINPROD_TYPE_TG_F10){
                if(board != BOARD_BD03 && board != BOARD_BD04){
                    $("#priceModel_" + currentTab).updateChosenArr({
                        item: [PRICE_MODEL_PRM02, PRICE_MODEL_PRM03],
                        chosen: $("#priceModel_" + currentTab).val(),
                        disabled: false,
                        isNotTrigger: true
                    });
                }else {
                    $("#priceModel_" + currentTab).updateChosenArr({
                        item: [PRICE_MODEL_PRM10, PRICE_MODEL_PRM02, PRICE_MODEL_PRM03],
                        chosen: $("#priceModel_" + currentTab).val(),
                        disabled: false,
                        isNotTrigger: true
                    });
                }
            }else {
                // 画面.价格模式  = 数据字典.价格模式.限价指令，数据字典.价格模式.市价指令
                $("#priceModel_" + currentTab).updateChosenArr({
                    item: [PRICE_MODEL_PRM02, PRICE_MODEL_PRM03],
                    chosen: $("#priceModel_" + currentTab).val(),
                    disabled: false,
                    isNotTrigger: true
                });
            }
        } else if (currentTab == 'gd') {
            // 画面.价格模式  = 数据字典.价格模式.不指定、定价申报、最优价成交申报、可转换成交申报、指定对手方成交申报
            $("#declaType_" + currentTab).updateChosenArr({
                item: [DECLA_TYPE_PRM14],
                chosen: DECLA_TYPE_PRM14,
                disabled: true,
                isNotTrigger: true
            });
        } else {
            $("#priceModel_" + currentTab).trigger('change', true);
            $("#declaType_" + currentTab).trigger('change', true);
        }
    };
    /**
     * 联动委托数量单位。
     * 交易场所和金融产品类型改变时调用
     */
    var linkEntrustNumUnit = function () {
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        // if 画面.金融产品类型（投管） = 股票
        if (finprodType2 == FINPROD_TYPE_TG_F10) {
            $("#entrustNumUnit_" + currentTab).text("股");
        }
        // if 画面.金融产品类型（债券） = 基金
        else if (finprodType2 == FINPROD_TYPE_TG_F06) {
            $("#entrustNumUnit_" + currentTab).text("份");
        }
        // if 画面.金融产品类型（债券） = 债券
        else if (finprodType2 == FINPROD_TYPE_TG_F01) {
            if (currentTab == 'dz' && tradeMarket == TRADE_PLACE_03) {
//                $("#entrustNumUnit_" + currentTab).text("手");
                //债券交易数量单位 edit by yeming 20210729 start
                if (bond_traded_unit == '02') {
                    $("#entrustNumUnit_" + currentTab).text("手");
                } else {
                    $("#entrustNumUnit_" + currentTab).text("张");
                }
                //债券交易数量单位 edit by yeming 20210729 end
            } else {
                //债券交易数量单位 edit by yeming 20210729 start
                if (bond_traded_unit == '02') {
                    $("#entrustNumUnit_" + currentTab).text("手");
                } else {
                    $("#entrustNumUnit_" + currentTab).text("张");
                }
                //债券交易数量单位 edit by yeming 20210729 end
                if(ps == PS_T68 || ps == PS_T69) {
                    $("#entrustAmtUnit_" + currentTab).text("万元");
                    $("#entrustNumUnit_" + currentTab).text("万元");
                }
            }
        }
        // if 画面.金融产品类型（债券） = 质押式回购
        else if (finprodType2 == FINPROD_TYPE_TG_ZYSHG || finprodType2 == FINPROD_TYPE_TG_F02 || finprodType2 == FINPROD_TYPE_TG_F03) {
           /* if (currentTab == 'dz' && tradeMarket == TRADE_PLACE_03) {
                $("#entrustNumUnit_" + currentTab).text("张");
            } else {
                $("#entrustNumUnit_" + currentTab).text("张");
            }*/
            $("#entrustNumUnit_" + currentTab).text("张");
        }
        if (currentTab == 'dz') {
            if (finprodType2 == FINPROD_TYPE_TG_ZYSHG) {//大宗质押式回购 指令价格单位 % 特殊处理
                $("#entrustPriceUnit").text("%");
            } else {
                $("#entrustPriceUnit").text("元");
            }
        }
    };

    /**
     * 联动指令价格、指令数量、指令金额
     */
    var calcEntrustNumAndAmt = function (init) {
        if (init || setPageItemsFlag) {
            return;
        }
        setRiskFlag(1);
        var secId = $("#secId_" + currentTab).val();
        var finprodId = $("#finprodId_" + currentTab).val();
        var targetType = $("#targetType_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        var priceModel = $("#priceModel_" + currentTab).val();
        var entrustNum = $("#entrustNum_" + currentTab).val();
        //债券交易数量单位 add by yeming 20210729 start
        if (bond_traded_unit == '02' && finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
            entrustNum = entrustNum * 10;
        }
        //债券交易数量单位 add by yeming 20210729 end
        var entrustAmt = $("#entrustAmt_" + currentTab).val();
        var entrustPrice = $("#entrustPrice_" + currentTab).val();
        var fprice = $("#fprice_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        var board = $("#board_" + currentTab).val();
        //var calculateField = $("[name=calculateField_" + currentTab + "]:radio").next(".layui-form-radioed").prev("input").val();
        // 如果是债券，则需要根据计算方式取单位净价或全价作为价格
        if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
            entrustPrice = $("#price_" + currentTab).val();
        }
        var param = {
            secId: secId,
            finprodId: finprodId,
            targetType: targetType,
            finprodType2: finprodType2,
            entrustNum: entrustNum,
            entrustAmt: entrustAmt,
            tradeMarket: tradeMarket,
            ps: ps,
            board: board,
            currentTab: currentTab,
            resetFlag: resetFlag
        };
        //竞价平台股票、债券、基金，卖出可能会存在零股，增加可用持仓参数；
        if (currentTab == 'jj' && ps == PS_T02 && (finprodType2 == FINPROD_TYPE_TG_F10 || finprodType2 == FINPROD_TYPE_TG_F01 || finprodType2 == FINPROD_TYPE_TG_F06)) {
            // var availableNum = parent.EtrExchangeTradeJjHq.getJJMktPrice("#availableNum_jj").replace(/,/g, "");
            // if(availableNum && availableNum > 0){
            //     $.extend(param,{availableNum: availableNum});
            // }
        }
        if (priceModel == PRICE_MODEL_PRM03 && isStandardPs(ps)) {
            if (targetType) {
                queryJJHQPrice(param, ajaxCalcEntrustNumAndAmt);
            }
        } else {
            if (targetType) {
                if (declarePriceFlag) {
                    $.extend(param, {entrustPrice: formatPrice(entrustPrice)});
                    $.extend(param, {fprice: formatPrice(fprice)});
                } else {
                    $.extend(param, {entrustPrice: entrustPrice});
                    $.extend(param, {fprice: formatPrice(fprice)});
                }
                ajaxCalcEntrustNumAndAmt(param);
            }
        }
        // 计算完数量需要重新计算可转股数量
        if (ps == PS_T49) {
            linkToShareAvailableNum();
        } else if (ps == PS_T68 || ps == PS_T69) {
            linkToConverseRatio();
        } else if (ps == PS_T46 || ps == PS_T97 || ps == PS_T04) {
            var secId = $("#secId_" + currentTab).val();
            linkToTrdOthPrice(secId);
        }
        declarePriceHandle();
    };

    /**
     * 申报维护的申报价格应该作为场内指令的指令价格
     */
    var declarePriceHandle = function () {
        var ps = $("#ps_" + currentTab).val();
        //委托价格==申报价格
        if (declarePriceObj) {
            for (var key in declarePriceObj) {
                if (key === ps) {
                    declarePriceFlag = false
                    $("#entrustPrice_" + currentTab).val(declarePriceObj[key]);
                    break;
                }
            }
        }
    }
    //根据参数、计算指令数量和金额
    var ajaxCalcEntrustNumAndAmt = function (param) {
        if (!param.targetType) {
            return;
        }
        if (!param.entrustAmt && !param.entrustNum) {
            return;
        }
        Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/calcEntrustNumAndAmt', param, function (result) {
            if (param.targetType == TARGET_CLASS_01 && param.availableNum && param.availableNum > 0) {
                var stockNum = param.availableNum - result.entrustNum;
                if (result.minNum) {
                    if (stockNum > 0 && stockNum < result.minNum) {
                        availableNum = param.availableNum;
                        if (!initFlag) {
                            window.parent.checkSalesOfSmallShares(availableNum);
                        }
                    }
                }
            }

            var finprodType2 = $("#finprodType2_" + currentTab).val();
            var ps = $("#ps_" + currentTab).val();
            //债券交易数量单位 add by yeming 20210729 start
            if (bond_traded_unit == '02' && finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                result.entrustNum = JoyinMath.doDiv(result.entrustNum, 10, 0, JoyinMath.ROUND_DOWN);//转成手配置
            }
            //债券交易数量单位 add by yeming 20210729 end
            // modify by etliu 20210901 start
            if (result.entrustAmt) {
                var entrustAmt = result.entrustAmt;
                if ((finprodType2 == FINPROD_TYPE_TG_F01 || finprodType2 == FINPROD_TYPE_TG_ZYSHG) && (ps == PS_T68 || ps == PS_T69)) {
                    // entrustAmt = Math.floor(new Number(entrustAmt));
                    $("#entrustAmt_" + currentTab).val(entrustAmt);
                    $("#entrustAmt_" + currentTab).attr("data-textrange",4);
                    $("#entrustNum_" + currentTab).attr("data-textrange",2);
                } else {
                    $("#entrustAmt_" + currentTab).val(entrustAmt).trigger('blur');
                }
            } else if ($("#priceModel_" + currentTab).val() == PRICE_MODEL_PRM03) {
                $("#entrustAmt_" + currentTab).val(0).trigger('blur');
            }
            // modify by etliu 20210901 end
            $("#entrustNum_" + currentTab).val(result.entrustNum).trigger('blur');
        }, false);
    }

    /**
     * 零股卖出全部
     */
    var setTotalSharesNum = function (entrustNum) {
        $("#entrustNum_" + currentTab).val(entrustNum).change();
    }
    /**
     * 计算净价、全价、到期收益率。
     *
     */
    var calcPriceAndYield = function (init) {
        if (init || setPageItemsFlag) {
            return;
        }
        setRiskFlag(1);
        var finprodId = $("#finprodId_" + currentTab).val();
        // 默认使用百元券面计算
        var prinAmt = new BigNumber(100);
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        // 以下条件不满足时直接返回
        if (finprodType2 != FINPROD_TYPE_TG_F01 || !finprodId || !prinAmt) {
            return;
        }
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        var calculateField = $("[name=calculateField_" + currentTab + "]:radio").next(".layui-form-radioed").prev("input").val();
        var priceModel = $("#priceModel_" + currentTab).val();
        var param = {
            finprodId: finprodId,
            prinAmt: prinAmt,
            tradeMarket: tradeMarket,
            ps: ps,
            calculateField: calculateField
        };
        // 选择的计算方式为“净价”时
        if (calculateField == SEC_CALC_MODE_01) {
            var price = $("#price_" + currentTab).val();
            // var fprice = $("#fprice_" + currentTab).val();
            // var yield = $("#yield_" + currentTab).val();
            if (priceModel == PRICE_MODEL_PRM03) {
                var price = $("#factPrice_" + currentTab).val();
            }
            // 首先格式化价格
            formatEntrustPrice();
            if (!resetFlag && price) {
                param.price = price;
                // param.fprice = fprice;
                // param.yield = yield;
                Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/calcPriceAndYield', param, function (result) {
                    var fprice = formatPrice(price, result.data.fprice);
                    $("#fprice_" + currentTab).val(fprice).trigger('blur');
                    $("#yield_" + currentTab).val(result.data.yield).trigger('blur');
                }, false);
                calcEntrustNumAndAmt();
                linkTableEntrustNumAmt();
            } else {
                $("#fprice_" + currentTab).val('').trigger('blur');
                $("#yield_" + currentTab).val('').trigger('blur');
            }
            // 计算完净价需要计算数量或金额
            // calcEntrustNumAndAmt();
        }
        // 选择的计算方式为“全价”时
        else if (calculateField == SEC_CALC_MODE_02) {
            var fprice = $("#fprice_" + currentTab).val();
            fprice = formatPrice(fprice);
            if (!resetFlag && fprice) {
                param.fprice = fprice;
                Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/calcPriceAndYield', param, function (result) {
                    var cprice = formatPrice(fprice, result.data.cprice);
                    $("#price_" + currentTab).val(cprice).trigger('blur');
                    $("#yield_" + currentTab).val(result.data.yield).trigger('blur');
                    // 设置净价后格式化价格
                    calcEntrustNumAndAmt();
                }, false);
            } else {
                $("#price_" + currentTab).val('').trigger('blur');
                $("#yield_" + currentTab).val('').trigger('blur');
                // 计算完净价需要计算数量或金额
                calcEntrustNumAndAmt();
            }
        }
        // 选择的计算方式为“到期收益率”时
        else if (calculateField == SEC_CALC_MODE_03) {
            var yield = $("#yield_" + currentTab).val();
            if (!resetFlag && yield) {
                param.yield = yield;
                Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/calcPriceAndYield', param, function (result) {
                    var cprice = formatPrice(result.data.cprice);
                    var fprice = formatPrice(result.data.cprice, result.data.fprice);
                    $("#price_" + currentTab).val(cprice).trigger('blur');
                    $("#fprice_" + currentTab).val(fprice).trigger('blur');
                    // 计算完净价需要计算数量或金额
                    calcEntrustNumAndAmt();
                }, false);
            } else {
                $("#price_" + currentTab).val('').trigger('blur');
                $("#fprice_" + currentTab).val('').trigger('blur');
                // 计算完净价需要计算数量或金额
                calcEntrustNumAndAmt();
            }
        }
    }

    /**
     * 根据金融产品类型和交易场所联动价格小数位数（包含净价和委托价格）
     *          债券      股票      回购      基金
     * 上交所   0.01      0.01     0.005      0.001
     * 深交所   0.001     0.01     0.001      0.001
     */
    var formatEntrustPrice = function (calcEntrustNumAndAmt) {
        setRiskFlag(1);
        var entrustPrice = null;
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        var priceModel = $("#priceModel_" + currentTab).val();
        if (ps == PS_T46 || ps == PS_T97 || ps == PS_T03 ||/* ps == PS_T04 ||*/ ps == PS_T49 || ps == PS_T68 || ps == PS_T69
            /*||((ps == PS_03 || ps == PS_04) && currentTab == 'dz')*/) {
            if (BOND_SCALE) {
                entrustPrice = $("#entrustPrice_" + currentTab).val();
                $("#entrustPrice_" + currentTab).val(new Number(entrustPrice).toFixed(BOND_SCALE));
            }
            return;
        }
        if (priceModel == PRICE_MODEL_PRM03) {
            entrustPrice = $("#factPrice_" + currentTab).val();
        } else {
            // 根据金融产品类型和交易方向取得不同的价格字段
            if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                entrustPrice = $("#price_" + currentTab).val();
            } else {
                entrustPrice = $("#entrustPrice_" + currentTab).val();
            }
        }
        // 指令价格非市价
        if (entrustPrice) {
            if (priceModel != PRICE_MODEL_PRM03) {
                var price = formatPrice(entrustPrice);
                // 根据金融产品类型和交易方向取得不同的价格字段
                if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
                    entrustPrice = $("#price_" + currentTab).val(price);
                } else {
                    entrustPrice = $("#entrustPrice_" + currentTab).val(price);
                }
            }
            if (calcEntrustNumAndAmt) {
                calcEntrustNumAndAmt();
            }
        }
    };


    /**
     * 判断交易方向是不是标准的交易方向。
     * 当金融产品类型为债券时，需要根据不同的交易方向确定是否为标准券交易。
     * 画面.委托方向 != 数据字典.交易方向.行权-回售 or 申购 or 配债 or 债转股 or 质押入库 or 质押出库
     */
    var isStandardPs = function (ps) {
        return ps != PS_T03 && ps != PS_T04 && ps != PS_T97 && ps != PS_T49 && ps != PS_T46 && ps != PS_T68 && ps != PS_T69;
    }

    /**
     * 002画面双击刷新001
     * @param data
     */
    var dbclickRefresh002 = function (data) {
        //设置
        setEntrust001PageItems001(data);
        //清空指令编号,设置指令记录操作类型
        $("#entrustId_" + currentTab).val('').trigger('change');
        optType = ENTRUST_RECORD_NEW;

        controlModifyView(false);

        //双击校验后，将风险试算标识重置
        window.parent.dbclickRefreshRisk();
    }

    /**
     * 003画面双击刷新001
     * @param data
     */
    var dbclickRefresh = function (data) {
        //设置
        setEntrust001PageItems(data);
        /*$.each(data, function(id, value) {
            setData(id, value, '');
        });*/
        if (data.entrustId) {
            //设置指令记录操作类型
            optType = ENTRUST_RECORD_MODIFY;
            //修改页面时组合，资产名称，交易方向不可改
            controlModifyView(true);
        }
        //双击校验后，将风险试算标识重置
        window.parent.dbclickRefreshRisk();
    }

    var controlModifyView = function (disabled) {
        $("#portfolioId_" + currentTab).attr("disabled", disabled);
        $("#secId_" + currentTab).attr("disabled", disabled);
        $("#ps_" + currentTab).attr("disabled", disabled);
        $("#priceModel_" + currentTab).attr("disabled", disabled);
    }

    /**
     * 双击赋值
     * @param id
     * @param value
     * @param pageId
     */
    var setData = function (id, value, pageId) {
        id = id + "_" + currentTab;
        if ($('#' + id).size() > 0) {
            element = $('#' + id);
        } else {
            element = $('input[name=' + id + ']');
        }
        //下拉框特殊处理
        if (element.hasClass('input-select')) {
            if (id == 'portfolioId_jj') {
                element.textSelect('setValue', value);
            } else {
                element.val(value).trigger('change');
            }
        } else if (element.hasClass('input-dateSelect')) {
            //日期框特殊处理
            if (value) {
                element.val(new Date(value).format('yyyy-MM-dd'));
            }
        } else if (element.hasClass('input-checkbox')) {
            //单选按钮特殊处理
            element.val(value).trigger('change');
        } else if ($('#' + id).attr('lay-skin') === 'switch') {
            //switch 特殊处理
            $('#' + id).prop('checked', false);
            $('#' + id).next().removeClass('layui-form-onswitch').trigger('change');
            $('#' + id).next().find("em").text('否');
            // 是否,有无
            if (value === 'Y' || value === '01') {
                $('#' + id).prop('checked', true);
                $('#' + id).next().addClass('layui-form-onswitch').trigger('change');
                $('#' + id).next().find("em").text('是');
            }
        } else {
            element.val(value);
        }
    }

    /**
     * 风险试算时不需要进行风险试算的校验
     */
    var isNeedRiskcal = function () {
        //大宗交易平台的意向申报不需要走风险
        // if(currentTab == 'dz' && $("#declaType_dz").val() && $("#declaType_dz").val() == DECLA_TYPE_PRM06) {
        //     return false;
        // }
        //质押券入库和出库不需要走风险试算
        ps = $("#ps_" + currentTab).val();
        if (ps == PS_T68 || ps == PS_T69) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param
     * @returns
     */
    function hideLeftTopPanel() {
        if (parentPageModel == PAGE_MODEL_NEW) {
            if (window.parent.showLeftTopPanel) {
                window.parent.showLeftTopPanel();
            }
        } else {
            if (window.parent.hideLeftTopPanel) {
                window.parent.hideLeftTopPanel();
            }
        }
    }

    // modify by etliu 20220601:【3.0.6场内 ZGCPCN-1087：【协议现券指令】指令新建时，清算速度为T+1，提交到审批人待办中，清算速度变成了T+0 start
    //根据是否非担保 设置清算速度
    // var setSettleSpeed = function (isNetSett) {
    var setSettleSpeed = function (isNoGuard) {
        // if (isNetSett == YES_OR_NO_Y) {
        //     $("#settleSpeed_" + currentTab).val(ACCOUNT_SPEED_02).trigger('change');
        // } else {
        //     $("#settleSpeed_" + currentTab).val(ACCOUNT_SPEED_01).trigger('change');
        // }
        if (isNoGuard == YES_OR_NO_Y) {
            $("#settleSpeed_" + currentTab).val(ACCOUNT_SPEED_01).trigger('change');
        } else {
            $("#settleSpeed_" + currentTab).val(ACCOUNT_SPEED_02).trigger('change');
        }
        $("#settleSpeed_" + currentTab).attr("disabled", true);
    }
    // modify by etliu 20220601:【3.0.6场内 ZGCPCN-1087：【协议现券指令】指令新建时，清算速度为T+1，提交到审批人待办中，清算速度变成了T+0 end


    /**
     * 校验只要是数字（包含正负整数，0以及正负浮点数）就返回true
     **/

    function isNumber(val) {
        var regPos = /^\d+(\.\d+)?$/; //非负浮点数
        var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
        if (regPos.test(val) || regNeg.test(val)) {
            return true;
        } else {
            return false;
        }
    }

    /***行情相关方法 add by chaowei begin*/

    /**根据市场代码，查询行情价格，展示出来*/
    var queryJJHQPrice = function (param, callback) {
        $.ajax({
            /**临时用这个地址，后面调整！！！！**/
            //url: "http://192.168.70.3:8081/tams/joyin/quote/tick?stock_code="+param.secId,
            url: basePath + "joyin/quote/tick?stock_code=" + param.secId,
            async: false,
            type: "GET",
            dataType: "json",
            success: function (info) {
                var result = info.data;
                if (result != null && result != '') {
                    //债券买卖、质押券正逆回购 指令头寸、风控按照最新价格、回购利率控制；
                    if (param.finprodType2 == FINPROD_TYPE_TG_F01 || param.finprodType2 == FINPROD_TYPE_TG_ZYSHG || param.finprodType2 == FINPROD_TYPE_TG_F02 || param.finprodType2 == FINPROD_TYPE_TG_F03) {
                        //场内交易指令，新建 。市价指令时 ，先取最新价，再去收盘价，收盘价都没有才不能下指令
                        var lastPrice = formatPrice(result.lastPrice == 0 || result.lastPrice == "" ? result.preClosePrice : result.lastPrice);
                        $("#factPrice_" + currentTab).val(lastPrice);
                        $("#price_" + currentTab).val(lastPrice);
                        $("#deviationDegree").text("");
                        $.extend(param, {entrustPrice: lastPrice});
                        $.extend(param, {fprice: $("#fprice_" + currentTab).val()});
                    } else if (param.finprodType2 == FINPROD_TYPE_TG_F06 || param.finprodType2 == FINPROD_TYPE_TG_F10) {
                        if (param.ps == PS_T01) {//市价指令股票、基金买入 价格按涨停价计算风控、头寸
                            var upperLimitPrice = formatPrice(result.upperLimitPrice);
                            $("#factPrice_" + currentTab).val(upperLimitPrice).trigger('change');
                            $.extend(param, {entrustPrice: upperLimitPrice});
                        } else if (param.ps == PS_T02) {//市价指令股票基金卖出 价格按跌停价计算风控、头寸
                            var lowerLimitPrice = formatPrice(result.lowerLimitPrice);
                            $("#factPrice_" + currentTab).val(lowerLimitPrice).trigger('change');
                            $.extend(param, {entrustPrice: lowerLimitPrice});
                        }
                    }
                    if (callback) {
                        return callback(param);
                    }
                } else {
                    if (callback) {
                        return callback(param);
                    }
                }
            }
        });
    }

    /***
     * 双击大宗行情，联动带出指令录入。
     */
    var dblDzHqTable = function (data) {
        //设置指令记录操作类型
        $("#entrustId_" + currentTab).val('').trigger('change');
        optType = ENTRUST_RECORD_NEW;
        var secId = $("#secId_" + currentTab).val();
        //返回指令DTO
        var zlobj = new Object();
        if (secId) {
            //var object = getSecIdSelectObjectData(secId);
            var object = $("#secIdObjRelative_" + currentTab).val();
            if (object) {
                object = JSON.parse(object);
                //交易场所
                var tradeMarket = object.tradeMarket;
                //行情类型
                var quotationType = data.quotationType;
                //交易场所 = 深圳证券交易所 and 行情类型 = 定价申报
                if (tradeMarket == '04' && quotationType == 'PRM07') {
                    //金融产品类型 （投管）
                    zlobj.finprodType2 = object.finprodType2;
                    //金融产品代码
                    zlobj.finprodId = object.finprodId;
                    //证券代码
                    zlobj.secId = secId;
                    //行情方向
                    if (data.entrustPs == "T01") {//买入
                        zlobj.ps = "T02";
                    } else if (data.entrustPs == "T02") {//卖出
                        zlobj.ps = "T01";
                    }
                    if (data.entrustPs == "03") {// 正回购
                        zlobj.ps = "04";
                    } else if (data.entrustPs == "04") {// 逆回购
                        zlobj.ps = "03";
                    }
                    //指令价格
                    zlobj.entrustPrice = formatPrice(data.entrustPrice);
                    if (data.entrustPs == "03" || data.entrustPs == "04") {// 正回购 或 逆回购
                        // 指令数量
                        zlobj.entrustNum = data.entrustNum;
                        // 指令金额
                        zlobj.entrustAmt = data.entrustNum * 100;
                    } else {
                        // 指令数量
                        zlobj.entrustNum = data.entrustNum;
                    }
                    //价格模式  限价价格
                    zlobj.priceModel = 'PRM02';
                    //申报类型
                    zlobj.declaType = 'PRM08';
                    //交易场所
                    zlobj.tradeMarket = object.tradeMarket;
                    //交易平台
                    zlobj.tradePlat = '04';
                    //金融产品类型（投管） =债券
                    // if(object.finprodType2='F01') {
                    //     zlobj.invAim = getInvAim();
                    // }
                    //对方席位
                    zlobj.seatId = data.oppositeSeat;
                    //约定号
                    zlobj.agreeOnId = data.agreeNum;

                    //数据来源 = 01：市场行情
                    zlobj.dataSource = '01';

                    //指令数量 min（选择记录.数量，可卖数量）
                    var enNum = queryHqAvailableNum();
                    if (enNum) {
                        zlobj.entrustNum = data.entrustNum < enNum ? data.entrustNum : enNum;
                    }
                }

            }
        }
        return zlobj;
    }

    /***
     * 双击大宗盘后行情，联动带出指令录入。
     */
    var dblPhHqTable = function (data) {
        //设置指令记录操作类型
        $("#entrustId_" + currentTab).val('').trigger('change');
        optType = ENTRUST_RECORD_NEW;
        var secId = $("#secId_" + currentTab).val();
        //返回指令DTO
        var zlobj = new Object();
        if (secId) {
            //var object = getSecIdSelectObjectData(secId);
            var object = $("#secIdObjRelative_" + currentTab).val();
            if (object) {
                object = JSON.parse(object);
                //交易场所
                var tradeMarket = object.tradeMarket;
                //行情类型
                var quotationType = data.quotationType;
                //金融产品类型 （投管）
                zlobj.finprodType2 = object.finprodType2;
                //金融产品代码
                zlobj.finprodId = object.finprodId;
                //证券代码
                zlobj.secId = secId;
                if (data.closingPrice) {//收盘价不为空
                    //指令价格
                    zlobj.entrustPrice = data.closingPrice;
                    //
                    zlobj.priceModel = 'PRM02';
                    //申报类型
                    zlobj.declaType = 'PRM10';

                } else if (data.dailyAvgPrice) {//日均价固定价格申报
                    //指令价格
                    zlobj.entrustPrice = data.dailyAvgPrice;
                    //价格模式
                    zlobj.priceModel = 'PRM02';
                    //价格模式
                    zlobj.declaType = 'PRM11';
                }

                //交易场所
                zlobj.tradeMarket = object.tradeMarket;
                //交易平台
                zlobj.tradePlat = '04';
                // //金融产品类型（投管） =债券
                // if(object.finprodType2='F01') {
                //     zlobj.invAim = getInvAim();
                // }
            }
        }
        return zlobj;
    }


    /***
     * 双击固收行情，联动带出指令录入。
     */
    var dblGsHqTable = function (data) {
        //设置指令记录操作类型
        $("#entrustId_" + currentTab).val('').trigger('change');
        optType = ENTRUST_RECORD_NEW;
        //返回指令DTO
        var zlobj = new Object();
        if (data) {
            //证券代码
            zlobj.secId = data.secId;
            //净价
            zlobj.price = data.nPrice;
            //全价
            zlobj.fprice = data.fPrice;
            //收益率
            zlobj.yield = data.yield;
            //行情方向
            if (data.entrustPs == "T01") {//买入
                zlobj.ps = "T02";
            } else if (data.entrustPs == "T02") {//卖出
                zlobj.ps = "T01";
            }

            //指令数量 min（选择记录.数量，可卖数量）
            var enNum = queryHqAvailableNum();
            if (enNum) {
                zlobj.entrustNum = data.entrustNum < enNum ? data.entrustNum : enNum;
            } else {
                zlobj.entrustNum = data.entrustNum;
            }

            //价格模式--点击成交申报
            zlobj.declaType = 'PRM08';

            //交易平台--固定收益平台
            zlobj.tradePlat = '03';

            //交易场所 上海证券交易所
            zlobj.tradeMarket = '03';

            //数据来源 = 01：市场行情
            zlobj.dataSource = '01';

        }
        return zlobj;
    }

    /**
     * 查询投资目的
     */
    var getInvAim = function () {
        var secId = $("#secId_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var invAim;
        // 如果金融产品类型债券”,需要投资目的
        if (secId && finprodType2 == FINPROD_TYPE_TG_F01) {
            Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/getInvAim', null, function (result) {
                invAim = result;
            });
        }
        return invAim;
    };

    /**
     * 联动指令价格、指令数量、指令金额 callback1 格式化指令价格 callback2 计算数量与金额 callback3 设置价格小数位 callback4 获取行情
     */
    var standardizedPrice = function (param, callback3, callback4) {
        if (callback3) {
            if (callback4) {
                callback3(param, callback4);
            } else {
                callback3(result);
            }
        }
    };

    /**
     * 查询可用持仓
     */
    var queryHqAvailableNum = function () {
        var portfolioId = $("#portfolioId_" + currentTab).val();
        var finprodId = $("#finprodId_" + currentTab).val();
        var secAcctId = $("#secAcctId_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();

        var availableNum;

        // 如果金融产品类型不是“债券”，则可以不需要投资目的
        if (portfolioId && secAcctId && finprodId) {
            var param = {
                portfolioId: portfolioId,
                finprodId: finprodId,
                secAcctId: secAcctId
            }
            Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectAvailableNum', param, function (result) {
                availableNum = result;
            });
        }
        return availableNum;
    };

    /***
     * 定时刷新行情
     */
    var dsRefreshHq = function () {
        setInterval(function () {
            if (currentTab == 'jj') {
                /**定时刷新竞价行情*/
                var secId = $("#secId_" + currentTab).val();
                var finprodName = '';
                var finprodType = '';
                if (secId) {
                    var object = $("#secIdObjRelative_" + currentTab).val();
                    if (object) {
                        object = JSON.parse(object);
                        finprodType = object.finprodType2;
                        if (object.othCodeType && object.othCodeType == FINPROD_OTH_CODE_TYPE_06) {
                            secId = object.marketId;
                        }
                        if (!useInterfQuoteFlag) {
                            console.log(etrExchange_HqArea_refresh_period + 's定时刷新竞价行情数据');
                            //parent.EtrExchangeTradeJjHq.queryJJHQXX(secId, finprodName, finprodType);
                        }
                    }
                }
                //刷新可用现金
                linkAccAvailableCash();
                //刷新可卖数量
                linkAvailableNum();
            }
            if (currentTab == 'dz') {
                /**定时大宗行情*/
                if ($("#secId_" + currentTab).val()) {
                    // parent.EtrExchangeTradeJjHq.loadDzHqTable();
                }
            }

            if (currentTab == 'gd') {
                /**定时固定收益行情*/
                if ($("#secId_" + currentTab).val()) {
                    parent.EtrExchangeTradeJjHq.loadGdHqTable();
                }
            }
        }, etrExchange_HqArea_refresh_period);

    }

    /**
     * 获取风险试算标签提示信息
     * @returns
     */
    function getRiskMathPrompt() {
        var ps = $("#ps_" + currentTab).val();
        var targetType = $("#targetType_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var calculateField = $("[name=calculateField_" + currentTab + "]:radio").next(".layui-form-radioed").prev("input").val();
        if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
            if (calculateField == SEC_CALC_MODE_01) {
                if (!$("#price_" + currentTab).val()) {
                    return 'EtrExchangeTrade001.price';
                }
            }
            if (calculateField == SEC_CALC_MODE_02) {
                if (!$("#fprice_" + currentTab).val()) {
                    return 'EtrExchangeTrade001.fprice';
                }
            }
            if (calculateField == SEC_CALC_MODE_03) {
                if (!$("#yield_" + currentTab).val()) {
                    return 'EtrExchangeTrade001.yield';
                }
            }
        } else if (currentTab == 'jj' && finprodType2 == FINPROD_TYPE_TG_ZYSHG) {
            if (!$("#entrustPrice_" + currentTab).val()) {
                return 'EtrRepo001.price';
            }
            if (!$("#entrustNum_" + currentTab).val()) {
                return 'EtrExchangeTrade002.secNum';
            }
        } else {
            if (!$("#entrustPrice_" + currentTab).val()) {
                return 'EtrExchangeTrade003.price';
            }
        }
        if (targetType == TARGET_CLASS_01 && !$("#entrustNum_" + currentTab).val()) {
            return 'EtrExchangeTrade001.entrustNum';
        }
        if (!$("#entrustAmt_" + currentTab).val()) {
            return 'EtrExchangeTrade001.entrustAmt';
        }
        return 'EtrExchangeTrade001.entrustAmt';
    }

    /**
     *联动带出，指令价格
     **/
    function setQuotaPrice(quotaPrice) {
        var ps = $("#ps_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        if (finprodType2 == FINPROD_TYPE_TG_F01 && isStandardPs(ps)) {
            $("#price_" + currentTab).val(quotaPrice).trigger('blur').trigger('change');
        } else {
            $("#entrustPrice_" + currentTab).val(quotaPrice).trigger('blur').trigger('change');
        }
    }

    /**
     * 设置当前tab页
     * @return
     */
    var setCurrentTab = function (tab) {
        currentTab = tab;
        if (currentTab == 'jj') {
            $("#content_div_jj").show();
            $("#content_div_dz").hide();
            $("#content_div_gd").hide();
        } else if (currentTab == 'dz') {
            $("#content_div_jj").hide();
            $("#content_div_dz").show();
            $("#content_div_gd").hide();
        } else if (currentTab == 'gd') {
            $("#content_div_jj").hide();
            $("#content_div_dz").hide();
            $("#content_div_gd").show();
        }

    }


    /***行情相关方法 add by chaowei end*/

    /**
     * 画面加载完成
     */


        //初始化时，是否隐藏左上角内容
    var initPage = function () {
            $("#businessModelDiv_jj").hide();
            $("#businessModelDiv_gd").hide();
            $("#businessModelDiv_dz").hide();
            var finprodType2 = $("#finprodType2_" + currentTab).val();
            var ps = $("#ps_" + currentTab).val();
            if(ps){
                $("#ps_" + currentTab).attr('disabled', 'disabled');
            }
            var secId = $("#secId_" + currentTab).val();
            if(secId){
                $("#secId_" + currentTab).attr('disabled', 'disabled');
            }
            //组合隐藏
            //分配组合展示
            if (ps == PS_T04 || ps == PS_T46 || ps == PS_T97) {
                $("#portfolioId_div_" + currentTab).show();
                $("#acctNo_div_" + currentTab).show();
                $("#dateTableDiv").hide();
                $("#ps_" + currentTab).attr('disabled', 'disabled');
                $("#businessModelDiv_"+ currentTab).hide();
            }
            if(ps == PS_T49){
                $("#entrustAmtUnit_div_" + currentTab).hide();
                $("#invAim_div_" + currentTab).hide();
                $("#targetType_div_" + currentTab).hide();

            }
            if(ps == PS_T03){
                $("#invAim_div_" + currentTab).hide();
                $("#targetType_div_" + currentTab).hide();
            }

            if (finprodType2 == FINPROD_TYPE_TG_F06) {
                $("#priceModel_div_" + currentTab).show();
                $("#entrustNumUnit_" + currentTab).text("份");
            }
            if(ps == PS_T68 || ps == PS_T69){
                $("#riskTrialRet_div").hide();
                $("#EtrExchangeTradeCommon004_btn_riskcal").hide();
                $("#targetType_div_" + currentTab).hide();
            }
            if (finprodType2 == FINPROD_TYPE_TG_F01) {
                $("#entrustNumUnit_" + currentTab).text("张");
                $("#acctNo_div_" + currentTab).hide();
                $("#portfolioId_div_" + currentTab).hide();
                $("#yield_" + currentTab).attr("data-textrange", 4);
                // $("#trader_div_xj").show();
                // $("#counterId_div_xj").show();
                // $("#counterTrader_div_xj").show();
                // $("#agreeOnId_div_xj").show();
                // $("#declaType_div_xj").show();
                // $("#settleSpeed_div_xj").show();
            }
            if(ps == PS_T04){
                $("#portfolioId_div_" + currentTab).show();
                $("#targetType_div_" + currentTab).hide();
            }

            if (finprodType2 == FINPROD_TYPE_TG_F01 && (ps == PS_T68 || ps == PS_T69)) {
                $("#entrustAmtUnit_" + currentTab).text("万元");
                $("#entrustNumUnit_" + currentTab).text("万元");
                $("#entrustPrice_" + currentTab).attr("data-textrange", 0);
                var entrustNum = $("#entrustNum_" + currentTab).val();
                if(entrustNum){
                    $("#entrustNum_" + currentTab).val(entrustNum/100);
                }
                var entrustPrice =  $("#entrustPrice_" + currentTab).val();
                if(entrustNum && entrustPrice){
                    entrustPrice = parseInt(entrustPrice);
                    $("#entrustPrice_" + currentTab).val(entrustPrice);
                    var amt = entrustNum*entrustPrice/10000;
                    $("#entrustAmt_" + currentTab).attr("data-textrange", 4);
                    $("#entrustAmt_" + currentTab).val(amt).blur().trigger('change');
                }
            }


            initFlag = true;
            hideLeftTopPanel();
            if (parentPageModel == PAGE_MODEL_REFERENCE || parentPageModel == PAGE_MODEL_EDIT) {
                // 隐藏重置按钮

                linkTradePlat(entrustType, true);
                portfolioIdChange(true);
                acctNoChange(true);
                secIdChange();
                tradeMarketChange();
                // secId();
                secAcctIdChange();
                targetTypeChange(true);
                // entrustNumChange();
                psChange(true);
                priceModelChange(true, true);
                formatEntrustPrice();
                //页面初始化结束后  是否弹出 <<剩余可用数量存在零股，是否卖出零股？>>
                if (availableNum != "") {
                    window.parent.checkSalesOfSmallShares(availableNum);
                }
            }
            setTimeout(function () {
                initFlag = false;
            }, 3000);
            setCurrentTab(currentTab);
            var riskLists = entity_RiskTrialRetList || [];
            if (riskLists.length > 0) {
                if (riskLists[0].conclusion) {
                    $("#riskTrialSpan").html("&nbsp;&nbsp;" + riskLists[0].conclusion);
                } else {
                    $("#riskTrialSpan").html("&nbsp;&nbsp;" + riskTrialSpan);
                }
                // setRiskFlag(0);
            }
            linkEntrustNumUnit();
            if (ps == PS_T04 && finprodType2 == FINPROD_TYPE_TG_F10) {
                $("#entrustPrice_" + currentTab).attr('disabled', 'disabled');
            }

            if(ps == PS_T01 || ps == PS_T02){
                $("#priceModel_div_" + currentTab).show();
                $("#acctNo_div_" + currentTab).hide();
            }

            if(ps == PS_T97){
                $("#invAim_div_" + currentTab).hide();
                $("#businessModelDiv_"+ currentTab).hide();
            }
            var priceModel = $("#priceModel_" + currentTab).val();
            if(priceModel == PRICE_MODEL_PRM10){
                $("#entrustPrice_" + currentTab).attr('disabled', 'disabled');
            }
        }
    var addMinPriceStep = function (type, num, input) {
        var $secIdObj = $("#secIdObjRelative_" + currentTab).val();
        if ($secIdObj) {
            var $obj = JSON.parse($secIdObj);
            $.extend($obj, {price: num});
            var precision = getMinPrice($obj);
            if (precision) {
                input.val(getNumResult(type, num, Number(precision.minPrice))).blur().trigger('change');
                return;
            }
        }
        input.val(getNumResult(type, num, Number(0.01))).blur().trigger('change');
    }

    var getNumResult = function (type, num1, num2) {
        if (type === 'add') {
            return Number(num1) + Number(num2)
        } else {
            return Number(num1) - Number(num2)
        }
    }

    var addMinNumStep = function (type, num, input) {
        var finprodId = $("#finprodId_" + currentTab).val();
        var targetType = $("#targetType_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        if (targetType) {
            var step = 1;
            var param = {
                finprodId: finprodId,
                targetType: targetType,
                finprodType2: finprodType2,
                tradeMarket: tradeMarket,
                ps: ps,
                currentTab: currentTab
            };
            Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/getMinNumStep', param, function (result) {
                if (result.minNum) {
                    input.val(getNumResult(type, num, Number(result.minNum))).blur().trigger('change');
                }
            }, false);
            return
        }
        input.val(getNumResult(type, num, Number(1))).blur().trigger('change');
    }

    var getScale = function () {
        var $secIdObj = $("#secIdObjRelative_" + currentTab).val();
        if ($secIdObj) {
            var $obj = JSON.parse($secIdObj);
            return getMinPrice($obj).scale;
        }
        return scale;
    }
    /**
     * 对外公开的接口
     */
    parent.EtrExchangeTrade001 = {
        dbclickRefresh: dbclickRefresh,
        dbclickRefresh002: dbclickRefresh002,
        getSearchParams: getSearchParams,
        getriskdata: getriskdata,
        riskResultdata: riskResultdata,
        cluflag: cluflag,
        setEntrust001PageItems: setEntrust001PageItems,
        doReset: doReset,
        getRiskInterfUrl: getRiskInterfUrl,
        getCurrentTab: getCurrentTab,
        getDzSearchParams: getDzSearchParams,
        dblPhHqTable: dblPhHqTable,
        dblDzHqTable: dblDzHqTable,
        setCurrentTab: setCurrentTab,
        currentTab: currentTab,
        isNeedRiskcal: isNeedRiskcal,
        setQuotaPrice: setQuotaPrice,
        formatMoney: formatMoney,
        moneyDelete: moneyDelete,
        setTotalSharesNum: setTotalSharesNum,
        getScale: getScale,
        formatPrice: formatPrice
    }

    /**
     * 查看资产详情事件
     */
    var searchDetail = function () {
        var finprodId = $('#finprodId_' + currentTab).val();
        var finprodType2 = $('#finprodType2_' + currentTab).val();
        if (finprodId != "" && FINPROD_TYPE_TG_ZYSHG != finprodType2) {
            // var url = "invest/asset/bond/FinBondTargetAsset002/init"; // 默认债券标的"zq2022041801.SH"
            //新 债券信息页面
            var url = "home.html?a=a&finprodMarketId=" + finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a"; // 默认债券标的
            if (FINPROD_TYPE_TG_F06 == finprodType2) {
                var flagStr = finprodId.substr(finprodId.length - 2, finprodId.length);
                // 场内基金标的
                url = "invest/asset/exfund/FinExFundTarget002/init";
                if (flagStr == "OF") {
                    // 公募基金标的
                    url = "invest/asset/fund/FinFundTarget002/init";
                }
            } else if (FINPROD_TYPE_TG_F10 == finprodType2) {
                // 股票标的
                url = "invest/asset/stock/FinStockInfo002/init";
            }
            var param = {
                pageModel: PAGE_MODEL_REFERENCE,
                isQuickInput: true,
                finprodId: finprodId
            };
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + url,
                "98%", "98%",
                param);
        } else if (FINPROD_TYPE_TG_ZYSHG == finprodType2) {
            Joyin.error("质押式回购不支持查看资产详情")
        } else {
            Joyin.error("资产名称不能为空")
        }
    };

    function getBussCombDistribtTableParams() {
        var ps = $("#ps_" + currentTab).val();
        var ebcd = entity_BussCombDistribtList || [];
        if(ebcd.length > 0) {
            var isPs68OrPs69 = (ps == PS_T68 || ps == PS_T69);
            if (isPs68OrPs69) {
            for (var i = 0; i < ebcd.length; i++) {
                    var ebcdKey = ebcd[i];
                    ebcdKey.entrustNum = ebcdKey.entrustNum / 100;
                    ebcdKey.entrustAmt = ebcdKey.entrustAmt / 100;
                }
            }
        }
        console.log(ebcd);
        return ebcd;
    }

    var dateTable = $("#dateTable").joyintable({
        data: getBussCombDistribtTableParams(),
        searchOnInit: true,
        loadonce: true,
        page: false,
        cols: [
            {
                field: 'portfolioId',
                title: 'EtrExchangeTrade002.portfolioName',
                sortField: 'portfolioId',
                width: 200,
                edit: true,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'text',formatoption: true,
                    url: JoyinUtil.getRealPath("/business/common/CommonSelect/selectPortfolioId"),
                    postData: function () {
                        return {};
                    }
                }
            },
            {
                field: 'acctNo', title: 'EtrExchangeTrade001.acctNo', width: 200, sortable: false, edit: true,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'text',
                    url: JoyinUtil.getRealPath("/etrexchange/common/EtrExchangeTradeCommon004/getPtlSecAcctInfo"),
                    postData: function (data) {
                        return {
                            portfolioId: data.portfolioId, tradeMarket: $("#tradeMarket_"+currentTab).val()
                        };
                    }
                }
            },
            {
                field: 'entrustNum',
                title: 'EtrExchangeTrade001.entrustNum',
                sortField: 'entrustNum',
                width: 200,
                type: 'number',
                digit:  ps != "T68" && ps != "T69" ? 0 : 2,
                edit: true
            },
            {
                field: 'entrustAmt',
                title: currentTab == 'gd'?'EtrExchangeTrade001.netPriceAmt':'EtrExchangeTrade001.entrustAmt',
                sortField: 'entrustAmt',
                width: 200,
                type: 'number',
                edit: true,
                visible: (ps != "T03" && ps != "T49" && ps != "T68" && ps != "T69")
            },
            {
                field: 'accAvailableCash',
                title: '可用现金(元)',
                width: 200,
                type: 'number',
                edit: false,
                visible: (ps != "T03" && ps != "T49" && ps != "T68" && ps != "T69")
            },
            {
                field: 'accAvailablePos',
                title: '可用持仓',
                width: 200,
                type: 'number',
                edit: false,
                visible: (ps != "T68" && ps != "T69")
            },
            {
                field: 'accAvailablePos',
                title: '可入库持仓',
                width: 200,
                type: 'number',
                edit: false,
                visible: (ps == "T68" || ps == "T69")
            },
            {
                field: 'standardBondVolume',
                title: '可出库持仓',
                width: 200,
                type: 'number',
                edit: false,
                visible: (ps == "T68" || ps == "T69")
            },
            {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['delt']}
        ],
        beforeEditCell: function (preVal, field, rowData) {
            var ps = $("#ps_" + currentTab).val();
            var targetType = $("#targetType_" + currentTab).val();
            if (field == "acctNo") {
                curPortfolioId = rowData.portfolioId;
                return true;
            } else if (field == "portfolioId") {
                return true;
            }/* else if (field == "entrustNum") {
                return true;
            }*/
            if (targetType && targetType == TARGET_CLASS_02) {
                if (field == "entrustAmt"){
                    return true;
                }
                if (field == "entrustNum") {
                    return false;
                }
            }else{
                if (field == "entrustAmt"){
                    return false;
                }
                if (field == "entrustNum") {
                    return true;
                }
            }

        },
        editCell: function (value, data, field, cell, column) {
            setRiskFlag(1)
            if (field == "portfolioId") {
                var param = {};
                var finprodType2 = $("#finprodType2_" + currentTab).val();
                if (finprodType2 != FINPROD_TYPE_TG_F06 && finprodType2 != FINPROD_TYPE_TG_F10 && finprodType2 !=FINPROD_TYPE_TG_ZYSHG){
                    var param1 = {};
                    param1.portfolioId = data.portfolioId;
                    param1.finprodId = $("#finprodId_" + currentTab).val();
                    param1.cdate = iPlatformDate;
                    var ps = $("#ps_" + currentTab).val();
                    if (ps == PS_T68 || ps == PS_T69 || ps == PS_T02 || ps == PS_T49 || ps == PS_T03) {
                        if(ps == PS_T02){ //卖出
                            if(dateTable.getAllRows().length >=2){
                                Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/selectAllBusModelList', param1, function (result) {
                                    if(result.data.length == 2){
                                        Joyin.error("组合持有该资产存在多个业务模式，不允许委托方向"+ps);
                                        dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        return;
                                    }else if(result.data.length == 1){
                                        $("#businessModel_" + currentTab).textSelect('setValue', result.data[0].BUSINESSMODEL);
                                    }else{
                                        Joyin.error("组合无选择业务模式的资产持仓");
                                        dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        return;
                                    }
                                })
                            }else{
                                Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/selectAllBusModelList', param1, function (result) {
                                    if(result.data.length == 2){
                                        Joyin.error("组合持有该资产存在多个业务模式,不允许卖出");
                                        dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        return;
                                    }else{
                                        if(result.data.length == 1){
                                            $("#businessModel_" + currentTab).textSelect('setValue', result.data[0].BUSINESSMODEL);
                                        }else{
                                            Joyin.error("组合无该资产持仓数据");
                                            dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                            return;
                                        }
                                    }
                                });
                            }
                        }else{
                            if(dateTable.getAllRows().length >=2){
                                Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/selectAllBusModelList', param1, function (result) {
                                    if(result.data.length == 2){
                                        Joyin.error("组合持有该资产存在多个业务模式，不允许委托方向"+ps);
                                        dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        return;
                                    }else if(result.data.length == 1){
                                        $("#businessModel_" + currentTab).textSelect('setValue', result.data[0].BUSINESSMODEL);
                                    }else{
                                        Joyin.error("组合无选择业务模式的资产持仓");
                                        dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        return;
                                    }
                                })
                            }else {
                                Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/selectAllBusModelList', param1, function (result) {
                                    if(result.data.length == 2){
                                        Joyin.error("组合持有该资产存在多个业务模式，不允许委托方向"+ps);
                                        dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        return;
                                    }
                                })
                            }
                        }
                    } else {
                        if(ps == PS_T01){
                            var businessModel = $("#businessModel_" + currentTab).val();
                            if(dateTable.getAllRows().length >=2){
                                Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/selectAllBusModelList', param1, function (result) {
                                    if(result.data.length != 0){
                                        if(result.data[0].BUSINESSMODEL != businessModel){
                                            Joyin.error("业务模式与组合最新持仓业务模式不一致");
                                            dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                        }
                                    }
                                })
                            }else{
                                if(businessModel == ''){
                                    Joyin.error("请选择业务模式");
                                    dateTable.deleteRow(data.LAY_TABLE_INDEX);
                                }else{
                                    Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTrade001/selectAllBusModelList', param1, function (result) {
                                        if(result.data.length != 0){
                                            var secId = $("#secId_" + currentTab).val();
                                            var param2 = {};
                                            param2.secId = secId;
                                            Joyin.ajaxSync(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/IsBondAsset', param2, function(result1) {
                                                if(result.data[0].BUSINESSMODEL == 'AC' && result1.data=='3'){
                                                    $("#businessModel_" + currentTab).val("FVTPL").trigger('change');
                                                    $("#businessModel_" + currentTab).prop("disabled", true);
                                                }else{
                                                    $("#businessModel_" + currentTab).textSelect('setValue', result.data[0].BUSINESSMODEL);
                                                    $("#businessModel_" + currentTab).prop("disabled", true);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
                param.portfolioId = data.portfolioId;
                param.tradeMarket = $("#tradeMarket_" + currentTab).val();
                Joyin.ajaxJson(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/getPtlSecAcctInfoJson', param, function (result) {
                    if (result.length == 1) { //只有一条，自动选上
                        var secAcctId = null;
                        if(result[0].object.length == 1){
                            secAcctId = result[0].object[0].secAcctId;
                        }
                        var acctNo = result[0].id;
                        dateTable.renderRow(data.LAY_TABLE_INDEX, {"acctNo": acctNo, "secAcctId":secAcctId});
                        if(currentTab == 'dz') {
                            var param = {
                                portfolioId: value,
                                finprodId: $("#finprodId_" + currentTab).val(),
                                finprodType2: $("#finprodType2_" + currentTab).val(),
                                tradeMarket: $("#tradeMarket_" + currentTab).val(),
                                acctId: acctNo,
                                tradePlat: "04",
                            };
                            Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAccAvailableCash', param, function (result) {
                                if (result.data){
                                    dateTable.renderRow(data.LAY_TABLE_INDEX,  {"accAvailableCash": result.data});
                                }
                            }, false);
                            param.acctId = null;
                            param.secAcctId = secAcctId;
                            Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAvailableNum', param, function (result) {
                                if (result.data) {
                                    dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailablePos": result.data});
                                }
                            }, false);
                        }
                        if(currentTab == 'gd') {
                            var param = {
                                portfolioId: value,
                                finprodId: $("#finprodId_" + currentTab).val(),
                                finprodType2: $("#finprodType2_" + currentTab).val(),
                                tradeMarket: $("#tradeMarket_" + currentTab).val(),
                                acctId: acctNo,
                                tradePlat: "03",
                            };
                            Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAccAvailableCash', param, function (result) {
                                if (result.data){
                                    dateTable.renderRow(data.LAY_TABLE_INDEX,  {"accAvailableCash": result.data});
                                }
                            }, false);
                            param.acctId = null;
                            param.secAcctId = secAcctId;
                            Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAvailableNum', param, function (result) {
                                if (result.data) {
                                    dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailablePos": result.data});
                                }
                            }, false);
                            setRiskFlag(1);
                        }
                        if(currentTab == 'jj') {
                            Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAccAvailableCash',
                                {
                                    portfolioId: data.portfolioId,
                                    acctId: result[0].id,
                                    cyy: result[0].object[0].cyy,
                                    tradeMarket: $("#tradeMarket_" + currentTab).val(),
                                    ps: ps,
                                }, function (result) {
                                if (result.data) {
                                    dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailableCash": result.data});
                                }
                            }, false);
                            Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAvailableNum',
                                {
                                    finprodId: $("#finprodId_" + currentTab).val(),
                                    finprodType2: $("#finprodType2_" + currentTab).val(),
                                    portfolioId: data.portfolioId,
                                    ps: ps,
                                    tradeMarket: $("#tradeMarket_" + currentTab).val(),
                                    secAcctId: result[0].object[0].secAcctId,
                                    tradePlat: "02",
                                }, function (result) {
                                if (result.data) {
                                    dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailablePos": result.data});
                                }
                            }, false);
                            linkTableEntrustNumAmt();
                        }
                    } else {
                        dateTable.renderRow(data.LAY_TABLE_INDEX, {"acctNo": null});
                    }
                });
            } else if (field == "acctNo") {
                var secAcctId = "";
                if (data.acctNo && $("#tradeMarket_" + currentTab).val()) {
                    var param = {};
                    param.portfolioId = data.portfolioId;
                    param.acctNo = data.acctNo;
                    param.tradeMarket = $("#tradeMarket_" + currentTab).val();
                    Joyin.ajaxJson(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/getPtlSecAcctInfoJson', param, function (result) {
                        if (result) {
                            secAcctId = result[0].object[0].secAcctId;
                            dateTable.renderRow(data.LAY_TABLE_INDEX, {"secAcctId": secAcctId});
                        }
                    }, false);
                    countAddTable();
                }
                curPortfolioId = "";
                if(currentTab == 'dz') {
                    var param = {
                        portfolioId: data.portfolioId,
                        finprodId: $("#finprodId_" + currentTab).val(),
                        finprodType2: $("#finprodType2_" + currentTab).val(),
                        tradeMarket: $("#tradeMarket_" + currentTab).val(),
                        acctId: value,
                        tradePlat: "04",
                    };
                    Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAccAvailableCash', param, function (result) {
                        if (result.data) {
                            dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailableCash": result.data});
                        }
                    }, false);
                    param.acctId = null;
                    param.secAcctId = secAcctId;
                    Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAvailableNum', param, function (result) {
                        if (result.data) {
                            dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailablePos": result.data});
                        }
                    }, false);
                }
                if(currentTab == 'gd') {
                    var param = {
                        portfolioId: data.portfolioId,
                        finprodId: $("#finprodId_" + currentTab).val(),
                        finprodType2: $("#finprodType2_" + currentTab).val(),
                        tradeMarket: $("#tradeMarket_" + currentTab).val(),
                        acctId: value,
                        tradePlat: "03",
                    };
                    Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAccAvailableCash', param, function (result) {
                        if (result.data) {
                            dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailableCash": result.data});
                        }
                    }, false);
                    param.acctId = null;
                    param.secAcctId = secAcctId;
                    Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAvailableNum', param, function (result) {
                        if (result.data) {
                            dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailablePos": result.data});
                        }
                    }, false);
                }
                if(currentTab == 'jj') {
                    Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAccAvailableCash',
                        {
                            portfolioId: data.portfolioId,
                            acctId: data.acctNo,
                            tradeMarket: $("#tradeMarket_" + currentTab).val(),
                            ps: ps,
                        }, function (result) {
                            if (result.data) {
                                dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailableCash": result.data});
                            }
                        }, false);
                    Joyin.ajaxJson(basePath + 'etrexchange/common/etrExchangeTrade001/selectAvailableNum',
                        {
                            finprodId: $("#finprodId_" + currentTab).val(),
                            finprodType2: $("#finprodType2_" + currentTab).val(),
                            portfolioId: data.portfolioId,
                            ps: ps,
                            tradeMarket: $("#tradeMarket_" + currentTab).val(),
                            secAcctId: secAcctId,
                            tradePlat: "02",
                        }, function (result) {
                            if (result.data) {
                                dateTable.renderRow(data.LAY_TABLE_INDEX, {"accAvailablePos": result.data});
                            }
                        }, false);

                    linkTableEntrustNumAmt();
                }
            } else if (field == 'entrustNum') {
                if (parseFloat(value) < 0){
                    Joyin.error("指令数量不能为负数！",function(){
                        dateTable.renderRow(data.LAY_TABLE_INDEX, {"entrustNum": 0});
                    });
                    return ;
                }
                var datas = dateTable.getAllRows();
                if (datas){
                    var entrustNum = Number(0)
                    for (var i in datas) {
                        var num = 0;
                        if (datas[i].entrustNum == '' || datas[i].entrustNum == undefined) {
                            num = 0;
                        } else {
                            num = parseFloat(datas[i].entrustNum);
                        }
                        var ps1 = $("#ps_" + currentTab).val();
                        console.log( "3939 +++ " + num + ps + ":" + PS_T68);
                        if(ps1 == PS_T68 || ps1 == PS_T69){
                            entrustNum += num;
                        }else{
                            entrustNum += parseInt(num);
                        }
                    }
                    $("#entrustNum_" + currentTab).val(entrustNum);
                    calcEntrustNumAndAmt();
                    var entrustAmt =  $("#entrustAmt_" + currentTab).val();
                    dateTable.renderRow(data.LAY_TABLE_INDEX, {"entrustAmt": parseFloat(entrustAmt) * parseInt(value) / entrustNum});
                }
            }else if(field == 'entrustAmt'){
                if (parseFloat(value) < 0){
                    Joyin.error("指令金额不能为负数！",function(){
                        dateTable.renderRow(data.LAY_TABLE_INDEX, {"entrustAmt": 0});
                    });
                    return ;
                }
                var datas= dateTable.getAllRows();
                if (datas){
                    var entrustAmt = Math.floor(Number(0));
                    for (var i in datas) {
                        var amt = 0;
                        if (datas[i].entrustAmt == '' || datas[i].entrustAmt == undefined) {
                            amt = 0;
                        } else {
                            amt = parseFloat(datas[i].entrustAmt);
                        }
                        entrustAmt += parseFloat(amt);
                    }
                    $("#entrustAmt_" + currentTab).val(entrustAmt);
                    calcEntrustNumAndAmt();
                    var entrustNum =  $("#entrustNum_" + currentTab).val();
                    dateTable.renderRow(data.LAY_TABLE_INDEX, {"entrustNum": parseFloat(entrustNum) * parseInt(value) / entrustAmt});
                }
            }
        },
        done: function (table, allData) {
            if (dateTable.getAllRows()) {
                var array = dateTable.getAllRows();
                if (array.length > 0) {
                    if (array[0].ps == PS_T68 || array[0].ps == PS_T69) {
                        $("#entrustAmout_total_label").text("标准券数量");
                        var entrustAmt = $("#entrustAmt_" + currentTab).val();
                        console.log("3985" + ":" + entrustAmt);
                        var ps = $("#ps_" + currentTab).val();
                        if(ps == PS_T68 || ps == PS_T69){
                            $("#entrustAmt_" + currentTab).val(entrustAmt);
                        }else {
                            $("#entrustAmt_" + currentTab).val(Math.floor(new Number(entrustAmt)));
                        }
                    } else {
                        $("#entrustAmout_total_label").text("指令金额");
                    }
                    if (array[0].finprodMarketId) {
                        $("#finprodId_total").text(array[0].finprodMarketId);
                        $("#finprodName_total").text(array[0].finprodName);

                        var entrustNumTotal = 0;
                        var entrustAmoutTotal = 0;
                        for (var i = 0; i < array.length; i++) {
                            entrustNumTotal = parseFloat(entrustNumTotal) + parseFloat(isNumber(array[i].entrustNum) ? array[i].entrustNum : 0);
                            entrustAmoutTotal = parseFloat(entrustAmoutTotal) + parseFloat(isNumber(array[i].entrustAmt) ? array[i].entrustAmt : 0);
                        }
                        $("#entrustNum_total").text(entrustNumTotal);
                        $("#entrustAmout_total").text(entrustAmoutTotal);
                    } else {
                        $("#finprodId_total").text("--");
                        $("#finprodName_total").text("--");
                        $("#entrustNum_total").text("--");
                        $("#entrustAmout_total").text("--");
                    }
                } else {
                    $("#finprodId_total").text("--");
                    $("#finprodName_total").text("--");
                    $("#entrustNum_total").text("--");
                    $("#entrustAmout_total").text("--");
                }
            }
        }
    });

    //新增行
    function countAddTable() {
        setRiskFlag(1)
        var entrustNum = $("#entrustNum_" + currentTab).val();
        var entrustAmt = $("#entrustAmt_" + currentTab).val();
        var array = dateTable.getAllRows();
        var len = array.length;
        if (entrustNum % len == 0) {
            for (var i = 0; i < array.length; i++) {
                array[i].entrustNum = entrustNum / len;
                array[i].entrustAmt = entrustAmt / len;
            }
        } else { //除不尽
            var entrustAmtData = 0

            var entrustNumForOne = entrustNum / len;
            for (var i = 0; i < array.length; i++) {
                if(i == 0){
                    array[i].entrustNum = Math.ceil(entrustNumForOne);
                }else {
                    array[i].entrustNum = Math.floor(entrustNumForOne);
                }
                array[i].entrustAmt = entrustAmt / len;
            }
            for (var i = 0; i < array.length - 1; i++) {
                entrustAmtData += array[i].entrustAmt != 0 ? Number(array[i].entrustAmt).toFixed(2) : 0;
            }
            array[len - 1].entrustAmt = entrustAmt - entrustAmtData;
        }
        for (var i = 0; i < array.length; i++) {
            dateTable.renderRow(i, array[i])
        }
        ;
    }

    //
    function linkTableEntrustNumAmt() {
        setRiskFlag(1);
        var entrustNum = $("#entrustNum_" + currentTab).val();
        if (parseFloat(entrustNum) < 0){
            Joyin.error("指令数量不能为负数！",function(){
                $("#entrustNum_" + currentTab).val(0)
            });
            return ;
        }
        var entrustAmt = $("#entrustAmt_" + currentTab).val();
        var array = dateTable.getAllRows();
        var len = array.length;
        var entrustNumForOne = entrustNum / len;
        var ps = $("#ps_" + currentTab).val();
        // for (var i = 0; i < array.length; i++) {
        //     if (array[i].acctNo) {
        //         if(i == 0){
        //             if(ps == PS_T68 || ps == PS_T69){
        //                 array[i].entrustNum = Math.ceil(entrustNumForOne*100)/100;
        //             }else {
        //                 array[i].entrustNum = Math.ceil(entrustNumForOne);
        //             }
        //         }else {
        //             if(ps == PS_T68 || ps == PS_T69){
        //                 array[i].entrustNum = Math.floor(entrustNumForOne*100)/100;
        //             }else {
        //                 array[i].entrustNum = Math.floor(entrustNumForOne);
        //             }
        //         }
        //         array[i].entrustAmt = entrustAmt / len;
        //     }
        // }
        //组合分配调整
        var finprodId = $("#finprodId_" + currentTab).val();
        var targetType = $("#targetType_" + currentTab).val();
        var finprodType2 = $("#finprodType2_" + currentTab).val();
        var tradeMarket = $("#tradeMarket_" + currentTab).val();
        var ps = $("#ps_" + currentTab).val();
        var minStep;
        if (targetType) {
            var step = 1;
            var param = {
                finprodId: finprodId,
                targetType: targetType,
                finprodType2: finprodType2,
                tradeMarket: tradeMarket,
                ps: ps,
                currentTab: currentTab
            };
            Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/getMinNumStep', param, function (result) {
                if (result.minNum) {
                    minStep=result.minNum;
                }
            }, false);
        }
        var object=getSearchParams();
        var entrustPrice=object.entrustPrice;
        //券
        if(object.entrustBussType == ENTRUST_BUSS_TYPE_07||object.entrustBussType == ENTRUST_BUSS_TYPE_06||object.entrustBussType == ENTRUST_BUSS_TYPE_08){
            entrustPrice=object.fprice;
        }else if (object.priceModel == PRICE_MODEL_PRM03) {
            entrustPrice = $("#factPrice_" + currentTab).val();
            if (!object.entrustPrice) {
                entrustPrice = 0;
            }
        }
        var params = {
            targetType: targetType,
            // minNum
            entrustPrice: entrustPrice,
            minStep: minStep,
            entrustNum: $("#entrustNum_" + currentTab).val(),
            entrustAmt: $("#entrustAmt_" + currentTab).val(),
            portfolioList: array
        }
        Joyin.ajaxJson(basePath + '/etrexchange/common/selectEtrPortfolioAllocationList', params, function (result) {
            if(result.data){
                array=result.data.portfolioList;
                for (var i = 0; i < array.length; i++) {
                    dateTable.renderRow(i, array[i]);
                }
            }
        })
        // dateTable.reload();
    }

    function linkTableEntrustNumAmtFF() {
        var entrustNum = $("#entrustNum_" + currentTab).val();
        if (parseFloat(entrustNum) < 0){
            Joyin.error("指令数量不能为负数！",function(){
                $("#entrustNum_" + currentTab).val(0)
            });
            return ;
        }
        var entrustAmt = $("#entrustAmt_" + currentTab).val();
        var array = dateTable.getAllRows();
        var len = array.length;
        var entrustNumForOne = entrustNum / len;
        for (var i = 0; i < array.length; i++) {
            if(i == 0){
                array[i].entrustNum = Math.ceil(entrustNumForOne);
            }else {
                array[i].entrustNum = Math.floor(entrustNumForOne);
            }
            array[i].entrustAmt = entrustAmt / len;
        }
        for (var i = 0; i < array.length; i++) {
            dateTable.renderRow(i, array[i]);
        }
        ;
    }


    /**
     * 绑定删除事件
     */
    dateTable.bind('click', '.delt', function (data, index) {
        setRiskFlag(1)
        dateTable.removeRow(data);
        linkTableEntrustNumAmtFF();
    });

    var colsRiskTrialRet = [
        {
            field: 'kriStatusCn', width: 100, title: 'BaseRiskMathComm.currStatus', sortable: false,
            render: function (data, type, row, meta) {
                if (row.kriStatus == 0) {
                    return "<span>正常</span>";
                }
                if (row.kriStatus == 1) {
                    return "<span style='color:orange'>预警</span>";
                }
                if (row.kriStatus == 2) {
                    return "<span style='color:#F7580D'>审批</span>";
                }
                if (row.kriStatus == 3) {
                    return "<span style='color:#F71A0D'>违规</span>";
                }
                if (row.kriStatus == 4) {
                    return "<span style='color:#951F1F'>异常</span>";
                }
                return row.kriStatus;
            }
        }
        , {
            /* 指标名称*/
            field: 'kriName',
            width: 200,
            title: 'BaseRiskMathComm.indName',
            sortable: false
        }
        , {/*指标类型*/
            field: 'kriTypeCn',
            width: 100,
            title: 'BaseRiskMathComm.indType',
            sortable: false
        }
        , {
            /*组合名称*/
            field: 'portfolioName',
            width: 150,
            title: 'BaseRiskMathComm.portfolioName',
            sortable: false
        }
        , {
            /*违规阈值*/
            field: 'assetName',
            width: 150,
            title: 'BaseRiskMathComm.assetName',
            sortable: false
        }
        , {
            /*预警阈值*/
            field: 'warningLine',
            width: 150,
            title: 'BaseRiskMathComm.wthreshold',
            sortable: false
        }
        , {
            /*审批线*/
            field: 'approveLine',
            width: 150,
            title: 'BaseRiskMathComm.approveold',
            sortable: false
        }
        , {
            /*违规阈值*/
            field: 'forbiddenLn',
            width: 150,
            title: 'BaseRiskMathComm.vthreshold',
            sortable: false
        }
        , {
            /*当前实际值*/
            field: 'kriValue',
            width: 200,
            title: 'BaseRiskMathComm.currValue',
            sortable: false
        }
        , {
            /*原值*/
            field: 'oldValue',
            width: 200,
            title: 'BaseRiskMathComm.preValue',
            sortable: false
        }
        , {
            /*趋势*/
            field: 'kriTrend',
            width: 200,
            title: 'BaseRiskMathComm.kriTrend',
            sortable: false
        }
    ]

    var tableRisk = query("#riskTrialRetTable").joyintable({
        data: entity_RiskTrialRetList || [],
        cols: colsRiskTrialRet,
        page: true,
        addToLast: true,
        searchOnInit: true,
        checkbox: false,
        height: 290
    });

    function setRiskFlag(flag) {
        riskFlag = flag;
    }

    function setRskStatus(status) {
        rskStatus = status;
    }

    function paramCheck() {
        var ps = $("#ps_" + currentTab).val();
        console.log(riskFlag);
        if (ps && "Y" === pageRedisFlag && (ps !== PS_T68 && ps !== PS_T69) && (!entity_RiskTrialRetList || riskFlag !== 0)) {
            Joyin.error("请先完成风险试算")
            return false;
        }
        if ("02" === isRiskMathSoftcontrol && $('#riskTrialSpan').text() && $('#riskTrialSpan').text().trim() == '试算不通过') {
            Joyin.error("风险试算未通过")
            return false;
        }
        if ("01" === isRiskMathSoftcontrol && $('#riskTrialSpan').text() && $('#riskTrialSpan').text().trim() == '试算不通过') {
            console.log(123);
            Joyin.confirm("风险试算未通过,是否继续提交？", function () {
                var checkFlag = true;
                var object = getSearchParams();
                //组合也需要校验
                if (!(ps == PS_T04 || ps == PS_T46 || ps == PS_T97)) { //单组合 新股申购、配股、配债、可转债信用申购
                    if (object.portfolioList.length == 0) {
                        Joyin.error("请分配组合")
                        return false;
                    }
                    for (var index in object.portfolioList) {
                        var item = object.portfolioList[index];
                        if (!item.portfolioId || !item.acctNo) {
                            checkFlag = false;
                        }
                        if (!item.entrustNum) {
                            Joyin.error("组合分配中指令数量不能为空")
                            return false;
                        }
                        if (item.entrustNum % 10 != 0) {
                            Joyin.error("指令数量，请输入数量最小交易单位10的整数倍")
                            return false;
                        }

                    }
                    if (!checkFlag) {
                        Joyin.error("请填写完整组合信息")
                        return false;
                    }
                    //组合是否是系列户
                    var acctNoTemp = object.portfolioList[0].acctNo;
                    for (var index in object.portfolioList) {
                        var item = object.portfolioList[index];
                        if (item.acctNo != acctNoTemp) {
                            checkFlag = false;
                            break;
                        }
                    }
                    if (!checkFlag) {
                        Joyin.error("修改时组合分配必须为系列户")
                        return false;
                    }
                }
                saveOrSubmit('submit');
            });
        }
        if(($('#riskTrialSpan').text() && $('#riskTrialSpan').text().trim() == '试算通过') || (ps == PS_T68 || ps == PS_T69) ){
            saveOrSubmit('submit');
        }
    }

    /**
     * 指令提交或者保存
     */

    function saveOrSubmit(operateType) {
        var s;
        var object = getSearchParams();
        var riskdata = {};

        object.operateType = operateType;
        object.pageModel = pageModel;
        object.currentTab = currentTab;
        //riskdata = getriskdata(object);
        // if(BaseRiskMathComm && isNeedRiskcal()) {
        //     s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
        //     if(!s && s != ''){
        //         return;
        //     }
        //     if (window.BaseRiskMathComm.isCalcConditionChanged(riskdata)){
        //         return;
        //     }
        // }
        if(entity_RiskTrialRetList){
            object.riskTrialRetDto = entity_RiskTrialRetList;
        }
        if(object.finprodType2 == FINPROD_TYPE_TG_F01 && (object.ps == PS_T68 || object.ps == PS_T69)){
            if(object.entrustNum){
                object.entrustNum = object.entrustNum * 100;
            }
            if(object.entrustAmt){
                object.entrustAmt = object.entrustAmt * 100;
            }
            for (var j = 0; j < object.portfolioList.length; j++) {
                var item = object.portfolioList[j];
                item.entrustNum = item.entrustNum * 100;
                item.entrustAmt = item.entrustAmt * 100;
            }
            console.log(object);
        }

        //TODO 风险和授信
        if (object.operateType === 'save') {
            Joyin.confirm("请确认是否要保存", function () {
                WorkFlow.doSaveList(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/save', object, "EtrExchangeTrade004_btn_save", null, function () {
                    JoyinUtil.disableButton("EtrExchangeTradeCommon004_btn_save", false);
                })
            });
        } else {
            if (object.ps == PS_T49 || object.ps == PS_T03) {
                var posSwitch = false;
                for (var i = 0; i < object.portfolioList.length; i++) {
                    if (object.portfolioList[i].entrustNum > object.portfolioList[i].accAvailablePos) {
                        posSwitch = true;
                    }
                }
                if (ETR_CHECK_POS_SWITCH == '02' && posSwitch) {
                    Joyin.error("组合分配中可用持仓不足");
                    return ;
                }
            }

            saveOrSubmitQues(object,posSwitch);
        }
    }

    function saveOrSubmitQues(object, posSwitch) {
        Joyin.ajaxJson(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/checkSaveError', object,
            function (result) {
                console.log(result);
                if (ETR_CHECK_POS_SWITCH == '01' && posSwitch) {
                    result.data = "组合分配中可用持仓不足,请确认是否要提交";
                    Joyin.confirm(result.data, function () {
                        Joyin.ajaxJson(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/buildDtoFromTodoSubmit', object,
                            function (res) {
                                WorkFlow.doSubmitList(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/save', res, "EtrExchangeTrade004_btn_submit", null, function () {
                                    JoyinUtil.disableButton("EtrExchangeTradeCommon004_btn_submit", false);
                                })
                            });
                    });
                }else if (result.statuCode !== 200) {

                } else {
                    if (result.data == null) {
                        result.data = "请确认是否要提交";
                        Joyin.confirm(result.data, function () {
                            Joyin.ajaxJson(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/buildDtoFromTodoSubmit', object,
                                function (res) {
                                    WorkFlow.doSubmitList(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/save', res, "EtrExchangeTrade004_btn_submit", null, function () {
                                        JoyinUtil.disableButton("EtrExchangeTradeCommon004_btn_submit", false);
                                    })
                                });
                        });
                    } else {
                        Joyin.ajaxJson(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/buildDtoFromTodoSubmit', object,
                            function (res) {
                                WorkFlow.doSubmitList(basePath + '/etrexchange/common/EtrExchangeTradeCommon004/save', res, "EtrExchangeTrade004_btn_submit", null, function () {
                                    JoyinUtil.disableButton("EtrExchangeTradeCommon004_btn_submit", false);
                                })
                            });
                    }
                }
            });
    }


    $(function () {


        //按tab键定位按钮事件
        var inp = $(".input-control").val();
        $(document).bind('keydown', function (e) {
            var e = window.event ? window.event : e;
            var keyCode = e.which ? e.which : e.keyCode;
            if (keyCode == 9) {
                e.preventDefault();
                if (inp != null) {
                    var submitBtn = $("#EtrExchangeTrade001_btn_submit");
                    if (!submitBtn || submitBtn.length == 0) {
                        submitBtn = $("#EtrExchangeTrade001_btn_submit", window.parent.document);
                    }

                    if (submitBtn) {
                        submitBtn.attr("tabindex", "-1");
                        if (!submitBtn.attr("firstAddFocus")) {
                            submitBtn.attr("firstAddFocus", true)
                            submitBtn.append("<style>#EtrExchangeTrade001_btn_submit:focus{border:1px solid red}</style>");
                        }
                        setTimeout(function () {
                            submitBtn.focus();
                        }, 50);
                    }

                }
            }
        });
        // 加号点击事件
        $('.increase-button').click(function () {
            var input = $(this).parent().find('input');
            if (input.attr("disabled") === 'disabled' || input.attr("disabled") === true) {
                return
            }
            var name = input.attr("name");
            var num = Number(input.val());
            switch (name) {
                case 'price':
                    addMinPriceStep('add', num, input);
                    break;
                case 'entrustPrice':
                    addMinPriceStep('add', num, input);
                    break;
                case 'entrustNum':
                    addMinNumStep('add', num, input);
                    break;
                case 'entrustAmt':
                    input.val(num + Number(0.01)).blur().trigger('change');
                    break;
                default:
                    input.val(num + Number(1)).blur().trigger('change');
            }
        });
        // 减号点击事件
        $('.decrease-button').click(function () {
            var input = $(this).parent().find('input');
            if (input.attr("disabled") === 'disabled' || input.attr("disabled") === true) {
                return
            }
            var name = input.attr("name");
            var num = Number(input.val());
            switch (name) {
                case 'price':
                    addMinPriceStep('sub', num, input);
                    break;
                case 'entrustPrice':
                    addMinPriceStep('sub', num, input);
                    break;
                case 'entrustNum':
                    addMinNumStep('sub', num, input);
                    break;
                case 'entrustAmt':
                    input.val(num - Number(0.01)).blur().trigger('change');
                    break;
                default:
                    input.val(num - Number(1)).blur().trigger('change');
            }
        });
        layui.use(['form'], function () {
            // 绑定计算模式的点击事件
            var form = layui.form;
            form.on('radio(calculateField)', function (data) {
                layui.form.render(); //更新
                calculateFieldChange();
            });
        });

        if (calculateField) {
            $("input[name=calculateField_" + currentTab + "][value='" + calculateField + "']").attr("checked", true);
            layui.form.render(); //更新
        }

        initPage();

        if (calculateField) {
            $("input[name=calculateField_" + currentTab + "][value='" + calculateField + "']").attr("checked", true);
            layui.form.render(); //更新
        }

        // 绑定各种字段的change事件
        // 组合名称
        $("#portfolioId_jj,#portfolioId_dz,#portfolioId_gd").change(function () {
            portfolioIdChange(false);
            linkInvAim();
        });
        // 证券代码
        $("#secId_jj,#secId_dz,#secId_gd").change(function () {
            secIdChange();
            //linkFindSecAmountInfo();
            linkInvAim();
            var secId = $("#secId_" + currentTab).val();
            var param = {};
            param.secId = secId;
            var finprodType2 = $("#finprodType2_" + currentTab).val();
            if (finprodType2 == FINPROD_TYPE_TG_F06) {
                $("#priceModel_div_" + currentTab).show();
            }
            if(secId){
                Joyin.ajaxSync(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/IsBondAsset', param, function(result) {
                        if(result.data=='2') {     //2 返回2代表所选资产是债券且中债sppi通过
                            $("#businessModelDiv_"+ currentTab).show();
                            $("#businessModel_" + currentTab).val("").trigger('change');
                            $("#businessModel_" + currentTab).prop("disabled", false);
                        }else if(result.data=='3'){ //3 返回3代表所选资产是债券且中债sppi不通过
                            $("#businessModelDiv_"+ currentTab).show();
                            $("#businessModel_" + currentTab).val("FVTPL").trigger('change');
                            $("#businessModel_" + currentTab).prop("disabled", true);
                        }else if(result.data=="4"){
                            Joyin.error("当前资产未进行SPPI测试，无法进行指令提交/保存。");
                            $("#secId_" + currentTab).val("").trigger('change');
                        }else if(result.data=='1'){
                            $("#businessModelDiv_"+ currentTab).hide();
                        }
                    });
            }
            // if(secId){
            //     Joyin.ajaxSync(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/IsBondAsset', param, function(result) {
            //         //1 返回1代表所选资产不是债券 不处理
            //         if(result.data=='1') {
            //             $("#invAim_div_"+ currentTab).hide();
            //             $("#businessModelDiv_"+ currentTab).hide();
            //         }else if(result.data=='2'){     //2 返回2代表所选资产是债券且中债sppi通过
            //             if(SHOW_INVAIM_OR_BUSSINSSMODEL=='02'){
            //                 $("#invAim_div_"+ currentTab).hide();
            //                 $("#businessModelDiv_"+ currentTab).show();
            //                 $("#businessModel_" + currentTab).val("").trigger('change');
            //                 $("#businessModel_" + currentTab).prop("disabled", false);
            //             }else{
            //                 $("#invAim_div_"+ currentTab).show();
            //                 $("#businessModelDiv_"+ currentTab).hide();
            //             }
            //         }else if(result.data=='3'){       //3 返回3代表所选资产是债券且中债sppi不通过
            //             if(SHOW_INVAIM_OR_BUSSINSSMODEL=='02'){
            //                 $("#invAim_div_"+ currentTab).hide();
            //                 $("#businessModelDiv_"+ currentTab).show();
            //                 $("#businessModel_" + currentTab).val("Y2").trigger('change');
            //                 $("#businessModel_" + currentTab).prop("disabled", true);
            //             }else{
            //                 $("#invAim_div_"+ currentTab).show();
            //                 $("#businessModelDiv_"+ currentTab).hide();
            //             }
            //         }else if(result.data=="4"){        //4 返回4代表所选资产是债券且没有进行中债sppi测试
            //             if(SHOW_INVAIM_OR_BUSSINSSMODEL=='02'){
            //                 $("#invAim_div_"+ currentTab).hide();
            //                 $("#businessModelDiv_"+ currentTab).show();
            //                 Joyin.error("当前资产未进行SPPI测试，无法进行指令提交/保存。");
            //                 $("#secId_" + currentTab).val("").trigger('change');
            //             }else{
            //                 $("#invAim_div_"+ currentTab).show();
            //                 $("#businessModelDiv_"+ currentTab).hide();
            //             }
            //
            //         }
            //     });
            // }
        });
        // 交易场所
        $("#tradeMarket_jj,#tradeMarket_dz,#tradeMarket_gd").change(function () {
            tradeMarketChange();
        });
        // 交易场所（联动证券代码用）
        $("#acctTradeMarket_jj,#acctTradeMarket_dz,#acctTradeMarket_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            acctTradeMarketChange();
        });
        // 金融产品类型（投管）
        $("#finprodType2_jj,#finprodType2_dz,#finprodType2_gd").change(function () {
            finprodType2Change();
            //add by chenjiaxing 债券竞价交易添加偏离度 20210907 start
            if ($("#finprodType2_" + currentTab).val() == FINPROD_TYPE_TG_F01 && currentTab == 'jj') {
                document.getElementById('deviationDegree').innerHTML = "<span style='color: red'>" +
                    '0.0000%' + "</span>";
            }
            //add by chenjiaxing 债券竞价交易添加偏离度 20210907 end
        });
        // 资金账户
        $("#acctNo_jj,#acctNo_dz,#acctNo_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            acctNoChange(false);
            //linkFindSecAmountInfo();
        });
        // 证券账户
        $("#secAcctId_jj,#secAcctId_dz,#secAcctId_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            secAcctIdChange();
            linkInvAim();
        });
        // 价格模式
        $("#priceModel_jj,#priceModel_dz,#priceModel_gd,#secId_jj").change(function (e, isNotTrigger) {
            if (setPageItemsFlag) {
                return;
            }
            priceModelChange(false, isNotTrigger);
        });
        // 价格模式
        $("#declaType_dz,#declaType_gd").change(function (e, isNotTrigger) {
            declaTypeChange(false, isNotTrigger);
        });
        // 委托价格
        $("#entrustPrice_jj,#entrustPrice_dz,#entrustPrice_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            entrustNumChange();
            entrustPriceChange();
        });
        // 目标类型
        $("#targetType_jj,#targetType_dz,#targetType_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            targetTypeChange();
        });
        // 委托数量
        $("#entrustNum_jj,#entrustNum_dz,#entrustNum_gd,#ps_jj").change(function () {
            entrustNumChange();
        });
        // 委托金额
        $("#entrustAmt_jj,#entrustAmt_dz,#entrustAmt_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            entrustAmtChange();
        });
        // 净价、全价、到期收益率变更时重新计算
        $("#price_jj,#price_dz,#price_gd,#fprice_jj,#fprice_dz,#fprice_gd,#yield_jj,#yield_dz,#yield_gd").change(function () {
            if (setPageItemsFlag) {
                return;
            }
            calcPriceAndYield();
            //();
            if (currentTab == 'jj' && $("#price_jj").val()) {
                calDeviationDegree();
            }
        });
        // 委托方向变更时
        $("#ps_jj,#ps_dz,#ps_gd").change(function () {
            if (parentPageModel != PAGE_MODEL_REFERENCE) {  //解决场内交易指令流程代办显示为0
                psChange(false);
            }
        });
        //业务模式
        $("#businessModel_jj,#businessModel_dz,#businessModel_gd").change(function () {
            setRiskFlag(1);
        });

        // 资产代码变更时
        $("#finprodName_jj").change(function () {
            if (parentPageModel != PAGE_MODEL_REFERENCE) {  //解决场内交易指令流程代办显示为0
                psChange(false);
            }
        });

        // 数据来源变更时
        $("#dataSource_jj,#dataSource_dz,#dataSource_gd").change(function () {
            dataSourceChange();
        });

        //大宗交易席位号输入时设置动画开始时左偏移为0px;
        $("#seatId_dz").keyup(function (e) {
            /*        	$("div#seatId_div_dz .dropdown-menu.dropdown-menu-right")[0].addEventListener('transitionstart', function(){
                            $(this).css("transition","all 0.3s ease-in-out 0s");
                            $(this).css("left",0);
                        });*/
            $("#seatId_dz").attr({maxlength: "6"});
            /*        	$(".dropdown-menu.dropdown-menu-right")[0].addEventListener('transitionend', function(){
                            $(".dropdown-menu.dropdown-menu-right").css("transition","all 0.3s ease 0s");
                            $(".dropdown-menu.dropdown-menu-right").css("left",0);
                        });*/
        });

        //固定收益平台对手交易商输入限制
        $("#counterOrg_gd").keyup(function (e) {
            $("#counterOrg_gd").attr({maxlength: "3"});
        });

        //固定收益平台对手交易员输入限制
        $("#counterTrader_gd").keyup(function (e) {
            $("#counterTrader_gd").attr({maxlength: "6"});
        });

        //固定收益平台本方交易员输入限制
        $("#trader_gd").keyup(function (e) {
            $("#trader_gd").attr({maxlength: "6"});
        });

        //dsRefreshHq();

        $('#EtrExchangeTrade001JJ_btn_search').click(function () {
            searchDetail();
        });
        $('#EtrExchangeTrade001DZ_btn_search').click(function () {
            searchDetail();
        });
        $('#EtrExchangeTrade001GD_btn_search').click(function () {
            searchDetail();
        });
        // 绑定提交按钮
        $('#EtrExchangeTradeCommon004_btn_submit').click(function () {
            paramCheck();
        });

        // 绑定保存按钮
        $('#EtrExchangeTradeCommon004_btn_save').click(function () {
            saveOrSubmit('save');
        });

        // 绑定取消按钮
        $('#EtrExchangeTradeCommon004_btn_reset').click(function () {
            doReset();
        });
        //绑定风险是算
        $('#EtrExchangeTradeCommon004_btn_riskcal').click(function () {
            var object = getSearchParams();
            var tradeMarket = $("#tradeMarket_" + currentTab).val();
            var res = parseFloat(object.entrustNum) % 10;
            if (tradeMarket == '03' && res != 0 ){
                Joyin.error("组合分配指令数量需满足最小交易单位10的整数倍！" );
                return ;
            }
            var param = JSON.parse(JSON.stringify(object))
            Joyin.ajaxJson(basePath + '/etrexchange/common/etrExchangeTradeCommonRiskcal/doRiskcal', param, function (data) {
                $("#riskTrialSpan").html("&nbsp;&nbsp;" + data.data.data.riskTrialSpan);
                entity_RiskTrialRetList = data.data.data.riskcalReturnList;
                riskResultdata =  data.data.data.riskcalReturnList;
                $.each(entity_RiskTrialRetList, function (index, item) {
                    item.conclusion = data.data.data.riskTrialSpan;
                })
                tableRisk.clear();
                tableRisk.addRow(entity_RiskTrialRetList);
                setRiskFlag(0);
                setRskStatus(data.data.data.rskStatus);
            });
        });
        $("#etrExchangeTradeMulti001_btn_add").click(function () {
            var temp = {};
            dateTable.addRow(temp);
            if (currentTab == 'dz'){
             linkTableEntrustNumAmtFF();
            }
        });
    });
}());