##release notes##

###version 2.2.0   2016.07.10

* 添加收藏页面，程序中添加的kml文件名和某样点生成的可替代样点将会出现在收藏页面，在这里可以对样点在地图上的marker进行统一管理，如显示，隐藏，删除，保存。目前只支持对一个kml文件进行操作；
* 将退出按钮移至“我”的界面；
* 在地图页面，长按某点，可以导航至此点；（待添加）
* 离线地图；
* 添加自定义图层；（下一个版本添加）
* 相似度计算出的可替代样点结果和FCM计算出的可替代样点结果，都保存在一个sharedpreference文件中，即AlterSamplesList；
* 在ReadKml.java中，解析成功时，给每个坐标点设置（setIconId）marker为default_marker；