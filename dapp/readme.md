# dapp

## 使用mysql数据库

gradle添加mysql jdbc驱动，或在driver中添加jdbc驱动。

```text
dataSourceProperties : {
    dataSourceClassName : com.mysql.jdbc.jdbc2.optional.MysqlDataSource
    "dataSource.url" : "jdbc:mysql://localhost:3306/one_n?useSSL=false"
    "dataSource.user" : root
    "dataSource.password" : "123456"
}
```

## setting the max_allowed_packet

调整到500MB 或更高，视具体环境而定。

## 启动corda.jar

java -jar -Xmx2048m corda.jar
-Xmx512m JVM最大允许分配的堆内存，按需分配

## 数据

1. 经销商基础信息
```text
1   客户名称    CUSTOMER_NAME
2   客户编号    CUSTOMER_CODE
3   税号        TAX_NO
4   经销区域    SALES_AREA
5   联系电话    PHONE_NO
6   联系地址    CONTACT_ADDR
7   送货地址    SHIPMENT_ADDR
8   合作年限    CONTRACT_LIMIT
9   营销中心    MARKETING_CENTER
10  大区名称    SALES_REGION
11  工作站名称   WORKSTATION
12  经销商状态

```

2. 订单信息
```text
1   客户名称    ACCOUNT_NAME
2   客户编号    ACCOUNT_NUMBER
3   销货单号     ORDER_NUMBER
4   产品名称    PRODUCT_NAME
5   产品编码    PRODUCT_CODE
6   销售场所    SALES_CHANNEL_CODE
7   产品档次    PRODUCT_GRADE
8   折扣后价格   UNIT_SELLING_PRICE
9   数量          ORDERED_QUANTITY
10  酒款小计    TOTAL_AMOUNT
11  收款金额    ACTUAL_AMOUNT
12  提货方式    FREIGHT_TERMS_CODE
13  提货日期    ACTUAL_SHIPMENT_DATE


```
## 修改party和端口号, 在部署时

## 创建缓存库的sql
```sql

```
## cordaCE oracle 为用户添加序列

```sql
create sequence hibernate_sequence
    minvalue 1
    maxvalue 999999999999999999999999999
    start with 1
    increment by 1
    cache 20;
```

## cordaCE oracle datasource
```sql
dataSourceProperties = {
    dataSourceClassName = oracle.jdbc.pool.OracleDataSource
    "dataSource.url" = "jdbc:oracle:thin:@192.168.50.140:1521:xe"
    "dataSource.user" = "notaryee"
    "dataSource.password" = " notaryee "
}
```


# 初始化数据库

```
alter table STATE_CUSTOMER
  modify (
  OUTPUT_INDEX NUMBER(10),
  TRANSACTION_ID NVARCHAR2(64),
  CUSTOMER_NAME NVARCHAR2(100),
  CUSTOMER_CODE NVARCHAR2(50),
  TAX_NO NVARCHAR2(50),
  SALES_AREA NVARCHAR2(100),
  PHONE_NO NVARCHAR2(50),
  CONTACT_ADDR NVARCHAR2(255),
  SHIPMENT_ADDR NVARCHAR2(100),
  CONTRACT_LIMIT NVARCHAR2(50),
  MARKETING_CENTER NVARCHAR2(100),
  SALES_REGION NVARCHAR2(100),
  WORKSTATION NVARCHAR2(100),
  IS_ACTIVED NVARCHAR2(1),
  CREATE_TIME DATE,
  FROM_PARTY NVARCHAR2(255),
  SEAL_ID NVARCHAR2(128),
  KEY_FIELD NVARCHAR2(255),
  HASH_CODE NVARCHAR2(64)
  )
```


```
alter table STATE_ORDER
  modify (
  OUTPUT_INDEX NUMBER(10),
  TRANSACTION_ID NVARCHAR2(64),
  ACCOUNT_NUMBER NVARCHAR2(100),
  ACCOUNT_NAME NVARCHAR2(50),
  ORDER_NUMBER NVARCHAR2(32),
  PRODUCT_NAME NVARCHAR2(100),
  PRODUCT_CODE NVARCHAR2(20),
  SALES_CHANNEL_CODE NVARCHAR2(30),
  PRODUCT_GRADE NVARCHAR2(30),
  UNIT_SELLING_PRICE NUMBER(13, 5),
  ORDERED_QUANTITY NUMBER(18, 6),
  TOTAL_AMOUNT NUMBER(18, 2),
  ACTUAL_AMOUNT NUMBER(18, 2),
  FREIGHT_TERMS_CODE NVARCHAR2(10),
  ACTUAL_SHIPMENT_DATE DATE,
  CREATE_TIME DATE,
  FROM_PARTY NVARCHAR2(255),
  SEAL_ID NVARCHAR2(128),
  KEY_FIELD NVARCHAR2(255),
  HASH_CODE NVARCHAR2(64)
  )
```

```
dataSourceProperties = {
    dataSourceClassName = oracle.jdbc.pool.OracleDataSource
    "dataSource.url" = "jdbc:oracle:thin:@192.168.50.140:1521:xe"
    "dataSource.user" = "one_n"
    "dataSource.password" = "one_n"
}
dataSourceProperties = {
    dataSourceClassName = oracle.jdbc.pool.OracleDataSource
    "dataSource.url" = "jdbc:oracle:thin:@192.168.50.140:1521:xe"
    "dataSource.user" = "one_a"
    "dataSource.password" = "one_a"
}
dataSourceProperties = {
    dataSourceClassName = oracle.jdbc.pool.OracleDataSource
    "dataSource.url" = "jdbc:oracle:thin:@192.168.50.140:1521:xe"
    "dataSource.user" = "one_b"
    "dataSource.password" = "one_b"
}
```

## 创建数据库

```
CREATE USER [USER] IDENTIFIED BY [PASSWORD];
GRANT UNLIMITED TABLESPACE TO [USER];
GRANT CREATE SESSION TO [USER];
GRANT CREATE TABLE TO [USER];
GRANT CREATE SEQUENCE TO [USER];
GRANT ALL PRIVILEGES TO [USER] IDENTIFIED BY [PASSWORD];
```