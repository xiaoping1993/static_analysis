var TYZX = parent.window.TYZX;
TYZX.regionalSta={};
TYZX.regionalSta.map={};
TYZX.regionalSta.parameters={};
TYZX.regionalSta.parameters.geochina = 'https://data.jianshukeji.com/jsonp?filename=geochina/';
TYZX.regionalSta.parameters.categories = {};
TYZX.regionalSta.parameters.cids = [];
TYZX.regionalSta.init = function(){
	//创建客户树
	var ownCustomer = tyControls.ownCustomer;
	ownCustomer.createOwnCustomer($('#customer'));
	TYZX.regionalSta.initMap(TYZX.regionalSta.parameters.geochina);
};
TYZX.regionalSta.bind = function(){
	msg = TYZX.Utils.MsgBox,
	$("#cusTerTree").tree({
		onCheck:function(node,checked){
			var nodes = $("#cusTerTree").tree("getChecked"),
				length = nodes.length,
				cutomers="",
				accounts="";
			TYZX.regionalSta.parameters.cids.length=0;//清空
			for (var i = 0; i < length-1; i++) {
				accounts+=nodes[i].account+",";
				TYZX.regionalSta.parameters.cids.push(nodes[i].id.replace("c_",""))
				cutomers+=nodes[i].text+",";
			}
			TYZX.regionalSta.parameters.cids.push(nodes[length-1].id.replace("c_",""))
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
	var categories = TYZX.regionalSta.parameters.categories;
	$(".appName,.operateSystem").data({"appNamelast":$(".appName select").val(),"operateSystemlast":$(".operateSystem select").val()}).bind('change',function(){
		var appNamelast = $(".appName,.operateSystem").data("appNamelast"),
			operateSystemlast = $(".appName,.operateSystem").data("operateSystemlast");
		$(".appName,.operateSystem").data({"appNamelast":$(".appName select").val(),"operateSystemlast":$(".operateSystem select").val()});
		TYZX.regionalSta.initMap(TYZX.regionalSta.parameters.geochina,appNamelast,operateSystemlast);
	});
	$("#search").click(function(){
		TYZX.regionalSta.initMap(TYZX.regionalSta.parameters.geochina);
	});
};
/**
 * 初始化条件参数
 */
TYZX.regionalSta.initParams = function(){
	var appNames = JSON.parse(localStorage.getItem("appNames"));
	for (var i = 0; i < appNames.length; i++) {
		$('.appName select').append('<option value="'+appNames[i].Code+'">'+appNames[i].Name+'</options>');
	}
	var operateSystems = JSON.parse(localStorage.getItem("operateSystems"));
	for (var i = 0; i < operateSystems.length; i++) {
		$('.operateSystem select').append('<option value="'+operateSystems[i].Code+'">'+operateSystems[i].Name+'</options>');
	}
};
/**
 * 初始化左侧柱状图
 */
TYZX.regionalSta.initHistogram = function(categories){
	var categories1 = [];
		data = [];
		length = categories.length;
	for (var i = 0; i < ((length-10>=0)?10:length); i++) {
		categories1.push(categories[i].name);
		data.push(categories[i].value);
	}
	var chart = new Highcharts.Chart('regionalStaHist',{
		chart:{
			type:'bar'
		},
		credits: {
	        enabled: false
	    },
		title:{
			text:"app使用量排名前10的地区"
		},
		xAxis:{
			categories:categories1
		},
		yAxis:{
			min:0,
			title:{
				text:'使用总数',
				aligh:'high'
			}
		},
		tooltip:{
			pointFormatter: function() {
			    return ''+"软件使用总数"+': <b>'+this.y+'</b><br/>'
			}
		},
		series:[{
			data:data
		}],
		legend:{enabled:false}
    });
	
};
/*
 * 初始化地域分布
 */
TYZX.regionalSta.initMap = function(geochina,appNamelast,operateSystemlast){
	var initHistogram = TYZX.regionalSta.initHistogram,
		serverData,
		msg = TYZX.Utils.MsgBox,
		appName = $(".appName select").val(),
		cids ="'"+TYZX.regionalSta.parameters.cids.join("','")+"'",
		accounts = $(".account select").val(),//需要将其处理为，分隔的字符串
		operateSystem = $(".operateSystem select").val();
	$.ajaxSettings.async = false;
	$.getJSON("getRegionsalAdcodeValueJSON?regionsalAdcode=100000&appName="+appName+"&operateSystem="+operateSystem+"&cids="+cids+"&accounts="+accounts,function(serverData1){
		if(!serverData1["flag"]){
			//$(".appName select").val(appNamelast);
			//$(".operateSystem select").val(operateSystemlast);
			msg.dialog("此条件下数据为空请重新选择");
			return;
		}
		serverData = serverData1;
	});
	$.ajaxSettings.async = true;
	//加载地图插件
	Highcharts.setOptions({
	    lang: {
	        drillUpText: '< 返回 "{series.name}"'
	    }
	});
	$.getJSON(geochina + 'china.json&callback=?', function(mapdata) {
		var data = [];
		// 地图数据
	    Highcharts.each(mapdata.features, function(md, index) {
	    	var adcode = md.properties.adcode;
	        var tmp = {
	            name: md.properties.name,
	            value:adcode==undefined?0:parseInt(serverData[adcode])
	        };
	        if(md.properties.drilldown) {
	            tmp.drilldown = md.properties.drilldown;
	        }
	        data.push(tmp);
	    });
	    var categories = data.sort(function(a,b){return (isNaN(b.value)?0:b.value)-(isNaN(a.value)?0:a.value)});
	    initHistogram(categories);
	    map = new Highcharts.Map('regionalStaMap', {
	        chart: {
	            events: {
	                drilldown: function(e) {
	                    // 异步下钻
	                    if (e.point.drilldown) {
	                        var pointName = e.point.properties.fullname,
	                        	adcode = e.point.properties.adcode,
	                        	xiajiData;
							$.ajaxSettings.async = false;
	                    	$.getJSON("getRegionsalAdcodeValueJSON?regionsalAdcode="+adcode+"&appName="+appName+"&operateSystem="+operateSystem+"&cids="+cids+"&accounts="+accounts,function(xiajiData1){
	                    		xiajiData = xiajiData1;
	                    	});
	                    	if(!xiajiData){
	                    		window.location.href= "login.do?toLogin";
	                    	}
	                    	$.ajaxSettings.async = true;
	                    	if(!xiajiData["flag"]){
	                    		msg.dialog("此条件下数据为空请重新选择");
	                    		return;
	                    	}
	                        map.showLoading('下钻中，请稍后...');
	                        // 获取二级行政地区数据并更新图表
	                        $.getJSON(geochina +   e.point.drilldown + '.json&callback=?', function(data) {
	                            data = Highcharts.geojson(data);
	                            Highcharts.each(data, function(d) {
	                                d.value =(xiajiData[d.properties.adcode]==undefined)?0:parseInt(xiajiData[d.properties.adcode]) ;
	                            });
	                            var categories1=data.sort(function(a,b){return b.value-a.value});
	                            initHistogram(categories1);
	                            map.hideLoading();
	                            map.addSeriesAsDrilldown(e.point, {
	                                name: e.point.name,
	                                data: data,
	                                dataLabels: {
	                                    enabled: true,
	                                    format: '{point.name}:{point.value}'
	                                }
	                            });
	                            map.setTitle({
	                                text: pointName
	                            });
	                        });
	                    }
	                },
	                drillup: function() {
	                    map.setTitle({
	                        text: '中国'
	                    });
	                    initHistogram(categories);
	                }
	            }
	        },
	        title: {
	            text: '中国地图'
	        },
	        subtitle: {
	            text: '手机使用情况地域分布图'
	        },
	        mapNavigation: {//缩放按钮
	            enabled: true,
	            enableButtons:false,
	            enableTouchZoom: false // 在开启导航器的情况下关闭移动端手势操作
	        },
	        tooltip: {
	            useHTML: true,
	            headerFormat: '<table><tr><td>{point.name}</td></tr>',
	            pointFormat: '<tr><td>{point.properties.fullname}</td></tr>' +
	            '<tr><td>软件使用总数：</td><td>{point.value}</td></tr>',
	            footerFormat: '</table>'
	        },
	        colorAxis: {
	            min: 0,
	            minColor: '#fff',
	            maxColor: '#006cee',
	            labels:{
	                style:{
	                    "color":"red","fontWeight":"bold"
	                }
	            }
	        },
	        series: [{
	            data: data,
	            mapData: mapdata,
	            joinBy: 'name',
	            name: '中国',
	            states: {
	                hover: {
	                    color: '#a4edba'
	                }
	            },
	            dataLabels:{
		        	enabled:true,
		        	format:'{point.name}:{point.value}'
		        },
	        }],
	        legend:{
	        	enabled:false
	        }
	    });
	});
};
(function(){
	TYZX.regionalSta.init();
	TYZX.regionalSta.bind();
	TYZX.regionalSta.initParams();//初始化条件参数
}());