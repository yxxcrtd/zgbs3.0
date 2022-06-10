$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        };
    };

    /**
     * 对正逆回购按钮进行操作
     */
    var ps = $("#psBuy").val();
	if(ps == 'psBuy'){
		$("#psSell").attr("disabled", true );
	}else if(ps == 'psSell'){
		$("#psSell").attr("checked", "checked" )
		$('#psBuy').removeAttr("checked");
		$("#psBuy").attr("disabled", true );
	}
    
	/*
	 * 若为委托带数据时
	 */
	var entrustId = $('#entrustId').val();	
	if(entrustId != '' && entrustId !=null ){
		$('#repoTerm').attr("disabled",true);
		$('#price').attr("disabled",true);
		$('#settleSpreed').attr("disabled",true);
		var repoTerm = $('#repoTerm').val();
		$('#actualDay').val(repoTerm).change();
		if(repoTerm == 1){
        	$('#termType').val("R001").change();
        }else if (repoTerm == 7){
        	$('#termType').val("R007").change();
        }else if (repoTerm == 14){
        	$('#termType').val("R014").change();
        }else if (repoTerm == 21){
        	$('#termType').val("R021").change();
        }else if (repoTerm == 30){
        	$('#termType').val("R1M").change();
        }else if (repoTerm == 60){
        	$('#termType').val("R2M").change();
        }else if (repoTerm == 90){
        	$('#termType').val("R3M").change();
        }else if (repoTerm == 120){
        	$('#termType').val("R4M").change();
        }else if (repoTerm == 180){
        	$('#termType').val("R6M").change();
        }
		/**
	     * 带委托时的绑定事件
	     */
	    $(function() {
		var init= function(){
			$('#searchDate').change();
	     // 画星星
	        star();
		}
	    //绑定回购期限数据变化事件
		$('#searchDate').change(function(){
	        var repoTerm = $('#repoTerm').val();
	        var re = /^[0-9]*$/;
	        if(re.test(repoTerm)){
	            var searchDate = $('#searchDate').val();
	            if(searchDate){
	                var d1 = new Date(searchDate).getTime();
	                var day = parseInt($('#repoTerm').val()*24*60*60*1000);
	                var mdateAdd = new Date(d1+day);
	                $('#mdateAdd').val(mdateAdd.format("yyyy-MM-dd")).change();
	            }
	        }
	    });
	});
}
	
	
    /**
     * 定义数据表格   质押券信息表格
     */
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
        /*cols:[
            {field:'quotationNum',  title: '选择'}//选择
            ,{field:'quotationState',title:'债券代码'}//债券代码
            ,{field:'tradeState',title:'债券名称'}//债券名称
            ,{field:'proName',title:'券面总额（万元）'}//券面总额（万元）
            ,{field:'tradeDirection', title:'折算比例（%）'}//折算比例（%）
        ]*/
        /**
         * 定义表头
         */
        cols: [
            // 债券代码
            {
                field: 'bondCode',
                width: 150, title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.bondCode', sortable: false
            }
            // 债券简称
            , {
                field: 'bondAbbrName',
                width: 150,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.bondAbbrName',
                sortable: false
            }
            // 可质押券面总额
            , {
                field: 'avaPledgePrin',
                width: 150,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.avaPledgePrin',
                type: 'money',
                digit: 2,
                sortable: false
            }
            // 质押券面额
            , {
                field: 'pledgePrin',
                width: 150,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.pledgePrin',
                type: 'money',
                digit: 2,
                edit: true,
                sortable: false,
                editOption:{type:'money',showField:'id',id:'pledgePrinId'}
            }
            // 首次净价
            , {
                field: 'firstNetPrice',
                width: 100,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.firstNetPrice',
                sortable: false
            }
            // 折算比例%
            , {
                field: 'pledgeRatio',
                width: 100,
                title: 'BussRepoSec.pledgeBondTotalTbl.colmodel.pledgeRatio',
                type: 'number',
                digit: 2,
                edit: true,
                sortable: false,
                editOption:{type:'number',showField:'id',id:'pledgeRatioId'}
            }
            ],
            editCell: function (value, data, field, cell, column) {
                if (field === "pledgePrin" && data.pledgePrin * 1 > data.avaPledgePrin * 1) {
                    var datas = table.getAllRows();
//                    datas[data.LAY_TABLE_INDEX].pledgePrin = tempPledgePrin;
                    table.reload({data: datas});
                    Joyin.error("质押券面额不能大于可质押券面总额");
                }else if(field === "pledgePrin" && data.pledgePrin * 1 <= data.avaPledgePrin * 1){
//                	table.reload({data: datas});
                	var datas = table.getAllRows();
                	var amount = Number(0);
                	for(var i in datas){
                		amount += datas[i].pledgePrin*datas[i].pledgeRatio/100;   //
                	}
                	getElementById("totalPrinAmt").val(amount).trigger('change').trigger('blur'); //汇总券面总额
                    getElementById("totalAmt").val(amount).trigger('change').trigger('blur'); //券面总额合计
                    var unitIntAmt = RandomNumBoth(1000,1500);
                    getElementById("tradeAmt").val(amount).trigger('change').trigger('blur');  //交易金额
                    getElementById("unitIntAmt").val(unitIntAmt).trigger('change').trigger('blur');//应计利息
                    getElementById("endDateAmt").val(amount+unitIntAmt).trigger('change').trigger('blur');//到期结算金额
                }else if (field === "pledgeRatio" && data.pledgePrin * 1 <= data.avaPledgePrin * 1){
                	var datas = table.getAllRows();
                	var amount = Number(0);
                	for(var i in datas){
                		amount += datas[i].pledgePrin*datas[i].pledgeRatio/100;   //
                	}
                	getElementById("totalPrinAmt").val(amount).trigger('change').trigger('blur'); //汇总券面总额
                    getElementById("totalAmt").val(amount).trigger('change').trigger('blur'); //券面总额合计
                    var unitIntAmt = RandomNumBoth(1000,1500);
                    getElementById("tradeAmt").val(amount).trigger('change').trigger('blur');  //交易金额
                    getElementById("unitIntAmt").val(unitIntAmt).trigger('change').trigger('blur');//应计利息
                    getElementById("endDateAmt").val(amount+unitIntAmt).trigger('change').trigger('blur');//到期结算金额
                }
            }
    });
    /**
	 * 质押券增加时间
	 */
    function getSelectData(param) {
		var obj = table.getAllRows();
		for ( var j in param) {
			var index = 0;
			for ( var i in obj) {
				if (obj[i].quotationState == param[j].quotationState) {
					index++;
				}
			}
			if (index == 0) {
				table.addRow(param[j]);
			}
		}
	};
	
	var getElementById = function (id) {
        return $("#"+ id);
    };
    
  //获取随机数两个数之间的随机小数
	function RandomNumBoth(Min,Max){
	      var Range = Max - Min;
	      var Rand = Math.random();
	      var num = Min + Math.round(Rand * Range); //四舍五入
	  //    var num1 = Math.random().toFixed(2);
	      return Number(num)
	      ;
	}
    
	/**
     * 保存按钮按下，添加子画面传回的质押券信息
     */
    var addPledgeBond = function (pledgeRows) {
        // 列表中未勾选
        if (!pledgeRows || pledgeRows.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        } else {
        	table.clear(function () {
                var totalPrinAmt = Number(0);
                for (var i in pledgeRows) {
                    table.addRow(pledgeRows[i]);
                    totalPrinAmt += pledgeRows[i].pledgePrin * pledgeRows[i].pledgeRatio /100;
                }
                getElementById("totalPrinAmt").val(totalPrinAmt).trigger('change').trigger('blur');
                getElementById("totalAmt").val(totalPrinAmt).trigger('change').trigger('blur');
                
                var tableAllRows = table.getAllRows();
                var totalAmount = Number(0);
                for(var i in tableAllRows){
                	totalAmount += tableAllRows[i].pledgePrin * tableAllRows[i].pledgeRatio /100;
                }
                var unitIntAmt = RandomNumBoth(1000,1500);
                getElementById("tradeAmt").val(totalAmount).trigger('change').trigger('blur');  //交易金额
                getElementById("unitIntAmt").val(unitIntAmt).trigger('change').trigger('blur');//应计利息
                getElementById("endDateAmt").val(totalAmount+unitIntAmt).trigger('change').trigger('blur');//到期结算金额
                getElementById("ccy").val("人民币(CNY)").trigger('change').trigger('blur');//结算币种
            });
        }
    };
    
    
    
    /**
     * 绑定事件
     */
    $(function() {
	var init= function(){
        $('#repoTerm').change();
        $('#price').change();
     // 画星星
        star();
	}
    //绑定回购期限数据变化事件
	$('#repoTerm').change(function(){
        var repoTerm = $('#repoTerm').val();
        var re = /^[0-9]*$/;
        if(re.test(repoTerm)){
            var searchDate = $('#searchDate').val();
            if(searchDate){
                var d1 = new Date(searchDate).getTime();
                var day = parseInt($('#repoTerm').val()*24*60*60*1000);
                var mdateAdd = new Date(d1+day);
                $('#vdateAdd').val(searchDate).change();
                $('#actualDay').val(repoTerm).change();
                $('#mdateAdd').val(mdateAdd.format("yyyy-MM-dd")).change();
            }
            if(repoTerm == 1){
            	$('#termType').val("R001").change();
            }else if (repoTerm == 7){
            	$('#termType').val("R007").change();
            }else if (repoTerm == 14){
            	$('#termType').val("R014").change();
            }else if (repoTerm == 21){
            	$('#termType').val("R021").change();
            }else if (repoTerm == 30){
            	$('#termType').val("R1M").change();
            }else if (repoTerm == 60){
            	$('#termType').val("R2M").change();
            }else if (repoTerm == 90){
            	$('#termType').val("R3M").change();
            }else if (repoTerm == 120){
            	$('#termType').val("R4M").change();
            }else if (repoTerm == 180){
            	$('#termType').val("R6M").change();
            }
        }
    });
	
	$('#price').change(function(){
		var unitIntAmt = $('#unitIntAmt').val();
		if(unitIntAmt !='' && unitIntAmt != null){
			var unitIntAmt = RandomNumBoth(1000,1500);
			getElementById("unitIntAmt").val(unitIntAmt).trigger('change').trigger('blur');//应计利息
		}
	});
	
	$('#searchDate').change(function(){
		var searchDate = $('#searchDate').val();
		if(searchDate !='' && searchDate != null){
			getElementById("vdateAdd").val(searchDate).change();//首次结算日
		}
	});
	
	$('#repoTerm').change(function(){
        var repoTerm = $('#repoTerm').val();
        var re = /^[0-9]*$/;
        if(re.test(repoTerm)){
            var vdateAdd = $('#vdateAdd').val();
            if(vdateAdd){
                var d1 = new Date(vdateAdd).getTime();
                var day = parseInt($('#repoTerm').val()*24*60*60*1000);
                var mdateAdd = new Date(d1+day);
                $('#mdateAdd').val(mdateAdd.format("yyyy-MM-dd")).change();
            }
        }
    });
	
	
	
    });
    
    
    /**
     * 画面加载完成
     */
    $(function() {
        //添加质押券
        $('#UpTrInqBuyBack002_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("质押券添加"),
                basePath + "invest/trade/uptrinqbuyback/UpTrInqBuyBack005/init",
                "98%", "98%",function(result) {
					var param = result;
					getSelectData(top.getSelectData);
				});
            
        });
        
        //绑定提交按钮
    	$("#UpTrInqSec001_btn_submit").click(function(){
    		Joyin.error("暂未开放上行接口");
    		return;
    	})
   
        
    });
    // 组合现金、持仓查询业务共通公开接口
    if (window.BussRepoSec == undefined) {
        window.BussRepoSec = {};
    }
    var xxt = 'bussRepoSec';
    window.BussRepoSec[xxt] = {
            addPledgeBond: addPledgeBond
        };
}());