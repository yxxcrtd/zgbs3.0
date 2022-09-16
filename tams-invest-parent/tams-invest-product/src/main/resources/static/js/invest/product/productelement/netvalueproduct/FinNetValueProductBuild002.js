(function(){
	/**
	 * 画星星
	 */
	function star() {
		// 加红星星
		// 查找label
		var _findPrevLabel = function (element, level) {
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
			$('.must').each(function () {
				$(this).remove();
			});
			$('*[required]').each(function () {
				var $el = $(this);
				var $target = _findPrevLabel($el, 3);
				if ($target.find('> .must').length === 0) {
					$target.append($('<i class="must">*</i>'));
				}
			});
		}
	}
	/**
	 * 初始化方法
	 */
	var finProductBuildInit = function(){
        //初始化-是否现金管理类产品和是否设置最短持有期限 ，开放式是必输，封闭式置灰， FinProductBuild002_operationType
		var operationType = FinProductCommon.getElementById('operationType').val();
        if(operationType == ISSUE_MODE_01){
            FinProductCommon.getElementById('isCashManage').prop('disabled', true).trigger("change");
        }
        //投资周期 默认不显示
        if(pageModel == PAGE_MODEL_NEW){
        	//BS-2036 期限选择单一固定期限，投资周期未反显 start
        	if(isDraft){
        		FinProductCommon.getElementById('investmentCycle').attr('checked',false).parent().parent().hide();
        	}
        	else{
        		FinProductCommon.getElementById('investmentCycle').attr('checked',false).val("").parent().parent().hide();
        	}
        	//BS-2036 期限选择单一固定期限，投资周期未反显 end
        }
        //通过系统参数来判断产品与申报信息是否合并
        if(productIsMerger === YES){
        	$('#FinNetValueProductBuild002_repFinprodInsId').prop('disabled', true);        
        }
        else{
        	//申报页面不能编辑
//        	disablePageElement(pageId+'_bussRepFinProductInfoDto', 1);
        	$('#FinNetValueProductBuild002_repFinprodInsId').prop('disabled', false);        
        }
		// 新建可编辑、到经办可编辑，修改不可 编辑、到代办也不可编辑
		if ($('#wfl_operType').val()){
			if ($('#wfl_operType').val() != 'NEW'){
				$('#FinNetValueProductBuild002_finprodMarketId').prop('disabled', true);
				$('#FinProductBuild002_btn_quickInput').prop('disabled', true).addClass('layui-btn-disabled');	
				//add by yejiejie 2019-3-12 start
				/*$('#FinNetValueProductBuild002_repFinprodStageC').prop('disabled', true);*/
				$('#FinNetValueProductBuild002_prodSeries').prop('disabled', true);
				$('#FinNetValueProductBuild002_prodFeature').prop('disabled', true);
				//add by yejiejie 2019-3-12 end
			} else if(pageModel != PAGE_MODEL_NEW){
			    $('#FinNetValueProductBuild002_finprodMarketId').prop('disabled', true);
			}
			//流程中非周期型产品针对期限与投资周期隐藏处理
			 if(FinProductCommon.getElementById('dealMode').val()=="10"){
					FinProductCommon.getElementById('investmentCycle').attr('checked',false).parent().parent().show();
					FinProductCommon.getElementById('period').attr('checked',false).parent().parent().show();
	                   /* FinProductCommon.getElementById('continueFlag').attr('checked',false).val("").parent().parent().hide();*/
			} else{
					FinProductCommon.getElementById('investmentCycle').val("").parent().parent().hide();
					FinProductCommon.getElementById('period').val("").parent().parent().hide();
            	 	/*FinProductCommon.getElementById('continueFlag').val("").parent().parent().show();*/
			}
		}else{
			if (pageModel != PAGE_MODEL_NEW || confirmFlag === YES){
				$('#FinNetValueProductBuild002_finprodMarketId').prop('disabled', true);
				$('#FinProductBuild002_btn_quickInput').prop('disabled', true).addClass('layui-btn-disabled');	
				//add by chenxiaoqiu 20191216 产品处理模式不能修改
				$('#FinNetValueProductBuild002_dealMode').prop('disabled', true).trigger('change');
				//add by yejiejie 2019-2-20 start
				if(FinProductCommon.getElementById('dealMode').val()=="01"){
					FinProductCommon.getElementById('investmentCycle').attr('checked',false).val("").parent().parent().hide();
					FinProductCommon.getElementById('period').attr('checked',false).val("").parent().parent().hide();
                   /* FinProductCommon.getElementById('continueFlag').attr('checked',false).val("").parent().parent().hide();*/
				} else if(FinProductCommon.getElementById('dealMode').val()=="10"){
					FinProductCommon.getElementById('investmentCycle').attr('checked',false).parent().parent().show();
					FinProductCommon.getElementById('period').attr('checked',false).parent().parent().show();
	                   /* FinProductCommon.getElementById('continueFlag').attr('checked',false).val("").parent().parent().hide();*/
				} else{
					FinProductCommon.getElementById('investmentCycle').val("").parent().parent().hide();
					FinProductCommon.getElementById('period').val("").parent().parent().hide();
               	 	/*FinProductCommon.getElementById('continueFlag').val("").parent().parent().show();*/
				}
				//add by yejiejie 2019-2-20 end
				//add by yejiejie 2019-3-12 start
				/*$('#FinNetValueProductBuild002_repFinprodStageC').prop('disabled', true);*/
				$('#FinNetValueProductBuild002_prodSeries').prop('disabled', true);
				$('#FinNetValueProductBuild002_prodFeature').prop('disabled', true);
				//add by yejiejie 2019-3-12 end
			}
		}
		
		// 待办及修改模式的初期化
        if (FinProductCommon.getElementById('finprodMarketId').val()) {
            // 处理模式相关项目封死
            if (FinProductCommon.getElementById('dealMode').val()) {
                // 收益类型
            	//modify by wm 2019-08-18 start 流程经办中可以修改收益类型
            	if(!workflowId){
                	FinProductCommon.getElementById('profitType').prop('disabled', true);
            	}  
            	//modify by wm 2019-08-18 end 流程经办中可以修改收益类型
            	
                if (FinProductCommon.getElementById('dealMode').val() === DISPOSE_PATTERN_08
                		|| FinProductCommon.getElementById('dealMode').val() === DISPOSE_PATTERN_09) {
                	// 开放类型
                	FinProductCommon.getElementById('openMode').prop('disabled', true).trigger('change');
                	// 利率类型
                	FinProductCommon.getElementById('calcType').val('').prop('disabled', true).trigger("change");
                	// 是否分层
                	FinProductCommon.getElementById('isLay').prop('disabled',true).prop('checked', false);
                	// 预期收益率
                	FinProductCommon.getElementById('yield').val('').prop('disabled', true);
                }else{
                	FinProductCommon.getElementById('operationType').prop('disabled', true).trigger("change");
                }
            }
            // 开放类型是封闭式的时候，是否周期型封上
            if (FinProductCommon.getElementById('profitType').val() === ISSUE_MODE_01) {
            	FinProductCommon.getElementById('isCycle').prop('disabled', true);
            }
            // 是否周期型是否的时候，封闭期限封上
            if (FinProductCommon.getElementById('isCycle').next().find("em").text() === '否') {
            	FinProductCommon.getElementById('closeDays').prop('disabled', true);
            }
            // 是否分层是否的时候，分层类型封上
            if (FinProductCommon.getElementById('isLay').next().find("em").text() === '否') {
            	FinProductCommon.getElementById('layType').prop('disabled', true);
            }else{
            	// 预期收益率
            	FinProductCommon.getElementById('yield').val('').prop('disabled', true);
            }
        }
        vdateMdateChangeAction();

        if(pageModel != PAGE_MODEL_NEW){
            //货币型
            if(FinProductCommon.getElementById('profitType').val() === PROFIT_TYPE_03){
                //产品处理模式
                FinProductCommon.getElementById('dealMode').val(DISPOSE_PATTERN_01).trigger("change").prop('disabled', true);
                //收益类型联动处理
                FinProductCommon.getElementById('profitType').trigger('change');
            }
            else{
            	//产品处理模式修改时不可编辑
            	FinProductCommon.getElementById('dealMode').prop('disabled', true);
            }
        }
        //净值周期型修改是否回补置灰
        if(pageModel == PAGE_MODEL_EDIT && $('#FinNetValueProductBuild002_dealMode').val() === DISPOSE_PATTERN_10){
        	$("#FinNetValueProductBuild002_isAnaplerotic").prop('disabled',true);
        }
	}
	
	/**
	 * 产品模板下拉框change事件处理
	 */
	var templetIdChangeAction = function(){
		var tmplId = $('#FinNetValueProductBuild002_tmplId').val();
		if (tmplId){
			FinProductCommon.initPageInfoAction(pageId, tmplId, finprodTypeTmpl);
		}
	};
	
	/**
	 * 产品代码快录
	 */
	var prodIdQuickInputAction = function(){
		var prodId = $('#FinNetValueProductBuild002_finprodMarketId').val();
		quickInput = true;
		if (prodId){
			FinProductCommon.initPageInfoAction(pageId, prodId, finprodTypeProd);
		}else{
			Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038', JoyinUtil.getLabel('FinProductBuild002.finprodMarketId')))
		}
	}
	//行内标识码
	var repFinprodInsIdChange = function(){
		var repFinprodId = $('#FinNetValueProductBuild002_repFinprodInsId').val();
		// 中债报送数据初始化
    	if(repFinprodId){
    		BussRepFinProductInfo.repInitByRepFinprodIdAction(repFinprodId);
    	}
    	
	}
	/**
	 * 起息日、到期日联动处理产品期限
	 */
	var vdateMdateChangeAction = function(vmflag){
		var vdate = $('#FinNetValueProductBuild002_vdate').val();
		var mdate = $('#FinNetValueProductBuild002_mdate').val();
		var dealMode = $('#FinNetValueProductBuild002_dealMode').val();
		$('#FinNetValueProductBuild002_actMdate').val(mdate);

		
		if (dealMode === DISPOSE_PATTERN_05){
			$('#FinNetValueProductBuild002_closeDays').prop('disabled', true);
		}
		if (vdate && mdate){
			var days = (Date.ofDate(mdate) - Date.ofDate(vdate))/(24*60*60*1000);
			if(days<=0){
				Joyin.error(JoyinUtil.getMessage("起息日必须小于到期日", JoyinUtil.getLabel("FinNetValueProductBuild002.mdate")));
				$('#FinNetValueProductBuild002_mdate').val('');	
			}else{
				if((FinProductCommon.getElementById('isSus').next().find("em").text()==="是") || FinProductCommon.getElementById('isSus').is(':checked')){//永续
					 $('#FinNetValueProductBuild002_termDays').val('');
				 }else{
					 $('#FinNetValueProductBuild002_termDays').val(days);
				 }
				}
			if (dealMode === DISPOSE_PATTERN_05){
				$('#FinNetValueProductBuild002_closeDays').val(days);
			}
		}else{
			$('#FinNetValueProductBuild002_termDays').val('');
			if (dealMode === DISPOSE_PATTERN_05){
				$('#FinNetValueProductBuild002_closeDays').val('');
			}
		}
		//add by yejiejie 2019-2-22 增加产品清盘日的联动
		if($('#FinNetValueProductBuild002_mdate').val()  && pageModel == PAGE_MODEL_NEW && !$('#FinNetValueProductBuild002_liquidationDate').val()){
			if($('#FinNetValueProductBuild002_calendarId').val()){
				getNextWorkDay(mdate,$('#FinNetValueProductBuild002_calendarId').val());
			}else{
				Joyin.error(JoyinUtil.getMessage("请选择交易日历", JoyinUtil.getLabel("FinNetValueProductBuild002.calendarId")));
				$('#FinNetValueProductBuild002_mdate').val('');
				$('#FinNetValueProductBuild002_actMdate').val('');
			}			
		}
		//add by yejiejie 2019-2-22 增加产品清盘日的联动
		
		//add by yejiejie 2019-3-11
		//判断起息日是否为节假日
		if($('#FinNetValueProductBuild002_vdate').val() && vmflag == 1){
			//交易日和首次计费基数联动 yzg 20191020 start 
			// if(pageModel == PAGE_MODEL_NEW || pageModel == PAGE_MODEL_EDIT){
			// 	var vdate = $('#FinNetValueProductBuild002_vdate').val();
			// 	var bussFinProductFeeInfoRows = BussFinProductFeeInfo.getAllRows();
			// 	var bussFinProductFeeInfoTable = BussFinProductFeeInfo.bussFinProductFeeInfoTable;
		    //     if(bussFinProductFeeInfoRows&&bussFinProductFeeInfoRows.length>0){
		    //  	   for(var i=0;i<bussFinProductFeeInfoRows.length;i++){
		    //  		   var rowData = bussFinProductFeeInfoRows[i];
		    //  		   var data ={};
		    //  		   var provisionFirstDay = Date.ofDate(rowData.provisionFirstDay).format('yyyy-MM-dd');
		    //  		   if((rowData.netAssetValueDay === BASE_DATE_TYPE_02 || rowData.netAssetValueDay === BASE_DATE_TYPE_03) && provisionFirstDay === vdate && rowData.basicBill === BILL_BASE_FEE_KIND_03){
		    //  			  data.firstBaseType = BASIC_BILL_04;
		    //  			  bussFinProductFeeInfoTable.renderRow(rowData.LAY_TABLE_INDEX, data);
		    //  		   }else{
		    //  			  data.firstBaseType = '';
		    //  			  bussFinProductFeeInfoTable.renderRow(rowData.LAY_TABLE_INDEX, data);
		    //  		   }
		    //  	   }
		    //     }
			// }
			//交易日和首次计费基数联动 yzg 20191020 end
	        
			if($('#FinNetValueProductBuild002_calendarId').val()){
				//根据交易日历判断是否为节假日
				isHoliday($('#FinNetValueProductBuild002_vdate').val(),$('#FinNetValueProductBuild002_calendarId').val(),vmflag);				
			}else{
				Joyin.error(JoyinUtil.getMessage("请选择交易日历", JoyinUtil.getLabel("FinNetValueProductBuild002.calendarId")));
				$('#FinNetValueProductBuild002_vdate').val('');
			}
		}
		//判断到期日是否为节假日
		if($('#FinNetValueProductBuild002_mdate').val() && vmflag == 2){
			if($('#FinNetValueProductBuild002_calendarId').val()){
				//根据交易日历判断是否为节假日
				isHoliday($('#FinNetValueProductBuild002_mdate').val(),$('#FinNetValueProductBuild002_calendarId').val(),vmflag);			
			}else{
				Joyin.error(JoyinUtil.getMessage("请选择交易日历", JoyinUtil.getLabel("FinNetValueProductBuild002.calendarId")));
				$('#FinNetValueProductBuild002_mdate').val('');
			}
		}
		//add by yejiejie 2019-3-11
	};
	var isHoliday = function(date,calendarId,flage){
		var par = {};
		if(flage == 1){
			par = {'vdate':date,'calendarId':calendarId};
		}else if(flage ==2 ){
			par = {'mdate':date,'calendarId':calendarId};
		}
		Joyin.ajaxJson(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/isHoliday', par, function(result) {
			if(result && flage == 1){
				//是节假日--进行校验--提示
				/*Joyin.error(JoyinUtil.getMessage("起息日是节假日,请重新选择！！！"));
				$('#FinNetValueProductBuild002_vdate').val('');*/
				
				 Joyin.confirm(JoyinUtil.getMessage('起息日是节假日,是否继续?'),null,null,function(){
					 $('#FinNetValueProductBuild002_vdate').val('');
	                });
			}
			if(result && flage == 2){
				//是节假日--进行校验--提示
				/*Joyin.error(JoyinUtil.getMessage("到期日是节假日,请重新选择！！！"));
				$('#FinNetValueProductBuild002_mdate').val('');*/
				 Joyin.confirm(JoyinUtil.getMessage('到期日是节假日,是否继续?'),null,null,function(){
					 $('#FinNetValueProductBuild002_mdate').val('');
	                });
			}
		});
	};
	//add by yejiejie 2019-3-11 end
	var dateChange = function(){
		var mdate = $('#FinNetValueProductBuild002_mdate').val();
		//add by yejiejie 2019-2-22 增加产品清盘日的联动
		if($('#FinNetValueProductBuild002_mdate').val()){
			if($('#FinNetValueProductBuild002_calendarId').val()){
				getNextWorkDay(mdate,$('#FinNetValueProductBuild002_calendarId').val());
			}else{
				Joyin.error(JoyinUtil.getMessage("请选择交易日历", JoyinUtil.getLabel("FinNetValueProductBuild002.calendarId")));
				$('#FinNetValueProductBuild002_mdate').val('');
				$('#FinNetValueProductBuild002_actMdate').val('');
			}			
		}
		//add by yejiejie 2019-2-22 增加产品清盘日的联动
	}
	var getNextWorkDay = function(mdate,calendarId){
		var bankCode = $("#bankCode").val().trim();
		// modify by jiangpeng on 20200723 添加是否永续债条件
		if (bankCode!='004' && !FinProductCommon.getElementById('isSus').is(':checked')) {
		    if(mdate !=null && mdate !=''){
		        var par = {'mdate':mdate,'calendarId':calendarId};
		        Joyin.ajaxJson(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/getNextWorkDay', par, function(result) {
		            if(result){
		                $('#FinNetValueProductBuild002_liquidationDate').val(Date.ofDate(result).format("yyyy-MM-dd"));
		            }
		        });
		    }
		}else{
			$('#FinNetValueProductBuild002_liquidationDate').val(Date.ofDate(mdate).format("yyyy-MM-dd"));
		}   
	};
	

     // add by liupeilin 货币型产品默认确认天数T+0 20200226 begin
	$(document).ready(
			function() {
				 // if(BANKCODE == '004'){
				$("#FinNetValueProductBuild002_profitType").on("change",function() {
							var profitType = $("#FinNetValueProductBuild002_profitType").val();
							if (profitType == PROFIT_TYPE_03) {
								$("#bussFinProductOpenPlanDto_purSpeed").val('0').trigger('change');
							} else {
								$("#bussFinProductOpenPlanDto_purSpeed").val('1').trigger('change');
							}
						});
				 // }
			});
	//add by liupeilin 货币型产品默认确认天数T+0 20200226 end
		    
	/**
	 * 保存或提交按钮执行事件
	 */
	var onSaveOrSubmitAction = function(operateType){
		var object = $.serializeToObject('#productBuildForm', $('#wfl_operType').val() == 'NEW');
		object.operateType = operateType;
        object.pageModel = pageModel;
        object.confirmFlag = confirmFlag;
        //中债申报页面客户类型单选  yjm start 20190808 
      /*  var customerTypeTemp = $('#bussRepFinProductInfoDto_customerType').val();
        var customerType= null;
        if(customerTypeTemp != null)
            customerType=customerTypeTemp.join(",");
        object.bussRepFinProductInfoDto.customerType = customerType;*/
        //中债申报页面客户类型多选  yjm end 20190808 
        var operationTypes = object.operationType;
        
        //add by niuqianyu 2019-07-18 start
        //附件信息
        object.fileCommonList = FileCommon.getUploadFileInfo();
        // add by niuqianyu 2019-07-18 end
        
        // 基础信息
        // 是否分层
    	if ($('#FinNetValueProductBuild002_isLay').next().find("em").text() === '是') {
            object.isLay = 'Y';
        } else {
            object.isLay = 'N';
        }
        // 是否周期型
    	if ($('#FinNetValueProductBuild002_isCycle').next().find("em").text() === '是') {
            object.isCycle = 'Y';
        } else {
            object.isCycle = 'N';
        }
        // 分层类型
        if (object.layType) {
            object.layType = object.layType.join(',');
        }
        // 分行
        if (object.entruster) {
            object.entruster = object.entruster.join(',');
        }
        
        // 付息计划
        if (object.bussFinProductPayPlanDto){
        	object.bussFinProductPayPlanDto.vdate = object.vdate;
        	object.bussFinProductPayPlanDto.mdate = object.mdate;
        	object.bussFinProductPayPlanDto.calendarId = object.calendarId;
        }
        if(operationTypes ==ISSUE_MODE_01){
        	object.bussFinProductOpenPlanDto = null;
        	object.bussFinProductOpenPlanRedDto = null;
        }
        // 开放计划
        if (object.bussFinProductOpenPlanDto){
        	if (object.bussFinProductOpenPlanDto.dayOfWeek){
        		object.bussFinProductOpenPlanDto.dayOfWeek = object.bussFinProductOpenPlanDto.dayOfWeek.join(',');
        	}
        	if (object.bussFinProductOpenPlanDto.dayOfMonth){
        		object.bussFinProductOpenPlanDto.dayOfMonth = object.bussFinProductOpenPlanDto.dayOfMonth.join(',');
        	}
        	if (object.bussFinProductOpenPlanDto.workdayOfMonth){
        		object.bussFinProductOpenPlanDto.workdayOfMonth = object.bussFinProductOpenPlanDto.workdayOfMonth.join(',');
        	}
        	if (!object.bussFinProductOpenPlanDto.hdayOpenFlag) {
        		object.bussFinProductSaleParamDto.hdayOpenFlag = 'N';
            }
        	if(object.operationType) {
        		object.bussFinProductOpenPlanDto.operationType = object.operationType;//发行方式不为空时，将发行方式传递到开放计划DTO里
        	}
        	//add by wm 2019-11-26 开放计划月选第几周报错修复 start
        	if (object.bussFinProductOpenPlanDto.weekOfMonth){
        		object.bussFinProductOpenPlanDto.weekOfMonth = object.bussFinProductOpenPlanDto.weekOfMonth.join(',');
        	}
        	if (object.bussFinProductOpenPlanDto.workdayOfWeek){
        		object.bussFinProductOpenPlanDto.workdayOfWeek = object.bussFinProductOpenPlanDto.workdayOfWeek.join(',');
        	}
        	//add by wm 2019-11-26 开放计划月选第几周报错修复 end
        }
        var openMode ="";
        if (object.bussFinProductOpenPlanDto){
        	openMode =object.bussFinProductOpenPlanDto.openMode;//add by zlp 20190314 高频开发不校验开放计划
        }
        if(!draftoption&&openMode!='02'&&!BussFinProductOpenPlan.chkOpenPlan()) return false;//add by zhangweiqi 开放计划元素调整后重新生成计划
        // 赎回开放计划 add by yejiejie
        if (object.bussFinProductOpenPlanRedDto){
        	var openRedMode = object.bussFinProductOpenPlanRedDto.openMode;
        	if(openMode!='02'&&object.dealMode != DISPOSE_PATTERN_10&&!BussFinProductOpenPlan.chkRedOpenPlan()){
        		return false;
        	}
        	if (object.bussFinProductOpenPlanRedDto.dayOfWeek){
        		object.bussFinProductOpenPlanRedDto.dayOfWeek = object.bussFinProductOpenPlanRedDto.dayOfWeek.join(',');
        	}
        	if (object.bussFinProductOpenPlanRedDto.dayOfMonth){
        		object.bussFinProductOpenPlanRedDto.dayOfMonth = object.bussFinProductOpenPlanRedDto.dayOfMonth.join(',');
        	}
        	if (object.bussFinProductOpenPlanRedDto.workdayOfMonth){
        		object.bussFinProductOpenPlanRedDto.workdayOfMonth = object.bussFinProductOpenPlanRedDto.workdayOfMonth.join(',');
        	}
        	if (!object.bussFinProductOpenPlanRedDto.hdayOpenFlag) {
        		object.bussFinProductOpenPlanRedDto.hdayOpenFlag = 'N';
            }
        	//add by wm 2019-11-26 开放计划月选第几周报错修复 start
        	if (object.bussFinProductOpenPlanRedDto.weekOfMonth){
        		object.bussFinProductOpenPlanRedDto.weekOfMonth = object.bussFinProductOpenPlanRedDto.weekOfMonth.join(',');
        	}
        	if (object.bussFinProductOpenPlanRedDto.workdayOfWeek){
        		object.bussFinProductOpenPlanRedDto.workdayOfWeek = object.bussFinProductOpenPlanRedDto.workdayOfWeek.join(',');
        	}
        	//add by wm 2019-11-26 开放计划月选第几周报错修复 end
        }
        
        // 销售参数
        if (object.bussFinProductSaleParamDto){
        	// 销售渠道
        	if (object.bussFinProductSaleParamDto.saleChannel){
        		object.bussFinProductSaleParamDto.saleChannel = object.bussFinProductSaleParamDto.saleChannel.join(',');
        	}
        	// 销售地区
        	if (object.bussFinProductSaleParamDto.saleArea){
        		object.bussFinProductSaleParamDto.saleArea = object.bussFinProductSaleParamDto.saleArea.join(',');
        	}
        	// 是否可质押
        	if (!object.bussFinProductSaleParamDto.canPlge) {
        		object.bussFinProductSaleParamDto.canPlge = 'N';
            }
        	//add by yejiejie 2019-2-13 start
        	// 销售部门
        	if (object.saleDepartment){
        		object.saleDepartment = object.saleDepartment.join(',');
        	}
        	// 目标客户类型
        	if (object.bussFinProductSaleParamDto.investorType){
        		object.bussFinProductSaleParamDto.investorType = object.bussFinProductSaleParamDto.investorType.join(',');
        		if (object.bussRepFinProductInfoDto){
        		    object.bussRepFinProductInfoDto.repInvestorType = object.bussFinProductSaleParamDto.investorType;
        		}
        	}
        	//add by yejiejie 2019-2-13 end
        	// 同业机构
        	if (object.sameOrg){
        		object.sameOrg = object.sameOrg.join(',');
        		object.bussFinProductSaleParamDto.sameOrg =  object.sameOrg;
        	}
        }
        // 估值信息
        if (object.bussFinProductValuationDto){
        	// 托管活期利息计提
        	if (!object.bussFinProductValuationDto.isCountingCurInt) {
        		object.bussFinProductValuationDto.isCountingCurInt = 'Y';
            }
        }
        
        // 中债报送信息
        if (object.bussRepFinProductInfoDto){
        	// 投资者风险偏好
        	if (object.bussRepFinProductInfoDto.repRiskType){
        		object.bussRepFinProductInfoDto.repRiskType = object.bussRepFinProductInfoDto.repRiskType.join(',');
        	}
        	// 产品增信机构类型
        	if (object.bussRepFinProductInfoDto.repCreditType){
        		object.bussRepFinProductInfoDto.repCreditType = object.bussRepFinProductInfoDto.repCreditType.join(',');
        	}
            // 产品投资国家或地区
        	if (object.bussRepFinProductInfoDto.repInvestCountry){
        		object.bussRepFinProductInfoDto.repInvestCountry = object.bussRepFinProductInfoDto.repInvestCountry.join(',');
        	}
            // 境外托管机构国别
        	if (object.bussRepFinProductInfoDto.repOutDepCtry){
        		object.bussRepFinProductInfoDto.repOutDepCtry = object.bussRepFinProductInfoDto.repOutDepCtry.join(',');
        	}

            //BSTOT-455 募集要素DIV里的募集币种、兑付本金币种、对付收益币种应该不能多选 guyunfei 20200111 start
        	// 兑付本金币种
        	/*if (object.bussRepFinProductInfoDto.repCashPrinCcy){
        		object.bussRepFinProductInfoDto.repCashPrinCcy = object.bussRepFinProductInfoDto.repCashPrinCcy.join(',');
        	}*/
        	// 兑付收益币种
        	/*if (object.bussRepFinProductInfoDto.repCashProfCcy){
        		object.bussRepFinProductInfoDto.repCashProfCcy = object.bussRepFinProductInfoDto.repCashProfCcy.join(',');
        	}*/
            //BSTOT-455 募集要素DIV里的募集币种、兑付本金币种、对付收益币种应该不能多选 guyunfei 20200111 end

        	//REP_IS_YIELD是否有预期收益率
        	if(object.bussRepFinProductInfoDto.repIsYield && object.bussRepFinProductInfoDto.repIsYield === NO){
        	    object.bussRepFinProductInfoDto.repIsYield = SVR_HAVE_OR_NOT_02;
        	}
        	//REP_TERM_RIGHT_FLAG发行机构提前终止权标识、REP_CUS_REDEEM_FLAG客户赎回权标识、REP_CREDIT_FLAG产品增信标识
        	if(object.bussRepFinProductInfoDto.repTermRightFlag && object.bussRepFinProductInfoDto.repTermRightFlag === NO){
        	    object.bussRepFinProductInfoDto.repTermRightFlag = SVR_HAVE_OR_NOT_02;
        	}
        	//REP_CUS_REDEEM_FLAG客户赎回权标识、REP_CREDIT_FLAG产品增信标识
        	if(object.bussRepFinProductInfoDto.repCusRedeemFlag && object.bussRepFinProductInfoDto.repCusRedeemFlag === NO){
        	    object.bussRepFinProductInfoDto.repCusRedeemFlag = SVR_HAVE_OR_NOT_02;
        	}
        	//REP_CREDIT_FLAG产品增信标识
        	if(object.bussRepFinProductInfoDto.repCreditFlag && object.bussRepFinProductInfoDto.repCreditFlag === NO){
        	    object.bussRepFinProductInfoDto.repCreditFlag = SVR_HAVE_OR_NOT_02;
        	}
        	//计划募集金额
        	if(object.bussFinProductSaleParamDto) {
        	    object.bussRepFinProductInfoDto.repRaiseAmtPlan  = object.bussFinProductSaleParamDto.raiseAmtPlan;
        	 }
        	 //募集起始日（从）
        	if(object.repRaisePstrdate) {
    	    object.bussRepFinProductInfoDto.repRaisePstrdate = object.repRaisePstrdate;
        	}
        	//募集起始日（从）（到）
        	if(object.repRaisePenddate) {
        		object.bussRepFinProductInfoDto.repRaisePenddate = object.repRaisePenddate;
        	}
        }
        var fundAccountTables = BussFinProductFundAccountInfo.table;
        var fundAccountTable = fundAccountTables.getAllRows();
        if(fundAccountTable&&fundAccountTable.length>0){
     	   for(var i=0;i<fundAccountTable.length;i++){
     		   if(fundAccountTable[i].feeType&& Array.isArray(fundAccountTable[i].feeType)){
     			   var feeType="";
     			  for(var j=0;j<fundAccountTable[i].feeType.length;j++){
     				   if(fundAccountTable[i].feeType[j])
     				   feeType+=fundAccountTable[i].feeType[j]+",";
     			   }
     			   fundAccountTable[i].feeType=feeType;
     		   }
     	   }
        }
        object.bussFinProductFundAccountInfoDto = fundAccountTable;
        //业绩回补合版本相关 start wangcheng 20191025
        //产品到期日调整后重新生成回补计划
        //修改bug BS2.1.0 BSTOZ-919 是否回补选择【否】时，提交产品仍旧报错【到期日已经改变,请重新生成回补计划】 update by wangcheng 20191028 start
        if($('#FinNetValueProductBuild002_isAnaplerotic').val()=='Y' && !BussFinProductAnapleroticCommon.chkAnapleroticPlan()){
     	   return false;
        } 
        //修改bug BS2.1.0 BSTOZ-919 是否回补选择【否】时，提交产品仍旧报错【到期日已经改变,请重新生成回补计划】 update by wangcheng 20191028 end
         //回补信息
         var finProductAnaplerotic = BussFinProductAnapleroticCommon.table;
         var finProductAnapleroticData = finProductAnaplerotic.getAllRows();
		if($('#FinNetValueProductBuild002_isAnaplerotic').val()=='Y'&& (finProductAnapleroticData && finProductAnapleroticData.length>1)
			&&object.dealMode != DISPOSE_PATTERN_11){
             Joyin.alert("回补信息不能大于一条数据");
             return;
         }
         if(finProductAnapleroticData){
             for(var i=0;i<finProductAnapleroticData.length;i++){
                 var finProductAnapleroticInfoDtoData = finProductAnapleroticData[i].finProductAnapleroticInfoDto;
                 finProductAnapleroticData[i].finProductAnapleroticInfoDto = JSON.parse(finProductAnapleroticInfoDtoData);
             }
         }
         object.finProductAnapleroticTabInfoDtoList = finProductAnapleroticData;
        //业绩回补合版本相关 end wangcheng 20191025
        // 分红计划
        if (object.bussFinProductBonusPlanDto){
        	object.bussFinProductBonusPlanDto.vdate = object.vdate;
        	object.bussFinProductBonusPlanDto.mdate = object.mdate;
        	object.bussFinProductBonusPlanDto.calendarId = object.calendarId;
        	object.bussFinProductBonusPlanDto.profitType = object.profitType;
        }
        //add by wm 2019-08-08 start 分红计划要素变更
        if(!draftoption&&!BussFinProductBonusPlan.checkIsChange()) return false;
        //add by wm 2019-08-08 end 分红计划要素变更
        //update zhangweiqi 根据处理模式判断是否周期型
        if(object.dealMode=="10") object.isCycle = 'Y';
        else  object.isCycle = 'N';
        if(object.dealMode=="11") object.isLay = 'Y';
        else  object.isLay = 'N';
        if(object.dealMode=="12"){
            object.isLay = 'Y';
            object.isCycle = 'Y';
        }
        //分层列表
        object.bussFinProductLayeredInfoDto = BussFinProductLayered.getAllRows();

		console.log("----------------object-------------------------")
		console.log(object)
		console.log(object.bussBenchMarkInfoDto)
		console.log(object.bussFinProductFloatFeeDto)
		console.log(object.bussBenchMarkFloatInfoDto)

        Joyin.ajaxJson(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/check',object, function(result) {
    		Joyin.confirm(result.data, function() {
                	if(operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/save', object, "FinNetValueProductBuild002_btn_save");
                    }
                    else {
                        WorkFlow.doSubmitList(basePath + 'invest/product/productelement/netvalueproduct/FinNetValueProductBuild002/save', object, "FinNetValueProductBuild002_btn_submit");
                    }
                })
            })
	}
	var targetCustomer1ChangeAction=function(){
		var targetCustomer1=$('#FinNetValueProductBuild002_targetCustomer1').val();
		if(targetCustomer1=="02"){
			$("#bussFinProductSaleParamDto_repInvestorType").val("02").prop("disabled",true).trigger("change");
		}else if(targetCustomer1=="03"){
			$("#bussFinProductSaleParamDto_repInvestorType").val("04").prop("disabled",true).trigger("change");
		}
	};
	var investmentCycleChangeAction=function(){
		var investmentCycleArr = [];
	//导致待办事项中周期天数必填星号多了两个  wyn 20210402
		var investmentCycle=$('#FinNetValueProductBuild002_investmentCycle').val();
		if(investmentCycle){
			investmentCycleArr[0] = {id: investmentCycle, text : investmentCycle};
			INVESTMENT_CLCYE = JSON.stringify(investmentCycleArr);
		}
		else{
			INVESTMENT_CLCYE = '';
		}
	};
	var targetCustomer2ChangeAction=function(){
		var targetCustomer2=$('#FinNetValueProductBuild002_targetCustomer2').val();
		if(targetCustomer2=="03"){
			$("#bussFinProductSaleParamDto_repInvestorType").val("01").prop("disabled",true).trigger("change");
		}else if(targetCustomer2=="02"){
			$("#bussFinProductSaleParamDto_repInvestorType").val("05").prop("disabled",true).trigger("change");
		}else if(targetCustomer2=="01"){
			$("#bussFinProductSaleParamDto_repInvestorType").val("03").prop("disabled",true).trigger("change");
		}
	};
	//add by yejiejie 2019-03-05 start
	var operationTypeChanges =function(){
		var operationType = $("#FinNetValueProductBuild002_operationType").val();
        var feeID = $("#bussFinProductFloatFeeDto_feeId").val();
		if(operationType==ISSUE_MODE_01){
			//封闭式--巨额赎回比例隐藏
			$("#hugeRedRatioDiv").hide();
			//add by yjj 2019-3-7
			$('#FinNetValueProductBuild002_isSus').prop("checked",false).trigger('change');
			$("#FinNetValueProductBuild002_isSus").next().find("em").text('否');
			$("#FinNetValueProductBuild002_isSus").next().removeClass("layui-form-onswitch").addClass("layui-checkbox-disbaled layui-disabled");
			$("#FinNetValueProductBuild002_isSus").prop('disabled',true);
			FinProductCommon.isSus();
            //OPEX-572 产品端-浮动管理费首次计提日应为-1日 guyunfei start
            //$("#bussFinProductFloatFeeDto_eAccrDate").val($("#bussFinProductFloatFeeDto_provisionFirstDay").val()).prop('disabled',true);
            var mdate = $("#FinNetValueProductBuild002_mdate").val();
            if(mdate != null && mdate != '' && feeID != '' && feeID != null){
                var mdateLow = Date.ofDate(mdate).DateAdd('d', -1).format('yyyy-MM-dd');
                $("#bussFinProductFloatFeeDto_provisionFirstDay").val(mdateLow);
                $("#bussFinProductFloatFeeDto_eAccrDate").val(mdateLow).prop('disabled',true);
            }
            //OPEX-572 产品端-浮动管理费首次计提日应为-1日 guyunfei end
			//end
			//封闭式置灰 中债报送tab的 是否设置最短持有期限 和 最短持有期限（天） start yjm 20190807
			$('#bussRepFinProductInfoDto_isShortTerm').removeAttr('required');
			$('#bussRepFinProductInfoDto_isShortTerm').val('').trigger('change').attr('disabled',true);
            //封闭式置灰 中债报送tab的 是否设置最短持有期限 和 最短持有期限（天） end yjm 20190807
            //封闭式置灰 基本信息的 是否现金管理类产品 start yjm 20190828
			$('#FinNetValueProductBuild002_isCashManage').removeAttr('required');
            $('#FinNetValueProductBuild002_isCashManage').val('').trigger('change').attr('disabled',true);
            //封闭式置灰 基本信息的 是否现金管理类产品 end yjm 20190828
		}else{
			//开放式--巨额赎回比例显示
			$("#hugeRedRatioDiv").show();
			
			//add by yejiejie 2019-3-7 start
			if(operationType == ISSUE_MODE_02){
				//yejiejie 2019-3-5
	    		//$('#FinNetValueProductBuild002_isSus').prop("checked",true).trigger('change');
	    		//$('#FinNetValueProductBuild002_isSus').attr('checked','true').val('Y');
	    		//SHBS-1939  2019-04-14 begin
				if(FinProductCommon.getElementById('isSus').is(':checked')){
				  $("#FinNetValueProductBuild002_isSus").next().find("em").text('是');
				  $("#FinNetValueProductBuild002_isSus").next().addClass("layui-form-onswitch");	 
	    		}
				$("#FinNetValueProductBuild002_isSus").next().removeClass("layui-checkbox-disbaled layui-disabled");
				$("#FinNetValueProductBuild002_isSus").prop('disabled',false);

                //OPEX-572 产品端-浮动管理费首次计提日应为-1日 guyunfei start
                var vdate = $('#FinNetValueProductBuild002_vdate').val();
                // bugTISHKBS-1157;联动时先判断下浮动管理费是否选择名称，如果没有视为还没有选择浮动管理费，所以不增加日期
                if( feeID != '' && feeID != null ){
                   $("#bussFinProductFloatFeeDto_eAccrDate").prop('disabled',false);
                   $("#bussFinProductFloatFeeDto_provisionFirstDay").val(vdate);
                }
                //OPEX-572 产品端-浮动管理费首次计提日应为-1日 guyunfei end
				//SHBS-1939  2019-04-14 end
//				chk.checked = true;
	    		FinProductCommon.isSus();
                //开放式 中债报送tab的 是否设置最短持有期限 和 最短持有期限（天） 必输 start yjm 20190807
				$('#bussRepFinProductInfoDto_isShortTerm').attr('required', true);
                $('#bussRepFinProductInfoDto_isShortTerm').attr('disabled', false);
                //开放式 中债报送tab的 是否设置最短持有期限 和 最短持有期限（天） 必输 end yjm 20190807
                //开放式必输 基本信息的 是否现金管理类产品 start yjm 20190828
				$('#FinNetValueProductBuild002_isCashManage').attr('required',true);
                $('#FinNetValueProductBuild002_isCashManage').attr('disabled', false);
                //开放式必输 基本信息的 是否现金管理类产品 end yjm 20190828
			}
			//add by yejiejie 2019-3-7 end
		}
		star();
	};
	//add by yejiejie 2019-03-05 end
	//add by yejiejie 2019-3-8 start
	var getIdName = function(){
		if($('#FinNetValueProductBuild002_prodSeries').val() 
				/*&& $('#FinNetValueProductBuild002_repFinprodStageC').val()*/
				&& $('#FinNetValueProductBuild002_prodFeature').val()){
				
				//产品代码
				//生成规则：产品系列对应的英文字母+产品特点对应的A~W+年份后两个数字+产品期次（不满3位数的，在左边补0）
				var finprodMarketIdStr = 
				FinProductCommon.prodSeriesStr(1) 
				+ FinProductCommon.prodFeatureStr() 
				+ FinProductCommon.newDate()
				+ FinProductCommon.repFinprodStageStr();	
				$('#FinNetValueProductBuild002_finprodMarketId').val(finprodMarketIdStr);
				//自动生成产品名称：
				//生成规则：“鑫意”理财+[产品系列]+[产品特点对应的ABCD...W等]+[年份后两个数字]+[产品期次(不满足3位的期次，在期次左边补0)]+期人民币理财产品				
				var finprodNameStr = 
				"“鑫意”理财"
				+FinProductCommon.prodSeriesStr(2) 
				+ FinProductCommon.prodFeatureStr() 
				+ FinProductCommon.newDate()
				+ FinProductCommon.repFinprodStageStr()
				+"期人民币理财产品";	
				$('#FinNetValueProductBuild002_finprodName').val(finprodNameStr);

				//自动生成产品名称：
				//产品简称生成规则：[产品系列]+[产品特点对应的ABCD...W等]+[年份后两个数字]+[产品期次(不满足3位的期次，在期次左边补0)]				
				var finprodAbbrStr = 
				FinProductCommon.prodSeriesStr(2) 
				+ FinProductCommon.prodFeatureStr() 
				+ FinProductCommon.newDate()
				+ FinProductCommon.repFinprodStageStr();	
				$('#FinNetValueProductBuild002_finprodAbbr').val(finprodAbbrStr);
		}
	};
	//add by yejiejie 2019-3-8 end
	$(function() {
		finProductBuildInit();
		
		FinProductCommon.bindEvent();
		
		/*// 开放类型下拉框change事件处理
		$('#FinProductBuild002_operationType').change(function(){
			FinProductCommon.openModeSelect();
		});*/
		
		// /**add by taoran 20200317*/
		// //产品代码修改事件 不允许中文
		// $('#FinNetValueProductBuild002_finprodMarketId').bind("change", function(){
		// 	$('#FinNetValueProductBuild002_finprodMarketId').val(
		// 			$('#FinNetValueProductBuild002_finprodMarketId').val().replace(/[^\a-\z\A-\Z0-9]/g,''));
		// });
		// /**add by taoran 20200317*/
		
		//行内标识码联动
		
		$('#FinNetValueProductBuild002_repFinprodInsId').change(function(){
			repFinprodInsIdChange();
		});
		// 起息日、到期日联动处理产品期限
		$('#FinNetValueProductBuild002_vdate').change(function(){
			vdateMdateChangeAction(1);
			//add by yejiejie 2019-3-1 start
			dateChange();
			//add by yejiejie 2019-3-1 end
		});
		$('#FinNetValueProductBuild002_mdate').change(function(){
			vdateMdateChangeAction(2);
			//add by yejiejie 2019-3-1 start
			dateChange();
			//add by yejiejie 2019-3-1 end
			if ($("#FinNetValueProductBuild002_operationType").val() === ISSUE_MODE_01) {
				var mdate = $("#FinNetValueProductBuild002_mdate").val();
				var feeID = $("#bussFinProductFloatFeeDto_feeId").val();
				if (mdate != null && mdate != '' && feeID != '' && feeID != null) {
					var mdateLow = Date.ofDate(mdate).DateAdd('d', -1).format('yyyy-MM-dd');
					$("#bussFinProductFloatFeeDto_provisionFirstDay").val(mdateLow);
					$("#bussFinProductFloatFeeDto_eAccrDate").val(mdateLow).prop('disabled', true);
				}
			}
		});
		$('#FinNetValueProductBuild002_dealMode').change(function(){
		    PROD_STYLE = '';
			vdateMdateChangeAction();
		});
		 
		// 快录按钮执行事件
		$('#FinProductBuild002_btn_quickInput').click(function(){
			prodIdQuickInputAction();
		});
		// 产品模板下拉框change事件处理
		$('#FinNetValueProductBuild002_tmplId').change(function(){
			templetIdChangeAction();
		});
		// 保存按钮执行事件
		$('#FinNetValueProductBuild002_btn_save').click(function(){
//			if($("#FinNetValueProductBuild002_operationType").val()=="02" && $("#bussRepFinProductInfoDto_repOpenMode").val()==""){
//				Joyin.error("开放型产品开放模式必填");
//                return;
//			}
            /*if ($('#bussFinProductSaleParamDto_investorType').val().indexOf("04")!=-1 && ($("#bussFinProductSaleParamDto_sameOrg").val()=="" ||$("#bussFinProductSaleParamDto_sameOrg").val()==null) ){
                Joyin.error("当目标客户类型选择“银行同业专属”时，同业机构必填");
                return;
            }*/
			var operationType2 =  $('#FinNetValueProductBuild002_operationType').val();
			var termDays2 =  $('#FinNetValueProductBuild002_termDays').val();
			var openMode =  $('#bussFinProductOpenPlanDto_openMode').val();
			var feeID = $("#bussFinProductFloatFeeDto_feeId").val();
			if($('#FinNetValueProductBuild002_dealMode').val() != '10' && $('#bussFinProductOpenPlanDto_openType').val() !='03'){//申赎不同频
				 if(($('#isClick').val() == 'false' || $('#isClickRed').val() == 'false')  &&  quickInput == true && operationType2!=ISSUE_MODE_01 && openMode != "02" ){
				    	Joyin.confirm(JoyinUtil.getMessage('未重新生成开放计划，是否继续操作?'),function(){
				    		if(operationType2 == "01" && termDays2 <90){
								Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
									onSaveOrSubmitAction('save');
				                },null);
							}else{
								onSaveOrSubmitAction('save');
							}
		                },null);
				    }
				    else{	
					if(operationType2 == "01" && termDays2 <90){
						Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
							onSaveOrSubmitAction('save');
		                },null);
					}else{
						onSaveOrSubmitAction('save');
					}
				    }
			}
			else{//申赎同频
		    if($('#isClick').val() == 'false' &&  quickInput == true && operationType2!=ISSUE_MODE_01 && openMode != "02" ){
		    	Joyin.confirm(JoyinUtil.getMessage('未重新生成开放计划，是否继续操作?'),function(){
		    		if(operationType2 == "01" && termDays2 <90){
						Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
							onSaveOrSubmitAction('save');
		                },null);
					}else{
						onSaveOrSubmitAction('save');
					}
               },null);
		    }
		    else{	
			if(operationType2 == "01" && termDays2 <90){
				Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
					onSaveOrSubmitAction('save');
               },null);
			}else{
				onSaveOrSubmitAction('save');
			}
		    }
		}
		});
		
		$('#FinNetValueProductBuild002_dealMode').change(function() {
            if($('#FinNetValueProductBuild002_dealMode').val() === '10') {
//            	$('#FinNetValueProductBuild002_period').val('').trigger('change').prop('disabled', false);
//            	$('#FinNetValueProductBuild002_investDays').val('').trigger('change').prop('disabled', false);
            	$('#FinNetValueProductBuild002_operationType').val("02").prop("disabled",true).trigger("change");
        	    //FinProductCommon.getElementById('investmentCycle').val("").parent().parent().show();
        	    //FinProductCommon.getElementById('period').val("").parent().parent().show();
            	//BS-2036 草稿箱功能，产品处理模式选择周期，期限任意选择一个存为草稿，打开该草稿时显示是空 start
            	FinProductCommon.getElementById('period').parent().parent().show();
            	FinProductCommon.getElementById('investmentCycle').parent().parent().show();
            	FinProductCommon.getElementById('period').trigger("change");
            	FinProductCommon.getElementById('investmentCycle').trigger("change");
            	//BS-2036 草稿箱功能，产品处理模式选择周期，期限任意选择一个存为草稿，打开该草稿时显示是空 end
            } 
            else {
//            	$('#FinNetValueProductBuild002_period').val('').trigger('change').prop('disabled', true);
//            	$('#FinNetValueProductBuild002_investDays').val('').trigger('change').prop('disabled', true);
				$('#FinNetValueProductBuild002_operationType').prop("disabled",false).trigger("change");
				FinProductCommon.getElementById('investmentCycle').attr('checked',false).val("").parent().parent().hide();
				FinProductCommon.getElementById('period').val("").parent().parent().hide();
				if($('#bussFinProductOpenPlanDto_sameFLag').val() === 'Y'  && $('#bussFinProductOpenPlanDto_openType').val() !='03')
				{//申赎同频 fixed BS-1473 BUG 【净值化产品管理】：快录周期型产品代码，修改为非周期型产品，流程提交时存在提醒问题；
				$('#bussFinProductOpenPlanDto_openType').val("03")
				}
			}
			// 上农商回补信息调整只支持普通产品，现修改分档、周期的时候，是否回补置灰，并默认为 否 add by wangcheng 20191030
			if(pageModel === PAGE_MODEL_NEW){
				if($('#FinNetValueProductBuild002_dealMode').val() === DISPOSE_PATTERN_10){
					$('#FinNetValueProductBuild002_isAnaplerotic').val('N').prop("disabled",true).trigger("change");
				}else{
					$('#FinNetValueProductBuild002_isAnaplerotic').prop("disabled",false).trigger("change");
				}
			}
		});

		// 提交按钮执行事件
		$('#FinNetValueProductBuild002_btn_submit').click(function(){
//			if($("#FinNetValueProductBuild002_operationType").val()=="02" && $("#bussRepFinProductInfoDto_repOpenMode").val()==""){
//				Joyin.error("开放型产品开放模式必填");
//                return;
//			}
			/*if ($('#bussFinProductSaleParamDto_investorType').val().indexOf("04")!=-1 && ($("#bussFinProductSaleParamDto_sameOrg").val()=="" ||$("#bussFinProductSaleParamDto_sameOrg").val()==null) ){
				Joyin.error("当目标客户类型选择“银行同业专属”时，同业机构必填");
				return;
			}*/
			var operationType2 =  $('#FinNetValueProductBuild002_operationType').val();
			var termDays2 =  $('#FinNetValueProductBuild002_termDays').val();	
			var openMode =  $('#bussFinProductOpenPlanDto_openMode').val();

			if(draftoption){
			    onSaveOrSubmitAction('submit');
			}else{
                if($('#FinNetValueProductBuild002_dealMode').val() != '10' && $('#bussFinProductOpenPlanDto_openType').val() !='03'){//申赎不同频
                     if(( $('#isClick').val() == 'false' || $('#isClickRed').val() == 'false' ) &&  quickInput == true && operationType2!=ISSUE_MODE_01 && openMode != "02"){
                            Joyin.confirm(JoyinUtil.getMessage('未重新生成开放计划，是否继续操作?'),function(){
                                if(!draftoption && operationType2 == "01" && termDays2 <90){
                                    Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
                                        onSaveOrSubmitAction('submit');
                                    },null);
                                }else{
                                    onSaveOrSubmitAction('submit');
                                }
                            },null);
                        }
                        else{
                        if(!draftoption && operationType2 == "01" && termDays2 <90){
                            Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
                                onSaveOrSubmitAction('submit');
                            },null);
                        }else{
                            onSaveOrSubmitAction('submit');
                        }
                        }
                }
                else{//申赎同频
                    if($('#isClick').val() == 'false' &&  quickInput == true && operationType2!=ISSUE_MODE_01 && openMode != "02" ){
                        Joyin.confirm(JoyinUtil.getMessage('未重新生成开放计划，是否继续操作?'),function(){
                            if(!draftoption && operationType2 == "01" && termDays2 <90){
                                Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
                                    onSaveOrSubmitAction('submit');
                                },null);
                            }else{
                                onSaveOrSubmitAction('submit');
                            }
                        },null);
                    }
                    else{
                        if(!draftoption && operationType2 == "01" && termDays2 <90){
                            Joyin.confirm(JoyinUtil.getMessage('封闭式产品产品期限小于90天?'),function(){
                                onSaveOrSubmitAction('submit');
                            },null);
                        }else{
                            onSaveOrSubmitAction('submit');
                        }
                    }
                }
		    }
			
		});
		//update zhangweiqi 增加目标客户类型一级分类和二级分类对于中债的目标客户类型的联动事件  begin
		$('#FinNetValueProductBuild002_targetCustomer1').change(function(){
			targetCustomer1ChangeAction();
		});
		$('#FinNetValueProductBuild002_targetCustomer2').change(function(){
			targetCustomer2ChangeAction();
		});
		$('#FinNetValueProductBuild002_investmentCycle').change(function(){
			investmentCycleChangeAction();
		});
		//update zhangweiqi 增加目标客户类型一级分类和二级分类对于中债的目标客户类型的联动事件  end
		//add by yejiejie 2019-03-05 start 发行方式的change事件
		$("#FinNetValueProductBuild002_operationType").change(operationTypeChanges);
		//add by yejiejie 2019-03-05 end
		//add by yejiejie 2109-3-7 start 产品期次的change事件
