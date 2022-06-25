$(function() {
	/**
	 * 画星星
	 */
	var star = function() {
		// 加红星星
		// 查找label
		var _findPrevLabel = function(element, level) {
			var $el = element.prev();
			if (!$el.is('label') && level > 0) {
				$el = $el.find('label');
				if ($el.length === 0) {
					return _findPrevLabel(element.parent(), level - 1);
				}
			}
			return $el;
		};

		// 非查看模式
		if (typeof pageModel !== 'undefined'
			&& (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW)) {
			$('.must').each(function() {
				$(this).remove();
			})
			$('*[required]').each(function() {
				var $el = $(this);
				var $target = _findPrevLabel($el, 3);
				if ($target.find('> .must').length === 0) {
					$target.append($('<i class="must">*</i>'));
				}
			});
		}
	}
    function saveOrSubmit(operateType) {
        var object = $.serializeToObject('#submitForm');
        object.pageModel = pageModel;
        object.operateType = operateType;
        Joyin.ajaxJson(basePath + 'invest/basicdata/mstmarketmanagement/MstMarketManagement002/check', object, function(result) {
			if(operateType === 'save') {
				WorkFlow.doSaveList(basePath + 'invest/basicdata/mstmarketmanagement/MstMarketManagement002/save', object, "MstMarketManagement002_btn_save");
			}
			else {
				WorkFlow.doSubmitList(basePath + 'invest/basicdata/mstmarketmanagement/MstMarketManagement002/save', object, "MstMarketManagement002_btn_submit");
			}
        });
    };
	$("#oCodeNameStr").change(function (){
		var data = $('#oCodeNameStr').select2('data')[0];
		if (data){
			$("#oCode").val(data.id);
			$("#oCodeName").val(data.text);
		}else {
			$("#oCode").val('');
			$("#oCodeName").val('');
		}
	});
    $("#oType").change(function (){
    	var oType = $("#oType").val();
		$("#oCodeType01").show();
		/*变动方向*/
		$('#directionChange').attr('required',true);
    	// 债项评级
    	if (oType === MAIN_GRADE_TYPE_02 && pageModel === PAGE_MODEL_NEW){
    		$("#gradeExpectation").val('').textSelect('setValue','');
			$("#directionChange").val('').textSelect('setValue','');
			/*变动方向*/
			$('#directionChange').attr('required',false);
			$("#oCodeType01").hide();
		}
		$("#oCode").val('');
		$("#oCodeName").val('');
		$("#oCodeNameStr").val('').text('');
		star();
	});

	/**
	 * 画面加载完成
	 */
	$(function() {
		if (pageModel === PAGE_MODEL_EDIT){
			$("#oType").attr("disabled",true);
			$("#oCodeNameStr").attr("disabled",true);
		}
		var oType = $("#oType").val();
		// 债项评级
		if (oType === MAIN_GRADE_TYPE_02){
			$("#oCodeType01").hide();
		}
		// 绑定按钮事件
		$('#MstMarketManagement002_btn_save').click(function() {
			saveOrSubmit('save');
		});
		$('#MstMarketManagement002_btn_submit').click(function() {
			saveOrSubmit('submit');
		});
	});
}());