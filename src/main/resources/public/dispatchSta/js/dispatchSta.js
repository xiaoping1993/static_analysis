var TYZX = parent.window.TYZX;
var msg = TYZX.Utils.MsgBox;
TYZX.dispatchSta={};
TYZX.dispatchSta.parameters={};
TYZX.dispatchSta.parameters.cids = [];

TYZX.dispatchSta.init = function(){
	//创建客户树
	var ownCustomer = tyControls.ownCustomer;
	ownCustomer.createOwnCustomer($('#customer'));
	$(".larryDetals").hide();
	//初始化拉车数据表格
	TYZX.dispatchSta.initShowResult();
};

TYZX.dispatchSta.bind = function(){
	$("#cusTerTree").tree({
		onCheck:function(node,checked){
			var nodes = $("#cusTerTree").tree("getChecked"),
				length = nodes.length,
				cutomers="",
				accounts="";
			TYZX.dispatchSta.parameters.cids.length=0;//清空
			for (var i = 0; i < length-1; i++) {
				accounts+=nodes[i].account+",";
				TYZX.regionalSta.parameters.cids.push(nodes[i].id.replace("c_",""))
				cutomers+=nodes[i].text+",";
			}
			TYZX.dispatchSta.parameters.cids.push(nodes[length-1].id.replace("c_",""))
			accounts+=nodes[length-1].account;
			cutomers+=nodes[length-1].text;
			$("#searchCust").val(cutomers);
			$("#searchCust").attr("title",cutomers);
		}
	});
	$("#search").click(function(){
		TYZX.dispatchSta.init();
	});
	$("#close").click(function(){
		$("#larryDetals").hide();
	});
};
/**
 * 展示拉车工具使用情况详情
 */
TYZX.dispatchSta.showLarryDetail = function(weixinData,dispatchData){
	weixinData = weixinData.split(","),
	dispatchData = dispatchData.split(",");
	var pointStart = ($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val();
	//获得拉车详细信息可通过获得表格数据时传过来
	$("#larrySpline").highcharts({
		colors:['#91c7ae', '#61a0a8', '#000', '#000', '#8085e9'],
		chart:{
			type:'spline'
		},
		title:'拉车工具使用情况对比图',
		xAxis:{
			type:'datetime',
			dataTimeLabelFormats:{
				day:'%Y-%m-%d',
				week:'%Y-%m-%d'
			}
		},
		yAxis:{
			title:{
				text:'使用人数'
			},
			labels:{
				formatter:function(){
					return this.value;
				}
			}
		},
		tooltip:{
			xDateFormat:'%Y-%m-%d',
			pointFormat:'{series.name}:<b><point.y>'
		},
		plotOptions:{
			spline:{
				pointStart:pointStart,//开始时间
				pointInterval:(24 * 3600 * 1000),//间隔时间一天
				marker:{
					enabled:false,
					symbol:'circle',
					radius:2,
					states:{
						hover:{
							enable:true
						}
					}
				}
			}
		},
		series:[{
			name:"微信小工具",
			data:[3,4]//weixinData
		},{
			name:"派工app",
			data:[4,7]//dispatchData
		}]
	});
	$("#larryDetals").show();
};

TYZX.dispatchSta.initShowResult = function(){
	//初始化表格
	var cid = "'1'",//获得客户id
		startTime = $("#start").val(),
		endTime = $("#end").val();
	$("#dg").datagrid({
		striped:true,
		singleSelect:true,
		loadMsg:"数据加载中请稍后",
		pagination:true,
		url:'getLarryStaData',
		queryParams:{cid:cid,startTime:startTime,endTime:endTime},
		pageSize:10,
		columns:[[
		     {field:"cname",title:"客户名称",align:'left',width:280},
		     {field:"weixindata",title:"微信小工具拉车次数",align:'left',width:280,
		    	 formatter:function(value,row,index){
		    		 return eval(value.split(",").join("+"));
		    	 }},
		     {field:"dispatchdata",title:"派工拉车次数",align:'left',width:280,
		    		 formatter:function(value,row,index){
		    			 return eval(value.split(",").join("+"));
			    	 }},
		     {field:"larryDetail",title:"拉车次数详细",align:"left",width:280,
		    	 formatter:function(value,row,index){
		    		 return "<a href='#' onclick='TYZX.dispatchSta.showLarryDetail(\""+row.weixindata+"\",\""+row.dispatchdata+"\")'>详细</a>"
		    	 }}
		]]
	});
};
/**
 * 初始化参数
 */
TYZX.dispatchSta.initParam = function(){
	var formatDate = TYZX.Utils.DateUtils.formatDate;
	var today = new Date();
	$("#end").val(formatDate(today,"yyyy-mm-dd"));
	today.setDate(today.getDate()-7);
	$("#start").val(formatDate(today,"yyyy-mm-dd"));
};
(function(){
	TYZX.dispatchSta.initParam();
	//第一次初始化日期
	start = ($("input#start+span>input:nth-child(2)").val()==undefined)?$("#start").val():$("input#start+span>input:nth-child(2)").val(),
	end = ($("input#end+span>input:nth-child(2)").val()==undefined)?$("#end").val():$("input#end+span>input:nth-child(2)").val();
	TYZX.dispatchSta.init();
	TYZX.dispatchSta.bind();
})();