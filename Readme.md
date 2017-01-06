##release notes##

###version 2.2.0   2016.07.10

* 添加收藏页面，程序中添加的kml文件名和某样点生成的可替代样点将会出现在收藏页面，在这里可以对样点在地图上的marker进行统一管理，如显示，隐藏，删除，保存。目前只支持对一个kml文件进行操作；
* 将退出按钮移至“我”的界面；
* 在地图页面，长按某点，可以导航至此点；（待添加）
* 离线地图；
* 添加自定义图层；（下一个版本添加）
* 相似度计算出的可替代样点结果和FCM计算出的可替代样点结果，都保存在一个sharedpreference文件中，即AlterSamplesList；
* 在ReadKml.java中，解析成功时，给每个坐标点设置（setIconId）marker为default_marker；

###version 2.2.2 2016.07.11

* 修复样点详情页面不能正确导出kml文件的问题；
* 修复计算可替代样点时，之前已经显示的可替代样点marker不会自动清除的问题；
* 可替代样点的可达性数值保留6位小数显示；

###version 2.2.4 2016.07.26

* 添加文件上传功能（服务器端暂时有bug）

###version 2.3.1 2016.07.31

* 全新的Material Design设计风格；
* 优化了可替代样点计算逻辑；
* 修复点击添加样点数据后弹出的文件浏览页面标题栏返回键不可用的问题；

### version 2.3.2 2016.08.04
* 更新帮助文档

### version 2.3.3 2016.09.03
* 修复点击可替代样点时样点编号的显示问题；

### version 2.3.4 2016.09.29
* 可替代样点与不可采点的距离默认为500米

### version 2.4.0 2016.11.03
* 增加绘制多边形功能；
* 绘制多边形之后，将不再计算显示落入多边形区域内的可替代样点；

### version 2.4.1 2016.11.11
* 定位间隔由5秒改为1秒，即每一秒程序进行一次定位请求

### version 2.4.2 2017.01.06
* 向服务器发送的参数增加用户定位位置；
* 增加百度坐标转wgs84坐标功能；