/*		$('#FinNetValueProductBuild002_repFinprodStageC').change(function(){
			getIdName();
		});*/
		$('#FinNetValueProductBuild002_prodSeries').change(function(){
//			getIdName();
//			FinProductCommon.prodSeriesChange();//add by yejiejie 2019-3-12
		});
		$('#FinNetValueProductBuild002_prodFeature').change(function(){
//			getIdName();
		});
		//add by yejiejie 2109-3-8 end
		
		//给了收益标识默认值，要执行其上面的联动
		// BS-2036 草稿箱反显，期限选择多固定期限，是否续投选择为是未正确显示 start
		if(pageModel === PAGE_MODEL_NEW) {
	    // BS-2036 草稿箱反显，期限选择多固定期限，是否续投选择为是未正确显示 end
			if(isDraft){
				$("#draftInit").val("Y");
			}
			FinProductCommon.profitTypeSelect();
			$("#draftInit").val("");
		}
		//唤醒change事件
		$("#FinNetValueProductBuild002_period").trigger('change');
		// add by jiangpeng on 20210708 TISHKBS-2404 start
		// 封闭式产品修改页面“是否永续”置灰状态
		if ($("#FinNetValueProductBuild002_operationType").val() == ISSUE_MODE_01) {
			$("#FinNetValueProductBuild002_isSus").prop('disabled',true);
		}
		// add by jiangpeng on 20210708 TISHKBS-2404 end
	});
}());

