$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var object = $.serializeToObject('#searchForm');
    	var val = '';
    	if($('#finprodId').val() && $('#finprodId').val() != ''){
    		val = $('#finprodId').select2('data')[0].object ;
    	}
        var secManageAcctId = object.secManageAcctId;
        if(null != secManageAcctId && "" != secManageAcctId){
            secManageAcctId=object.secManageAcctId.join(',');
        }
        var portfolioId = object.portfolioId;
        // add by swwan 多组合查询 20210719 start
        if(null != portfolioId && "" != portfolioId){
            portfolioId=object.portfolioId.join(',');
        }
        // add by swwan 多组合查询 20210719 start
        return {
        	portfolioId : portfolioId,//组合代码
        	cdate : $('#cdate').val(),//日期
        	secManageAcctId: secManageAcctId,//证券账户
        	type8:$('#type8').val(),//资产类型
        	finprodId:val//资产代码
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
        /**
         * 定义表格名称
         */
        cols:[
             {field:'portfolioName', title: 'ActualTimeAsset001.portfolioName', width : 200}//组合名称
            ,{field:'secAcctInfo', title: 'ActualTimeAsset001.secAcctInfo', sortField:'secAcctId',width : 220}//证券账户
            ,{field:'type8Name', title: 'ActualTimeAsset001.type8',sortField:'type8', width : 150}//资产类型
            ,{field:'type9Str', title: 'ActualTimeAsset001.type7',sortField:'type9', width : 150}//资产二级类型
            ,{field:'finprodMarketId', title: 'ActualTimeAsset001.finprodMarketId', width : 150}//资产编码
            ,{field:'finprodAbbr', title: 'ActualTimeAsset001.finprodAbbr', width : 150}//资产名称
            ,{field:'actualPositionAmount', title: 'ActualTimeAsset001.actualPositionAmount',type:'number', width : 200}//实际持仓
            ,{field:'instructionsAmount', title: 'ActualTimeAsset001.instructionsAmount',type:'number', width : 200}//指令可用持仓
            ,{field:'tradePositionAmount', title: 'ActualTimeAsset001.tradePositionAmount',type:'number', width : 200}//交易可用持仓
            /* modify by jiangpeng on 20210416 start ZGBSSE-226 【行权债升级】通道债券行权赎回生效及流程中，交易可用头寸、实际头寸未扣减。*/
            ,{field:'pledgeAmount', title: 'ActualTimeAsset001.pledgeAmount',type:'number', width : 200}//质押持仓
            ,{field:'preReductionAmount', title: 'ActualTimeAsset001.preReductionAmount',type:'number', width : 200,visible:false}
            //modify by jiangpeng on 20210623 ZGBSSE-680 隐藏预减持仓
            /* modify by jiangpeng on 20210416 end ZGBSSE-226 【行权债升级】通道债券行权赎回生效及流程中，交易可用头寸、实际头寸未扣减。*/
            ,{field:'marketValue', title: 'ActualTimeAsset001.marketValue',type:'number', width : 200}//市值

        ]
    });
    
    //联动：组合代码联动效果
    var portfolioIdLinkShow = function() {
    	$('#secManageAcctId').val('').trigger('change');
    }
    
    //资产类型联动资产代码
    var type8LinkShow = function() {
    	$('#finprodId').val('').trigger('change');
    }
    
    /**
     * 导出
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();//获取参数
        Joyin.exportExcel('export', paramObj);//导出
    };
    /**
     * 画面加载完成
     */
    $(function() {
        // 绑定查询按钮
        $('#ActualTimeAsset001_btn_search').click(function() {
        	var strError = '';
            // if(!$('#portfolioId').val()){
            //     strError = "请输入"+JoyinUtil.getLabel("ActualTimeAsset001.portfolioId")+"<br>";
            // }
            if(!$('#cdate').val()){
                strError = strError + "请输入"+JoyinUtil.getLabel("ActualTimeAsset001.cdate");
            }
            else {
                if ($('#cdate').val() > sysDate) {
                    strError = strError + '日期必须小于等于平台日';
                }
            }
            if(strError != '' && strError != null){
                Joyin.error(strError);
                return;
            }
            table.load();//加载表格
        });
        //绑定到处按钮
        $('#ActualTimeAsset001_btn_export').click(function() {
            exportExcel();//导出
        });
        
        //联动：组合代码联动效果
        $("#portfolioId").change(function(){
        	portfolioIdLinkShow();
        });
        
        //资产类型联动资产代码
        $("#type8").change(function(){
        	type8LinkShow();
        });
        
        
    });
}());