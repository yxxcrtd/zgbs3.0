$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
            portfolioId:$('#portfolioId').val()?$('#portfolioId').val().join(','):null,//组合代码
            cdate : $('#cdate').val(),
            profitType : $('#profitType').val(),
            prodManager:$('#prodManager').val()?$('#prodManager').val().join(','):null,
            showDimension:$('#showDimension').val(),
            index:'0'
        }
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({ 
        ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        tree:true,
        checkbox:false,
        // lazyloadWithWhere:true,
        loadonce:true,
        searchOnInit: false,
        page:false,
        treeId:'portfolioId',//树形id字段名称
        treeUpId:'upperPortfolioCode',//树形父id字段名称
        treeShowName:'portfolioId',//以树形式显示的字段
        /**
         * 定义表格名称
         */
        cols:[
            {field:'portfolioId',  title: 'PtlFutureCashPosi001.datePortfolioId', width:260}
            ,{field:'usableAmt',  title: 'PtlFutureCashPosi001.usableAmt', width:200,type:'money'}
            ,{field:'prdEndAmt',  title: 'PtlFutureCashPosi001.prdEndAmt', width:200,type:'money'}
            ,{field:'assetPeriodInAmt',  title: 'PtlFutureCashPosi001.assetPeriodInAmt', width:240,type:'money'}
            ,{field:'assetPeriodOutAmt',  title: 'PtlFutureCashPosi001.assetPeriodOutAmt', width:240,type:'money'}
            ,{field:'cashGapAmt',  title: 'PtlFutureCashPosi001.cashGapAmt', width:200,type:'money'}
            ,{field:'mDateStr',  title: 'PtlFutureCashPosi001.mDate', width:150,sortField: 'mdate'}
        ],
        done: function() {

        }
    });
    
    /**
     * 导出
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();//获取参数
        Joyin.ajaxJson(basePath + 'invest/portfolio/ptlfuturecashposi/PtlFutureCashPosi001/check', paramObj, function(result) {
            Joyin.exportExcel('export', paramObj);//导出
    	});
    };
    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定查询按钮
        $('#PtlFutureCashPosi001_btn_search').click(function() {
            var paramObj = getSearchParams();//获取参数
            Joyin.ajaxJson(basePath + 'invest/portfolio/ptlfuturecashposi/PtlFutureCashPosi001/check', paramObj, function(result) {
            	if(table.table){
                    table.reload()
                }
                else{
                   table.load();
                }
        	});
        });
        //绑定导出按钮
        $('#PtlFutureCashPosi001_btn_export').click(function() {
            exportExcel();//导出
        });
        // 展示维度联动
        $('#showDimension').change(function() {
        	if($('#showDimension').val() == showDimensionShort){
        		$('#cdate').val('');
        		$('#cdate').prop("disabled", true);
        	} else {
        		$('#cdate').prop("disabled", false);
        	}
        });
    });
}());