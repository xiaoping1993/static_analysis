var TYZX = parent.window.TYZX;
TYZX.activitydedgreeSta = {};
TYZX.activitydedgreeSta.parameters={};
TYZX.activitydedgreeSta.parameters.cids=[];
/**
 * 获得图表所需数据
 * 返回:
 * 	addPersons：新增人数数列;
 * 	activitys:活跃人数数列；
 * 	totalPersonsCount：总人数
 * 	todayAddPersonsCount：今日新增人数
 * 	todayActivityPersonsCount：今日活跃人数
 * 	pointStart:图表开始时间
 */
TYZX.activitydedgreeSta.getAreaData = function(){
	var promise = new Promise(function(resolve,reject){
		var msg = TYZX.Utils.MsgBox,
			appname = $('.appName select').val(),
			operateSystem = $('.operateSystem select').val(),
			appVersion = $('.appVersion select').val(),
			timing = $('.timeing select').val(),
			now = new Date(),
			pointStart = now.setDate(now.getDate()-timing+1)
			cids ="'"+TYZX.regionalSta.parameters.cids.join("','")+"'",
			accounts = $(".account select").val();//需要将其处理为，分隔的字符串
	$.ajax({
		   type: "POST",
		   async:true,
		   url: "getAreaData",
		   data: {appname:appname,operateSystem:operateSystem,appVersion:appVersion,timing:timing,cids:cids,accounts:accounts},
		   error:function(){
			},
		   success: function(data){
			   if(data.code==0){
				   var serverdata = data.data,
				   	   addPersons = serverdata.addPersons,//新增人数数列
				       personsList = serverdata.personsList,//每日总人数数列
				       activitys = serverdata.activitys,//活跃人数数列
				       totalPersonsCount = serverdata.totalPersons;//今日总人数
				   var todayAddPersonsCount = addPersons.pop(),//今日新增人数
				   	   todayActivityPersonsCount = activitys.pop();//今日活跃人数
					   addPersons.push(todayAddPersonsCount);//将输出的最后一个元素加回去
					   activitys.push(todayActivityPersonsCount);//将输出的最后一个元素加回去
					var serverData = {
						addPersons:addPersons,
						personsList:personsList,
						activitys:activitys,
						todayAddPersonsCount:todayAddPersonsCount,
						todayActivityPersonsCount:todayActivityPersonsCount,
						totalPersonsCount :totalPersonsCount,
						pointStart:pointStart
					}
					resolve(serverData);
			   }else{
				   msg.dialog("数据加载出错");
			   }
		   }
		});
	});
	return promise;
};
/**
 * 初始化图表
 */
TYZX.activitydedgreeSta.initArea = function(data){
	$('#container').highcharts({
		colors: ['#91c7ae', '#61a0a8', '#000', '#000', '#8085e9'] ,
	    chart: {
	        type: 'area'
	    },
	    credits: {
	        enabled: false
	    },
	    title: {
	        text: ''
	    },
	    xAxis: {
	        type:'datetime',
	        dateTimeLabelFormats:{
	        	day:'%Y-%m-%d',
	        	week: '%Y-%m-%d',
	        }
	    },
	    yAxis: {
	        title: {
	            text: '数量'
	        },
	        labels: {
	            formatter: function () {
	                return this.value;
	            }
	        }
	    },
	    tooltip: {
	    	xDateFormat: '%Y-%m-%d',
	        pointFormat: '{series.name}:<b>{point.y}</b>'
	    },
	    plotOptions: {
	        area: {
	        	pointStart:data.pointStart, // 开始值
	        	pointInterval:(24 * 3600 * 1000), // 间隔一天
	            marker: {
	                enabled: false,
	                symbol: 'circle',
	                radius: 2,
	                states: {
	                    hover: {
	                        enabled: true
	                    }
	                }
	            }
	        }
	    },
	    series: [{
	        name: '累计用户',
	        data:data.personsList
	    }, {
	        name: '活跃用户',
	        data:data.activitys
	    }]
	});
}

TYZX.activitydedgreeSta.init = function() {
	TYZX.activitydedgreeSta.getAreaData()
	.then(function(serverData){//获得数据
		//获得图表数据
		TYZX.activitydedgreeSta.initArea(serverData);//初始化地图
		//初始化统计数据
		$('#onePerson').text(serverData.totalPersonsCount);
		$('#twoPerson').text(serverData.todayAddPersonsCount);
		$('#totalActivity').text(serverData.todayActivityPersonsCount);
	});
};
/**
 * 初始化条件参数
 */
TYZX.activitydedgreeSta.initParams = function(){
	var appNames = JSON.parse(localStorage.getItem("appNames"));
	for (var i = 0; i < appNames.length; i++) {
		$('.appName select').append('<option value="'+appNames[i].Code+'">'+appNames[i].Name+'</options>');
	}
	var operateSystems = JSON.parse(localStorage.getItem("operateSystems"));
	for (var i = 0; i < operateSystems.length; i++) {
		$('.operateSystem select').append('<option value="'+operateSystems[i].Code+'">'+operateSystems[i].Name+'</options>');
	}
	var appVersions = JSON.parse(localStorage.getItem("appVersions"));
	for (var i = 0; i < appVersions.length; i++) {
		$('.appVersion select').append('<option value="'+appVersions[i].Name+'">'+appVersions[i].Name+'</options>');
	}
};
TYZX.activitydedgreeSta.bind = function() {
	$("#cusTerTree").tree({
		onCheck:function(node,checked){
			var nodes = $("#cusTerTree").tree("getChecked"),
				length = nodes.length,
				cutomers="",
				accounts="";
			TYZX.activitydedgreeSta.parameters.cids.length=0;//清空
			for (var i = 0; i < length-1; i++) {
				accounts+=nodes[i].account+",";
				TYZX.activitydedgreeSta.parameters.cids.push(nodes[i].id.replace("c_",""))
				cutomers+=nodes[i].text+",";
			}
			TYZX.activitydedgreeSta.parameters.cids.push(nodes[length-1].id.replace("c_",""))
			accounts+=nodes[length-1].account;
			cutomers+=nodes[length-1].text;
			$("#searchCust").val(cutomers);
			$("#searchCust").attr("title",cutomers);
			//账户树初始化
			accounts = accounts.split(",");
			for (var i = 0; i < accounts.length; i++) {
				$('.account select').append('<option value="'+accounts[i]+'">'+accounts[i]+'</options>');
			}
		}
	});
	$('.appName,.operateSystem,.appVersion,.timeing').bind('change',function(){
		TYZX.activitydedgreeSta.init();
	})
	$("#search").click(function(){
		TYZX.activitydedgreeSta.init();
	});
};
(function(){
	TYZX.activitydedgreeSta.init();
	//创建客户树
	var ownCustomer = tyControls.ownCustomer;
	ownCustomer.createOwnCustomer($('#customer'));
	TYZX.activitydedgreeSta.bind();
	TYZX.activitydedgreeSta.initParams();//初始化参数
})();