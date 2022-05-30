(function () {
    var picinfo_UnitValue;
    var picinfo_PYield;
    var picinfo_DaySevenDayRate;
    var bakupMockTestParam = null; // 模拟试算画面参数
    var bakupMockTestResult = null; // 模拟试算结果

    //清空信息
    var clearInfo = function () {
        $('#baseRule').attr('disabled', false);
        $('#baseRule').val('').trigger("change");
        $('#mgrFeeRate').attr('disabled', false);
        $('#mgrFeeRate').val('').trigger("change");
        $('#raiseAmtPlan').attr('disabled', false);
        $('#raiseAmtPlan').val('').trigger("change");
        // 募集金额：设置为显示
        $('#raiseAmtPlanDiv').show();
        $('#vdate').attr('disabled', false);
        $('#vdate').val('').trigger("change");
        $('#mdate').attr('disabled', false);
        $('#mdate').val('').trigger("change");
    };
    //基础信息赋值
    var cleanInfoAndDisabled = function () {
        $('#baseRule').attr('disabled', true);
        $('#baseRule').val('').trigger("change");
        $('#mgrFeeRate').attr('disabled', true);
        $('#mgrFeeRate').val('').trigger("change");
        $('#raiseAmtPlan').attr('disabled', true);
        $('#raiseAmtPlan').val('').trigger("change");
        $('#vdate').attr('disabled', true);
        $('#vdate').val('').trigger("change");
        $('#mdate').attr('disabled', true);
        $('#mdate').val('').trigger("change");
    };
    
    /**
     * 计算模拟交易列表数据的交易金额
     * 交易金额 = 交易数量*（交易净价+应计利息）/100
     */
    var calcTradeAmt = function(rowData) {
        if (rowData.amount && rowData.tradePrice) {
            var tempAccuir = 0;
            if (rowData.accuir) {
                tempAccuir = rowData.accuir;
            }
            rowData.tradeAmt = JoyinMath.doDiv(JoyinMath.doMul(rowData.amount,
                    JoyinMath.doAdd(rowData.tradePrice, tempAccuir, 2), 2), 100, 2);
        } else {
            rowData.tradeAmt = null;
        }
    };
    
    /**
     * 计算到期收益率、行权收益率
     */
    var calcYield = function(rowData) {
        if (rowData.finprodId && rowData.type8 && rowData.tradePrice) {
            Joyin.ajaxSync(JoyinUtil.getRealPath('/poc/portfolio/ptlmock/PtlMockTest002/calcYield'), rowData, function (result) {
                if (result) {
                    rowData.yield = result.yield;
                    rowData.exerYield = result.exerYield;
                }
            });
        }
    };

    // 模拟交易列表
    var mockTradeTable = $("#mockTradeTable").joyintable({
        data: mockProductDealList == null ? [] : mockProductDealList,
        searchOnInit: true,
        checkbox: false,
        height: '225px',
        cols: [
            {field: 'portfolioId', sortable: false, title: '组合代码', width:150}
            , {field: 'portfolioName', sortable: false, title: '组合名称', width:150},
            {
                field: 'finprodId',
                title: '资产代码',
                sortable: false,
                width:150,
                edit: true,
                editOption: {
                    type: 'select',
                    showField: 'id',
                    url: JoyinUtil.getRealPath('/poc/portfolio/ptlmock/PtlMockTest002/getFinProdId')
                }
            }
            , {field: 'finprodAbbr', edit: false, sortable: false, width:150, title: 'PtlMockTest002.finprodName'}
            , {
                field: 'type8', edit: true, sortable: false, width:120, title: '资产类型', 
                editOption: {
                    type: 'select', multiple: false, searchable: true, result: PINAN_ASSET_TYPE
                }
            }
            , {field: 'tradeDate', sortable: false, width:100, title: 'PtlMockTest002.tradeDate', type: 'date'}
            , {
                field: 'ps', edit: true, sortable: false, width:100, title: 'PtlMockTest002.ps',
                editOption: {
                    type: 'select', multiple: false, searchable: true, result: PS
                }
            }
            , {
                field: 'invAim', edit: true, sortable: false, width:170, title: 'PtlMockTest002.invAim',
                editOption: {type: 'select', multiple: false, searchable: true, result: INV_AIM}
            }
            , {field: 'amount', sortable: false, title: '交易数量', width:100, edit: true, type: 'number', digit: 2}
            , {field: 'tradePrice', sortable: false, title: '交易净价', width:100, edit: true, type: 'number', digit: 2}
            , {field: 'accuir', sortable: false, title: '应计利息', width:100, edit: true, type: 'number', digit: 2}
            , {field: 'tradeAmt', sortable: false, width:120, title: '交易金额', type: 'number'}
            , {
                field: 'mdate',
                edit: true,
                sortable: false,
                width:100,
                title: '到期日',
                type: 'date',
                editOption: {type: 'date', formatter: 'yyyy-mm-dd'}
            }
            , {field: 'yield', sortable: false, title: '到期收益率%', width:150, edit: true, type: 'number', digit: 12}
            , {field: 'exerYield', sortable: false, width:150, title: '行权收益率%', type: 'number', digit: 12}
            , {
                field: 'ccy',
                edit: true,
                width:150,
                sortable: false,
                title: '币种',
                editOption: {type: 'select', multiple: false, searchable: true, result: CCY}
            }
            , {field: 'operation', sortable: false, title: 'SysCommon.operation', type: 'action', buttons: ['delt']}
        ],
        addbtn: true,
        addclick: function(row) {
            // 交易日期:默认平台日,不可修改 
            row.tradeDate = platDate;
            // 币种：默认人民币
            row.ccy = CCY_CNY;
        },
        beforeEditCell: function (preVal, field, rowData) {
            // 资产类型不允许编辑
            if (field == 'type8') {
                return false;
            } else {
                // 选择了资产，则到期日、到期收益率不允许标记
                if (rowData.finprodId) {
                    if (field == 'mdate' || field == 'yield') {
                        return false;
                    }
                    return true;
                }
                return true;
            }

        },
        editCell: function (value, data, field, cell, column) {
            // 交易金额联动计算
            if (field == 'amount' || field == 'tradePrice' || field == 'accuir') {
                // 交易金额 = 交易数量*（交易净价+应计利息）/100
                calcTradeAmt(data);
                // 计算到期收益率、行权收益率
                calcYield(data);
                mockTradeTable.renderRow(data.LAY_TABLE_INDEX, data);
            }
            // 资产代码change事件
            else if (field == 'finprodId') {
                var rowData = data;
                if (value != '') {
//                  1. 当所选资产代码非空时           
//                  根据选中的资产代码到标的类金融产品表查询资产信息，带出如下信息：        
//                      资产名称：标的类金融产品.资产简称   
//                      资产类型：金融产品分类.分类8（投管资产大类） 
//                      到期日：标的类金融产品.到期日 
//                      到期收益率%  
//                      币种：标的类金融产品.币种   
                    var obj = {'initValue': value};
                    Joyin.ajaxSync(JoyinUtil.getRealPath('/poc/portfolio/ptlmock/PtlMockTest002/getFinProdId'), obj, function (result) {
                        result = result[0].object;
                        if (result.TYPE_8) {
                            rowData.type8 = result.TYPE_8;
                        } else {
                            rowData.type8 = '';
                        }
                        if (result.FINPROD_ABBR) {
                            rowData.finprodAbbr = result.FINPROD_ABBR;
                        } else {
                            rowData.finprodAbbr = '';
                        }
                        if (result.MDATE) {
                            rowData.mdate = result.MDATE;
                        } else {
                            rowData.mdate = '';
                        }
                        if (result.CCY) {
                            rowData.ccy = result.CCY;
                        } else {
                            rowData.ccy = '';
                        }
                        
                        // 计算到期收益率、行权收益率
                        calcYield(rowData);

                        mockTradeTable.renderRow(data.LAY_TABLE_INDEX, rowData);
                    });
                } else {
//                    2. 当所选资产代码为空时                       
//                    清空如下信息：                 
//                        资产名称                
//                        资产类型                
//                        到期日             
//                        到期收益率%              
//                        币种              
                    rowData.finprodAbbr = '';
                    rowData.type8 = '';
                    rowData.mdate = '';
                    rowData.yield = '';
                    rowData.ccy = '';
                    mockTradeTable.renderRow(data.LAY_TABLE_INDEX, rowData);
                }
            }
            return true;
        },
        done: function () {
            this.bind('click', '.delt', function (data, index) {
                var object = {selectedItem: [data], operateType: "delete"};
                mockTradeTable.deleteRow(index);
            });
        }
    });

    //风险试算table
    var riskTable = $('#riskTable').joyintable({
        data: mockTestRskList == null ? [] : mockTestRskList,
        searchOnInit: true,
        checkbox: false,
        height: '300px',
        cols: [
            {field: 'indName', width: 150, sortable: false, title: '指标名称'}
            , {field: 'riskDate', width: 150, sortable: false, title: '日期', type: 'date'}
            , {field: 'wInvValue', width: 150, sortable: false, title: '预警线'}
            , {field: 'aInvValue', width: 150, sortable: false, title: '审批线'}
            , {field: 'eInvValue', width: 150, sortable: false, title: '违规线'}
            , {field: 'currValue', width: 150, sortable: false, title: '当前实际值'}
        ]
    });

    //测算前资产配置列表
    var preTestTable = $('#preTestTable').joyintable({
        data: preMockTestTaaList == null ? [] : preMockTestTaaList,
        searchOnInit: true,
        checkbox: false,
        loadonce:true,
        page:false,
        height:"200px",
        cols: [
            {field: 'assetTypeName', width: 100, sortable: false, title: '资产维度'}
            , {field: 'cost', width: 100, sortable: false, type: 'number', title: '成本（元）'}
            , {field: 'marketValue', width: 100, sortable: false, type: 'number', title: '市值（元）'}
            , {field: 'accruedInc', width: 100, sortable: false, type: 'number', title: '应收利息（元）'}
            , {field: 'amtRation', width: 100, sortable: false, type: 'number', title: '规模占比'}
        ],
        done:function(){
        }
    });
    /**
     * 加载资产配置图表
     */
    var loadAssetDisposeChart = function(chartId, chartDataList) {
        var assetDimension = [];//资产维度
        var evasionRatio=[];//规避占比（%）
        if (chartDataList) {
            $.each(chartDataList, function(index, rowData) {
                assetDimension.push(rowData.assetTypeName);
                var tempObj = {name : rowData.assetTypeName, value : rowData.amtRation};
                evasionRatio.push(tempObj)
            });
        }

        var chartOption = {
            title : {
                text: '资产大类规模占比',
                x:'center'
            },
            tooltip : {
                trigger: 'item',
                formatter: "{b} : {d}%"
            },
            legend: {
                x : 'center',
                y : 'bottom',
                data: ['债券','基金','计划类资产(利率型)','计划类资产(货币型)','计划类资产(净值型)','存款','固收类资产','质押式回购','拆借','买断式回购','股票','债券借贷','外汇即期','外汇远期','外汇掉期','利率互换','一级市场债','现金']
            },
            series: [{
                name:'半径模式',
                radius: '60%',
                center:['50%','43%'],
                type: 'pie',
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                color: ['#FF4500','#FFD700','#EE00EE','#B452CD','#050505','#191970','#8B8B00','#8B6914','#15f31f'],
                data: evasionRatio
            }]
        };
        echarts.init(document.getElementById(chartId)).setOption(chartOption);
    };

    //测算后资产配置
    var mockTestTable = $('#mockTestTable').joyintable({
        data: afterMockTestTaaList == null ? [] : afterMockTestTaaList,
        searchOnInit: true,
        checkbox: false,
        loadonce:true,
        page:false,
        height:"200px",
        cols: [
            {field: 'assetTypeName', width: 100, sortable: false, title: '资产维度'}
            , {field: 'cost', width: 100, sortable: false, type: 'number', title: '成本（元）'}
            , {field: 'marketValue', width: 100, sortable: false, type: 'number', title: '市值（元）'}
            , {field: 'accruedInc', width: 100, sortable: false, type: 'number', title: '应收利息（元）'}
            , {field: 'amtRation', width: 100, sortable: false, type: 'number', title: '规模占比'}
        ],
        done:function(){
        }
    });

    //可选资产
    $('#PtlMockTest002_btn_selectAsset').click(function () {
        Joyin.dialog(JoyinUtil.getLabel("PtlMockTest003"), basePath
            + "poc/portfolio/ptlmock/PtlMockTest003/init", "80%", "80%", {
            pageModel: PAGE_MODEL_NEW
        }, function () {
            if (top.returnData) {
                $.each(top.returnData, function(index, rowData) {
                    var newRowData = {};
                    newRowData.portfolioId = rowData.portfolioId;
                    newRowData.portfolioName = rowData.portfolioNamep;
                    newRowData.finprodId = rowData.finprodId;
                    newRowData.finprodAbbr = rowData.finprodAbbr;
                    newRowData.type8 = rowData.type8;
                    newRowData.tradeDate = platDate; // 默认平台日
                    newRowData.ps = ps_T01; // 交易方向默认为买入
                    newRowData.invAim = rowData.invAim;
                    newRowData.amount = rowData.amountp;
                    newRowData.tradePrice = rowData.price;
                    newRowData.accuir = rowData.tdyIntincexp;
                    // 计算模拟交易列表数据的交易金额
                    calcTradeAmt(newRowData);
                    newRowData.mdate = rowData.mdate;
                    newRowData.yield = rowData.yield;
                    newRowData.exerYield = rowData.exerYield;
                    newRowData.ccy = rowData.assetCcy;
                    if (!newRowData.ccy) {
                        // 币种：默认人民币
                        newRowData.ccy = CCY_CNY;
                    }

                    mockTradeTable.addRow(newRowData);
                });
            }
        });
    });

    // 导入按钮点击事件
    $('#PtlMockTest002_btn_importTrade').click(function () {
        Joyin.uploadFile(function (fileName) {
            Joyin.ajax('checkForImportFile', {fileName: fileName}, function (result) {
                if (result.length == 0) {
                    Joyin.ajax('importFile', {fileName: fileName}, function (result) {
                        $('iframe')[0].contentWindow.Joyin.closeTopWindow();
                        $.each(result, function (i, v) {
                            // 计算模拟交易列表数据的交易金额
                            calcTradeAmt(v);
                            mockTradeTable.addRow(v);
                        });
                    });
                }
            });
        });

    });
    
    /**
     * 获取图表option
     */
    var genChartOption = function(titleText, legendData, xAxisData, seriesName, seriesData, baseRuleSeriesData, toolTipData) {
        legendData.push("业绩基准计算结果");
        return {
            title: {
                text: titleText
            },
            toolbox: {
                feature:{
                    //saveAsImage: {}
                }
            },
            animation : false,//去掉动画效果
            tooltip: {
                trigger: 'axis',
                formatter:function(param){
                    if (param[0]) {
                        var tmp = param[0]['data'];
                        var tempFormatter = "";
                        if (tmp) {
                            if (titleText == "净值走势") {
                                tempFormatter = '日期：'+ tmp.tempDate +'<br>'
                                + '单位净值：'+ tmp.netUnitValue + '<br>'
                                + '涨跌幅：'+ tmp.valChg +'<br>'
                                + '业绩基准计算结果：' + tmp.baseRuleChg;
                            } else if (titleText == "万份收益走势") {
                                tempFormatter = '日期：'+ tmp.tempDate +'<br>'
                                + '万份收益：'+ tmp.netUnitValue + '<br>'
                                + '业绩基准计算结果：' +tmp.baseRuleChg;
                            } else if (titleText == "七日年化收益率走势") {
                                tempFormatter = '日期：'+ tmp.tempDate + '<br>'
                                + '七日年化收益率：'+ tmp.sevenDayRate + '<br>'
                                + '业绩基准计算结果：' + tmp.baseRuleChg;
                            }
                        }
                        return tempFormatter;
                    }
                }
            },
            legend: {
                data: legendData
            },
            xAxis: {
                data: xAxisData
            },
            yAxis: {
                //splitLine:{show: false},
                type: 'value'
            },
            series: [{
                name: seriesName,
                type: 'line', //线性
                smooth: true,
                data: toolTipData,
                itemStyle: {
                    normal: { // 颜色
                        color: 'blue'
                    }
                },
                markLine: {
                    symbol: 'none',               //去掉警戒线最后面的箭头                     
                    data: [{
                        silent: false,             //鼠标悬停事件  true没有，false有
                        lineStyle: {               //警戒线的样式  ，虚实  颜色
                            type: 'solid',
                            color: 'blue',
                        },
                        xAxis: '平台日'           // 警戒线的标注值
                    }]
                }
            },
                {
                    name: seriesName,
                    type: 'line',
                    data: seriesData,
                    itemStyle: {
                        normal: { // 颜色
                            color: 'blue'
                        }
                    }
                },
                {
                name: "业绩基准计算结果",
                type: 'line', //线性
                smooth: true,
                data: baseRuleSeriesData,
                itemStyle: {
                    normal: { // 颜色
                        color: 'red'
                    }
                }
            }]
        };
    };

    /**
     * 加载净值走势、万份收益走势、七日年化收益率走势
     */
    var loadTrendChart = function() {
        var dateArray = [];
        var unitValueDataArray = [];
        var sevenDayRateDataArray = [];
        var baseRuleSeriesDataArray = [];
        var toolTipData = [];
        if (mockTestNetTrendList) {
            $.each(mockTestNetTrendList, function(index, rowData) {
                var tempDate = new Date(rowData.cpriceDate).Format('yyyy-MM-dd');
                if (tempDate == platDate) {
                    tempDate = "平台日";
                }
                var toolObj = {};
                toolObj.tempDate = tempDate;
                toolObj.netUnitValue = rowData.netUnitValue;
                toolObj.valChg = rowData.valChg;
                toolObj.baseRuleChg = rowData.baseRuleChg;
                toolObj.sevenDayRate = rowData.sevenDayRate;
                dateArray.push(tempDate);
                toolTipData.push(toolObj);
                unitValueDataArray.push(rowData.netUnitValue);
                sevenDayRateDataArray.push(rowData.sevenDayRate);
                baseRuleSeriesDataArray.push(rowData.baseRuleChg);
            });
        }
        if ($("#profitType").val() == profitType01 || !$("#profitType").val()) {
            // 单位净值走势
            var unitValueOption = genChartOption("净值走势", ['单位净值'], dateArray, '单位净值',
                unitValueDataArray, baseRuleSeriesDataArray, toolTipData);
            var echartUnitValue = echarts.init(document.getElementById('echartUnitValue'), '',
                unitValueOption);
            echartUnitValue.setOption(unitValueOption, true);
            picinfo_UnitValue = echartUnitValue.getConnectedDataURL({
                type: 'png',
                backgroundColor: '#fff',
                pixelRatio: 2
            });
        }
        if ($("#profitType").val() == profitType03 || !$("#profitType").val()) {
            // 万份收益走势
            var pYieldOption = genChartOption("万份收益走势", ['万份收益'], dateArray, '万份收益',
                unitValueDataArray, baseRuleSeriesDataArray, toolTipData);
            var echartPYield = echarts.init(document.getElementById('echartPYield'), '',
                pYieldOption);
            echartPYield.setOption(pYieldOption, true);
            picinfo_PYield = echartPYield.getConnectedDataURL({
                type: 'png',
                backgroundColor: '#fff',
                pixelRatio: 2
            });
        }

        // 七日年化收益率走势
        var sevenDayRateOption = genChartOption("七日年化收益率走势", ['七日年化收益率'], dateArray, '七日年化收益率',
            sevenDayRateDataArray, baseRuleSeriesDataArray, toolTipData);
        var echartDaySevenDayRate = echarts.init(document.getElementById('echartDaySevenDayRate'),
            '', sevenDayRateOption);
        echartDaySevenDayRate.setOption(sevenDayRateOption, true);
        picinfo_DaySevenDayRate = echartDaySevenDayRate.getConnectedDataURL({
            type: 'png',
            backgroundColor: '#fff',
            pixelRatio: 2
        });
    };
    
    /**
     * 画面初始化控制画面显示
     */
    var controlPageShowWhenInit = function() {
        var profitType = $("#profitType").val();
        // 净值产品展示单位净值，货币产品展示万份收益
        if (profitType == profitType01) {//净值
            /*
             *  隐藏
             *  前一日组合表现、实时组合表现（考虑当日成交）、实时组合表现（试算后）
             *   三个区域的万份收益、平均剩余期限、平均剩余存续期限设置为显示
             * 显示
             * 三个区域内的 基点价值 和 组合久期
             */
             $('div[name=value_div]').show();
             $('div[name=ccy_div]').hide();
        } else if (profitType == profitType03) {//货币
            /*
             *  显示
             *  前一日组合表现、实时组合表现（考虑当日成交）、实时组合表现（试算后）
             *   三个区域的万份收益、平均剩余期限、平均剩余存续期限设置为显示
             * 隐藏
             * 三个区域内的 基点价值 和 组合久期
             */
            $('div[name=value_div]').hide();
            $('div[name=ccy_div]').show();
        }
        
        var portfolioType = $("#portfolioType").val();
        // 虚拟组合
        if (portfolioType == portfolioType04) {
            // 募集金额：清空，设置为显示，且必填。
            $('#raiseAmtPlanDiv').show();

            // 前一日组合表现、实时组合表现（考虑当日成交）不显示
            $('#preDay_div').hide();
            $('#realTime_div').hide();
        }
        else {
            // 募集金额：清空，设置为不显示，非必填。
            $('#raiseAmtPlanDiv').hide();
            $('#preDay_div').show();
            $('#realTime_div').show();
        }
    }
    
    //画面初始化
    var pageInit = function (isPageInit) {
        if (isPageInit) {
            //监听Tab切换，以改变地址hash值
            layui.element.on('tab(mockResultTabDiv)', function(ele) {
                // 点击资产配置tab
                if (ele.index === 1) {
                    // 加载：测算前（考虑当日成交）图表
                    loadAssetDisposeChart("preTestChart", preMockTestTaaList);
                    // 加载：测算后图表
                    loadAssetDisposeChart("mockTestChart", afterMockTestTaaList);
                }
            });
        }
        // 加载净值走势、万份收益走势、七日年化收益率走势
        loadTrendChart();
        // 画面初始化控制画面显示
        controlPageShowWhenInit();
    };
    $('#PtlMockTest002_btn_save').click(function () {
        //保存
        saveOrSubmit('save');
    });
    
    /**
     * 获取画面参数
     */
    var getPageParam = function() {
//        var paramObj = $.serializeToObject('#submitForm');
        var paramObj = {};
        paramObj.portfolioId = $('#portfolioId').val();
        paramObj.portfolioName = $('#portfolioName').val();
        paramObj.portfolioType = $('#portfolioType').val();
        paramObj.profitType = $('#profitType').val();
        paramObj.vdate = $('#vdate').val();
        paramObj.mdate = $('#mdate').val();
        paramObj.baseRule = $('#baseRule').val();
        paramObj.mgrFeeRate = $('#mgrFeeRate').val();
        paramObj.raiseAmtPlan = $('#raiseAmtPlan').val();
        paramObj.againInvestRate = $('#againInvestRate').val();
        paramObj.finanCost = $('#finanCost').val();

        // 模拟交易列表
        paramObj.pageMockDealList = mockTradeTable.getAllRows();
        // 选择区间
        paramObj.mockRangeStartDate = $.trim($('#mockRange').startDate());
        paramObj.mockRangeEndDate = $.trim($('#mockRange').endDate());
        
        return paramObj;
    }
    
    /**
     * 模拟试算按钮事件
     */
    $('#PtlMockTest002_btn_mockTest').click(function () {
        var paramObj = getPageParam();
        paramObj.operateType = "doMockTest";
        
        Joyin.ajaxJson(basePath + 'poc/portfolio/ptlmock/PtlMockTest002/check', paramObj, function (result) {
            var confirmMsg = JoyinUtil.getMessage('COMMON_C_0001', ["进行模拟试算"]);
            Joyin.confirm(confirmMsg, function () {
                Joyin.ajaxJson(basePath + 'poc/portfolio/ptlmock/PtlMockTest002/doMockTest', paramObj, function (result) {
                    if (result && result.data) {
                        // 备份模拟试算画面参数和模拟试算结果
                        delete paramObj.operateType;
                        bakupMockTestParam = paramObj;
                        bakupMockTestResult = result.data;

                        // 前一日组合表现
                        $('#preDayUnitValue').val(result.data.preDayUnitValue).trigger("change").trigger("blur"); // 单位净值
                        $('#preDayPYield').val(result.data.preDayPYield).trigger("change").trigger("blur"); // 万份收益
                        $('#preDayValChg').val(result.data.preDayValChg).trigger("change").trigger("blur"); // 涨跌幅%
                        $('#preDayCashPosition').val(result.data.preDayCashPosition).trigger("change").trigger("blur"); // 剩余现金
                        $('#preDayAssetValue').val(result.data.preDayAssetValue).trigger("change").trigger("blur"); // 总资产
                        $('#preDayAssetNetvalue').val(result.data.preDayAssetNetvalue).trigger("change").trigger("blur"); // 净资产
                        $('#preDayLeverRate').val(result.data.preDayLeverRate).trigger("change").trigger("blur"); // 杠杆率%
                        $('#preDayBpvalue').val(result.data.preDayBpvalue).trigger("change").trigger("blur"); // 基点价值
                        $('#preDayPtlMDuration').val(result.data.preDayPtlMDuration).trigger("change").trigger("blur"); // 组合久期
                        $('#preDaySevenDayRate').val(result.data.preDaySevenDayRate).trigger("change").trigger("blur"); // 七日年化收益率%
                        $('#preDayAvgsurplustime').val(result.data.preDayAvgsurplustime).trigger("change").trigger("blur"); // 平均剩余期限
                        $('#preDayAvgsurplustimeP').val(result.data.preDayAvgsurplustimeP).trigger("change").trigger("blur"); // 平均剩余存续期限
                        
                        // 实时组合表现
                        $('#realTimeUnitValue').val(result.data.realTimeUnitValue).trigger("change").trigger("blur"); // 单位净值
                        $('#realTimePYield').val(result.data.realTimePYield).trigger("change").trigger("blur"); // 万份收益
                        $('#realTimeValChg').val(result.data.realTimeValChg).trigger("change").trigger("blur"); // 涨跌幅%
                        $('#realTimeCashPosition').val(result.data.realTimeCashPosition).trigger("change").trigger("blur"); // 剩余现金
                        $('#realTimeAssetValue').val(result.data.realTimeAssetValue).trigger("change").trigger("blur"); // 总资产
                        $('#realTimeAssetNetvalue').val(result.data.realTimeAssetNetvalue).trigger("change").trigger("blur"); // 净资产
                        $('#realTimeLeverRate').val(result.data.realTimeLeverRate).trigger("change").trigger("blur"); // 杠杆率%
                        $('#realTimeBpvalue').val(result.data.realTimeBpvalue).trigger("change").trigger("blur"); // 基点价值
                        $('#realTimePtlMDuration').val(result.data.realTimePtlMDuration).trigger("change").trigger("blur"); // 组合久期
                        $('#realTimeSevenDayRate').val(result.data.realTimeSevenDayRate).trigger("change").trigger("blur"); // 七日年化收益率%
                        $('#realTimeAvgsurplustime').val(result.data.realTimeAvgsurplustime).trigger("change").trigger("blur"); // 平均剩余期限
                        $('#realTimeAvgsurplustimeP').val(result.data.realTimeAvgsurplustimeP).trigger("change").trigger("blur"); // 平均剩余存续期限
                        
                        // 实时组合表现（测算后）
                        $('#mockUnitValue').val(result.data.mockUnitValue).trigger("change").trigger("blur"); // 单位净值
                        $('#mockPYield').val(result.data.mockPYield).trigger("change").trigger("blur"); // 万份收益
                        $('#mockValChg').val(result.data.mockValChg).trigger("change").trigger("blur"); // 涨跌幅%
                        $('#mockCashPosition').val(result.data.mockCashPosition).trigger("change").trigger("blur"); // 剩余现金
                        $('#mockAssetValue').val(result.data.mockAssetValue).trigger("change").trigger("blur"); // 总资产
                        $('#mockAssetNetvalue').val(result.data.mockAssetNetvalue).trigger("change").trigger("blur"); // 净资产
                        $('#mockLeverRate').val(result.data.mockLeverRate).trigger("change").trigger("blur"); // 杠杆率%
                        $('#mockBpvalue').val(result.data.mockBpvalue).trigger("change").trigger("blur"); // 基点价值
                        $('#mockPtlMDuration').val(result.data.mockPtlMDuration).trigger("change").trigger("blur"); // 组合久期
                        $('#mockSevenDayRate').val(result.data.mockSevenDayRate).trigger("change").trigger("blur"); // 七日年化收益率%
                        $('#mockAvgsurplustime').val(result.data.mockAvgsurplustime).trigger("change").trigger("blur"); // 平均剩余期限
                        $('#mockAvgsurplustimeP').val(result.data.mockAvgsurplustimeP).trigger("change").trigger("blur"); // 平均剩余存续期限
                        $('#mockOneMonthRate').val(result.data.mockOneMonthRate).trigger("change").trigger("blur"); // 一月收益率%
                        $('#mockThreeMonthRate').val(result.data.mockThreeMonthRate).trigger("change").trigger("blur"); // 三月收益率%
                        $('#mockQuarterDayRate').val(result.data.mockQuarterDayRate).trigger("change").trigger("blur"); // 至下一季报日收益率
                        $('#mockOneYearRate').val(result.data.mockOneYearRate).trigger("change").trigger("blur"); // 一年收益率%
                        $('#mockYield').val(result.data.mockYield).trigger("change").trigger("blur"); // 到期收益率%
                        $('#mockRangeRate').val(result.data.mockRangeRate).trigger("change").trigger("blur"); // 区间收益率%
                        
                        // 重新加载：风险试算列表
                        mockTestRskList = result.data.mockTestRskList;
                        riskTable.clear(function() {
                            $.each(mockTestRskList, function(index, rowData) {
                                riskTable.addRow(rowData);
                            });
                        });

                        // 重新加载：资产配置 测算前（考虑当日成交）列表及图表
                        preMockTestTaaList = result.data.preMockTestTaaList;
                        preTestTable.clear(function() {
                            $.each(preMockTestTaaList, function(index, rowData) {
                                preTestTable.addRow(rowData);
                            });
                        });
                        loadAssetDisposeChart("preTestChart", preMockTestTaaList);

                        // 重新加载：资产配置 测算后列表及图表
                        afterMockTestTaaList = result.data.afterMockTestTaaList;
                        mockTestTable.clear(function() {
                            $.each(afterMockTestTaaList, function(index, rowData) {
                                mockTestTable.addRow(rowData);
                            });
                        });
                        loadAssetDisposeChart("mockTestChart", afterMockTestTaaList);

                        // 重新加载：净值走势、万份收益走势、七日年化收益率走势
                        mockTestNetTrendList = result.data.mockTestNetTrendList;
                        loadTrendChart();
                    }
                });
            });
        });
    });

    /**
     * 保存或提交
     *
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        // 如果没有进行模拟试算，提示要要进行模拟试算
        if (!bakupMockTestResult) {
            Joyin.error(JoyinUtil.getMessage('PtlMockTest_E_0027'));
            return;
        }
        
        // 获取画面参数
        var pageObj = getPageParam();
        // 若模拟试算画面参数有变更，提示重新进行模拟试算
        if (bakupMockTestParam && JSON.stringify(bakupMockTestParam) != JSON.stringify(pageObj)) {
            Joyin.error(JoyinUtil.getMessage('PtlMockTest_E_0028'));
            return;
        }

        pageObj.operateType = operateType;
        pageObj.pageModel = pageModel;

        Joyin.ajaxJson(basePath + 'poc/portfolio/ptlmock/PtlMockTest002/check', pageObj,
            function (result) {
                Joyin.confirm(result.data, function () {
                    if (operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'poc/portfolio/ptlmock/PtlMockTest002/save', bakupMockTestResult, "PtlMockTest002_btn_save");
                    } else {
                        WorkFlow.doSubmitList(basePath + 'poc/portfolio/ptlmock/PtlMockTest002/save', bakupMockTestResult, "PtlMockTest002_btn_submit");
                    }
                });
            });
    }

    $('#echartUnitValue_export').click(function () { 
        var cellList=['日期','单位净值','业绩基准计算结果','涨跌幅%'];
        Joyin.exportExcel('chartExport', {picinfo: picinfo_UnitValue,
            mockTestNetTrendList: JSON.stringify( mockTestNetTrendList),
            type:'净值走势',cellList:JSON.stringify( cellList)}); 
    });
    $('#echartPYield_export').click(function () {
        var cellList=['日期','万份收益','业绩基准计算结果'];
        Joyin.exportExcel('chartExport', {picinfo: picinfo_PYield,
            mockTestNetTrendList:JSON.stringify( mockTestNetTrendList), 
            type:'万份收益走势',cellList:JSON.stringify( cellList)});
    });
    $('#echartDaySevenDayRate_export').click(function () {
        var cellList=['日期','七日年化收益率','业绩基准计算结果',];
        Joyin.exportExcel('chartExport', {picinfo: picinfo_DaySevenDayRate,
            mockTestNetTrendList:JSON.stringify( mockTestNetTrendList), 
            type:'七日年化收益率走势',cellList:JSON.stringify( cellList)});
    });

    //画面加载完成
    $(function () {
        pageInit(true);
        //自动带出组合信息
        $('#portfolioId').on('change', function () {
            var data = $('#portfolioId').select2('data')[0];

            if (data.object) {
                var object = data.object;
                
                // 净值产品展示单位净值，货币产品展示万份收益
                if (object.PROFIT_TYPE == profitType01) {//净值
                    /*
                    *  隐藏
                    *  前一日组合表现、实时组合表现（考虑当日成交）、实时组合表现（试算后）
                    *   三个区域的万份收益、平均剩余期限、平均剩余存续期限设置为显示
                    * 显示
                    * 三个区域内的 基点价值 和 组合久期
                    */
                    $('div[name=value_div]').show();
                    $('div[name=ccy_div]').hide();
                } else if (object.PROFIT_TYPE == profitType03) {//货币
                    /*
                     *  显示
                     *  前一日组合表现、实时组合表现（考虑当日成交）、实时组合表现（试算后）
                     *   三个区域的万份收益、平均剩余期限、平均剩余存续期限设置为显示
                     * 隐藏
                     * 三个区域内的 基点价值 和 组合久期
                     */
                    $('div[name=value_div]').hide();
                    $('div[name=ccy_div]').show();
                }
                
                if (object.PORTFOLIO_TYPE == portfolioType04) {
                    /*
                    *  虚拟组合
                    *  业绩基准 管理费率 募集金额  清空必填
                    *  起息日 到期日 可输
                    */
                    clearInfo();

                    // 募集金额：清空，设置为显示，且必填。
                    $('#raiseAmtPlanDiv').show();

                    // 前一日组合表现、实时组合表现（考虑当日成交）不显示
                    $('#preDay_div').hide();
                    $('#realTime_div').hide();
                } else {
                    /*
                   *  现有组合
                   *  业绩基准 管理费率 募集金额  组合带出不可输，非必填
                   *  起息日 到期日 不可输
                   *  前一日组合表现、实时组合表现（考虑当日成交）显示
                   */
                    cleanInfoAndDisabled();

                    // 募集金额：清空，设置为不显示，非必填。
                    $('#raiseAmtPlanDiv').hide();

                    // 查询组合业绩基准、管理费率
                    var paramObj = {portfolioId: $('#portfolioId').val()};
                    Joyin.ajax(JoyinUtil.getRealPath('/poc/portfolio/ptlmock/PtlMockTest002/getPtlDetailInfo'), paramObj, function (result) {
                        if (result) {
                            $('#baseRule').val(result.finBaseRuleYield).trigger("change");
                            $('#mgrFeeRate').val(result.manageFeeRate).trigger("change");
                        }
                    });

                    $('#preDay_div').show();
                    $('#realTime_div').show();
                }

                // 从组合表查询如下字段并设值到画面对应项目：
                // 组合名称、组合类型、收益类型、起息日、到期日
                $('#portfolioName').val(data.text);
                $('#portfolioType').textSelect('setValue', object.PORTFOLIO_TYPE);
                $('#profitType').textSelect('setValue', object.PROFIT_TYPE);
                $('#vdate').val(object.vdate).trigger("change");
                $('#mdate').val(object.mdate).trigger("change");
            } else {
                //清空信息
                var submitForm = $.serializeToObject('#submitForm', true);
                clearInfo();
                $.each(submitForm, function (i, v) {
                    $('#' + i).val('').trigger("change");
                    //下拉框特殊处理
                    /*if ($('#' + i).hasClass('input-select')) {
                        $('#' + i).textSelect('setValue', '');
                    }*/
                });
                //$('#baseInfo_div').html('');
                //$('#content_div').clear();
                //$('.form-input').val('');
                $('div[name=value_div]').show();
                $('div[name=ccy_div]').hide();
                $('#preDay_div').show();
                $('#realTime_div').show();
            }
        });
    })
}());