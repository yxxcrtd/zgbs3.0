$(function() {
    /**
     *  是否自动加券(发大镜操作)
     * **/
    var isAutoCoupon = false;
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

    //获取计息要素信息元素id
    var getInterestFactorElementById = function(id,type) {
        if(type=="div"){
            return $('#' + 'finInterestFactorDto' + '_' + id+'_'+'div');
        }else{
            return $('#' + 'finInterestFactorDto' + '_' + id);
        }
    };
    
    
    function saveOrSubmit(operateType) {
        //update by  zhangnian 20190920 start
        if(!isAutoCoupon){
            if (FinPayInterestStdInfo && FinPayInterestStdInfo.checkPlanChanged()) {
                Joyin.error("付息计划已修改，请确认。")
                return;
            }
        }
        // if($('#type12').val() == "013" && !$('#bussFinBondRepInfoDto_bankSecKzzq').val()){
        // 	Joyin.error("是否商业银行可转债数据不能为空。")
        //     return;
        // }
        // if($('#repAssetType').val() == "1110" && !$('#bussFinBondRepInfoDto_bankSecZxzq').val()){
        // 	Joyin.error("是否专项债券数据不能为空。")
        //     return;
        // }
        var text = $('#isRight').next().find('em').text();
        if(text==='否'){
            $('#isRight').val('N');
        }else{
            $('#isRight').val('Y');
        }
        var repAssetType = $('#repAssetType').val();
        //update by zhangnian 20190920 end
        var  couponSpecies = getInterestFactorElementById('couponSpecies').val();
        var object = $.serializeToObject('#submitForm', true);
        // if(object.tradePlat){
        //     object.tradePlat = object.tradePlat.toString();
        // }
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.finCashCalcRuleList = finCashCalcRuleList;
        //update by zhangnian 20200110 start
        //新建 快录提交时  清空主键  避免原标的的多于check
        if(isAutoCoupon&&pageModel==PAGE_MODEL_NEW){
            object.finprodId = "";
        }
        //update by zhangnian 20200110 end
        if (object.tradeMarketId != null) {
			object.finprodMarketId = object.finprodMarketId + object.tradeMarketId;
		}else {
			object.finprodMarketId = object.finprodMarketId + AssetCommon.setTradeMarketId(object.finprodMarketId);
		}
      //文件信息
        if(FileCommon){
            object.fileArray = FileCommon.getUploadFileInfo();
        }
        // 付息计划
        if(FinPayInterestStdInfo){
            object.finPayInterestStdInfoDto.couponSpecies = couponSpecies;
            object.finPayInterestStdInfoDto.finCashPlanList = FinPayInterestStdInfo.getAllRows(); 
        }
        // 还本计划
        if (object.bussFinRepaymentDto){
            object.bussFinRepaymentDto.vdate = $('#vdate').val();
            object.bussFinRepaymentDto.mdate = $('#mdate').val();
        }

        if(BussFinRepayment){
            object.bussFinRepaymentDto.finCashPlanList = BussFinRepayment.getAllRows();
        }
        // 行权计划
        object.busiFinExercisePlanDto = {};
        // 行权计划列表有值且是否行权选择是的场合，才保存行权计划
        if(BusiFinExercisePlan && object.isRight == YES_OR_NO_Y){
            object.busiFinExercisePlanDto.finExercisePlanList = BusiFinExercisePlan.getAllRows();
            object.busiFinExercisePlanDto.finCashPlanList = FinPayInterestStdInfo.getAllRows();// 行权计划业务验证需要支付计划
        }
        //评级信息
        object.busiFinBondRatingInfoDto = {};
        if(BusiFinBondRatingInfo){
            object.busiFinBondRatingInfoDto.mstBondCreditRatingList = BusiFinBondRatingInfo.getAllRows();  
        }
        // 若系统参数ASSET_INFO_CHB_HIDE为隐藏中债信息，则不进行赋值
        if(ASSET_INFO_CHB_HIDE != '02'){

            if (object.bussFinBondRepInfoDto){
                //登记托管机构
                object.bussFinBondRepInfoDto.repRegistOrg = $("#registOrg").val();
                //债券报送信息
                object.bussFinBondRepInfoDto.finprodType2 = FINPROD_TYPE_TG_F01;
                object.bussFinBondRepInfoDto.repAssetType = repAssetType;
                object.bussFinBondRepInfoDto.tradeMarket = $('#tradeMarket').val();
            }
	        if (object.bussRepEquityInfoDto){
                //权益类中债报送
                object.bussRepEquityInfoDto.repAssetType = repAssetType;

            }

	        
        }
        if (object.finPayInterestStdInfoDto){
            object.finPayInterestStdInfoDto.mdate = $('#mdate').val();
        }

        Joyin.ajaxJson(basePath + 'invest/asset/bond/FinBondTargetAsset002/check', object, function(
                result) {
            //息票品种为贴现、零息时不作付息计划 、还本计划check
            if(couponSpecies==COUPON_SPECIES_02||couponSpecies==COUPON_SPECIES_03){
                if(isTransInAsset){
                    Joyin.confirm(JoyinUtil.getMessage('FIN_ASSET_W_0002'),function(){
                            finalSava(object,operateType,result);
                        });
                 }else{
                     finalSava(object,operateType,result);
                 }
            }else{
                if(isTransInAsset){
                    Joyin.confirm(JoyinUtil.getMessage('FIN_ASSET_W_0002'),function(){
                        if (FinPayInterestStdInfo && FinPayInterestStdInfo.comfirmCheck()&&FinPayInterestStdInfo.check()&&BussFinRepayment.isComfirmCheck()) {
                            finalSava(object,operateType,result);
                        }
                    })
                }else{
                    if (FinPayInterestStdInfo && FinPayInterestStdInfo.comfirmCheck()&&FinPayInterestStdInfo.check()&&BussFinRepayment.isComfirmCheck()) {
                        finalSava(object,operateType,result);
                    }
                }
            }
        });
    }
    
    /**
     * 最终提交
     */
    var finalSava = function(object,operateType,result){
        Joyin.ajaxJson(basePath + 'invest/asset/bond/FinBondTargetAsset002/isSameMdateAndVdate', {
            mdate: object.mdate,
            vdate: object.vdate,
            oFinprodId: object.oFinprodId
        },function (isSameMdateAndVdate){
            Joyin.ajaxJson(basePath + 'invest/asset/common/isHoliday', {mdate:object.mdate,calendarId:object.calendarId}, function(isHoliday){
                if(isSameMdateAndVdate && isHoliday){
                    Joyin.confirm("与关联的一级资产起息到期日不一致，请确认是否继续提交", function () {
                        Joyin.confirm("到期日为节假日，是否确认提交", function () {
                            if (operateType === 'save') {
                                WorkFlow.doSaveList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                                    object, "FinBondTargetAsset002_btn_save");
                            } else {
                                WorkFlow.doSubmitList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                                    object, "FinBondTargetAsset002_btn_submit");
                            }
                        })

                    })
                }else if(isHoliday){
                    Joyin.confirm("到期日为节假日，是否确认提交", function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                                object, "FinBondTargetAsset002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                                object, "FinBondTargetAsset002_btn_submit");
                        }
                    })
                }else if(isSameMdateAndVdate){
                    Joyin.confirm("与关联的一级资产起息到期日不一致，请确认是否继续提交", function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                                object, "FinBondTargetAsset002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                                object, "FinBondTargetAsset002_btn_submit");
                        }
                    })
                }else{
                    if (operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                            object, "FinBondTargetAsset002_btn_save");
                    } else {
                        WorkFlow.doSubmitList(basePath + 'invest/asset/bond/FinBondTargetAsset002/save',
                            object, "FinBondTargetAsset002_btn_submit");
                    }
                }
            })
    })

    }
    /**
     * 是否含权债change事件
     */
    var isRightChange = function(){
        var isRight = $('#isRight')[0].checked;
        if(isRight){
            $('#busiFinExercisePlanDto_div').show();
        }else{
            $('#busiFinExercisePlanDto_div').hide();
        }
    }

