$(function () {
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var result = $.serializeToObject('#submitForm');
        if ($("#searchDate").val()) {
            result.searchDateStart = $("#searchDate").startDate();
            result.searchDateEnd = $("#searchDate").endDate();
        }
        if($("#portfolioId").val() == null || $("#portfolioId").val() == ''){
            result.portfolioId = $("#portfolioId").val();
        }
        else{
            result.portfolioId = $("#portfolioId").val().join(',');
        }
        if($("#assetType").val() == null || $("#assetType").val() == ''){
            result.assetType = $("#assetType").val();
        }
        else{
            result.assetType = $("#assetType").val().join(',');
        }
        // add by wangyang1 【吉林银行】 增加 产品经理  2019年9月29日 start 
	    if($("#prodManager").val() == null || $("#prodManager").val() == ''){
            result.prodManager = null;
        }
        else{
            result.prodManager = $("#prodManager").val();
        }
		// add by wangyang1 【吉林银行】 增加 产品经理  end 
        return result;
    };
    var dataTable=$("#dataTable").joyintable({
         ajax: {
             url: 'findPage',
             type: 'POST',
             data: function() {
                return getPageParams();
             }
         },
        checkbox:false,
        tree:true,
        treeId: 'childInfo',
        treeUpId: 'parentInfo',
        treeShowName: 'portfolioName',
        lazyload:true,
        searchOnInit: false,
        isMulti: true,
        height:250,
        cols: [[
            {field:'portfolioName', title: '组合名称',width:150, rowspan: 2,sortable: false}//组合名称
            ,{field:'finprodType2Name', title: '资产类型',width:90,rowspan: 2,sortable: false}//资产类型
            ,{field:'type5Name', title: '资产二级类型', width:120,rowspan: 2,sortable: false}//资产二级类型
            ,{field:'finprodType2', visible: false,width:90, rowspan: 2}//资产类型
            ,{field:'type5',visible: false,width:100, rowspan: 2}//资产二级类型
            ,{field:'finprodMarketId', title: '资产代码', width:80,rowspan: 2,sortable: false}//资产代码
            ,{field:'finprodAbbr', title: '资产名称',width:80, rowspan: 2,sortable: false}//资产名称
            ,{field:'shareAmtStr',  title: '数量',width: 80, rowspan: 2,sortable: false,type:'number'}//数量
            ,{field:'dscCostAmtStr',title: '成本(元)',  width: 80, rowspan: 2,sortable: false,type:'number'}//成本(元)
            ,{field:'marketValueStr', title: '市值(元)',width: 80,  rowspan: 2,sortable: false,type:'number'}//市值(元)
            ,{field:'floatAmtStr', title: '盈亏(元)',width: 80,  rowspan: 2,sortable: false,type:'number'}//盈亏(元)
            ,{field:'tdyIntincexpStr',  title: '应收利息(元)', width: 80,rowspan: 2,sortable: false,type:'number'}//应收利息(元)
            ,{field:'childInfo', visible: false,rowspan: 2}
            ,{field:'parentInfo',visible: false,rowspan: 2}
            ,{title: '规模占比%', colspan: 4,align:'center',sortable: false}//市值占比%
        ],[
            {field:'tdyAmtRationStr',width:78,title: '规模占比',sortable: false}//规模占比
            ,{field:'ystAmtRationStr',width:102,title: '较上一日变化',sortable: false}//较上一日变化
            ,{field:'monthAmtRationStr',width:102,title: '较本月初变化',sortable: false}//较本月初变化
            ,{field:'yearAmtRationStr',width:102,title: '较本年初变化',sortable: false}//较本年初变化
            ,{field:'belongInstitutionNameNew',minWidth:180, width:180, title: '机构名称',visible:AUTHORITY_TYPE=='02'}
        ]],
        done:function(){
            $(".laytable-cell-group").css({'text-align':'center'})
        }
    });
    var table2=$("#trastList").joyintable({
        ajax: {
            url: 'findPageForAssetList',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getPageParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox: false,
        loadonce:true,
        page:false,
        cols:[
             {field:'finprodType2Name', width:100, title: '资产维度',sortable: false} //资产维度
            ,{field:'finprodType2', visible: false} //资产维度
            ,{field:'dscCostAmt',  width:100, title: '成本(元)',sortable: false,type:'number'}//成本(元)
            ,{field:'marketValue',  width:100, title: '市值(元)',sortable: false,type:'number'}//市值(元)
            ,{field:'floatAmt',  width:100, title: '盈亏(元)',sortable: false,type:'number'}//市值(元)
            ,{field:'tdyIntincexp',width:100, title:'应收利息(元)',sortable: false,type:'number'}//应收利息(元)
            ,{field:'amtRation',width:100, title:'规模占比(%)',sortable: false,type:'number'}//规避占比(%)
        ],
        done:function(){
            fetchData(function (data) {
                echarts.init(document.getElementById('main')).setOption({
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
                        data: ['债券','基金','计划类资产(利率型)','计划类资产(货币型)','计划类资产(净值型)','存款','项目类资产','质押式回购','拆借','买断式回购','股票','债券借贷','外汇即期','外汇远期','外汇掉期','利率互换','一级市场债','现金','期权','信用保护凭证']
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
                        data: data.data
                    }]
                })
            });
        }
    });
    function fetchData(cb){
        setTimeout(function () {
            var assetDimension = [];//资产维度
            var evasionRatio=[];//规避占比（%）
            var myChart=document.getElementById('main');
            $(".trast-list-left table tbody tr").each(function(){
                var td = $(this).find("td[data-field='finprodType2Name']");
                var td1 = $(this).find("td[data-field='amtRation']");
                assetDimension.push(td.text());
                var obj ={name:td.text(),value:td1.text()}
                evasionRatio.push(obj);
            });
            cb({
                categories: assetDimension,
                data: evasionRatio
            });
        }, 1000);
    };
    /**
     * 导出
     */
    var exportExcel = function() {
        var paramObj = getPageParams();//获取参数
        if(!paramObj.searchDate){
            Joyin.alert("请选择查询日期！",7);
            return;
        }
        Joyin.exportExcel('export', paramObj);//导出
    };
    $(function () {
        $("#PtlPositionAnalysis001_btn_search").click(function () {
            var paramObj = getPageParams();//获取参数
            if(!paramObj.searchDate){
                Joyin.alert("请选择查询日期！",7);
                return;
            }
            dataTable.load();
            table2.load();
        });
         //绑定到处按钮
        $('#PtlPositionAnalysis001_btn_export').click(function() {
            exportExcel();//导出
        }); 
        //资产类型查询条件
        /*var selcted=$('#assetType option:selected').val();
        if(selcted!=null && selcted!=undefined){
            seachFun();
        }*/
        /*function fetchData(cb){
            setTimeout(function () {
                var assetDimension = [];//资产维度
                var evasionRatio=[];//规避占比（%）
                var myChart=document.getElementById('main');
                $(".trast-list-left table tbody tr").each(function(){
                    var td = $(this).find("td[data-field='finprodType2']");
                    var td1 = $(this).find("td[data-field='amtRation']");
                    assetDimension.push(td.text());
                    var obj ={name:td.text(),value:td1.text()}
                    evasionRatio.push(obj);
                });
                cb({
                    categories: assetDimension,
                    data: evasionRatio
                });
            }, 1000);
        }
        fetchData(function (data) {
            echarts.init(document.getElementById('main')).setOption({
                title : {
                    text: '资产大规模占比',
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{b} : {d}%"
                },
                legend: {
                    x : 'center',
                    y : 'bottom',
                    data: ['F01','F02','F03','F06','F07','F08','F09','F17','F18','F25']
                },
                series: [{
                    name:'半径模式',
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
                    color: ['#7cb5ec','#6cb5ec','#434348','#43e348','#90ed7d','#90ad7d','#f7a35c','#f7aa5c','#8085e9','#80a5e9'],
                    data: data.data
                }]
            })
        });*/
    })
});
