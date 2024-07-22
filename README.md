<div align="center">
  <h1 align="center">Corss Chain API Server</h1>
  <p align="center">
    <a href="http://makeapullrequest.com">
      <img alt="pull requests welcome badge" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat">
    </a>
    <a href="https://www.java.com">
      <img alt="Language" src="https://img.shields.io/badge/Language-Java-blue.svg?style=flat">
    </a>
    <a href="https://www.apache.org/licenses/LICENSE-2.0">
      <img alt="License" src="https://img.shields.io/github/license/AntChainOpenLab/AntChainBridgePluginServer?style=flat">
    </a>
  </p>
</div>


# 介绍
跨链API服务层旨面向合作开发者，辅助其将区块链更方便快捷的接入到跨链系统。API服务通过代理BCDNS（区块链域名系统）服务，提供申请区块链域名的功能；同时，提供向Relay服务添加指定区块链的功能，需要携带区块链绑定的插件服务信息及区块链配置信息。

# 架构

<img src="C:\Users\liuyc\AppData\Roaming\Typora\typora-user-images\image-20240603104659012.png" alt="image-20240603104659012" style="zoom:50%;" />

- Relay服务：跨链桥中继（AntChain Bridge Relayer）是蚂蚁链跨链开源项目的重要组件，负责连接区块链、区块链域名服务（BCDNS）和证明转化组件（PTC），完成可信信息的流转与证明，实现区块链互操作。
- BCDNS服务：区块链域名系统（BlockChain Domain Name System, BCDNS），是按照[IEEE 3205](https://antchainbridge.oss-cn-shanghai.aliyuncs.com/antchainbridge/document/ieee/p3205/IEEE_3205-2023_Final.pdf)跨链标准中的身份协议实现的证书颁发服务，负责给跨链系统中的证明转化组件（PTC）、中继（Relayer）、和区块链域名赋予唯一性标识和可信证书，以实现跨链互操作过程中的身份认证。

# 快速开始

## 部署API

### 环境

API使用了MySQL和Redis，这里建议使用docker快速安装依赖。

首先通过脚本安装docker，或者在[官网](https://docs.docker.com/get-docker/)下载。

```bash
wget -qO- https://get.docker.com/ | bash
```

然后下载MySQL镜像并启动容器：

```bash
docker run -itd --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD='YOUR_PWD' mysql --default-authentication-plugin=mysql_native_password
```

然后下载Redis镜像并启动容器：

```bash
docker run -itd --name redis-test -p 6379:6379 redis --requirepass 'YOUR_PWD' --maxmemory 500MB
```

### 构建

**在开始之前，请您确保安装了maven和JDK，这里推荐使用[openjdk-1.8](https://adoptium.net/zh-CN/temurin/releases/?version=8)版本*

进入代码的根目录，运行mvn编译即可：

```bash
mvn clean package -Dmaven.test.skip=true
```

在`cross-chain-api-server/target`下面会产生压缩包`cross-chain-api-server.zip`，将该压缩包解压到运行环境即可。

### 配置

在获得安装包之后，执行解压缩操作：

```bash
unzip cross-chain-api-server.zip
```

进入解压后的目录，可以看到：

```
cd cross-chain-api-server/
tree .
.
├── bin
│   ├── launch
│   ├── launch.bat
│   ├── wrapper-linux-x86-64
│   └── wrapper-windows-x86-64.exe
├── conf
│   ├── application-dev.properties
│   ├── application.properties
│   ├── application-pro.properties
│   ├── application-test.properties
│   ├── db.sql
│   └── wrapper.conf
├── lib
│   ├── animal-sniffer-annotations-1.21.jar
│   ├── ......
```

## 修改配置

配置文件在`conf`目录下，不同的配置文件由项目的根目录配置文件(pom.xml)中的profiles配置决定，开发环境为dev、测试环境为test和生产环境pro。

配置文件示例：

```properties
server.port=8113 //服务端口号
logging.level.root=info

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bcdns?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2b8&useSSL=false //mysql配置
spring.datasource.username=xxx //mysql用户名
spring.datasource.druid.filter.config.enabled=true
public-key=xxx //用于解密mysql密码的解密公钥
spring.datasource.druid.connection-properties=config.decrypt=true;config.decrypt.key=${public-key}
spring.datasource.password=xxx //加密后的mysql密码
spring.datasource.druid.initial-size=1
spring.datasource.druid.min-idle=1
spring.datasource.druid.max-active=20
spring.datasource.druid.max-wait=60000
spring.datasource.druid.validation-query=select 1
spring.datasource.druid.time-between-log-stats-millis=1800000
spring.mvc.servlet.load-on-startup=1

redis.host=127.0.0.1 
redis.port=6379
redis.password=xxx //加密后的redis密码
redis.publicKey=xxx //用于解密redis密码的解密公钥

object-identity.manager.address=did:bid:ef23GG....Y4K //API管理员地址
relay.admin.address=127.0.0.1:8088 //中继grpc访问地址
bif.ipList=127.0.0.1:8080 //星火主链对外出口IP地址
```

## 运行

首先运行数据库脚本来创建表单，数据库脚本为`src/main/resources/db.sql`。
[]()
数据库表单创建成功后，在项目根目录之下，运行命令即可：

```bash
./bin/launch start
```

日志文件存储在`logs`目录之下。可以通过`./bin/launch stop`关闭服务。

## 示例

服务启动之后即可调用http接口完成区块链域名申请和区块链注册。接口调用流程如下图所示，接口调用详情请参考**跨链服务层API说明文档**。

![](.\src\docs\image-20240611103106848.png)

# 社区治理

欢迎您参与[开发者社区](https://bif-doc.readthedocs.io/zh-cn/2.0.0/other/开发者社区.html)进行讨论和建设。

# License

详情参考[LICENSE](./LICENSE)。