//    add by zoujiamin 20210120 TISHKBS-1799  start
        /**
         * 资产四级分类change事件
         */
    var type11Change = function(){
         console.log("值",$('#type11').val())
         console.log("conFinprodMarketId值",$('#conFinprodMarketId').val())
         if($('#type11').val()=='013'||$('#type11').val()=='011') {
            if($('#conFinprodMarketId').val() == ''){
                console.log("四级分类是可转债且没有转债代码")
                $('#conFinprodMarketId').prop('required', false);
                $('#conShareId').prop('required', false).trigger('change');
                $('#conVdate').prop('required', false).trigger('change');
                $('#conPrice').prop('required', false).trigger('change');
                $('#conPrent').prop('required', false).trigger('change');
                $('#resalePrice').prop('required', false).trigger('change');
                $('#redeemPrice').prop('required', false).trigger('change');
            } else {
                console.log("四级分类是可转债且有转债代码")
                $('#conFinprodMarketId').prop('required', true);
                $('#conShareId').prop('required', true).trigger('change');
                $('#conVdate').prop('required', true).trigger('change');
                $('#conPrice').prop('required', true).trigger('change');
                $('#conPrent').prop('required', true).trigger('change');
                $('#resalePrice').prop('required', true).trigger('change');
                $('#redeemPrice').prop('required', true).trigger('change');
            }
         } else{
            console.log("四级分类不是可转债")
            $('#conFinprodMarketId').prop('required', true);
            $('#conShareId').prop('required', true).trigger('change');
            $('#conVdate').prop('required', true).trigger('change');
            $('#conPrice').prop('required', true).trigger('change');
            $('#conPrent').prop('required', true).trigger('change');
            $('#resalePrice').prop('required', true).trigger('change');
            $('#redeemPrice').prop('required', true).trigger('change');
         }
         star()
    }

