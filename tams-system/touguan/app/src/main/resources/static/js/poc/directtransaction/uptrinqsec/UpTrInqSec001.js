$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        };
    };

    /**
     * 定义数据表格
     */
    var table2=$("#dataTable2").joyintable({
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
        cols:[
            {field:'quotationNum',  title: '报价编号',width:100}//报价编号
            ,{field:'quotationState',title:'报价状态',width:100}//报价状态
            ,{field:'tradeState',title:'交易状态',width:100}//交易状态
            ,{field:'proName',title:'产品名称',width:100}//产品名称
            ,{field:'bondNum', title:'债券编号',width:100}//债券编号
            ,{field:'tradeDirection', title:'本方交易方向',width:120}//本方交易方向
            ,{field:'partyRole', title:'本方角色',width:100}//本方角色
            ,{field:'tradeCounter', title:'交易对手',width:100}//交易对手
            ,{field:'rivalTraders', title:'对手方交易员',width:120}//对手方交易员
            ,{field:'ourTrader', title:'本方交易员',width:100}//本方交易员
            ,{field:'businessTime', title:'业务发生时间',width:120}//业务发生时间
            ,{field:'netPrice', title:'净价（元）',width:100}//净价（元）
            ,{field:'yields', title:'到期收益率（%）',width:150}//到期收益率（%）
            ,{field:'totalArea', title:'券面总额',width:100}//券面总额
            ,{field:'enquiryTimes', title:'询价次数',width:100}//询价次数
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit']}//编辑和删除事件
        ]
    });
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
        searchOnInit: true,