//add by wangyang1 [吉林个性化] 2019年9月26日 start 
function setFinprodAbbr() {
    var bankCode = $("#bankCode").val().trim();
    if (bankCode=='004') {
        var finprodMarketId = $("#FinNetValueProductBuild002_finprodMarketId").val().trim();
        if (finprodMarketId) {
            $("#FinNetValueProductBuild002_finprodAbbr").val(finprodMarketId);
        }
    }
}

//add by wangyang1 [吉林个性化] end

//START 引用组件时  未添加此方法  导致js报错
// modify by wj 20200426 TISHKBS-270  净值型分档产品，在产品分层信息中添加产品款型，添加到第二个的时候，选择目标客户类型出现界面错乱
var tarCustomerTypeChange = function(){
	var LayeredTableInfo = BussFinProductLayered.getAllRows();
	if($("#bussFinProductSaleParamDto_investorType").val()=='04'){
		$("#bussFinProductSaleParamDto_sameOrg").removeAttr("disabled");
	}
	else{
		for(var i in LayeredTableInfo){
			if(LayeredTableInfo[i].tarCustomerType && LayeredTableInfo[i].tarCustomerType.indexOf("04") != -1){
				$("#bussFinProductSaleParamDto_sameOrg").removeAttr("disabled");
				break;
			}
			else{
				$("#bussFinProductSaleParamDto_sameOrg").attr("disabled","disabled");
			}
		}
	}
}
//END 引用组件时  未添加此方法  导致js报错