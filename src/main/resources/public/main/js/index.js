/*以命名空间方式定义全局变量*/
var TYZX = {};//根对象
TYZX.main = {};//main对象
TYZX.main.authoritys={}//权限
TYZX.main.authoritys.menus={}//权限之菜单
TYZX.Utils = {};//工具对象
/*
 * 工具类方法：以命名空间方式创造全局变量
 */
TYZX.Utils.namespace = function (ns_string) {
        var parts = ns_string.split('.'),
            parent = MYAPP,
            i;
        // strip redundant leading global
        if (parts[0] === "MYAPP") {
            parts = parts.slice(1);
        }
        for (i = 0; i < parts.length; i += 1) {
            // create a property if it doesn't exist
            if (typeof parent[parts[i]] === "undefined") {
                parent[parts[i]] = {};
            }
            parent = parent[parts[i]];
        }
        return parent;
    };
/*
 * 工具类方法：提示框工具类
 * 
 */
TYZX.Utils.MsgBox = (function () {
	return {
		dialog : function (msg,title) {
			if(!title){
				title = "提示";
			}
			$("#cancel").hide();
			$('#msg-box #title').empty().html(title);
			$('#msg-box #msg').empty().html(msg);
			$('#msg-box').modal({backdrop: 'static'});
		},
		light : function(text,type){
			if (type==1){
				$("body").append("<div class='lightTip'><span class='lightTipSuc'></span><a>"+text+"</a></div>");
			}else if(type==2){
				$("body").append("<div class='lightTip'><span class='lightTipFail'></span><a>"+text+"</a></div>")	
			}else{
				$("body").append("<div class='lightTip'><span class='lightTipSuc'></span><a>"+text+"</a></div>");
			}
			$('.lightTip').css('margin-left',-$('.lightTip').width()/2).delay(800).fadeOut(200);
			setTimeout(function(){
				$('.lightTip').remove();
			},1000);
		},
		/**
		 * 二次确认弹框调用它显示弹框当点击确认/取消后才会放回true/false结果
		 * 调用方式：msg.twoAffirm.then(function(data){
		 * 			//data代表放回true/false结果
		 * 		})
		 */
		twoAffirm : function(msg,title){
			if(!title){
				title = "提示";
			}
			$("#cancel").show();
			$('#msg-box #title').empty().html(title);
			$('#msg-box #msg').empty().html(msg);
			$('#msg-box').modal({backdrop: 'static'});
			var promise = new Promise(function(resolve,reject){
				$("#affirm").click(function(){
					//点击确定时删除背景
					$(".modal-backdrop").remove();
					resolve(true);//再次确认后确认
				});
				$("#cancel").click(function(){
					resolve(false);//再次确认后取消
				});
			})
			return promise;
		}
	}
})();
/**
 * Number类型自定义方法
 */
TYZX.Utils.NumberUtils = (function(){
	return {
		fixLength : function(num,n){
			var len = num.toString().length;  
		    while(len < n) {  
		        num = "0" + num;  
		        len++;  
		    }  
		    return num;
		}
	}
})();
/**
 * 日期工具
 */
