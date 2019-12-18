var tyControls = function () {
	var ty,
	    cusTerTree=null,
	    cid = 1,
	    oid = 1,
	    _hisPathIndex = 0,
	    _hisPath = [],
	    search = {
	        id:null,
	        type:null,
	        path:null,
	    }
	/**
	 * 客户控件
	 */
	function ownCustomer(selector) {
			$target = $(selector),
			childs = 
				   $('<input type="button" id ="searchCust" class="searchCust" placeholder="请选择客户" value="请选择客户">'+
					'<div id = "searchTree" class="searchTree" style="display:none;">'+									
						'<div class="selDevTree  tyDrop">'+											
							'<div class="tree-box-head">'+												
								'<!-- 这里是搜索框加下拉框模块-->'+												
				                '<div class="tree-box-inputbox">'+	
				                    '<input class="serchInput" placeholder="客户名称" id="serchInput">'+  
				                    '<div class="serchDown" id="serchResult"></div>'+					
				                '</div>'+
							'</div>'+
				       		'<div class="trBoxDw">'+
				                '<ul id="cusTerTree"></ul>'+
				            '</div>'+
				       	'</div>'+
				    '</div>');
			$target.append(childs);
			$target.css({"width":"205px","position":"relative"});
			$(".searchTree").css("height","280px")
			$(".searchCust").css("height","25px");
			customerInit($target);
			customerBind($target);
	};
	/**
	 * 设备号/名称，所属客户控件
	 */
	function equipOrCust(selector){
			$target = $(selector),
			childs = 
				'<div id = "equipIdOrName" class="equipIdOrName">设备号/名称</div>'+
				'<div id = "customerOwn" class="customerOwn" style="display:none;">所属客户</div>'+
				'<input type="text" id = "searchText" class="searchText" placeholder="设备号（IMEI）/设备名称">'+
				'<div id="tyControls-OwnCustomerTree" class = "tyControls-OwnCustomerTree" style="display:none;"></div>';
		$target.append(childs);	
		ownCustomer("#tyControls-OwnCustomerTree");
		equipOrCustInit($target);
		equipOrCustBind($target);
	};
	/**
	 * 设备号/名称，所属客户控件初始化
	 */
	function equipOrCustInit($target){
		
	};
	/**
	 * 设备号/名称，所属客户控件绑定事件
	 */
	function equipOrCustBind($target){
		$('.equipIdOrName').click(function(e){
			$('.equipIdOrName').css('display','none');
			$('.customerOwn').css('display','inline-block');
			$('.searchText').css('display','none');
			$('.tyControls-OwnCustomerTree').css('display','inline-block');
		});
		//所属客户
		$('.customerOwn').click(function(e){
			$('.customerOwn').css('display','none');
			$('.equipIdOrName').css('display','inline-block');
			$('.tyControls-OwnCustomerTree').css('display','none');
			$('.searchText').css('display','inline-block');
		});
	};
	/**
	 * 获得设备/名称和所属客户控件选择结果
	 */
	function equipOrCustGetResult(){
		var searchText='',
			cidArr = [],
			imeiArr = [],
			sonType = true,
			errorMsg = null;
		if($('.searchText').css("display")=="none"){
			var customerResult = customerGetResult();
			sonType = customerResult.sonType;
			if(customerResult.errorMsg != null){
				errorMsg = customerResult.errorMsg;
			}else{
				cidArr = customerResult.cidArr;
				imeiArr = customerResult.imeiArr;
			}
		}else{
			searchText = $(".searchText").val().trim();
			//对输入设备号做限制提示
    		if(searchText.search(/[^\x00-\xff]/g)!=-1){//有汉字
    			if(searchText.length<2){
    				errorMsg = "请输入更多信息(位数)!"
    			}
    		}else{
    			if(searchText.length<6){
    				errorMsg = "请输入更多信息(位数)!"
    			}
    		}
		}
		return {
			searchText:searchText,
			cidArr:cidArr,
			imeiArr:imeiArr,
			sonType:sonType,
			errorMsg:errorMsg
		}
	}
	/**
	 * 客户控件初始化
	 */
	function customerInit(){
	    //客户设备混选树初始化
		cusTerTree = $("#cusTerTree");
		$("input:radio[name=sonType]").eq(1).attr("checked",'checked');
		cusTerTree.tree({
			url:'cusTree4All?targetCid='+cid+'&targetOid='+oid,
	        checkbox : true,
	        onLoadSuccess : function(node, data){
	            if(_hisPathIndex ==_hisPath.length){
	                //最后一次检索
	                if("terminal" == search.type){
	                    var node = cusTerTree.tree('find', "t_" + search.id);
	                    if(!!node){
	                    cusTerTree.tree('scrollTo',node.target);
	                    cusTerTree.tree('select',node.target);
	                    setTimeout('$(".trBoxDw").animate({scrollTop: $("#'+node.domId+'").offset().top - $(".trBoxDw").offset().top + $(".trBoxDw").scrollTop()}, "fast")',100);
	                    }
	                    search = {
	                        id:null,
	                        type:null,
	                        path:null
	                    }
	                }
	            }
	            if(_hisPathIndex < _hisPath.length){
	                var node = cusTerTree.tree('find', "c_"+_hisPath[_hisPathIndex]);
	                if(!!node){
	                var	target = node.target;
	                cusTerTree.tree('select',target);
	                cusTerTree.tree('expand',target);
	                cusTerTree.tree('scrollTo',target);
	                _hisPathIndex ++;
	                setTimeout('$(".trBoxDw").animate({scrollTop: $("#'+node.domId+'").offset().top - $(".trBoxDw").offset().top + $(".trBoxDw").scrollTop()}, "fast")',100);
	                }
	            }
	        }
		});
		cusTerTree.tree('options').cascadeCheck = false;//初始化设置不联结
	};
	/**
	 * 所属客户自动搜索
	 */
	function searchRelative(){
	    var search = $('#serchInput').val(),
	        parentObj = $('#serchResult');
	    if(!search || search.replace(/[^\x00-\xff]/g,"**").length<3){
	        //没输入或者字符数少于3个
	        parentObj.empty();
	        return;
	    }
	    $.ajax({
	        async : false,
	        cache : false,
	        type : 'get',
	        dataType:"json",
	        url : "searchCus4All",
	        data : {
	            search : search,
	            cid : cid
	        },
	        success : function(res){
	            var childrensDom = "";
	            if(res.success) {
	                var data = res.obj;
	                $.each(data, function(i, item) {
	                    childrensDom += '<div class="serchResult" name="'+i+'" onmousedown="tyControls.ownCustomer.chooseResult(\''+item.id+'\',\''+item.type+'\',\''+item.path+'\')">'+item.name+'</div>';
	                });
	                if(!childrensDom) {
	                    childrensDom = '<div class="serchResult">无匹配记录</div>';
	                }
	            }
	            parentObj.html(childrensDom);
	            parentObj.show();
	        }
	    });
	};

	/**
	 * 选择搜索结果
	 */
	function chooseResult(id,type,path){
		$('.serchDown').css('display','none');
	    search = {
	        id:id,
	        type:type,
	        path:path
	    }
	    _hisPathIndex = 0;
	    _hisPath = path.substring(1,path.length-1).split("#");
	    var length = _hisPath.length;
	    for(; _hisPathIndex < length ; _hisPathIndex++){
	        var node = cusTerTree.tree('find', "c_"+_hisPath[_hisPathIndex])
	        if(node){
	            cusTerTree.tree('select',node.target);
	            cusTerTree.tree('expand',node.target);
	        }else{
	            var pnode = cusTerTree.tree('find', "c_"+_hisPath[_hisPathIndex-1])
	            cusTerTree.tree('expand',pnode.target);
	            break;
	        }
	        if(_hisPathIndex + 1 == length){
	            //最后一次客户检索
	            if("customer" == type){
	                cusTerTree.tree('scrollTo',node.target);
	                setTimeout('$(".trBoxDw").animate({scrollTop: $("#'+node.domId+'").offset().top - $(".trBoxDw").offset().top + $(".trBoxDw").scrollTop()}, "fast")',100);
	            }else{
	                var node = cusTerTree.tree('find', "t_"+id);
	                if(!!node){
	                    cusTerTree.tree('scrollTo',node.target);
	                    cusTerTree.tree('select',node.target);
	                    setTimeout('$(".trBoxDw").animate({scrollTop: $("#'+node.domId+'").offset().top - $(".trBoxDw").offset().top + $(".trBoxDw").scrollTop()}, "fast")',100);
	                    search = {
	                        id:null,
	                        type:null,
	                        path:null
	                    }
	                }else{
	                    var pnode = cusTerTree.tree('find', "c_"+_hisPath[_hisPathIndex])
	                    cusTerTree.tree('expand',pnode.target);
	                }
	            }
	        }
	    }
	};
	function customerBind($target){
		$target.click(function(e){e.stopPropagation();});
	    //输入内容重置搜索框
	    $("input.serchInput").bind('keyup',searchRelative);
	    $("input.searchCust").bind('click',function(){
	    	if($("#searchTree").css("display")=="none"){
	    		$("#searchTree").css("display","block");
	    	}else{
	    		$("#searchTree").css("display","none");
	    	}
	    	
	    });
	    $('body').click(function(e){
	    	$('#searchTree').css('display','none');
	    });
	    $("input:radio[name='sonType']").click(function(){
	    	if($('input:radio[name="sonType"]:checked').val()=="1"){
		    	cusTerTree.tree('options').cascadeCheck = true;
		    }else{
		    	cusTerTree.tree('options').cascadeCheck = false;
		    }
	    });
	    
	};
	/**
	 * 获得所选客户对象：客户数组cidArr,设备数组imeiArr,
	 */
	function customerGetResult(){
		var cidArr = [],
			imeiArr = [],
			sonType = true,
			errorMsg=null;
		var sonType = $('input[name="sonType"]:checked ').val()=='1'?true:false;
		var checkedIds = cusTerTree.tree('getChecked');
		if($(''))
		$.each(checkedIds,function(index,node){
			var id = node.id;
			if(/c_/.test(id)){
				cidArr.push(id.substring(2));
			}else{
				imeiArr.push(id.substring(2));
			}
		});
		if(cidArr.length==0&&imeiArr.length==0){
			errorMsg = "请选择客户";
		}
		return {
			cidArr:cidArr,
			imeiArr:imeiArr,
			sonType:sonType,
			errorMsg:errorMsg
		}
	}
    ty = {
    	ownCustomer:{
    		createOwnCustomer:ownCustomer,
    		getResult:customerGetResult,
    		chooseResult:chooseResult,
    	},
    	equipOrCust:{
    		createEquipOrCust:equipOrCust,
        	getResult:equipOrCustGetResult,
    	}
    };
    // 将暴露的公有指针指向私有函数和属性上
    return ty;
}();
