<html>
<head>
    <title>测试页面</title>
</head>
<body>
学生信息
学号：${student.id}
姓名：${student.name}
年龄：${student.age}
家庭住址：${student.address}


学生列表
<table border="1">
    <tr>
        <th>下标</th>
        <th>身份证号</th>
        <th>姓名</th>
        <th>地址</th>
    </tr>
		<#list stuList as stu>
		<tr>
            <td>${stu.id}</td>
            <td>${stu.name}</td>
            <td>${stu.age}</td>
            <td>${stu.address}</td>
        </tr>
        </#list>
</table>
<br>
null值处理：${val!"默认值"}
<#if val??>
<#else>
</#if>
</body>
</html>