TYZX.Utils.DateUtils = (function(){
	return {
		/**
		 * 格式化Date为指定format
		 * @param date
		 * @param format
		 * @returns
		 */
		formatDate : function(date,format){
			var year = date.getFullYear(),
				month = date.getMonth()+1,
				day = date.getDate();
			if(format=="yyyy-mm-dd"){
				return year+"-"+TYZX.Utils.NumberUtils.fixLength(month,2)+"-"+TYZX.Utils.NumberUtils.fixLength(day,2);
			}
		},
		/**
		 * 比较两个日期类型字符串大小
		 * @param DateOne
		 * @param DateTwo
		 * @returns
		 */
		compareDateForYyyymmdd : function(DateOne,DateTwo){
			  var OneMonth = DateOne.substring(5, DateOne.lastIndexOf("-"));
			  	  OneDay = DateOne.substring(DateOne.length, DateOne.lastIndexOf("-") + 1);
			  	  OneYear = DateOne.substring(0, DateOne.indexOf("-"));
			  	  TwoMonth = DateTwo.substring(5, DateTwo.lastIndexOf("-"));
			  	  TwoDay = DateTwo.substring(DateTwo.length, DateTwo.lastIndexOf("-") + 1);
			  	  TwoYear = DateTwo.substring(0, DateTwo.indexOf("-"));
			  if (Date.parse(OneMonth + "/" + OneDay + "/" + OneYear) > Date.parse(TwoMonth + "/" + TwoDay + "/" + TwoYear)) {
			    return true;
			  } else {
			    return false;
			  }
		},
		/**
		 * 毫秒转为天时分秒
		 * @param mss
		 * @returns
		 */
		formatMssToDdhhmmss : function(mss){
			 var result = "";
			 var days = parseInt(mss / (1000 * 60 * 60 * 24));
		     var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		     var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
		     var seconds = Math.round((mss % (1000 * 60)) / 1000);
		     var date = (days==0?"":days + " 天 ") + (hours==0?"":hours+ " 小时 ") + (minutes==0?"":minutes + " 分钟 ") + seconds + " 秒 ";
		     var formatedate = date.split(" ");
		     if(formatedate.length>=4){
		    	 result =  " "+formatedate[0]+" "+formatedate[1]+" "+formatedate[2]+" "+formatedate[3];
		     }else{
		    	 result = formatedate.join(" ");
		     }
		     return result;
		     //return (days==0?"":days + " 天 ") + (hours==0?"":hours+ " 小时 ") + (minutes==0?"":minutes + " 分钟 ") + seconds + " 秒 ";
		},
		formatMssToDdHhmmss2 : function(mss){
			 var result = "";
			 var days = parseInt(mss / (1000 * 60 * 60 * 24));
		     var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		     var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
		     var seconds =Math.round((mss % (1000 * 60)) / 1000);
		     var date = (days==0?"":days + " 天 ") + (hours==0?"":hours+ " 小时 ") + (minutes==0?"":minutes + " 分钟 ") + seconds + " 秒 ";
		     var formatedate = date.split(" ");
		     if(formatedate.length>=4){
		    	 result =  " "+formatedate[0]+" "+formatedate[1]+" "+formatedate[2]+" "+formatedate[3];
		     }else{
		    	 result = formatedate.join(" ");
		     }
		     return result;
		}
	}
})();
TYZX.Utils.dataParse = function(data){
	var result = "";
	if(data==undefined||data==""){
		result=null;
		return result;
	}
	var data1=data.split("/");
	data1[1].length==1?data1[1]="0"+data1[1]:data1[1]=data1[1];
	var result = data1[2]+"-"+data1[0]+"-"+data1[1];
	return result;
};
TYZX.Utils.compareDataParse=function(data1,data2){
	var result = false;
	data11 = data1.split("-");
	data21 = data2.split("-");
	if(data11[0]>data21[0]){
		result = true;
	}else if(data11[0]==data21[0]){
		if(data11[1]>data21[1]){
			result = true;
		}else if(data11[1]==data21[1]){
			if(data11[2]>data21[2]){
				result = true;
			}
		}
	}
	return result;
};
TYZX.main.changePassword = function(){
	var dialog = TYZX.Utils.MsgBox.dialog,
		oldPw = $('#old-password').val(),
		newPw = $('#new-password').val(),
		newPwR = $('#new-password-repeat').val(),
		pattern=/^(([a-z]+[0-9]+)|([0-9]+[a-z]+))[a-z0-9]*$/i;
	if(!oldPw){
		dialog("请输入原密码");
		return false;
	}
	
	if(!newPw){
		dialog("请输入新密码");
		return false;
	}
	if(newPw!=newPwR){
		dialog("两次密码输入不一致");
		return false;
	}
	
	if(!pattern.test($("#new-password").val())){
		dialog("密码必须为数字和字母组合");
		return false;
	}
	
	if(newPw.length<8||newPw.length>16){
		MsgBox.dialog("密码至少8个字符，最多16个字符");
		return false;
	}
	$.ajax({
		type : 'POST',
		dataType:"json",
		url : "login.do?modifyPwd",
		data: {
			oldPw :oldPw,
			newPw : newPw,
		},
		success : function(j){
			if(j.code==0){
				dialog("修改密码成功");
				$('#change-password-modal').modal('hide');
				return;
			}
			dialog(j.msg);
		}
	});
};
/*
 * iframe页面跳转
 */
