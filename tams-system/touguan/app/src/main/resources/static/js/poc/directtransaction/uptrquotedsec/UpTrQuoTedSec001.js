$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        };
    };

    var table=$("#dataTable").joyintable({
        /*ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },*/
        /**
         * 定义表头
         */
    	checkbox: true,
        searchOnInit: true,
        cols:[
            {field:'quotationNum',  title: '报价编号'}//报价编号
            ,{field:'quotationState',title:'报价状态'}//报价状态
            ,{field:'tradeState',title:'交易状态'}//交易状态
            ,{field:'proName',title:'组合名称'}//产品名称
//            ,{field:'bondNum',title:'债券编号'}//债券编号
            ,{field:'bondNum',title:'资产代码'}//资产代码
            ,{field:'tradeDirection', title:'本方交易方向'}//本方交易方向
            ,{field:'totalArea', title:'券面总额（元）'}//券面总额（元）
            ,{field:'businessTime', title:'业务发生时间'}//业务发生时间
//            ,{field:'priceLimitRequest', title:'限价请求'}//限价请求
//            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit']}//编辑和删除事件
        ],
        data:[
            /*{
                "quotationNum":"2018082020",
                "quotationState":"成功",
                "tradeState":"新增报价",
                "proName":"日薪月益2018001",
                "bondNum":"190201",
                "tradeDirection":"买入",
                "ourTrader":"1000000",
                "businessTime":"2019-11-02"
//                "priceLimitRequest":"停用"
            },
            {
                "quotationNum":"2018082021",
                "quotationState":"成功",
                "tradeState":"已回复",
                "proName":"日薪月益2018001",
                "bondNum":"111914020",
                "tradeDirection":"卖出",
                "ourTrader":"2000000",
                "businessTime":"2019-11-02"
//                "priceLimitRequest":"启用"
            }*/
        ],
        click : function(table, allData){
        	if (allData.quotationNum === '2018082020'){
        		table1.clear();
            	table1.addRow(sourceData1[0]);
            	table1.addRow(sourceData1[1]);
            	table1.addRow(sourceData1[2]);
            	table1.addRow(sourceData1[3]);
        	}else{
        		table1.clear();
        	}
        }
    });
    var table1=$("#dataTable1").joyintable({
        /*ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },*/
        /**
         * 定义表头
         */
        searchOnInit: true,
        checkbox:true,
        page:false,
        cols:[
            {field:'num',title:'编号'}//序号
            ,{field:'quotationNum',title:'报价编号'}//报价编号
            ,{field:'businessTime',title:'业务发生时间'}//业务发生时间
            ,{field:'tradeDirection', title:'本方交易方向'}//本方交易方向
            ,{field:'netPrice', title:'净价（元）'}//券面总额（元）
            ,{field:'ourTrader', title:'到期收益率（%）'}//到期收益率
            ,{field:'totalArea', title:'券面总额'}//券面总额（元）
            ,{field:'mark', title:'备注'},//备注
            {field:'operation', title: 'SysCommon.operation', type:'action', buttons: [{
                    text: '处理',
                    clas: 'deal',
                    title: '处理'
                }]}
            /*,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit']}//编辑和删除事件
*/        ],
        data:[]
    });
    
    sourceData = [
                  {
                "quotationNum":"2018082020",
                "quotationState":"成功",
                "tradeState":"新增报价",
                "proName":"日薪月益2018001",
                "bondNum":"190201",
                "tradeDirection":"买入",
                "totalArea":"1000000",
                "businessTime":"2019-11-02"
            },
            {
                "quotationNum":"2018082021",
                "quotationState":"成功",
                "tradeState":"已成交",
                "proName":"日薪月益2018001",
                "bondNum":"111914020",
                "tradeDirection":"卖出",
                "totalArea":"2000000",
                "businessTime":"2019-11-02"
            }];
    sourceData1 = [
					{
					    "num":"1",
					    "quotationNum":"2018082020",
					    "businessTime":"14：20",
					    "tradeDirection":"买入",
					    "netPrice":"99.2",
					    "ourTrader":"2.69",
					    "yields":"800000",
					    "mark":"请求回复"
					},
					{
					    "num":"2",
					    "quotationNum":"2018082020",
					    "businessTime":"14：30",
					    "tradeDirection":"买入",
					    "netPrice":"99.4",
					    "ourTrader":"2.62",
					    "yields":"500000",
					    "mark":"请求回复"
					},
					{
					    "num":"3",
					    "quotationNum":"2018082020",
					    "businessTime":"14:35",
					    "tradeDirection":"买入",
					    "netPrice":"99.5",
					    "ourTrader":"2.57",
					    "yields":"800000",
					    "mark":"请求回复 "
					},
					{
					    "num":"4",
					    "quotationNum":"2018082020",
					    "businessTime":"14:45",
					    "tradeDirection":"买入",
					    "netPrice":"99.6",
					    "ourTrader":"2.55",
					    "yields":"700000",
					    "mark":"请求回复 "
					}
                   ];
    /**
     * 绑定编辑事件
     */
    table1.bind('click', '.deal', function(data, index) {
    	var rows = table.getAllCheckedRows(); 
    	Joyin.dialog(JoyinUtil.getLabel("成交报价"),
                basePath + "invest/trade/uptrquotedsec/UpTrQuoTedSec003/init",
                "98%", "98%",rows[0]);
    });


    /**
     * 画面加载完成
     */
    $(function() {
        //点击查询按钮
        $("#UpTrQuoTedSec001_btn_search").click(function () {
            $(".pool-table1").css("display","none");
            $(".pool-table").css("display","block");
            
            var allRows = table.getAllRows();
            //对业务发生时间进行判断
            var date = new Date().format("2019-11-02","yyyy-MM-dd");
            var a = $('#searchDate').startDate();
            var begin = new Date().format(a,"yyyy-MM-dd");
            var b = $('#searchDate').endDate();
            var end = new Date().format(b,"yyyy-MM-dd");
            var bol = 'ABCDE180087'==$('#portfolioId').val()||''== $('#portfolioId').val();
            var bol2 = '190201'==$('#finprodMarketId').val()||''== $('#finprodMarketId').val();
            var bol3 = '111914020'==$('#finprodMarketId').val()||''== $('#finprodMarketId').val();
            	//选择了时间且时间区间正确或不选时间区间的
            	if(((begin<= date && date<=end)&&(a !='' && b !='')) || (a =='' && b =='')){
                	if(('01'==$('#tradeDirection').val()||''== $('#tradeDirection').val()) && ('01'==$('#quotationNum').val()||''==$('#quotationNum').val() )&&('01' == $('#tradeState').val()||'' == $('#tradeState').val() )&&('01'==$('#quotationState').val()||''==$('#quotationState').val())&&bol&&bol2){
                    	table.clear();
                    	table.addRow(sourceData[0]);
                    }else if(('02'==$('#tradeDirection').val()||''== $('#tradeDirection').val()) && ('02'==$('#quotationNum').val()||''==$('#quotationNum').val() )&&('02' == $('#tradeState').val()||'' == $('#tradeState').val() )&&('01'==$('#quotationState').val()||''==$('#quotationState').val())&&bol&&bol3){
                    	table.clear();
                    	table.addRow(sourceData[1]);
                    }else {
                    	table.clear();
                    }
                }

            //选择时间区间，但是时间不在区间内
            if((a !='' && b !='') && (begin> date || date>end)){
            	table.clear();		
            }
            
            //不选时间区间的
            	if(((begin<= date && date<=end) &&( a !='' && b !='')) || (a =='' && b =='')){
            		if(''== $('#tradeDirection').val() && ''==$('#quotationNum').val()&&'' == $('#tradeState').val()&&''==$('#quotationState').val()&&bol&&''== $('#finprodMarketId').val()){
		            	table.clear();
		            	table.addRow(sourceData[0]);
		            	table.addRow(sourceData[1]);
                		}
            	}
        });
        //新增报价事件
        $('#UpTrQuoTedSec001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("新增报价"),
                basePath + "invest/trade/uptrquotedsec/UpTrQuoTedSec002/init",
                "98%", "98%");
        });
        //成交报价事件
        $("#UpTrQuoTedSec001_btn_reply").click(function () {
        	//只有新增报价才可以成交报价
        	var rows = table.getAllCheckedRows(); 
        	var rows1 = table1.getAllCheckedRows(); 
        	if(rows.length != 1 && rows1.length === 0 ){
                layer.msg("请选择一条记录");
                return;
            }
        	
        	if(rows.length !='' && rows.length !=null ){
        		if(rows[0].quotationNum === "2018082021"){
            		layer.msg("报价已成交,请选择其他记录");
            		return;
            	}else if (rows[0].quotationNum === "2018082020"){
            		Joyin.dialog(JoyinUtil.getLabel("成交报价"),
                            basePath + "invest/trade/uptrquotedsec/UpTrQuoTedSec003/init",
                            "98%", "98%",rows[0]);
            	}
        	}else if(rows1.length !='' && rows1.length !=null){
        		 if(rows1[0].quotationNum === '2018082020'){
            		Joyin.dialog(JoyinUtil.getLabel("成交报价"),
                            basePath + "invest/trade/uptrquotedsec/UpTrQuoTedSec003/init",
                            "98%", "98%",rows[0]);
            	}
        	}
        	
        });
        //撤回报价事件
        $("#UpTrQuoTedSec001_btn_revoke").click(function () {
        	var rows = table.getAllCheckedRows(); 
        	if(rows.length == 0 ){
        		layer.msg("请选择需要撤回的报价记录");
                return;
            }
        	if(rows.length > 0){
        		Joyin.error("接口未连接，报价撤回失败");
        		return;
        	}
        	
            Joyin.dialog(JoyinUtil.getLabel("回复报价"),
                basePath + "invest/trade/uptrquotedsec/UpTrQuoTedSec004/init",
                "98%", "98%");
        })
    });
}());