$(function() {
	
	/*
	 * 对买入卖出方向进行处理
	 */
	var ps = $("#psBuy").val();
	if(ps == 'psBuy'){
		$("#psSell").attr("disabled", true );
	}else if(ps == 'psSell'){
		$("#psSell").attr("checked", "checked" )
		$('#psBuy').removeAttr("checked");
		$("#psBuy").attr("disabled", true );
	}
	
	/**
     * 绑定事件
     */
    $(function() {
	var init= function(){
        $('#finprodId').change();
        $('#price').change();
     // 画星星
        star();
	}
        
		//获取随机数两个数之间的随机小数
		function RandomNumBoth(Min,Max){
		      var Range = Max - Min;
		      var Rand = Math.random();
		      var num = Min + Math.round(Rand * Range); //四舍五入
		      var num1 = Math.random().toFixed(2);
		      return Number(num)+Number(num1)
		      ;
		}
	
        /**
         * 资产代码联动资产名称
         */
        $('#finprodId').change(function() {
            var finprodId = $('#finprodId').val();
            var data = $('#finprodId').select2("data")[0].object;
            $('#finprodName').val(data);
    });
        
        /**
         * 净价联动到期收益率
         */
        $('#price').change(function() {
        	var entrustNum = $('#entrustNum').val();
        	if(entrustNum != '' && entrustNum != null){
        		
        		var matureYield = RandomNumBoth(4,8);  //到期收益率
            	$('#matureYield').val(matureYield);
            	var unitIntAmt = RandomNumBoth(2.5,3.5);  //应计利息
            	$('#unitIntAmt').val(unitIntAmt);
            	var price = $('#price').val();
            	$('#fullPrice').val(Number(price)+Number(unitIntAmt));  //全价
        		$('#tradeAmt').val(Number(entrustNum)*((Number(price)+Number(unitIntAmt))/100).toFixed(2));  //交易金额
        		$('#intAmt').val(((Number(entrustNum)*Number(unitIntAmt))/100).toFixed(2));   //应计利息总额
        		$('#settleAmt').val(Number(entrustNum)*((Number(price)+Number(unitIntAmt))/100).toFixed(2));
        	}else{
        		Joyin.error("请输入券面总额");
        		return;
        	}
    });
        
  });
	
	
 
    /**
     * 画面加载完成
     */
    $(function() {
    	$("#UpTrInqSec001_btn_submit").click(function(){
    		Joyin.error("暂未开放上行接口");
    		return;
    	})
    });
    
    
    
    
}());