//    add by zoujiamin 20210120 TISHKBS-1799  end




    /**
     * 计息要素画面显示/隐藏
     */
    var showFinInterestFactorDiv = function(){
        var obj = $('#finInterestFactorDto_div');
        getInterestFactorElementById('investRate','div').hide();
        getInterestFactorElementById('loadRate','div').hide();
        getInterestFactorElementById('referRate','div').hide();
        getInterestFactorElementById('buyOutRate','div').hide();
        $('#calRateType_div').hide();
    }
    
    /*var issuerChange = function(){
        var customerId = $('#issuer').val();
        AssetCommon.getRepInfoByIssuer(customerId);
    }*/

    var resetForm = function(id,value,pageId){
        //update by zhangnian 20191112 start
        //可转债信息
        if(id == 'finProductConvertible'){
            $.each(value, function(id,value){
                resetForm(id,value,'');
            });
        }else
        //update by zhangnian 20191112 end
    	//计息 付息 还本
    	if (id == 'finInterestFactorDto') {
			$.each(value, function(id,value){
				resetForm(id,value,'finInterestFactorDto');
			});
		}else if(id == 'finPayInterestStdInfoDto'){
			$.each(value, function(id,value){
				resetForm(id,value,'finPayInterestStdInfoDto');
			});
		}else{
			var element;
	    	if(pageId != '' && pageId != undefined){
				id = pageId + '_' + id;
			}
	    	if($('#'+id).size() > 0){
	    		element = $('#'+id);
	    	}else{
	    		element = $('input[name=' + id + ']');
	    	}
	    	
	    	//下拉框特殊处理
	    	if (element.hasClass('input-select')) {
	    		element.val(value).trigger('change');
			}else if(element.hasClass('input-checkbox')){
				//单选按钮特殊处理
	    		element.val(value).trigger('change');
	    	}else if ($('#' + id).attr('lay-skin') === 'switch') {
	    		//switch 特殊处理
				$('#' + id).prop('checked', false);
				// 是否,有无
				if (value === 'Y' || value === '01') {
					$('#' + id).prop('checked', true);
				}
	    	}else{
	    		element.val(value);
	    	}
		}
    	
    }
    	 
	    var setData = function(id, value ,pageId) {
            //update by zhangnian 20191112 start
            //可转债信息
            if(id == 'finProductConvertible'){
                $.each(value, function(id, value) {
                    setData(id, value , '');
                });
            }else
            //update by zhangnian 20191112 end
			// 计息
			if (id == 'finInterestFactorDto' ) {
				$.each(value, function(id, value) {
                    if (id == 'intType'){
                        id = 'rateType';
                    }
                    // 债券新建新增快捷录入功能 add by renping 20191010 start
                    else if (id == 'highestRate'){
                    	id = 'rateMax';
                    }
                    else if (id == 'lowestRate'){
                    	id = 'rateMin';
                    }
                    else if (id == 'benchmarkId'){
                    	id = 'indexId';
                    }
                    // 债券新建新增快捷录入功能 add by renping 20191010 end
					setData(id, value , 'finInterestFactorDto');
				});
			} else if (id == 'finPayInterestStdInfoDto') {
				// 付息
				$.each(value, function(id, value) {		
				    if (id == 'firstPayDate' && $('#finPayInterestStdInfoDto_calcRule').val()=='01') {
				        value = null;
                    }
                        setData(id, value , 'finPayInterestStdInfoDto');					
				});
				// 表格
				$('#generationPlanTable').data('joyintable').reload({
					data : value.finCashPlanList,
					url : null
				});
			} else if (id == 'bussFinRepaymentDto') {
				// 还本
				// 表格
				$('#bussFinRepaymentTable').data('joyintable').reload({
					data : value.finCashPlanList,
					url : null
				});
			}else if (id == 'repAssetBond'){
			    //中债报送 bussFinBondRepInfoDto_repAssetType  bussFinBondRepInfoDto_repPartyType2
                $.each(value, function(id, value) {
                    setData(id, value , 'bussFinBondRepInfoDto');
                });
			    
            }else if (id == 'repAssetEquity'){
                //中债报送
                $.each(value, function(id, value) {
                    setData(id, value , 'bussRepEquityInfoDto');
                });
               // 中债报送 by 赵东东 2019.11.12 start
            }else if(id == 'repAssetOther'){
            	$.each(value, function(id, value) {
                    setData(id, value , 'bussFinBondRepInfoDto');
                });
                $.each(value, function(id, value) {
                    setData(id, value , 'bussRepEquityInfoDto');
                });
            }else if(id == 'repAssetDept'){
                $.each(value, function(id, value) {
                    setData(id, value , 'bussFinBondRepInfoDto');
                });
                $.each(value, function(id, value) {
                    setData(id, value , 'bussRepEquityInfoDto');
                });
                // 中债报送 by 赵东东 2019.11.12 end
            }else if (id == 'repAssetEquity'){
                //中债报送
                $.each(value, function(id, value) {
                    setData(id, value , 'bussRepEquityInfoDto');
                });

            }else if (id == 'isRight') {
                if (value === 'Y' || value === '01') {
                    $('#isRight').prop('checked', true);
                }
			    //是否含权债
                isRightChange();
				 
			}else if (id == 'busiFinExercisePlanDto'){
               // 行权 表格
                $('#busiFinExercisePlanTable').data('joyintable').reload({
                    data : value.finExercisePlanList,
                    url : null
                });
            }
            else if(id == 'mstBondCreditRatingList'){
			    //评级  表格
                $('#busiFinBondRatingInfoTable').data('joyintable').reload({
                    data : value,
                    url : null
                });
            // 债券新建新增快捷录入功能 add by renping 20191010 start
            }
            else if(id == 'fileArray'){
            	// 附件
        		$("#fileUploader").data('uploader','');
        		$("#fileUploader").attr('data-data',value);
        		$("#fileUploader").data('data',value);
        		$("#fileUploader").uploadFile()
            // 债券新建新增快捷录入功能 add by renping 20191010 end
			    
            }else {
				// 基本信息
				var element;
		    	if(pageId != '' && pageId != undefined){
					id = pageId + '_' + id;
				}
		    	if($('#'+id).size() > 0){
		    		element = $('#'+id);
		    	}else{
		    		element = $('input[name=' + id + ']');
		    	}
		    	
		    	//下拉框特殊处理
		    	if (element.hasClass('input-select')) {
                    if (id == 'tradeMarket') {
                        AssetCommon.setTradeMarketId(value);
                        element.val(value).trigger('change');
                    // 债券新建新增快捷录入功能 modify by renping 20191010 start
                        // 修改快录中债报送丢失交易流通场所 by 赵东东 2019.11.22 start
                    }else if(id == 'bussFinBondRepInfoDto_repTradeMarket'){
                        //修改债券快录资产代码后缀名出错 by 赵东东 start
                    	//AssetCommon.setTradeMarketId(value);
                      //修改债券快录资产代码后缀名出错 by 赵东东 end
                    	element.val(value).trigger('change');
                    	// 修改快录中债报送丢失交易流通场所 by 赵东东 2019.11.22 end
                    }else if (id == 'issuer' || id == 'calendarId' || id == 'sponsor' || id == 'oFinprodId' || id == 'postOrg'||id=='conFinprodMarketId') {
                    // 债券新建新增快捷录入功能 modify by renping 20191010 end
                        //data-url 赋值要特殊处理
                        element.textSelect('setValue', value);
                    } else {
                        element.val(value).trigger('change');
                    }
                }else if (element.hasClass('input-dateSelect')) {
		    		//日期框特殊处理
		    		if (value){
                        element.val(new Date(value).format('yyyy-MM-dd'));
                    } 
				}else if(element.hasClass('input-checkbox')){
					//单选按钮特殊处理
		    		element.val(value).trigger('change');
		    	}else if ($('#' + id).attr('lay-skin') === 'switch') {
		    		//switch 特殊处理
					$('#' + id).prop('checked', false);
					$('#' + id).next().removeClass('layui-form-onswitch').trigger('change');
					$('#' + id).next().find("em").text('否');
					// 是否,有无
					if (value === 'Y' || value === '01') {
						$('#' + id).prop('checked', true);
						$('#' + id).next().addClass('layui-form-onswitch').trigger('change');
						$('#' + id).next().find("em").text('是');
					}
                    // 债券新建新增快捷录入功能 add by renping 20191010 start
					if(id == 'isCityBond'){
						isCityBondChange();
					}
                    // 债券新建新增快捷录入功能 add by renping 20191010 end
		    	}else{
		    		element.val(value);
		    	}
			}
	    }
    //从接口获取债券信息
    var finCashCalcRuleList = [];//加券利息
    //add by mashukai 20201124 JNZGEQUAT-57 行权计划理论行权日不对问题 start
    var baseFinProductBaseInfoList = [];
    //add by mashukai 20201124 JNZGEQUAT-57 行权计划理论行权日不对问题 end
    var getInterfBondInfo = function(){
        isAutoCoupon = true;
    	var finprodMarketId =  $('#finprodMarketId').val();
        //拆分市场代码       
        var arr  = finprodMarketId.split('.');
        if (arr.length == 2){
            finprodMarketId = arr[0];
            $('#finprodMarketId').val(finprodMarketId);
            $('#tradeMarketId').val('.' + arr[1]);
        } 
        if (finprodMarketId) {
    		Joyin.ajax(basePath + 'invest/asset/bond/FinBondTargetAsset002/getInterfBondDto?finprodMarketId=' 
   				 + finprodMarketId + '&tradeMarketId=' + $('#tradeMarketId').val(),'' , function(result){
    			if (result.data[0]) {
    				Joyin.error(result.data[0].message);
				} else {
                    //add by mashukai 20201124 JNZGEQUAT-57 wind利率调整数据未接到问题 start
                    baseFinProductBaseInfoList = result.data.baseFinProductBaseInfoList;
                    if(baseFinProductBaseInfoList != null && baseFinProductBaseInfoList[0].finExercisePlanList != null){
                        for(var i = 0 ;i< baseFinProductBaseInfoList[0].finExercisePlanList.length ;i++){
                            var finCashPlanList = result.data.baseFinProductBaseInfoList[0].finCashPlanList;
                            for(var j = 0;j<finCashPlanList.length;j++){
                                if(finCashPlanList[j].rangeYield != null && finCashPlanList[j].mdate != finCashPlanList[j].payDate &&
                                        finCashPlanList[j].mdate != baseFinProductBaseInfoList[0].finExercisePlanList[i].exerciseDate &&
                                            finCashPlanList[j].payDate == baseFinProductBaseInfoList[0].finExercisePlanList[i].actExerciseDate){
                                    baseFinProductBaseInfoList[0].finExercisePlanList[i].exerciseDate = finCashPlanList[j].mdate;
                                }
                            }
                        }
                        finCashCalcRuleList = baseFinProductBaseInfoList[0].finCashCalcRuleList;
                    }
                    //add by mashukai 20201124 JNZGEQUAT-57 wind利率调整数据未接到问题 end
                    //组件赋值 先清后赋
                    $.each(Joyin.curfromData['FinBondTargetAsset002'],function(id, value){
                        resetForm(id, value.odata, '');
                    });
                    
                    $('#repAssetType').val(result.data.repAssetType).trigger('change');

                    $.each(result.data, function(id, value) {
                        setData(id, value, '');
                    });

                    //add by mashukai 20201124 JNZGEQUAT-57 行权计划理论行权日不对问题 start
                    if(baseFinProductBaseInfoList != null && baseFinProductBaseInfoList[0].finExercisePlanList != null){
                    	$('#busiFinExercisePlanTable').data('joyintable').reload({
                            data : baseFinProductBaseInfoList[0].finExercisePlanList,
                            url : null
                        });
                    }
                    //add by mashukai 20201124 JNZGEQUAT-57 行权计划理论行权日不对问题 end

                    //评级信息
                    $.each(result.data.baseFinProductBaseInfoList[0], function(id, value) {
                    	if (id ==='mstBondCreditRatingList') {
                    		setData(id, value, '');
                    	}
                    });

                    if(result.data.baseFinProductBaseInfoList[0].finProductAdd){
                    	// 是否次级债
	                    var isSecBond = result.data.baseFinProductBaseInfoList[0].finProductAdd.isSecBond;
	                    if (isSecBond == 'Y' || isSecBond == '01') {
	                        $('#isSecBond').prop('checked', true);
	                        $('#isSecBond').next().addClass('layui-form-onswitch').trigger('change');
	                        $('#isSecBond').next().find("em").text('是');
	                    } else {
	                        $('#isSecBond').prop('checked', false);
	                        $('#isSecBond').next().removeClass('layui-form-onswitch').trigger('change');
	                        $('#isSecBond').next().find("em").text('否');
	                    }
	                    
	                    // 是否城投债
	                    var isCityBond = result.data.baseFinProductBaseInfoList[0].finProductAdd.isCityBond;
	                    if (isCityBond == 'Y' || isCityBond == '01') {
	                        $('#isCityBond').prop('checked', true);
	                        $('#isCityBond').next().addClass('layui-form-onswitch').trigger('change');
	                        $('#isCityBond').next().find("em").text('是');
	                    } else {
	                        $('#isCityBond').prop('checked', false);
	                        $('#isCityBond').next().removeClass('layui-form-onswitch').trigger('change');
	                        $('#isCityBond').next().find("em").text('否');
	                    }
	                    isCityBondChange();
                    }
                    if(result.data.registOrg != "99" && result.data.registOrg != "06"){
                        $('#bussFinBondRepInfoDto_repRegistorgComment').val('');
                    }
				}
                conPriceChange();
    			  layui.form.render();

   		 });
		} else {
			Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038',JoyinUtil.getLabel('FinBondTargetAsset002.finprodId')));
		}
    }
    var issuerChange = function(){
        var customerId = $('#issuer').val();
        if(customerId){
            AssetCommon.getRepInfoByIssuer(customerId);
            if (typeof pageModel !== 'undefined' && pageModel !== PAGE_MODEL_REFERENCE){
                Joyin.ajax(basePath + 'invest/asset/bond/FinBondTargetAsset002/getFinBondRatingDto?issuer='+customerId, '' ,function(result){
                    if (result) {
                        $('#busiFinBondRatingInfoTable').data('joyintable').reload({
                            data : result,
                            url : null
                        });
                    }
                });
            }
        }
    }
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            //计息要素展示/隐藏
            showFinInterestFactorDiv();
            isRightChange();
            //息票品种
            if(FinInterestFactor){
                getInterestFactorElementById('couponSpecies').change(FinInterestFactor.handleCouponSpecies).trigger("change");
            }
          //该资产存在交易
            if(isTransInAsset){
                $('#vdate').attr('disabled',true).trigger('change');
                /*$('#mdate').attr('disabled',true).trigger('change');*/
                $('#ccy').attr('disabled',true).trigger('change');
                $('#tradeMarket').attr('disabled',true).trigger('change');
                $('#isRight').attr('disabled',true).trigger('change');
                $('#tradeMarket').attr('disabled',true).trigger('change');
                getInterestFactorElementById('rate').attr('disabled',true).trigger('change');
                getInterestFactorElementById('couponSpecies').attr('disabled',true).trigger('change');
                getInterestFactorElementById('indexId').attr('disabled',true).trigger('change');
                getInterestFactorElementById('coefficient').attr('disabled',true).trigger('change');
                getInterestFactorElementById('spreadRate').attr('disabled',true).trigger('change');
                //登记托管机构
                $('#bussFinBondRepInfoDto_repRegistOrg').attr('disabled',true).trigger('change');
                $('#registOrg').attr('disabled', true).trigger('change');
                //置灰付息计划生成计划按钮
                /*$('#finPayInterestStdInfoDto_btn_generationPlan').off('click').prop('disabled', true).addClass('layui-btn-disabled');*/
            }
           if(isHqTrade){
        	   $('#isRight').attr('disabled',true).trigger('change');
           }
            showReportInfo();
           /*$('#bussFinBondRepInfoDto_repAssetType_div').show();*/
        }
    }
    var init = function(){
    	
       //资产代码拆分
       var finprodMarketId =  $('#finprodMarketId').val();
       if (finprodMarketId){
           $('#finprodMarketId').val(finprodMarketId.split('.')[0]);
           $('#tradeMarketId').val('.' + finprodMarketId.split('.')[1]);
       } 
    	//如果授信模式=='02'，则显示批复号和实际授信主体
    	if(CE_MODEL == '02'){
    		$('#actualCreditProvidDiv').show();
    		$('#pflshDiv').show();
    	}
    	else{
    		$('#actualCreditProvidDiv').hide();
    		$('#pflshDiv').hide();
    	}
    	var hapCreditAmt = $("#hapCreditAmt").val();
    	if(hapCreditAmt && hapCreditAmt>0){
    		$("#pflsh").attr("disabled",true).trigger('change');
    	}
    	if($("#creditFlag").val() == 'Y'){
    		$("#actualCreditProvid").attr("required", true).trigger('change');
    	}
    	if($("#creditFlag").val() != 'Y'){
    		$("#actualCreditProvid").attr("required", false).trigger('change');
    	}
        isCityBondChange();
        isApproveShow();
    	star();
    	//    add by zoujiamin 20210120 TISHKBS-1799  start
    	type11Change();
    	//    add by zoujiamin 20210120 TISHKBS-1799  end
    }

    var isCityBondChange = function(){
        var isCityBondChange = $('#isCityBond')[0].checked;
        if (!isCityBondChange) {
            $("#cityBondLevDiv").hide();
        }else {
            $("#cityBondLevDiv").show();
        }
    };

    var isApproveShow = function () {
        if($('#tradeMarket').val() == '03' || $('#tradeMarket').val() == '04'){
            $('#isNetPrice_div').show();
            //$('#isApproveMarket_div').show();
        }else {
            $('#isNetPrice_div').hide();
            //$('#isApproveMarket_div').hide();
        }
    }

    var isApproveMarketChange = function () {
        var isPpb = $('#isPPB').val();
        var isPpbChange = $('#isPPB')[0].checked;
        if (!isPpbChange) {
            isPpb = "N";
        }else {
            isPpb = "Y";
        }

        // var $isApproveMarket = $("#tradePlat");
        // var isApproveMarket = $isApproveMarket.val();
        // $isApproveMarket.updateChosenArr({
        //     item: [''],
        //     chosen: isApproveMarket
        // })
        var tradeMarket = $('#tradeMarket').val();
        // 只有交易所债券才展示“是否私募债”字段
        if(tradeMarket == '03' || tradeMarket == '04'){
            $('#isPpb_div').show();
        }else{
            $('#isPpb_div').hide();
        }
        var type9 = $('#type9').val();
        var finprodMarketId = $('#finprodMarketId').val();
        if (finprodMarketId && isPpb && type9 && tradeMarket) {
            Joyin.ajax(basePath + 'invest/asset/bond/FinBondTargetAsset002/getIsNetSett?finprodMarketId='
                + finprodMarketId + '&tradeMarket=' + tradeMarket + '&isPpb=' + isPpb
                + '&type9=' + type9, '', function (result) {
                $('#isNetSett').val(result.data.isNetSett);
                //var tradePlat = result.data.tradePlat;
                //if($('#tradeMarket').val() == '03' && result.data.isNetSett == "N" ){
//                    $("#tradePlat").select2();
                    //$("#tradePlat").val(['03']).trigger("change");
                    // var $isApproveMarket = $("#tradePlat");
                    // var isApproveMarket = $isApproveMarket.val();
                    // $isApproveMarket.updateChosenArr({
                    //     item: ['03'],
                    //     chosen: isApproveMarket
                    // });
               // }else if($('#tradeMarket').val() == '03' && result.data.isNetSett == "Y" ) {
//                    $("#tradePlat").select2();
                    //$("#tradePlat").val(['02','03','04']).trigger("change");
                    //$("#tradePlat").val(['02','03','04']).trigger("change");
                    // var $isApproveMarket = $("#tradePlat");
                    // var isApproveMarket = $isApproveMarket.val();
                    // $isApproveMarket.updateChosenArr({
                    //     item: ['02','03','04'],
                    //     chosen: isApproveMarket
                    // });
               // }
               // else if($('#tradeMarket').val() == '04' && result.data.isNetSett == "N" ){
//                    $("#tradePlat").select2();
                    //$("#tradePlat").val(['05']).trigger("change");
                    // var $isApproveMarket = $("#tradePlat");
                    // var isApproveMarket = $isApproveMarket.val();
                    // $isApproveMarket.updateChosenArr({
                    //     item: ['05'],
                    //     chosen: isApproveMarket
                    // });
               // }else if($('#tradeMarket').val() == '04' && result.data.isNetSett == "Y" ) {
                    //$("#tradePlat").select2();
                    //$("#tradePlat").val(['02', '05']).trigger("change");
                    // var $isApproveMarket = $("#tradePlat");
                    // var isApproveMarket = $isApproveMarket.val();
                    // $isApproveMarket.updateChosenArr({
                    //     item: ['02', '05'],
                    //     chosen: isApproveMarket
                    // })
                //}else {
                    //$("#tradePlat").select2();
                    //$("#tradePlat").val([]).trigger("change");
                    // var $isApproveMarket = $("#tradePlat");
                    // var isApproveMarket = $isApproveMarket.val();
                    // $isApproveMarket.updateChosenArr({
                    //     item: [],
                    //     chosen: isApproveMarket
                    // })
                //}
                // if(tradePlat){
                //     tradePlat =  tradePlat.split(",");
                //     $("#tradePlat").val(tradePlat).trigger("change");
                // }
                // $('#tradePlat').trigger('change');
            });
        }
    };
    /**
     * 中债报送show/hide
     */
    var showReportInfo = function(){
        var repAssetType = $('#repAssetType').val();
        //权益类中债报送
        $('#bussRepEquityInfoDto_div').hide();
        //债券报送信息
        $('#bussFinBondRepInfoDto_div').hide();

        if(repAssetType!=""){
            if(repAssetType=="1301" || repAssetType=="1305"){
            	
                $('#bussRepEquityInfoDto_div').show();
                $('#bussRepEquityInfoDto_repSharesType').val(SVR_SHARES_TYPE_02);
                $('#bussRepEquityInfoDto_repSharesType').prop('disabled',true).trigger('change');
                
            	$('#bussRepEquityInfoDto_repBankSecPrefer_div').hide();
                $('#bussRepEquityInfoDto_repDeptEquity_div').hide();

                //展示权益类资产报送信息时  如果是优先股 则展示 是否商业银行优先股
                if($('#bussRepEquityInfoDto_repSharesType').val() === SVR_SHARES_TYPE_02){
                	$('#bussRepEquityInfoDto_repBankSecPrefer_div').show();
                	$('#repBankSecPrefer').attr('required',true);
                }
                
                if(repAssetType=="1305"){
                	
                    //股票一级市场  则显示  是否债转股
                    	$('#bussRepEquityInfoDto_repDeptEquity_div').show();
                    	$('#repDeptEquity').attr('required',true);
                    	$('#bussRepEquityInfoDto_repInvestStage').parent().parent().find('i.must').remove();
                }
                
            }else {
                //债券中债报送
                $('#bussFinBondRepInfoDto_div').show();

                $('#bussFinBondRepInfoDto_bankSecKzzq_div').hide();
            	$('#bussFinBondRepInfoDto_bankSecZxzq_div').hide();
            	//资产五级分类选择“可转债”时展示是否商业银行可转债
            	if($("#type12").val() == '013'){
            		$('#bussFinBondRepInfoDto_bankSecKzzq_div').show();
            	}
            	//当资产/负债类别选择“1110 地方政府债”时展示是否专项债券
            	  if(repAssetType=="1110"){
            		  $('#bussFinBondRepInfoDto_bankSecZxzq_div').show(); 
            	  }
//                // start   modify by wj 20200408
//            	$('#bussFinBondRepInfoDto_repFinancialComissue_div').hide();
//            	$('#bussFinBondRepInfoDto_repBankSecCapital_div').hide();
//            	$('#bussFinBondRepInfoDto_repBankSecPerpetual_div').hide();
//
//                 if(repAssetType=="2617"){
//                // 其他私募股权投资基金 则显示 是否由金融资产投资公司发行  是否商业银行二级资本债  是否商业银行永续债
//                	
//                	$('#bussFinBondRepInfoDto_repFinancialComissue_div').show();
//                	$('#repFinancialComissue').prop('required',true);
//                	$('#bussFinBondRepInfoDto_repBankSecCapital_div').show();
//                	$('#repBankSecCapital').prop('required',true);
//                	$('#bussFinBondRepInfoDto_repBankSecPerpetual_div').show();
//                	$('#repBankSecPerpetual').prop('required',true);
//                	
//                }
                
            }
            // add by jiangpeng on 20200731 显示行业 start
            $('#div02').show();
            // add by jiangpeng on 20200731 显示行业 end
        }
        $('#bussFinBondRepInfoDto_repSectype').parent().parent().find('i.must').remove();
        if(repAssetType === "1112"||repAssetType === "1113"||repAssetType === "1114"||repAssetType === "1115"||repAssetType === "1116"||repAssetType === "1117") {
            $('#bussFinBondRepInfoDto_repSectype').parent().parent().find('.layui-form-label').append('<i class="must">*</i>')
        }
    }

    var type4Change = function(){
        if($('#type4').val()==WINDTYPEONE_B14||$('#type4').val()==WINDTYPEONE_B15){
            $('#FinProductConvertible_div').show();
        }else{
            $('#FinProductConvertible_div').hide();
        }
    }

    var conPriceChange = function(){
        if($('#conPrice').val()!=""){
            $('#conPrent').val(JoyinMath.doDiv(100,$('#conPrice').val(),2));
        }
    }

    $(function() {
    	init();
        //修改  资产代码置灰
        if(pageModel!=PAGE_MODEL_NEW){
            $("#finprodMarketId").attr('disabled',true);
            //提交流程确认后，交易场所置灰不允许修改
            $("#tradeMarket").attr('disabled',true);
            $('.btn-save').hide();
        }
        // 只有交易所债券才展示“是否私募债”字段
        var tradeMarket = $('#tradeMarket').val();
        if(tradeMarket == '03' || tradeMarket == '04'){
            $('#isPpb_div').show();
        }else{
            $('#isPpb_div').hide();
        }
        //是否含权债change
        layui.form.on('switch(isRight)',function(){
            isRightChange();
        });
        // 绑定按钮事件
        $('#FinBondTargetAsset002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#FinBondTargetAsset002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        // $('#tradeMarket').change(function(){
		// 	AssetCommon.setTradeMarketId($('#tradeMarket').val());
		// });
        $('#issuer').change(issuerChange);

        $('#type11').change(type11Change);
	$('#tradeMarket,#type9,#isPPB,#finprodMarketId').change(function() {
            isApproveMarketChange()
        });

        $('#tradeMarket').change(function(){
			AssetCommon.setTradeMarketId($('#tradeMarket').val());
			if($('#tradeMarket').val() == '03' || $('#tradeMarket').val() == '04'){
                $('#isNetPrice_div').show();
                //$('#isApproveMarket_div').show();
            }else {
                $('#isNetPrice_div').hide();
                //$('#isApproveMarket_div').hide();
            };

            if($('#tradeMarket').val() == '01'){
                $('#calendarId').textSelect('setValue', '03');
            }else if($('#tradeMarket').val() == '03'){
                $('#calendarId').textSelect('setValue', '04');
            }else if($('#tradeMarket').val() == '04'){
                $('#calendarId').textSelect('setValue', '05');
            }else {
                $('#calendarId').empty()
            }

            // Joyin.ajaxJson(basePath + 'business/common/CommonSelect/selectCalendarId', null ,function(dataList){
            //     if(dataList){
            //         $("#calendarId").vzzal(JSON.stringify(dataList.root))
            //
            //         // if($('#tradeMarket').val() == '01'){
            //         //     $("#calendarId").select2();
            //         //     $("#calendarId").val(['03']).trigger("change");
            //         // }else if($('#tradeMarket').val() == '03'){
            //         //     $("#calendarId").select2();
            //         //     $("#calendarId").val(['04']).trigger("change");
            //         // }else if($('#tradeMarket').val() == '04'){
            //         //     $("#calendarId").select2();
            //         //     $("#calendarId").val(['05']).trigger("change");
            //         // }
            //     }
            // });



		});
        $('#magnifier').click(getInterfBondInfo);
        $('#mdate,#vdate').change(function(obj){
            obj.type = FINPROD_TYPE_TG_F01;
            AssetCommon.checkDate(obj);
        });
//        $("#registOrg").change(function () {
//            if($(this).val()=='04'){
//                $('#bussFinBondRepInfoDto_repRegistorgComment').val('中证机构间报价系统股份有限公司');
//            }else{
//                $('#bussFinBondRepInfoDto_repRegistorgComment').val('');
//            }
//        });
        $("#pflsh").change(function(){
            var str = '';
            if($('#pflsh').select2('data')[0].object){
              str = $('#pflsh').select2('data')[0].object.split(',');
            }

        	if(str != null && str.length==3){
        		$("#seqno").val(str[0]);
        		$("#creditMstId").val(str[1]);
        		$("#creditFlag").val(str[2]);
        	}
        	if(str[2] == 'Y'){
        		$("#actualCreditProvid").attr("required", true).trigger('change');
        	}
        	if(str[2] != 'Y'){
        		$("#actualCreditProvid").attr("required", false).trigger('change');
        	}
        	star();
        });

        /*type4 and type5's automatic match added by wangguiqi on 2019/7/23 -start*/
        $("#type9,#type10,#type11,#type12").change(function(){
        	if($("#type12").val() == CUSTOM_ASSET_TYPE_FIVE_009 || $("#type12").val() == CUSTOM_ASSET_TYPE_FIVE_158){
        		$('#isSecBond').next().addClass('layui-form-onswitch').trigger('change');
    	        $('#isSecBond').next().find("em").text('是');
        	}else{
        		$('#isSecBond').next().removeClass('layui-form-onswitch').trigger('change');
    	        $('#isSecBond').next().find("em").text('否');
        	}
            	window.BussAssetFiveType.doAssetFiveTypeChange(IS_SHOW_TYPE,FINPROD_TYPE_TG_F01,'type9','type10','type11','type12','type4','type5');
        });
        /*type4 and type5's automatic match added by wangguiqi on 2019/7/23 -end*/

        layui.form.on('switch(isCityBond)',function(){
            isCityBondChange();
        });

        layui.form.on('switch(isPPB)',function(){
            isApproveMarketChange();
        });
        //是否展示资产四五级分类
        /*if(IS_SHOW_TYPE == '01'){
        	$('#selector5').show();
        	$('#selector6').show();
        }else{
        	$('#selector5').hide();
        	$('#selector6').hide();
        }*/
        //正股代码change
        $('#conFinprodMarketId').change(function () {
            var str = $('#conFinprodMarketId').select2('data')[0];
            if (str){
                $('#conFinprodId').val(str.object);
                $('#conFinprodName').textSelect('setValue', $('#conFinprodMarketId').val());
            }else{
                $('#conFinprodId').val("");
                $('#conFinprodName').textSelect('setValue', "");
            }

//    add by zoujiamin 20210120 TISHKBS-1799  start
            if($('#type11').val()=='013'||$('#type11').val()=='011') {
                        if($('#conFinprodMarketId').val() == ''){
                            console.log("四级分类是可转债且没有转债代码")
                            $('#conFinprodName').textSelect('setValue', "");
                            $('#conFinprodMarketId').prop('required', false);
                            $('#conShareId').prop('required', false).trigger('change');
                            $('#conVdate').prop('required', false).trigger('change');
                            $('#conPrice').prop('required', false).trigger('change');
                            $('#conPrent').prop('required', false).trigger('change');
                            $('#resalePrice').prop('required', false).trigger('change');
                            $('#redeemPrice').prop('required', false).trigger('change');
                        } else {
                            console.log("四级分类是可转债且有转债代码")
                            $('#conFinprodMarketId').prop('required', true);
                            $('#conShareId').prop('required', true).trigger('change');
                            $('#conVdate').prop('required', true).trigger('change');
                            $('#conPrice').prop('required', true).trigger('change');
                            $('#conPrent').prop('required', true).trigger('change');
                            $('#resalePrice').prop('required', true).trigger('change');
                            $('#redeemPrice').prop('required', true).trigger('change');
                        }
                     } else{
                        console.log("四级分类不是可转债")
                        $('#conFinprodMarketId').prop('required', true);
                        $('#conShareId').prop('required', true).trigger('change');
                        $('#conVdate').prop('required', true).trigger('change');
                        $('#conPrice').prop('required', true).trigger('change');
                        $('#conPrent').prop('required', true).trigger('change');
                        $('#resalePrice').prop('required', true).trigger('change');
                        $('#redeemPrice').prop('required', true).trigger('change');
                     }
            star()
//    add by zoujiamin 20210120 TISHKBS-1799  end
        })
        //转股价格
        $('#conPrice').change(function () {
            conPriceChange();
        })
        $('#type4').change(function () {
            type4Change();
        })
        type4Change();
        conPriceChange();
        //负债类别change事件
        $('#repAssetType').change(function(){
            showReportInfo();
        });
    });
});
