# 饮品点单应用 (DrinkOrder)

基于Android Studio开发的饮品点单移动应用，支持咖啡、奶茶、果茶等多种饮品的浏览、搜索、下单等功能。

## 功能特性

### 基础功能
- 启动页（Splash页面）
- 用户注册/登录（密码SHA-256加密存储）
- 首页饮品列表展示（支持滚动浏览）
- 商品详情页面
- 购物车功能（添加/删除/修改数量）
- 订单管理（提交订单、查看历史订单）
- 个人中心（查看个人信息、退出登录）

### 额外功能
- 搜索功能（按饮品名称搜索）
- 分类筛选（咖啡/奶茶/果茶）
- 权限管理（普通用户/管理员）
- 预置管理员账号（用户名：admin，密码：123456）

## 技术栈

- 开发语言：Java
- 最低SDK：API 21 (Android 5.0)
- 目标SDK：API 33
- 数据库：SQLite
- UI框架：Fragment + RecyclerView + Material Design
- 图片处理：使用系统内置资源和drawable

## 项目结构

```
app/
├── src/main/
│   ├── java/com/example/drinkorder/
│   │   ├── Activity类
│   │   │   ├── SplashActivity.java
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   ├── MainActivity.java
│   │   │   ├── DetailActivity.java
│   │   │   ├── CartActivity.java
│   │   │   └── OrderActivity.java
│   │   ├── Fragment类
│   │   │   ├── HomeFragment.java
│   │   │   └── ProfileFragment.java
│   │   ├── Adapter类
│   │   │   ├── ProductAdapter.java
│   │   │   ├── CartAdapter.java
│   │   │   └── OrderAdapter.java
│   │   ├── 实体类
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── CartItem.java
│   │   │   └── OrderItem.java
│   │   └── 数据库
│   │       └── DatabaseHelper.java
│   ├── res/
│   │   ├── layout/          # 布局文件
│   │   ├── values/          # 资源值
│   │   └── xml/             # 配置文件
│   └── AndroidManifest.xml
└── build.gradle
```

## 使用说明

### 1. 导入项目
在Android Studio中打开本项目，等待Gradle同步完成。

### 2. 预置数据
首次启动应用时，会自动创建数据库并预置以下数据：
- 管理员账号：用户名 admin，密码 123456
- 8款饮品：包含咖啡、奶茶、果茶三类

### 3. 运行应用
连接Android设备或启动模拟器，点击Run按钮运行应用。

## 数据库设计

### User表
- id: 自增主键
- username: 用户名（唯一）
- password: 加密后的密码
- role: 角色（user/admin）
- nickname: 昵称

### Product表
- id: 自增主键
- name: 饮品名称
- category: 分类（咖啡/奶茶/果茶）
- price: 价格
- rating: 评分（1-5）
- description: 详细介绍
- imageResId: 图片资源ID

### Cart表
- id: 自增主键
- userId: 关联User.id
- productId: 关联Product.id
- quantity: 数量
- price: 加入时的单价

### Order表
- id: 自增主键
- userId: 关联User.id
- orderTime: 下单时间
- totalPrice: 订单总价
- items: 商品清单（JSON字符串）

## 注意事项

- 请确保在Android SDK Manager中安装了API 33的SDK Platform
- 首次运行可能需要较长时间下载依赖
- 图片资源使用系统内置图标，实际使用时可替换为真实图片

## 开发者

此项目为完整的Android饮品点单应用示例。
