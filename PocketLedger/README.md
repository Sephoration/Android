# 随手记账

一个离线的日常支出记录应用。按月份查看合计与明细，支持选择分类、设置日期、备注，以及修改和删除记录。点击一笔记录即可编辑；在顶部切换月份。

金额以整数分存储，输入支持最多两位小数。数据保存在应用私有 SQLite 数据库，不需要账号或网络；卸载应用会删除数据，当前版本没有备份或导出功能。

## 构建

使用 Android Studio 打开本目录，安装 Android SDK 34 和 JDK 17，等待 Gradle 同步后运行 `app`。也可以在本目录执行：

```bash
./gradlew assembleDebug
```

首次构建需要下载 Gradle、Android Gradle 插件和 SDK。最低支持 Android 6.0（API 23）。
