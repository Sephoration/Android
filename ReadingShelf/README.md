# 阅读书架

记录想读、在读和读完的书。支持书名、作者、页数和笔记，按状态筛选或搜索书名、作者和笔记；点击书目可编辑或删除。

所有内容保存在应用私有 SQLite 数据库，不需要账号或网络。卸载应用会删除数据，当前版本没有备份或云同步功能。

## 构建

使用 Android Studio 打开本目录，安装 Android SDK 34 和 JDK 17，等待 Gradle 同步后运行 `app`。也可以在本目录执行：

```bash
./gradlew assembleDebug
```

首次构建需要下载 Gradle、Android Gradle 插件和 SDK。最低支持 Android 6.0（API 23）。