TYZX.main.iframeJump = function(url,name){
	if($(".contentleft .collapse")!=undefined){
		$(".contentleft .collapse").collapse("hide");
		$(".contentleft .navbar-brand").text("统计分析-"+name);
	}
	$('.contentright').html('<iframe src="'+url+'" scrolling="no" onload="TYZX.main.setIframeHeight(this)"></iframe>');
};
/**
 * 设置iframe自适应高度
 * @param iframe
 */
TYZX.main.setIframeHeight = function(iframe){
	if (iframe) {
		var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
		if (iframeWin.document.body) {
		iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
		}
	}
}
TYZX.main.init = function(){
	if(screen.width<820){
		TYZX.main.navChangeToMobel();
	}
	TYZX.main.refeshMenus();
	$('.content>.contentleft .nav li:first>a').click();
};
/**
 * 导航栏变为响应式导航栏
 */
TYZX.main.navChangeToMobel = function(){
	var nav$ = 
		"<nav class=\"navbar navbar-default\" role=\"navigation\">"+
		    "<div class=\"container-fluid\">"+
		    "<div class=\"navbar-header\">"+
		        "<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\""+
		                "data-target=\"#example-navbar-collapse\">"+
		            "<span class=\"sr-only\">切换导航</span>"+
		            "<span class=\"icon-bar\"></span>"+
		            "<span class=\"icon-bar\"></span>"+
		            "<span class=\"icon-bar\"></span>"+
		        "</button>"+
		        "<a class=\"navbar-brand\" href=\"#\">统计分析Home</a>"+
		    "</div>"+
		    "<div class=\"collapse navbar-collapse\" id=\"example-navbar-collapse\">"+
		        "<ul class=\"nav navbar-nav\">"+
		        "</ul>"+
		    "</div>"+
		    "</div>"+
		"</nav>";
	$(".contentleft ul").remove();
	$(".contentleft").append(nav$);
};
/**
 * 刷新左侧菜单栏
 */
TYZX.main.refeshMenus = function(){
	$('.contentleft ul').empty();
	var loginName,
	menus_ids;
	$.ajax({
		async:false,
		cache:false,
		type:'post',
		url:'main.do?searchUserInfo',
		error:function(){
		},
		success:function(data){
			if(data.code==0){
				loginName = data.data.loginName;
				menus_ids = data.data.menus_ids;
				}
		}
	});
	$('.person').text(loginName+"  ");
	//按权限分配显示对应的菜单模块
	$.ajax({
		async:false,
		cache:false,
		type:"post",
		url:"main.do?getMenusInfo",
		data:{menus_ids:menus_ids},
		error:function(){
		},
		success:function(data){
			if(data.code==0){
				TYZX.main.authoritys.menus=data.data;
				for(var i=0;i<data.data.length;i++){
					var name = data.data[i].name,
						url = data.data[i].url,
						id = data.data[i].id,
						pid = data.data[i].pid,
						iconname = data.data[i].iconname;
					if(id==7){
							$('.contentleft ul').append(" <li class='dropdown' id='dataDic'>" +
									"<a style='background:url(\"main/images/"+iconname+"\") no-repeat;background-size:22px 22px; background-position:26px 8px;' class='dropdown-toggle' data-toggle='dropdown'>"+name+"<span class='caret'></span></a>" +
									"<ul class='dropdown-menu'>" +
									"</ul>" +
								"</li>"
							);
					}else{
						if(pid==7){
							var aaa=$(this).text().indexOf(name);
							if(aaa==-1){
								$('#dataDic>ul').append("<li class='dataDic1' style='display:none'><a onclick='TYZX.main.iframeJump(\""+url+"\",\""+name+"\")'>"+name+"</a></li>");
							}
							$('.contentleft ul').delegate('#dataDic','click',function(){
								$('.dataDic1').show();
							});
						}else{
							$('.contentleft ul').append("<li><a style='background:url(\"main/images/"+iconname+"\") no-repeat;background-size:22px 22px; background-position:26px 8px;' onclick='TYZX.main.iframeJump(\""+url+"\",\""+name+"\")'>"+name+"</a></li>");
						}
					}
				}
			}
		}
	});
};
TYZX.main.bind = function() {
	$(".exit").click(function(){
		$.ajax({
			async:false,
			cache:false,
			type:"post",
			url:"main.do?clearSession",
			error:function(){
			},
			success:function(data){
				localStorage.clear();
				console.log("session清除成功");
				window.location.href="login.do?toLogin";
			}
		});
	});
};
(function(){
	TYZX.main.init();
	TYZX.main.bind();
}());