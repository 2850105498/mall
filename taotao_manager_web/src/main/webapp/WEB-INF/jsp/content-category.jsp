<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div>
	 <ul id="contentCategory" class="easyui-tree">  </ul>
</div>
<div id="contentCategoryMenu" class="easyui-menu" style="width:120px;" data-options="onClick:menuHandler">
    <div data-options="iconCls:'icon-add',name:'add'">添加</div>
    <div data-options="iconCls:'icon-remove',name:'rename'">重命名</div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'icon-remove',name:'delete'">删除</div>
</div>
<script type="text/javascript">
$(function(){
	$("#contentCategory").tree({
		url : '/content/category/list',
		animate: true,
		method : "GET",
		onContextMenu: function(e,node){
            e.preventDefault();
            $(this).tree('select',node.target);
            $('#contentCategoryMenu').menu('show',{
                left: e.pageX,
                top: e.pageY
            });
        },
        onAfterEdit : function(node){
        	var _tree = $(this);
        	if(node.id == 0){
        		// 新增节点
        		$.post("/content/category/create",{parentId:node.parentId,name:node.text},function(data){
        			if(data.status == 200){
        				_tree.tree("update",{
            				target : node.target,
            				id : data.data.id
            			});
        			}else{
        				$.messager.alert('提示','创建'+node.text+' 分类失败!');
        			}
        		});
        	}else{
        		$.post("/content/category/update",{id:node.id,name:node.text});
        	}
        }
	});
});
function menuHandler(item) {
    var tree = $("#contentCategory");
    var node = tree.tree("getSelected");
    if (item.name === "add") {
        tree.tree('append', {
            parent: (node ? node.target : null),
            data: [{
                text: '新建分类',
                id: 0,
                parentId: node.id
            }]
        });
        var _node = tree.tree('find', 0);//根节点
        tree.tree("select", _node.target).tree('beginEdit', _node.target);
    } else if (item.name === "rename") {
        tree.tree('beginEdit', node.target);
    } else if (item.name === "delete") {
        $.messager.confirm('确认', '确定删除名为 ' + node.text + ' 的分类吗？', function (r) {
            //1.如果判断是父节点不允许删除
            if (r) {
                //获取被删除节点的子节点
                var Children = tree.tree("getChildren", node.target);
                //console.log(typeof(Children)); object
                //console.log(Children.length);
                //如果没有则删除该节点，如果有则提示删除子节点后才能删除
                if (Children.length == 0) {
                    $.post("/content/category/delete", {id: node.id}, function (data) {
                        if (data.status == 200) {
                            //删除前台的节点
                            tree.tree("remove", node.target);
                            $.messager.alert('提示', node.text + '分类删除成功！');
                        } else if (data.status == 500) {
                            //提示服务端消息
                            $.messager.alert('提示', data.msg);
                        } else {
                            $.messager.alert('提示', '删除' + node.text + '分类失败');
                        }
                    });
                } else {
                    $.messager.alert('提示', '请先删除' + node.text + '分类下的所有子分类，再删除' + node.text);
                }
            }
        });

    }
}
</script>