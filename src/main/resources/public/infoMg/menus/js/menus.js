var TYZX = parent.window.TYZX;
TYZX.menus={};
var msg = TYZX.Utils.MsgBox;
TYZX.menus.init=function(){
	$('#menusdg').datagrid({
		pagination:true,
		pageSize:10,
		pageList:[10,20,30,40,50],
		singleSelect:true,
		url:"getAllMenus",
		loadMsg:"数据加载中请稍后。。。",
		columns:[[
		   //{field:'id',title:"主键",align:'left',width:280},
		   //{field:'pid',title:"父级",align:'left',width:280},
		   {field:'order',title:"排序",align:'left',width:280},
		   {field:'name',title:"名称",align:'left',width:280},
		   {field:'url',title:"url",align:'left',width:280},
		]],
		toolbar:[{
					text:'新增',
					iconCls:'icon-add',
					handler:function(){
						$('#tips').text("新增");
						$(".menusMode").show();
						$('#divId').css('display','none');
						var root = $('#pid').tree('find', 0);
						$('#pid').tree('select', root.target);
						$('#name').val("");
						$('#url').val("");
						$('#order').val("");
				}},'-',{
					text:'删除',
					iconCls:'icon-remove',
					handler:function(){
						var row = $('#menusdg').datagrid('getSelected');
						if(row==null){
							msg.dialog("请选择删除行");
							return;
						}
						//添加二次确认
						msg.twoAffirm("请确认是否删除").then(function(data){
							if(data){//确认后再执行
								$.ajax({
									asyn:true,
									cache:false,
									type:'post',
									data:{id:row.id},
									url:"menusDele",
									error:function(){
										msg.dialog("删除失败");
									},
									success:function(data){
										if(data){
											msg.light("删除成功",2);
											$('#menusdg').datagrid('reload');
											TYZX.main.refeshMenus();//刷新左侧菜单栏
										}
									}
								});
							}
						});
				}},'-',{
					text:'修改',
					iconCls:'icon-edit',
					handler:function(){
						$('#tips').text("修改");
						var row = $('#menusdg').datagrid('getSelected');
						if(row==null){
							msg.dialog("请选择修改行");
							return;
						}
						$(".menusMode").show();
						$("#divId").hide();
						$('#id').val(row.id);
						$("#order").val(row.order);
						var node = $('#pid').tree('find', row.pid);
						$('#pid').tree('select', node.target);
						$('#name').val(row.name);
						$('#url').val(row.url);
					}
				}]
	});
	//初始化父级id树
	$("#pid").tree({
		url:"getAllmenusJSONTree",
		onSelect:function(node){
			$("#pidcontent").data({"id":node.id,"order":node.attributes.order}).val(node.text);
		}
	});
};
TYZX.menus.bind=function(){
	$('#menusCancel').click(function(){
		$('.menusMode').hide();
	});
	
	$('#menusSave').click(function(){
		var id = $('#id').val(),
			pid = $('#pidcontent').data("id"),
			pidOrder = $("#pidcontent").data("order");
			name = $('#name').val(),
			url = $('#url').val(),
			order = $("#order").val(),
			isEdit = false;
		if($('#tips').text()=='新增'){//插入
			isEdit = false;
		}else{//修改
			isEdit = true;
		}
		if(pid==""||null){
			msg.dialog("请选择父级");
			$('#pid').focus();
			return;
		}
		if(name==""||null){
			msg.dialog("请填写菜单名");
			$('#name').focus();
			return;
		}
		if(order==""||order==null){
			msg.dialog("请指定排序");
			$("#order").focus();
			return;
		}
		if(order<=pidOrder){
			msg.dialog("子菜单栏order值需大于父菜单栏");
			$("#order").focus();
			return;
		}
		msg.twoAffirm("请确认是否保存").then(function(data){
			if(data){
			$.ajax({
				async:true,
				cache:false,
				type:'post',
				url:'menusSave',
				data:{order:order,id:id,pid:pid,name:name,url:url,isEdit:isEdit},
				error:function(){
					msg.dialog("保存失败");
				},
				success:function(data){
					if(data){
						msg.light("保存成功");
						$('.menusMode').hide();
						$('#menusdg').datagrid('reload');
						TYZX.main.refeshMenus();//刷新左侧菜单栏
					}else{
						msg.dialog('保存失败');
					}
				}
			});
			}
		})
	});
	$("#pidcontent").click(function(e){
		e.stopPropagation();
		$("#pid").toggle();
	});
	$(".box").click(function(){
		$("#pid").hide();
	})
};
(function(){
	TYZX.menus.init();
	TYZX.menus.bind();
}());