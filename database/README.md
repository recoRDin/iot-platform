# 数据库初始化

## 1. 创建表结构

在项目根目录执行：

```powershell
$env:MYSQL_PWD="你的数据库密码"
mysql -uroot --default-character-set=utf8mb4 < database/001_schema.sql
```

`001_schema.sql` 使用 `CREATE TABLE IF NOT EXISTS`，可以重复执行，不会删除已有数据。

已有数据库升级到产品管理阶段时执行：

```powershell
$env:MYSQL_PWD="你的数据库密码"
mysql -uroot --default-character-set=utf8mb4 < database/002_product.sql
```

`002_product.sql` 只增加 `iot_product` 表，不修改或删除已有数据。

### 产品物模型属性表

已有数据库继续执行：

```powershell
$env:MYSQL_PWD="你的数据库密码"
mysql -uroot --default-character-set=utf8mb4 < database/003_product_property.sql
```

`003_product_property.sql` 只增加 `iot_product_property` 表，用于保存产品可动态配置的物模型属性定义，不保存设备实际上报的遥测值。

## 2. 初始化首个平台管理员

管理员密码不写入仓库。首次启动应用前设置：

```powershell
$env:IOT_DB_PASSWORD="你的数据库密码"
$env:IOT_TOKEN_SECRET="至少32字节的JWT密钥"
$env:IOT_BOOTSTRAP_ADMIN_ENABLED="true"
$env:IOT_BOOTSTRAP_ADMIN_PASSWORD="首次管理员密码"
```

然后启动 `iot-server`。应用会幂等创建：

```text
租户：000000 / 平台管理租户
角色：admin / 平台管理员
用户：admin / 平台管理员
关系：admin用户 -> admin角色
```

如果数据已经存在，不会重置已有管理员密码。

初始化成功后关闭初始化开关并重新启动：

```powershell
$env:IOT_BOOTSTRAP_ADMIN_ENABLED="false"
```

不要把明文密码、数据库密码或 JWT 密钥提交到 Git。
