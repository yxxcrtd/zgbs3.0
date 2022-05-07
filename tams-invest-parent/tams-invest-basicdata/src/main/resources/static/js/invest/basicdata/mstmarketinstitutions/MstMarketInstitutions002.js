$(function() {
	
    function saveOrSubmit(operateType) {
    	var mstBondCreditRating = table.getAllRows();
        var object = $.serializeToObject('#submitForm');
        object.pageModel = pageModel;
        object.operateType = operateType;
        for(var i=0; i<mstBondCreditRating.length; i++){
        	if(typeof mstBondCreditRating[i].noticeDate != 'undefined' )
        		mstBondCreditRating[i].noticeDate=new Date(mstBondCreditRating[i].noticeDate).format();
        	if(typeof mstBondCreditRating[i].gradeDate != 'undefined' && mstBondCreditRating[i].gradeDate !=null)  // update by zoujiamin  TISHKBS-2137  增加非空判断
        		mstBondCreditRating[i].gradeDate=new Date(mstBondCreditRating[i].gradeDate).format();
        	mstBondCreditRating[i].createTime=new Date().format();
        	mstBondCreditRating[i].updateTime=new Date().format();
        }
        object.mstBondCreditRating = mstBondCreditRating;
        Joyin.ajaxJson(basePath + 'invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/save', object, "MstMarketInstitutions002_btn_save",null,function () {
						debugger;
                    	if (CLOSE_FUNC_NAME && typeof parent[CLOSE_FUNC_NAME] === 'function') {
							parent[CLOSE_FUNC_NAME]();
						}
					});
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/save', object, "MstMarketInstitutions002_btn_submit",null,function () {
						if (CLOSE_FUNC_NAME && typeof parent[CLOSE_FUNC_NAME] === 'function') {
							parent[CLOSE_FUNC_NAME]();
						}
					});
                }
            });
        });
    };
    
    /**
     * 页面加载初始化
     */
	var options = {
		page: true,
		ajax: {
	        url: JoyinUtil.getRealPath('invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/findPage'),
	        type: 'POST',
	        data: function() {
	            return {customerId: $('#customerId').val(),
	            		workflowId: $('#workflowId').val()
	            	};
	        }
	    },
        checkbox: false,
        addbtn: true,
        searchOnInit: true,
        cols:[
            {field:'noticeDate', title: 'MstMarketInstitutions002.noticeDate', edit:true, type:'date', sortable:false},
            {field:'gradeDate', title: 'MstMarketInstitutions002.gradeDate', edit:true, type:'date', sortable:false},
            {field:'shortLongTerm', title: 'MstMarketInstitutions002.gradeType', edit:true, editOption:{type:'select', result: LONG_SHORT_TERM}, sortable:false},
            {field:'gradeResult', title: 'MstMarketInstitutions002.gradeResult', edit:true, editOption:{type:'select', result: MAIN_GRADE_RESULT, autosort:false}, sortable:false},
            {field:'gradeExpectation', title: 'MstMarketInstitutions002.gradeExpectation', edit:true, editOption:{type:'select', result: RATING_OUTLOOK}, sortable:false},
            {field:'directionChange', title: 'MstMarketInstitutions002.directionChange', edit:true , editOption:{type:'select', result: CHANGE_DIRECTION}, sortable:false},
            //{field:'gradeOrgId', title: 'MstMarketInstitutions002.gradeOrgId', edit:true , editOption:{type:'select', showField:'text', url: JoyinUtil.getRealPath('/invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/getGradeOrgId')}, sortable:false},
            {field:'gradeOrgId', title: 'MstMarketInstitutions002.gradeOrgId', edit:true , editOption:{type:'select', result:GRADE_PARTY}, sortable:false},
            {field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt']}
        ]
    };
    
    /*
     * 绑定表格事件
     */
	var table = $("#dataTable").joyintable(options);
    /**
     *绑定删除事件
    */
    table.bind('click', '.delt', function(data, index) {
        table.removeRow(data);
    });
    /**
     * 画面加载完成
     */
    // 绑定按钮事件
    $('#MstMarketInstitutions002_btn_save').click(function() {
        saveOrSubmit('save');
    });
    $('#MstMarketInstitutions002_btn_submit').click(function() {
        saveOrSubmit('submit');
    });

    if(CLOSE_FUNC_NAME) {//如果是从其他页面打开则隐藏关闭按钮
    	$("#MstMarketInstitutions002_btn_cancel").hide();
	}
    
    /**
     * BECIF
     */
    $('#linkId2').change(function(){
    	Joyin.ajax(basePath + 'invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/selectInterfCmsInstitution',{becif:$('#linkId2').val()},
    	          function(result) {
    				if(result != null && result != ''){
    					Joyin.confirm(JoyinUtil.getMessage("MST_INFO_E_0002"),function() {
    						$('#customerId').val(result.customerId);
    						$('#customerName').val(result.institutionName);
    						$('#partyType3').val(result.partyType3).trigger('change');
    						$('#partyType2').val(result.partyType2).trigger('change');
    						$('#partyType1').val(result.partyType1).trigger('change');
    						$('#industry').val(result.repIndustry).trigger('change');
    						$('#industry2').val(result.repInddustryLevel2).trigger('change');
    						$('#insitituteCode').val(result.insitituteCode);
    						$('#isGov').val(result.isGov).trigger('change');
    						//$('#mainCapital').val(result.insitituteCode);核心资本
    						$('#registerCapital').val(result.registeCapital);
    						$('#province').val(result.province).trigger('change');
    						$('#city').val(result.city).trigger('change');
    						$('#county').val(result.county).trigger('change');
    						$('#holdingType').val(result.holdingType).trigger('change');
    						$('#customerObjType').val(result.customerObjType).trigger('change');
    						$('#isListedCompany').val(result.isListedCompany).trigger('change');
    						$('#isGroupCustomer').val(result.isGroupCustomer).trigger('change');
    						$('#parentCustomerName').val(result.parentCustomerName);
    						$('#isPrivate').val(result.isPrivate).trigger('change');
    						$('#isEscapeDept').val(result.isEscapeDept).trigger('change');
    						$('#creditType').val(result.creditType).trigger('change');
    						$('#isCreditRelatedTrd').val(result.isCreditRelatedTrd).trigger('change');
    						$('#structAdjustType').val(result.structAdjustType).trigger('change');
    						$('#indusUpgrateType').val(result.indusUpgrateType).trigger('change');
    						$('#busiScope').val(result.busiScope);
    						$('#mainBusi').val(result.mainBusi);
      	                },function(){
      	                	return false;
            			}
    					);
    				}
    		});
    })
    /**
     * wind编码
     */
    $('#linkId1').change(function(){
    	Joyin.ajax(basePath + 'invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002/selectInterfCompintroduction',{objectId:$('#linkId1').val()},
  	          function(result) {
  				if(result != null && result != ''){
  					Joyin.confirm(JoyinUtil.getMessage("MST_INFO_E_0002"),function() {
  						$('#customerId').val(result.compId);
  						$('#customerName').val(result.compName);
  						//$('#partyType3').val(result.partyType3).trigger('change');
  						$('#partyType2').val(result.compProperty).trigger('change');
  						//$('#partyType1').val(result.partyType1).trigger('change');
  						//$('#industry').val(result.repIndustry).trigger('change');
  						//$('#industry2').val(result.repInddustryLevel2).trigger('change');
  						//$('#insitituteCode').val(result.insitituteCode);
  						//$('#isGov').val(result.isGov).trigger('change');
  						//$('#mainCapital').val(result.insitituteCode);核心资本
  						$('#registerCapital').val(result.regcapital);
  						$('#province').val(result.province).trigger('change');
  						$('#city').val(result.city).trigger('change');
  						//$('#county').val(result.county).trigger('change');
  						//$('#holdingType').val(result.holdingType).trigger('change');
  						//$('#customerObjType').val(result.customerObjType).trigger('change');
  						//$('#isListedCompany').val(result.isListedCompany).trigger('change');
  						//$('#isGroupCustomer').val(result.isGroupCustomer).trigger('change');
  						//$('#parentCustomerName').val(result.parentCustomerName);
  						//$('#isPrivate').val(result.isPrivate).trigger('change');
  						//$('#isEscapeDept').val(result.isEscapeDept).trigger('change');
  						//$('#creditType').val(result.creditType).trigger('change');
  						//$('#isCreditRelatedTrd').val(result.isCreditRelatedTrd).trigger('change');
  						//$('#structAdjustType').val(result.structAdjustType).trigger('change');
  						//$('#indusUpgrateType').val(result.indusUpgrateType).trigger('change');
  						$('#busiScope').val(result.businessScope);
  						//$('#mainBusi').val(result.mainBusi);
    	                },function(){
    	                	return false;
          			}
  					);
  				}
  		});
    })
}());