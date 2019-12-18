# 一：项目介绍
## 1.1项目背景
	一家公司可能有很多移动端应用（app,ios，微信小程序等）这个应用在使用过程中可能会产生很多数据，
	如果能分析这些应用产生的数据会变得非常有用，本套产品就是基于这个目的，通过收集移动端应用的数据来做的数据分析，
	希望通过本套资源，同样分析出你的移动端应用产生数据，我这里尽量方便完整的介绍嵌入过程，只为帮你分析你的移动端应用数据
	测试地址：https://47.103.133.15:8443/statis_analysis/main.do?toMain
## 1.2 功能介绍
[图介绍](https://github.com/xiaoping1993/static_analysis/tree/master/resource/功能介绍.md)
# 二：技术架构
	springboot+mybatis+postgresql
# 三：代码详解
	aop:切片操作（如莫些方法别调用，统一做处理）
	config\DataSourceGpsDbConfig.java：多数据源配置之一
	config\DataSourceGpsDbConfig.java：多数据源配置之一
	controller\ActivitydegreeStaController.java：活跃度统计
	controller\ExceptionInfoMgController.java：异常信息管理
	controller\FunctionUsedInfoStaController.java：功能使用统计
	controller\InfoMgController.java： 菜单栏管理
	controller\MainController.java：主页面
	controller\ModelStaController.java：机型统计
	controller\RegionalStaController.java： 地域统计
	controller\UserMgController.java.java：用户管理
	controller\VersionStaController.java： 版本统计
	...
	fiter\SeesionFilter.java：做些过滤操作
	job\DoPushInfo.java：计划任务相关
	mapper\gpsdb|tyzx:本来是两个数据库mapper位置，现在都放在一起了
	service：服务层代码
	util：工具类
	Application.java：应用启动类
	resources\mappers：mybats中的mapper层
	resource\public|static：前端脚本
	application-dev.properties：指定项目的一些配置如数据库参数等
	待完善
# 四：部署手册
	1）安装postgresql参考：https://www.runoob.com/postgresql/windows-install-postgresql.html
	2）找到数据库文件：resource\masdb.sql->导入postgresql->在application-dev.properties中配置好数据库参数
	注意：里面redis相关我已经去掉了，ftp相关也暂时弃用
	3)git clone本项目->cd 根目录->mvn clean package->得到war包
	注意：你电脑要具备java、maven环境
	4）war包放到tomcat容器中
	注意：war包若放在服务器上部署，需配置服务器tomcat容器启动为https参考：https://www.cnblogs.com/liaojie970/p/6693841.html其中如果报错其中protocol="org.apache.coyote.http11.Http11Protocol"要变为protocol="HTTP/1.1"
	5）打开浏览器访问：
	https://127.0.0.1:8443/statis_analysis(如果配置了https)
	https://127.0.0.1:8080/statis_analysis(如果没有配置https)
	https://47.103.133.15:8443/statis_analysis（我提供测试环境）