//        checkbox: false,
        page: false,
        cols:[
            {field:'quotationNum',  title: '报价编号',width:100}//报价编号
            ,{field:'quotationState',title:'报价状态',width:100}//报价状态
            ,{field:'tradeState',title:'交易状态',width:100}//交易状态
            ,{field:'proName',title:'组合名称',width:100}//组合名称
  //          ,{field:'bondNum', title:'债券编号',width:100}//债券编号
            ,{field:'bondNum', title:'资产代码',width:100}//资产代码
            ,{field:'tradeDirection', title:'本方交易方向',width:120}//本方交易方向
            ,{field:'partyRole', title:'本方角色',width:100}//本方角色
            ,{field:'tradeCounter', title:'交易对手',width:100}//交易对手
            ,{field:'rivalTraders', title:'对手方交易员',width:120}//对手方交易员
            ,{field:'ourTrader', title:'本方交易员',width:100}//本方交易员
            ,{field:'businessTime', title:'业务发生时间',width:120}//业务发生时间
            ,{field:'netPrice', title:'净价（元）',width:100}//净价（元）
            ,{field:'yields', title:'到期收益率（%）',width:150}//到期收益率（%）
            ,{field:'totalArea', title:'券面总额',width:100}//券面总额
          //  ,{field:'enquiryTimes', title:'询价次数',width:100}//询价次数
       //     ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit']}//编辑和删除事件
        ],
        data:[
            /*{
                "quotationNum":"2018082015",
                "quotationState":"成功",
                "tradeState":"新增报价",
                "proName":"日薪月益2018001",
                "bondNum":"190201",
                "tradeDirection":"买入",
                "partyRole":"询价方",
                "tradeCounter":"平安银行合肥分行",
                "rivalTraders":"张宁",
                "ourTrader":"薛浩然",
                "businessTime":"2019/11/2",
                "netPrice":"99.5",
                "yields":"2.57",
                "totalArea":"1000000",
                "enquiryTimes":"1"
            },
            {
                "quotationNum":"2018082016",
                "quotationState":"成功",
                "tradeState":"已回复",
                "proName":"日薪月益2018001",
                "bondNum":"111914020",
                "tradeDirection":"卖出",
                "partyRole":"确认方",
                "tradeCounter":"工银瑞信基金管理有限公司",
                "rivalTraders":"王致",
                "ourTrader":"薛浩然",
                "businessTime":"2019/11/2",
                "netPrice":"99.7",
                "yields":"2.75",
                "totalArea":"2000000",
                "enquiryTimes":"5"
            }*/
        ],
        click : function(table, allData){
        	if (allData.quotationNum === '2018082015'){
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
        checkbox:false,
        page:false,
        cols:[
            {field:'num',title:'序号'}//序号
            ,{field:'quotationNum',title:'报价编号'}//报价编号
            ,{field:'businessTime',title:'业务发生时间'}//业务发生时间
            ,{field:'tradeDirection', title:'本方交易方向'}//本方交易方向
            ,{field:'tradeCounter', title:'交易对手'}//交易对手
            ,{field:'rivalTraders', title:'对手方交易员'}//对手方交易员
            ,{field:'manetPricerk', title:'净价（元）'}//净价（元）
            ,{field:'yields', title:'到期收益率（%）'}//到期收益率（%）
            ,{field:'totalArea', title:'券面总额'}//券面总额
            ,{field:'mark', title:'备注'}//备注
        ],
        data:[
            /*{
                "num":"1",
                "quotationNum":"20182015",
                "businessTime":"15：20",
                "tradeDirection":"买入",
                "tradeCounter":"农业银行",
                "rivalTraders":"首席",
                "manetPricerk":"96.0000",
                "yields":"8.490698",
                "totalArea":"100000",
                "mark":"本方发起新增报价"
            },
            {
                "num":"2",
                "quotationNum":"20182015",
                "businessTime":"15：30",
                "tradeDirection":"",
                "tradeCounter":"农业银行",
                "rivalTraders":"首席",
                "manetPricerk":"97.0000",
                "yields":"6.097889",
                "totalArea":"100000",
                "mark":"对手方修改报价"
            },
            {
                "num":"3",
                "quotationNum":"20182015",
                "businessTime":"15：35",
                "tradeDirection":"",
                "tradeCounter":"农业银行",
                "rivalTraders":"首席",
                "manetPricerk":"96.5000",
                "yields":"7.454812",
                "totalArea":"100000",
                "mark":"对手方修改报价"
            },
            {
                "num":"4",
                "quotationNum":"20182015",
                "businessTime":"15：45",
                "tradeDirection":"买入",
                "tradeCounter":"农业银行",
                "rivalTraders":"首席",
                "manetPricerk":"96.5000",
                "yields":"7.454812",
                "totalArea":"100000",
                "mark":"对手方确认成交"
            }*/
        ]
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.dialog(JoyinUtil.getLabel("准入池新建"),
            basePath + "risk/admission/admissionassetpool/AdmissionAssetPool003/init",//打开新界面
            "75%", "45%",function(){
                table.reload();//回调函数，表格刷新
            });
    });

    var sourceData = [
                      {
                    	  "quotationNum":"2018082015",
                          "quotationState":"成功",
                          "tradeState":"已成交",
                          "proName":"日薪月益2018001",
                          "bondNum":"190201",
                          "tradeDirection":"买入",
                          "partyRole":"询价方",
                          "tradeCounter":"平安银行合肥分行",
                          "rivalTraders":"张宁",
                          "ourTrader":"薛浩然",
                          "businessTime":"2019-11-02",
                          "netPrice":"99.5",
                          "yields":"2.57",
                          "totalArea":"1000000"
                      },
                      {
                    	  "quotationNum":"2018082016",
                          "quotationState":"成功",
                          "tradeState":"已回复",
                          "proName":"日薪月益2018001",
                          "bondNum":"111914020",
                          "tradeDirection":"卖出",
                          "partyRole":"确认方",
                          "tradeCounter":"工银瑞信基金管理有限公司",
                          "rivalTraders":"王致",
                          "ourTrader":"薛浩然",
                          "businessTime":"2019-11-02",
                          "netPrice":"99.7",
                          "yields":"2.75",
                          "totalArea":"2000000"
                      }
                  ];
    
    var sourceData1 = [
						{
						    "num":"1",
						    "quotationNum":"20182015",
						    "businessTime":"14：20",
						    "tradeDirection":"买入",
						    "tradeCounter":"平安银行合肥分行",
						    "rivalTraders":"张宁",
						    "manetPricerk":"99.5",
						    "yields":"2.57",
						    "totalArea":"1000000",
						    "mark":"本方发起新增报价"
						},
						{
						    "num":"2",
						    "quotationNum":"20182015",
						    "businessTime":"14：30",
						    "tradeDirection":"",
						    "tradeCounter":"平安银行合肥分行",
						    "rivalTraders":"张宁",
						    "manetPricerk":"99.8",
						    "yields":"2.45",
						    "totalArea":"1000000",
						    "mark":"对手方修改报价"
						},
						{
						    "num":"3",
						    "quotationNum":"20182015",
						    "businessTime":"14：35",
						    "tradeDirection":"",
						    "tradeCounter":"平安银行合肥分行",
						    "rivalTraders":"张宁",
						    "manetPricerk":"99.7",
						    "yields":"2.52",
						    "totalArea":"1000000",
						    "mark":"对手方修改报价"
						},
						{
						    "num":"4",
						    "quotationNum":"20182015",
						    "businessTime":"14：45",
						    "tradeDirection":"买入",
						    "tradeCounter":"平安银行合肥分行",
						    "rivalTraders":"张宁",
						    "manetPricerk":"99.6",
						    "yields":"2.55",
						    "totalArea":"1000000",
						    "mark":"对手方确认成交"
						}
                       ];
    /**
     * 画面加载完成
     */
    $(function() {
        //点击查询按钮
        $("#UpTrInqSec001_btn_search").click(function () {
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
            var bol4 = 'JYDS000000010002'==$('#tradeCounter').val()||''== $('#tradeCounter').val();
            var bol5 = 'JYDS000000010020'==$('#tradeCounter').val()||''== $('#tradeCounter').val();
            	//选择了时间且时间区间正确或不选时间区间的
            	if(((begin<= date && date<=end)&&(a !='' && b !='')) || (a =='' && b =='')){
                	if(('01'==$('#tradeDirection').val()||''== $('#tradeDirection').val()) && ('01'==$('#quotationNum').val()||''==$('#quotationNum').val() )&&('01' == $('#partyRole').val()||'' == $('#partyRole').val())&&('01' == $('#tradeState').val()||'' == $('#tradeState').val() )&&('01'==$('#quotationState').val()||''==$('#quotationState').val())&&bol&&bol2&&bol4){
                    	table.clear();
                    	table.addRow(sourceData[0]);
                    }else if(('02'==$('#tradeDirection').val()||''== $('#tradeDirection').val()) && ('02'==$('#quotationNum').val()||''==$('#quotationNum').val() )&&('02' == $('#partyRole').val()||'' == $('#partyRole').val())&&('02' == $('#tradeState').val()||'' == $('#tradeState').val() )&&('01'==$('#quotationState').val()||''==$('#quotationState').val())&&bol&&bol3&&bol5){
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
            		if(''== $('#tradeDirection').val() && ''==$('#quotationNum').val()&&'' == $('#partyRole').val()&&'' == $('#tradeState').val()&&''==$('#quotationState').val()&&bol&&''== $('#finprodMarketId').val()&&''== $('#tradeCounter').val()){
		            	table.clear();
		            	table.addRow(sourceData[0]);
		            	table.addRow(sourceData[1]);
                		}
            	}
        });
        //新增报价事件
        $('#UpTrInqSec001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("新增报价"),
                basePath + "invest/trade/uptrinqsec/UpTrInqSec002/init",
                "98%", "98%");
        });
        //修改报价事件
        $("#UpTrInqSec001_btn_modify").click(function () {
        	var rows = table.getAllCheckedRows(); 
        	if(rows.length != 1 ){
                layer.msg("请选择一条记录");
                return;
            }
        	if(rows[0].quotationNum === "2018082015"){
        		//
        		Joyin.dialog(JoyinUtil.getLabel("修改报价"),
                        basePath + "invest/trade/uptrinqsec/UpTrInqSec003/init",
                        "98%", "98%");
        	}else if(rows[0].quotationNum == "2018082016"){
        		//
        		Joyin.dialog(JoyinUtil.getLabel("修改报价"),
                        basePath + "invest/trade/uptrinqsec/UpTrInqSec006/init",
                        "98%", "98%");
        	}
        		
            
        });
        //回复报价事件
        $("#UpTrInqSec001_btn_reply").click(function () {
        	var rows = table.getAllCheckedRows(); 
        	if(rows.length != 1 ){
                layer.msg("请选择一条记录");
                return;
            }
        	if(rows[0].quotationNum === "2018082015"){
        		//
        		layer.msg("报价已成交,请选择其他记录");
        		return;
        	}else if(rows[0].quotationNum === "2018082016"){
        		//
        		Joyin.dialog(JoyinUtil.getLabel("回复报价"),
                        basePath + "invest/trade/uptrinqsec/UpTrInqSec004/init",
                        "98%", "98%", rows[0]);
        	}
           /* Joyin.dialog(JoyinUtil.getLabel("回复报价"),
                basePath + "trade/uptrinqsec/UpTrInqSec004/init",
                "98%", "98%");*/
        });
        //撤回报价事件
        $("#UpTrInqSec001_btn_revoke").click(function () {
        	var rows = table.getAllCheckedRows(); 
        	if(rows.length == 0 ){
        		layer.msg("请选择需要撤回的报价记录");
                return;
            }
        	if(rows.length > 0){
        		Joyin.error("接口未连接，报价撤回失败");
        		return;
        	}
            Joyin.dialog(JoyinUtil.getLabel(""),
                basePath + "invest/trade/uptrinqsec/UpTrInqSec005/init",
                "30%", "30%");
        })
    });
}());